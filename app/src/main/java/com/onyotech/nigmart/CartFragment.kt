package com.onyotech.nigmart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        val cartItemsRecycler = view.findViewById<RecyclerView>(R.id.cartItemsRecycler)
        val adapter = CartRecyclerAdapter(requireContext(), listOf(CategoryItem()))
        cartItemsRecycler.adapter = adapter
        return view
    }

}