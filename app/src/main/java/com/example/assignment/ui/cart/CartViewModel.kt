package com.example.assignment.ui.cart

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assignment.database.Entity.CartDataSource
import com.example.assignment.database.Entity.CartDatabase
import com.example.assignment.database.Entity.CartItem
import com.example.assignment.database.Entity.LocalCartDataSource
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CartViewModel : ViewModel()  {

    private val compositeDisposable:CompositeDisposable
    private var cartDataSource:CartDataSource?=null
    private var  mAuth = FirebaseAuth.getInstance()
    private var user = mAuth.currentUser
    private var mutableLiveDataCartItem:MutableLiveData<List<CartItem>>?=null

    init {
        compositeDisposable = CompositeDisposable()
    }

    fun initCartdataSource(context : Context){
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }

  fun getMutableLiveDataCartItem():MutableLiveData<List<CartItem>>{
      if(mutableLiveDataCartItem == null)
          mutableLiveDataCartItem = MutableLiveData()
      getCartItem()
      return mutableLiveDataCartItem!!
  }

    private fun getCartItem(){
        compositeDisposable.addAll(cartDataSource!!.getAllCart(user!!.uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({cartItem ->

                mutableLiveDataCartItem!!.value = cartItem
            },{t: Throwable? -> mutableLiveDataCartItem!!.value = null}))
    }

    fun onStop(){
        compositeDisposable.clear()
    }

}
