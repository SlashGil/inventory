package com.chava.inventorymdys.Activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.chava.inventorymdys.Entity.Cliente
import com.chava.inventorymdys.Entity.Imagen
import com.chava.inventorymdys.Entity.Inventario
import com.chava.inventorymdys.MyService
import com.chava.inventorymdys.R
import com.chava.inventorymdys.SQLiteHelper
import com.chava.inventorymdys.Utilities.Online
import com.chava.inventorymdys.Utilities.Utilities
import com.chava.inventorymdys.VolleySingleton
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.sql.Blob
import java.text.SimpleDateFormat
import java.util.*


class MainActivity :AppCompatActivity(),View.OnClickListener {

    private val listC = mutableListOf<Cliente>()
    private val MYJOBID: Int = 1
    private val MY_DEFAULT_TIMEOUT = 15000
    private var fotos: MutableList<Imagen> = mutableListOf()
    val REQUEST_TAKE_PHOTO = 1
    var Handler: Handler? = null
    val Utilities: Utilities = Utilities()
    var string: String? = null
    var selector: Spinner? = null
    var edtNumInventory: EditText? = null
    var edtDescription: EditText? = null
    var edtMarca: EditText? = null
    var edtModelo: EditText? = null
    var edtNumSerie: EditText? = null
    var edtUbicacion: EditText? = null
    var edtComenFotos: EditText? = null
    var Online = Online(this)
    var uri: Uri? = null
    var edtNumPedimento: EditText? = null
    var edtFactura_UUID: EditText? = null
    var edtNumActivo: EditText? = null
    var edtLinea: EditText? = null
    var edtArea: EditText? = null
    var btnSend: MaterialButton? = null
    var toolbar: MaterialToolbar? = null
    var btnFoto: MaterialButton? = null
    var edtComentarios: EditText? = null
    var edtExtra: EditText? = null
    var txtCountFotos: TextView? = null
    var txtCliente: TextView? = null
    var contador: Int = 0
    var id_m = 1
    var conn: SQLiteHelper? = null
    var progreso: ProgressBar? = null
    var stringRequest: JsonObjectRequest? = null
    var listE: MutableList<Inventario> = mutableListOf()
    var inventoryready: Boolean? = false
    val KEY_NAME = "NAME"
    val KEY_ID = "ID_USER"
    val KEY_USER = "USERNAME"
    val DirectorioRaiz = "InventoryApp/"
    var data: JSONObject = JSONObject()
    var DirectorioPropio: String? = null
    var fullDirectorio: String? = null
    var inventario: String? = null
    var volley: VolleySingleton? = null
    var id_user: String = ""
    var username: String = ""
    var pref: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermisos()
        toolbar = findViewById(R.id.materialToolbar)
        setSupportActionBar(toolbar)
        toolbar!!.overflowIcon!!.setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        volley = VolleySingleton.getInstance(this)
        selector = findViewById(R.id.selector)
        txtCliente = findViewById(R.id.Cliente)
        pref = getSharedPreferences("Usuario" , Context.MODE_PRIVATE)
        selector!!.visibility = View.GONE
        txtCliente!!.visibility = View.GONE
        var face = Typeface.createFromAsset(assets , "font/nimbus.ttf")
        //Ingresar y setear los elementos del XML a Kotlin, para poder usarlos
        edtNumInventory = findViewById(R.id.edtNumInventory)
        edtDescription = findViewById(R.id.edtDescription)
        edtMarca = findViewById(R.id.edtMarca)
        edtModelo = findViewById(R.id.edtModelo)
        edtNumSerie = findViewById(R.id.edtNumSerie)
        edtUbicacion = findViewById(R.id.edtUbication)
        edtNumPedimento = findViewById(R.id.edtNumPedimento)
        edtFactura_UUID = findViewById(R.id.edtFactura_UUID)
        edtNumActivo = findViewById(R.id.edtNumActivo)
        edtLinea = findViewById(R.id.edtLinea)
        edtArea = findViewById(R.id.edtArea)
        edtComentarios = findViewById(R.id.edtComentarios)
        edtExtra = findViewById(R.id.edtExtras)
        edtComenFotos = findViewById(R.id.edtComenFotos)
        txtCountFotos = findViewById(R.id.countPhotos)
        btnSend = findViewById(R.id.enviar)
        btnFoto = findViewById(R.id.takePhoto)
        //Ingresar el tipo de fuente determinada en la interfaz
        edtNumInventory!!.setTypeface(face)
        edtDescription!!.setTypeface(face)
        edtMarca!!.setTypeface(face)
        edtModelo!!.setTypeface(face)
        edtNumSerie!!.setTypeface(face)
        edtUbicacion!!.setTypeface(face)
        edtNumPedimento!!.setTypeface(face)
        edtFactura_UUID!!.setTypeface(face)
        edtNumActivo!!.setTypeface(face)
        edtLinea!!.setTypeface(face)
        edtArea!!.setTypeface(face)
        edtComentarios!!.setTypeface(face)
        edtExtra!!.setTypeface(face)
        edtComenFotos!!.setTypeface(face)
        txtCountFotos!!.setTypeface(face)
        btnSend!!.setTypeface(face)
        btnSend!!.setOnClickListener(this)
        btnFoto!!.setOnClickListener(this)

