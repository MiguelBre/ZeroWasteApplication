package br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelCupons

data class Coupon(
    val id: Int,
    val nome: String,
    val descricao: String,
    val criterios: String,
    val pontos: Int,
    val codigo: String,
)
