package br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelPedido

import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.Address

data class OrderGerador(

    val FilaPedidoCatador: List<FilaPedidoCatador>,
    val MateriaisPedido: List<MateriaisPedido>,
    val created_at: String,
    val endereco: Address,
    val finished_at: String?,
    val id: Int,
    val id_catador: Int,
    val id_endereco: Int,
    val id_gerador: Int,
    val id_status: Int,
    val message: String? = null

)
