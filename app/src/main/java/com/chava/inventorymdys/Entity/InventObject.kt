package com.chava.inventorymdys.Entity

class InventObject(ni: String, desc: String, marca: String, modelo: String, serie: String, ubicacion: String, pedimento: String, factura: String,
                   num_a: String, linea: String, area: String, id: Int, coment: String, extras: String, array: Array<Imagen>) {
    var numInventario : String = ni
    var descripcion_Mat: String = desc
    var marca: String = marca
    var modelo: String = modelo
    var serie: String = serie
    var ubicacion: String = ubicacion
    var pedimento: String = pedimento
    var factura_uuid: String = factura
    var num_activo: String = num_a
    var linea: String = linea
    var area: String = area
    var id_user: Int = id
    var comentarios: String = coment
    var extras: String = extras
    var fotos: Array<Imagen> = array
}