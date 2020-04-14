@file:Suppress("DEPRECATION")

package com.example.assignment.ui.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.assignment.MainActivity
import com.example.assignment.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_logout.view.*


class Logout : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_logout, container, false)

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Toast.makeText(activity, "Logout Successful", Toast.LENGTH_SHORT).show()

        return root
    }






}