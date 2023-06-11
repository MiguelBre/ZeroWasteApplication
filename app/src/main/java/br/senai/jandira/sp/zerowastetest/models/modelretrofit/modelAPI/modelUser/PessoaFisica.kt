package br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser

data class PessoaFisica(
    var id: String = "",
    var cpf: String = "",
    var nome: String = "",
    var data_nascimento: String = "",
    var id_usuario: String = ""
)