        //Validar campos
        btnFoto!!.isEnabled = false
        btnSend!!.isEnabled = false
        edtNumInventory!!.afterTextChanged {
            checkData()
        }
        edtDescription!!.afterTextChanged {
            checkData()
        }
        edtMarca!!.afterTextChanged {
            checkData()
        }
        edtModelo!!.afterTextChanged {
            checkData()
        }
        edtNumSerie!!.afterTextChanged {
            checkData()
        }
        edtUbicacion!!.afterTextChanged {
            checkData()
        }
        edtNumPedimento!!.afterTextChanged {
            checkData()
        }
        edtFactura_UUID!!.afterTextChanged {
            checkData()
        }
        edtNumActivo!!.afterTextChanged {
            checkData()
        }
        edtLinea!!.afterTextChanged {
            checkData()
        }
        edtArea!!.afterTextChanged {
            checkData()
        }
        edtComentarios!!.afterTextChanged {
            checkData()
        }
        edtExtra!!.afterTextChanged {
            checkData()
        }
        edtComenFotos!!.afterTextChanged {
            checkData()
        }

        DirectorioPropio = "${edtNumInventory!!.text.toString()}/"
        fullDirectorio = DirectorioRaiz + DirectorioPropio
        volley = VolleySingleton.getInstance(this)
        conn = SQLiteHelper(this.applicationContext , "bd_Inventory" , null , 1)
        //Revisar si ya se habia seleccionado previamente un Inventario
        inventoryready = checkInventory()
        if (inventoryready == false)
            buildDialog()
        checkUser()
        // updateSpinner()
        //updateSpinnerVolley()


    }

    private fun checkPermisos(): Boolean {
        if ((checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
            (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        ) {
            return true;
        }

        if ((shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) ||
            (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        ) {
            cargarDialogoRecomendacion();
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                    Manifest.permission.CAMERA
                ) , 100
            );
        }

        return false;
    }

    private fun cargarDialogoRecomendacion() {
        val dialogo =
            AlertDialog.Builder(this)
        dialogo.setTitle("Permisos Desactivados")
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App")
        dialogo.setPositiveButton(
            "Aceptar"
        ) { dialogInterface , i ->
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                    Manifest.permission.CAMERA
                ) , 100
            )
        }
        dialogo.show()
    }

    private fun checkUser() {
        id_user = pref!!.getString(KEY_ID , " ")!!
        username = pref!!.getString(KEY_NAME , " ")!!
        Toast.makeText(this , "username: $username" , Toast.LENGTH_LONG).show()
    }
