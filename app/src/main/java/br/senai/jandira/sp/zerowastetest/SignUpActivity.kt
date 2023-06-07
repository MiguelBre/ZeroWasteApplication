package br.senai.jandira.sp.zerowastetest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.jandira.sp.zerowastetest.api.ApiCalls
import br.senai.jandira.sp.zerowastetest.api.CepCalls
import br.senai.jandira.sp.zerowastetest.api.GeoCalls
import br.senai.jandira.sp.zerowastetest.api.RetrofitApi
import br.senai.jandira.sp.zerowastetest.dataSaving.SessionManager
import br.senai.jandira.sp.zerowastetest.ime.rememberImeState
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.*
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelPedido.MaterialMessage
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.*
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.NewCatadorFisico
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.NewCatadorJuridico
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.SignResponseCatador
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelGerador.NewGeradorFisico
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelGerador.NewGeradorJuridico
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelGerador.SignResponseGerador
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelCEP.CepResponse
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelGeocode.Geometry
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelGeocode.Results
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContent {
            ZeroWasteTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ZeroWasteApplication()
                }
            }
        }
    }
}

//@OptIn(ExperimentalAnimationApi::class)
@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun ZeroWasteApplication() {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val sessionManager = SessionManager(context)

    val retrofitApi = RetrofitApi.getMainApi()
    val retrofitCep = RetrofitApi.getCepApi()
    val retrofitGeocode = RetrofitApi.getGeoCodeApi()
    val userCalls = retrofitApi.create(ApiCalls::class.java)
    val cepCalls = retrofitCep.create(CepCalls::class.java)
    val geoCalls = retrofitGeocode.create(GeoCalls::class.java)

    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.animateScrollTo(scrollState.maxValue / 4, tween(200))
        }
    }

    var addressInfo by remember {
        mutableStateOf(CepResponse("", "", "", "", "", "", "", "", "", ""))
    }

    var recyclerClick by remember {
        mutableStateOf(true)
    }

    var catcherClick by remember {
        mutableStateOf(false)
    }

    var color1 by remember {
        mutableStateOf(Color(128, 204, 40))
    }

    var color2 by remember {
        mutableStateOf(Color.Transparent)
    }

    var nameState by rememberSaveable {
        mutableStateOf("")
    }

    var fisicoClick by remember {
        mutableStateOf(true)
    }

    var juridicoClick by remember {
        mutableStateOf(false)
    }

    var newColor1 by remember {
        mutableStateOf(Color(128, 204, 40))
    }

    var newColor2 by remember {
        mutableStateOf(Color.Transparent)
    }

    var cpfState by rememberSaveable {
        mutableStateOf("")
    }

    var emailState by rememberSaveable {
        mutableStateOf("")
    }

    var phoneState by rememberSaveable {
        mutableStateOf("")
    }

    var cepState by rememberSaveable {
        mutableStateOf("")
    }

    var resNumberState by rememberSaveable {
        mutableStateOf("")
    }

    var complementState by rememberSaveable {
        mutableStateOf("")
    }

    var passwordState by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassState by rememberSaveable {
        mutableStateOf("")
    }

    var resultLatLong by remember {
        mutableStateOf(Geometry())
    }

    var userAddress by remember {
        mutableStateOf(Address())
    }

    var infoCatadorFisico by remember {
        mutableStateOf(NewCatadorFisico())
    }
    var infoCatadorJuridico by remember {
        mutableStateOf(NewCatadorJuridico())
    }
    var infoGeradorFisico by remember {
        mutableStateOf(NewGeradorFisico())
    }
    var infoGeradorJuridico by remember {
        mutableStateOf(NewGeradorJuridico())
    }

    val materialsList = mutableListOf<Int?>()

    val calendarState = rememberSheetState()

    var birthdayState by rememberSaveable {
        mutableStateOf("Ano-Mes-Dia")
    }

    var urlEncoded by remember {
        mutableStateOf("")
    }

    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    val icon = if (passwordVisibility) {
        painterResource(id = R.drawable.visibility_icon_on)
    } else {
        painterResource(id = R.drawable.visibility_icon_off)
    }

    val confirmIcon = if (confirmPasswordVisibility) {
        painterResource(id = R.drawable.visibility_icon_on)
    } else {
        painterResource(id = R.drawable.visibility_icon_off)
    }

    var confirmPassError by remember {
        mutableStateOf(false)
    }

    fun validatePass(pass: String, confirmPass: String): Boolean {
        return confirmPass != pass
    }

    var nameError by remember {
        mutableStateOf(false)
    }

    var cpfError by remember {
        mutableStateOf(false)
    }

    var emailError by remember {
        mutableStateOf(false)
    }

    var phoneError by remember {
        mutableStateOf(false)
    }

    var cepError by remember {
        mutableStateOf(false)
    }

    var resNumError by remember {
        mutableStateOf(false)
    }

    var birthDayError by remember {
        mutableStateOf(false)
    }

    var passError by remember {
        mutableStateOf(false)
    }

    var conPassError by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 7.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Voltar",
                modifier = Modifier
                    .size(50.dp)
                    .padding(start = 16.dp, top = 16.dp)
                    .clickable {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(96.dp)
                        .padding(start = 8.dp, end = 8.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Black)) {
                            append("Zero")
                        }
                        append(' ')
                        withStyle(style = SpanStyle(color = Color(8, 113, 19))) {
                            append("Waste")
                        }
                    },
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(8, 113, 19))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.background(
                        color = Color.White,
                        shape = RoundedCornerShape(5.dp)
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.recycler),
                        modifier = Modifier
                            .background(
                                color1,
                                shape = RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp)
                            )
                            .clickable {
                                recyclerClick = true
                                catcherClick = false
                                color1 = Color(128, 204, 40)
                                color2 = Color.Transparent
                            }
                            .padding(
                                top = 15.dp,
                                bottom = 15.dp
                            )
                            .width(130.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(id = R.string.catador),
                        modifier = Modifier
                            .background(
                                color2,
                                shape = RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp)
                            )
                            .clickable {
                                recyclerClick = false
                                catcherClick = true
                                color2 = Color(128, 204, 40)
                                color1 = Color.Transparent
                            }
                            .padding(
                                top = 15.dp,
                                bottom = 15.dp,
                            )
                            .width(130.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.welcome_message),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Column {
                OutlinedTextField(
                    value = nameState, onValueChange = { newValue ->
                        nameState = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp),
                    placeholder = { Text(text = stringResource(id = R.string.name_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = ""
                        )
                    },
                    isError = nameError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.dark_green
                        ),
                        backgroundColor = Color.White
                    )
                )
                if (nameError) {
                    Text(
                        text = stringResource(id = R.string.empty_error),
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 30.dp, start = 30.dp),
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                } else {
                    Spacer(modifier = Modifier.height(15.dp))
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.pessoa_fisica),
                            modifier = Modifier
                                .background(
                                    newColor1,
                                    shape = RoundedCornerShape(topStart = 10.dp)
                                )
                                .clickable {
                                    fisicoClick = true
                                    juridicoClick = false
                                    newColor1 = Color(128, 204, 40)
                                    newColor2 = Color.Transparent
                                }
                                .padding(
                                    top = 2.dp,
                                    bottom = 2.dp
                                )
                                .fillMaxWidth(0.5f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(id = R.string.pessoa_juridica),
                            modifier = Modifier
                                .background(
                                    newColor2,
                                    shape = RoundedCornerShape(topEnd = 10.dp)
                                )
                                .clickable {
                                    fisicoClick = false
                                    juridicoClick = true
                                    newColor2 = Color(128, 204, 40)
                                    newColor1 = Color.Transparent
                                }
                                .padding(
                                    top = 2.dp,
                                    bottom = 2.dp,
                                )
                                .fillMaxWidth(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }


                OutlinedTextField(
                    value = cpfState, onValueChange = {

                        val cleanInput = it.replace("\\D".toRegex(), "")
                        cpfState = cleanInput

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomStart = 10.dp,
                                bottomEnd = 10.dp
                            )
                        ),
                    placeholder = {
                        if (fisicoClick) {
                            Text(text = stringResource(id = R.string.cpf_label))
                        } else {
                            Text(text = stringResource(id = R.string.cnpj_label))
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = ""
                        )
                    },
                    isError = cpfError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 10.dp,
                        bottomEnd = 10.dp
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.dark_green
                        ),
                        backgroundColor = Color.White
                    )
                )
                if (cpfError) {
                    Text(
                        text = stringResource(id = R.string.empty_error),
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 30.dp, start = 30.dp),
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                } else {
                    Spacer(modifier = Modifier.height(15.dp))
                }
                OutlinedTextField(
                    value = emailState, onValueChange = { newValue ->
                        emailState = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                    placeholder = { Text(text = stringResource(id = R.string.email_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = ""
                        )
                    },
                    isError = emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.dark_green
                        ),
                        backgroundColor = Color.White
                    )
                )
                if (emailError) {
                    Text(
                        text = stringResource(id = R.string.empty_error),
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 30.dp, start = 30.dp),
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                } else {
                    Spacer(modifier = Modifier.height(15.dp))
                }
                OutlinedTextField(
                    value = phoneState, onValueChange = {

                        // Remove all non-numeric characters from the input
                        val cleanInput = it.replace("\\D".toRegex(), "")
                        // Format the input as a phone number
                        phoneState = formatPhone(cleanInput)

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                    placeholder = { Text(text = stringResource(id = R.string.telephone_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = ""
                        )
                    },
                    isError = phoneError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.dark_green
                        ),
                        backgroundColor = Color.White
                    )
                )
                if (phoneError) {
                    Text(
                        text = stringResource(id = R.string.phone_format_error),
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 30.dp, start = 30.dp),
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                } else {
                    Spacer(modifier = Modifier.height(15.dp))
                }
                Row {
                    Column {
                        OutlinedTextField(
                            value = cepState, onValueChange = { newValue ->
                                cepState = newValue
                            },
                            modifier = Modifier
                                .width(200.dp)
                                .padding(start = 30.dp)
                                .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                            placeholder = { Text(text = stringResource(id = R.string.cep_label)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = ""
                                )
                            },
                            isError = cepError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Right)
                                }
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = colorResource(
                                    id = R.color.light_green
                                ),
                                cursorColor = colorResource(
                                    id = R.color.dark_green
                                ),
                                backgroundColor = Color.White
                            )
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
                            Spacer(modifier = Modifier.height(7.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(7.dp))
                    Column {
                        OutlinedTextField(
                            value = resNumberState, onValueChange = { newValue ->
                                resNumberState = newValue
                            },
                            modifier = Modifier
                                .width(220.dp)
                                .padding(end = 30.dp)
                                .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                            placeholder = { Text(text = stringResource(id = R.string.residencial_label)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = ""
                                )
                            },
                            isError = resNumError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Ascii,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = colorResource(
                                    id = R.color.light_green
                                ),
                                cursorColor = colorResource(
                                    id = R.color.dark_green
                                ),
                                backgroundColor = Color.White
                            )
                        )
                        if (resNumError) {
                            Text(
                                text = stringResource(id = R.string.empty_field_reduced),
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(end = 30.dp, start = 30.dp),
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.height(7.dp))
                        }
                    }
                }
                if (!resNumError) {
                    Spacer(modifier = Modifier.height(15.dp))
                }
                OutlinedTextField(
                    value = complementState, onValueChange = { newValue ->
                        complementState = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                    placeholder = { Text(text = stringResource(id = R.string.complement_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = ""
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.dark_green
                        ),
                        backgroundColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                AnimatedVisibility(
                    visible = fisicoClick,
                    enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                    exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
                ) {
                    Column {
                        CalendarDialog(
                            state = calendarState,
                            config = CalendarConfig(
                                yearSelection = true
                            ),
                            selection = CalendarSelection.Date { birthdate ->
                                birthdayState = birthdate.toString()
                            }
                        )

                        Button(
                            onClick = {
                                calendarState.show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 30.dp, end = 30.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(
                                    128,
                                    204,
                                    40
                                )
                            ),
                            shape = RoundedCornerShape(
                                topStart = 10.dp,
                                topEnd = 10.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.select_bithday),
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = birthdayState,
                            modifier = Modifier
                                .padding(start = 30.dp, end = 30.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 0.dp,
                                        bottomStart = 10.dp,
                                        bottomEnd = 10.dp
                                    )
                                )
                                .height(30.dp)
                                .fillMaxWidth(),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        if (birthDayError) {
                            Text(
                                text = stringResource(id = R.string.birthday_error),
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 30.dp, start = 30.dp),
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.height(7.dp))

                        } else {
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }

                }


                OutlinedTextField(
                    value = passwordState,
                    onValueChange = { newValue ->
                        passwordState = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                    placeholder = { Text(text = stringResource(id = R.string.password_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = ""
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisibility = !passwordVisibility
                        }) {
                            Icon(
                                painter = icon,
                                contentDescription = "visibility icon",
                                modifier = Modifier.width(35.dp)
                            )
                        }
                    },
                    isError = passError,
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.dark_green
                        ),
                        backgroundColor = Color.White
                    )
                )
                if (passError) {
                    Text(
                        text = stringResource(id = R.string.empty_error),
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 30.dp, start = 30.dp),
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                } else {
                    Spacer(modifier = Modifier.height(15.dp))
                }
                OutlinedTextField(
                    value = confirmPassState,
                    onValueChange = { newValue ->
                        confirmPassState = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                    placeholder = { Text(text = stringResource(id = R.string.confirm_password_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (confirmPassError) {
                                Icons.Default.Error
                            } else {
                                Icons.Default.Lock
                            },
                            contentDescription = "",
                            tint = if (confirmPassError)
                                Color.Red
                            else
                                Color.Gray
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            confirmPasswordVisibility = !confirmPasswordVisibility
                        }) {
                            Icon(
                                painter = confirmIcon,
                                contentDescription = "Visibility Icon",
                                modifier = Modifier.width(35.dp)
                            )
                        }
                    },
                    isError = confirmPassError,
                    visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.dark_green
                        ),
                        backgroundColor = Color.White
                    )
                )
                if (confirmPassError) {
                    Text(
                        text = stringResource(id = R.string.different_pass),
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 30.dp, start = 30.dp),
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                AnimatedVisibility(
                    visible = recyclerClick,
                    enter = slideInHorizontally(animationSpec = tween(500)) { fullWidth -> -fullWidth } + fadeIn(
                        animationSpec = tween(durationMillis = 300)
                    ),
                    exit =
                    slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) {
                        300
                    } + fadeOut()
                ) {
                    Button(
                        onClick = {

                            confirmPassError = validatePass(
                                passwordState,
                                confirmPassState
                            )

                            nameError = nameState.isEmpty()
                            cpfError = cpfState.isEmpty()
                            emailError = emailState.isEmpty()
                            phoneError = phoneState.isEmpty() || phoneState.length < 14 || phoneState.length > 15
                            cepError = cepState.isEmpty()
                            resNumError = resNumberState.isEmpty()
                            birthDayError = birthdayState == "Ano-Mes-Dia"
                            passError = passwordState.isEmpty()
                            conPassError = confirmPassState.isEmpty()

                            if (!confirmPassError && !nameError && !cpfError && !emailError && !phoneError && !cepError && !resNumError && !birthDayError && !passError && !conPassError) {

                                cepCalls.getAddressInfo(cepState)
                                    .enqueue(object : Callback<CepResponse> {
                                        override fun onResponse(
                                            call: Call<CepResponse>,
                                            response: Response<CepResponse>
                                        ) {

                                            addressInfo = response.body()!!

                                            urlEncoded =
                                                URLEncoder.encode("${addressInfo.logradouro}, ${addressInfo.localidade}, ${addressInfo.uf}")

                                            geoCalls.getLatiLong(
                                                urlEncoded,
                                                "8c86308380ad443fac12280fd96b4ac5"
                                            ).enqueue(object : Callback<Results> {
                                                override fun onResponse(
                                                    call: Call<Results>,
                                                    response: Response<Results>
                                                ) {

                                                    resultLatLong =
                                                        response.body()!!.results!![0].geometry!!

                                                    if (addressInfo.complemento == "")
                                                        addressInfo.complemento = " "

                                                    userAddress = Address(

                                                        cep = cepState,
                                                        logradouro = addressInfo.logradouro,
                                                        bairro = addressInfo.bairro,
                                                        cidade = addressInfo.localidade,
                                                        estado = addressInfo.uf,
                                                        complemento = addressInfo.complemento,
                                                        numero = resNumberState,
                                                        latitude = resultLatLong.latitude,
                                                        longitude = resultLatLong.longitude

                                                    )

                                                    Log.i("what", userAddress.toString())



                                                    if (fisicoClick) {

                                                        infoGeradorFisico =
                                                            NewGeradorFisico(
                                                                nome = nameState,
                                                                cpf = cpfState,
                                                                email = emailState,
                                                                telefone = phoneState,
                                                                endereco = userAddress,
                                                                data_nascimento = birthdayState + "T12:01:30.543Z",
                                                                senha = passwordState
                                                            )

                                                        userCalls.saveGeradorFisico(
                                                            infoGeradorFisico
                                                        )
                                                            .enqueue(object :
                                                                Callback<SignResponseGerador> {
                                                                override fun onResponse(
                                                                    call: Call<SignResponseGerador>,
                                                                    response: Response<SignResponseGerador>
                                                                ) {

                                                                    Toast.makeText(
                                                                        context,
                                                                        "Cadastro Realizado",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()

                                                                    val intent = Intent(
                                                                        context,
                                                                        LogInActivity::class.java
                                                                    )
                                                                    context.startActivity(intent)

                                                                }

                                                                override fun onFailure(
                                                                    call: Call<SignResponseGerador>,
                                                                    t: Throwable
                                                                ) {
                                                                    Log.i(
                                                                        "fail",
                                                                        t.message.toString()
                                                                    )
                                                                }

                                                            })

                                                    } else {

                                                        infoGeradorJuridico =
                                                            NewGeradorJuridico(
                                                                nome = nameState,
                                                                cnpj = cpfState,
                                                                email = emailState,
                                                                telefone = phoneState,
                                                                endereco = userAddress,
                                                                senha = passwordState
                                                            )

                                                        userCalls.saveGeradorJuridico(
                                                            infoGeradorJuridico
                                                        )
                                                            .enqueue(object :
                                                                Callback<SignResponseGerador> {
                                                                override fun onResponse(
                                                                    call: Call<SignResponseGerador>,
                                                                    response: Response<SignResponseGerador>
                                                                ) {

                                                                    Toast.makeText(
                                                                        context,
                                                                        "Cadastro Realizado",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()

                                                                    val intent = Intent(
                                                                        context,
                                                                        LogInActivity::class.java
                                                                    )
                                                                    context.startActivity(intent)

                                                                }

                                                                override fun onFailure(
                                                                    call: Call<SignResponseGerador>,
                                                                    t: Throwable
                                                                ) {
                                                                    Log.i(
                                                                        "fail",
                                                                        t.message.toString()
                                                                    )
                                                                }

                                                            })

                                                    }
                                                    Log.i(
                                                        "Testing",
                                                        infoGeradorFisico.toString()
                                                    )
                                                    Log.i(
                                                        "Testing",
                                                        infoGeradorJuridico.toString()
                                                    )

                                                }

                                                override fun onFailure(
                                                    call: Call<Results>,
                                                    t: Throwable
                                                ) {
                                                    Log.i("fail", t.message.toString())
                                                }
                                            })
                                        }

                                        override fun onFailure(
                                            call: Call<CepResponse>,
                                            t: Throwable
                                        ) {
                                            Log.i("fail", t.message.toString())
                                        }
                                    })
                            }

                        },
                        modifier = Modifier
                            .padding(start = 30.dp, end = 30.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(128, 204, 40))
                    ) {
                        Text(
                            text = (stringResource(id = R.string.login_as) + " " + stringResource(id = R.string.recycler)),
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                AnimatedVisibility(
                    visible = catcherClick,
                    enter = slideInHorizontally(animationSpec = tween(500)) { fullWidth -> -fullWidth } + fadeIn(
                        animationSpec = tween(durationMillis = 200)
                    ),
                    exit =
                    slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) {
                        300
                    } + fadeOut()
                ) {
                    Button(
                        onClick = {

                            confirmPassError = validatePass(
                                passwordState,
                                confirmPassState
                            )
                            nameError = nameState.isEmpty()
                            cpfError = cpfState.isEmpty()
                            emailError = emailState.isEmpty()
                            phoneError = phoneState.isEmpty()
                            cepError = cepState.isEmpty()
                            resNumError = resNumberState.isEmpty()
                            birthDayError = birthdayState == "Ano-Mes-Dia"
                            passError = passwordState.isEmpty()
                            conPassError = confirmPassState.isEmpty()

                            if (!confirmPassError && !nameError && !cpfError && !emailError && !phoneError && !cepError && !resNumError && !birthDayError && !passError && !conPassError) {

                                cepCalls.getAddressInfo(cepState)
                                    .enqueue(object : Callback<CepResponse> {
                                        override fun onResponse(
                                            call: Call<CepResponse>,
                                            response: Response<CepResponse>
                                        ) {

                                            addressInfo = response.body()!!

                                            urlEncoded =
                                                URLEncoder.encode("${addressInfo.logradouro}, ${addressInfo.localidade}, ${addressInfo.uf}")

                                            geoCalls.getLatiLong(
                                                urlEncoded,
                                                "8c86308380ad443fac12280fd96b4ac5"
                                            ).enqueue(object : Callback<Results> {
                                                override fun onResponse(
                                                    call: Call<Results>,
                                                    response: Response<Results>
                                                ) {

                                                    resultLatLong =
                                                        response.body()!!.results!![0].geometry!!

                                                    if (addressInfo.complemento == "")
                                                        addressInfo.complemento = " "

                                                    userAddress = Address(

                                                        cep = cepState,
                                                        logradouro = addressInfo.logradouro,
                                                        bairro = addressInfo.bairro,
                                                        cidade = addressInfo.localidade,
                                                        estado = addressInfo.uf,
                                                        complemento = addressInfo.complemento,
                                                        numero = resNumberState,
                                                        latitude = resultLatLong.latitude,
                                                        longitude = resultLatLong.longitude

                                                    )

                                                    Log.i("what", userAddress.toString())

                                                    userCalls.getMateriaisList()
                                                        .enqueue(object : Callback<MaterialMessage> {
                                                            override fun onResponse(
                                                                call: Call<MaterialMessage>,
                                                                response: Response<MaterialMessage>
                                                            ) {

                                                                for (i in response.body()!!.message) {
                                                                    materialsList.add(i.id)
                                                                }

                                                                if (fisicoClick) {

                                                                    infoCatadorFisico =
                                                                        NewCatadorFisico(
                                                                            nome = nameState,
                                                                            cpf = cpfState,
                                                                            email = emailState,
                                                                            telefone = phoneState,
                                                                            endereco = userAddress,
                                                                            data_nascimento = birthdayState + "T12:01:30.543Z",
                                                                            senha = passwordState,
                                                                            materiais = materialsList
                                                                        )

                                                                    userCalls.saveCatadorFisico(
                                                                        infoCatadorFisico
                                                                    )
                                                                        .enqueue(object :
                                                                            Callback<SignResponseCatador> {
                                                                            override fun onResponse(
                                                                                call: Call<SignResponseCatador>,
                                                                                response: Response<SignResponseCatador>
                                                                            ) {

                                                                                Log.i(
                                                                                    "return_id",
                                                                                    response.body()!!.id.toString()
                                                                                )

                                                                                Toast.makeText(
                                                                                    context,
                                                                                    "Cadastro Realizado",
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()

                                                                                sessionManager.saveUserLogin(
                                                                                    emailState
                                                                                )

                                                                                val intent = Intent(
                                                                                    context,
                                                                                    LogInActivity::class.java
                                                                                )
                                                                                context.startActivity(
                                                                                    intent
                                                                                )

                                                                            }

                                                                            override fun onFailure(
                                                                                call: Call<SignResponseCatador>,
                                                                                t: Throwable
                                                                            ) {
                                                                                Log.i(
                                                                                    "fail",
                                                                                    t.message.toString()
                                                                                )
                                                                            }

                                                                        })

                                                                } else {

                                                                    infoCatadorJuridico =
                                                                        NewCatadorJuridico(
                                                                            nome = nameState,
                                                                            cnpj = cpfState,
                                                                            email = emailState,
                                                                            telefone = phoneState,
                                                                            endereco = userAddress,
                                                                            senha = passwordState,
                                                                            materiais = materialsList
                                                                        )

                                                                    userCalls.saveCatadorJuridico(
                                                                        infoCatadorJuridico
                                                                    )
                                                                        .enqueue(object :
                                                                            Callback<SignResponseCatador> {
                                                                            override fun onResponse(
                                                                                call: Call<SignResponseCatador>,
                                                                                response: Response<SignResponseCatador>
                                                                            ) {

                                                                                Toast.makeText(
                                                                                    context,
                                                                                    "Cadastro Realizado",
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()

                                                                                val intent = Intent(
                                                                                    context,
                                                                                    LogInActivity::class.java
                                                                                )
                                                                                context.startActivity(
                                                                                    intent
                                                                                )

                                                                            }

                                                                            override fun onFailure(
                                                                                call: Call<SignResponseCatador>,
                                                                                t: Throwable
                                                                            ) {
                                                                                Log.i(
                                                                                    "fail",
                                                                                    t.message.toString()
                                                                                )
                                                                            }

                                                                        })

                                                                }
                                                                Log.i(
                                                                    "Testing",
                                                                    infoCatadorFisico.toString()
                                                                )
                                                                Log.i(
                                                                    "Testing",
                                                                    infoCatadorJuridico.toString()
                                                                )


                                                            }

                                                            override fun onFailure(
                                                                call: Call<MaterialMessage>,
                                                                t: Throwable
                                                            ) {
                                                                TODO("Not yet implemented")
                                                            }
                                                        })

                                                }

                                                override fun onFailure(
                                                    call: Call<Results>,
                                                    t: Throwable
                                                ) {
                                                    TODO("Not yet implemented")
                                                }

                                            })

                                        }

                                        override fun onFailure(
                                            call: Call<CepResponse>,
                                            t: Throwable
                                        ) {
                                            Log.i("fail", t.message.toString())
                                        }
                                    })
                            }
                        },
                        modifier = Modifier
                            .padding(start = 30.dp, end = 30.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(128, 204, 40))
                    ) {
                        Text(
                            text = (stringResource(id = R.string.login_as) + " " + stringResource(id = R.string.catador)),
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

fun formatPhone(phoneNumber: String): String {
    val phoneRegex = "(\\d{2})(\\d{5})(\\d{4})".toRegex()
    return phoneRegex.replace(phoneNumber, "($1) $2-$3")
}

//fun formatCNPJ(cnpjNumber: String): String {
//    val phoneRegex = "(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})".toRegex()
//    return phoneRegex.replace(cnpjNumber, "$1.$2.$3/$4-$5")
//}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ZeroWasteApplicationPreview() {
    ZeroWasteApplication()
}





