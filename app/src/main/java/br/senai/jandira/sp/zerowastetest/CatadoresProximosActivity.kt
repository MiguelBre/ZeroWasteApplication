package br.senai.jandira.sp.zerowastetest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import br.senai.jandira.sp.zerowastetest.api.ApiCalls
import br.senai.jandira.sp.zerowastetest.api.LogisticCalls
import br.senai.jandira.sp.zerowastetest.api.RetrofitApi
import br.senai.jandira.sp.zerowastetest.dataSaving.SessionManager
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelPedido.ListEnderecoUsuario
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelRating.Media
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.Address
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.Favoritar
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserAddress
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserData
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.CatadoresProximos
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import com.google.gson.Gson

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatadoresProximosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val api = RetrofitApi.getMainApi()
        val mainApi = api.create(ApiCalls::class.java)

        val sessionManager = SessionManager(this)
        val authToken = "Bearer " + sessionManager.fetchAuthToken()
        var enderecos: List<UserAddress>
        var idUser: Int

        mainApi.getUserData(authToken).enqueue(object : Callback<UserData>{
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {

                if(response.isSuccessful){

                    idUser = response.body()!!.id

                    mainApi.getEnderecoUsuario(
                        authToken,
                        idUser
                    )
                        .enqueue(object : Callback<List<UserAddress>> {
                            override fun onResponse(
                                call: Call<List<UserAddress>>,
                                response: Response<List<UserAddress>>
                            ) {
                                if (response.isSuccessful) {
                                    enderecos = response.body()!!

                                    setContent {
                                        ZeroWasteTestTheme {
                                            Surface(
                                                modifier = Modifier.fillMaxSize(),
                                                color = Color(8, 113, 19),
                                            ) {
                                                CatadoresProximosContent(enderecos, authToken)
                                            }
                                        }
                                    }
                                } else {
                                    Log.e("Err_responseAddress", response.toString())
                                }
                            }

                            override fun onFailure(call: Call<List<UserAddress>>, t: Throwable) {
                                Log.i("fail", t.message.toString())
                            }

                        })

                } else {
                    Log.e("Err_response", response.toString())
                }

            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Log.e("Err_getUserData", t.message.toString())
            }

        })


    }
}

