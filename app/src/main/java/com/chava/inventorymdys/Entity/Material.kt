package com.chava.inventorymdys.Entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "materials")
data class Material(
    var empresa: String = "" ,
    var num_inventario: String = "" ,
    var descripcion_material: String = "" ,
    var marca: String = "" ,
    var modelo: String = "" ,
    var serie: String = "" ,
    var num_maquina: String = "" ,
    var planta: String = "" ,
    var ubicacion_planta: String = "" ,
    var division: String = "" ,
    var linea: String = "" ,
    var area: String = "" ,
    var id_user: String = "" ,
    var comentarios: String = "" ,
    var extra1: String = "" ,
    var extra2: String = "" ,
    var extra3: String = "" ,
    var extra4: String = "" ,
    var extra5: String = "" ,
    var fotosJson: String ="" ,
    var status: Int = 0,
    ){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var num_activo: String = ""
    var pedimento: String = ""
    var factura_uuid: String = ""
    @Ignore var id_material: String = ""
    @Ignore var fotos: List<ImagesInfo> = listOf(ImagesInfo("","","",false))
    @Ignore var edit: Boolean = false
    @Ignore var imgs: ImagenEdit? = null
}