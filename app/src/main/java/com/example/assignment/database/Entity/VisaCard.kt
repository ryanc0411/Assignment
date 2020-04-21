package com.example.assignment.database.Entity

class VisaCard(

    val nameOnCard: String,
    val cardNumber: String,
    val mmyy: String,
    val cvc: String

) {
    constructor() : this("","","","")
}