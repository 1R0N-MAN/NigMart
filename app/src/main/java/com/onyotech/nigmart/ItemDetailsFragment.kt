package com.onyotech.nigmart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ItemDetailsFragment : Fragment() {

    private val args: ItemDetailsFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var itemId: String
    private var itemInCart: Boolean = false
    private lateinit var addToCartButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_item_details, container, false)

        // load the views in the layout
        val itemImage = view.findViewById<ImageView>(R.id.itemImage)
        val itemName = view.findViewById<TextView>(R.id.itemName)
        val priceLabel = view.findViewById<TextView>(R.id.priceLabel)
        val quantityLabel = view.findViewById<TextView>(R.id.quantityLabel)
        val itemImageSliderRecycler = view.findViewById<RecyclerView>(R.id.itemImageSlider)

        addToCartButton = view.findViewById(R.id.addToCartButton)
        addToCartButton.setOnClickListener{
            // change text in addToCartButton
            if (itemInCart){
                // remove item from cart in database
                removeItemFromCart()
            }
            else {
                // add item to cart in database
                addItemToCart()
            }
        }

        // get item info from arguments in previous fragment
        val categoryItem = args.categoryItem

        // get database reference for item
        setItemsInCartDatabaseReference()

        // get name of category in which the item is in
        val categoryName = args.category

        // create an id for the item
        itemId = "$categoryName-${categoryItem.id}-${categoryItem.name}"
        getItemInCart()
        Log.d(TAG, "Item in Cart Extracted: itemId=$itemId, itemInCart=$itemInCart")

        Picasso.get().load(categoryItem.image).into(itemImage)
        Log.d(TAG, "Loaded Image into View!")

        itemName.text = categoryItem.name
        val price = "N ${categoryItem.price}"
        priceLabel.text = price
        quantityLabel.text = categoryItem.quantity

        val dummyUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/4/4a/Commons-logo.svg/30px-Commons-logo.svg.png"
        val categoryItemImage = categoryItem.image?:"empty"
        val imageUrls = listOf(categoryItemImage, categoryItemImage, categoryItemImage, dummyUrl)

        val itemImageSliderRecyclerAdapter = ItemImageSliderRecyclerAdapter(
            requireContext(), itemImage, imageUrls)
        itemImageSliderRecycler.adapter = itemImageSliderRecyclerAdapter

        return view
    }

    private fun setItemsInCartDatabaseReference() {
        auth = Firebase.auth
        val userId = auth.currentUser?.uid
        if (userId != null){
            database = Firebase.database.reference
                .child("users")
                .child(userId)
                .child("cart")
        }
    }

    private fun getItemInCart() {
        // get value of item with index itemId
        database.child(itemId).get().addOnSuccessListener {
            // get list in items_in_cart, if list is null, return the empty mutable list
            itemInCart = it.getValue<Boolean>()?:false
            Log.d(TAG, "Got value $itemInCart")

            if (itemInCart){
                addToCartButton.text = getString(R.string.added_to_cart)
                Log.d(TAG, "Add to cart button set")
            }


        }.addOnFailureListener {
            Log.e(TAG, "Error getting data", it)
        }
    }

    private fun addItemToCart() {
        addToCartButton.text = getString(R.string.added_to_cart)
        itemInCart = true
        database.child(itemId).setValue(itemInCart)
    }

    private fun removeItemFromCart(){
        addToCartButton.text = getString(R.string.add_to_cart)
        itemInCart = false
        database.child(itemId).setValue(itemInCart)
    }
}