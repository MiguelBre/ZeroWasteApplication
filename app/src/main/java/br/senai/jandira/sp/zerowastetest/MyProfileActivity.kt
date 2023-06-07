package br.senai.jandira.sp.zerowastetest

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import br.senai.jandira.sp.zerowastetest.api.ApiCalls
import br.senai.jandira.sp.zerowastetest.api.RetrofitApi
import br.senai.jandira.sp.zerowastetest.dataSaving.SessionManager
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.MateriaisCatador
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserAddress
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserData
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ZeroWasteTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ProfileActivityBody()
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileActivityBody() {

    val context = LocalContext.current
    val scrollable = rememberScrollState()

    val retrofit = RetrofitApi.getMainApi()
    val apiCalls = retrofit.create(ApiCalls::class.java)

    val sessionManager = SessionManager(context)
    val authToken = "Bearer " + sessionManager.fetchAuthToken()

    Log.i("authToken", authToken)

    var dadosUsuario by remember {
        mutableStateOf(UserData())
    }
    var username by remember {
        mutableStateOf("...")
    }
    var userType by remember {
        mutableStateOf("...")
    }

    var materiaisCatador by remember {
        mutableStateOf(listOf<MateriaisCatador>())
    }

    var enderecosCadastrados by remember {
        mutableStateOf(listOf<UserAddress>())
    }

    var profilePicture by remember {
        mutableStateOf("")
    }

    var contactInfo by remember {
        mutableStateOf("")
    }

    val userInfo = apiCalls.getUserData(authToken).enqueue(object : Callback<UserData> {
        override fun onResponse(call: Call<UserData>, response: Response<UserData>) {

            if (response.body() != null) {

                dadosUsuario = response.body()!!
                username = if (dadosUsuario.pessoa_fisica!!.isEmpty())
                    dadosUsuario.pessoa_juridica!![0].nome_fantasia
                else
                    dadosUsuario.pessoa_fisica!![0].nome

                userType = if (dadosUsuario.catador!!.isEmpty())
                    "Gerador"
                else
                    "Catador"

                if (userType == "Catador")
                    materiaisCatador = dadosUsuario.catador!!.get(0).materiais_catador!!

                enderecosCadastrados = dadosUsuario.endereco_usuario!!

                profilePicture = dadosUsuario.foto

                contactInfo = dadosUsuario.telefone

            } else {

                val backToMain = Intent(context, MainActivity::class.java)
                context.startActivity(backToMain)

            }

        }

        override fun onFailure(call: Call<UserData>, t: Throwable) {
            Log.i("fail", t.message.toString())
        }
    })

    val userTelephone = dadosUsuario.telefone

    var menuVisibility by remember {
        mutableStateOf(false)
    }

    var actionOptionsVisibility by remember {
        mutableStateOf(false)
    }

    var requestPickupClick by remember {
        mutableStateOf(false)
    }
    var registerLocationClick by remember {
        mutableStateOf(false)
    }
    var mapCatadoresClick by remember {
        mutableStateOf(false)
    }
    var materialsClick by remember {
        mutableStateOf(false)
    }
    var enderecoVisibility by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .verticalScroll(scrollable)
            .fillMaxSize()
            .blur(blurEffect(menuVisibility))
    ) {

        Box(
            modifier = Modifier
                .background(colorResource(id = R.color.dark_green))
                .fillMaxWidth()
                .height(155.dp)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.borgor),
                    contentDescription = "",
                    modifier = Modifier
                        .size(45.dp)
                        .padding(start = 15.dp, top = 15.dp)
                        .clickable {
                            menuVisibility = !menuVisibility
                        }
                )
                Image(painter = painterResource(id = R.drawable.edit_icon),
                    contentDescription = "Editar Perfil",
                    modifier = Modifier
                        .size(45.dp)
                        .padding(end = 15.dp, top = 15.dp)
                        .clickable {
                            val toEditActivity = Intent(context, EditMyProfileActivity::class.java)
                            context.startActivity(toEditActivity)
                        }
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, end = 25.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 85.dp)
                        .background(
                            color = colorResource(id = R.color.almost_white),
                            shape = RoundedCornerShape(25.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = username,
                        modifier = Modifier.padding(start = 20.dp, bottom = 5.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = userType,
                        modifier = Modifier.padding(start = 20.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = colorResource(id = R.color.dark_green)
                    )
                }

                Card(
                    shape = CircleShape,
                    backgroundColor = colorResource(id = R.color.almost_white),
                    modifier = Modifier.size(130.dp)
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {

                        DisplayImageFromUrl(
                            imageUrl = profilePicture,
                            "Foto de perfil",
                            size = 120.dp,
                            padding = 0.dp
                        )

                    }

                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                CopyLinkButton(contact = contactInfo)

            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, colorResource(id = R.color.light_green)),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
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
                            if (enderecosCadastrados!!.size == 1)
                                Spacer(modifier = Modifier.height(45.dp))
                            else if (enderecosCadastrados!!.size == 2)
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
                        if (userType == "Catador") {
                            Text(
                                text = stringResource(id = R.string.workHours) + ":",
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
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = dadosUsuario.email,
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

                                for (i in enderecosCadastrados.indices) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        enderecosCadastrados[i].endereco!!.cidade?.let {
                                            Text(
                                                text = "- $it, ${enderecosCadastrados[i].endereco!!.estado}",
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Text(
                            text = userTelephone,
                            modifier = Modifier.padding(end = 15.dp, bottom = 40.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(id = R.color.dark_green)
                        )
                        if (userType == "Catador") {
                            Text(
                                text = "10:00-14:30",
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

                                    for (i in materiaisCatador.indices) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            materiaisCatador[i].material!!.nome?.let {
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
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.user_biography),
                    modifier = Modifier.padding(bottom = 10.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getBiography(dadosUsuario),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
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
                        Image(
                            painter = painterResource(R.drawable.star_filled_icon),
                            contentDescription = "Estrelas de Avaliação",
                            modifier = Modifier.size(40.dp)
                        )
                        Image(
                            painter = painterResource(R.drawable.star_filled_icon),
                            contentDescription = "Estrelas de Avaliação",
                            modifier = Modifier.size(40.dp)
                        )
                        Image(
                            painter = painterResource(R.drawable.star_filled_icon),
                            contentDescription = "Estrelas de Avaliação",
                            modifier = Modifier.size(40.dp)
                        )
                        Image(
                            painter = painterResource(R.drawable.star_filled_icon),
                            contentDescription = "Estrelas de Avaliação",
                            modifier = Modifier.size(40.dp)
                        )
                        Image(
                            painter = painterResource(R.drawable.star_filled_icon),
                            contentDescription = "Estrelas de Avaliação",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Text(text = "5.0", fontSize = 80.sp)
                    Divider(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        color = colorResource(id = R.color.light_green),
                        thickness = 1.dp
                    )
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


//Parte do MENU
    AnimatedVisibility(visible = menuVisibility,
        enter = slideInHorizontally(animationSpec = tween(400)) { fullWidth -> -fullWidth } + fadeIn(
            animationSpec = tween(durationMillis = 200)
        ),
        exit = slideOut(tween(100, easing = FastOutSlowInEasing)) {
            IntOffset(-180, 0)
        } + fadeOut(animationSpec = tween(durationMillis = 200))
    ) {
        Card(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 100.dp)
                .verticalScroll(scrollable),
            backgroundColor = colorResource(id = R.color.almost_white)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Image(painter = painterResource(id = R.drawable.back_arrow),
                    contentDescription = "Voltar",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 15.dp, top = 15.dp)
                        .clickable {
                            menuVisibility = !menuVisibility
                        }
                )
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp)
                        .clickable {
                            menuVisibility = !menuVisibility
                        },
                    shape = RoundedCornerShape(10.dp),
                    backgroundColor = colorResource(id = R.color.dark_green)
                ) {
                    Row {

                        DisplayImageFromUrl(
                            imageUrl = profilePicture,
                            "Foto de perfil",
                            size = 60.dp,
                            padding = 4.dp
                        )

//                        Image(
//                            painter = painterResource(id = R.drawable.avatar_standard_icon),
//                            contentDescription = "Foto do Perfil",
//                            modifier = Modifier
//                                .size(60.dp)
//                                .padding(4.dp)
//                        )
                        Column(
                            modifier = Modifier.padding(start = 4.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = username,
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                                color = Color.White,
                                fontSize = 18.sp
                            )
                            Text(text = "Conectado", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(start = 8.dp, end = 20.dp)
                        .clickable {
                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.home_icon),
                        contentDescription = "Página inicial",
                        modifier = Modifier
                            .size(55.dp)
                            .padding(start = 12.dp, end = 4.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.home_page_menu),
                        modifier = Modifier.padding(start = 4.dp, end = 20.dp),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 10.dp, end = 20.dp)
                        .clickable {
                            actionOptionsVisibility = !actionOptionsVisibility
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.actions_icon),
                        contentDescription = "Ações",
                        modifier = Modifier
                            .size(60.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string.actions_text_menu),
                            fontSize = 18.sp
                        )
                        Image(
                            painter = painterResource(id = R.drawable.drop_down_icon),
                            contentDescription = "Mais ações",
                            modifier = Modifier
                                .size(15.dp)
                        )
                    }
                }
                AnimatedVisibility(
                    visible = actionOptionsVisibility,
                    modifier = Modifier.padding(start = 20.dp),
                    enter = slideInVertically(
                        initialOffsetY = { -40 }
                    ) + expandVertically(
                        expandFrom = Alignment.Top
                    ) + fadeIn(initialAlpha = 0.3f),
                    exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(
                        targetScale = 1.2f
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(
                            start = 10.dp,
                            top = 10.dp,
                            end = 20.dp
                        )
                    ) {
                        if (userType == "Gerador") {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp)
                                .clickable {
                                    requestPickupClick = !requestPickupClick

                                    val toSolicitarColetaActivity = Intent(context, SolicitarColetaActivity::class.java)
                                    context.startActivity(toSolicitarColetaActivity)
                                }) {
                                Text(
                                    text = stringResource(id = R.string.request_pickup),
                                    color = verifyClick(
                                        getClick = requestPickupClick
                                    )
                                )
                            }
                        } else {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp)
                                .clickable {
                                    requestPickupClick = !requestPickupClick

                                    val toAceitarColetaActivity = Intent(context, AceitarColetaActivity::class.java)
                                    context.startActivity(toAceitarColetaActivity)

                                }) {
                                Text(
                                    text = stringResource(id = R.string.solicitacoes_coletas),
                                    color = verifyClick(
                                        getClick = requestPickupClick
                                    )
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp)
                                .clickable {
                                    registerLocationClick = !registerLocationClick
                                    val toCadastrarEnd = Intent(context, CadastrarEnd::class.java)
                                    context.startActivity(toCadastrarEnd)
                                }) {
                            Text(
                                text = stringResource(id = R.string.register_pickup_location),
                                color = verifyClick(
                                    getClick = registerLocationClick
                                )
                            )

                        }
                        if (userType == "Gerador") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 10.dp)
                                    .clickable {
                                        mapCatadoresClick = !mapCatadoresClick
                                        val toCatadoresProximosActivity =
                                            Intent(context, CatadoresProximosActivity::class.java)
                                        context.startActivity(toCatadoresProximosActivity)
                                    }) {
                                Text(
                                    text = stringResource(id = R.string.map_close_catadores),
                                    color = verifyClick(
                                        getClick = mapCatadoresClick
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(start = 8.dp, end = 20.dp)
                        .clickable {
                            val toCouponActivity = Intent(context, TelaPontos::class.java)
                            context.startActivity(toCouponActivity)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.prize_icon),
                        contentDescription = "Tela de pontos e cupons",
                        modifier = Modifier
                            .size(55.dp)
                            .padding(start = 12.dp, end = 4.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.prize_text_menu),
                        modifier = Modifier.padding(start = 4.dp, end = 20.dp),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(start = 8.dp, end = 20.dp)
                        .clickable {
                            /*TODO*/
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hints_icon),
                        contentDescription = "Dicas",
                        modifier = Modifier
                            .size(55.dp)
                            .padding(start = 12.dp, end = 4.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.hints_menu),
                        modifier = Modifier.padding(start = 4.dp, end = 20.dp),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(start = 8.dp, end = 20.dp)
                        .clickable {
                            /*TODO*/
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.favorites_icon),
                        contentDescription = "Favoritos",
                        modifier = Modifier
                            .size(55.dp)
                            .padding(start = 12.dp, end = 4.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.favorites_menu),
                        modifier = Modifier.padding(start = 4.dp, end = 20.dp),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(start = 10.dp, end = 20.dp)
                        .clickable {
                            sessionManager.eraseAuthToken()
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.log_out_icon),
                        contentDescription = "Sair",
                        modifier = Modifier
                            .size(55.dp)
                            .padding(start = 12.dp, end = 4.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.logout_menu),
                        modifier = Modifier.padding(start = 4.dp, end = 20.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun getBiography(dadosUsuario: UserData): String {

    var biography by remember {
        mutableStateOf("")
    }

    biography = dadosUsuario.biografia

    if (biography == "" || biography == null)
        return "Sem biografia ainda."
    else
        return biography
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview4() {
    ZeroWasteTestTheme {
        ProfileActivityBody()
    }
}

@Composable
fun CopyLinkButton(contact: String) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    OutlinedButton(
        onClick = {
            clipboardManager.setText(AnnotatedString(contact))
            Toast.makeText(context, "Telefone Copiado", Toast.LENGTH_SHORT)
                .show()
        },
        modifier = Modifier
            .fillMaxWidth(0.65f)
            .padding(end = 10.dp, start = 5.dp),
        border = BorderStroke(1.dp, colorResource(id = R.color.dark_green))
    ) {
        Row(horizontalArrangement = Arrangement.SpaceAround) {
            Image(
                painter = painterResource(id = R.drawable.whatsapp_icon),
                contentDescription = "Contato",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Contato",
                color = Color.Black,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun CustomToast(message: String, imageResId: Int) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Start
            )
        }
    }
}
