package com.chava.inventorymdys.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.chava.inventorymdys.Activities.MainActivity
import com.chava.inventorymdys.Entity.LoginAnswer
import com.chava.inventorymdys.Entity.User
import com.chava.inventorymdys.R
import com.chava.inventorymdys.SQLiteHelper
import com.chava.inventorymdys.Utilities.Online
import com.chava.inventorymdys.VolleySingleton
import com.chava.inventorymdys.interfaces.API
import com.chava.inventorymdys.ui.login.LoginViewModel
import com.chava.inventorymdys.ui.login.LoginViewModelFactory
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    var usuario: User? = null
    val KEY_NAME = "NAME"
    val KEY_ID = "ID_USER"
    val KEY_USER = "USERNAME"
    var pref: SharedPreferences? = null
    var online = Online(this)
    var conn = SQLiteHelper(this,"bd_inventory",null,1)
    var volley: VolleySingleton? = null
    var isLogged: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences("Usuario", Context.MODE_PRIVATE)
        checkLogin()
        setContentView(R.layout.activity_login)
        val Nimbus= "font/nimbus.ttf"
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)
        loginViewModel = ViewModelProviders.of(this , LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        volley = VolleySingleton.getInstance(this)
        loginViewModel.loginFormState.observe(this@LoginActivity , Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid
            if(login.isEnabled == true)
                login.setBackgroundColor(getColor(R.color.colorButton))
            else
                login.setBackgroundColor(getColor(R.color.buttonDisabled))
            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })
        login.setOnClickListener {
            acceder(username.text.toString(),password.text.toString())
        }
        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString() ,
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString() ,
                    password.text.toString()
                )
            }
        }
    }

    private fun guardarUsuario(username: String,name: String, id: String){
       var editor: SharedPreferences.Editor = pref!!.edit()
        editor.putString(KEY_USER, username)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_ID,id)
        editor.commit()
    }
    private fun checkLogin(){
        var id = pref!!.getString(KEY_ID,"No hay ID")
        if (id != "No hay ID")
            Intent()
    }

    private fun acceder(username: String , password: String) {
        if(online.isOnline())
            loginRequest(username,password)
        else
            loginSQLite(username,password)
}

    private fun loginRequest(username: String , password: String){
            var url = "http://marketi.servehttp.com:80/EO-Plataform/Eo-service/"
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service =retrofit.create(API::class.java)
            val call = service.login(username,password)
            call.enqueue(object: Callback<LoginAnswer> {

                override fun onResponse(
                    call: Call<LoginAnswer>? ,
                    response: retrofit2.Response<LoginAnswer>?
                ) {
                    var isLogged = false
                    if(response!!.code()==200){
                        val values = response.body()!!
                        if(values.status!!.toInt()==801){
                            var name = values.status_message!!.substringAfterLast("ss ")
                            Log.d("onResponseAnswer" , "${values.status}, ${values.status_message}, ${values.id}, $name")
                            guardarUsuario(username,name,values.id.toString())
                            Toast.makeText(this@LoginActivity,"Bienvenido $name!", Toast.LENGTH_LONG).show()
                            isLogged = true
                            Intent()
                        }
                        else{
                            isLogged = false
                            Toast.makeText(this@LoginActivity,"Acceso denegado!", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginAnswer>? , t: Throwable?) {
                    Log.d("Response" , t.toString())
                    isLogged = false
                    Toast.makeText(this@LoginActivity,"Acceso denegado!", Toast.LENGTH_LONG).show()
                }
            })
    }
    private fun Intent(){
        var intent: Intent = Intent(this, MainActivity::class.java)
        startActivityForResult(intent,0)
        setResult(Activity.RESULT_OK)
        //Complete and destroy login activity once successful
        finish()
    }

    private fun loginSQLite(username: String, password: String) {
        var query = conn.readableDatabase.rawQuery(
            "SELECT user, contra from us where user='$username' and contra='$password'" ,
            null
        )
        var queryName = conn.readableDatabase.rawQuery(
            "SELECT nameuser from us where user='$username'" ,
            null
        )
        val name = queryName.moveToFirst().toString()
        val queryID =conn.readableDatabase.rawQuery("SELECT id_user from us where user='$username'" , null)
        val ID = queryID.moveToFirst().toString()
        // TODO: handle loggedInUser authentication
        if (query.moveToFirst()) {
            guardarUsuario(username,name,ID)
            Toast.makeText(this,"Bienvenido ${name}!", Toast.LENGTH_LONG).show()
            Intent()
        }
        else
            Toast.makeText(this,"Acceso denegado!", Toast.LENGTH_LONG).show()

    }
    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence ,
                start: Int ,
                count: Int ,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence , start: Int , before: Int , count: Int) {}
        })
    }
}
