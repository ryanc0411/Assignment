package com.example.assignment.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.Callback.IRecyclerItemClickListener
import com.example.assignment.EventBus.PopularFoodItemClick
import com.example.assignment.Model.PopularGategoryModel
import com.example.assignment.R
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus

class MyPopularCategoriesAdapter(internal var context: Context,
                                 internal  var  popularGategoryModels: List<PopularGategoryModel>):
    RecyclerView.Adapter<MyPopularCategoriesAdapter.MyViewHolder>()
{
    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var category_name:TextView?=null
        var category_image:CircleImageView?=null

        internal var listener : IRecyclerItemClickListener?=null

        fun setListner(listener: IRecyclerItemClickListener)
        {
            this.listener = listener
        }

        init {
            category_name = itemView.findViewById(R.id.txt_category_name) as TextView
            category_image = itemView.findViewById(R.id.category_image) as CircleImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
           listener!!.onItemClick(p0!!,adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_popular_categories_iem,parent,false))
    }

    override fun getItemCount(): Int {
        return popularGategoryModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

       Glide.with(context).load(popularGategoryModels.get(position).image).into(holder.category_image!!)
        holder.category_name!!.setText(popularGategoryModels.get(position).name)

        //Event
        holder.setListner(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                EventBus.getDefault().postSticky(PopularFoodItemClick(popularGategoryModels[pos]))
            }
            
        })
    }
}