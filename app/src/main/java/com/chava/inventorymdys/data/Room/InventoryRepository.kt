package com.chava.inventorymdys.data.Room

import android.app.Application
import android.os.AsyncTask
import com.chava.inventorymdys.Entity.Cliente
import com.chava.inventorymdys.Entity.ImagesInfo
import com.chava.inventorymdys.Entity.Inventario
import com.chava.inventorymdys.Entity.Material
import com.chava.inventorymdys.interfaces.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InventoryRepository(application: Application) {
    private val inventoryDao: InventoryDao? = InventoryDB.getInstance(application)?.inventoryDao
    val url = "http://marketi.servehttp.com:80/EO-Plataform/Eo-service/"
    var retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var service: API =retrofit.create(API::class.java)
    fun insertMaterial(mat:Material): Int{
        if(inventoryDao != null){
            InsertAsyncTask(inventoryDao).execute(mat).get()
        }
        return 0
    }

    suspend fun getMaterials():List<Material> = withContext(Dispatchers.IO){
        return@withContext inventoryDao?.getAllUnsendedMaterials()!!
    }


    fun updateStatus(mat: Material){
        inventoryDao?.updateStatus(mat)
    }

    fun getClients(): List<Cliente>{
        return inventoryDao?.getAllClients()!!
    }

    fun getInventories(): List<Inventario>{
        return inventoryDao?.getAllInventories()!!
    }

    fun insertInventories(inventories: List<Inventario>){
        inventoryDao?.insertAllInventories(inventories)
    }

    fun insertClients(clients: List<Cliente>){
        inventoryDao?.insertAllClients(clients)
    }

    private class InsertAsyncTask(private val inventoryDao: InventoryDao) : AsyncTask<Material,Void,Void>(){
        override fun doInBackground(vararg materials: Material?): Void? {
            for (material in materials){
                if(material != null) inventoryDao.insertMaterial(material)
            }
            return null
        }
    }
}