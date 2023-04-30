package com.onyotech.nigmart

import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        // Define the views in sign up form
        val usernameInput = view.findViewById<TextInputLayout>(R.id.usernameInput)
        val emailInput = view.findViewById<TextInputLayout>(R.id.emailInput)
        val passwordInput = view.findViewById<TextInputLayout>(R.id.passwordInput)
        val confirmPasswordInput = view.findViewById<TextInputLayout>(R.id.confirmPasswordInput)
        val keepMeLoggedInCheckBox = view.findViewById<CheckBox>(R.id.keepMeLoggedIn)

        // Implement signup button
        val signUpBtn = view.findViewById<Button>(R.id.signUpButton)
        signUpBtn.setOnClickListener {
            // get content from sign up form
            val username = usernameInput.editText?.text.toString().trim()
            val email = emailInput.editText?.text.toString().trim()
            val password = passwordInput.editText?.text.toString()
            val confirmPassword = confirmPasswordInput.editText?.text.toString()
            val keepMeLoggedIn = keepMeLoggedInCheckBox.isChecked

            // check authenticity of sign up details
            checkAuthenticity(username, email, password, confirmPassword, keepMeLoggedIn)
        }

        // Button to go back to login page
        val loginBtn = view.findViewById<TextView>(R.id.login)
        // go back to login page
        loginBtn.setOnClickListener { parentFragmentManager.popBackStack() }

        auth = Firebase.auth
        database = Firebase.database.reference

        return view
    }

    private fun checkAuthenticity(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        keepMeLoggedIn: Boolean
    ) {
        if (checkEntry(username, email)) {
            // check authenticity of email
            val emailAuth = checkEmailAuthenticity(email)
            // check if password is valid
            val passwordAuth = checkPasswordAuthenticity(password, confirmPassword)

            // sign up user if email and password is valid
            if (emailAuth && passwordAuth) {
                // save keepMeLoggedIn info in shared preferences file
                setAutomaticLogin(keepMeLoggedIn)
                // signup user using email and password
                signUpUser(username, email, password)
            } else {
                Log.d(TAG, "User Sign up Failed!")
            }
        }
    }

    private fun checkEntry(username: String, email: String): Boolean {
        return when {
            // check if username is empty
            username.isEmpty() -> {
                Toast.makeText(context, "Please enter a valid username", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Invalid username")
                false
            }
            // check if email is empty
            email.isEmpty() -> {
                Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Invalid email address")
                false
            }
            else -> true
        }
    }

    private fun signUpUser(username: String, email: String, password: String) {
        // sign up user using email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // if task is successful, set user DisplayName to 'username'
                    Log.d(TAG, "Sign up successful")
                    val profile = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    auth.currentUser?.updateProfile(profile)

                    // add user id to database
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        writeNewUser(userId, username, email)
                    }

                    // Open NigMart Store
                    Log.d(TAG, "Opening NigMart Store")
                    val action = R.id.action_signUpFragment_to_nigmartStoreFragment
                    view?.findNavController()?.navigate(action)
                }
                else {
                    // If sign in fails, display a message to the user.
                    val message = task.exception?.localizedMessage
                    if (message != null)
                        Log.d(TAG, message)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun writeNewUser(userId: String, username: String, email: String) {
        val user = User(username, email)
        database.child("users").child(userId).setValue(user)
    }

    private fun setAutomaticLogin(keepMeLoggedIn: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEEP_ME_LOGGED_IN, keepMeLoggedIn)
        editor.apply()
        Log.d(TAG, "KeepMeLoggedIn: $keepMeLoggedIn saved in shared preferences")
    }

    private fun checkPasswordAuthenticity(password: String, confirmPassword: String): Boolean {
        Log.d(TAG, "Password Authenticity Check Executed")

        when {
            // check if password is more than 7 characters
            password.length < 8 -> {
                Toast.makeText(context, "Password is too short!", Toast.LENGTH_SHORT)
                    .show()
                Log.d(TAG, "Password too short")
                return false
            }
            // check if password and confirmPassword match
            password != confirmPassword -> {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT)
                    .show()
                Log.d(TAG, "Passwords do not match")
                return false
            }
            // return true if password is more than 7 characters long and matches confirmPassword
            else -> {
                Log.d(TAG, "Password Authenticity Confirmed")
                return true
            }
        }
    }

    private fun checkEmailAuthenticity(email: String): Boolean {
        var isNewUser: Boolean? = true

        // check if email address has been used
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                // set isNewUser variable if task is successful
                if (task.isSuccessful){
                    isNewUser = task.result.signInMethods?.isEmpty()
                    Log.d(TAG, "checkEmailAuthenticity Completed; isNewUser: $isNewUser")

                // display an error message if task is not successful
                } else {
                    val message = task.exception?.localizedMessage

                    // if localized message is null, display the entire exception in log
                    if (message == null) {
                        Log.w(TAG, task.exception)
                    } else {
                        Log.d(TAG, message)
                    }

                    // display error message to the user
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }

        // return true if 'isNewUser' is null otherwise return the value of 'isNewUser'
        return if (isNewUser == null) {
            true
        } else {
            // display a message if the email address has been used
            if (isNewUser == false) {
                Log.d(TAG, "There is already an account associated with the email: $email")

                Toast.makeText(
                    context,
                    "There is already an account associated with this email!",
                    Toast.LENGTH_LONG)
                    .show()
            }
            isNewUser!!
        }
    }
}