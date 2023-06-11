package br.senai.jandira.sp.zerowastetest.api

import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelMessage.Message
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelMessage.MessageSend
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatCalls {
    @GET("message/{id_to}")
    fun getMessages(@Path("id_to") id: Int, @Header("Authorization") token: String): Call<List<Message>>

    @POST("message")
    fun sendMessage(@Header("Authorization") token: String, @Body sendMessage: MessageSend): Call<Message>
}