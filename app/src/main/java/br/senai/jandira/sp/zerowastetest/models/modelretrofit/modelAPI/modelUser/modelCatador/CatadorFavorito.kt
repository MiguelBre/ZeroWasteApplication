package br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador

import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserData

data class CatadorFavorito(
    val id: Int,
    val id_status_catador: Int,
    val id_usuario: Int,
    val user: UserData
)