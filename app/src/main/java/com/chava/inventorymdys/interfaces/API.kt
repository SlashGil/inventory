package com.chava.inventorymdys.interfaces

import com.chava.inventorymdys.Entity.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface API {
    @GET("api/login.php?")
    fun login(@Query("nomb") name: String,@Query("passw") passw:String): Call<LoginAnswer>

    @POST("api/wsinsert.php")
    fun insert(@Body mat: Material): Call<InsertAnswer>

    @GET("api/test/listInventory.php?")
    fun Inventorys(@Query("solicitud") sol: String): Call<List<Inventory>>

    @POST("api/search.php")
    fun search(@Body s:Search): Call<List<SearchItem>>

    @POST("api/update.php")
    fun update(@Body mat: Material): Call<InsertAnswer>
}