package com.example.assignment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.assignment.Common.Common
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class SellerActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private var menuItemClick=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_seller_layout)
        val navView: NavigationView = findViewById(R.id.nav_seller)
        navController = findNavController(R.id.nav_seller_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_logout,R.id.nav_food_list,R.id.nav_login,R.id.nav_logout), drawerLayout)
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("username1", Context.MODE_PRIVATE )
        val textView: TextView = findViewById<TextView>(R.id.Usernametext)
        val username = sharedPreferences.getString("username","TARUC FOOD")
        textView.text =  username

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_seller_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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


}