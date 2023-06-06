package br.senai.jandira.sp.zerowastetest.api

import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelCupons.Coupon
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelPedido.*
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelRating.Media
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelRating.Rating
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.Pontos
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelGeocode.Geometry
import retrofit2.Call
import retrofit2.http.*

interface LogisticCalls {
    @GET("/order")
    fun getOrder(@Header("Authorization") authToken: String): Call<PedidoReturn>

    @PUT("/order/{id}")
    fun acceptOrder(@Header("Authorization") authToken: String, @Path("id") id: Int?): Call<PedidoResponse>

    @PUT("/order/finish/{id}")
    fun finishOrder(@Header("Authorization") authToken: String, @Path("id") id: Int?, @Body latLong: Geometry): Call<FinishOrder>

    @PUT("/order/deny/{id}")
    fun denyOrder(@Header("Authorization") authToken: String, @Path("id") id: Int?): Call<Void>

    @POST("/order")
    fun storeOrder(@Header("Authorization") authToken: String, @Body order: PedidoCriado): Call<PedidoResponse>

    @DELETE("/order/cancel/{id}")
    fun cancelOrder(@Header("Authorization") authToken: String, @Path("id") id: Int): Call<Void>

    @GET("/order/gerador")
    fun getPedido(@Header("Authorization") authToken: String): Call<List<OrderGerador>>

    @GET("/rating/{id}")
    fun getAverage(@Path("id") id: Int): Call<List<Media>>

    @POST("/rating")
    fun rate(@Body body: Rating, @Header("Authorization") token: String): Call<Rating>

    @PUT("/rating")
    fun updateRate(@Body body: Rating, @Header("Authorization") token: String): Call<Rating>

    @GET("/rating/my/{id}")
    fun checkRate(@Header("Authorization") token: String, @Path("id") id: Int): Call<Rating>

    @GET("/coupon/pontos")
    fun getPontos(@Header("Authorization") token: String): Call<Pontos>

    @GET("/coupon/unreedem")
    fun getUnreedemCoupons(@Header("Authorization") token: String): Call<List<Coupon>>

    @GET("/coupon/reedem")
    fun getReedemCoupons(@Header("Authorization") token: String): Call<List<Coupon>>

    @POST("/coupon/{id}")
    fun reedemCoupon(@Header("Authorization") token: String, @Path("id") id: Int): Call<Coupon>

    @GET("/coupon/unique/{id}")
    fun getCouponById(@Path("id") id: Int): Call<Coupon>

}