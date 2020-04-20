package com.example.assignment.ui.fooddetails

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.lifecycle.Observer
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.assignment.Common.Common
import com.example.assignment.EventBus.CountCartEvent
import com.example.assignment.Model.FoodModel
import com.example.assignment.Model.RatingModel
import com.example.assignment.R
import com.example.assignment.database.Entity.*
import com.example.assignment.ui.cart.FoodDetailViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_register.view.*
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder


class FoodDetailFragment : Fragment(), TextWatcher {

    private lateinit var foodDetailViewModel: FoodDetailViewModel

    private lateinit var addonBottomSheetDialog: BottomSheetDialog

    private var img_food: ImageView?=null
    private var btnCart: CounterFab?=null
    private var btnRating: FloatingActionButton?=null
    private var food_name: TextView?=null
    private var food_description: TextView?=null
    private var food_price: TextView?=null
    private var number_button: ElegantNumberButton?=null
    private var ratingBar: RatingBar?=null
    private var rdi_group_size:RadioGroup?=null
    private var img_add_on:ImageView?=null
    private var chip_group_user_selected_addon:ChipGroup?=null

    private val compositeDisposable = CompositeDisposable()
    private lateinit var cartDataSource:CartDataSource

    //Adoon layout
    private var chip_group_addon : ChipGroup?=null
    private var edt_search_addon:EditText?=null

    val mAuth = FirebaseAuth.getInstance()
    val ref = FirebaseDatabase.getInstance()

    private var waitingDailog:android.app.AlertDialog?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        foodDetailViewModel =
            ViewModelProviders.of(this).get(FoodDetailViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_food_detail, container, false)
        initViews(root)
        foodDetailViewModel.getMutableLiveDataFood().observe(viewLifecycleOwner, Observer {
            displayInfo(it)

        })

