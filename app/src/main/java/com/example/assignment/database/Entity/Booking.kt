package com.example.assignment.database.Entity

class Booking(
    val bookingID: Int,
    val bookingDate: String,
    val ticketQuantity: Int,
    val discountRate: Double,
    val themeParkID: Int,
    val userID: String
) {
    constructor(): this(0,"",0,0.0,0,"")
}