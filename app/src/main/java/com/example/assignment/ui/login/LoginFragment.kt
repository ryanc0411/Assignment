@file:Suppress("DEPRECATION")

package com.example.assignment.ui.login

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.assignment.R
import com.example.assignment.SellerActivity
import com.example.assignment.UserActivity
import com.example.assignment.database.Entity.LoginAttempt
import com.example.assignment.database.Entity.Users
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment() {

    lateinit var emailText: EditText
    lateinit var  passwordText: EditText
    lateinit var ref: DatabaseReference
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        root.registerBtn.setOnClickListener{view : View ->
            view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)}
//            binding.registerBtn.setOnClickListener{view : View ->
//                view.findNavController().navigate(com.google.firebase.database.R.id.action_loginFragment_to_registerFragment)}
//            binding.forgetpwdBtn.setOnClickListener{view : View ->
//                view.findNavController().navigate(com.google.firebase.database.R.id.action_loginFragment_to_forgetPwdFragment)}




        // initiate the firebase database
        ref = FirebaseDatabase.getInstance().getReference("users")

        emailText = root.email_editText_login
        passwordText = root.password_editText_login

        root.button_login.setOnClickListener{
            val loading = ProgressDialog(context)
            loading.setMessage("Loading...")
            loading.show()

            login(loading)
        }

        return root
    }

    private fun login(loading: ProgressDialog) {
        mAuth.signInWithEmailAndPassword(emailText.text.toString(), passwordText.text.toString()).addOnCompleteListener {
            if (it.isSuccessful) {
                // get the created user
                val user = mAuth.currentUser
                // get the created user ID
                val uid = user!!.uid

                // check the login attempt limit
                var registerEmail = emailText.text.toString()
                var emailRoot = registerEmail.replace(".", " ")
                ref.child("loginAttempt").child(emailRoot).addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val currentAttempt = p0.getValue(LoginAttempt::class.java)!!.currentAttempt
                        if (currentAttempt < 4) {
                            // login attempt within valid range, proceed to login
                            resetLoginAttempt()
                            // determine the login user role
                            checkUserRole(loading, user, uid)
                        } else {
                            // login attempt exceeds 3
                            // this account has been blocked
                            loading.dismiss()
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("This email has been blocked!")
                            builder.setMessage("Please contact to administrators 011-34567890!")
                            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                                emailText.requestFocus()
                            }
                            builder.show()
                        }
                    }
                })
            } else {
                // check if email correct, but password incorrect
                // then update the login attempt
                checkEmailExistsOrNot()
                loading.dismiss()
                // pop up a error message
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Invalid email and password!")
                builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                    emailText.requestFocus()
                }
                builder.show()
            }
        }
    }

    private fun checkUserRole(loading: ProgressDialog, user: FirebaseUser, uid: String) {
        ref.child(uid).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                loading.dismiss()
                val urole = p0.getValue(Users::class.java)
                if (urole!!.userRole.equals("Customer")) {
                    // redirect to the customer page
                    val intent = Intent(activity, UserActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    Toast.makeText(activity, "Login Successful", Toast.LENGTH_SHORT).show()
                } else {
                    // redirect to the seller page
                    val intent = Intent(activity, SellerActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    Toast.makeText(activity, "Login Successful", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun checkEmailExistsOrNot(){
        mAuth.fetchSignInMethodsForEmail(emailText.text.toString())
            .addOnCompleteListener(OnCompleteListener<SignInMethodQueryResult> { task ->
                if (task.result!!.signInMethods!!.size == 0) {
                    // email and password incorrect
                } else {
                    // email correct, but password incorrect
                    // update the login attempt
                    updateLoginAttempt()
                }
            }).addOnFailureListener(OnFailureListener { e -> e.printStackTrace() })
    }

    private fun resetLoginAttempt() {
        // reset the login attempt
        var registerEmail = emailText.text.toString()
        var emailRoot = registerEmail.replace(".", " ")
        ref.child("loginAttempt").child(emailRoot).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val updateAttempt = LoginAttempt(0)
                ref.child("loginAttempt").child(emailRoot).setValue(updateAttempt)
            }
        })
    }

    private fun updateLoginAttempt() {
        var registerEmail = emailText.text.toString()
        var emailRoot = registerEmail.replace(".", " ")
        ref.child("loginAttempt").child(emailRoot).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                var counter = p0.getValue(LoginAttempt::class.java)!!.currentAttempt
                counter++
                val updateAttempt = LoginAttempt(counter)
                    ref.child("loginAttempt").child(emailRoot).setValue(updateAttempt)

            }
        })
    }

}


