package com.example.assignment.database.Entity

class Canteen(
    val CanteenID: Int,
    val CanteenImage: String,
    val CanteenName: String,
    val ShopName: String,
    val ShopBusinessHours: String,
    val staffID: String

) {

    constructor() : this(0, "", "", "", "", "" )

}