package com.example.assignment.Callback

import com.example.assignment.Model.Order

interface ILoadOrderCallbackListener {
    fun onLoadOrderSucess(orderList:List<Order>)
}