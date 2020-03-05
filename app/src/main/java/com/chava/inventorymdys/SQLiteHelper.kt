package com.chava.inventorymdys

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.chava.inventorymdys.Utilities.Utilities

class SQLiteHelper : SQLiteOpenHelper {
    val Utilities : Utilities = Utilities()
    constructor(
        context: Context,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int
    ) : super(context, name, factory, version)

    override fun onCreate(db: SQLiteDatabase?) {

        if (db != null) {
            db.execSQL(Utilities.CREATE_TABLE_MATERIALES)
            db.execSQL(Utilities.CREATE_TABLE_IMG)
            db.execSQL(Utilities.CREATE_TABLE_USER)
            db.execSQL(Utilities.CREATE_TABLE_INVENTORY)
        }

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + Utilities.TABLE_USER)
        db.execSQL("DROP TABLE IF EXISTS " + Utilities.TABLE_IMG)
        db.execSQL("DROP TABLE IF EXISTS " + Utilities.TABLE_MATERIALES)
        onCreate(db)
    }

}