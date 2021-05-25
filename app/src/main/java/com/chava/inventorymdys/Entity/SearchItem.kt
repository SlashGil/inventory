package com.chava.inventorymdys.Entity

import com.google.gson.annotations.SerializedName

class SearchItem
{
    @SerializedName("num_caso")
    var num_caso: String? = null
    @SerializedName("empresa")
    var empresa: String? = null
    @SerializedName("marca")
    var marca: String? = null
    @SerializedName("objeto")
    val objeto: Material? = null
    @SerializedName("mensaje")
    var msj:String = ""
}