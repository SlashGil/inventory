package com.chava.inventorymdys.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

class ImagesInfo {
    var D : String? = null
    var I : String? = null
    var P: String? = null
    var e: Boolean? = false
    constructor(
        D0: String? ,
        I0: String? ,
        P0: String?,
        e: Boolean?
    ) {
        this.D = D0
        this.I = I0
        this.P = P0
        this.e = e
    }
}
