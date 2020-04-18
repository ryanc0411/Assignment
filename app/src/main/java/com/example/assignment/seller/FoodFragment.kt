//package com.example.assignment.seller
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.app.ProgressDialog
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.graphics.Bitmap
//import android.net.Uri
//import android.os.AsyncTask
//import android.os.Bundle
//import android.provider.MediaStore
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.core.view.isVisible
//import androidx.fragment.app.Fragment
//import androidx.navigation.findNavController
//import com.example.assignment.R
//import com.example.assignment.database.Entity.Canteen
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//import com.google.firebase.storage.FirebaseStorage
//import kotlinx.android.synthetic.main.seller_shop.*
//import kotlinx.android.synthetic.main.seller_shop.view.*
//import java.util.*
//
//class FoodFragment : Fragment() {
//
//    lateinit var saveContext: Context
//    lateinit var uploadImageView: ImageView
//
//    // view fields
//    lateinit var shopNameTextView: TextView
//    lateinit var FoodNameTextView: TextView
//    lateinit var price: TextView
//
//    // edit fields
//    lateinit var canteenNameDropdown: String
//    lateinit var shopNameEditText: EditText
//    lateinit var shopBusinessHourEditText: EditText
//
//
//    // cache
//    lateinit var sharedPreferences: SharedPreferences
//    lateinit var editor: SharedPreferences.Editor
//
//    lateinit var loading: ProgressDialog
//    lateinit var ref: DatabaseReference
//    val mAuth = FirebaseAuth.getInstance()
//    var uri: Uri? = null
//    var preventLoop = 0
//    var isChange = 0
//    var currentCanteenID = 0
//
//    //@RequiresApi(Build.VERSION_CODES.M)
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val root = inflater.inflate(R.layout.seller_shop, container, false)
//        root.addButton.setOnClickListener{view : View ->
//            view.findNavController().navigate(R.id.action_ADDCanteen_to_ADDFood)}
//
//
//        // Create an ArrayAdapter
//        val adapter = ArrayAdapter.createFromResource(activity!!, R.array.canteen_list, android.R.layout.simple_spinner_item)
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        // Apply the adapter to the spinner
//        root.CanteenNameDropdown.adapter = adapter
//
//        // initiate the cacha file
//        sharedPreferences = activity!!.getSharedPreferences(mAuth.currentUser!!.uid, Context.MODE_PRIVATE)
//        editor =  sharedPreferences.edit()
//
//        // initiate the firebase database
//        ref = FirebaseDatabase.getInstance().getReference("Canteen")
//
//        uploadImageView = root.uploadImageView
//
//        canteenNameTextView = root.CanteenNameTextView
//        shopNameTextView = root.ShopNameTextView
//        shopBusinessHourTextView = root.ShopBusinessHourTextView
//
//
//        canteenNameDropdown = root.CanteenNameDropdown.selectedItem.toString()
//        shopNameEditText = root.ShopNameEditText
//        shopBusinessHourEditText = root.ShopBusinessHourEditText
//
//
////        displayCurrentThemePark()
//
//        uploadImageView.setOnClickListener {
//            if (!root.createButton.isVisible) {
//                val galleryIntent =
//                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                startActivityForResult(galleryIntent, 0)
//            }
//        }
//
//        root.createButton.setOnClickListener {
//            isChange = 0
//            root.addButton.visibility = View.VISIBLE
//            root.createButton.visibility = View.GONE
//            root.CanteenInfoTemplate.visibility = View.GONE
//            root.CanteenInfoEditTemplate.visibility = View.VISIBLE
//
//        }
//
//        root.addButton.setOnClickListener {
//            preventLoop = 0
//
//            if (edittextValidation()) {
//                loading = ProgressDialog(context)
//                loading.setMessage("Loading...")
//                loading.show()
//                uploadImageToFirebase()
//                root.createButton.visibility = View.VISIBLE
//                root.addButton.visibility = View.GONE
//                root.CanteenInfoTemplate.visibility = View.VISIBLE
//                root.CanteenInfoEditTemplate.visibility = View.GONE
//            }
//        }
//
//
//        return root
//    }
//
//    private fun edittextValidation(): Boolean {
//        var isValid = true
//        if (shopBusinessHourEditText.text.toString().trim().isEmpty()) {
//            isValid = false
//            shopBusinessHourEditText.setError("Business hour is required")
//        }
//        if (shopNameEditText.text.toString().trim().isEmpty()) {
//            isValid = false
//            shopNameEditText.setError("Shop Name is required")}
//
////        } else {
////            val str = themeparkAdultPriceEditText.text.toString().trim()
////            var k = 0
////            for (i in str) {
////                k++
////                if (!i.isDigit()) {
////                    if (i != '.' && k != 1) {
////                        isValid = false
////                        themeparkAdultPriceEditText.setError("Please enter a valid price")
////                    } else if (i == '.' && k == 1) {
////                        isValid = false
////                        themeparkAdultPriceEditText.setError("Please enter a valid price")
////                    }
////                }
////            }
////        }
////        if (themeparkChildPriceEditText.text.toString().trim().isEmpty()) {
////            isValid = false
////            themeparkChildPriceEditText.setError("Child Price is required")
////        } else {
////            val str = themeparkChildPriceEditText.text.toString().trim()
////            for (i in str) {
////                if (!i.isDigit() ) {
////                    if (i != '.') {
////                        isValid = false
////                        themeparkChildPriceEditText.setError("Please enter a valid price")
////                    }
////                }
////            }
////        }
//        if (uri == null) {
//            val builder = AlertDialog.Builder(context)
//            builder.setTitle("Please upload your Shop image!")
//            builder.setPositiveButton(android.R.string.ok) { dialog, which -> }
//            builder.show()
//            isValid = false
//        }
//        return isValid
//    }
//
////    private fun displayCurrentThemePark() {
////        var tpid = sharedPreferences.getInt("CanteenID",  0)
////        var image = sharedPreferences.getString("CanteenImage", "")
////        var canteenname = sharedPreferences.getString("CanteenName", "")
////        var shopname = sharedPreferences.getString("ShopName", "")
////        var hour = sharedPreferences.getString("ShopBusinessHours", "")
////
////        if (tpid != 0) {
////            this.currentCanteenID = tpid
////            if (image != "" && uri.toString() != image) {
////                uri = Uri.parse(image)
////                Picasso.with(saveContext).load(image).fit().centerCrop().into(uploadImageView)
////            }
////            if (canteenname != "") {
////                canteenNameTextView.text = "Canteen Name: "
////                val adapter = ArrayAdapter.createFromResource(activity!!, R.array.canteen_list, android.R.layout.simple_spinner_item)
////                val selectionPosition: Int = adapter.getPosition(canteenname)
////                adapter.setDropDownViewResource(selectionPosition);
////            }
////            if (shopname != "") {
////                shopNameTextView.text = "Shop Name: "
////                shopNameEditText.setText(shopname)
////            }
////            if (hour != "") {
////                shopBusinessHourTextView.text = "Business Hours: " + hour
////                shopBusinessHourEditText.setText(hour)
////            }
////
////        }
////        else {
////            ref.orderByChild("staffID").equalTo(mAuth.currentUser!!.uid)
////                .addListenerForSingleValueEvent(
////                    object : ValueEventListener {
////                        override fun onCancelled(p0: DatabaseError) {
////                            TODO("Not yet implemented")
////                        }
////
////                        override fun onDataChange(p0: DataSnapshot) {
////                            if (p0.exists()) {
////                                for (i in p0.children) {
////                                    val Canteen = i.getValue(Canteen::class.java)
////                                    if (tpid == 0) {
////                                        AsyncTask.execute {
////                                            editor.putInt(
////                                                "CanteenID",
////                                                Canteen!!.CanteenID
////                                            )
////                                            editor.putString(
////                                                "CanteenImage",
////                                                Canteen.CanteenImage
////                                            )
////                                            editor.putString(
////                                                "CanteenName",
////                                                Canteen.CanteenName
////                                            )
////                                            editor.putString(
////                                                "ShopName",
////                                                Canteen.ShopName
////                                            )
////                                            editor.putString(
////                                                "ShopBusinessHours",
////                                                Canteen.ShopBusinessHours
////                                            )
////
////                                            editor.commit()
////                                        }
////                                        currentCanteenID = Canteen!!.CanteenID
////                                        uri = Uri.parse(Canteen.CanteenImage)
////                                        Picasso.with(saveContext).load(Canteen.CanteenImage)
////                                            .fit().centerCrop()
////                                            .into(uploadImageView)
////                                        CanteenNameTextView.text =
////                                            "Canteen Name: " + Canteen.CanteenName
////                                        canteenNameDropdown.setText(Canteen.CanteenName)
////                                        themeparkBusinessHourTextView.text =
////                                            "Business Hours: " + themepark.themeParkBusinessHours
////                                        themeparkBusinessHourEditText.setText(themepark.themeParkBusinessHours)
////                                        themeparkAdultPriceTextView.text =
////                                            "Adult Price: RM " +
////                                                    BigDecimal(themepark.adultPrice).setScale(
////                                                        2,
////                                                        RoundingMode.HALF_EVEN
////                                                    ).toString()
////                                        themeparkChildPriceTextView.text =
////                                            "Child Price: RM " +
////                                                    BigDecimal(themepark.childPrice).setScale(
////                                                        2,
////                                                        RoundingMode.HALF_EVEN
////                                                    ).toString()
////                                        themeparkAdultPriceEditText.setText(
////                                            BigDecimal(themepark.adultPrice)
////                                                .setScale(2, RoundingMode.HALF_EVEN)
////                                                .toString()
////                                        )
////                                        themeparkChildPriceEditText.setText(
////                                            BigDecimal(themepark.childPrice)
////                                                .setScale(2, RoundingMode.HALF_EVEN)
////                                                .toString()
////                                        )
////                                    }
////                                }
////                            }
////                        }
////                    }
////                )
////        }
////    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        this.saveContext = context
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
//            uri = data.data
//            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
//
//            // resize the chosen image
//            var resizeImg: Bitmap = Bitmap.createScaledBitmap(bitmap, 640, 450, false)
//            uploadImageView.setImageBitmap(resizeImg)
//            isChange = 1
//        }
//    }
//
//    private fun uploadImageToFirebase() {
//        if (uri == null) return
//
//        AsyncTask.execute {
//            if (isChange == 1) {
//                val imageName = UUID.randomUUID().toString()
//                val refStorage = FirebaseStorage.getInstance().getReference("/image/$imageName")
//                refStorage.putFile(uri!!).addOnSuccessListener {
//                    refStorage.downloadUrl.addOnSuccessListener {
//                        editor.putString("CanteenImage", it.toString())
//                        saveThemeParkToFirebase(it)
//                    }
//                }
//            } else {
//                val uid = mAuth.currentUser!!.uid
//                ref.addValueEventListener(object : ValueEventListener {
//                    override fun onCancelled(p0: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onDataChange(p0: DataSnapshot) {
//                        if (p0.exists()) {
//                            val newCanteen = Canteen(
//                                currentCanteenID,
//                                uri.toString(),
//                                CanteenNameDropdown.selectedItem.toString(),
//                                shopNameEditText.text.toString(),
//                                shopBusinessHourEditText.text.toString(),
//                                uid
//                            )
//                            ref.child(currentCanteenID.toString()).setValue(newCanteen)
//                            saveIntoCache(
//                                currentCanteenID,
//                                uri.toString(),
//                                CanteenNameDropdown.selectedItem.toString(),
//                                shopNameEditText.text.toString(),
//                                shopBusinessHourEditText.text.toString()
//                            )
//                        }
//                    }
//                })
//            }
//        }
//    }
//
//    private fun saveThemeParkToFirebase(uri: Uri) {
//
//        AsyncTask.execute {
//            val uid = mAuth.currentUser!!.uid
//            ref.addValueEventListener(object : ValueEventListener {
//                override fun onCancelled(p0: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onDataChange(p0: DataSnapshot) {
//                    if (p0.exists() && preventLoop == 0 && currentCanteenID == 0) {
//                        // add new theme park but not the first record
//                        preventLoop++
//                        var totalRecords = p0.childrenCount
//                        totalRecords++
//                        // save into firebase
//                        val newShop = Canteen(
//                            totalRecords.toInt(),
//                            uri.toString(),
//                            CanteenNameDropdown.selectedItem.toString(),
//                            shopNameEditText.text.toString(),
//                            shopBusinessHourEditText.text.toString(),
//                            uid
//                        )
//                        ref.child(totalRecords.toString()).setValue(newShop)
//                        // save into cache
//                        saveIntoCache(
//                            totalRecords.toInt(),
//                            uri.toString(),
//                            CanteenNameDropdown.selectedItem.toString(),
//                            shopNameEditText.text.toString(),
//                            shopBusinessHourEditText.text.toString()
//                        )
//                    } else if (!p0.exists() && preventLoop == 0 && currentCanteenID == 0) {
//                        // add new theme park that is first record
//                        preventLoop++
//                        // save into firebase
//                        val newShop = Canteen(
//                            1,
//                            uri.toString(),
//                            CanteenNameDropdown.selectedItem.toString(),
//                            shopNameEditText.text.toString(),
//                            shopBusinessHourEditText.text.toString(),
//                            uid
//                        )
//                        ref.child("1").setValue(newShop)
//                        // save into cache
//                        saveIntoCache(
//                            1,
//                            uri.toString(),
//                            CanteenNameDropdown.selectedItem.toString(),
//                            shopNameEditText.text.toString(),
//                            shopBusinessHourEditText.text.toString()
//                        )
//
//                    } else if (preventLoop == 0 && currentCanteenID != 0) {
//                        // edit current theme park
//                        preventLoop++
//                        // save into firebase
//                        val newShop = Canteen(
//                            currentCanteenID,
//                            uri.toString(),
//                            CanteenNameDropdown.selectedItem.toString(),
//                            shopNameEditText.text.toString(),
//                            shopBusinessHourEditText.text.toString(),
//                            uid
//                        )
//                        ref.child(currentCanteenID.toString()).setValue(newShop)
//                        // save into cache
//                        saveIntoCache(
//                            currentCanteenID,
//                            uri.toString(),
//                            CanteenNameDropdown.selectedItem.toString(),
//                            shopNameEditText.text.toString(),
//                            shopBusinessHourEditText.text.toString()
//                        )
//
//                    }
//                }
//            })
//        }
//    }
//
//    private fun saveIntoCache(currentCanteenID: Int, toString: String, toString1: String,
//                              toString2: String, toString3: String) {
//        AsyncTask.execute {
//            editor.putInt("themeParkID", currentCanteenID)
//            if (isChange == 0) editor.putString("CanteenImage", toString)
//            editor.putString("CanteenName", toString1)
//            editor.putString("ShopName", toString2)
//            editor.putString("ShopBusinessHours", toString3)
//            editor.commit()
//        }
//        loading.dismiss()
//        Toast.makeText(saveContext, "Canteen Added Successful", Toast.LENGTH_SHORT).show()
////        displayCurrentThemePark()
//        view?.findNavController()?.navigate(R.id.action_ADDCanteen_to_ADDFood)
//    }
//
//}