        foodDetailViewModel.getMutableLiveDataComment().observe(viewLifecycleOwner, Observer {
            submitRatingToFirebase(it)
        })
        return root
    }

    private fun submitRatingToFirebase(ratingModel: RatingModel?) {
            waitingDailog!!.show()

        ref.getReference(Common.COMMENT_REF).child(Common.foodSelected!!.id!!)
            .push().setValue(ratingModel)
            .addOnCompleteListener{task->
                if(task.isSuccessful)
                {
                    addRatingtoFood(ratingModel!!.ratingValue.toDouble())

                }else
                    waitingDailog!!.dismiss()

            }
    }

    private fun addRatingtoFood(ratingValue: Double) {
        ref.getReference(Common.CATEGORY_REF)//Select Category
            .child(Common.categorySelected!!.menu_id!!)//select menu in category
            .child("foods") //select food array
            .child(Common.foodSelected!!.key!!)//Selecy key
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    waitingDailog!!.dismiss()
                    Toast.makeText(context!!,""+p0.message,Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        val foodModel = dataSnapshot.getValue(FoodModel::class.java)
                        foodModel!!.key = Common.foodSelected!!.key
                        //Apply rating
                        val sumRating = foodModel.ratingValue!!.toDouble() + (ratingValue)
                        val ratingCount = foodModel.ratingCount+1


                        val updataData = HashMap<String,Any>()
                        updataData["ratingValue"] = sumRating
                        updataData["ratingCount"] = ratingCount

                        //update data in variable
                        foodModel.ratingCount = ratingCount
                        foodModel.ratingValue = sumRating

                        dataSnapshot.ref.updateChildren(updataData)
                            .addOnCompleteListener() { task ->
                                waitingDailog!!.dismiss()
                                if(task.isSuccessful)
                                {
                                    Common.foodSelected = foodModel
                                    foodDetailViewModel!!.setFoodModel(foodModel)
                                    Toast.makeText(context!!,"Thank you for your rating & comment!",Toast.LENGTH_SHORT).show()

                                }
                            }
                    }
                    else
                        waitingDailog!!.dismiss()
                }

            })

    }

    private fun displayInfo(it: FoodModel?) {
    Glide.with(requireContext()).load(it!!.image).into(img_food!!)
        food_name!!.text = StringBuilder(it!!.name!!)
        food_description!!.text = StringBuilder(it!!.description!!)
        food_price!!.text = StringBuilder(it!!.price!!.toString())

        ratingBar!!.rating = it!!.ratingValue.toFloat() / it.ratingCount

        //set Size
        for(sizeModel in it!!.size)
        {
            var radioButton = RadioButton(context)
            radioButton.setOnCheckedChangeListener{ compoundButton,b->
                if(b)
                Common.foodSelected!!.userSelectedSize = sizeModel
                calculateTotalPrice()
            }
            val params = LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1.0f
            )
            radioButton.layoutParams = params
            radioButton.text = sizeModel.name
            radioButton.tag = sizeModel.price

            rdi_group_size!!.addView(radioButton)
        }

        if (rdi_group_size!!.childCount > 0 )
        {
            val radioButton = rdi_group_size!!.getChildAt(0)as RadioButton
            radioButton.isChecked = true
        }

    }

    private fun calculateTotalPrice() {
        var totalPrice = Common.foodSelected!!.price.toDouble()
        var displayPrice = 0.0

        //Addon
        if(Common.foodSelected!!.userSelectedAddon!=null && Common.foodSelected!!.userSelectedAddon!!.size >0)
        {
            for (addonModel in Common.foodSelected!!.userSelectedAddon!!)
                totalPrice+=addonModel.price!!.toDouble()
        }

        totalPrice += Common.foodSelected!!.userSelectedSize!!.price!!.toDouble()
        displayPrice = totalPrice * number_button!!.number.toInt()
        displayPrice = Math.round(displayPrice *100.0)/100.0

        food_price!!.text = StringBuilder("").append(Common.formatPrice(displayPrice)).toString()
    }


    private fun initViews(root: View?) {

        cartDataSource =LocalCartDataSource(CartDatabase.getInstance(context!!).cartDAO())

        addonBottomSheetDialog = BottomSheetDialog(requireContext(),R.style.DialogStyle)
        val layout_user_selected_addon = layoutInflater.inflate(R.layout.layout_addon_display,null)
        chip_group_addon = layout_user_selected_addon.findViewById(R.id.chip_group_addon) as ChipGroup
        edt_search_addon = layout_user_selected_addon.findViewById(R.id.edt_search) as EditText
        addonBottomSheetDialog.setContentView(layout_user_selected_addon)

        addonBottomSheetDialog.setOnDismissListener{ dialogInterface ->
            displayUserSelectedAddon()
            calculateTotalPrice()
        }


        waitingDailog = SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()

        btnCart = root!!.findViewById(R.id.btnCart) as CounterFab
        img_food = root!!.findViewById(R.id.img_food) as ImageView
        btnRating = root!!.findViewById(R.id.btn_rating) as FloatingActionButton
        food_name = root!!.findViewById(R.id.food_name) as TextView
        food_description = root!!.findViewById(R.id.food_description) as TextView
        food_price = root!!.findViewById(R.id.food_price) as TextView
        number_button = root!!.findViewById(R.id.number_button) as ElegantNumberButton
        ratingBar = root!!.findViewById(R.id.ratingBar) as RatingBar
        rdi_group_size = root!!.findViewById(R.id.rdi_group_size) as RadioGroup
        img_add_on = root!!.findViewById(R.id.img_add_addon)as ImageView
        chip_group_user_selected_addon = root!!.findViewById(R.id.chip_group_user_selected_addon)as ChipGroup

        //Event
        val user = mAuth.currentUser
        if(user!=null) {
            btnRating!!.setOnClickListener {
                showDialogRating()
            }

            btnCart!!.setOnClickListener{
                val cartItem = CartItem()
                cartItem.uid = user!!.uid
                cartItem.foodId = Common.foodSelected!!.id!!
                cartItem.foodName = Common.foodSelected!!.name!!
                cartItem.foodImage = Common.foodSelected!!.image!!
                cartItem.foodPrice = Common.foodSelected!!.price!!.toDouble()
                cartItem.foodQuantity = number_button!!.number.toInt()
                cartItem.foodExtraPrice = Common.calculateExtraPrice(Common.foodSelected!!.userSelectedSize,Common.foodSelected!!.userSelectedAddon)
                if(Common.foodSelected!!.userSelectedAddon!=null)
                    cartItem.foodAddon = Gson().toJson(Common.foodSelected!!.userSelectedAddon)
                else
                cartItem.foodAddon = "Default"
                if(Common.foodSelected!!.userSelectedSize!=null)
                    cartItem.foodSize = Gson().toJson(Common.foodSelected!!.userSelectedSize)
                else
                cartItem.foodSize = "Default"

                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Toast.makeText(context, "Add to cart success!", Toast.LENGTH_SHORT).show()
                        //call mainactivity update counter fab
                        EventBus.getDefault().postSticky(CountCartEvent(true))
                    }, { t: Throwable? -> Toast.makeText(context, "[INSERT CART]" + t!!.message, Toast.LENGTH_SHORT).show()
                    })
                )


            }
        }

        img_add_on!!.setOnClickListener{
            if(Common.foodSelected!!.addon!=null)
            {
                displayAllAddon()
                addonBottomSheetDialog.show()
            }
        }

    }

    private fun displayAllAddon() {
        if(Common.foodSelected!!.addon!!.size>0)
        {
            chip_group_addon!!.clearCheck()
            chip_group_addon!!.removeAllViews()

            edt_search_addon!!.addTextChangedListener(this)

            for(addonModel in Common.foodSelected!!.addon!!)
            {

                    val chip = layoutInflater.inflate(R.layout.layout_chip,null,false) as Chip
                    chip.text = StringBuilder(addonModel.name!!).append("(+RM").append(addonModel.price).append(")").toString()
                    chip.setOnCheckedChangeListener{compoundButton,b->
                        if(b){
                            if(Common.foodSelected!!.userSelectedAddon ==null)
                                Common.foodSelected!!.userSelectedAddon = ArrayList()
                            Common.foodSelected!!.userSelectedAddon!!.add(addonModel)
                        }
                    }
                    chip_group_addon!!.addView(chip)
                }
            }
        }


    private fun displayUserSelectedAddon() {
        if(Common.foodSelected!!.userSelectedAddon!=null && Common.foodSelected!!.userSelectedAddon!!.size>0)
        {
            chip_group_user_selected_addon!!.removeAllViews()
            for(addonModel in Common.foodSelected!!.userSelectedAddon!!)
            {
                val chip = layoutInflater.inflate(R.layout.layout_chip_with_delete,null,false) as Chip
                chip.text = StringBuilder(addonModel.name!!).append("(+RM").append(addonModel.price!!).append(")").toString()
                chip.isCheckable = false
                chip.setOnCloseIconClickListener{
                    chip_group_user_selected_addon!!.removeView(view)
                    Common.foodSelected!!.userSelectedAddon!!.remove(addonModel)
                    calculateTotalPrice()
                }
                chip_group_user_selected_addon!!.addView(chip)
            }
        }
        else if (Common.foodSelected!!.userSelectedAddon!!.size==0)
            chip_group_user_selected_addon!!.removeAllViews()
    }

    private fun showDialogRating() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Rating Food")
        builder.setMessage("Please fill information")

        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_rating_comment,null)
        val ratingBar = itemView.findViewById<RatingBar>(R.id.rating_bar)
        val edt_comment = itemView.findViewById<EditText>(R.id.edt_comment)

        builder.setView(itemView)
        builder.setNegativeButton("CANCEL"){ dialogInterface,i -> dialogInterface.dismiss()}

        builder.setPositiveButton("OK"){dialogInterface,i->
            val ratingModel = RatingModel()
            val user = mAuth.currentUser
            ratingModel.name = user!!.displayName
            ratingModel.uid = user!!.uid
            ratingModel.comment = edt_comment.text.toString()
            ratingModel.ratingValue = ratingBar.rating
            val serverTimestamp = HashMap<String,Any>()
            serverTimestamp["timeStamp"] = ServerValue.TIMESTAMP
            ratingModel.commentTimeStamp = (serverTimestamp)

            foodDetailViewModel!!.setRatingModel(ratingModel)
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
        chip_group_addon!!.clearCheck()
        chip_group_addon!!.removeAllViews()
        for(addonModel in Common.foodSelected!!.addon!!)
        {
            if(addonModel.name!!.toLowerCase().contains(charSequence.toString().toLowerCase()))
            {
                val chip = layoutInflater.inflate(R.layout.layout_chip,null,false) as Chip
                chip.text = StringBuilder(addonModel.name!!).append("(+RM").append(addonModel.price).append(")").toString()
                chip.setOnCheckedChangeListener{compoundButton,b->
                    if(b){
                        if(Common.foodSelected!!.userSelectedAddon ==null)
                            Common.foodSelected!!.userSelectedAddon = ArrayList()
                        Common.foodSelected!!.userSelectedAddon!!.add(addonModel)
                    }
                }
                chip_group_addon!!.addView(chip)

            }
        }
    }
}
