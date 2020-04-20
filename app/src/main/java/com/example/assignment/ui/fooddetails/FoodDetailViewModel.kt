package com.example.assignment.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assignment.Common.Common
import com.example.assignment.Model.FoodModel
import com.example.assignment.Model.RatingModel

class FoodDetailViewModel : ViewModel()  {

    private var mutableLiveDataFood : MutableLiveData<FoodModel>?=null
    private var mutableLiveDataComment : MutableLiveData<RatingModel>?=null

    init{
        mutableLiveDataComment = MutableLiveData()
    }

    fun getMutableLiveDataFood():MutableLiveData<FoodModel>{
        if (mutableLiveDataFood==null)
            mutableLiveDataFood = MutableLiveData()
        mutableLiveDataFood!!.value = Common.foodSelected
        return mutableLiveDataFood!!
    }

    fun getMutableLiveDataComment():MutableLiveData<RatingModel>{
        if (mutableLiveDataComment==null)
            mutableLiveDataComment = MutableLiveData()
        return mutableLiveDataComment!!
    }

    fun setRatingModel(ratingModel: RatingModel) {
        if(mutableLiveDataComment!=null)
            mutableLiveDataComment!!.value = (ratingModel)

    }

    fun setFoodModel(foodModel: FoodModel) {
        if(mutableLiveDataFood != null)
            mutableLiveDataFood!!.value = foodModel
    }


}
