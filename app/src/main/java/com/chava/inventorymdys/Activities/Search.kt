package com.chava.inventorymdys.Activities

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import com.chava.inventorymdys.Adapters.SearchAdapter
import com.chava.inventorymdys.Entity.*
import com.chava.inventorymdys.R
import com.chava.inventorymdys.interfaces.API
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tiper.MaterialSpinner
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Search : AppCompatActivity() {
    var inventario:String = ""
    var client:Int = 0
    var pref: SharedPreferences? = null
    var list = arrayOf("Num Activo","Num Pedimento", "ID Material")
    val listC = mutableListOf<Cliente>()
    val KEY_CLIENT = "ID_Cliente"
    val listE = mutableListOf<Inventario>()
    var searchTypeText: String = ""
    var item: MutableList<String> = mutableListOf()
    private val listener by lazy {
        object : MaterialSpinner.OnItemSelectedListener {
            override fun onItemSelected(parent: MaterialSpinner , view: View? , position: Int , id: Long) {
                Log.v("MaterialSpinner", "onItemSelected parent=${parent.id}, position=$position")
                parent.focusSearch(View.FOCUS_UP)?.requestFocus()
                searchTypeText = list[position]
                if(searchTypeText== "ID Material"){
                    buildDialog()
                }
            }

            override fun onNothingSelected(parent: MaterialSpinner) {
                Log.v("MaterialSpinner", "onNothingSelected parent=${parent.id}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        pref = getSharedPreferences("Usuario" , MODE_PRIVATE)
        results_rv.stopLoading()
        ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list).let{
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            searchType.apply {
                adapter = it
                onItemSelectedListener = listener
                onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    Log.v("MaterialSpinner", "onFocusChange hasFocus=$hasFocus")
                }
            }
        }
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(s: String): Boolean {
                // make a server call
                return true
            }

            override fun onQueryTextSubmit(s: String): Boolean {
                var call: Call<List<SearchItem>>
                results_rv.visibility = View.VISIBLE
                search_not.visibility = View.GONE
                results_rv.startLoading()
                var url ="http://marketi.servehttp.com:80/EO-Plataform/Eo-service/"
                val retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service =retrofit.create(API::class.java)
                if(searchTypeText == "ID Material")
                {
                    val searchQuery = Search(changeQueryText(searchTypeText),s,pref!!.getString("Inventario","NA")!!,pref!!.getInt("ID_CLIENTE",0)!!.toString())
                    call = service.search(searchQuery)
                }
                else
                {
                    val searchQuery = Search(searchTypeText,s)
                    call = service.search(searchQuery)
                }

                call.enqueue(object: Callback<List<SearchItem>>
                {
                    override fun onResponse(call: Call<List<SearchItem>> , response: Response<List<SearchItem>>)
                    {
                        Log.d("Response",response.toString())
                        if(response.isSuccessful)
                        {
                            if(response.code()==200) {
                                var data = response.body()
                                Log.d("Search" , data.toString())
                                if (data!!.isNotEmpty()) {

                                    if (data[0].msj.isEmpty()) {
                                        var adapter = SearchAdapter(data!! as ArrayList<SearchItem>)
                                        adapter.setOnItemClickListener {

                                            var gson = Gson()
                                            var jsonString = gson.toJson(it.objeto)
                                            startActivity(Intent(
                                                applicationContext ,
                                                EditMaterial::class.java
                                            ).apply
                                            {
                                                this.putExtra("edit" , true)

                                                this.putExtra("toEdit" , jsonString)
                                            })
                                        }
                                        results_rv.adapter = adapter
                                        results_rv.stopLoading()
                                    }
                                    if (data[0].msj.isNotEmpty()){
                                        results_rv.visibility = View.GONE
                                        search_not.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<SearchItem>> , t: Throwable) {
                        Snackbar.make(results_rv,t.toString(), Snackbar.LENGTH_LONG).show()
                        Log.d("Error!!",t.toString())
                    }

                })
                return true
            }
        })
    }
    private fun changeQueryText(text: String): String {
        when (text){
            "Num Activo"->{
                return "Num Activo"
            }
            "Num Pedimento"->{
                return "Num Pedimento"
            }
            "ID Material"->{
                return "id_material"
            }
        }
        return ""
    }
    private fun buildDialog() {

        var url ="http://marketi.servehttp.com:80/EO-Plataform/Eo-service/"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service =retrofit.create(API::class.java)
        val call = service.Inventorys("ListaI")
        call.enqueue(object: Callback<List<Inventory>> {
            override fun onResponse(
                call: Call<List<Inventory>> ,
                response: Response<List<Inventory>>
            ) {
                Log.d("Response",response.toString())
                if(response.isSuccessful){
                    if(response.code() == 200){
                        var data = response.body()
                        if (data != null) {
                            data.forEach {
                                Log.d("Response",it.toString())
                            }
                            for(inventory in data){
                                var cliente = Cliente(inventory.id_c,inventory.client)
                                if(!listC.contains(cliente)){
                                    listC.add(cliente)
                                }
                                var inventory = Inventario(inventory.inventory,inventory.date,inventory.id_c)
                                if(!listE.contains(inventory)){
                                    listE.add(inventory)
                                }
                            }
                            for (i in listC.indices) {
                                item.add(listC[i].id_c + " " + listC[i].name)
                            }
                            var items = item.toTypedArray()
                            var builder = MaterialAlertDialogBuilder(this@Search)
                            builder.setTitle("Clientes")
                            builder.setSingleChoiceItems(
                                items ,
                                1 ,
                                DialogInterface.OnClickListener { dialog , which ->
                                    for( i in items.indices){
                                        when(which == i){
                                            true -> {
                                                pref!!.edit().putInt(KEY_CLIENT,listC[i].id.toInt()).commit()
                                                dialog.dismiss()
                                                buildDialog2(listC[pref!!.getInt(KEY_CLIENT,0)].id_c,listE)
                                            }
                                        }
                                    }
                                })
                            var alert: androidx.appcompat.app.AlertDialog = builder.create()
                            alert.setCanceledOnTouchOutside(false)
                            alert.show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Inventory>> , t: Throwable) {

                Snackbar.make(results_rv,t.toString(), Snackbar.LENGTH_LONG).show()
            }

        })
    }
    private fun buildDialog2(id: String , listE: MutableList<Inventario>) {
        var item: MutableList<String> = mutableListOf()
        for (Inventario in listE){
            if(Inventario.id_c == id) {
                item.add(Inventario.inventory  )
            }
        }
        var items = item.toTypedArray()
        var builder = MaterialAlertDialogBuilder(this@Search)
        builder.setTitle("Inventarios")
        builder.setSingleChoiceItems(
            items ,
            1 ,
            DialogInterface.OnClickListener { dialog , which ->
                for( i in items.indices){
                    when(which == i){
                        true -> {
                            listE.forEach {
                                var string = it.inventory
                                if(items[i] == string){
                                    pref!!.edit().putString("Inventario",items[i]).commit()
                                    dialog.dismiss()
                                }
                            }

                        }
                    }
                }
            })
        var alert: androidx.appcompat.app.AlertDialog = builder.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }
}