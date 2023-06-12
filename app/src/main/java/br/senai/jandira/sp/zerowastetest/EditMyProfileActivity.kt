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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import br.senai.jandira.sp.zerowastetest.api.ApiCalls
import br.senai.jandira.sp.zerowastetest.api.RetrofitApi
import br.senai.jandira.sp.zerowastetest.dataSaving.SessionManager
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.*
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelMaterial.MaterialCatador
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelMaterial.Materials
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.*
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.MateriaisCatador
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
    val focusManager = LocalFocusManager.current

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

    var expanded1 by remember {
        mutableStateOf(false)
    }

    val icon1 = if (expanded1) {
        Icons.Filled.KeyboardArrowUp

    } else {
        Icons.Filled.KeyboardArrowDown
    }

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

    var enderecosUsuario by remember {
        mutableStateOf(listOf<UserAddress>())
    }

    var profilePicture by remember {
        mutableStateOf("")
    }

    var cpfState by remember {
        mutableStateOf("")
    }
    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }

    val materialsList = mutableListOf<Materials>()

    apiCalls.getUserData(authToken).enqueue(object : Callback<UserData> {

        override fun onResponse(call: Call<UserData>, response: Response<UserData>) {

            dadosUsuario = response.body()!!
            username =
                if (dadosUsuario.pessoa_fisica!!.isEmpty()) dadosUsuario.pessoa_juridica!![0].nome_fantasia
                else dadosUsuario.pessoa_fisica!![0].nome

            userType = if (dadosUsuario.catador!!.isEmpty()) "Gerador"
            else "Catador"

            enderecoUsuario = dadosUsuario.endereco_usuario!![0].endereco!!.cep

            if (userType == "Catador"){

                materiaisCatador = dadosUsuario.catador!![0].materiais_catador!!

                apiCalls.getMateriaisNotCollected(id = dadosUsuario.catador?.get(0)?.id).enqueue(object : Callback<List<Materials>> {
                    override fun onResponse(call: Call<List<Materials>>, response: Response<List<Materials>>) {

                        if(response.isSuccessful){

                            for (i in response.body()!!) {

                                materialsList.add(i)

                            }

                        } else {
                            Log.e("no_materials_can_be_added", response.toString())

                            materialsList.add(Materials(0, "Você recolhe todos materiais"))
                        }

                    }

                    override fun onFailure(call: Call<List<Materials>>, t: Throwable) {
                        Log.e("fail_getMaterials", t.message.toString())
                    }

                })

            }


            cpfState = if (dadosUsuario.pessoa_fisica!!.isEmpty()) {
                dadosUsuario.pessoa_juridica!![0].cnpj
            } else {
                dadosUsuario.pessoa_fisica!![0].cpf
            }

            profilePicture = dadosUsuario.foto

            enderecosUsuario = dadosUsuario.endereco_usuario!!

        }

        override fun onFailure(call: Call<UserData>, t: Throwable) {
            Log.i("fail", t.message.toString())
        }

    })


//    Log.i("teste", dadosUsuario.catador?.get(0)?.id.toString())

