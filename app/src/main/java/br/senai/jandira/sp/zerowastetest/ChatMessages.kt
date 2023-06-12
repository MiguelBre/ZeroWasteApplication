package br.senai.jandira.sp.zerowastetest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.jandira.sp.zerowastetest.api.ApiCalls
import br.senai.jandira.sp.zerowastetest.api.ChatCalls
import br.senai.jandira.sp.zerowastetest.api.RetrofitApi
import br.senai.jandira.sp.zerowastetest.dataSaving.SessionManager
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelMessage.Message
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserData
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelMessage.MessageRecieve
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelMessage.MessageSend

import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import com.google.gson.Gson
import io.socket.client.Socket
import retrofit2.Call
import retrofit2.Response

class ChatMessages : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val api = RetrofitApi.getMainApi()
        val mainApi = api.create(ApiCalls::class.java)

        val sessionManager = SessionManager(this)
        val cleanToken = sessionManager.fetchAuthToken()
        val authToken = "Bearer $cleanToken"

        val id = intent.getSerializableExtra("id") as? String
//        val nome = intent.getSerializableExtra("nome") as String


        val chatHandler = ChatHandler()
        chatHandler.setSocketChat(cleanToken)
        chatHandler.establishConnectionChat()
        val mChat = chatHandler.getSocketChat()

        val apiChat = RetrofitApi.getChatApi()
        val chatApi = apiChat.create(ChatCalls::class.java)
        var message = listOf(Message())

        mainApi.getUserData(authToken)
            .enqueue(object : retrofit2.Callback<UserData> {
                override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                    if (response.isSuccessful){
                        if (id != null) {
                            chatApi.getMessages(id.toInt(), authToken)
                                .enqueue(object : retrofit2.Callback<List<Message>> {
                                    override fun onResponse(call: Call<List<Message>>, responseChat: Response<List<Message>>) {
                                        if (responseChat.isSuccessful){
                                            message = responseChat.body()!!
                                            var mensagens = listOf(Messages())

                                            message.map {
                                                mensagens += Messages(text = it.message.text, isSentByMe = it.from != id.toInt())
                                            }

                                            setContent {
                                                ZeroWasteTestTheme {
                                                    // A surface container using the 'background' color from the theme
                                                    Surface(
                                                        modifier = Modifier .fillMaxSize(),
                                                        color = MaterialTheme.colors.background
                                                    ) {
                                                        MessageScreen(authToken, mChat, mensagens, response.body()!!.id, cleanToken)
                                                    }
                                                }
                                            }
                                        } else {

                                            Log.i("not_found_messages", responseChat.toString())
                                            chatApi.sendMessage(authToken, MessageSend(to = id.toInt(), message = "Olá"))
                                                .enqueue(object : retrofit2.Callback<Message> {
                                                    override fun onResponse(call: Call<Message>, responseSend: Response<Message>) {
//                                                        if (response.isSuccessful) {
//                                                            messages += Messages(
//                                                                text = response.body()!!.message.text,
//                                                                isSentByMe = true
//                                                            )
//                                                        }

                                                        if (responseSend.isSuccessful) {

                                                            chatApi.getMessages(id.toInt(), authToken)
                                                                .enqueue(object : retrofit2.Callback<List<Message>> {
                                                                    override fun onResponse(call: Call<List<Message>>, responseChat: Response<List<Message>>) {
                                                                        if (responseChat.isSuccessful){
                                                                            message = responseChat.body()!!
                                                                            var mensagens = listOf(Messages())

                                                                            message.map {
                                                                                mensagens += Messages(text = it.message.text, isSentByMe = it.from != id.toInt())
                                                                            }

                                                                            setContent {
                                                                                ZeroWasteTestTheme {
                                                                                    // A surface container using the 'background' color from the theme
                                                                                    Surface(
                                                                                        modifier = Modifier .fillMaxSize(),
                                                                                        color = MaterialTheme.colors.background
                                                                                    ) {
                                                                                        MessageScreen(authToken, mChat, mensagens, response.body()!!.id, cleanToken)
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                    override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                                                                        Log.i("fail", t.message.toString())
                                                                    }

                                                                })

                                                        }

                                                    }

                                                    override fun onFailure(call: Call<Message>, t: Throwable) {
                                                        Log.i("fail", t.message.toString())
                                                    }

                                                })


                                        }
                                    }

                                    override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                                        Log.i("fail", t.message.toString())
                                    }

                                })
                        }

                    }
                }

                override fun onFailure(call: Call<UserData>, t: Throwable) {
                    Log.i("fail", t.message.toString())
                }

            })




    }
}



