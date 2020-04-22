package com.example.assignment.seller

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assignment.Callback.ISellerCallBackListener
import com.example.assignment.Common.Common
import com.example.assignment.Model.Order_seller_Model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class Seller_OderViewModel: ViewModel(), ISellerCallBackListener {


 private val orderModelList = MutableLiveData<List<Order_seller_Model>>()
    val messageError = MutableLiveData<String>()
    private val orderCallBackListener:ISellerCallBackListener


  init {
      orderCallBackListener=this
  }

  fun getOrderModelList() :MutableLiveData<List<Order_seller_Model>>{
    loadOrder(0)
    return orderModelList
  }

  public fun loadOrder(status: Int) {
      val tempList : MutableList<Order_seller_Model> = ArrayList()
    val orderRef = FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
      .orderByChild("orderStatus")
      .equalTo(status.toDouble())
    orderRef.addListenerForSingleValueEvent(object:ValueEventListener{
      override fun onCancelled(p0: DatabaseError) {
        orderCallBackListener.onSellerOrderLoadFailed(p0.message)
      }

      override fun onDataChange(p0: DataSnapshot) {
        for(itemSnapShot in p0.children){
          val orderModel = itemSnapShot.getValue(Order_seller_Model::class.java)
          orderModel!!.key = itemSnapShot.key
          tempList.add(orderModel)

        }
        orderCallBackListener.onSellerOrderLoadSuccess(tempList)
      }


    })

  }

  override fun onSellerOrderLoadSuccess(Order_seller_Model: List<Order_seller_Model>) {
   if(Order_seller_Model.size >= 0)
   {
       Collections.sort(Order_seller_Model) {t1,t2 ->
       if (t1.createDate!! < t2.createDate!!)return@sort -1
           if(t1.createDate == t2.createDate) 0 else 1


       }


   }
       orderModelList.value = Order_seller_Model
   }


  override fun onSellerOrderLoadFailed(message: String) {
   messageError.value = message
  }

}