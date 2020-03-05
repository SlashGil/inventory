package com.chava.inventorymdys.Activities

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chava.inventorymdys.Activities.MainActivity
import com.chava.inventorymdys.Activities.LoginActivity
import com.chava.inventorymdys.R


class Bienvenida : AppCompatActivity() {
    val CHANNEL_ID: String = "Inventario"
    var pref: SharedPreferences? = null
    val KEY_ID = "ID_USER"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)
        pref = getSharedPreferences("Usuario", Context.MODE_PRIVATE)
        createNotificationChannel()
        Handler().postDelayed(Runnable {
            checkLogin()
        },2000)
    }

    private fun checkLogin(){
        var id = pref!!.getString(KEY_ID,"No hay ID")
        if (id != "No hay ID")
            Intent()
        else
            IntentLogin()
    }
    private fun IntentLogin(){
        var intent: Intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent,0)
        setResult(Activity.RESULT_OK)
        //Complete and destroy login activity once successful
        finish()
    }
    private fun Intent(){
        var intent: Intent = Intent(this, MainActivity::class.java)
        startActivityForResult(intent,0)
        setResult(Activity.RESULT_OK)
        //Complete and destroy login activity once successful
        finish()
    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
