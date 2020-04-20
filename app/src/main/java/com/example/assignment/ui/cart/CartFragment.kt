package com.example.assignment.ui.cart

import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.MyCartAdapter
import com.example.assignment.Callback.IMyButtonCallback
import com.example.assignment.Common.Common
import com.example.assignment.Common.MySwipeHelper
import com.example.assignment.EventBus.CountCartEvent
import com.example.assignment.EventBus.HideFABCart
import com.example.assignment.EventBus.UpdateItemInCart
import com.example.assignment.R
import com.example.assignment.database.Entity.CartDataSource
import com.example.assignment.database.Entity.CartDatabase
import com.example.assignment.database.Entity.LocalCartDataSource
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder


class CartFragment : Fragment() {

   private var cartDataSource:CartDataSource?=null
    private var compositeDisposable:CompositeDisposable = CompositeDisposable()
    private var recyclerViewState: Parcelable?=null
    private lateinit var cartViewModel: CartViewModel
    val mAuth = FirebaseAuth.getInstance()
    var user = mAuth.currentUser

    var txt_empty_cart:TextView?=null
    var txt_Total_price:TextView?=null
    var group_place_holder:CardView?=null
    var recycler_cart:RecyclerView?=null
    var adapter:MyCartAdapter?=null

    override fun onResume(){
        super.onResume()
        calculateTotalPrice()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        EventBus.getDefault().postSticky(HideFABCart(true))
        cartViewModel =
            ViewModelProviders.of(this).get(CartViewModel::class.java)
        //After create cartViewModel , init data source
        cartViewModel.initCartdataSource(context!!)
        val root = inflater.inflate(R.layout.fragment_cart, container, false)
        initViews(root)
        cartViewModel.getMutableLiveDataCartItem().observe(this, Observer {
            if(it == null || it.isEmpty())
            {
                recycler_cart!!.visibility = View.GONE
                group_place_holder!!.visibility = View.GONE
                text_empty_cart!!.visibility = View.VISIBLE
            }
            else{
                recycler_cart!!.visibility = View.VISIBLE
                group_place_holder!!.visibility = View.VISIBLE
                text_empty_cart!!.visibility = View.GONE

                 adapter = MyCartAdapter(context!!,it)
                recycler_cart!!.adapter = adapter

            }

        })
        return root
    }

    private fun initViews(root:View) {

        setHasOptionsMenu(true)

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context!!).cartDAO())

        recycler_cart = root.findViewById(R.id.recycler_cart) as RecyclerView
        recycler_cart!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(context,layoutManager.orientation))

        val swipe = object : MySwipeHelper(context!!,recycler_cart!!,200)
        {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
               buffer.add(MyButton(context!!,
               "Delete",
               30,
               0,
               Color.parseColor("#FF3c30"),
                   object : IMyButtonCallback
                   {
                       override fun onClick(pos: Int) {
                           val deleteItem = adapter!!.getItemAtPosition(pos)
                           cartDataSource!!.deleteCart(deleteItem)
                               .subscribeOn(Schedulers.io())
                               .observeOn(AndroidSchedulers.mainThread())
                               .subscribe(object : SingleObserver<Int>{
                                   override fun onSuccess(t: Int) {
                                       adapter!!.notifyItemRemoved(pos)
                                       sumCart()
                                       EventBus.getDefault().postSticky(CountCartEvent(true))
                                       Toast.makeText(context,"Delete item success",Toast.LENGTH_SHORT).show()
                                   }

                                   override fun onSubscribe(d: Disposable) {

                                   }

                                   override fun onError(e: Throwable) {
                                      Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT).show()
                                   }

                               })
                       }

                   }))
            }

        }



        txt_empty_cart = root.findViewById(R.id.text_empty_cart) as TextView
        txt_Total_price = root.findViewById(R.id.text_total_price) as TextView
        group_place_holder = root.findViewById(R.id.group_place_holder) as CardView
    }

    private fun sumCart() {
       cartDataSource!!.sumPrice(user!!.uid)
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe(object : SingleObserver<Double>{
               override fun onSuccess(t: Double) {
                    txt_Total_price!!.text = StringBuilder("Total: RM")
                        .append(t)
               }

               override fun onSubscribe(d: Disposable) {

               }

               override fun onError(e: Throwable) {
                   if(!e.message!!.contains("Query returned empty"))
                       Toast.makeText(context,""+e.message!!,Toast.LENGTH_SHORT).show()

               }

           })
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        cartViewModel!!.onStop()
        compositeDisposable.clear()
        EventBus.getDefault().postSticky(HideFABCart(false))
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onUpdateItemInCart(event:UpdateItemInCart){
        if(event.cartItem!=null)
        {
            recyclerViewState = recycler_cart!!.layoutManager!!.onSaveInstanceState()
            cartDataSource!!.updateCart(event.cartItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Int> {
                    override fun onSuccess(t: Int) {
                        calculateTotalPrice();
                        recycler_cart!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context,"[Update Cart]"+e.message,Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun calculateTotalPrice() {
      cartDataSource!!.sumPrice(user!!.uid)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : SingleObserver<Double>{
              override fun onSuccess(price: Double) {

                  txt_Total_price!!.text = StringBuilder("Total: RM")
                      .append(Common.formatPrice(price))
              }

              override fun onSubscribe(d: Disposable) {

              }

              override fun onError(e: Throwable) {
                  if(!e.message!!.contains("Query returned empty"))
                    Toast.makeText(context,"[SUM Cart]"+e.message,Toast.LENGTH_SHORT).show()
              }

          })

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu!!.findItem(R.id.action_settings).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cart_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       if(item!!.itemId == R.id.action_clear_cart){
           cartDataSource!!.cleanCart(user!!.uid)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(object : SingleObserver<Int>{
                   override fun onSuccess(t: Int) {
                       Toast.makeText(context,"Clear Cart Success",Toast.LENGTH_SHORT).show()
                       EventBus.getDefault().postSticky(CountCartEvent(true))
                   }

                   override fun onSubscribe(d: Disposable) {

                   }

                   override fun onError(e: Throwable) {
                       Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT).show()
                   }

               })
           return true
       }
        return super.onOptionsItemSelected(item)
    }


}
