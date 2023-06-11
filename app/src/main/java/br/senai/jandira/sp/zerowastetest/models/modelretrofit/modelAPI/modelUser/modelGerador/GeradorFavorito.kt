package br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelGerador

import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.PessoaFisica

data class GeradorFavorito(
    var gerador: GeradorX = GeradorX(id = 0, id_usuario = 0, user = User("", "", "", 0, listOf(
        PessoaFisica()
    ), listOf(NewGeradorJuridico()))),
    var id: Int =0,
    var id_catador: Int = 0,
    var id_gerador: Int = 0
)