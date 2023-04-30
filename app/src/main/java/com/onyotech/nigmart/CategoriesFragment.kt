package com.onyotech.nigmart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView

class CategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_categories, container, false)
        val categoriesRecycler = view.findViewById<RecyclerView>(R.id.categoriesRecycler)
        categoriesRecycler.adapter = CategoriesRecyclerAdapter(requireContext(), findNavController(), DataManager.categories)

        return view
    }
}