package com.chava.inventorymdys.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "clients")
data class Cliente (@SerializedName("id_cliente") var id_c: String="",@SerializedName("nombre_cliente") var name: String= ""){
    @PrimaryKey(autoGenerate = true)
     var id: Int = 1
}