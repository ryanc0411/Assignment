package com.example.assignment.Callback

import com.example.assignment.Model.BestDealModel

interface IBestDealLoadCallback {
    fun onBestDealLoadSuccess(bestDealList:List<BestDealModel>)
    fun onBestDealLoadFailed(message:String)
}