@Composable
fun MessageScreen(token: String, mChat: Socket, message: List<Messages>, myId: Int, cleanToken: String) {
    var messages by remember {
        mutableStateOf(message)
    }

    val context = LocalContext.current
    val intent = (context as ChatMessages).intent

    val apiChat = RetrofitApi.getChatApi()
    val chatApi = apiChat.create(ChatCalls::class.java)
    val chatId = intent.getStringExtra("id")!!.toInt()

    mChat.on("msg-recieve") {message ->

        messages += Messages(text = message[0].toString(), isSentByMe = false)
    }


    Surface(color = Color.White) {
        Column() {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(
                    colorResource(
                        id = R.color.dark_green
                    )
                ).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ){

                Image(painter = painterResource(id = R.drawable.back_arrow),
                    contentDescription = "Voltar para página inicial",
                    modifier = Modifier
                        .size(26.dp)
                        .clickable {
                            val intent = Intent(context, Chat::class.java)
                            context.startActivity(intent)
                        }
                )

                Text(text = "Nome do conversador", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 20.dp))

            }
            Box(modifier = Modifier.weight(1f).padding(16.dp)) {
                MessageList(messages = messages)
            }
            Spacer(modifier = Modifier.height(16.dp))
            MessageInput { message ->
                val mensagem = MessageRecieve(to = chatId, msg = message, from = cleanToken)
                val objectMessage = Gson().toJson(mensagem)
                mChat.emit("send-msg", objectMessage)
                chatApi.sendMessage(token, MessageSend(to = chatId, message = message))
                    .enqueue(object : retrofit2.Callback<Message> {
                        override fun onResponse(call: Call<Message>, response: Response<Message>) {
                            if (response.isSuccessful) {
                                messages += Messages(
                                    text = response.body()!!.message.text,
                                    isSentByMe = true
                                )
                            }
                        }

                        override fun onFailure(call: Call<Message>, t: Throwable) {
                            Log.i("fail", t.message.toString())
                        }

                    })
            }
        }
    }
}


@Composable
fun MessageList(messages: List<Messages>) {
    val listState = rememberLazyListState()
    LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
        items(messages){ message ->
            if (message.text != ""){
                MessageItem(message)
                Spacer(modifier = Modifier.height(8.dp))
            }

        }
    }

    LaunchedEffect(messages.size) {
        listState.scrollToItem(messages.size - 1)
    }
}

@Composable
fun MessageItem(message: Messages) {
    val backgroundColor = if (message.isSentByMe) {
        colorResource(R.color.light_green)
    } else {
        Color.LightGray
    }

    val messageAlignment = if (message.isSentByMe) {
        Alignment.CenterEnd
    } else {
        Alignment.CenterStart
    }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = messageAlignment
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = backgroundColor,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.body1,
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun MessageInput(onMessageSent: (String) -> Unit) {
    var textState by remember { mutableStateOf(TextFieldValue()) }
    val focusManager = LocalFocusManager.current


    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = textState,
            onValueChange = { textState = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text(text = "Mensagem") },
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(
                    id = R.color.light_green
                ),
                cursorColor = colorResource(
                    id = R.color.dark_green
                ),
                backgroundColor = colorResource(
                    id = R.color.almost_white
                ),
                unfocusedBorderColor = colorResource(
                    id = R.color.dark_green
                )
            )
        )
        Button(
            onClick = {
                val message = textState.text
                if (message.isNotEmpty()) {
                    onMessageSent(message)
                    textState = TextFieldValue()
                }
                focusManager.clearFocus()
            },
            modifier = Modifier.padding(start = 8.dp),
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.dark_green))
        ) {
            Text(text = "Enviar")
        }
    }
}

data class Messages(val text: String = "", val isSentByMe: Boolean = false)