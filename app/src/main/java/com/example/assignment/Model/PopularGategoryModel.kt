package com.example.assignment.Model

class PopularGategoryModel {
    var food_id:String?=null
    var menu_id:String?=null
    var name:String?=null
    var image:String?=null

    constructor()
    constructor(food_id:String?,menu_is:String?,name:String?,image:String?){
        this.food_id = food_id
        this.menu_id = menu_id
        this.name = name
        this.image =  image
    }
}