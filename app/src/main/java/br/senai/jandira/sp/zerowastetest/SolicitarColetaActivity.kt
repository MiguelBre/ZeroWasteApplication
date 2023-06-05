package br.senai.jandira.sp.zerowastetest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import retrofit2.Callback
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import br.senai.jandira.sp.zerowastetest.api.ApiCalls
import br.senai.jandira.sp.zerowastetest.api.LogisticCalls
import br.senai.jandira.sp.zerowastetest.api.RetrofitApi
import br.senai.jandira.sp.zerowastetest.dataSaving.SessionManager
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.Address
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelMaterial.Materials
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelPedido.*
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserAddress
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserData
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import com.google.gson.Gson
import io.socket.client.Socket
import retrofit2.Call
import retrofit2.Response
import retrofit2.create

class SolicitarColetaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val sessionManager = SessionManager(this)
            val authToken = "Bearer " + sessionManager.fetchAuthToken()
            val cleanToken = sessionManager.fetchAuthToken()
//            val idUsuario = sessionManager.getUserId()
            Log.i("Token", authToken)

            val socketHandler = SocketHandler()
            socketHandler.setSocket(cleanToken)
            val mSocket = socketHandler.getSocket()
            socketHandler.establishConnection()

            var order by remember {
                mutableStateOf(OrderGerador(
                    endereco = Address(
                        apelido = "",
                        bairro = "",
                        cep = "",
                        cidade = "",
                        complemento = "",
                        estado = "",
                        id = 0,
                        latitude = 0.0,
                        logradouro = "",
                        longitude = 0.0,
                        numero = ""
                    ),
                    id = 0,
                    id_gerador = 0,
                    id_endereco = 0,
                    created_at = "",
                    finished_at = "",
                    id_catador = 0,
                    id_status = 0,
                    FilaPedidoCatador = listOf(
                        FilaPedidoCatador(
                        distancia = 0
                    )
                    ) ,
                    MateriaisPedido = listOf(
                        MateriaisPedido(
                        material = Materials(
                            nome = ""
                        )
                    )
                    )
                ))
            }

            var lockOrder = false

            val logisticApi = RetrofitApi.getLogisticApi()
            val orderApi = logisticApi.create(LogisticCalls::class.java)

            orderApi.getPedido(authToken)
                .enqueue(object : Callback<List<OrderGerador>> {
                    override fun onResponse(
                        call: Call<List<OrderGerador>>,
                        response: Response<List<OrderGerador>>
                    ) {

                        if (!response.isSuccessful) {
                            lockOrder = false
                        } else{
                            lockOrder = true
                            Log.i("pedido", response.body()!!.toString())
                            order = response.body()!![0]
                        }


                    }

                    override fun onFailure(call: Call<List<OrderGerador>>, t: Throwable) {
                        Log.i(
                            "fail",
                            t.message.toString()
                        )
                    }

                })

            var userData by remember {
                mutableStateOf(UserData())
            }

            var idUsuario by remember {
                mutableStateOf(0)
            }

            var userTypeID by remember {
                mutableStateOf(0)
            }



            val api = RetrofitApi.getMainApi()
            val mainApi = api.create(ApiCalls::class.java)

            var list1: List<UserAddress>
            var list2: MaterialMessage

            mainApi.getUserData(authToken).enqueue(object : Callback<UserData>{

                override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                    if (response.isSuccessful){

                        userData = response.body()!!

                        idUsuario = userData.id

                        userTypeID = if (userData.catador!!.isEmpty()){
                            userData.gerador!![0].id
                        } else {
                            userData.catador!![0].id
                        }

                    } else {

                        Log.e("response.fail", response.toString())

                    }
                }

                override fun onFailure(call: Call<UserData>, t: Throwable) {
                    Log.e("response.fail", t.message.toString())
                }

            })

            mainApi.getEnderecoUsuario(
                authToken,
                idUsuario
            )
                .enqueue(object : Callback<List<UserAddress>> {
                    override fun onResponse(
                        call: Call<List<UserAddress>>,
                        response: Response<List<UserAddress>>
                    ) {
                        Log.i("testing", idUsuario.toString())
                        Log.i("endereco", response.toString())
                        if (response.isSuccessful) {
                            list1 = response.body()!!
                            Log.i("deu certo", "Ok")

                            mainApi.getMateriaisList().enqueue(object : Callback<MaterialMessage> {
                                override fun onResponse(call: Call<MaterialMessage>, response: Response<MaterialMessage>) {
                                    if (response.isSuccessful) {
                                        list2 = response.body()!!

                                        setContent {
                                            ZeroWasteTestTheme {

                                                Surface(
                                                    modifier = Modifier.fillMaxSize(),
                                                    color = Color(108, 162, 76),

                                                    ) {

                                                    SolicitarColetaContent(lockOrder, order, list2, list1, mSocket, userTypeID, idUsuario)
                                                }
                                            }
                                        }
                                    }

                                }

                                override fun onFailure(call: Call<MaterialMessage>, t: Throwable) {
                                    Log.e("error_that", t.message.toString())
                                }
                            })



                            Log.e("res", list1.toString())
                        }
                    }

                    override fun onFailure(call: Call<List<UserAddress>>, t: Throwable) {
                        Log.e("error_this", t.message.toString())
                    }

                })


        }
    }
}

