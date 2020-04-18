package com.example.assignment.Callback

import com.example.assignment.Model.PopularGategoryModel

interface IPopularLoadCallback {
    fun onPopularLoadSuccess(popularModelList:List<PopularGategoryModel>)
    fun onPopularLoadFailed(message:String)
}