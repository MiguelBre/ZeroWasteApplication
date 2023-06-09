package br.senai.jandira.sp.zerowastetest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.jandira.sp.zerowastetest.api.ApiCalls
import br.senai.jandira.sp.zerowastetest.api.LogisticCalls
import br.senai.jandira.sp.zerowastetest.api.RetrofitApi
import br.senai.jandira.sp.zerowastetest.dataSaving.SessionManager
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelRating.Media
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.Favoritar
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.Favoritos
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserData
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.Catador
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.CatadorFavorito
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.MateriaisCatador
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatadoresFavoritosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionMnager = SessionManager(this)
        val cleanToken = sessionMnager.fetchAuthToken()
        val authToken = "Bearer $cleanToken"


        val api = RetrofitApi.getMainApi()
        val mainApi = api.create(ApiCalls::class.java)

        mainApi.getUserData(authToken).enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.isSuccessful){

                    val responseUser = response.body()!!

                    mainApi.getFavoritos(responseUser.gerador!![0].id, authToken)
                        .enqueue(object : Callback<List<Favoritos>> {
                            override fun onResponse(
                                call: Call<List<Favoritos>>,
                                responseFavoritos: Response<List<Favoritos>>
                            ) {
                                if (responseFavoritos.isSuccessful) {
                                    setContent {
                                        ZeroWasteTestTheme {
                                            setContent {
                                                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                                    Image(
                                                        painter = painterResource(R.drawable.fundo),
                                                        contentDescription = null,
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.FillBounds
                                                    )
                                                    Greeting(
                                                        responseFavoritos.body()!!,
                                                        authToken,
                                                        responseUser.gerador!![0].id
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                else if (responseFavoritos.code() == 404){

                                    val noFavoritos = listOf(Favoritos(id = 0, catador = CatadorFavorito(0, 0, 0, UserData()), id_catador = 0, id_gerador = 0)
                                    )

                                    setContent {
                                        ZeroWasteTestTheme {
                                            setContent {
                                                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                                    Image(
                                                        painter = painterResource(R.drawable.fundo),
                                                        contentDescription = null,
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.FillBounds
                                                    )
                                                    Greeting(noFavoritos, authToken, responseUser.gerador!![0].id)
                                                }
                                            }
                                        }
                                    }
                                }
                                else {
                                    Log.e("Err_response_Favoritos", responseFavoritos.toString())
                                }
                            }

                            override fun onFailure(call: Call<List<Favoritos>>, t: Throwable) {
                                Log.e("fail", t.message.toString())
                            }

                        })

                } else {
                    Log.e("Err_response_UserData", response.toString())
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Log.e("Err_UserData", t.message.toString())
            }

        })



    }

}

