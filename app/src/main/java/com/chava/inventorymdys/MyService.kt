package com.chava.inventorymdys

import android.app.Application
import android.content.Context
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import com.chava.inventorymdys.Entity.*
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chava.inventorymdys.Entity.ImagesInfo
import com.chava.inventorymdys.Entity.InsertAnswer
import com.chava.inventorymdys.Entity.Material
import com.chava.inventorymdys.Entity.Result
import com.chava.inventorymdys.data.Room.InventoryRepository
import com.chava.inventorymdys.interfaces.API
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class MyService(application: Application,context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    val PROGRESS_MAX = 100
    val PROGRESS_CURRENT = 0
    private val MY_DEFAULT_TIMEOUT = 15000
    val repository = InventoryRepository(application)
    val CHANNEL_ID: String = "Inventario"
    val notificationId = 1
    var progress = 0
    var data = ""
    lateinit var values: InsertAnswer

    private fun savedata(string: String) {
        progress = 100
        data = string
    }
    private fun notificationSuccess(string: String)
    {
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
            setContentTitle("Datos enviados")
            setContentText(string)
            setSmallIcon(R.drawable.ic_check)
            setPriority(NotificationCompat.PRIORITY_LOW)
        }
        with(NotificationManagerCompat.from(applicationContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }
private fun notificationError(string: String){
    var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_error)
        .setContentTitle("Error")
        .setContentText("Hubo un error $string")
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText("Hubo un error al enviar. Tus datos si se han guardado. En caso de que no, nosotros lo revisaremos"))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    with(NotificationManagerCompat.from(applicationContext)) {
        // notificationId is a unique int for each notification that you must define
        notify(notificationId, builder.build())
    }
}
    private fun startNotification(){
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
            setContentTitle("Envio de Datos")
            setContentText("Envio en progreso")
            setSmallIcon(R.drawable.ic_envio)
            setPriority(NotificationCompat.PRIORITY_LOW)
        }
        with(NotificationManagerCompat.from(applicationContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    fun convertImageFileToBase64(imageFile: File): String {
        return FileInputStream(imageFile).use { inputStream ->
            ByteArrayOutputStream().use { outputStream ->
                Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                    inputStream.copyTo(base64FilterStream)
                    base64FilterStream.close()
                    Log.d("BASE64",outputStream.toString())
                    outputStream.toString()
                }
            }
        }
    }

    private fun genJSON(): JSONObject {
        var num_inventory = inputData.getString("num_inventory")
        var desc = inputData.getString("desc")
        var marca = inputData.getString("marca")
        var modelo = inputData.getString("modelo")
        var num_serie = inputData.getString("serie")
        var extra1 = inputData.getString("extra1")
        var extra2 = inputData.getString("extra2")
        var extra3 = inputData.getString("extra3")
        var extra4 = inputData.getString("extra4")
        var extra5 = inputData.getString("extra5")
        var nmaq = inputData.getString("num_maquina")
        var planta = inputData.getString("planta")
        var ubicPlanta = inputData.getString("ubicacion_planta")
        var division = inputData.getString("division")
        var ubic = inputData.getString("ubic")
        var nped = inputData.getString("nped")
        var fact = inputData.getString("fact")
        var num_act = inputData.getString("num_act")
        var linea = inputData.getString("linea")
        var area = inputData.getString("area")
        var id = inputData.getInt("id",0)
        var coment = inputData.getString("coment")
        var size = inputData.getInt("size",0)
        var data = JSONObject()
        try {
            data.put("num_inventario" , num_inventory)
            data.put("descripion_material" , desc)
            data.put("marca" , marca)
            data.put("modelo" , modelo)
            data.put("serie" , num_serie)
            data.put("num_maquina",nmaq)
            data.put("extra1",extra1)
            data.put("extra2",extra2)
            data.put("extra3",extra3)
            data.put("extra4",extra4)
            data.put("extra5",extra5)
            data.put("ubicacion_planta",ubicPlanta)
            data.put("planta",planta)
            data.put("division",division)
            data.put("ubicacion" , ubic)
            data.put("pedimento" , nped)
            data.put("factura_uuid" , fact)
            data.put("num_activo" , num_act)
            data.put("linea" , linea)
            data.put("area" , area)
            data.put("id_user" , id)
            data.put("comentarios" , coment)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        var photos = JSONObject()

        //Generar JSONArray para fotos
        try {
            for(i in 0 until size) {
                photos.put("D$i" , inputData.getString("D$i"))
                photos.put("I$i" , convertImageFileToBase64(File(inputData.getString("I$i")!!)))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        data.put("fotos" , photos)
       return data
    }
    private suspend fun sendToServer(mat: Material) : com.chava.inventorymdys.Entity.Result<InsertAnswer> = withContext(Dispatchers.IO){
        val call = repository.service.insert(mat)
        return@withContext call.executeForResult()
    }
    override fun doWork(): Result {
        startNotification()
        val url = "http://51.79.99.130:5050/eoapp/"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(API::class.java)
        GlobalScope.launch{
            var materials = repository.getMaterials()
            for (material in materials) {
                material.fotos = listOf(Gson().fromJson(material.fotosJson , ImagesInfo::class.java))
                material.fotos.forEach {
                    it.I = convertImageFileToBase64(File(it.P))
                }
                GlobalScope.launch {
                    val result = sendToServer(material)
                    when (result) {
                        is com.chava.inventorymdys.Entity.Result.Success -> {
                            val answer = result.response.body()
                            if(answer!!.message!!.contains("id")){
                                notificationSuccess(answer!!.message!!)
                                material.status = 1
                                material.fotos.forEach { it.I = " " }
                                repository.updateStatus(material)
                            }
                            else{
                                notificationError(answer!!.message!!)
                            }
                        }
                        is  com.chava.inventorymdys.Entity.Result.Failure -> {
                            Log.d("Response" , result.error.toString())
                            notificationError(result.error.toString())
                        }
                    }
                }
        }

            /*GlobalScope.launch(Dispatchers.IO) {
                try {
                    service.insert(material).let {
                        if (it.isSuccessful) {
                            values = it!!.body()!!
                            Log.d("Response" , values.toString())
                            values = it!!.body()!!
                            if (values != null) {
                                if (values.message!!.contains("id")) {
                                    if (values != null) {
                                        notificationSuccess(values.message!!)
                                        material.status = 1
                                        material.fotos.forEach { it.I = " " }
                                    }
                                    repository.updateStatus(material)
                                } else {
                                    notificationError(values.message!!)
                                }
                            }
                        }
                    }
                } catch (t: Exception) {
                    Log.d("Response" , t.toString())
                    notificationError(t.toString())
                }
            }*/
            /*call.enqueue(object: Callback<InsertAnswer> {
                override fun onResponse(
                    call: Call<InsertAnswer>? ,
                    response: retrofit2.Response<InsertAnswer>?
                ) {
                    if(response!!.isSuccessful){
                        values = response!!.body()!!
                        if (values != null) {
                            if(values.message!!.contains("id")){
                                if (values != null) {
                                    notificationSuccess(values.message!!)
                                    material.status = 1
                                    material.fotos.forEach { it.I = " " }
                                }
                                repository.updateStatus(material)
                            } else{
                                notificationError(values.message!!)
                            }
                        }
                    }*/
        }

        /*override fun onFailure(call: Call<InsertAnswer>? , t: Throwable?) {
                    Log.d("Response" , t.toString())
                    notificationError(t.toString())
                }*/
        /*})
        }*/
        startNotification()
        if(this::values.isInitialized){
            if(values.message!!.contains("id")){
                return Result.success()
            }
        }
        else
            return Result.retry()
        return Result.retry()
    }
    inline fun <reified T> Call<T>.executeForResult(): com.chava.inventorymdys.Entity.Result<T> {
        return try {
            val response = execute()
            com.chava.inventorymdys.Entity.Result.Success(this , response)
        } catch (e: Exception) {
            com.chava.inventorymdys.Entity.Result.Failure(this , e)
        }
    }
    private fun getName(p0: String?): String? {
        val file = File(p0)
        return file.name
    }
}

