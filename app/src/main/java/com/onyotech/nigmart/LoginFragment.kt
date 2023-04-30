package com.onyotech.nigmart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Load Views
        val emailInput = view.findViewById<TextInputLayout>(R.id.emailInput)
        val passwordInput = view.findViewById<TextInputLayout>(R.id.passwordInput)
        val keepMeLoggedInCheckBox = view.findViewById<CheckBox>(R.id.keepMeLoggedIn)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val forgotPasswordButton = view.findViewById<TextView>(R.id.forgotPassword)

        forgotPasswordButton.setOnClickListener {
            //TODO: Implement Forgot Password Feature
        }

        // Implement Login Button
        loginButton.setOnClickListener {
            // get content from login form
            val email = emailInput.editText?.text.toString().trim()
            val password = passwordInput.editText?.text.toString().trim()
            val keepMeLoggedIn = keepMeLoggedInCheckBox.isChecked

            if (checkEntry(email, password)){
                loginUser(email, password, keepMeLoggedIn)
            }
        }

        // Button to go to sign up page
        val signUpButton = view.findViewById<TextView>(R.id.signUp)
        signUpButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_signUpFragment)
        )

        auth = Firebase.auth

        val navController = findNavController()
        checkIfUserIsLoggedIn(navController)
        return view
    }

    private fun checkIfUserIsLoggedIn(navController: NavController) {
        if (auth.currentUser != null){
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val isLoggedIn = sharedPreferences.getBoolean(KEEP_ME_LOGGED_IN, false)

            if (isLoggedIn){
                // Open NigMart Store
                Log.d(TAG, "User is Logged In! Opening NigMart Store...")
                val action = R.id.action_loginFragment_to_nigmartStoreFragment
                navController.navigate(action)
            }
            else {
                Log.d(TAG, "User not logged in! Opening Login Page...")
            }
        }
    }

    private fun loginUser(email: String, password: String, keepMeLoggedIn: Boolean) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "Login Successful")
                    // Save keepMeLoggedIn info in Shared Preferences
                    setAutomaticLogin(keepMeLoggedIn)

                    // Open NigMart Store
                    Log.d(TAG, "Opening NigMart Store")
                    val action = R.id.action_loginFragment_to_nigmartStoreFragment
                    view?.findNavController()?.navigate(action)
                } else {
                    // display message if login is not successful
                    val message = task.exception?.localizedMessage
                    if (message != null){
                        Log.d(TAG, message)
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun setAutomaticLogin(keepMeLoggedIn: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEEP_ME_LOGGED_IN, keepMeLoggedIn)
        editor.apply()
        Log.d(TAG, "KeepMeLoggedIn: $keepMeLoggedIn saved in shared preferences")
    }

    private fun checkEntry(email: String, password: String): Boolean {
        return when {
            // check if email is empty
            email.isEmpty() -> {
                Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Invalid username")
                false
            }
            // check if password is empty
            password.isEmpty() -> {
                Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Invalid email address")
                false
            }
            else -> true
        }
    }
}