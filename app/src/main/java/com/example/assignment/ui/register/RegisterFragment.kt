package com.example.assignment.ui.register

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.assignment.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import androidx.databinding.DataBindingUtil
import com.example.assignment.database.Entity.LoginAttempt
import com.example.assignment.database.Entity.Users
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_register.view.*
import java.util.regex.Pattern.compile

class RegisterFragment : Fragment() {

    lateinit var nameText: EditText
    lateinit var emailText: EditText
    lateinit var  passwordText: EditText
    lateinit var retypeText: EditText
    lateinit var custBtn: RadioButton
    lateinit var sellBtn: RadioButton
    lateinit var ref: DatabaseReference
    val mAuth = FirebaseAuth.getInstance()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val root = inflater.inflate(R.layout.fragment_register, container, false)


        // initiate the firebase database
        ref = FirebaseDatabase.getInstance().getReference("users")

        // get those edit text and save into a variable
        nameText = root.nameEditText
        emailText = root.emailEditText
        passwordText = root.passwordEditText
        retypeText = root.retypePasswordEditText
        custBtn = root.customerButton
        sellBtn = root.sellerButton

        root.signupBtn.setOnClickListener {
            var isValid = checkUserInput()

            // once all the input fields are valid, then go into this if statement
            if (isValid) {
                // start loading to prevent the user clicks sign up button many times
                val loading = ProgressDialog(context)
                loading.setMessage("Loading...")
                loading.show()

                // check the existing registered email
                checkEmailExistsOrNot(loading)
            }
        }
            return root
    }

    private fun checkUserInput(): Boolean {
        var isValid = true
        val patterns = compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%!\\-_?&])(?=\\S+\$).{8,}")
        // ensure there is not any blank edit text
        if (nameText.text.toString().trim().isEmpty()) {
            nameText.setError("Name is required")
            isValid = false
        }
        if (emailText.text.toString().trim().isEmpty()) {
            emailText.setError("Email is required")
            isValid = false
        }
        if (passwordText.text.toString().isEmpty()) {
            passwordText.setError("Password is required")
            isValid = false
        }
        else if((!patterns.matcher(passwordText.text.toString()).matches())){
            passwordText.setError("Password format need to include uppercase, lowercase, number and symbol")
            isValid = false
        }

        if (retypeText.text.toString().isEmpty()) {
            retypeText.setError("Retype password is required")
            isValid = false
        }

        // ensure the password and retype password are same
        if (!passwordText.text.toString().equals(
                retypeText.text.toString(), false) && isValid) {
            Toast.makeText(activity, "Password do not match", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText.text.toString()).matches()) {
            view?.let {
                Snackbar.make(it, "Please enter valid email!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                isValid = false
            }
            emailText.requestFocus()
        }

        return isValid
    }

    private fun checkEmailExistsOrNot(loading: ProgressDialog){
        mAuth.fetchSignInMethodsForEmail(emailText.text.toString())
            .addOnCompleteListener(OnCompleteListener<SignInMethodQueryResult> { task ->
                if (task.result!!.signInMethods!!.size == 0) {
                    // email not existed, then go to create account
                    createNewUser(loading)
                } else {
                    // email existed
                    loading.dismiss()
                    // pop up a error message
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("This email already registered!")
                    builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                        emailText.requestFocus()
                    }
                    builder.show()
                }
            }).addOnFailureListener(OnFailureListener { e -> e.printStackTrace() })
    }

    private fun createNewUser(loading: ProgressDialog) {
        mAuth.createUserWithEmailAndPassword(emailText.text.toString(), passwordText.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // get the created user
                    val user = mAuth.currentUser
                    // get the created user ID
                    val uid = user!!.uid

                    // set the default login attempt to this account
                    val defaultLoginAttempt = LoginAttempt(0)
                    var registerEmail = emailText.text.toString()
                    var emailRoot = registerEmail.replace(".", " ")
                    ref.child("loginAttempt").child(emailRoot).setValue(defaultLoginAttempt)

                    if (custBtn.isChecked) {
                        // register as a customer
                        val newUser = Users(uid, nameText.text.toString(), "Customer")
                        ref.child(uid).setValue(newUser).addOnCompleteListener {
                            loading.dismiss()
                            //pop up a dialog, after user click ok, it will redirect to login page
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Register Successful!")
                            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                                mAuth.signOut()
                                view?.findNavController()?.navigate(R.id.nav_login)
                            }
                            builder.show()
                        }
                    } else {
                        // register as a seller
                        val newUser = Users(uid, nameText.text.toString(), "Seller")
                        ref.child(uid).setValue(newUser).addOnCompleteListener {
                            loading.dismiss()
                            //pop up a dialog, after user click ok, it will redirect to login page
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("Register Successful!")
                            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                                mAuth.signOut()
                                view?.findNavController()?.navigate(R.id.nav_login)
                            }
                            builder.show()
                        }
                    }
                }
            }.addOnFailureListener {
                loading.dismiss()
                // pop up a error message
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Invalid email format!")
                builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                    emailText.requestFocus()
                }
                builder.show()
            }
    }



}



