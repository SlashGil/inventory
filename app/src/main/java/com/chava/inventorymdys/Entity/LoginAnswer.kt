package com.chava.inventorymdys.Entity

import com.google.gson.annotations.SerializedName

class LoginAnswer {
    @SerializedName("status")
    val status: String? = null
    @SerializedName("status_message")
    val status_message:String? = null
    @SerializedName("data")
    val id: Int = 0.toInt()
}