private fun toJSON(): PersistableBundle {
    var bundle = PersistableBundle()
    var listComen = mutableListOf<String>()
    var listPath = mutableListOf<String>()
    for (Imagen in fotos)
    {
        listComen.add(Imagen.descripcion)
        listPath.add(Imagen.path)
    }
    var descFotos = listComen.toTypedArray()
    var pathFotos = listPath.toTypedArray()
    var num_inventory =edtNumInventory!!.text.toString()
    var desc = edtDescription!!.text.toString()
    var marca = edtMarca!!.text.toString()
    var modelo = edtModelo!!.text.toString()
    var num_serie = edtNumSerie!!.text.toString()
    var ubic = edtUbicacion!!.text.toString()
    var nped = edtNumPedimento!!.text.toString()
    var fact = edtFactura_UUID!!.text.toString()
    var num_act = edtNumActivo!!.text.toString()
    var linea = edtLinea!!.text.toString()
    var area = edtArea!!.text.toString()
    var id = id_user.toInt()
    var coment = edtComentarios!!.text.toString()
    var extras = edtExtra!!.text.toString()
    bundle.putString("num_inventory",num_inventory)
    bundle.putString("desc",desc)
    bundle.putString("marca",marca)
    bundle.putString("modelo",modelo)
    bundle.putString("num_serie",num_serie)
    bundle.putString("ubic",ubic)
    bundle.putString("nped",nped)
    bundle.putString("fact",fact)
    bundle.putString("num_act",num_act)
    bundle.putString("linea",linea)
    bundle.putString("area",area)
    bundle.putInt("id",id)
    bundle.putString("coment",coment)
    bundle.putString("extras",extras)
    bundle.putStringArray("descfoto",descFotos)
    bundle.putStringArray("pathfoto",pathFotos)
    return bundle
}
    /**private fun chargeImgSqlite() {
        var db: SQLiteDatabase = conn!!.writableDatabase
        var values = ContentValues()
        var insertConta = 0
        if (contador > 0) {
            for (Imagen in fotos) {
                values.put(Utilities.CAMPO_ID_IMAGEN, insertConta)
                values.put(Utilities.CAMPO_ID_MATERIAL,id_m)
                values.put(Utilities.CAMPO_DESCRIP_MATERIAL,edtDescription!!.text.toString())
                values.put(Utilities.CAMPO_DESCRIPCION , Imagen.descripcion)
                values.put(Utilities.CAMPO_IMAGEN , Imagen.path)
                db.insert(Utilities.TABLE_IMG , Utilities.CAMPO_DESCRIP_MATERIAL, values)
                insertConta++

            }
            Toast.makeText(
                this ,
                "Se ingresaron $insertConta a la tabla ${Utilities.TABLE_IMG}" ,
                Toast.LENGTH_LONG
            ).show()
            id_m++
            onRefresh()
        } else
            Toast.makeText(
                this ,
                "No se pueden ingresar datos porque no has tomado fotos :(" ,
                Toast.LENGTH_LONG
            ).show()
    }**/

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

    private fun updateLater(){
        var componentName = ComponentName(applicationContext ,MyService::class.java)
        var jobInfo = JobInfo.Builder(MYJOBID,componentName).
            setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setPersisted(true)
            .setExtras(toJSON()).
                build()
        Toast.makeText(this,"Guardando datos...",Toast.LENGTH_LONG).show()
        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(jobInfo)
        Toast.makeText(this,"Tus datos han sido guardados, Puedes seguir!",Toast.LENGTH_LONG).show()
        onRefresh()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.takePhoto) {

            if (contador < 3) {
                contador++
                string = edtComenFotos!!.text.toString()
                tomarFotos()
                txtCountFotos!!.setText("Fotos : $contador")
            } else {
                btnFoto!!.isEnabled = false
                btnSend!!.isEnabled = true
                edtComenFotos!!.setText("Ya tomaste tus fotos")
                edtComenFotos!!.isEnabled = false
                Toast.makeText(
                    this ,
                    "Lo siento, solo puedes tomar 3 fotos por caso" ,
                    Toast.LENGTH_LONG
                ).show()

            }

        }
        if (v.id == R.id.enviar) {
           if (Online.isOnline()) {
                cargarWebService()
            } else {
                updateLater()
            }
        }
    }

    private fun getId_Material(): String {
        var String = "MAT_"
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        String += timeStamp
        return String
    }

    private fun cargarSqlite() {
        var db: SQLiteDatabase = conn!!.writableDatabase
        var values = ContentValues()
        var id = pref!!.getString(KEY_ID , "")
        var idNum: Int = 0
        if (id != "") {
            idNum = id!!.toInt()
        }
        values.put(Utilities.CAMPO_NUM_INVENTARIO , edtNumInventory!!.text.toString())
        values.put(Utilities.CAMPO_DESCRIP_MATERIAL , edtDescription!!.text.toString())
        values.put(Utilities.CAMPO_MARCA , edtMarca!!.text.toString())
        values.put(Utilities.CAMPO_MODELO , edtModelo!!.text.toString())
        values.put(Utilities.CAMPO_SERIE , edtNumSerie!!.text.toString())
        values.put(Utilities.CAMPO_UBICACION , edtUbicacion!!.text.toString())
        values.put(Utilities.CAMPO_PEDIMENTO , edtNumPedimento!!.text.toString())
        values.put(Utilities.CAMPO_FACTURA_UUID , edtFactura_UUID!!.text.toString())
        values.put(Utilities.CAMPO_NUM_ACTIVO , edtNumActivo!!.text.toString())
        values.put(Utilities.CAMPO_LINEA , edtLinea!!.text.toString())
        values.put(Utilities.CAMPO_AREA , edtArea!!.text.toString())
        values.put(Utilities.CAMPO_ID_USER , idNum)
        values.put(Utilities.CAMPO_COMENTARIOS , edtComentarios!!.text.toString())
        values.put(Utilities.CAMPO_EXTRAS , edtExtra!!.text.toString())
        db.insert(Utilities.TABLE_MATERIALES , Utilities.CAMPO_NUM_INVENTARIO , values)
        Toast.makeText(this , "Se registro exitosamente" , Toast.LENGTH_LONG).show()

    }

    private fun cargarWebService() {

        progreso = ProgressBar(this)
        progreso!!.setProgress(0)
        var url = "http://www.eo-ti.com/api/wsinsert.php"
        var json = genJSON()
        stringRequest = JsonObjectRequest(Request.Method.POST , url ,json , Response.Listener {
            Log.d("RESPONSE" , it.toString())
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            if(it.getString("mensaje").contains("id_material")){
                    Toast.makeText(this,"Datos enviados",Toast.LENGTH_LONG).show()
                    onRefresh()
                }
            else
                Toast.makeText(this,it.getString("mensaje"),Toast.LENGTH_LONG).show()
        } , Response.ErrorListener {
            Log.d("Error" , it.toString())
            Toast.makeText(this, "Lo siento, no se puede enviar tus datos", Toast.LENGTH_LONG).show()
        })
        stringRequest!!.setRetryPolicy(DefaultRetryPolicy(
        MY_DEFAULT_TIMEOUT,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Toast.makeText(this,"Se estan enviando tus datos",Toast.LENGTH_LONG).show()
        volley!!.requestQueue.add(stringRequest!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu , menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuSalir -> {
                logout()
                Toast.makeText(this , "Adios " + username + "!" , Toast.LENGTH_LONG).show()
                var login = Intent(this , LoginActivity::class.java)
                startActivityForResult(login , 0)

            }
            R.id.mnunuevo -> {
                onRefresh()
            }
            R.id.mnuInventario -> {
                eraseInventory()
                buildDialog()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        borrarUsuario()
    }

    private fun borrarUsuario() {
        var editor = pref!!.edit()
        editor.remove(KEY_USER)
        editor.remove(KEY_NAME)
        editor.remove(KEY_ID)
        editor.commit()
    }

    private fun saveInventory(i: Inventario) {
        var editor = pref!!.edit()
        editor.putString("NumInv" , i.number)
        editor.putString("Cliente" , i.cliente)
        editor.putString("Inventario" , i.inventario)
        editor.putString("Fecha" , i.hora)
        editor.commit()
    }

    private fun eraseInventory() {
        var editor = pref!!.edit()
        editor.remove("NumInv")
        editor.remove("Cliente")
        editor.remove("Inventario")
        editor.remove("Fecha")
        editor.commit()
    }

    private fun checkInventory(): Boolean {
        inventario = pref!!.getString("Inventario" , "")
        if (inventario != "") {
            edtNumInventory!!.setText(inventario)
            edtNumInventory!!.isEnabled = false
            return true
        } else
            if (inventario.isNullOrEmpty())
                return false
            else
                return false
    }

    //Funcion que construye el Array pora el request
    fun genJSON(): JSONObject {
        var data = JSONObject()
        try {
            data.put("num_inventario" , edtNumInventory!!.text.toString())
            data.put("descripion_material" , edtDescription!!.text.toString())
            data.put("marca" , edtMarca!!.text.toString())
            data.put("modelo" , edtModelo!!.text.toString())
            data.put("serie" , edtNumSerie!!.text.toString())
            data.put("ubicacion" , edtUbicacion!!.text.toString())
            data.put("pedimento" , edtNumPedimento!!.text.toString())
            data.put("factura_uuid" , edtFactura_UUID!!.text.toString())
            data.put("num_activo" , edtNumActivo!!.text.toString())
            data.put("linea" , edtLinea!!.text.toString())
            data.put("area" , edtArea!!.text.toString())
            data.put("id_user" , id_user.toInt())
            data.put("comentarios" , edtComentarios!!.text.toString())
            data.put("extras" , edtExtra!!.text.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        var photos = JSONObject()

        //Generar JSONArray para fotos
        try {
            for (i in 0 until fotos.size) run {

                photos.put("D$i" , fotos[i].descripcion)
                photos.put("I$i" , fotos[i].imagen)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        data.put("fotos" , photos)
        var prueba = photos.getString("I0")
        Log.d("JSON", prueba)
        //Toast.makeText(this, "JSONArray dentro de JSON generado", Toast.LENGTH_LONG).show()
        var string = data.toString()
        Log.d("JSON" , string)
        return data
    }

    //Funcion que construye el dialog
    private fun buildDialog() {
        if (Online.isOnline()) {
            var url = "http://eo-ti.com/api/listinventory.php?solicitud=ListaI"
            var item: MutableList<String> = mutableListOf()
            var request =
                JsonObjectRequest(Request.Method.GET , url , null , Response.Listener<JSONObject> {
                    var data = it.getJSONObject("data")
                    var id = data.getString("id_cliente")
                    var cliente = data.getString("nombre_cliente")
                    listC.add(Cliente(id,cliente))
                    Log.d("Response" , "$id, $cliente, $inventario")
                    for (Cliente in listC) {
                        item.add(Cliente.id + " " + Cliente.nombre)
                    }
                    var items = item.toTypedArray()
                    var builder = MaterialAlertDialogBuilder(this@MainActivity)
                    builder.setTitle("Clientes")
                    builder.setSingleChoiceItems(
                        items ,
                        1 ,
                        DialogInterface.OnClickListener { dialog , which ->
                            for( i in items.indices){
                                when(which == i){
                                    true -> saveClient(listC[i])
                                }
                            }
                        })
                    var alert: androidx.appcompat.app.AlertDialog = builder.create()
                    alert.setCanceledOnTouchOutside(false);
                    alert.show()
                } , Response.ErrorListener {
                    Log.d("Error" , "${it.toString()}")
                    Toast.makeText(this , "${it.toString()}" , Toast.LENGTH_LONG).show()
                })
            volley!!.requestQueue.add(request)
            Log.d("Data" , data.toString())

        }
    }

    private fun saveClient(cliente:Cliente) {
        var editor = pref!!.edit()
        var id = cliente.id
        var nombre = cliente.nombre
        editor.putString("Cliente_ID" , id)
        editor.putString("Cliente_Nombre",nombre)
        editor.commit()
    }

    override fun onResume() {
        super.onResume()
        if(contador == 3){
            btnFoto!!.isEnabled = false
        }

    }
    private fun checkData() {
        var lista = listOf<EditText>(
            edtNumInventory!! ,
            edtDescription!! ,
            edtMarca!! ,
            edtModelo!! ,
            edtNumSerie!! ,
            edtUbicacion!! ,
            edtFactura_UUID!! ,
            edtNumPedimento!! ,
            edtArea!! ,
            edtNumActivo!! ,
            edtLinea!! ,
            edtComentarios!! ,
            edtExtra!! ,
            edtComenFotos!!
        )
        var errors = 0
        for (EditText in lista) {
            if (EditText.nonEmpty()) {
                EditText.setBackgroundColor(getColor(R.color.btnFoto))
            } else {
                EditText.setError(getString(R.string.blank))
                errors++
            }
        }
        if (errors == 0) {
            btnFoto!!.isEnabled = true
            btnFoto!!.setBackgroundColor(getColor(R.color.btnFoto))
            btnSend!!.isEnabled = true
            btnSend!!.setBackgroundColor(getColor(R.color.colorButton))
            btnSend!!.setTextColor(getColor(R.color.white))
        } else
            btnSend!!.setBackgroundColor(getColor(R.color.buttonDisabled))
    }

    private fun cargarInventarios(mutableList: MutableList<Inventario>) {
        var db = conn!!.writableDatabase
        for (Inventario in mutableList) {
            var cv = ContentValues()
            cv.put(Utilities.CAMPO_ID_CLIENTE , Inventario.number)
            cv.put(Utilities.CAMPO_NOM_CLIENTE , Inventario.cliente)
            cv.put(Utilities.CAMPO_NUM_INVENTORY , Inventario.inventario)
            cv.put(Utilities.FECHA_INVENTARIO , Inventario.hora)
            db.insert(Utilities.TABLE_INV , null , cv)
        }
    }

    private fun onRefresh() {
        edtComentarios!!.setText("")
        edtExtra!!.setText("")
        edtArea!!.setText("")
        edtLinea!!.setText("")
        edtNumActivo!!.setText("")
        edtFactura_UUID!!.setText("")
        edtNumPedimento!!.setText("")
        edtUbicacion!!.setText("")
        edtDescription!!.setText("")
        edtMarca!!.setText("")
        edtModelo!!.setText("")
        edtNumSerie!!.setText("")
        edtComenFotos!!.setText("")
        contador = 0
        txtCountFotos!!.setText("Fotos: $contador")
        deleteImages()
    }

    private fun deleteImages() {
        var size = fotos.size
        for (i in 0 until size)
            fotos.removeAt(i)
    }

    var currentPhotoPath: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        var nombre = getId_Material() + "_" + contador
        val storageDir: File = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_$nombre" , ".jpg" , storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun tomarFotos() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()

                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this ,
                        "com.chava.inventorymdys.fileprovider" , it
                    )
                    uri = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT , photoURI)
                    startActivityForResult(takePictureIntent , REQUEST_TAKE_PHOTO)
                    }
                }
            }
        }

    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if(resultCode == Activity.RESULT_OK) {
            val imagen = convertImageFileToBase64(File(currentPhotoPath!!))
            val path:String = currentPhotoPath!!
            val img = Imagen(string!!,imagen,path,id_m)
                fotos.add(img)
            Log.d("SIZE",fotos.size.toString())
        }

    }
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
