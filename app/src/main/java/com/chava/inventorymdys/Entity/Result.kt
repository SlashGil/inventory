package com.chava.inventorymdys.Entity

import retrofit2.Call
import retrofit2.Response

sealed class Result<T> {
    data class Success<T>(val call: Call<T> , val response: Response<T>): Result<T>()
    data class Failure<T>(val call: Call<T>, val error: Throwable): Result<T>()
    /*inline fun <reified T> Call<T>.enqueue(crossinline result: (Result<T>) -> Unit) {
        enqueue(object: Callback<T> {
            override fun onFailure(call: Call<T>, error: Throwable) {
                result(Failure(call, error))
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                result(Success(call, response))
            }
        })
    }
    inline fun <reified T> Call<T>.executeForResult(): Result<T> {
        return try {
            val response = execute()
            Success(this, response)
        } catch (e: Exception) {
            Failure(this, e)
        }
    }*/
}