@Composable
fun SolicitarColetaContent(lockedOrder: Boolean, orders: OrderGerador, materiais: MaterialMessage, enderecos: List<UserAddress>, mSocket: Socket, idTipo: Int, idUsuario: Int) {
    Log.i("orders", orders.toString())

    var lockOrder by remember {
        mutableStateOf(lockedOrder)
    }

    var order by remember {
        mutableStateOf(orders)
    }

    val retrofitApi = RetrofitApi.getLogisticApi()
    val orderApi = retrofitApi.create(LogisticCalls::class.java)
    val context = LocalContext.current
    val authToken = "Bearer " + SessionManager(context).fetchAuthToken()
    val userID = idUsuario

    var isDialogShown by remember {
        mutableStateOf(false)
    }

    mSocket.on("newOrder") { pedido ->
        Log.i("sockett", pedido[0].toString())
        val json = pedido[0].toString()
        val orderPedido = Gson().fromJson(json, PedidoResponse::class.java)

        order = OrderGerador(
            endereco = orderPedido.endereco,
            id = orderPedido.id,
            id_gerador = orderPedido.id_gerador,
            id_endereco = orderPedido.endereco.id,
            created_at = orderPedido.created_at,
            finished_at = "",
            id_catador = orderPedido.id_catador,
            id_status = orderPedido.id_status,
            MateriaisPedido = orderPedido.id_material,
            FilaPedidoCatador = listOf(
                FilaPedidoCatador(
                    distancia = orderPedido.distancia ?: 0
                )
            ),
        )
        lockOrder = true
    }

    mSocket.on("acceptOrder") { order ->
        Log.i("socket", order.toString())
    }

    mSocket.on("orderError") { order ->
        lockOrder = false
        Log.i("socket", order.toString())
    }

    mSocket.on("finishOrder") { order ->
        lockOrder = false
        Log.i("socket", order.toString())
    }


    var expanded1 by remember {
        mutableStateOf(false)
    }

    var expanded2 by remember {
        mutableStateOf(false)
    }

    val list2 by remember {
        mutableStateOf(materiais)
    }

    val list1 by remember {
        mutableStateOf(enderecos)
    }



    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }

    var selectedLocal by remember {
        mutableStateOf("")
    }

    var selectedOptions by remember { mutableStateOf(setOf<String>()) }

    var selectedMaterialsId by remember {
        mutableStateOf(setOf<Int>())
    }

    var selectLocalId by remember {
        mutableStateOf(0)
    }

    val icon1 = if (expanded1) {
        Icons.Filled.KeyboardArrowUp

    } else {
        Icons.Filled.KeyboardArrowDown
    }

    var message by remember {
        mutableStateOf("")
    }

    if (isDialogShown) {
        CustomDialog(onDismiss = { isDialogShown = false }, message = message)
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        //.background(color = Color(120, 160, 87 ))

    ) {

        Text(
            text = stringResource(id = R.string.solicite_coleta),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        if (!lockOrder) {
            Text(
                text = stringResource(id = R.string.formulario),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp, bottom = 1.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Text(
                text = stringResource(id = R.string.primeiro),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 1.dp),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Column(modifier = Modifier.padding(3.dp)) {

                OutlinedTextField(
                    value = selectedLocal,
                    onValueChange = {
                        Log.i("teste", selectLocalId.toString())
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
                    label = { Text(text = "Selecione o local") },

                    trailingIcon = {
                        Icon(icon1, "", Modifier.clickable { expanded1 = !expanded1 })
                    }
                )

                DropdownMenu(
                    expanded = expanded1,
                    onDismissRequest = { expanded1 = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),

                    ) {
                    list1.map { label ->
                        DropdownMenuItem(onClick = {
                            selectedLocal = label.endereco!!.apelido!!
                            selectLocalId = label.id_endereco
                            expanded1 = false
                            Log.i("teste", selectLocalId.toString().toInt().toString())
                        }) {
                            label.endereco!!.apelido?.let { Text(text = it) }
                        }
                    }
                }

            }




            Text(
                text = stringResource(id = R.string.segundo),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 1.dp),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Spacer(modifier = Modifier.padding(7.dp))

            Column {

                OutlinedTextField(
                    value = selectedOptions.joinToString(),
                    onValueChange = { /* do nothing */ },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(
                            188,
                            219,
                            183
                        )
                    ),
                    label = { Text(text = "Selecione os materias") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    trailingIcon = {
                        IconButton(onClick = { expanded2 = true }) {
                            Icon(Icons.Filled.ArrowDropDown, "Expandir")
                        }
                    }
                )

                DropdownMenu(
                    expanded = expanded2,
                    onDismissRequest = { expanded2 = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                ) {
                    list2.message.map { option ->
                        val selected = selectedOptions.contains(option.nome)
                        DropdownMenuItem(onClick = {
                            selectedOptions = if (selected) {
                                selectedOptions - option.nome!!
                            } else {
                                selectedOptions + option.nome!!
                            }

                            selectedMaterialsId = if (selected) {
                                selectedMaterialsId - option.id
                            } else {
                                selectedMaterialsId + option.id
                            }
                            Log.i("id_material", selectedMaterialsId.toString())
                        }) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                option.nome?.let { Text(it) }
                                if (selected) {
                                    Icon(Icons.Filled.Check, "Selecionado")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(10.dp))


            }

            Button(
                onClick = {
                    val pedidos = PedidoCriado(
                        id_endereco = selectLocalId,
                        id_gerador = idTipo,
                        id_materiais = selectedMaterialsId.toString()
                            .substring(1, selectedMaterialsId.toString().length - 1).split(", ")
                            .map { it.toInt() },
                        status = 1
                    )

                    Log.i("pedidos", pedidos.toString())
                    Log.i("pedidos", idTipo.toString())

                    orderApi.storeOrder(
                        authToken,
                        pedidos
                    ).enqueue(object : Callback<PedidoResponse> {
                        override fun onResponse(
                            call: Call<PedidoResponse>,
                            response: Response<PedidoResponse>
                        ) {
                            lockOrder = true
                            Log.i("teste", response.toString())
                            if (!response.isSuccessful) {
                                message = "Não foi possível criar a fila"
                                isDialogShown = true
                                lockOrder = false
                            } else{
                                order = OrderGerador(
                                    FilaPedidoCatador = listOf( FilaPedidoCatador(
                                        distancia = response.body()!!.distancia!!
                                    )),
                                    endereco = response.body()!!.endereco,
                                    id = response.body()!!.id,
                                    id_gerador = response.body()!!.id_gerador,
                                    id_endereco = response.body()!!.endereco.id,
                                    created_at = response.body()!!.created_at,
                                    finished_at = "",
                                    id_catador = response.body()!!.id_catador,
                                    id_status = response.body()!!.id_status,
                                    MateriaisPedido = response.body()!!.id_material,
                                )
                            }
                        }

                        override fun onFailure(call: Call<PedidoResponse>, t: Throwable) {
                            isDialogShown = true
                            message = "Algo deu errado"

                            Log.i(
                                "fail",
                                t.message.toString()
                            )
                        }

                    })

                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp)
                    .padding(top = 7.dp, start = 40.dp, end = 40.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(107, 177, 115))
            ) {
                Text(
                    text = "SOLICITAR",
                    color = Color.White,
                    //fontsize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.padding(20.dp))


            Image(
                painter = painterResource(id = R.drawable.foto),
                contentDescription = "",
                modifier = Modifier
                    .width(320.dp)
                    .height(310.dp),
                alignment = Alignment.Center
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
            {

                Card(
                    modifier = Modifier
                        .width(500.dp)
                        .height(560.dp)
                        .padding(start = 15.dp, end = 15.dp),
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = Color(255, 255, 255)
                )
                {

                    Column(horizontalAlignment = Alignment.CenterHorizontally)
                    {
                        Text(
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 14.dp),
                            text = "Você posssui uma coleta em andamento",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            modifier = Modifier.padding(top = 7.dp),
                            textAlign = TextAlign.Center,
                            text = "Informações sobre a coleta",
                            fontSize = 17.sp,
                            color = Color(117, 117, 117)
                        )


                        Column(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .align(Alignment.Start)
                                .padding(16.dp)
                        ) {
                            Text(
                                modifier = Modifier,
                                textAlign = TextAlign.Start,
                                text = "Endereço da coleta:",
                                fontSize = 18.sp,
                                color = Color.Black
                            )



                            Text(
                                modifier = Modifier,
                                textAlign = TextAlign.Start,
                                text = "${order.endereco.logradouro } ${order.endereco.numero}, ${order.endereco.cidade}, ${order.endereco.estado}",
                                fontSize = 17.sp,
                                color = Color(117, 117, 117)
                            )

                            Text(
                                modifier = Modifier.padding(top = 8.dp),
                                textAlign = TextAlign.Start,
                                text = "Materiais que serão recolhidos:",
                                fontSize = 18.sp,
                                color = Color.Black
                            )



                            Text(
                                modifier = Modifier,
                                textAlign = TextAlign.Start,
                                text = "${order.MateriaisPedido.map {
                                    it.material.nome
                                }}",
                                fontSize = 17.sp,
                                color = Color(117, 117, 117)
                            )


                        }

                        Image(
                            painter = painterResource(R.drawable.foto_recycle),
                            contentDescription = null,
                            modifier = Modifier
                                .size(180.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        Button(
                            onClick = {

                                orderApi.cancelOrder(
                                    authToken,
                                    order.id
                                ).enqueue(object : Callback<Void> {
                                    override fun onResponse(
                                        call: Call<Void>,
                                        response: Response<Void>
                                    ) {

                                        if (response.code() != 204 ) {
                                            message = "Não foi possível cancelar pedido"
                                            isDialogShown = true
                                        } else{
                                            lockOrder = false
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        isDialogShown = true
                                        message = "Algo deu errado"

                                        Log.i(
                                            "fail",
                                            t.message.toString()
                                        )
                                    }

                                })

                            }, modifier = Modifier
                                .fillMaxWidth()
                                .height(53.dp)
                                .padding(top = 7.dp, start = 40.dp, end = 40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(107, 177, 115))
                        ) {
                            Text(
                                text = "Cancelar pedido",
                                color = Color.White,
                                //fontsize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }




                }

            }
        }
    }
}

