package br.senai.jandira.sp.zerowastetest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
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
                                if (responseFavoritos.isSuccessful){
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
                                                    Greeting(responseFavoritos.body()!!, authToken)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Log.e("Err_response_Favoritos", response.toString())
                                }
                            }

                            override fun onFailure(call: Call<List<Favoritos>>, t: Throwable) {
                                Log.i("fail", t.message.toString())
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
fun Greeting(catadores: List<Favoritos>, authToken: String) {

    val api = RetrofitApi.getMainApi()
    val mainApi = api.create(ApiCalls::class.java)

    val apiLog = RetrofitApi.getLogisticApi()
    val logisticApi = apiLog.create(LogisticCalls::class.java)

    val context = LocalContext.current

    var searchText by remember { mutableStateOf("") }

    Box(modifier = Modifier.padding()) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
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
            }
            LazyColumn(
                modifier = Modifier
                    .padding(top = 45.dp)
            ) {
                items(catadores.filter {
                    val nome = if (it.catador.user!!.pessoa_fisica?.get(0)?.nome != "") it.catador.user.pessoa_fisica?.get(0)?.nome else it.catador.user.pessoa_juridica?.get(0)?.nome_fantasia
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
                                imageUrl = catador.catador.user!!.foto,
                                description = "${
                                    if (catador.catador.user!!.pessoa_fisica?.get(0)?.nome != "") catador.catador.user.pessoa_fisica?.get(
                                        0
                                    )?.nome else catador.catador.user.pessoa_juridica?.get(0)?.nome_fantasia
                                }",
                                size = 65.dp,
                                padding = 0.dp
                            )

//                            Image(
//                                painter = painterResource(R.drawable.foto_usuario),
//                                contentDescription = "${if (catador.catador.user!!.pessoa_fisica?.get(0)?.nome != "") catador.catador.user.pessoa_fisica?.get(0)?.nome else catador.catador.user.pessoa_juridica?.get(0)?.nome_fantasia}",
//                                modifier = Modifier
//                                    .size(65.dp)
//                                    .clip(CircleShape),
//                                contentScale = ContentScale.Crop
//                            )

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
                                    text = "${catador.catador.user.email}",
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
                                            .enqueue(object : retrofit2.Callback<UserData> {
                                                override fun onResponse(
                                                    call: Call<UserData>,
                                                    response: Response<UserData>
                                                ) {
                                                    if (response.isSuccessful){
                                                        mainApi.checkFavorited(1, catador.id_catador)
                                                            .enqueue(object : retrofit2.Callback<List<Favoritar>> {
                                                                override fun onResponse(
                                                                    call: Call<List<Favoritar>>,
                                                                    responseCheck: Response<List<Favoritar>>
                                                                ) {
                                                                    if(responseCheck.isSuccessful){
                                                                        logisticApi.getAverage(catador.id_catador).enqueue(object : retrofit2.Callback<List<Media>> {
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
