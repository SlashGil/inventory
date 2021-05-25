package com.chava.inventorymdys.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import com.chava.inventorymdys.Activities.MainActivity
import com.chava.inventorymdys.Entity.SearchItem
import com.chava.inventorymdys.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.item_list.view.*

class SearchAdapter(listSearch: ArrayList<SearchItem>):
RecyclerView.Adapter<SearchAdapter.SearchHolder>(){
    var list = listSearch
    private var listener: ((SearchItem) -> Unit)? = null
    class SearchHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var txtCase = itemView.findViewById<MaterialTextView>(R.id.caseNumberTxt)
        var txtEnterprise = itemView.findViewById<MaterialTextView>(R.id.companyTxt)
        var txtManufacturer = itemView.findViewById<MaterialTextView>(R.id.manufacturerTxt)
        var btnOk = itemView.findViewById<MaterialButton>(R.id.btnOk)
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): SearchHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_list,parent,false)
        var layoutParams: RecyclerView.LayoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        view.layoutParams = layoutParams
        return SearchHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnItemClickListener(f: (SearchItem) -> Unit){
        listener = f
    }

    override fun onBindViewHolder(holder: SearchHolder , position: Int) {
        holder.txtCase.text =  "ID Material: " + list[position].num_caso.toString()
        holder.txtEnterprise.text = "Cliente: ${list[position].empresa}"
        holder.txtManufacturer.text = "Marca del activo: ${list[position].marca}"
        holder.btnOk.setOnClickListener {
            listener?.invoke(list[position])
        }
    }
}