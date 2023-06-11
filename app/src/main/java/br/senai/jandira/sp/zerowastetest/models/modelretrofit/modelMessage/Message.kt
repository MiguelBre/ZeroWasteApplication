package br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelMessage

data class Message(
    val _id: String = "",
    val from: Int = 0,
    val to: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = "",
    val __v: Int = 0,
    val message: Text = Text("")
)
