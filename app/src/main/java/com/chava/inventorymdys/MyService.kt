package com.chava.inventorymdys

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.chava.inventorymdys.Utilities.Utilities
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class MyService : JobService() {
    val PROGRESS_MAX = 100
    val PROGRESS_CURRENT = 0
    var volley = VolleySingleton.getInstance(this)
    val CHANNEL_ID: String = "Inventario"
    val notificationId = 1
    var progress = 0
    var data = ""
    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    private fun toServer(json: JSONObject ){
        val url = "http://www.eo-ti.com/api/wsinsert.php"
        progress = 40
        val request = JsonObjectRequest(Request.Method.POST,url,json, Response.Listener<JSONObject> {
            if (it.getString("mensaje").contains("id_material")){
                savedata(it.getString("mensaje"))
                notificationSuccess(data)
            }

            Log.d("VOLLEY",it.getString("mensaje"))
        }, Response.ErrorListener{

                Log.d("VOLLEY",it.toString())
                notificationError(it.toString())
            })
            volley!!.requestQueue.add(request)
        }
    private fun savedata(string: String) {
        progress = 100
        data = string
    }
    private fun notificationSuccess(string: String)
    {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("Datos enviados")
            setContentText(string)
            setSmallIcon(R.drawable.ic_check)
            setPriority(NotificationCompat.PRIORITY_LOW)
        }
    }
private fun notificationError(string: String){
    var builder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_error)
        .setContentTitle("Error")
        .setContentText("Hubo un error $string")
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText("Hubo un error al enviar. Tus datos si se han guardado. En caso de que no, nosotros lo revisaremos"))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
}
    override fun onStartJob(params: JobParameters?): Boolean {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("Envio de Datos")
            setContentText("Envio en progreso")
            setSmallIcon(R.drawable.ic_envio)
            setPriority(NotificationCompat.PRIORITY_LOW)
        }
        updateLater(params!!)
        return false
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
fun updateLater(jobParams: JobParameters) {
            var json = genJSON(jobParams)
            Log.d("SERVICE","El servicio comenzara a procesar tus datos")
            toServer(json)
            jobFinished(jobParams,false)
            Log.d("SERVICE","Finalizando Servicio")
        }

    private fun genJSON(jobParams: JobParameters): JSONObject {
        var num_inventory =jobParams.extras.getString("num_inventory")
        var desc = jobParams.extras.getString("desc")
        var marca =jobParams.extras.getString("marca")
        var modelo = jobParams.extras.getString("modelo")
        var num_serie =jobParams.extras.getString("num_serie")
        var ubic = jobParams.extras.getString("ubic")
        var nped = jobParams.extras.getString("nped")
        var fact = jobParams.extras.getString("fact")
        var num_act = jobParams.extras.getString("num_act")
        var linea = jobParams.extras.getString("linea")
        var area = jobParams.extras.getString("area")
        var id = jobParams.extras.getInt("id")
        var coment = jobParams.extras.getString("coment")
        var extras = jobParams.extras.getString("extras")
        var descfoto= jobParams.extras.getStringArray("descfoto")
        var pathfoto = jobParams.extras.getStringArray("pathfoto")
        var data = JSONObject()
        try {
            data.put("num_inventario" , num_inventory)
            data.put("descripion_material" , desc)
            data.put("marca" , marca)
            data.put("modelo" , modelo)
            data.put("serie" , num_serie)
            data.put("ubicacion" , ubic)
            data.put("pedimento" , nped)
            data.put("factura_uuid" , fact)
            data.put("num_activo" , num_act)
            data.put("linea" , linea)
            data.put("area" , area)
            data.put("id_user" , id)
            data.put("comentarios" , coment)
            data.put("extras" , extras)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        var photos = JSONObject()

        //Generar JSONArray para fotos
        try {
            for (i in descfoto!!.indices) run {

                photos.put("D$i" , descfoto[i])
                photos.put("I$i" , convertImageFileToBase64(File(pathfoto!![i])))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        data.put("fotos" , photos)
       return data
    }
}