@Composable
fun Greeting(catadores: List<Favoritos>, authToken: String, idGerador : Int) {

    val api = RetrofitApi.getMainApi()
    val mainApi = api.create(ApiCalls::class.java)

    val apiLog = RetrofitApi.getLogisticApi()
    val logisticApi = apiLog.create(LogisticCalls::class.java)

    val context = LocalContext.current

    var searchText by remember { mutableStateOf("") }

    Box(modifier = Modifier.padding()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {

            Image(painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Voltar para catadores próximos",
                modifier = Modifier
                    .size(60.dp)
                    .clickable {
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                    }
                    .padding(8.dp)
            )

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Transparent)
                    .clip(RoundedCornerShape(18.dp)), shape = RoundedCornerShape(18.dp),
                placeholder = {
                    Text(
                        "Pesquisar por catador",
                        style = MaterialTheme.typography.body1.copy(color = White)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Ícone de pesquisa",
                        tint = White
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = White,
                    cursorColor = White,
                    placeholderColor = Color.Gray,
                    focusedIndicatorColor = Transparent,
                    unfocusedIndicatorColor = Transparent
                )
            )
        }

    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
    {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .width(380.dp)
                .height(700.dp)
                .padding(top = 80.dp, bottom = 10.dp, end = 10.dp, start = 10.dp)
                .clip(RoundedCornerShape((2.dp)))
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Catadores Favoritos",
                    fontSize = 22.sp,
                    color = Color(12, 139, 17),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 8.dp),
                    color = colorResource(id = R.color.lighter_gray),
                    thickness = 2.5f.dp
                )
            }
            if (catadores[0].id == 0){

                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

                    Text(text = stringResource(id = R.string.not_found_favorited), fontSize = 24.sp, fontWeight = FontWeight.Bold , color = colorResource(
                        id = R.color.dark_green
                    ))
                    Text(text = stringResource(id = R.string.todo_favorited))

                }

            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 45.dp)
                ) {
                    items(catadores.filter {
                        val nome = if (it.catador!!.user!!.pessoa_fisica?.get(0)?.nome != "") it.catador.user!!.pessoa_fisica?.get(0)?.nome else it.catador.user!!.pessoa_juridica?.get(0)?.nome_fantasia
                        nome!!.contains(
                            searchText,
                            ignoreCase = true
                        )
                    }) { catador ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                DisplayImageFromUrl(
                                    imageUrl = catador.catador!!.user!!.foto,
                                    description = "Foto ${
                                        if (catador.catador.user!!.pessoa_fisica?.get(0)?.nome != "") catador.catador.user.pessoa_fisica?.get(
                                            0
                                        )?.nome else catador.catador.user.pessoa_juridica?.get(0)?.nome_fantasia
                                    }",
                                    size = 65.dp,
                                    padding = 0.dp
                                )

                                Spacer(modifier = Modifier.width(12.dp))
                                Column (modifier = Modifier
                                    .width(130.dp)
                                    .height(100.dp)
                                    .padding(top = 12.dp)) {
                                    Text(
                                        text = "${if (catador.catador.user.pessoa_fisica?.get(0)?.nome != "") catador.catador.user.pessoa_fisica?.get(0)?.nome else catador.catador.user.pessoa_juridica?.get(0)?.nome_fantasia}",
                                        fontSize = 21.sp,
                                        color = Color(12, 139, 17),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Text(
                                        text = catador.catador.user.email,
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                ) {
                                    Button(
                                        modifier = Modifier
                                            .height(35.dp)
                                            .width(100.dp),
                                        onClick = {
                                            mainApi.getUserData(authToken, catador.catador.id_usuario)
                                                .enqueue(object : Callback<UserData> {
                                                    override fun onResponse(
                                                        call: Call<UserData>,
                                                        response: Response<UserData>
                                                    ) {
                                                        if (response.isSuccessful){
                                                            mainApi.checkFavorited(idGerador, catador.id_catador)
                                                                .enqueue(object : Callback<List<Favoritar>> {
                                                                    override fun onResponse(
                                                                        call: Call<List<Favoritar>>,
                                                                        responseCheck: Response<List<Favoritar>>
                                                                    ) {
                                                                        if(responseCheck.isSuccessful){
                                                                            logisticApi.getAverage(catador.id_catador).enqueue(object : Callback<List<Media>> {
                                                                                override fun onResponse(
                                                                                    call: Call<List<Media>>,
                                                                                    responseMedia: Response<List<Media>>
                                                                                ) {
                                                                                    if (responseMedia.isSuccessful){
                                                                                        val intent = Intent(context, Profile::class.java)
                                                                                        intent.putExtra("user", Gson().toJson(response.body()!!))
                                                                                        intent.putExtra("media", Gson().toJson(responseMedia.body()!![0]))
                                                                                        intent.putExtra("isFavorited", if(responseCheck.isSuccessful) "Favoritado" else "Favoritar")
                                                                                        context.startActivity(intent)
                                                                                    }
                                                                                }

                                                                                override fun onFailure(
                                                                                    call: Call<List<Media>>,
                                                                                    t: Throwable
                                                                                ) {
                                                                                    Log.i("fail", t.message.toString())
                                                                                }

                                                                            })
                                                                        }
                                                                    }

                                                                    override fun onFailure(
                                                                        call: Call<List<Favoritar>>,
                                                                        t: Throwable
                                                                    ) {
                                                                        Log.i("fail", t.message.toString())
                                                                    }

                                                                })
                                                        }
                                                    }

                                                    override fun onFailure(
                                                        call: Call<UserData>,
                                                        t: Throwable
                                                    ) {
                                                        Log.i("fail", t.message.toString())
                                                    }

                                                })
                                        },
                                        shape = RoundedCornerShape(13.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color(
                                                12,
                                                139,
                                                17
                                            )
                                        )
                                    ) {
                                        Text(text = "PERFIL", color = White, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
