package com.example.assignment


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.assignment.Common.Common
import com.example.assignment.EventBus.*
import com.example.assignment.Model.CategoryModel
import com.example.assignment.Model.FoodModel
import com.example.assignment.Model.PopularGategoryModel
import com.example.assignment.database.Entity.CartDataSource
import com.example.assignment.database.Entity.CartDatabase
import com.example.assignment.database.Entity.LocalCartDataSource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_user.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class UserActivity : AppCompatActivity() {



    private lateinit var appBarConfiguration: AppBarConfiguration
    val mAuth = FirebaseAuth.getInstance()
    private lateinit var cartDataSource: CartDataSource
    private lateinit var navController:NavController
    private var dialog : AlertDialog?=null

    private var menuItemClick=-1

    override fun onResume() {
        super.onResume()
        countCartItem()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(this).cartDAO())
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            navController.navigate(R.id.nav_cart)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_user_layout)
        val navView: NavigationView = findViewById(R.id.nav_user)
         navController = findNavController(R.id.nav_user_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_cart,R.id.nav_menu,R.id.nav_food_detail, R.id.nav_order,R.id.nav_logout), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(p0: MenuItem): Boolean {
                p0.isChecked = true
                drawerLayout!!.closeDrawers()
                if(p0.itemId == R.id.nav_logout) {
                    signOut()
                }else if(p0.itemId == R.id.nav_menu){
                    if(menuItemClick!=p0.itemId)
                    navController.navigate(R.id.nav_menu)
                } else if(p0.itemId == R.id.nav_home){
                    if(menuItemClick!=p0.itemId)
                    navController.navigate(R.id.nav_home)
                } else if(p0.itemId == R.id.nav_cart){
                    if(menuItemClick!=p0.itemId)
                    navController.navigate(R.id.nav_cart)
                } else if(p0.itemId == R.id.nav_order){
                    if(menuItemClick!=p0.itemId)
                    navController.navigate(R.id.nav_order)
                }

                menuItemClick = p0!!.itemId
                return true

            }



            })

        countCartItem()
    }

    private fun signOut() {
       val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Sign out")
            .setMessage("Do you really want to exit?")
            .setNegativeButton("CANCEL",{dialogInterface, _ ->  dialogInterface.dismiss()})
            .setPositiveButton("OK") { dialogInterface, _ ->
                Common.foodSelected=null
                Common.categorySelected = null
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show()
                finish()

            }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("username1", Context.MODE_PRIVATE )
        val textView: TextView = findViewById<TextView>(R.id.Usernametext)
        val username = sharedPreferences.getString("username","TARUC FOOD")
        textView.text =  "Hey, " + username
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_user_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //Event Bus
    override fun onStart(){
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        Common.categorySelected = null
        Common.foodSelected = null
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true,threadMode= ThreadMode.MAIN)
    fun onCategorySelected(event: CategoryClick)
    {
        if(Common.categorySelected!=null) {
            if (event.isSuccess) {
                //Toast.makeText(this,"Click to"+event.category.name,Toast.LENGTH_SHORT).show()
                findNavController(R.id.nav_user_fragment).navigate(R.id.nav_food_list)
            }
        }

    }

    @Subscribe(sticky = true,threadMode= ThreadMode.MAIN)
    fun onFoodSelected(event: FoodItemClick)
    {
        if(Common.foodSelected!=null) {
            if (event.isSuccess) {
                findNavController(R.id.nav_user_fragment).navigate(R.id.nav_food_detail)
            }
        }
    }

    @Subscribe(sticky = true,threadMode=ThreadMode.MAIN)
    fun onHideFABCart(event: HideFABCart)
    {
        if(event.isHide)
        {
          fab.hide()
        }
        else
        {
            fab.show()
        }

    }


    @Subscribe(sticky = true,threadMode=ThreadMode.MAIN)
    fun onCountCartEven(event: CountCartEvent)
    {
        if(event.isSuccess)
        {
            countCartItem()
        }

    }

    @Subscribe(sticky = true,threadMode=ThreadMode.MAIN)
    fun onPopularFoodItemClick(event: PopularFoodItemClick)
    {
        if(event.popularGategoryModel!=null)
        {
           dialog!!.show()
            FirebaseDatabase.getInstance()
                .getReference("Category")
                .child(event.popularGategoryModel!!.menu_id!!)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                       dialog!!.dismiss()
                        Toast.makeText(this@UserActivity,""+p0.message,Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()){
                            Common.categorySelected = p0.getValue(CategoryModel::class.java)
                            Common.categorySelected!!.menu_id = p0.key

                            //Load Food
                            FirebaseDatabase.getInstance().getReference("Category")
                                .child(event.popularGategoryModel!!.menu_id!!)
                                .child("foods").orderByChild("id")
                                .equalTo(event.popularGategoryModel!!.food_id!!)
                                .limitToLast(1)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        dialog!!.dismiss()
                                        Toast.makeText(this@UserActivity,""+p0.message,Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        if(p0.exists()){

                                            for(foodSnapShot in p0.children)
                                            {
                                                Common.foodSelected = foodSnapShot.getValue(FoodModel::class.java)
                                                Common.foodSelected!!.key =foodSnapShot.key
                                            }
                                                navController!!.navigate(R.id.nav_food_detail)

                                        }else
                                        {
                                            Toast.makeText(this@UserActivity,"Item doesn't exists",Toast.LENGTH_SHORT).show()

                                        }
                                        dialog!!.dismiss()

                                    }
                                })
                        }
                        else{
                            dialog!!.dismiss()
                            Toast.makeText(this@UserActivity,"Item doesn't exists",Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@UserActivity,""+p0.message,Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()){
                            Common.categorySelected = p0.getValue(CategoryModel::class.java)
                            Common.categorySelected!!.menu_id = p0.key
                            //Load Food
                            FirebaseDatabase.getInstance().getReference("Category")
                                .child(event.model!!.menu_id!!)
                                .child("foods").orderByChild("id")
                                .equalTo(event.model!!.food_id!!)
                                .limitToLast(1)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        dialog!!.dismiss()
                                        Toast.makeText(this@UserActivity,""+p0.message,Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        if(p0.exists()){

                                            for(foodSnapShot in p0.children)
                                            {
                                                Common.foodSelected = foodSnapShot.getValue(FoodModel::class.java)
                                                Common.foodSelected!!.key =foodSnapShot.key
                                            }
                                            navController!!.navigate(R.id.nav_food_detail)

                                        }else
                                        {
                                            Toast.makeText(this@UserActivity,"Item doesn't exists",Toast.LENGTH_SHORT).show()

                                        }
                                        dialog!!.dismiss()

                                    }
                                })
                        }
                        else{
                            dialog!!.dismiss()
                            Toast.makeText(this@UserActivity,"Item doesn't exists",Toast.LENGTH_SHORT).show()
                        }
                    }


                })
        }

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public fun onMenuItemBack(event: MenuItemBack)
    {
        menuItemClick = -1
        if(supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack();
    }

    private fun countCartItem() {
        var user = mAuth.currentUser
        if (user != null) {
           // Toast.makeText(this@UserActivity, "${user.uid}", Toast.LENGTH_SHORT).show()
            cartDataSource.countItemInCart(user!!.uid)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Int> {
                    override fun onSuccess(t: Int) {
                        fab.count = t
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        if(!e.message!!.contains("Query returned empty"))
                        Toast.makeText(this@UserActivity, "[COUNT CART]" + e.message, Toast.LENGTH_SHORT).show()
                        else
                            fab.count = 0
                    }

                })
        }
    }


}



