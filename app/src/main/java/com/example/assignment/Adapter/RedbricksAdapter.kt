//package com.example.assignment.Adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.assignment.Common.Common
//import com.example.assignment.Model.RedbricksModel
//import com.example.assignment.R
//
//class RedbricksAdapter ( internal var context:Context,
//                internal var redbricksList: List<RedbricksModel>) :
//        RecyclerView.Adapter<RedbricksAdapter.MyviewHolder>(){
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedbricksAdapter.MyviewHolder {
//                return MyviewHolder(LayoutInflater.from(context).inflate(R.layout.layout_redbrick_item,parent, false))
//        }
//
//        override fun getItemCount(): Int {
//               return redbricksList.size
//        }
//
//
//        override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
//                Glide.with(context).load(redbricksList.get(position).image).into(holder.red_image!!)
//                holder.red_name!!.setText(redbricksList.get(position).name)
//        }
//
//        override fun getItemViewType(position: Int): Int {
//                return if(redbricksList.size==1)
//                        Common.DEFAULT_COLUMN_COUNT
//                else{
//                        if (redbricksList.size% 2 == 0)
//                                Common.DEFAULT_COLUMN_COUNT
//                        else
//                                if(position>1 && position == redbricksList.size-1) Common.FULL_WIDTH_COLUMN else Common.DEFAULT_COLUMN_COUNT
//                }
//
//        }
//
//        inner class MyviewHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
//
//                var red_name:TextView?=null
//                var red_image:ImageView?=null
//
//                init
//                {
//                        red_name = itemview.findViewById(R.id.redbricks_name) as TextView
//                        red_image = itemview.findViewById(R.id.redbricks_image) as ImageView
//                }
//        }
//
//
//}