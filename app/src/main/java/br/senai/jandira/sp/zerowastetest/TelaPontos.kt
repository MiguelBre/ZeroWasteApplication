package com.example.telacupons


import androidx.compose.foundation.layout.padding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.telacupons.ui.theme.TelaCuponsTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import br.senai.jandira.sp.zerowastetest.TelaCupomRes
import com.example.telacupons.api.LogisticCalls
import com.example.telacupons.api.RetrofitApi
import com.example.telacupons.model.Coupon
import com.example.telacupons.model.Pontos
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofitApi = RetrofitApi.getLogisticApi()
        val orderApi = retrofitApi.create(LogisticCalls::class.java)

        var pontos = Pontos(
            pontos = 0
        )



        var unreedem = listOf(
            Coupon(
                id = 0,
                nome = "",
                codigo = "",
                pontos = 0,
                criterios = "",
                descricao = ""
            )
        )

        var reedem = listOf(
            Coupon(
                id = 0,
                nome = "",
                codigo = "",
                pontos = 0,
                criterios = "",
                descricao = ""
            )
        )





        orderApi.getUnreedemCoupons("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTMsInVzZXJfdHlwZSI6IkdFUkFET1IiLCJpZF91c3VhcmlvIjoxMywiaWRfbW9kbyI6NCwiaWF0IjoxNjg2MDQ4NTk0LCJleHAiOjE2ODYxMzQ5OTR9.gHtnt7IXkrS991vMHRHhNuiB-w6VuBYS6RGcDxxRnOo")
            .enqueue(object : Callback<List<Coupon>> {
                override fun onResponse(
                    call: Call<List<Coupon>>,
                    response: Response<List<Coupon>>
                ) {
                    if (response.isSuccessful) {
                        unreedem = response.body()!!

                        orderApi.getReedemCoupons("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTMsInVzZXJfdHlwZSI6IkdFUkFET1IiLCJpZF91c3VhcmlvIjoxMywiaWRfbW9kbyI6NCwiaWF0IjoxNjg2MDQ4NTk0LCJleHAiOjE2ODYxMzQ5OTR9.gHtnt7IXkrS991vMHRHhNuiB-w6VuBYS6RGcDxxRnOo")
                            .enqueue(object : Callback<List<Coupon>> {
                                override fun onResponse(
                                    call: Call<List<Coupon>>,
                                    responseReedem: Response<List<Coupon>>
                                ) {
                                    if (responseReedem.isSuccessful) {
                                        reedem = responseReedem.body()!!

                                        orderApi.getPontos("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTMsInVzZXJfdHlwZSI6IkdFUkFET1IiLCJpZF91c3VhcmlvIjoxMywiaWRfbW9kbyI6NCwiaWF0IjoxNjg2MDQ4NTk0LCJleHAiOjE2ODYxMzQ5OTR9.gHtnt7IXkrS991vMHRHhNuiB-w6VuBYS6RGcDxxRnOo")
                                            .enqueue(object : Callback<Pontos> {
                                                override fun onResponse(call: Call<Pontos>, responsePonto: Response<Pontos>) {
                                                    if (responsePonto.isSuccessful) {
                                                        pontos = responsePonto.body()!!

                                                        setContent {
                                                            TelaCuponsTheme {
                                                                // A surface container using the 'background' color from the theme
                                                                Surface(
                                                                    modifier = Modifier.fillMaxSize(),
                                                                    color = Color(255, 255, 255)
                                                                ) {
                                                                    Greeting(pontos, unreedem, reedem)
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                        Log.e("Err_getPontos", response.toString())
                                                    }

                                                }

                                                override fun onFailure(call: Call<Pontos>, t: Throwable) {
                                                    Log.i("fail", t.message.toString())
                                                }
                                            })
                                    } else {
                                        Log.e("Err_getRedeemCoupon", response.toString())
                                    }
                                }

                                override fun onFailure(call: Call<List<Coupon>>, t: Throwable) {
                                    Log.i("fail", t.message.toString())
                                }

                            })
                    } else {
                        Log.e("Err_getUnredeemCoupon", response.toString())
                    }
                }

                override fun onFailure(call: Call<List<Coupon>>, t: Throwable) {
                    Log.i("fail", t.message.toString())
                }

            })




    }
}


@Composable
fun RoundedTextField(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    placeholder: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    value: String,
    onValueChange: (String) -> Unit
) {


    var searchText by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchText,
        onValueChange = { newText -> searchText = newText },
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .padding(16.dp)
            .background(color = Color(240, 240, 240), shape = shape)
            .clip(shape),
        textStyle = MaterialTheme.typography.body1,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            placeholderColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}


@Composable
fun Greeting(pontos: Pontos, unreedem: List<Coupon>, reedem: List<Coupon>) {

    val retrofitApi = RetrofitApi.getLogisticApi()
    val orderApi = retrofitApi.create(LogisticCalls::class.java)

    var isVisible by remember { mutableStateOf(false) }


    val selectedRow = remember { mutableStateOf(-1) }

    val scrollState = rememberScrollState()


    fun showRowDetails(rowId: Int) {
        selectedRow.value = rowId
    }

    fun hideRowDetails() {
        selectedRow.value = -1
    }


    var Pesquisastate by remember {
        mutableStateOf("")
    }

    val navController = rememberNavController()

    // cor 2
    var VERDE by remember {
        mutableStateOf(Color(8, 113, 19))
    }

    var DisponiveisClick by remember {
        mutableStateOf(true)
    }

    var ResgatadosClick by remember {
        mutableStateOf(false)
    }

    var color1 by remember {
        mutableStateOf(Color(53, 155, 55))
    }
    var color2 by remember {
        mutableStateOf(Color.Transparent)
    }

    val context = LocalContext.current

    var searchText by remember { mutableStateOf("") }

    var selectedWord by remember { mutableStateOf("disponiveis") }

    var showAlert by remember { mutableStateOf(false) }

    var selectedCupom by remember {
        mutableStateOf(Coupon(
            nome = "",
            id = 0,
            descricao = "",
            criterios = "",
            pontos = 0,
            codigo = ""
        ))
    }

    if (showAlert){
        Dialogs(onDismiss = { showAlert = false }, onConfirm = {
            orderApi.reedemCoupon("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTMsInVzZXJfdHlwZSI6IkdFUkFET1IiLCJpZF91c3VhcmlvIjoxMywiaWRfbW9kbyI6NCwiaWF0IjoxNjg2MDQ4NTk0LCJleHAiOjE2ODYxMzQ5OTR9.gHtnt7IXkrS991vMHRHhNuiB-w6VuBYS6RGcDxxRnOo", it)
                .enqueue(object : Callback<Coupon> {
                    override fun onResponse(
                        call: Call<Coupon>,
                        response: Response<Coupon>
                    ) {
                        if (response.isSuccessful){
                            val intent = Intent(context, CupomActivity::class.java)
                            intent.putExtra("coupon", Gson().toJson(response.body()!!))

                            context.startActivity(intent)
                        } else{
                            Toast.makeText(context, "Você não tem pontos suficientes", Toast.LENGTH_LONG).show()

                        }

                    }

                    override fun onFailure(
                        call: Call<Coupon>,
                        t: Throwable
                    ) {
                        Log.i("fail", t.message.toString())
                    }

                })
        }, coupon = selectedCupom)
    }

    var showCoupon by remember {
        mutableStateOf(false)
    }

    if (showCoupon) {
        TelaCupomRes(coupon = selectedCupom, onDismiss = { showCoupon = false})
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(255, 255, 255))


    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(id = R.string.foto_logo),
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(top = 5.dp)
                .width(60.dp)
                .height(51.dp)
        )

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black, fontSize = 18.sp)) {
                    append("SEU SALDO:")
                }
                append(' ')
                withStyle(style = SpanStyle(color = Color(30, 122, 22), fontSize = 19.sp)) {
                    append(pontos.pontos.toString())
                }
                append(' ')
                withStyle(style = SpanStyle(color = Color.Black, fontSize = 19.sp)) {
                    append("PONTOS")
                }

            },
            modifier = Modifier.align(CenterHorizontally),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        RoundedTextField(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            placeholder = {
                Text(
                    "Busque por mercados por aqui...",
                    style = MaterialTheme.typography.body1.copy(color = Color.White),
                    modifier = Modifier.fillMaxSize()
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Ícone de pesquisa",
                    tint = Color.White
                )
            },
            value = searchText,
            onValueChange = { searchText = it }
        )




        Text(
            text = stringResource(id = R.string.texto),
            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 13.dp, end = 15.dp),
            horizontalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier
                    .background(
                        color = Color.White, shape = RoundedCornerShape(7.dp)
                    ), verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = "Disponíveis", modifier = Modifier
                    .background(
                        color1,
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            bottomStart = 4.dp
                        )
                    )
                    .clickable {
                        selectedWord = "disponiveis"
                        DisponiveisClick = true
                        ResgatadosClick = false
                        isVisible = true
                        color1 = Color(53, 155, 55)
                        color2 = Color.Transparent

                    }
                    .padding(
                        top = 4.dp,
                        bottom = 4.dp
                    )

                    .width(130.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center)


                Text(text = "Resgatados",
                    modifier = Modifier
                        .background(
                            color2,
                            shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                        )
                        .clickable {
                            selectedWord = "resgatados"
                            DisponiveisClick = false
                            ResgatadosClick = true
                            isVisible = false
                            color2 = Color(181, 116, 48)
                            color1 = Color.Transparent
                        }
                        .padding(
                            top = 4.dp,
                            bottom = 4.dp,
                        )
                        .width(130.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center)

            }
        }

        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {

            when (selectedWord) {
                "disponiveis" -> {

                    unreedem.forEach {
                        Box(
                            modifier = Modifier
                                .height(130.dp)
                                .fillMaxWidth()
                        ) {
                            Card(
                                modifier = Modifier
                                    .width(400.dp)
                                    .height(130.dp)
                                    .padding(start = 25.dp, end = 25.dp, top = 15.dp),
                                shape = RoundedCornerShape(15.dp),
                                backgroundColor = Color(53, 155, 55)

                            ) {

                                Row(
                                    modifier = Modifier.padding(bottom = 25.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.logo_sem_fundo),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .padding(start = 10.dp)

                                    )

                                }

                                Column(
                                    modifier = Modifier.padding(start = 80.dp, top = 15.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                )
                                {

                                    Text(
                                        modifier = Modifier.fillMaxWidth(0.9F),
                                        textAlign = TextAlign.Center,
                                        text = it.nome,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )

                                    Row() {

                                        Box(
                                            modifier = Modifier
                                                .width(95.dp)
                                                .height(80.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(R.drawable.retangulo),
                                                contentDescription = "Imagem",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(80.dp)
                                            )
                                            Text(
                                                text = "${it.pontos} pontos",
                                                modifier = Modifier
                                                    .align(Alignment.Center),
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.padding(5.dp))

                                        Row(modifier = Modifier.padding(top = 13.dp))
                                        {
                                            Button(
                                                onClick = {
                                                    selectedCupom = it
                                                    showAlert = true


                                                }, modifier = Modifier
                                                    .width(92.dp)
                                                    .height(32.dp),
                                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                                            )
                                            {
                                                Text(text = "PEGUE", color = Color(8, 113, 19))

                                            }
                                        }

                                    }

                                }

                            }

                            Box(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .size(40.dp)
                                    .align(Alignment.CenterStart)
                                    .offset(x = (-1).dp)
                                    .background(Color.White, shape = CircleShape)
                            ) {

                            }
                            Box(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .size(40.dp)
                                    .align(Alignment.CenterEnd)
                                    .offset(x = (2).dp)
                                    .background(Color.White, shape = CircleShape)
                            )


                        }
                    }


                }

                "resgatados" -> {

                    reedem.forEach{
                        // Primeiro Card
                        Box(
                            modifier = Modifier
                                .height(130.dp)
                                .fillMaxWidth()
                        )
                        {

                            Card(
                                modifier = Modifier
                                    .width(400.dp)
                                    .height(130.dp)
                                    .padding(start = 25.dp, end = 25.dp, top = 15.dp),
                                shape = RoundedCornerShape(15.dp),
                                backgroundColor = Color(181, 116, 48)
                            )
                            {


                                Row(
                                    modifier = Modifier.padding(bottom = 25.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.logo_sem_fundo),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .padding(start = 10.dp)

                                    )

                                }

                                Column(
                                    modifier = Modifier.padding(start = 80.dp, top = 15.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                )
                                {

                                    Text(
                                        modifier = Modifier.fillMaxWidth(0.8f),
                                        textAlign = TextAlign.Justify,
                                        text = it.nome,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )


                                    Row() {

                                        Box(
                                            modifier = Modifier
                                                .width(95.dp)
                                                .height(80.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(R.drawable.retangulo),
                                                contentDescription = "Imagem",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(80.dp)
                                            )
                                            Text(
                                                text = "${it.pontos} pontos",
                                                modifier = Modifier
                                                    .align(Alignment.Center),
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.padding(5.dp))

                                        Row(modifier = Modifier.padding(top = 13.dp))
                                        {
                                            Button(
                                                onClick = {

                                                    orderApi.getCouponById(it.id).enqueue(object : Callback<Coupon> {
                                                        override fun onResponse(
                                                            call: Call<Coupon>,
                                                            response: Response<Coupon>
                                                        ) {
                                                            if (response.isSuccessful){
                                                                selectedCupom = it
                                                                showCoupon = true
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: Call<Coupon>,
                                                            t: Throwable
                                                        ) {
                                                            Log.i("fail", t.message.toString())
                                                        }

                                                    })

                                                }, modifier = Modifier
                                                    .width(92.dp)
                                                    .height(32.dp),
                                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                                            )
                                            {
                                                Text(text = "VEJA", color = Color(181, 116, 48))

                                            }


                                        }


                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .size(40.dp)
                                    .align(Alignment.CenterStart)
                                    .offset(x = (-1).dp)
                                    .background(Color.White, shape = CircleShape)
                            ) {

                            }
                            Box(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .size(40.dp)
                                    .align(Alignment.CenterEnd)
                                    .offset(x = (2).dp)
                                    .background(Color.White, shape = CircleShape)
                            )
                        }
                    }


                }


            }
        }

    }

}


