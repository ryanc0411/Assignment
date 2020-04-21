
package com.example.assignment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import android.app.AlertDialog

import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import com.example.assignment.Common.Common
import com.example.assignment.EventBus.*
import com.example.assignment.Model.CategoryModel
import com.example.assignment.Model.FoodModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var dialog: AlertDialog?=null
    private lateinit var navController: NavController
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_login,R.id.nav_menu,R.id.nav_food_detail,R.id.nav_register, R.id.nav_cart, R.id.nav_order), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
         navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //Event Bus
    override fun onStart(){
        super.onStart()
        Common.categorySelected = null
        Common.foodSelected = null
        FirebaseAuth.getInstance().signOut()
        EventBus.getDefault().register(this)
    }


    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true,threadMode=ThreadMode.MAIN)
    fun onCategorySelected(event: CategoryClick)
    {
        if(Common.categorySelected!=null) {
            if (event.isSuccess) {
                //Toast.makeText(this,"Click to"+event.category.name,Toast.LENGTH_SHORT).show()
                findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_list)
            }
        }
    }

    @Subscribe(sticky = true,threadMode=ThreadMode.MAIN)
    fun onFoodSelected(event: FoodItemClick)
    {
        if(Common.foodSelected!=null) {
            if (event.isSuccess) {
                findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_detail)
            }
        }
    }

    @Subscribe(sticky = true,threadMode=ThreadMode.MAIN)
    fun onnavigateEvent(event: navigate)
    {
        if(event.isSuccess)
        {
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_login)
        }

    }

    @Subscribe(sticky = true,threadMode=ThreadMode.MAIN)
    fun onPopularFoodItemClick(event: PopularFoodItemClick)
    {
            if (event.popularGategoryModel != null) {
                dialog!!.show()
                FirebaseDatabase.getInstance()
                    .getReference("Category")
                    .child(event.popularGategoryModel!!.menu_id!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            dialog!!.dismiss()
                            Toast.makeText(this@MainActivity, "" + p0.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                Common.categorySelected = p0.getValue(CategoryModel::class.java)

                                //Load Food
                                FirebaseDatabase.getInstance().getReference("Category")
                                    .child(event.popularGategoryModel!!.menu_id!!)
                                    .child("foods").orderByChild("id")
                                    .equalTo(event.popularGategoryModel!!.food_id!!)
                                    .limitToLast(1)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {
                                            dialog!!.dismiss()
                                            Toast.makeText(
                                                this@MainActivity,
                                                "" + p0.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            if (p0.exists()) {

                                                for (foodSnapShot in p0.children)
                                                    Common.foodSelected =
                                                        foodSnapShot.getValue(FoodModel::class.java)
                                                navController!!.navigate(R.id.nav_food_detail)

                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity, "Item doesn't exists",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }
                                            dialog!!.dismiss()

                                        }
                                    })
                            } else {
                                dialog!!.dismiss()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Item doesn't exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }


                    })
            }

        }

    @Subscribe(sticky = true,threadMode=ThreadMode.MAIN)
    fun onBestDealFoodItemClick(event: BestDealItemClick)
    {
        if(event.model!=null)
        {
            dialog!!.show()
            FirebaseDatabase.getInstance()
                .getReference("Category")
                .child(event.model!!.menu_id!!)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        dialog!!.dismiss()
                        Toast.makeText(this@MainActivity,""+p0.message,Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()){
                            Common.categorySelected = p0.getValue(CategoryModel::class.java)

                            //Load Food
                            FirebaseDatabase.getInstance().getReference("Category")
                                .child(event.model!!.menu_id!!)
                                .child("foods").orderByChild("id")
                                .equalTo(event.model!!.food_id!!)
                                .limitToLast(1)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        dialog!!.dismiss()
                                        Toast.makeText(this@MainActivity,""+p0.message,Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        if(p0.exists()){

                                            for(foodSnapShot in p0.children)
                                                Common.foodSelected = foodSnapShot.getValue(FoodModel::class.java)
                                            navController!!.navigate(R.id.nav_food_detail)

                                        }else
                                        {
                                            Toast.makeText(this@MainActivity,"Item doesn't exists",Toast.LENGTH_SHORT).show()

                                        }
                                        dialog!!.dismiss()

                                    }
                                })
                        }
                        else{
                            dialog!!.dismiss()
                            Toast.makeText(this@MainActivity,"Item doesn't exists",Toast.LENGTH_SHORT).show()
                        }
                    }


                })
        }

    }


    }


//    @Subscribe(sticky = true,threadMode=ThreadMode.MAIN)
//    fun onCountCartEven(event: CountCartEvent)
//    {
//        if(event.isSuccess)
//        {
//           countCartItem()
//        }
//
//    }

//    private fun countCartItem() {
//
//        if (user != null) {
//            cartDataSource.countItemInCart(user!!.uid)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : SingleObserver<Int> {
//                    override fun onSuccess(t: Int) {
//                        fab.count = t
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//
//                    }
//
//                    override fun onError(e: Throwable) {
//                        Toast.makeText(this@MainActivity, "[COUNT CART]" + e.message, Toast.LENGTH_SHORT).show()
//                    }
//
//                })
//        }
//    }


