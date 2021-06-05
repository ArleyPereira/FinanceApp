package com.br.apsmobile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.br.apsmobile.R
import com.br.apsmobile.helper.GetMask
import com.br.apsmobile.model.Moviment
import kotlinx.android.synthetic.main.layout_item_movimento.view.*
import java.sql.Timestamp
import java.util.*

class AdapterMoviments(
    private var movimentList: MutableList<Moviment> = mutableListOf(),
    private val context: Context
) : RecyclerView.Adapter<AdapterMoviments.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_movimento, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val moviment = movimentList[position]

        holder.text_description_movement.text = moviment.description

        holder.text_date_movement.text = moviment.date?.let { GetMask.getDate(it.time, 1) }

        if(moviment.type == "gastos"){
            holder.text_value_movement.text = context.getString(R.string.text_value_negative, GetMask.getValue(moviment.value))
            holder.img_bg_type.setImageResource(R.drawable.bg_gastos)
            holder.img_type.setImageResource(R.drawable.ic_arrow_up)
        }else {
            holder.text_value_movement.text = context.getString(R.string.text_value, GetMask.getValue(moviment.value))
            holder.img_bg_type.setImageResource(R.drawable.bg_ganhos)
            holder.img_type.setImageResource(R.drawable.ic_arrow_down)
        }

    }

    override fun getItemCount() = movimentList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val img_bg_type: ImageView = itemView.img_bg_type
        val img_type: ImageView = itemView.img_type
        val text_description_movement: TextView = itemView.text_description_movement
        val text_date_movement: TextView = itemView.text_date_movement
        val text_value_movement: TextView = itemView.text_value_movement
    }
}