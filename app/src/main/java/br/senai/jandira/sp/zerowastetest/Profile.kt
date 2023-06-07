package br.senai.jandira.sp.zerowastetest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.jandira.sp.zerowastetest.api.ApiCalls
import br.senai.jandira.sp.zerowastetest.api.LogisticCalls
import br.senai.jandira.sp.zerowastetest.api.RetrofitApi
import br.senai.jandira.sp.zerowastetest.dataSaving.SessionManager
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelRating.Media
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelRating.Rating
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.Favoritar
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserData
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class Profile : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ZeroWasteTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(8, 113, 19),
                ) {
                    OthersProfileContent()
                }
            }


        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OthersProfileContent() {
    val context = LocalContext.current
    val intent = (context as Profile).intent

    val sessionManager = SessionManager(context)
    val authToken = sessionManager.fetchAuthToken()

    val stringUser = intent.getStringExtra("user")
    val user = Gson().fromJson(stringUser, UserData::class.java)

    val stringMedia = intent.getStringExtra("media")
    val media = Gson().fromJson(stringMedia, Media::class.java)

    val logistic = RetrofitApi.getLogisticApi()
    val logisticApi = logistic.create(LogisticCalls::class.java)

    val api = RetrofitApi.getMainApi()
    val mainApi = api.create(ApiCalls::class.java)

    val scrollable = rememberScrollState()

    var rating by remember {
        mutableStateOf(5f)
    }

    var enderecoVisibility by remember {
        mutableStateOf(false)
    }

    var isFavorited by remember {
        mutableStateOf(intent.getStringExtra("isFavorited")!!)
    }

    var isDialogShown by remember {
        mutableStateOf(false)
    }

    var materialsClick by remember {
        mutableStateOf(false)
    }

    if (isDialogShown) {
        CustomDialogOthersProfile(onDismiss = { isDialogShown = false }, onConfirm = {
            user.catador?.get(0)?.id?.let { it1 ->
                logisticApi.checkRate(authToken, it1.toInt())
                    .enqueue(object : Callback<Rating> {
                        override fun onResponse(call: Call<Rating>, response: Response<Rating>) {
                            if (response.code() != 200) {
                                logisticApi.rate(
                                    Rating(
                                        id_gerador = 1,
                                        id_catador = user.catador!![0].id,
                                        nota = it.toInt()
                                    ), authToken
                                )
                                    .enqueue(object : Callback<Rating> {
                                        override fun onResponse(
                                            call: Call<Rating>,
                                            response: Response<Rating>
                                        ) {
                                            Toast.makeText(
                                                context,
                                                "Nota cadastrada com sucesso",
                                                Toast.LENGTH_LONG
                                            )
                                        }

                                        override fun onFailure(call: Call<Rating>, t: Throwable) {
                                            Log.i("fail", t.message.toString())
                                        }

                                    })
                            } else {
                                logisticApi.updateRate(
                                    Rating(
                                        id_gerador = 1,
                                        id_catador = user.catador!![0].id,
                                        nota = it.roundToInt()
                                    ), authToken
                                )
                                    .enqueue(object : Callback<Rating> {
                                        override fun onResponse(
                                            call: Call<Rating>,
                                            response: Response<Rating>
                                        ) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Nota cadastrada com sucesso",
                                                    Toast.LENGTH_LONG
                                                )
                                                isDialogShown = false

                                            }
                                            Log.i("ok", "ok")
                                        }

                                        override fun onFailure(call: Call<Rating>, t: Throwable) {
                                            Log.i("fail", t.message.toString())
                                        }

                                    })
                            }
                        }

                        override fun onFailure(call: Call<Rating>, t: Throwable) {
                            Log.i("fail", t.message.toString())
                        }

                    })
            }

        })
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollable)
//            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)

        ) {
            Column(modifier = Modifier.padding(6.dp)) {
                Image(painter = painterResource(id = R.drawable.back_arrow),
                    contentDescription = "Voltar para catadores próximos",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            val intent = Intent(context, CatadoresProximosActivity::class.java)
                            context.startActivity(intent)
                        }
                )


                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(6.dp)
                ) {
                    Card(
                        shape = CircleShape,
                        backgroundColor = colorResource(id = R.color.almost_white),
                        modifier = Modifier.size(150.dp)
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {

                            DisplayImageFromUrl(
                                imageUrl = user.foto,
                                "Foto de perfil",
                                size = 140.dp,
                                padding = 0.dp
                            )

                        }

                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = colorResource(id = R.color.white),
                                shape = RoundedCornerShape(25.dp)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${
                                if (user.pessoa_fisica?.size!! > 0) user.pessoa_fisica?.get(0)?.nome else user.pessoa_juridica?.get(
                                    0
                                )?.nome_fantasia
                            }",
                            modifier = Modifier.padding(start = 20.dp, bottom = 5.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Catador",
                            modifier = Modifier.padding(start = 20.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = colorResource(id = R.color.dark_green)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {


                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Button(
                                    onClick = {
                                        mainApi.favoritar(
                                            Favoritar(
                                                id_catador = user.catador?.get(0)?.id!!.toInt(),
                                                id_gerador = 1
                                            ),
                                            authToken
                                        )
                                            .enqueue(object : Callback<Favoritar> {
                                                override fun onResponse(
                                                    call: Call<Favoritar>,
                                                    response: Response<Favoritar>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        Log.i("teste", response.code().toString())
                                                        isFavorited =
                                                            if (response.code() == 201) "Favoritado" else "Favoritar"
                                                    } else {
                                                        Log.i("teste", response.code().toString())
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<Favoritar>,
                                                    t: Throwable
                                                ) {
                                                    Log.i("fail", t.message.toString())
                                                }

                                            })

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .padding(start = 10.dp, end = 5.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        colorResource(id = R.color.dark_green)
                                    )
                                ) {
                                    Text(text = isFavorited, color = Color.White)
                                }

                                CopyLinkButton(contact = user.telefone)

//                            OutlinedButton(
//                                onClick = { /*TODO*/ },
//                                modifier = Modifier
//                                    .fillMaxWidth(0.6f)
//                                    .padding(end = 10.dp, start = 5.dp),
//                                border = BorderStroke(1.dp, colorResource(id = R.color.dark_green))
//                            ) {
//                                Row(horizontalArrangement = Arrangement.SpaceAround) {
//                                    Image(
//                                        painter = painterResource(id = R.drawable.whatsapp_icon),
//                                        contentDescription = "Contato",
//                                        modifier = Modifier.size(15.dp)
//                                    )
//                                    Text(
//                                        text = "Contato",
//                                        color = Color.Black,
//                                        modifier = Modifier.padding(start = 10.dp)
//                                    )
//                        }

                            }
                        }
                    }
                }
            }
        }


        Column() {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                border = BorderStroke(1.dp, colorResource(id = R.color.light_green)),
                shape = RoundedCornerShape(0.dp)
            ) {

                Row(modifier = Modifier.padding(top = 20.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.35f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.user_email_text) + ":",
                            modifier = Modifier.padding(start = 15.dp, bottom = 40.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Text(
                            text = stringResource(id = R.string.enderecos_text) + ":",
                            modifier = Modifier.padding(start = 15.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )

                        if (!enderecoVisibility)
                            Spacer(modifier = Modifier.height(40.dp))
                        else
                            if (user.endereco_usuario!!.size == 1)
                                Spacer(modifier = Modifier.height(45.dp))
                            else if (user.endereco_usuario!!.size == 2)
                                Spacer(modifier = Modifier.height(35.dp))
                            else
                                Spacer(modifier = Modifier.height(90.dp))


                        Text(
                            text = stringResource(id = R.string.user_telephone_text) + ":",
                            modifier = Modifier.padding(start = 15.dp, bottom = 40.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Text(
                            text = stringResource(id = R.string.materials_text) + ":",
                            modifier = Modifier.padding(start = 15.dp, bottom = 40.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )

                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = user.email,
                            modifier = Modifier.padding(end = 15.dp, bottom = 40.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.dark_green)
                        )
                        Row(
                            modifier = Modifier
                                .padding(end = 15.dp)
                                .clickable { enderecoVisibility = !enderecoVisibility },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.visualizar_enderecos),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorResource(id = R.color.dark_green)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.drop_down_icon),
                                contentDescription = "Visualizar Materiais",
                                modifier = Modifier
                                    .size(25.dp)
                                    .padding(start = 6.dp)
                            )
                        }
                        if (!enderecoVisibility)
                            Spacer(modifier = Modifier.height(40.dp))
                        else
                            Spacer(modifier = Modifier.height(10.dp))
                        AnimatedVisibility(
                            visible = enderecoVisibility,
                            enter = slideInVertically(
                                initialOffsetY = { -40 }
                            ) + expandVertically(
                                expandFrom = Alignment.Top
                            ) + scaleIn(
                                transformOrigin = TransformOrigin(0.5f, 0f)
                            ) + fadeIn(initialAlpha = 0.3f),
                            exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(
                                targetScale = 1.2f
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 40.dp, bottom = 15.dp)
                            ) {

                                for (i in user.endereco_usuario!!.indices) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        user.endereco_usuario!![i].endereco!!.cidade?.let {
                                            Text(
                                                text = "- $it, ${user.endereco_usuario!![i].endereco!!.estado}",
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Text(
                            text = user.telefone,
                            modifier = Modifier.padding(end = 15.dp, bottom = 40.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.dark_green)
                        )
                        Row(
                            modifier = Modifier
                                .padding(end = 15.dp, bottom = 15.dp)
                                .clickable { materialsClick = !materialsClick },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.materials_recycle_text),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorResource(id = R.color.dark_green)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.drop_down_icon),
                                contentDescription = "Visualizar Materiais",
                                modifier = Modifier
                                    .size(25.dp)
                                    .padding(start = 6.dp)
                            )
                        }
                        AnimatedVisibility(
                            visible = materialsClick,
                            enter = slideInVertically(
                                // Start the slide from 40 (pixels) above where the content is supposed to go, to
                                // produce a parallax effect
                                initialOffsetY = { -40 }
                            ) + expandVertically(
                                expandFrom = Alignment.Top
                            ) + scaleIn(
                                // Animate scale from 0f to 1f using the top center as the pivot point.
                                transformOrigin = TransformOrigin(0.5f, 0f)
                            ) + fadeIn(initialAlpha = 0.3f),
                            exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(
                                targetScale = 1.2f
                            )
                        ) {
                            // Content that needs to appear/disappear goes here:
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 40.dp, bottom = 15.dp)
                            ) {

                                for (i in user.catador?.get(0)?.materiais_catador!!.indices) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        user.catador!![0].materiais_catador?.get(i)?.material!!.nome?.let {
                                            Text(
                                                text = "- $it",
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.user_biography),
                    modifier = Modifier.padding(bottom = 10.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = getBiography(dadosUsuario = user),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White

                )
            }

            Card(
                modifier = Modifier.padding(start = 30.dp, end = 30.dp, bottom = 20.dp),
                border = BorderStroke(2.dp, colorResource(id = R.color.light_green)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 0 until media.media.roundToInt()) {
                            Log.i("estrela", "${"0" !in media.media.toString()}")
                            if (i - 1 == media.media.roundToInt() && "0" !in media.media.toString()) {
                                Image(
                                    painter = painterResource(R.drawable.half_star),
                                    contentDescription = "Estrelas de Avaliação",
                                    modifier = Modifier.size(40.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.star_filled_icon),
                                    contentDescription = "Estrelas de Avaliação",
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                        }

                    }
                    Text(text = String.format("%.1f", media.media), fontSize = 80.sp)
                    Divider(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        color = colorResource(id = R.color.light_green),
                        thickness = 1.dp
                    )
                    Button(
                        onClick = {
                            isDialogShown = true
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .padding(start = 10.dp, end = 5.dp),
                        colors = ButtonDefaults.buttonColors(
                            colorResource(id = R.color.dark_green)
                        )
                    ) {
                        Text(text = "Avaliar", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = stringResource(id = R.string.corridas_finalizadas))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "300", fontWeight = FontWeight.SemiBold, fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = stringResource(id = R.string.favoritos_text))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "13", fontWeight = FontWeight.SemiBold, fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                }
            }
        }
    }
}





