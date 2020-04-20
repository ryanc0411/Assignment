package com.example.assignment.ui.foodlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assignment.Common.Common
import com.example.assignment.Model.FoodModel

class FoodListViewModel : ViewModel()  {

  private var mutableFoodListLiveData : MutableLiveData<List<FoodModel>>?=null
    fun getMutableFoodModelListData() : MutableLiveData<List<FoodModel>>{
        if(mutableFoodListLiveData == null)
            mutableFoodListLiveData = MutableLiveData()
        mutableFoodListLiveData!!.value = Common.categorySelected!!.foods
        return mutableFoodListLiveData!!

    }
}
