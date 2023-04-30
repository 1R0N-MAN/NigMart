package com.onyotech.nigmart

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class NigMartStoreFragment : Fragment() {

    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_nigmart_store, container, false)

        // Implement the nav drawer
        drawerLayout = view.findViewById(R.id.drawer_layout)
        actionBarToggle = ActionBarDrawerToggle(
            requireActivity(), drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()

        val navMenu = view.findViewById<ImageButton>(R.id.nav_menu)
        navMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val navView = view.findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.myProfile -> {
                    displayMessage("My Profile Selected")
                }
                R.id.accountSettings -> {
                    displayMessage("Account Settings Selected")
                }
                R.id.logout -> {
                    Toast.makeText(context, "Logging Out...", Toast.LENGTH_SHORT).show()
                    resetKeepMeLoggedIn()
                    val auth = Firebase.auth
                    auth.signOut()

                    val name = parentFragmentManager.getBackStackEntryAt(0).name
                    parentFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        auth = Firebase.auth

        loadUI(navView)

        val goToCartButton = view.findViewById<ImageButton>(R.id.goToCart)
        goToCartButton.setOnClickListener {
            val nigmartStoreFragment = view.findViewById<FragmentContainerView>(R.id.nigmart_store_nav_host_fragment)
            nigmartStoreFragment.findNavController().navigate(R.id.cartFragment)
        }

        return view
    }

    private fun loadUI(navView: NavigationView) {
        val header = navView.getHeaderView(0)

        // Set user email and username to nav drawer profile
        val user = auth.currentUser
        val username = user?.displayName
        val email = user?.email

        val navDrawerUsername = header.findViewById<TextView>(R.id.navDrawerUserName)
        val navDrawerEmail = header.findViewById<TextView>(R.id.navDrawerEmail)

        navDrawerUsername.text = username
        navDrawerEmail.text = email

    }

    private fun resetKeepMeLoggedIn() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEEP_ME_LOGGED_IN, false)
        editor.apply()
        Log.d(TAG, "Logout Operation Called! KeepMeLoggedIn reset to false!")
    }

    private fun displayMessage(message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}