package com.chava.inventorymdys.Entity

import com.google.gson.annotations.SerializedName

class Search(type:String,search:String,inventory:String = "",client:String = "",msg: String = "") {
    @SerializedName("client")
    var client:String = client
    @SerializedName("type")
    var type:String  = type
    @SerializedName("search")
    var search:String = search
    @SerializedName("inventory")
    var inventory: String = inventory

}