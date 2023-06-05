package br.senai.jandira.sp.zerowastetest


import android.content.Intent
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelCEP.CepResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.jandira.sp.zerowastetest.api.ApiCalls
import br.senai.jandira.sp.zerowastetest.api.CepCalls
import br.senai.jandira.sp.zerowastetest.api.GeoCalls
import br.senai.jandira.sp.zerowastetest.api.RetrofitApi
import br.senai.jandira.sp.zerowastetest.dataSaving.SessionManager
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.Address
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserAddress
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserData
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelGeocode.Results
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import java.net.URLEncoder

class CadastrarEnd : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofitApi = RetrofitApi.getMainApi()
        val userCalls = retrofitApi.create(ApiCalls::class.java)
        val sessionManager = SessionManager(this)
        val authToken = "Bearer " + sessionManager.fetchAuthToken()
        Log.i("testing_authToken", authToken)

        userCalls.getUserData(authToken).enqueue(object : Callback<UserData>{
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.isSuccessful){

                    Log.i("testing id", response.body()!!.id.toString())

                    setContent {
                        ZeroWasteTestTheme {
                            // A surface container using the 'background' color from the theme
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = Color(120, 160, 87)
                            ) {
                                CadastrarEndContent(response.body()!!.id, authToken)
                            }
                        }
                    }

                } else {
                    Log.e("error_request", response.toString())
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Log.e("error_connecting", t.message.toString())
            }

        })

    }
}

