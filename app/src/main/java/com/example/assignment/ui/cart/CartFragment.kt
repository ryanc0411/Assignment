package com.example.assignment.ui.cart

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.MyCartAdapter
import com.example.assignment.Callback.ILoadTimeFromFirebaseCallBack
import com.example.assignment.Callback.IMyButtonCallback
import com.example.assignment.Common.Common
import com.example.assignment.Common.MySwipeHelper
import com.example.assignment.EventBus.CountCartEvent
import com.example.assignment.EventBus.HideFABCart
import com.example.assignment.EventBus.MenuItemBack
import com.example.assignment.EventBus.UpdateItemInCart
import com.example.assignment.Model.Order
import com.example.assignment.R
import com.example.assignment.UserActivity
import com.example.assignment.database.Entity.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.layout_rating_comment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class CartFragment : Fragment(), ILoadTimeFromFirebaseCallBack {

  private var cartDataSource:CartDataSource?=null
    private var compositeDisposable:CompositeDisposable = CompositeDisposable()
    private var recyclerViewState: Parcelable?=null
    private lateinit var cartViewModel: CartViewModel
    val mAuth = FirebaseAuth.getInstance()
    var user = mAuth.currentUser
    private lateinit var btn_place_order: Button
    lateinit var nameOnCard: EditText
    lateinit var cardNumber: EditText
    lateinit var mmyy: EditText
    lateinit var cvc: EditText

    var txt_empty_cart:TextView?=null
    var txt_Total_price:TextView?=null
    var group_place_holder:CardView?=null
    var recycler_cart:RecyclerView?=null
    var adapter:MyCartAdapter?=null
    lateinit var visaCardRef: DatabaseReference


    lateinit var listener:ILoadTimeFromFirebaseCallBack


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

        visaCardRef = FirebaseDatabase.getInstance().getReference("Card")


        return root
    }

    private fun initViews(root:View) {

        setHasOptionsMenu(true)

        listener = this

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
                                       if(!e.message!!.contains("Query returned empty"))
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

        btn_place_order = root.findViewById(R.id.btn_place_order) as Button

        //Event
       btn_place_order.setOnClickListener{


            val builder = AlertDialog.Builder(context!!)
           builder.setTitle("One More Step!")

           val view = LayoutInflater.from(context).inflate(R.layout.layout_place_order,null)

           val edt_address = view.findViewById<View>(R.id.edt_address) as EditText
           val rdi_home = view.findViewById<View>(R.id.rdi_home_address) as RadioButton
           val rdi_other_address = view.findViewById<View>(R.id.rdi_other_address) as RadioButton

           val CreditCradView = view.findViewById<View>(R.id.visaCardCardView) as CardView
           val rdi_cod = view.findViewById<View>(R.id.rdi_cod) as RadioButton
           val rdi_card = view.findViewById<View>(R.id.rdi_Card) as RadioButton
            val card_number = view.findViewById<View>(R.id.cardNumberEditText) as EditText
           val card_holder_name = view.findViewById<View>(R.id.nameOnCardEditText) as EditText
           val card_expired_date = view.findViewById<View>(R.id.mmyyEditText) as EditText
           val card_CVC = view.findViewById<View>(R.id.cvcEditText) as EditText
           val btn_creditcard = view.findViewById<View>(R.id.btn_creditcard) as Button


            nameOnCard = card_holder_name
            mmyy = card_expired_date
              cvc = card_CVC
           cardNumber = card_number


            //Data
           edt_address.setText(Common.homeAddress)
           rdi_home.setOnCheckedChangeListener{ compoundButton, b->
               if(b)
               {
                   edt_address.setText(Common.homeAddress)
               }
           }
           rdi_other_address.setOnCheckedChangeListener{ compoundButton, b->
               if(b)
               {
                   edt_address.setText("")
                   edt_address.setHint("Enter your address")
               }
           }

           rdi_cod.setOnCheckedChangeListener{ compoundButton, b ->
               if(b)
               {
                   CreditCradView!!.visibility= View.GONE

               }
           }

           rdi_card.setOnCheckedChangeListener{ compoundButton, b ->
               if(b)
               {
                   CreditCradView!!.visibility= View.VISIBLE
               }
           }

           btn_creditcard.setOnClickListener{
               if (verifyVisaCard()) {
                   visaCardRef.addListenerForSingleValueEvent(object : ValueEventListener {
                       override fun onCancelled(p0: DatabaseError) {
                       }

                       override fun onDataChange(p0: DataSnapshot) {
                           if (p0.exists()) {
                               val visacard = p0.getValue(VisaCard::class.java)
                               if (visacard!!.nameOnCard == nameOnCard.text.toString() &&
                                   visacard.cardNumber == cardNumber.text.toString() &&
                                   visacard.mmyy == mmyy.text.toString() &&
                                   visacard.cvc == cvc.text.toString()) {
                                   CreditCard(edt_address.text.toString())
                                   val builder = AlertDialog.Builder(context)
                                   builder.setTitle("Transaction completed")
                                   builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                                       val intent = Intent(activity, UserActivity::class.java)
                                       intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                       startActivity(intent)
                                   }
                                   builder.show()
                               } else {
                                   val builder = AlertDialog.Builder(context)
                                   builder.setTitle("Invalid visa card info!")
                                   builder.setPositiveButton(android.R.string.ok) { dialog, which -> }
                                   builder.show()
                               }
                           }
                       }
                   })
               }
           }



           builder.setView(view)
           builder.setNegativeButton("NO") { dialogInterface, _ -> dialogInterface.dismiss()}
               .setPositiveButton("YES") { dialogInterface, _ ->
                   if(rdi_cod.isChecked) {
                       paymentCOD(edt_address.text.toString())


                   }else if(rdi_card.isChecked){


                   }


               }

           val dialog = builder.create()
           dialog.show()

       }
    }


    private fun verifyVisaCard(): Boolean {
        var isValid = true
        if (nameOnCard.text.isEmpty()) {
            isValid = false
            nameOnCard.setError("Name on card cannot be blank")
        }
        if (cardNumber.text.isEmpty()) {
            isValid = false
            cardNumber.setError("Card number cannot be blank")
        }
        if (mmyy.text.isEmpty()) {
            isValid = false
            mmyy.setError("MM/YY cannot be blank")
        }
        if (cvc.text.isEmpty()) {
            isValid = false
            cvc.setError("CVC cannot be blank")
        }
        return isValid
    }

    private fun paymentCOD(address: String) {
        compositeDisposable.add(cartDataSource!!.getAllCart(user!!.uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ cartItemList ->

                //when we have all cartItems , we will get total price
                cartDataSource!!.sumPrice(user!!.uid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object:SingleObserver<Double>{
                        override fun onSuccess(totalPrice: Double) {
                            val finalPrice = totalPrice
                            val order = Order()
                            order.userId = user!!.uid
                            order.userName = Common.userName
                            order.shippingAddress = address
                            order.cartItemList = cartItemList
                            order.totalPayment = totalPrice
                            order.finalPayment = finalPrice
                            order.discount = 0
                            order.isCod = true
                            order.transactionId = "Cash On Delivery"

                            //Submit to Firebase
                            syncLocalTimeWithServerTime(order)
                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onError(e: Throwable) {
                            if(!e.message!!.contains("Query returned empty"))
                            Toast.makeText(context!!,""+e.message,Toast.LENGTH_SHORT).show()
                        }


                    })

            },{throwable -> Toast.makeText(context!!,""+throwable.message,Toast.LENGTH_SHORT).show()  }))

    }

    private fun CreditCard(address: String) {
        compositeDisposable.add(cartDataSource!!.getAllCart(user!!.uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ cartItemList ->

                //when we have all cartItems , we will get total price
                cartDataSource!!.sumPrice(user!!.uid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object:SingleObserver<Double>{
                        override fun onSuccess(totalPrice: Double) {
                            val finalPrice = totalPrice
                            val order = Order()
                            order.userId = user!!.uid
                            order.userName = Common.userName
                            order.shippingAddress = address
                            order.cartItemList = cartItemList
                            order.totalPayment = totalPrice
                            order.finalPayment = finalPrice
                            order.discount = 0
                            order.isCod = false
                            order.transactionId = "Credit Card"

                            //Submit to Firebase
                            syncLocalTimeWithServerTime(order)
                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onError(e: Throwable) {
                            if(!e.message!!.contains("Query returned empty"))
                                Toast.makeText(context!!,""+e.message,Toast.LENGTH_SHORT).show()
                        }


                    })

            },{throwable -> Toast.makeText(context!!,""+throwable.message,Toast.LENGTH_SHORT).show()  }))

    }

    private fun syncLocalTimeWithServerTime(order: Order) {
        var offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset")
        offsetRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
              listener.onLoadTimeFailed(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
               val offset = p0.getValue(Long::class.java)
                val estimatedServerTimeInMs = System.currentTimeMillis()+offset!!
                val sdf = SimpleDateFormat("MMM dd yyyyy, HH:mm")
                val date = Date(estimatedServerTimeInMs)
                Log.d("EDMT_DEV",""+sdf.format(date))
                listener.onLoadTimeSuccess(order,estimatedServerTimeInMs)
            }


        })
    }

    private fun WriteOrderToFirebase(order: Order) {
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
            .child(Common.createOrderNumber())
            .setValue(order)
            .addOnFailureListener{ e -> Toast.makeText(context!!,""+e.message,Toast.LENGTH_SHORT).show() }
            .addOnCompleteListener{task ->
           //clean cart
                if(task.isSuccessful)
                {
                    cartDataSource!!.cleanCart(user!!.uid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : SingleObserver<Int>{
                            override fun onSuccess(t: Int) {
                                Toast.makeText(context!!,"Order placed successfuly !",Toast.LENGTH_SHORT).show()
                                EventBus.getDefault().postSticky(CountCartEvent(true))
                            }

                            override fun onSubscribe(d: Disposable) {

                            }

                                override fun onError(e: Throwable) {
                                if(!e.message!!.contains("Query returned empty"))
                                Toast.makeText(context!!,"[CLEAR CART]"+e.message,Toast.LENGTH_SHORT).show()
                            }


                        })
                }
            }
    }

    private fun sumCart() {
       cartDataSource!!.sumPrice(user!!.uid)
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe(object : SingleObserver<Double>{
               override fun onSuccess(t: Double) {
                    txt_Total_price!!.text = StringBuilder("Total: RM").append(t)
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
                       if(!e.message!!.contains("Query returned empty"))
                       Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT).show()
                   }

               })
           return true
       }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()
    }

    override fun onLoadTimeSuccess(order: Order, estimatedTimeMs: Long) {
        order.createDate = (estimatedTimeMs)
        order.orderStatus = 0
        WriteOrderToFirebase(order)
    }

    override fun onLoadTimeFailed(message: String) {
      Toast.makeText(context!!,""+message,Toast.LENGTH_SHORT).show()
    }

}
