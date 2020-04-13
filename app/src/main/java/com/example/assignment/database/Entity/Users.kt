package com.example.assignment.database.Entity

class Users(
    val userID: String,
    val username: String,
    val userRole: String
) {
    constructor() : this("","", "")
}