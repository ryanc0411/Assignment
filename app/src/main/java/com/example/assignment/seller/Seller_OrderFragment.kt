package com.example.assignment.seller

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.MySellerOrderAdapter
import com.example.assignment.Callback.IMyButtonCallback
import com.example.assignment.Common.BottomSheetOrderFragment
import com.example.assignment.Common.Common
import com.example.assignment.Common.MySwipeHelper
import com.example.assignment.EventBus.ChangeMenuClick
import com.example.assignment.EventBus.LoadOrderEvent
import com.example.assignment.Model.Order_seller_Model

import com.example.assignment.R
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.seller_fragment_order.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.zip.Inflater

class Seller_OrderFragment:Fragment()
{
    lateinit var  recycler_seller_order:RecyclerView
    lateinit var layoutAnimationController:LayoutAnimationController

  lateinit var orderViewModel:Seller_OderViewModel

    private var adapter : MySellerOrderAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.seller_fragment_order,container,false)
        initView(root)

        orderViewModel = ViewModelProviders.of(this).get(Seller_OderViewModel::class.java)

        orderViewModel!!.messageError.observe(this, Observer { s->
            Toast.makeText(context,s,Toast.LENGTH_SHORT).show()
        })

        orderViewModel!!.getOrderModelList().observe(this, Observer { orderList ->
            if(orderList != null){
                adapter = MySellerOrderAdapter(context!!,orderList.toMutableList())
                recycler_seller_order.adapter = adapter
                recycler_seller_order.layoutAnimation = layoutAnimationController

                txt_order_filter.setText(StringBuilder("Orders (")
                    .append(orderList.size)
                    .append(")"))
            }
        })


        return root
    }

    private fun initView(root: View?) {

        setHasOptionsMenu(true)

        recycler_seller_order = root!!.findViewById(R.id.recycler_seller_order) as RecyclerView
        recycler_seller_order.setHasFixedSize(true)
        recycler_seller_order.layoutManager = LinearLayoutManager(context)

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)

        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        val swipe = object : MySwipeHelper(context!!,recycler_seller_order!!,width/6){
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(context!!,
                    "Direction",
                    30,
                    0,
                    Color.parseColor("#9b0000"),
                    object : IMyButtonCallback{
                        override fun onClick(pos: Int) {


                        }

                    })
                )
                buffer.add(
                    MyButton(context!!,
                        "Update",
                        30,
                        0,
                        Color.parseColor("#560027"),
                        object : IMyButtonCallback{
                            override fun onClick(pos: Int) {


                            }

                        })
                )
                buffer.add(
                    MyButton(context!!,
                        "Remove",
                        30,
                        0,
                        Color.parseColor("#12005e"),
                        object : IMyButtonCallback{
                            override fun onClick(pos: Int) {

                                val orderModel = adapter!!.getItemAtPosition(pos)

                                val builder = AlertDialog.Builder(context!!)
                                    .setTitle("Delete")
                                    .setMessage("Do you really want to delete this order?")
                                    .setNegativeButton("CANCEL"){dialogInterface, i -> dialogInterface.dismiss()}
                                    .setPositiveButton("DELETE"){dialogInterface , i ->
                                    FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                                        .child(orderModel.key!!)
                                        .removeValue()
                                        .addOnFailureListener{
                                            Toast.makeText(context!!,""+it.message,Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnSuccessListener {
                                            adapter!!.removeItem(pos)
                                            adapter!!.notifyItemRemoved(pos)
                                            txt_order_filter.setText(StringBuilder("Order (")
                                                .append(adapter!!.itemCount)
                                                .append(")"))
                                            dialogInterface.dismiss()
                                            Toast.makeText(context!!,"Order has been delete",Toast.LENGTH_SHORT).show()

                                        }
                                }

                                val dialog = builder.create()
                                dialog.show()

                                val btn_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                                btn_negative.setTextColor(Color.LTGRAY)
                                val btn_Positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                btn_Positive.setTextColor(Color.RED)
                            }

                        })
                )
                buffer.add(
                    MyButton(context!!,
                        "Edit",
                        30,
                        0,
                        Color.parseColor("#333639"),
                        object : IMyButtonCallback{
                            override fun onClick(pos: Int) {

                                showEditDialog(adapter!!.getItemAtPosition(pos),pos)
                            }

                        })
                )
            }
        }

    }

    private fun showEditDialog(orderModel: Order_seller_Model, pos: Int) {
        var layout_dialog:View?=null
        var builder:AlertDialog.Builder?=null

        if(orderModel.orderStatus == -1)
        {
            layout_dialog = LayoutInflater.from(context!!)
                .inflate(R.layout.layout_dialog_cancelled,null)
            builder = AlertDialog.Builder(context!!)
                .setView(layout_dialog)
        }
        else if(orderModel.orderStatus == 0)
            {
                layout_dialog = LayoutInflater.from(context!!)
                    .inflate(R.layout.layout_dialog_shipping,null)
                builder = AlertDialog.Builder(context!!,
                    android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
                    .setView(layout_dialog)
            }
        else
        {
            layout_dialog = LayoutInflater.from(context!!)
                .inflate(R.layout.layout_dialog_shipped,null)
            builder = AlertDialog.Builder(context!!)
                .setView(layout_dialog)
        }

        //View
        val btn_ok = layout_dialog.findViewById<View>(R.id.btn_ok) as Button
        val btn_cancel = layout_dialog.findViewById<View>(R.id.btn_cancel) as Button

        val rdi_shipping  = layout_dialog.findViewById<View>(R.id.rdi_shipping) as RadioButton
        val rdi_shipped  = layout_dialog.findViewById<View>(R.id.rdi_shipped) as RadioButton
        val rdi_cancelled  = layout_dialog.findViewById<View>(R.id.rdi_cancelled) as RadioButton
        val rdi_delete  = layout_dialog.findViewById<View>(R.id.rdi_delete) as RadioButton
        val rdi_restore_placed  = layout_dialog.findViewById<View>(R.id.rdi_restore_placed) as RadioButton

        val txt_status =  layout_dialog.findViewById<View>(R.id.txt_order_status) as TextView

        //Set data
        txt_status.setText(StringBuilder("Order Status (")
            .append(Common.convertStatusToString(orderModel.orderStatus))
            .append(")"))

        //Create Dialog
        val dialog = builder.create()
        dialog.show()

        btn_cancel.setOnClickListener { dialog.dismiss()}
        btn_ok.setOnClickListener {
            dialog.dismiss()
            if(rdi_cancelled != null && rdi_cancelled.isChecked)
            {
                updateOrder(pos,orderModel,-1)
            }
            else if(rdi_shipping != null && rdi_shipping.isChecked)
            {
                updateOrder(pos,orderModel,1)
            }
           else if(rdi_shipped != null && rdi_shipped.isChecked)
            {
                updateOrder(pos,orderModel,2)
            }
            else if(rdi_restore_placed != null && rdi_restore_placed.isChecked)
            {
                updateOrder(pos,orderModel,0)
            }
            else if(rdi_delete != null && rdi_delete.isChecked)
            {
                deleteOrder(pos,orderModel)
            }
        }
    }

    private fun updateOrder(pos: Int, orderModel: Order_seller_Model, i: Int) {
        if(!TextUtils.isEmpty(orderModel.key))
        {

        }
        else
        {
            Toast.makeText(context!!,"Order number must not be null or empty",Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       inflater.inflate(R.menu.order_list_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       if(item.itemId == R.id.action_filter)
       {
           val bottomSheet = BottomSheetOrderFragment.instance
           bottomSheet!!.show(requireActivity().supportFragmentManager,"OrderList")
           return true
       }
        else
           return super.onOptionsItemSelected(item)

    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        if(EventBus.getDefault().hasSubscriberForEvent(LoadOrderEvent::class.java))
            EventBus.getDefault().removeStickyEvent(LoadOrderEvent::class.java)
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(ChangeMenuClick(true))
        super.onDestroy()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLoadOrder(event:LoadOrderEvent){
        orderViewModel.loadOrder(event.status)

    }


}