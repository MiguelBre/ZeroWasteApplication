package br.senai.jandira.sp.zerowastetest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.jandira.sp.zerowastetest.api.ApiCalls
import br.senai.jandira.sp.zerowastetest.api.RetrofitApi
import br.senai.jandira.sp.zerowastetest.dataSaving.SessionManager
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.*
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import okhttp3.internal.wait
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditMyProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZeroWasteTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    ProfileContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileContent() {

    val context = LocalContext.current

    val storage = Firebase.storage("gs://teste---zerowaste.appspot.com")
    val storageRef =
        storage.reference.child("images/") // Adicionar UID (que vem da autenticação) do ususário

    val retrofit = RetrofitApi.getMainApi()
    val apiCalls = retrofit.create(ApiCalls::class.java)

    val sessionManager = SessionManager(context)
    val authToken = "Bearer " + sessionManager.fetchAuthToken()

    var newProfilePicture by remember {
        mutableStateOf<Uri?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            newProfilePicture = uri
        }
    )

    var dadosUsuario by remember {
        mutableStateOf(UserData())
    }
    var username by remember {
        mutableStateOf("...")
    }
    var userType by remember {
        mutableStateOf("...")
    }
    var enderecoUsuario by remember {
        mutableStateOf("")
    }

    var materiaisCatador by remember {
        mutableStateOf(listOf<MateriaisCatador>())
    }

    var profilePicture by remember {
        mutableStateOf("")
    }

    var cpfState by remember {
        mutableStateOf("")
    }

    apiCalls.getUserData(authToken).enqueue(object : Callback<UserData> {

        override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
            dadosUsuario = response.body()!!
            username =
                if (dadosUsuario.pessoa_fisica!!.isEmpty()) dadosUsuario.pessoa_juridica!![0].nome_fantasia
                else dadosUsuario.pessoa_fisica!![0].nome

            userType = if (dadosUsuario.catador!!.isEmpty()) "Gerador"
            else "Catador"

            enderecoUsuario = dadosUsuario.endereco_usuario!![0].endereco!!.cep

            if (userType == "Catador")
                materiaisCatador = dadosUsuario.catador!!.get(0).materiais_catador!!

            cpfState = if (dadosUsuario.pessoa_fisica!!.isEmpty()) {
                dadosUsuario.pessoa_juridica!![0].cnpj
            } else {
                dadosUsuario.pessoa_fisica!![0].cpf
            }

            profilePicture = dadosUsuario.foto

        }

        override fun onFailure(call: Call<UserData>, t: Throwable) {
            Log.i("fail", t.message.toString())
        }

    })

    var materialsList = mutableListOf<Materials>()

    apiCalls.getMateriaisList().enqueue(object : Callback<MaterialsList> {
        override fun onResponse(call: Call<MaterialsList>, response: Response<MaterialsList>) {

            for (i in response.body()!!.materials!!) {

//                materialsList.add()

            }

        }

        override fun onFailure(call: Call<MaterialsList>, t: Throwable) {
            TODO("Not yet implemented")
        }

    })

    var emailState by remember {
        mutableStateOf("...")
    }
    emailState = dadosUsuario.email

    var attRequest by remember {
        mutableStateOf(AttRequest())
    }


    var telephoneState by remember {
        mutableStateOf("")
    }
    telephoneState = dadosUsuario.telefone

    var biographyState by remember {
        mutableStateOf("...")
    }
    biographyState = if (dadosUsuario.biografia != null) dadosUsuario.biografia
    else "Inserir Biografia"


    var confirmationVisibility by remember {
        mutableStateOf(false)
    }

    var passwordState by remember {
        mutableStateOf("")
    }

    var passwordVisibility by remember {
        mutableStateOf(false)
    }

    val icon =
        if (passwordVisibility)
            painterResource(id = R.drawable.visibility_icon_on)
        else
            painterResource(id = R.drawable.visibility_icon_off)

    var showList by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .blur(blurEffect(confirmationVisibility))
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Voltar para página inicial",
                modifier = Modifier
                    .size(26.dp)
                    .clickable {
                        val intent = Intent(context, MyProfileActivity::class.java)
                        context.startActivity(intent)
                    })
            Card(
                modifier = Modifier.padding(start = 64.dp, end = 80.dp), border = BorderStroke(
                    2.dp, color = colorResource(
                        id = R.color.lighter_gray
                    )
                ), shape = RoundedCornerShape(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_standard_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = stringResource(id = R.string.profile_text),
                        modifier = Modifier.padding(start = 4.dp, end = 6.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.my_account),
            modifier = Modifier.padding(start = 16.dp),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            backgroundColor = colorResource(id = R.color.dark_green),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (newProfilePicture != null){

                    DisplayImageFromUrl(
                        imageUrl = newProfilePicture.toString(),
                        "Foto de perfil",
                        size = 170.dp,
                        padding = 10.dp
                    )

                } else {

                    DisplayImageFromUrl(
                        imageUrl = profilePicture,
                        "Foto de perfil",
                        size = 170.dp,
                        padding = 10.dp
                    )

                }

                Button(
                    onClick = {

                        launcher.launch("image/*")

                    },
                    modifier = Modifier.padding(bottom = 12.dp),
                    shape = (RoundedCornerShape(0.dp)),
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.light_green))
                ) {
                    Text(
                        text = stringResource(id = R.string.edit_profile_picture),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = userType,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Divider(
                    modifier = Modifier.padding(start = 10.dp, end = 26.dp, bottom = 8.dp),
                    color = Color.White,
                    thickness = 0.5f.dp
                )

                Text(
                    text = stringResource(id = R.string.username_text),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )


                TextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 32.dp)
                        .background(
                            color = colorResource(id = R.color.dark_green),
                            shape = RoundedCornerShape(0.dp)
                        ),
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "")
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        Color.White,
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.light_green
                        ),
                        trailingIconColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.user_email_text),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                TextField(
                    value = emailState,
                    onValueChange = { emailState = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 32.dp)
                        .background(
                            color = colorResource(id = R.color.dark_green),
                            shape = RoundedCornerShape(0.dp)
                        ),
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "")
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        Color.White,
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.light_green
                        ),
                        trailingIconColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (userType == "Catador") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 40.dp, top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string.materials_recycle_text),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar Material",
                            modifier = Modifier
                                .clickable {

                                    showList = true

                                }
                                .size(30.dp),
                            tint = Color.White
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 40.dp, top = 8.dp)
                    ) {

                        for (i in materiaisCatador.indices) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                materiaisCatador[i].material!!.nome?.let {
                                    Text(
                                        text = "- $it",
                                        color = Color.White
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Excluir Material",
                                    modifier = Modifier.clickable {
                                        Log.i("testando", "Catador: ${materiaisCatador[i].id_catador} / Material: ${materiaisCatador[i].material!!.id}")

                                        val deleted = apiCalls.deletarMaterial(authToken, id_catador = materiaisCatador[i].id_catador, id_material = materiaisCatador[i].material!!.id).enqueue(
                                            object : Callback<Void> {
                                                override fun onResponse(
                                                    call: Call<Void>,
                                                    response: Response<Void>
                                                ) {
                                                    if (response.code() == 204){

                                                        Toast.makeText(context, "Material Deletado", Toast.LENGTH_SHORT).show()

                                                    } else {

                                                        Log.i("Failed", "$response")

                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<Void>,
                                                    t: Throwable
                                                ) {
                                                    Log.i("Failde to contact API", t.message.toString())
                                                }

                                            })

                                    },
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(
                        modifier = Modifier.padding(start = 16.dp, end = 26.dp, bottom = 8.dp),
                        color = Color.Black,
                        thickness = 0.7f.dp
                    )
                }

                Text(
                    text = stringResource(id = R.string.user_telephone_text),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                TextField(
                    value = telephoneState,
                    onValueChange = {

                        // Remove all non-numeric characters from the input
                        val cleanInput = it.replace("[^\\d]".toRegex(), "")
                        // Format the input as a phone number
                        telephoneState = formatPhone(cleanInput)

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 32.dp)
                        .background(
                            color = colorResource(id = R.color.dark_green),
                            shape = RoundedCornerShape(0.dp)
                        ),
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "")
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        Color.White,
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.light_green
                        ),
                        trailingIconColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.user_cep_text),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                TextField(
                    value = enderecoUsuario,
                    onValueChange = { enderecoUsuario = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 32.dp)
                        .background(
                            color = colorResource(id = R.color.dark_green),
                            shape = RoundedCornerShape(0.dp)
                        ),
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "")
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        Color.White,
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.light_green
                        ),
                        trailingIconColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.user_biography),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                TextField(
                    value = biographyState,
                    onValueChange = { biographyState = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 32.dp)
                        .background(
                            color = colorResource(id = R.color.dark_green),
                            shape = RoundedCornerShape(0.dp)
                        ),
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "")
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        Color.White,
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = colorResource(
                            id = R.color.light_green
                        ),
                        cursorColor = colorResource(
                            id = R.color.light_green
                        ),
                        trailingIconColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {

                        attRequest = AttRequest(
                            nome = username,
                            email = emailState,
                            telefone = telephoneState,
                            cpf = cpfState,
                            biografia = biographyState,
                            foto = profilePicture
                        )



                        confirmationVisibility = !confirmationVisibility
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.light_green)),
                    shape = RoundedCornerShape(0.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.save_changes_text),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

            }
        }
    }
    
    AnimatedVisibility(
        visible = showList,
        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
        exit = scaleOut(animationSpec = tween(1)) + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
    ) {
        
        LazyColumn {
            item { materialsList }
        }
        
    }
    
    AnimatedVisibility(
        visible = confirmationVisibility,
        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
        exit = scaleOut(animationSpec = tween(1)) + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clickable { confirmationVisibility = !confirmationVisibility },
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.4f),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(3.dp, color = colorResource(id = R.color.light_green))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(0.9f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm_password_to_update),
                        fontSize = 20.sp
                    )

                    OutlinedTextField(
                        value = passwordState,
                        onValueChange = { passwordState = it },
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
                                    contentDescription = "Visualizar Senha",
                                    modifier = Modifier.width(35.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = colorResource(
                                id = R.color.light_green
                            ),
                            unfocusedBorderColor = colorResource(
                                id = R.color.dark_green
                            ),
                            cursorColor = colorResource(
                                id = R.color.dark_green
                            )
                        )
                    )

                    Button(
                        onClick = {

                            attRequest.senha = passwordState

                            Log.i("teste", attRequest.toString())

                            Log.i("teste", username)


                            val storageRef = Firebase.storage.reference
                            val imageRef =
                                storageRef.child("profPicture/${sessionManager.getUserId()}.jpeg")

                            if (newProfilePicture != null) {
                                val uploadTask = imageRef.putFile(newProfilePicture!!)

                                uploadTask.addOnSuccessListener { taskSnapshot ->
                                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                                        profilePicture = uri.toString()

                                        Log.i("YESSIR!", profilePicture)

                                        attRequest.foto = profilePicture

                                        Log.i("teste", attRequest.toString())

                                        apiCalls.updateUserData(authToken, attRequest)
                                            .enqueue(object : Callback<AttResponse> {
                                                override fun onResponse(
                                                    call: Call<AttResponse>,
                                                    response: Response<AttResponse>
                                                ) {

                                                    Log.i("teste", response.toString())

//                                                    Log.i("teste", attRequest.toString())

                                                    if (response.body() == null) {
                                                        Toast.makeText(
                                                            context,
                                                            "Algo deu Errado!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {

                                                        Toast.makeText(
                                                            context,
                                                            "Usuário Atualizado",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        val toMyProfile = Intent(context, MyProfileActivity::class.java)
                                                        context.startActivity(toMyProfile)

                                                    }

                                                }

                                                override fun onFailure(
                                                    call: Call<AttResponse>,
                                                    t: Throwable
                                                ) {
                                                    Log.i(
                                                        "fail_atualizarUser",
                                                        t.message.toString()
                                                    )
                                                }

                                            })

                                    }?.addOnFailureListener { exception ->
                                        Log.i("error", "$exception")
                                    }
                                }.addOnFailureListener { exception ->
                                    Log.i("error", "$exception")
                                }

                            } else {

                                apiCalls.updateUserData(authToken, attRequest)
                                    .enqueue(object : Callback<AttResponse> {
                                        override fun onResponse(
                                            call: Call<AttResponse>,
                                            response: Response<AttResponse>
                                        ) {

                                            Log.i("teste", response.toString())

//                                            Log.i("teste", attRequest.toString())

                                            if (response.body() == null) {
                                                Toast.makeText(
                                                    context,
                                                    "Algo deu Errado!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {

                                                Toast.makeText(
                                                    context,
                                                    "Usuário Atualizado",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                Log.i("teste", authToken)

                                            }

                                        }

                                        override fun onFailure(
                                            call: Call<AttResponse>,
                                            t: Throwable
                                        ) {
                                            Log.i("fail_atualizarUser", t.message.toString())
                                        }

                                    })

                            }

                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.light_green))
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview2() {
    ZeroWasteTestTheme {
        ProfileContent()
    }
}