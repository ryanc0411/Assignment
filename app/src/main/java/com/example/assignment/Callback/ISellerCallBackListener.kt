package com.example.assignment.Callback

import com.example.assignment.Model.Order_seller_Model

interface ISellerCallBackListener {
    fun onSellerOrderLoadSuccess(Order_seller_Model: List<Order_seller_Model>)
    fun onSellerOrderLoadFailed(message:String)
}