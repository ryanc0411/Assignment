package com.example.assignment.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.Common.Common
import com.example.assignment.Model.Order
import com.example.assignment.Model.Order_seller_Model
import com.example.assignment.R
import java.text.SimpleDateFormat

class MySellerOrderAdapter(internal var context: Context,
                           internal var orderList: MutableList<Order_seller_Model>) :
    RecyclerView.Adapter<MySellerOrderAdapter.MyViewHolder>() {


    lateinit var simpleDateFormat:SimpleDateFormat

    init {
       simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var seller_txt_date: TextView?=null
        var seller_txt_order_number: TextView?=null
        var seller_txt_order_status: TextView?=null
        var seller_txt_num_item: TextView?=null
        var seller_txt_buyer_name: TextView?=null
        var seller_img_food_image: ImageView?=null

        init {
            seller_txt_date = itemView.findViewById(R.id.seller_txt_date) as TextView
            seller_txt_order_number = itemView.findViewById(R.id.seller_txt_order_number) as TextView
            seller_txt_order_status = itemView.findViewById(R.id.seller_txt_order_status) as TextView
            seller_txt_num_item = itemView.findViewById(R.id.seller_txt_num_item) as TextView
            seller_txt_buyer_name = itemView.findViewById(R.id.seller_txt_buyer_name) as TextView
            seller_img_food_image = itemView.findViewById(R.id.seller_img_food_image) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       return MyViewHolder(LayoutInflater.from(context)
           .inflate(R.layout.seller_fragment_order_item,parent,false))
    }

    override fun getItemCount(): Int {
       return orderList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(orderList[position].cartItemList!![0].foodImage)
            .into(holder.seller_img_food_image!!)
        holder.seller_txt_order_number!!.setText(orderList[position].key)
        Common.setSpanStringColor("Order date ",simpleDateFormat.format(orderList[position].createDate),
        holder.seller_txt_date, Color.parseColor("#333639"))

        Common.setSpanStringColor("Order Status ",Common.convertStatusToString(orderList[position].orderStatus),
            holder.seller_txt_order_status, Color.parseColor("#005758"))

        Common.setSpanStringColor("Num of item ", if(orderList[position].cartItemList == null)"0"
            else orderList[position].cartItemList!!.size.toString(),
            holder.seller_txt_num_item, Color.parseColor("#00574B"))

        Common.setSpanStringColor("Name of buyer ", orderList[position].userName,
            holder.seller_txt_buyer_name, Color.parseColor("#006061"))
    }

    fun getItemAtPosition(pos:Int): Order_seller_Model{
        return orderList[pos]
    }

    fun removeItem(pos: Int) {
        orderList.removeAt(pos)
    }


}