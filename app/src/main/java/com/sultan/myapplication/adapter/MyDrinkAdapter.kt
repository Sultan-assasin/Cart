package com.sultan.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sultan.myapplication.DrinkModel.DrinkModel
import com.sultan.myapplication.R
import java.lang.StringBuilder

class MyDrinkAdapter(
    private val context: Context,
    private val list: List<DrinkModel>
) : RecyclerView.Adapter<MyDrinkAdapter.MyDrinkViewHolder>() {

    class MyDrinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = null
        var txtName: TextView? = null
        var txtPrice: TextView? = null

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDrinkViewHolder {
        return MyDrinkViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_drink_item, parent,false))
    }

    override fun onBindViewHolder(holder: MyDrinkViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].image)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder("$").append(list[position].price)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}