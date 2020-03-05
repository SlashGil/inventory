package com.chava.inventorymdys.Entity

class Inventario(
    number: String ,
    cliente: String ,
    inventario: String ,
    hora: String
) {
    val number = number
    val cliente = cliente
    val inventario = inventario
    val hora = hora

    override fun toString() : String{
        var String = number + " " + cliente + " "+ inventario +" "+ hora
        return String
    }

}
