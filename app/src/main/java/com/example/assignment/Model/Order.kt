package com.example.assignment.Model

import com.example.assignment.database.Entity.CartItem

class Order {
    var userId:String?=null
    var userName:String?=null
    var shippingAddress:String?=null
    var transactionId:String?=null
    var totalPayment :Double = 0.toDouble()
    var finalPayment :Double = 0.toDouble()
    var isCod:Boolean = false
    var discount:Int =0
    var cartItemList:List<CartItem>?=null
    var createDate:Long?=null
    var orderNumber:String?=null
    var orderStatus:Int=0

}