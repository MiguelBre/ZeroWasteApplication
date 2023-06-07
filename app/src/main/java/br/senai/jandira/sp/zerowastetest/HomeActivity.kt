package br.senai.jandira.sp.zerowastetest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.senai.jandira.sp.zerowastetest.api.ApiCalls
import br.senai.jandira.sp.zerowastetest.api.RetrofitApi
import br.senai.jandira.sp.zerowastetest.dataSaving.SessionManager
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserData
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelGeocode.Geometry
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CompletableFuture

class HomeActivity : ComponentActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation: Geometry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val sessionManager = SessionManager(this)
        val cleanToken = sessionManager.fetchAuthToken()

        val socketHandler = SocketHandler()
        socketHandler.setSocket(cleanToken)
        socketHandler.establishConnection()

        fun fetchLocation(): CompletableFuture<Geometry> {
            val completableFuture = CompletableFuture<Geometry>()

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }

            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latLong = Geometry(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        completableFuture.complete(latLong)
                    } else {
                        // Caso não seja possível obter a localização
                        completableFuture.complete(null)
                    }
                }
                .addOnFailureListener { e ->
                    // Em caso de erro ao obter a localização
                    completableFuture.completeExceptionally(e)
                }

            return completableFuture
        }


        fetchLocation().thenAccept { location ->
            currentLocation = location

            setContent {


                ZeroWasteTestTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
//                        color = colorResource(id = R.color.light_green)
                    ) {

                        currentLocation?.let { location ->
                            Log.i("location", currentLocation.toString())
                            HomeContent()
                        }
                    }
                }
            }
        }


    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeContent() {

    val scrollable = rememberScrollState()

    val context = LocalContext.current

    val retrofit = RetrofitApi.getMainApi()
    val apiCalls = retrofit.create(ApiCalls::class.java)
    val sessionManager = SessionManager(context)

    val authToken = "Bearer " + sessionManager.fetchAuthToken()

    var dadosUsuario by remember {
        mutableStateOf(UserData())
    }
    var username by remember {
        mutableStateOf("...")
    }
    var userType by remember {
        mutableStateOf("...")
    }

    var profilePicture by remember {
        mutableStateOf("")
    }

    apiCalls.getUserData(authToken).enqueue(object : Callback<UserData> {
        override fun onResponse(call: Call<UserData>, response: Response<UserData>) {

            if (response.body() == null) {

                val backToMain = Intent(context, MainActivity::class.java)
                context.startActivity(backToMain)

            } else {

                Log.i("tesiting", response.code().toString())

                dadosUsuario = response.body()!!
                username = if (dadosUsuario.pessoa_fisica!!.isEmpty())
                    dadosUsuario.pessoa_juridica!![0].nome_fantasia
                else
                    dadosUsuario.pessoa_fisica!![0].nome

                userType = if (dadosUsuario.catador!!.isEmpty())
                    "Gerador"
                else
                    "Catador"

                sessionManager.saveUserId(dadosUsuario.id)

                if (dadosUsuario.catador!!.isNotEmpty()){

                    sessionManager.saveUserIdType(dadosUsuario.catador!![0].id)
                    Log.i("teste_idcatador", sessionManager.getUserIdType().toString())

                } else {

                    sessionManager.saveUserIdType(dadosUsuario.gerador!![0].id)

                }

                profilePicture = dadosUsuario.foto
            }

        }

        override fun onFailure(call: Call<UserData>, t: Throwable) {
            Log.i("fail", t.message.toString())
        }
    })

    var menuState by remember {
        mutableStateOf(false)
    }

    Image(
        painter = painterResource(id = R.drawable.ellipse_top_home),
        contentDescription = "",
        modifier = Modifier
            .padding(start = 215.dp)
            .blur(radius = blurEffect(menuState)),
        alignment = Alignment.TopEnd
    )
    Image(
        painter = painterResource(id = R.drawable.ellipse_bottom_home),
        contentDescription = "",
        modifier = Modifier
            .padding(end = 260.dp)
            .blur(radius = blurEffect(menuState)),
        alignment = Alignment.BottomStart
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .blur(blurEffect(menuState))
            .verticalScroll(scrollable)
    ) {
        Image(
            painter = painterResource(id = R.drawable.borgor),
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .padding(start = 15.dp, top = 15.dp)
                .clickable {
                    menuState = !menuState
                }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(65.dp)
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(8, 113, 19))) {
                        append("Zero")
                    }
                    append(' ')
                    withStyle(style = SpanStyle(color = Color.Black)) {
                        append("Waste")
                    }
                }, fontSize = 26.sp, fontWeight = FontWeight.Bold
            )
        }
        Image(
            painter = painterResource(id = R.drawable.lata_melhor),
            contentDescription = "",
            modifier = Modifier.size(380.dp),
            alignment = Alignment.Center
        )

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround

        ) {
            Text(
                text = stringResource(id = R.string.text_homeactivity),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
            Button(
                onClick = {
                    val toPagerSlider = Intent(context, PageSliderActivity::class.java)
                    context.startActivity(toPagerSlider)
                },
                border = BorderStroke(2.dp, color = Color.White),
                modifier = Modifier.padding(start = 25.dp, top = 20.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(8, 113, 19))
            ) {
                Text(
                    text = stringResource(id = R.string.learn_more),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
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

    AnimatedVisibility(visible = menuState,
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
                            menuState = !menuState
                        }
                )
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp)
                        .clickable {
                            val intent = Intent(context, MyProfileActivity::class.java)
                            context.startActivity(intent)
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
                            menuState = !menuState
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

                                    val toSolicitarColetaActivity =
                                        Intent(context, SolicitarColetaActivity::class.java)
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

                                    val toAceitarColetaActivity =
                                        Intent(context, AceitarColetaActivity::class.java)
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
                        contentDescription = "Página inicial",
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
                            val intent = Intent(context, CatadoresFavoritosActivity::class.java)
                            context.startActivity(intent)
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

fun blurEffect(menuVisibility: Boolean): Dp {
    return if (menuVisibility) {
        10.dp
    } else {
        0.dp
    }
}

@Composable
fun verifyClick(getClick: Boolean): Color {
    return if (getClick)
        colorResource(id = R.color.light_green)
    else
        colorResource(id = R.color.black)
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomePagePreview() {
    HomeContent()
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun DisplayImageFromUrl(imageUrl: String, description: String, size: Dp, padding: Dp) {
    val painter = rememberImagePainter(imageUrl)
    Column {
        Image(
            painter = painter,
            contentDescription = description,
            modifier = Modifier
                .size(size)
                .padding(padding)
                .clip(CircleShape)
        )
    }
}

