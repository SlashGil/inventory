package com.chava.inventorymdys.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "inventories")
data class Inventario(
    @SerializedName("num_inventario")
    var inventory: String = "" ,
    @SerializedName("fecha_inventario")
    var date: String = "",
    @SerializedName("id_cliente")
    var id_c: String = "") {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 1

    override fun toString() : String{
        var String =  inventory +" "+ date
        return String
    }

}