@Composable
fun CadastrarEndContent(userId: Int, authToken: String) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val retrofitApi = RetrofitApi.getMainApi()
    val retrofitCep = RetrofitApi.getCepApi()
    val retrofitGeocode = RetrofitApi.getGeoCodeApi()
    val userCalls = retrofitApi.create(ApiCalls::class.java)
    val cepCalls = retrofitCep.create(CepCalls::class.java)
    val geoCalls = retrofitGeocode.create(GeoCalls::class.java)

    Log.i("user_id", userId.toString())

    var cepState by remember {
        mutableStateOf("")
    }
    var cepError by remember {
        mutableStateOf(false)
    }

    var cidadeState by remember {
        mutableStateOf("")
    }
    var cidadeError by remember {
        mutableStateOf(false)
    }

    var ufState by remember {
        mutableStateOf("")
    }
    var ufError by remember {
        mutableStateOf(false)
    }

    var logradouroState by remember {
        mutableStateOf("")
    }
    var logradouroError by remember {
        mutableStateOf(false)
    }

    var numeroState by remember {
        mutableStateOf("")
    }
    var numeroError by remember {
        mutableStateOf(false)
    }

    var complementoState by remember {
        mutableStateOf("")
    }

    var nomeLocalState by remember {
        mutableStateOf("")
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {


        Row(modifier = Modifier.padding(bottom = 8.dp)) {

            Image(painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Voltar",
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 10.dp, top = 10.dp)
                    .clickable {
                        val backToHome = Intent(context, HomeActivity::class.java)
                        context.startActivity(backToHome)
                    }
            )

            Column(horizontalAlignment = Alignment.Start) {

                Text(
                    text = stringResource(id = R.string.cadastre_novo_endereco),
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

                Text(
                    text = stringResource(id = R.string.cadastre_nova_coleta),
                    modifier = Modifier
                        .fillMaxWidth(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

            }

        }




        OutlinedTextField(
            value = cepState,
            onValueChange = { cepState = it },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.White,
                focusedBorderColor = colorResource(
                    id = R.color.light_green
                ),
                cursorColor = colorResource(
                    id = R.color.dark_green
                ),
                focusedLabelColor = colorResource(id = R.color.dark_green)
            ),
            label = {
                Text(text = "CEP")
            },
            isError = cepError,
            singleLine = true
        )

        if (cepError) {
            Text(
                text = stringResource(id = R.string.empty_field_reduced),
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(end = 30.dp, start = 30.dp),
                textAlign = TextAlign.End
            )
        }

        Row {

            Column {

                OutlinedTextField(
                    value = cidadeState,
                    onValueChange = { cidadeState = it },
                    modifier = Modifier.width(170.dp),
                    //.padding(start = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White,
                        focusedBorderColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.dark_green
                        ),
                        focusedLabelColor = colorResource(id = R.color.dark_green)
                    ),
                    label = {
                        Text(text = "Cidade")
                    },
                    isError = cidadeError

                )
                if (cidadeError) {
                    Text(
                        text = stringResource(id = R.string.empty_field_reduced),
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(end = 30.dp, start = 30.dp),
                        textAlign = TextAlign.End
                    )
                }

            }


            Spacer(modifier = Modifier.width(7.dp))


            Column {
                OutlinedTextField(
                    value = ufState,
                    onValueChange = { ufState = it },
                    modifier = Modifier.width(180.dp),
                    //.padding(start = 10.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White,
                        focusedBorderColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.dark_green
                        ),
                        focusedLabelColor = colorResource(id = R.color.dark_green)
                    ),
                    label = {
                        Text(text = "UF")
                    },
                    isError = ufError,
                    singleLine = true
                )
                if (ufError) {
                    Text(
                        text = stringResource(id = R.string.empty_field_reduced),
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(end = 30.dp, start = 30.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        Row {

            Column {
                OutlinedTextField(
                    value = numeroState,
                    onValueChange = { numeroState = it },
                    modifier = Modifier.width(170.dp),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White,
                        focusedBorderColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.dark_green
                        ),
                        focusedLabelColor = colorResource(id = R.color.dark_green)
                    ),
                    label = {
                        Text(text = "Número")
                    },
                    isError = numeroError
                )
                if (numeroError) {
                    Text(
                        text = stringResource(id = R.string.empty_field_reduced),
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(end = 30.dp, start = 30.dp),
                        textAlign = TextAlign.End
                    )
                }
            }


            Spacer(modifier = Modifier.width(7.dp))

            Column {
                OutlinedTextField(
                    value = logradouroState,
                    onValueChange = { logradouroState = it },
                    modifier = Modifier.width(180.dp),
                    //.padding(start = 10.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White,
                        focusedBorderColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.dark_green
                        ),
                        focusedLabelColor = colorResource(id = R.color.dark_green)
                    ),
                    label = {
                        Text(text = "Logradouro")
                    },
                    isError = logradouroError,
                    singleLine = true
                )
                if (logradouroError) {
                    Text(
                        text = stringResource(id = R.string.empty_field_reduced),
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(end = 30.dp, start = 30.dp),
                        textAlign = TextAlign.End
                    )
                }
            }


        }

        OutlinedTextField(
            value = complementoState,
            onValueChange = { complementoState = it },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.White,
                focusedBorderColor = colorResource(
                    id = R.color.light_green
                ),
                cursorColor = colorResource(
                    id = R.color.dark_green
                ),
                focusedLabelColor = colorResource(id = R.color.dark_green)
            ),
            label = {
                Text(text = "Complemento")
            },
            singleLine = true
        )

        OutlinedTextField(
            value = nomeLocalState,
            onValueChange = { nomeLocalState = it },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.White,
                focusedBorderColor = colorResource(
                    id = R.color.light_green
                ),
                cursorColor = colorResource(
                    id = R.color.dark_green
                ),
                focusedLabelColor = colorResource(id = R.color.dark_green)
            ),
            label = {
                Text(text = "Nomear Local")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )



        Button(
            onClick = {

                focusManager.clearFocus()

                cepError = cepState.isEmpty()
                cidadeError = cidadeState.isEmpty()
                ufError = ufState.isEmpty()
                numeroError = numeroState.isEmpty()
                logradouroError = logradouroState.isEmpty()

                if (!cepError && !cidadeError && !ufError && !numeroError && !logradouroError) {

                    cepCalls.getAddressInfo(cepState).enqueue(object : Callback<CepResponse> {
                        override fun onResponse(
                            call: Call<CepResponse>,
                            response: Response<CepResponse>
                        ) {

                            Log.i("test", cepState)
                            Log.i("cep_success", response.toString())

                            if (response.body() != null) {
                                val cepBody = response.body()!!

                                val encodedURL =
                                    URLEncoder.encode("${numeroState}, ${cepBody.logradouro}, ${cepBody.localidade}, ${cepBody.uf}")
                                Log.i("testando", encodedURL)


                                geoCalls.getLatiLong(encodedURL, "8c86308380ad443fac12280fd96b4ac5")
                                    .enqueue(
                                        object : Callback<Results> {
                                            override fun onResponse(
                                                call: Call<Results>,
                                                response: Response<Results>
                                            ) {

                                                val resultLatLong =
                                                    response.body()!!.results!![0].geometry!!

                                                Log.i("testing_geometry", resultLatLong.toString())

                                                if (complementoState == "")
                                                    complementoState = " "

                                                val userAddress = Address(

                                                    cep = cepState,
                                                    logradouro = cepBody.logradouro,
                                                    bairro = cepBody.bairro,
                                                    cidade = cepBody.localidade,
                                                    estado = cepBody.uf,
                                                    complemento = complementoState,
                                                    numero = numeroState,
                                                    latitude = resultLatLong.latitude,
                                                    longitude = resultLatLong.longitude,
                                                    apelido = nomeLocalState,
                                                    id_usuario = userId

                                                )

                                                Log.i("what", userAddress.toString())

                                                userCalls.newEnderecoUser(authToken, userAddress)
                                                    .enqueue(object : Callback<UserAddress> {
                                                        override fun onResponse(
                                                            call: Call<UserAddress>,
                                                            response: Response<UserAddress>
                                                        ) {

                                                            Log.i(
                                                                "success_API",
                                                                response.toString()
                                                            )

                                                            if (response.code() == 400) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Endereço já cadastrado",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            } else if (response.code() == 201) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Endereço cadastrado",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }

                                                        }

                                                        override fun onFailure(
                                                            call: Call<UserAddress>,
                                                            t: Throwable
                                                        ) {
                                                            Log.i("fail_API", t.message.toString())
                                                        }
                                                    })

                                            }

                                            override fun onFailure(
                                                call: Call<Results>,
                                                t: Throwable
                                            ) {
                                                Log.i("fail_GEOCode", t.message.toString())
                                            }

                                        })

                            } else {
                                Toast.makeText(context, "Algo deu errado! Verifique o CEP", Toast.LENGTH_SHORT).show()
                            }

                        }

                        override fun onFailure(call: Call<CepResponse>, t: Throwable) {
                            Log.i("Cep_FAIL", t.message.toString())
                        }

                    })

                }

            }, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 7.dp, start = 30.dp, end = 30.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(107, 177, 115))
        ) {
            Text(
                text = "Cadastar",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        }


        Image(
            painter = painterResource(id = R.drawable.cadastrar_end_image),
            contentDescription = "Imagem Ilustrativa",
            modifier = Modifier.align(CenterHorizontally)
        )
    }
}