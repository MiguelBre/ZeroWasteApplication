package br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelGerador

import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.PessoaFisica

data class User(
    var biografia: String = "",
    var email: String = "",
    var foto: String = "",
    var id: Int = 0,
    var pessoa_fisica: List<PessoaFisica>,
    var pessoa_juridica: List<NewGeradorJuridico>,
    var pontos: Int = 0,
    var senha: String = "",
    var telefone: String = ""
)