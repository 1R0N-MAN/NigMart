package com.onyotech.nigmart

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CartRecyclerAdapter(
    private val context: Context,
    private val cartItems: List<CategoryItem>): RecyclerView.Adapter<CartRecyclerAdapter.ViewHolder>() {

        class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
            val itemName: TextView = itemView.findViewById(R.id.itemName)
            val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
            val removeFromCartButton: Button = itemView.findViewById(R.id.removeFromCartButton)

            // Item Quantity Layout
            val increaseQuantity: ImageView = itemView.findViewById(R.id.increaseQuantity)
            val decreaseQuantity: ImageView = itemView.findViewById(R.id.decreaseQuantity)
            val itemQuantity: TextView = itemView.findViewById(R.id.itemQuantity)
            val unitTag: TextView = itemView.findViewById(R.id.unitTag)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.cart_recycler_items, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartItems[position]
        Picasso.get().load(cartItem.image).into(holder.itemImage)
        holder.itemName.text = cartItem.name
        holder.itemPrice.text = context.getString(R.string.price_holder, cartItem.price)
        holder.itemQuantity.text = "1"
        holder.unitTag.text = cartItem.quantity
        holder.removeFromCartButton.setOnClickListener { removeItemFromCart() }
        holder.increaseQuantity.setOnClickListener { increaseQuantity(holder, cartItem.price) }
        holder.decreaseQuantity.setOnClickListener { decreaseQuantity(holder, cartItem.price) }
    }

    private fun increaseQuantity(holder: ViewHolder, price: Int?) {
        val itemQuantity = holder.itemQuantity
        var quantity = 1
        try {
            quantity = itemQuantity.text.toString().toInt() + 1
        } catch (e: Exception){
            Log.w(TAG, "Error: $e")
        }

        itemQuantity.text = quantity.toString()
        setPrice(holder, price!!)
    }

    private fun decreaseQuantity(holder: ViewHolder, price: Int?) {
        val itemQuantity = holder.itemQuantity
        var quantity = 1
        try {
            quantity = itemQuantity.text.toString().toInt() - 1
        } catch (e: Exception){
            Log.w(TAG, "Error: $e")
        }

        if (quantity < 1){
            quantity = 1
        }

        itemQuantity.text = quantity.toString()
        setPrice(holder, price!!)
    }

    private fun setPrice(holder: ViewHolder, price: Int) {
        val quantity = holder.itemQuantity.text.toString().toInt()
        val amount = quantity * price
        holder.itemPrice.text = context.getString(R.string.price_holder, amount)
    }

    private fun removeItemFromCart() {
        //TODO: Implement removeItemFromCart() function
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}