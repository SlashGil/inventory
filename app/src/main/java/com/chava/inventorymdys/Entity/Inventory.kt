package com.chava.inventorymdys.Entity

import com.google.gson.annotations.SerializedName

class Inventory {
    @SerializedName("id_cliente")
    var id_c: String = ""
    @SerializedName("num_inventario")
    var inventory: String = ""
    @SerializedName("fecha_inventario")
    var date: String = ""
    @SerializedName("nombre_cliente")
    var client: String = ""

    override fun toString(): String {
        return id_c + " " + inventory + " " + date + " " + client
    }
}