package com.example.BukuHarian

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_raw.view.*

class ItemAdapter(val context: Context, val items: ArrayList<EmpModel>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(){
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val linlay = view.Linlay
        val tvtanggal = view.Tv_date
        val tvdescription = view.Tv_desc
//        val ivEdit = view.iv_edit
        val ivDelete = view.iv_delete
        val ivDetail = view.iv_detail
    }
    // method untuk membuat view holder
    // inflate = memunculkan data
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_raw, parent, false
            )
        )
    }

    // memasukkan data ke view holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.tvtanggal.text = item.time
        holder.tvdescription.text = item.desc

        if(position % 2 == 0) {
            holder.linlay.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }else{
            holder.linlay.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        }

        holder.ivDelete.setOnClickListener{
            if (context is MainActivity)
            context.deleteRecordAlertDialog(item)
        }
//        holder.ivEdit.setOnClickListener{
//            if(context is MainActivity){
//                context.updateRecordDialog(item)
//            }
//        }
        holder.ivDetail.setOnClickListener{
            if (context is MainActivity) {
                context.showDetail(item)
            }
        }
    }


    override fun getItemCount(): Int {
        return items.size
    }
}