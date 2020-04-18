package com.example.assignment.Callback

import com.example.assignment.Model.CategoryModel

interface ICategoryCallbackListener {
    fun onCategoryLoadSuccess(categoryList :List<CategoryModel>)
    fun onCategoryLoadFailed(message:String)
}