@Composable
fun CatadoresProximosContent(enderecoUsuario: List<UserAddress>, authToken: String) {

    val context = LocalContext.current

    val api = RetrofitApi.getMainApi()
    val mainApi = api.create(ApiCalls::class.java)
    val logistic = RetrofitApi.getLogisticApi()
    val logisticApi = logistic.create(LogisticCalls::class.java)

    var searchText by remember { mutableStateOf("") }

    var selectedLocal by remember { mutableStateOf("") }
    var selectLocalId by remember {
        mutableStateOf(0)
    }

    var catadoresProximos by remember {
        mutableStateOf(
            listOf(
                CatadoresProximos(
                    cidade = "",
                    numero = "",
                    logradouro = "",
                    id_usuario = 0,
                    nome = "",
                    id_catador = 0,
                    distance = 0.0,
                    foto = "",
                    nome_fantasia = ""
                )
            )
        )
    }

    var list1 by remember {
        mutableStateOf(enderecoUsuario)
    }
    Log.i("list", list1.toString())

    var expanded1 by remember {
        mutableStateOf(false)
    }

    val icon1 = if (expanded1) {
        Icons.Filled.KeyboardArrowUp

    } else {
        Icons.Filled.KeyboardArrowDown
    }

    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row (verticalAlignment = Alignment.CenterVertically){

            Image(painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Voltar para catadores próximos",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                    }
                    .padding(8.dp)
            )
            Box {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, end = 16.dp, start = 16.dp, bottom = 8.dp)
                        .background(Black)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = {
                        Text(
                            "Pesquisar por catador/material...",
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
                        placeholderColor = Color.LightGray,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

        }

        Column(
            modifier = Modifier
                .padding()
                .padding(top = 8.dp, end = 16.dp, start = 16.dp, bottom = 16.dp)
        ) {

            OutlinedTextField(
                value = selectedLocal,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(188, 219, 183)
                ),
                label = { Text(text = "Selecione o local") },
                trailingIcon = {
                    Icon(icon1, "", Modifier.clickable { expanded1 = !expanded1 })
                }
            )


            Log.i("expanded", expanded1.toString())


            DropdownMenu(
                expanded = expanded1,
                onDismissRequest = { expanded1 = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),

                ) {
                list1.map { label ->
                    DropdownMenuItem(onClick = {
                        selectedLocal = label.endereco!!.apelido.toString()
                        selectLocalId = label.id_endereco
                        expanded1 = false

                        mainApi.getNearCollectors(
                            authToken,
                            selectLocalId
                        )
                            .enqueue(object : Callback<List<CatadoresProximos>> {
                                override fun onResponse(
                                    call: Call<List<CatadoresProximos>>,
                                    response: Response<List<CatadoresProximos>>
                                ) {
                                    if (response.isSuccessful) {
                                        catadoresProximos = response.body()!!
                                    }
                                }

                                override fun onFailure(
                                    call: Call<List<CatadoresProximos>>,
                                    t: Throwable
                                ) {
                                    Log.i("fail", t.message.toString())
                                }

                            })
                        Log.i("teste", selectLocalId.toString())
                    }) {
                        label.endereco!!.apelido?.let { Text(text = it) }
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp, end = 9.dp, start = 9.dp)
                .clip(RoundedCornerShape((2.dp)))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Catadores Próximos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
            }

            if (catadoresProximos[0].id_catador > 0) {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 62.dp)
                ) {
                    items(catadoresProximos.filter {
                        it.nome?.contains(
                            searchText,
                            ignoreCase = true
                        ) ?: false
                    }) { catador ->
                        Card(
                            elevation = 6.dp,
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
                                    imageUrl = catador.foto,
                                    description = "Foto ${catador.nome}",
                                    size = 80.dp,
                                    padding = 0.dp
                                )
//                                Image(
//                                    painter = painterResource(R.drawable.baseline_person_24),
//                                    contentDescription = "${catador.nome}",
//                                    modifier = Modifier
//                                        .size(80.dp)
//                                        .clip(CircleShape),
//                                    contentScale = ContentScale.Crop
//                                )

                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    catador.nome?.let {
                                        Text(
                                            text = it,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    Text(
                                        text = "${catador.cidade}",
                                        fontSize = 18.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        Button(
                                            modifier = Modifier
                                                .height(35.dp)
                                                .width(140.dp),
                                            onClick = {
                                                mainApi.getUserData(authToken, catador.id_usuario)
                                                    .enqueue(object : Callback<UserData> {
                                                        override fun onResponse(
                                                            call: Call<UserData>,
                                                            response: Response<UserData>
                                                        ) {
                                                            if (response.isSuccessful){
                                                                Log.i("cata", catador.id_catador.toString())
                                                                logisticApi.getAverage(catador.id_catador)
                                                                    .enqueue(object : Callback<List<Media>> {
                                                                        override fun onResponse(
                                                                            call: Call<List<Media>>,
                                                                            responseMedia: Response<List<Media>>
                                                                        ) {
                                                                            if (responseMedia.isSuccessful){
                                                                                mainApi.checkFavorited(1, catador.id_catador)
                                                                                    .enqueue(object : Callback<List<Favoritar>> {
                                                                                        override fun onResponse(
                                                                                            call: Call<List<Favoritar>>,
                                                                                            responseCheck: Response<List<Favoritar>>
                                                                                        ) {
                                                                                            val intent = Intent(context, Profile::class.java)
                                                                                            intent.putExtra("user", Gson().toJson(response.body()!!))
                                                                                            intent.putExtra("media", Gson().toJson(responseMedia.body()!![0]))
                                                                                            intent.putExtra("isFavorited", if(responseCheck.isSuccessful) "Favoritado" else "Favoritar")
                                                                                            context.startActivity(intent)
                                                                                        }

                                                                                        override fun onFailure(
                                                                                            call: Call<List<Favoritar>>,
                                                                                            t: Throwable
                                                                                        ) {
                                                                                            Log.i("fail",t.message.toString())
                                                                                        }

                                                                                    })

                                                                            } else{
                                                                                Log.i("teste",
                                                                                    responseMedia.code().toString()
                                                                                )
                                                                            }

                                                                        }

                                                                        override fun onFailure(
                                                                            call: Call<List<Media>>,
                                                                            t: Throwable
                                                                        ) {
                                                                            Log.i("fail" , t.message.toString())
                                                                        }

                                                                    })

                                                            } else{
                                                                Log.i("cata", catador.id_catador.toString())
                                                                Log.i("fail", response.code().toString())
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
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = Color(
                                                    8,
                                                    113,
                                                    19
                                                )
                                            )
                                        ) {
                                            Text(text = "Ver perfil", color = White)
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


}

@Composable
fun AddressSelectorPopup(
    catadores: List<String>,
    onAddressSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf("") }

    Column {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Expand"
            )
        }
        if (expanded) {
            AlertDialog(
                onDismissRequest = { expanded = false },
                title = { Text(text = "Selecione um endereço") },
                text = {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        catadores.forEach { address ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedAddress = address
                                    expanded = false
                                    onAddressSelected(selectedAddress)
                                }
                            ) {
                                Text(text = address)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            expanded = false
                            onAddressSelected(selectedAddress)
                        }
                    ) {
                        Text(text = "Selecionar")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { expanded = false }
                    ) {
                        Text(text = "Cancelar")
                    }
                }
            )
        }
    }
}
