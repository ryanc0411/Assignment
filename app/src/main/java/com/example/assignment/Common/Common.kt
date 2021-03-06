package com.example.assignment.Common

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
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

    fun getDateOfWeek(i: Int): String {
        when(i){
            1 -> return "Monday"
            2 -> return "Tuesday"
            3 -> return "Wednesday"
            4 -> return "Thursday"
            5 -> return "Friday"
            6 -> return "Saturday"
            7 -> return "Sunday"
            else -> return "Unk"
        }

    }

    fun convertStatusToText(orderStatus: Int): String {
        when(orderStatus)
        {
            -1 -> return "Cancelled"
            0 -> return "Placed"
            1 -> return "Shipping"
            2 -> return "Shipped"
            else -> return "Unk"
        }
    }


    fun setSpanString(welcome: String, name: String?, txtUser: TextView?) {
        val builder = SpannableStringBuilder()
        builder.append(welcome)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan,0,name!!.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        txtUser!!.setText(builder,TextView.BufferType.SPANNABLE)

    }


    fun setSpanStringColor(welcome: String, name: String?, txtUser: TextView?, color: Int) {
        val builder = SpannableStringBuilder()
        builder.append(welcome)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan,0,name!!.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtSpannable.setSpan(ForegroundColorSpan(color),0,name!!.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        txtUser!!.setText(builder,TextView.BufferType.SPANNABLE)

    }

    fun convertStatusToString(orderStatus: Int): String? =
        when(orderStatus)
        {
            -1 -> "Cancel"
            0-> "Placed"
            1 -> "Shipping"
            2-> "Shipped"
            else -> "Error"
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