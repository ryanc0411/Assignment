package com.example.assignment.Model

import com.example.assignment.database.Entity.Seller_CartItem


class Order_seller_Model {
    var key:String?=null
    var userId:String?=null
    var userName:String?=null
    var shippingAddress:String?=null
    var transactionId:String?=null
    var totalPayment :Double = 0.toDouble()
    var finalPayment :Double = 0.toDouble()
    var isCod:Boolean = false
    var discount:Int =0
    var cartItemList:List<Seller_CartItem>?=null
    var createDate:Long?=null
    var orderNumber:String?=null
    var orderStatus:Int=0

}


