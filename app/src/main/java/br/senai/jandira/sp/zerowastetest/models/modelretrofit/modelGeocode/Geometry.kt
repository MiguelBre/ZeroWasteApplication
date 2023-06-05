package br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelGeocode

import com.google.gson.annotations.SerializedName

data class Geometry(

    @SerializedName("lat")
    var latitude: Double = 0.0,

    @SerializedName("lng")
    var longitude: Double = 0.0

)
