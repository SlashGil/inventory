package com.chava.inventorymdys

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton private constructor(context: Context) {
    val requestQueue: RequestQueue

    companion object {
        private var instance: VolleySingleton? = null
        fun getInstance(context: Context): VolleySingleton? {
            if (instance == null) {
                instance = VolleySingleton(context)
            }
            return instance
        }
    }

    init {
        requestQueue = Volley.newRequestQueue(context.applicationContext)
    }
}