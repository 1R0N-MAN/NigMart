package com.onyotech.nigmart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class CategoryItemFragment : Fragment() {

    private val args: CategoryItemFragmentArgs by navArgs()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: CategoryItemRecyclerAdapter
    private lateinit var categoryItemRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val categoryItems = mutableListOf<CategoryItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category_item, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        val categoryName = args.categoryName
        Log.d(TAG, "CategoryItemFragment: categoryName=$categoryName")
        databaseReference = Firebase.database.reference.child("categories").child(categoryName)
        Log.d(TAG, "Database Reference: $databaseReference")

        categoryItemRecyclerView = view.findViewById(R.id.categoryItemRecycler)
        categoryItems.clear()
        adapter = CategoryItemRecyclerAdapter(requireContext(), findNavController(), categoryItems, categoryName)
        val childEventListener = getChildEventListener()
        databaseReference.addChildEventListener(childEventListener)

        return view
    }

    private fun getChildEventListener(): ChildEventListener{
        val childEventListener = object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded: ${snapshot.key}")
                val categoryItem = snapshot.getValue<CategoryItem>()
                Log.d(TAG, "onChildAdded: Category Item: ${categoryItem?.name}")
                Log.d(TAG, "onChildAdded: Category Item Image: ${categoryItem?.image}")
                categoryItems.add(categoryItem!!)
                adapter.notifyDataSetChanged()
                progressBar.visibility = ProgressBar.INVISIBLE
                Log.d(TAG, "Progress Bar Set to Invisible")
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${snapshot.key}")
                adapter.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved: ${snapshot.key}")
                val categoryItem: CategoryItem? = snapshot.getValue<CategoryItem>()
                categoryItems.remove(categoryItem)
                adapter.notifyDataSetChanged()

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved: ${snapshot.key}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
                Toast.makeText(context, "Failed to load items!", Toast.LENGTH_SHORT).show()
            }
        }

        categoryItemRecyclerView.adapter = adapter
        return childEventListener
    }
}