
package com.example.assignment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.assignment.EventBus.CategoryClick
import com.example.assignment.EventBus.CountCartEvent
import com.example.assignment.EventBus.FoodItemClick
import com.example.assignment.EventBus.navigate
import com.example.assignment.database.Entity.CartDataSource
import com.example.assignment.database.Entity.CartDatabase
import com.example.assignment.database.Entity.LocalCartDataSource
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
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
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //Event Bus
    override fun onStart(){
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true,threadMode=ThreadMode.MAIN)
    fun onCategorySelected(event: CategoryClick)
    {
        if(event.isSuccess)
        {
            //Toast.makeText(this,"Click to"+event.category.name,Toast.LENGTH_SHORT).show()
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_list)
        }

    }

    @Subscribe(sticky = true,threadMode=ThreadMode.MAIN)
    fun onFoodSelected(event: FoodItemClick)
    {
        if(event.isSuccess)
        {
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_detail)
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
}

