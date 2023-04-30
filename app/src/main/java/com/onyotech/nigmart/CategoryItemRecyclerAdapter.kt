package com.onyotech.nigmart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CategoryItemRecyclerAdapter(
    private val context: Context,
    private val navController: NavController,
    private val categoryItems: List<CategoryItem>,
    private val categoryName: String
): RecyclerView.Adapter<CategoryItemRecyclerAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val priceLabel: TextView = itemView.findViewById(R.id.priceLabel)
        val quantityLabel: TextView = itemView.findViewById(R.id.quantityLabel)
        val itemTag: TextView = itemView.findViewById(R.id.itemTag)
        val itemCard: ConstraintLayout = itemView.findViewById(R.id.item_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.category_item_recycler_items, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryItem = categoryItems[position]
        Picasso.get().load(categoryItem.image).into(holder.itemImage)
        holder.itemName.text = categoryItem.name
        // val itemPrice = "N ${categoryItem.price}"
        val itemPrice = context.getString(R.string.price_holder, categoryItem.price)
        holder.priceLabel.text = itemPrice
        holder.quantityLabel.text = categoryItem.quantity
        holder.itemTag.text = categoryName
        holder.itemCard.setOnClickListener { viewItem(categoryItem) }
    }

    private fun viewItem(categoryItem: CategoryItem) {
        val action = CategoryItemFragmentDirections
            .actionCategoryItemFragmentToItemDetailsFragment(categoryItem, categoryName)
        navController.navigate(action)
    }

    override fun getItemCount(): Int {
        return categoryItems.size
    }
}