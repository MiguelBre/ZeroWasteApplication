package br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser

import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.CatadorFavorito

data class Favoritos(
    val catador: CatadorFavorito,
    val id: Int,
    val id_catador: Int,
    val id_gerador: Int
)