//    apiCalls.getMateriaisNotCollected(id = dadosUsuario.catador?.get(0)?.id).enqueue(object : Callback<List<Materials>> {
//        override fun onResponse(call: Call<List<Materials>>, response: Response<List<Materials>>) {
//
//            for (i in response.body()!!) {
//
//                materialsList.add(i)
//
//            }
//
//        }
//
//        override fun onFailure(call: Call<List<Materials>>, t: Throwable) {
//            Log.e("fail_getMaterials", t.message.toString())
//        }
//
//    })

    var emailState by remember {
        mutableStateOf("...")
    }
    emailState = dadosUsuario.email

    var attRequest by remember {
        mutableStateOf(AttRequest())
    }



    var selectedMaterial by remember {
        mutableStateOf(Materials(id = 0, nome = ""))
    }


    var telephoneState by remember {
        mutableStateOf("")
    }
    telephoneState = dadosUsuario.telefone

    var biographyState by remember {
        mutableStateOf("...")
    }
    biographyState = dadosUsuario.biografia


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

    var showListMateriais by remember {
        mutableStateOf(false)
    }

    var adressesErr by remember {
        mutableStateOf(false)
    }

    var materialsErr by remember {
        mutableStateOf(false)
    }
    var showModal by remember { mutableStateOf(false) }
    var showDrop by remember { mutableStateOf(false) }

    if (showModal) {
        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(text = "Cadastro de materiais") },
            text = {
                Column {
                    selectedMaterial.nome?.let {
                        OutlinedTextField(
                            value = it,
                            onValueChange = {
                                Log.i("teste", selectedMaterial.toString())
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    textFieldSize = coordinates.size.toSize()
                                },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(
                                    188,
                                    219,
                                    183
                                )
                            ),
                            //                shape = RoundedCornerShape(16.dp),
                            label = { Text(text = "Selecione uma opção") },

                            trailingIcon = {
                                Icon(icon1, "", Modifier.clickable { showDrop = !showDrop })
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = showDrop,
                        onDismissRequest = { showDrop = false }
                    ) {
                        materialsList.map { label ->
                            DropdownMenuItem(onClick = { label.nome?.let {  selectedMaterial= Materials(nome = it, id = label.id) } }) {
                                label.nome?.let { Text(text = it) }
                            }
                        }
                    }
                }
            },
            
            confirmButton = {
                
                Button(onClick = {
                    val material = MaterialCatador(id_catador = dadosUsuario.catador!![0].id, id_materiais = listOf(selectedMaterial.id))
                    apiCalls.storeMaterial(authToken, material)
                        .enqueue(object : Callback<List<MaterialCatador>> {
                            override fun onResponse(
                                call: Call<List<MaterialCatador>>,
                                response: Response<List<MaterialCatador>>

                            ) {
                                showModal = false
                                if (!response.isSuccessful){
                                    Toast.makeText(context, "${response.code()}", Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<List<MaterialCatador>>,
                                t: Throwable
                            ) {
                                Log.i("fail", t.message.toString())
                            }

                        })
                    Toast.makeText(context, "Cadastrado com sucesso", Toast.LENGTH_LONG).show()
                },
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.light_green))) {
                    Text(text = "Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showModal = false },
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.dark_green))) {
                    Text(text = "Cancelar")
                }
            }
        )
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

                if (newProfilePicture != null) {

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
                                    showModal = true
                                    showListMateriais = true

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
                                    .padding(bottom = 16.dp, top = 8.dp),
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
                                        Log.i(
                                            "testando",
                                            "Catador: ${materiaisCatador[i].id_catador} / Material: ${materiaisCatador[i].material!!.id}"
                                        )

                                        apiCalls.deletarMaterial(
                                            authToken,
                                            id_catador = materiaisCatador[i].id_catador,
                                            id_material = materiaisCatador[i].material!!.id
                                        ).enqueue(
                                            object : Callback<Void> {
                                                override fun onResponse(
                                                    call: Call<Void>,
                                                    response: Response<Void>
                                                ) {
                                                    if (response.code() == 204) {

                                                        Toast.makeText(
                                                            context,
                                                            "Material Deletado",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        materiaisCatador.drop(i)

                                                    } else {

                                                        Log.i("Failed", "$response")

                                                        materialsErr = true

                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<Void>,
                                                    t: Throwable
                                                ) {
                                                    Log.i(
                                                        "Failde to contact API",
                                                        t.message.toString()
                                                    )
                                                }

                                            })

                                    },
                                    tint = Color.White
                                )
                            }
                        }
                        if (materialsErr) {

                            Text(
                                text = stringResource(id = R.string.atleast1_material),
                                color = colorResource(
                                    id = R.color.brighter_red
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                        } else {

                            Spacer(modifier = Modifier.height(8.dp))

                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                        val cleanInput = it.replace("\\D".toRegex(), "")
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 40.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.user_cep_text),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Cadastrar Endereço",
                        modifier = Modifier
                            .clickable {

                                context.startActivity(Intent(context, CadastrarEnd::class.java))

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

                    for (i in enderecosUsuario.indices) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp, top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            enderecosUsuario[i].endereco!!.apelido?.let {
                                Text(
                                    text = "- $it",
                                    color = Color.White
                                )
                            }

                            Row {

                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar Endereço",
                                    modifier = Modifier.clickable {

                                        Log.i(
                                            "testando_editar",
                                            "Id_address: ${enderecosUsuario[i].id_endereco} / Id_User: ${enderecosUsuario[i].id_usuario}"
                                        )

                                    },
                                    tint = Color.White
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Excluir Endereço",
                                    modifier = Modifier.clickable {
                                        Log.i(
                                            "testando",
                                            "Id_address: ${enderecosUsuario[i].id_endereco} / Id_User: ${enderecosUsuario[i].id_usuario}"
                                        )

                                        apiCalls.deletarEndereco(
                                            authToken,
                                            id_usuario = enderecosUsuario[i].id_usuario,
                                            id_endereco = enderecosUsuario[i].id_endereco
                                        ).enqueue(
                                            object : Callback<Message> {
                                                override fun onResponse(
                                                    call: Call<Message>,
                                                    response: Response<Message>
                                                ) {
                                                    if (response.code() == 200) {

                                                        Toast.makeText(
                                                            context,
                                                            response.body()!!.message,
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                    } else {

                                                        Log.i("Failed", "$response")
                                                        Log.i("Failed", "${response.body()}")

//                                                    Toast.makeText(context, "Deve ter ao menos 1 endereço.", Toast.LENGTH_SHORT)
//                                                        .show()
                                                        adressesErr = true


                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<Message>,
                                                    t: Throwable
                                                ) {
                                                    Log.i(
                                                        "Failde to contact API",
                                                        t.message.toString()
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        "Não foi possível conectar-se à internet. Verifique sua conexão",
                                                        Toast.LENGTH_LONG
                                                    )
                                                        .show()
                                                }

                                            })

                                    },
                                    tint = Color.White
                                )

                            }


                        }
                    }
                    if (adressesErr) {

                        Text(
                            text = stringResource(id = R.string.atleast1_address),
                            color = colorResource(
                                id = R.color.brighter_red
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                    } else {

                        Spacer(modifier = Modifier.height(8.dp))

                    }
                }

                Divider(
                    modifier = Modifier.padding(start = 16.dp, end = 26.dp, bottom = 8.dp),
                    color = Color.Black,
                    thickness = 0.7f.dp
                )

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

                                                        val toMyProfile = Intent(
                                                            context,
                                                            MyProfileActivity::class.java
                                                        )
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