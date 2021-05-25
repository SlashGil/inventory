package com.chava.inventorymdys.data.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chava.inventorymdys.Entity.*

@Database(entities = arrayOf(Material::class,Cliente::class, Inventario::class), version = 3, exportSchema = false)
abstract class InventoryDB : RoomDatabase(){
    companion object{
        private const val DATABASE_NAME ="Inventory.db"
        @Volatile
        private var INSTANCE: InventoryDB? = null

        fun getInstance(context:Context): InventoryDB?{
            INSTANCE ?: synchronized(this){
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext ,
                    InventoryDB::class.java,
                    DATABASE_NAME).build()
            }
            return INSTANCE
        }
    }
    abstract val inventoryDao: InventoryDao
}