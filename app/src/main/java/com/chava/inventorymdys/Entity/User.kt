package com.chava.inventorymdys.Entity

class User (username: String, name: String, id: String){
    var username = username
    var name = name
    var id = id
    fun logout(){
        username = ""
        name = ""
        id = ""
    }
}
