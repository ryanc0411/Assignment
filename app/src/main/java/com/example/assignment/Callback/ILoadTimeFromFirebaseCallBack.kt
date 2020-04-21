package com.example.assignment.Callback

import com.example.assignment.Model.Order

interface ILoadTimeFromFirebaseCallBack {
    fun onLoadTimeSuccess(order: Order,estimatedTimeMs:Long)
    fun onLoadTimeFailed(message:String)
}