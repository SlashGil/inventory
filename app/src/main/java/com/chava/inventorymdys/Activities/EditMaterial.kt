package com.chava.inventorymdys.Activities

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.FileProvider
import com.chava.inventorymdys.Entity.*
import com.chava.inventorymdys.R
import com.chava.inventorymdys.Utilities.Online
import com.chava.inventorymdys.data.Room.InventoryRepository
import com.chava.inventorymdys.interfaces.API
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class EditMaterial : AppCompatActivity() , View.OnClickListener {
    private var customAlertDialogView : View? = null
    private var currentPhotoPath = ""
    private var fotos : MutableList<Imagen>? = mutableListOf()
    val REQUEST_TAKE_PHOTO = 1
    lateinit var repository : InventoryRepository
    private var MaterialToEdit : Material? = null
    var string : String? = null
    var number : Int = 0
    var Online = Online(this)
    var uri : Uri? = null
    lateinit var log : Logger
    var contador : Int = 0
    private var id_m = 1
    private val CHANNEL_ID : String = "Inventario"
    private val notificationId = 1
    var progreso : ProgressBar? = null
    val KEY_NAME = "NAME"
    val KEY_ID = "ID_USER"
    val KEY_USER = "USERNAME"
    val KEY_CLIENT = "ID_Cliente"
    val KEY_INVENTORY = "Inventory"
    val DirectorioRaiz = "InventoryApp/"
    var data : JSONObject = JSONObject()
    private var DirectorioPropio : String? = null
    private var fullDirectorio : String? = null
    var inventario : String? = null
    var id_user : String = ""
    var username : String = ""
    var pref : SharedPreferences? = null
    var edit = false


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermisos()
        setSupportActionBar(materialToolbar)
        log = LoggerFactory.getLogger(MainActivity::class.java)
        log.info("LOGGER SETUP COMPLETED")
        repository = InventoryRepository(application)
        materialToolbar!!.overflowIcon!!.setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        pref = getSharedPreferences("Usuario" , MODE_PRIVATE)
        val face = Typeface.createFromAsset(assets , "font/nimbus.ttf")
        setTypefaces(face)
        //Ingresar y setear los elementos del XML a Kotlin, para poder usarlos
        //Ingresar el tipo de fuente determinada en la interfaz
        if (intent.getBooleanExtra("edit" , false)) {
            edit = true
            imgLay.visibility = View.VISIBLE
            if (edtMarca.text.toString() == "") {
                convertStringtoObject()
            }
            camLay.visibility = View.GONE
            enviar!!.setOnClickListener(this)
            takePhoto1.setOnClickListener(this)
            takePhoto2.setOnClickListener(this)
            takePhoto3.setOnClickListener(this)
        }

        DirectorioPropio = "${edtNumInventory!!.text.toString()}/"
        fullDirectorio = DirectorioRaiz + DirectorioPropio
        //Revisar si ya se habia seleccionado previamente un Inventario
    }

    fun setTypefaces(face : Typeface) {
        GlobalScope.launch {
            edtNumInventory.typeface = face
            edtDescription.typeface = face
            edtMarca.typeface = face
            edtModelo.typeface = face
            edtNumSerie.typeface = face
            edtNumMaquina.typeface = face
            edtExtra1.typeface = face
            edtExtra2.typeface = face
            edtExtra3.typeface = face
            edtExtra4.typeface = face
            edtExtra5.typeface = face
            edtNumPedimento.typeface = face
            edtFactura_UUID.typeface = face
            edtNumActivo.typeface = face
            edtLinea.typeface = face
            edtArea.typeface = face
            edtPlanta.typeface = face
            edtUbicPlanta.typeface = face
            edtDivision.typeface = face
            edtComentarios.typeface = face
            edtComenFotos.typeface = face
            countPhotos.typeface = face
            enviar!!.typeface = face
            edtDescription.afterTextChanged {
                checkData()
            }
            edtMarca.afterTextChanged {
                checkData()
            }
            edtModelo.afterTextChanged {
                checkData()
            }
            if (!edit) {
                edtNumSerie.afterTextChanged {
                    checkData()
                }
                edtNumMaquina.afterTextChanged {
                    checkData()
                }
            }
        }
    }

    private fun checkPermisos() : Boolean {
        if ((checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
            (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
            (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        ) {
            return true
        }

        if ((shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) ||
            (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
            (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
        ) {
            cargarDialogoRecomendacion()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                    Manifest.permission.READ_EXTERNAL_STORAGE ,
                    Manifest.permission.CAMERA
                ) , 100
            )
        }

        return false
    }

    private fun convertStringtoObject() {
        var objeto : String = intent.extras?.get("toEdit").toString()
        var materialFromServer = Gson().fromJson<Material>(objeto , Material::class.java)
        MaterialToEdit = materialFromServer
        log.debug("id_material" , materialFromServer.id_material)
        fillData()
    }

    private fun fillData() {
        edtComenFotos.visibility = View.GONE
        edtComenFotosLay.visibility = View.GONE
        edtNumInventory.setText(MaterialToEdit!!.num_inventario)
        edtNumInventory.isEnabled = false
        edtNumSerie.setText(MaterialToEdit!!.serie)
        edtDescription.setText(MaterialToEdit!!.descripcion_material)
        edtNumMaquina.setText(MaterialToEdit!!.num_maquina)
        edtNumActivo.setText(MaterialToEdit!!.num_activo)
        edtMarca.setText(MaterialToEdit!!.marca)
        edtModelo.setText(MaterialToEdit!!.modelo)
        edtNumPedimento.setText(MaterialToEdit!!.pedimento)
        edtExtra1.setText(MaterialToEdit!!.extra1)
        edtExtra2.setText(MaterialToEdit!!.extra2)
        edtExtra3.setText(MaterialToEdit!!.extra3)
        edtExtra4.setText(MaterialToEdit!!.extra4)
        edtExtra5.setText(MaterialToEdit!!.extra5)
        edtFactura_UUID.setText(MaterialToEdit!!.factura_uuid)
        edtLinea.setText(MaterialToEdit!!.linea)
        edtUbicPlanta.setText(MaterialToEdit!!.ubicacion_planta)
        edtPlanta.setText(MaterialToEdit!!.planta)
        edtArea.setText(MaterialToEdit!!.area)
        edtDivision.setText(MaterialToEdit!!.division)
        edtComentarios.setText(MaterialToEdit!!.comentarios)
        MaterialToEdit!!.edit = true
        edtComenFotos1.setText(MaterialToEdit!!.imgs!!.desc1)
        edtComenFotos2.setText(MaterialToEdit!!.imgs!!.desc2)
        edtComenFotos3.setText(MaterialToEdit!!.imgs!!.desc3)
        if (MaterialToEdit!!.imgs!!.img1 == "YES") {
            takePhoto1.backgroundTintList = getColorStateList(R.color.yellow)
        }
        if (MaterialToEdit!!.imgs!!.img2 == "YES") {
            takePhoto2.backgroundTintList = getColorStateList(R.color.yellow)
        }
        if (MaterialToEdit!!.imgs!!.img3 == "YES") {
            takePhoto3.backgroundTintList = getColorStateList(R.color.yellow)
        }
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

    private fun checkIdM() {
        id_m = pref!!.getInt("IdM" , 0)
        if (id_m == 0) {
            id_m++
            Toast.makeText(this , "Bienvenido, empiezas con el Caso $id_m" , Toast.LENGTH_LONG)
                .show()
        } else {
            id_m++
            Toast.makeText(this , "Bienvenido,empiezas con el Caso $id_m" , Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun saveIdM() {
        var editor : SharedPreferences.Editor = pref!!.edit()
        editor.putInt("IdM" , id_m)
        editor.commit()
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

    fun convertImageFileToBase64(imageFile : File) : String {
        return FileInputStream(imageFile).use { inputStream ->
            ByteArrayOutputStream().use { outputStream ->
                Base64OutputStream(outputStream , Base64.DEFAULT).use { base64FilterStream ->
                    inputStream.copyTo(base64FilterStream)
                    base64FilterStream.close()
                    //Log.d("BASE64",outputStream.toString())
                    outputStream.toString()
                }
            }
        }
    }

    override fun onClick(v : View) {
        if (v.id == R.id.takePhoto1) {
            number = 1
            tomarFotos()
        }
        if (v.id == R.id.takePhoto2) {
            number = 2
            tomarFotos()
        }
        if (v.id == R.id.takePhoto3) {
            number = 3
            tomarFotos()
        }
        if (v.id == R.id.enviar) {
            if (Online.isOnline()) {
                cargarWebService()
            }
        }
    }

    private fun getIdMaterial() : String {
        return edtNumInventory!!.text.toString() + "_" + "Caso $id_m" + "_" + contador
    }

    private fun safeDataToMaterial() {
        MaterialToEdit!!.empresa = pref!!.getInt(KEY_CLIENT,0).toString()
        MaterialToEdit!!.descripcion_material = edtDescription.text.toString()
        MaterialToEdit!!.num_maquina = edtNumMaquina.text.toString()
        MaterialToEdit!!.num_activo = edtNumActivo.text.toString()
        MaterialToEdit!!.marca = edtMarca.text.toString()
        MaterialToEdit!!.modelo = edtModelo.text.toString()
        MaterialToEdit!!.pedimento = edtNumPedimento.text.toString()
        MaterialToEdit!!.extra1 = edtExtra1.text.toString()
        MaterialToEdit!!.extra2 = edtExtra2.text.toString()
        MaterialToEdit!!.extra3 = edtExtra3.text.toString()
        MaterialToEdit!!.extra4 = edtExtra4.text.toString()
        MaterialToEdit!!.extra5 = edtExtra5.text.toString()
        MaterialToEdit!!.factura_uuid = edtFactura_UUID.text.toString()
        MaterialToEdit!!.linea = edtLinea.text.toString()
        MaterialToEdit!!.ubicacion_planta = edtUbicPlanta.text.toString()
        MaterialToEdit!!.planta = edtPlanta.text.toString()
        MaterialToEdit!!.area = edtArea.text.toString()
        MaterialToEdit!!.division = edtDivision.text.toString()
        MaterialToEdit!!.comentarios = edtComentarios.text.toString()
        MaterialToEdit!!.serie = edtNumSerie.text.toString()
        if(MaterialToEdit!!.imgs!!.desc1.length < edtComenFotos1.text.toString().length){
            MaterialToEdit!!.imgs!!.desc1 = edtComenFotos1.text.toString()
        }
        if(MaterialToEdit!!.imgs!!.desc2.length < edtComenFotos2.text.toString().length){
            MaterialToEdit!!.imgs!!.desc2 = edtComenFotos2.text.toString()
        }
        if(MaterialToEdit!!.imgs!!.desc3.length < edtComenFotos3.text.toString().length){
            MaterialToEdit!!.imgs!!.desc3 = edtComenFotos3.text.toString()
        }
        //MaterialToEdit!!.imgs!!.img_one = convertImageFileToBase64(File(MaterialToEdit!!.imgs!!.img_one))
    }

    private suspend fun sendToServer(mat : Material) : Result<InsertAnswer> =
        withContext(Dispatchers.IO) {
            var call : Call<InsertAnswer> = if (edit) {
                repository.service.update(mat)
            } else {
                repository.service.insert(mat)
            }
            return@withContext call.executeForResult()
        }

    private fun changeProgressBar(message : String , progress : ProgressDialog) {
        progress.setTitle("Cargando Datos")
        progress.setMessage("Procesando Datos")
        progress.show()
        progress.setCancelable(false)
        progress.setCanceledOnTouchOutside(false)
    }

    private fun cargarWebService() {
        progreso = ProgressBar(this)
        progreso!!.progress = 0
        val progressDialog = ProgressDialog(this@EditMaterial)
        progressDialog.setTitle("Cargando Datos")
        progressDialog.setMessage("Procesando Datos")
        progressDialog.show()
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
    enviar!!.isEnabled = false
    enviar!!.backgroundTintList = getColorStateList(R.color.buttonDisabled)
    safeDataToMaterial()
    Log.d("JSON" , Gson().toJson(MaterialToEdit).toString())
    GlobalScope.launch {
        when (val result = sendToServer(MaterialToEdit!!)) {
            is Result.Success -> {
                val answer = result.response.body()
                Log.d("ANSWER" , answer!!.message)
                log.debug("ANSWER " + answer.message)
                val request = result.response.raw().toString()
                Log.d("RAW" , request)
                log.debug("RAW Request" , request)
                if (answer.message!!.contains("id")) {
                    val id = answer.message!!.split(":").last()
                    Snackbar.make(
                        MainLayout ,
                        "Registro exitoso, el Objeto tiene el ID: " + id ,
                        Snackbar.LENGTH_LONG
                    ).show()
                    progressDialog.cancel()
                }
            }
            is Result.Failure -> {
                log.debug("ERROR!! " + result.error.toString())
                Snackbar.make(MainLayout , result.error.toString() , Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    } }
        //Log.d("JSON", Gson().toJson(mat))

    override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
        menuInflater.inflate(R.menu.menu , menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        when (item.itemId) {
            R.id.mnunuevo -> {
                var intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.mnuInventario -> {
                eraseInventory()
                buildDialog()
            }
            R.id.searchActivity -> {
                val intent = Intent(this , Search::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }



    private fun eraseInventory() {
        val editor = pref!!.edit()
        editor.remove("NumInv")
        editor.remove(KEY_CLIENT)
        editor.remove(KEY_INVENTORY)
        editor.remove("Fecha")
        editor.commit()
    }

    //Funcion que construye el dialog
    private fun buildDialog() {
        val listC = mutableListOf<Cliente>()
        val listE = mutableListOf<Inventario>()
        var item : MutableList<String> = mutableListOf()
        var url = "http://marketi.servehttp.com:80/EO-Plataform/Eo-service/"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(API::class.java)
        val call = service.Inventorys("ListaI")
        call.enqueue(object : Callback<List<Inventory>> {
            override fun onResponse(
                call : Call<List<Inventory>> ,
                response : Response<List<Inventory>>
            ) {
                Log.d("Response" , response.toString())
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        var data = response.body()
                        if (data != null) {
                            data.forEach {
                                Log.d("Response" , it.toString())
                            }
                            for (inventory in data) {
                                var cliente = Cliente(inventory.id_c , inventory.client)
                                if (!listC.contains(cliente)) {
                                    listC.add(cliente)
                                }
                                var inventory = Inventario(
                                    inventory.inventory ,
                                    inventory.date ,
                                    inventory.id_c
                                )
                                if (!listE.contains(inventory)) {
                                    listE.add(inventory)
                                }
                            }
                            for (i in listC.indices) {
                                item.add(listC[i].id_c + " " + listC[i].name)
                            }
                            var items = item.toTypedArray()
                            var builder = MaterialAlertDialogBuilder(this@EditMaterial)
                            builder.setTitle("Clientes")
                            builder.setSingleChoiceItems(
                                items ,
                                1 ,
                                DialogInterface.OnClickListener { dialog , which ->
                                    for (i in items.indices) {
                                        when (which == i) {
                                            true -> {
                                                pref!!.edit()
                                                    .putInt(KEY_CLIENT , listC[i].id)
                                                    .apply()
                                                buildDialog2(listC[i].id_c , listE)
                                                dialog.dismiss()
                                            }
                                        }
                                    }
                                })
                            var alert : androidx.appcompat.app.AlertDialog = builder.create()
                            alert.setCanceledOnTouchOutside(false)
                            alert.show()
                        }
                    }
                }
            }

            override fun onFailure(call : Call<List<Inventory>> , t : Throwable) {

                Snackbar.make(MainLayout , t.toString() , Snackbar.LENGTH_LONG).show()
            }

        })
    }

    private fun buildDialog2(id : String , listE : MutableList<Inventario>) {
        var item : MutableList<String> = mutableListOf()
        for (Inventario in listE) {
            if (Inventario.id_c == id) {
                item.add(Inventario.inventory + " " + Inventario.date)
            }
        }
        var items = item.toTypedArray()
        var builder = MaterialAlertDialogBuilder(this@EditMaterial)
        builder.setTitle("Inventarios")
        builder.setSingleChoiceItems(
            items ,
            1 ,
            DialogInterface.OnClickListener { dialog , which ->
                for (i in items.indices) {
                    when (which == i) {
                        true -> {
                            listE.forEach {
                                var string = it.inventory + " " + it.date
                                if (items[i] == string) {
                                    edtNumInventory!!.setText(it.inventory)
                                    edtNumInventory!!.isEnabled = false
                                    dialog.dismiss()
                                }
                            }

                        }
                    }
                }
            })
        var alert : androidx.appcompat.app.AlertDialog = builder.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    override fun onResume() {
        super.onResume()
        saveIdM()
        if (contador == 3) {
            takePhoto!!.isEnabled = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveIdM()
    }

    private fun checkData() {
        var map : Map<TextInputLayout , TextInputEditText> =
            mapOf<TextInputLayout , TextInputEditText>(
                Pair(edtDescriptionLay , edtDescription) ,
                Pair(edtMarcaLay , edtMarca) ,
                Pair(edtModeloLay , edtModelo)
            )
        var errors = 0
        map.forEach { (t , u) ->
            if (u.nonEmpty()) {
                t.isErrorEnabled = false
                t.boxStrokeColor = getColor(R.color.colorOK)
            } else {
                t.error = getString(R.string.blank)
                errors++
            }
        }
        if (errors == 0) {
            takePhoto!!.isEnabled = true
            takePhoto!!.backgroundTintList = getColorStateList(R.color.btnFoto)
            enviar!!.isEnabled = true
            enviar!!.backgroundTintList = getColorStateList(R.color.colorButton)
            enviar!!.setTextColor(getColor(R.color.white))
        } else
            enviar!!.backgroundTintList = getColorStateList(R.color.buttonDisabled)
    }

    private fun onRefresh() {
        if (edit) {
            edit = false
            imgLay.visibility = View.GONE
            edtComenFotosLay.visibility = View.VISIBLE
            edtComenFotos.visibility = View.VISIBLE
            camLay.visibility = View.VISIBLE
            takePhoto.setOnClickListener(this)
        }
        else{
            contador = 0
            countPhotos.text = "Fotos: $contador"
            deleteImages()
            MaterialToEdit = null
            edit = false
        }
        finish()
        startActivity(intent)

    }

    private fun deleteImages() {
        fotos = null
        fotos = mutableListOf()
    }

    private fun createImageFile() : File {
        var nombre = getIdMaterial()
        val storageDir : File = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(nombre , ".jpg" , storageDir).apply {
            currentPhotoPath = absolutePath
            Log.d("PICTURE" , absolutePath)
        }
    }

    private fun tomarFotos() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile : File? = try {
                    createImageFile()

                } catch (ex : IOException) {
                    ex.printStackTrace()
                    log.debug(ex.printStackTrace().toString())
                    null
                }
                photoFile?.also {
                    val photoURI : Uri = FileProvider.getUriForFile(
                        this ,
                        "com.chava.inventorymdys.fileprovider" , it
                    )
                    uri = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT , photoURI)
                    startActivityForResult(takePictureIntent , REQUEST_TAKE_PHOTO)
                }
            }
        }
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath!!)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

    override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if (resultCode == RESULT_OK) {
            GlobalScope.launch {
                val compressedImageFile =
                    Compressor.compress(applicationContext , File(currentPhotoPath!!)) {
                        default()
                        destination(File(currentPhotoPath!!))
                    }
                val imagen = compressedImageFile.absolutePath
                val path : String = currentPhotoPath!!
                if (edit) {
                    when (number) {
                        1 -> {
                            MaterialToEdit!!.imgs!!.img1 = convertImageFileToBase64(File(path))
                            MaterialToEdit!!.imgs!!.desc1 = edtComenFotos1.text.toString()
                        }
                        2 -> {
                            MaterialToEdit!!.imgs!!.img2 = convertImageFileToBase64(File(path))
                            MaterialToEdit!!.imgs!!.desc2 = edtComenFotos2.text.toString()
                        }
                        3 -> {
                            MaterialToEdit!!.imgs!!.img3 = convertImageFileToBase64(File(path))
                            MaterialToEdit!!.imgs!!.desc3 = edtComenFotos3.text.toString()
                        }
                    }
                } else {
                    val img = Imagen(string!! , convertImageFileToBase64(File(path)) , path , id_m)
                    fotos!!.add(img)
                    Log.d("SIZE" , fotos!!.size.toString())
                    log.debug("SIZE " + fotos!!.size)
                }
            }
        }
        if (resultCode == RESULT_CANCELED) {
            contador--
            countPhotos.text = "Fotos : $contador"
        }

    }

    private fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
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
