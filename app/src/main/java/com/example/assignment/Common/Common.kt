package com.example.assignment.Common

import com.example.assignment.Model.AddonModel
import com.example.assignment.Model.CategoryModel
import com.example.assignment.Model.FoodModel
import com.example.assignment.Model.SizeModel
import com.example.assignment.database.Entity.Users
import java.lang.StringBuilder
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.random.Random


object Common {
    fun formatPrice(price: Double): String {
        if(price!=0.toDouble())
        {
            val df = DecimalFormat("#,##0.00")
            df.roundingMode = RoundingMode.HALF_UP
            val finalPrice = StringBuilder(df.format(price)).toString()
            return finalPrice.replace(".",".")
        }
        else
            return "0.00"
    }

    fun calculateExtraPrice(
        userSelectedSize:SizeModel?,
        userSelectedAddon:MutableList<AddonModel>?
    ): Double{
        var result:Double=0.0
        if(userSelectedSize==null&&userSelectedAddon==null)
        {
            return 0.0

        }else if(userSelectedSize==null)
        {
            for(addonModel in userSelectedAddon!!)
                result += addonModel.price!!.toDouble()
            return result

        }else if(userSelectedAddon==null)
        {
            result = userSelectedSize!!.price.toDouble()
            return result
        }
        else{
            result = userSelectedSize!!.price.toDouble()
            for(addonModel in userSelectedAddon!!)
                result += addonModel.price!!.toDouble()

            return result
        }
    }

    fun createOrderNumber(): String {
        return StringBuilder().append(System.currentTimeMillis()).append(abs(Random.nextInt()))
            .toString()

    }

    val ORDER_REF: String="Order"
    val COMMENT_REF: String="Comments"
    var foodSelected: FoodModel? = null
    var categorySelected: CategoryModel?=null
    val CATEGORY_REF: String="Category"
    val BEST_DEAL_REF: String="BestDeals"
    val POPULAR_REF: String="MostPopular"
    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int=0
    var homeAddress : String?=null
    var userName : String?=null

    var isValid : Int=0

}