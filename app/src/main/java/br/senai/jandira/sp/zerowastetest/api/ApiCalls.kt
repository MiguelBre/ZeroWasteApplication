package br.senai.jandira.sp.zerowastetest.api

import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.*
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelMaterial.MaterialCatador
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelMaterial.Materials
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelPedido.ListEnderecoUsuario
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelPedido.MaterialMessage
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.*
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.CatadoresProximos
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.NewCatadorFisico
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.NewCatadorJuridico
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador.SignResponseCatador
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelGerador.NewGeradorFisico
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelGerador.NewGeradorJuridico
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelGerador.SignResponseGerador
import retrofit2.Call
import retrofit2.http.*

interface ApiCalls {

    @POST("gerador")
    fun saveGeradorFisico(@Body newGeradorFisico: NewGeradorFisico): Call<SignResponseGerador>

    @POST("gerador")
    fun saveGeradorJuridico(@Body newGeradorJuridico: NewGeradorJuridico): Call<SignResponseGerador>

    @POST("catador")
    fun saveCatadorFisico(@Body newCatadorFisico: NewCatadorFisico): Call<SignResponseCatador>

    @POST("catador")
    fun saveCatadorJuridico(@Body newCatadorFisico: NewCatadorJuridico): Call<SignResponseCatador>

    @POST("user/auth")
    fun verifyLogin(@Body userLoginRequest: UserLoginRequest): Call<LoginResponse>

    @GET("user")
    fun getUserData(@Header("Authorization") token: String): Call<UserData>

    @GET("materiais")
    fun getMateriaisList(): Call<MaterialMessage>

    @POST("endereco")
    fun newEnderecoUser(@Header("Authorization") authToken: String, @Body address: Address): Call<UserAddress>

    @PUT("user")
    fun updateUserData(@Header("Authorization") token: String, @Body attRequest: AttRequest): Call<AttResponse>

    @DELETE("materiais/{id_catador}/{id_material}")
    fun deletarMaterial(@Header("Authorization") authToken: String, @Path("id_catador") id_catador: Int, @Path("id_material") id_material: Int): Call<Void>

    @DELETE("endereco/{id_usuario}/{id_endereco}")
    fun deletarEndereco(@Header("Authorization") authToken: String, @Path("id_usuario") id_usuario: Int, @Path("id_endereco") id_endereco: Int): Call<Message>

    @GET("endereco/{id_usuario}")
    fun getEnderecoUsuario(@Header("Authorization") authToken: String, @Path("id_usuario") id: Int): Call<List<UserAddress>>

    @GET("/gerador/{id_endereco}")
    fun getNearCollectors(@Header("Authorization") token: String, @Path("id_endereco") id_endereco: Int): Call<List<CatadoresProximos>>

    @GET("/user/{id}")
    fun getUserData(@Header("Authorization") token: String, @Path("id") id: Int): Call<UserData>

    @PATCH("/favoritar/")
    fun favoritar(@Body favorito: Favoritar, @Header("Authorization") token: String): Call<Favoritar>

    @GET("/favoritar/{id_gerador}/{id_catador}")
    fun checkFavorited(@Path("id_gerador") id_gerador: Int, @Path("id_catador") id_catador: Int): Call<List<Favoritar>>

    @GET("/materiais/not_collect/{id}")
    fun getMateriaisNotCollected(@Path("id") id: Int?): Call<List<Materials>>

    @POST("materiais/catador")
    fun storeMaterial(@Header("Authorization") token: String, @Body newMaterialCatador: MaterialCatador): Call<List<MaterialCatador>>

}