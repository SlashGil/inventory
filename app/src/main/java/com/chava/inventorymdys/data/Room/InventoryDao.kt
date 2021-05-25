package com.chava.inventorymdys.data.Room

import androidx.room.*
import com.chava.inventorymdys.Entity.Cliente
import com.chava.inventorymdys.Entity.ImagesInfo
import com.chava.inventorymdys.Entity.Inventario
import com.chava.inventorymdys.Entity.Material
@Dao
interface InventoryDao {
    @Query("Select * from materials where status=0")
    suspend fun getAllUnsendedMaterials(): List<Material>

    @Query("Select * from clients")
    fun getAllClients(): List<Cliente>

    @Query("Select * from inventories")
    fun getAllInventories(): List<Inventario>

    @Insert
    fun insertMaterial(material:Material)

    @Insert
    fun insertAllClients(clients: List<Cliente>)

    @Insert
    fun insertAllInventories(inventories: List<Inventario>)

    @Update
    fun updateStatus(material: Material)
}