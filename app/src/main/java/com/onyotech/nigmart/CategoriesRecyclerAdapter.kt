package com.onyotech.nigmart

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView

class CategoriesRecyclerAdapter(
    private val context: Context,
    private val navController: NavController,
    private val categories: List<Category>
    ):
    RecyclerView.Adapter<CategoriesRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val frame: FrameLayout = itemView.findViewById(R.id.frame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.categories_recycler_items, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryImage.setImageResource(category.categoryImage)
        holder.categoryName.text = category.categoryName
        holder.frame.setOnClickListener{
            openCategoryItem(category)
        }
    }

    private fun openCategoryItem(category: Category) {
        Log.d(TAG, "Opening Category: ${category.categoryName}")
        val action = CategoriesFragmentDirections
            .actionCategoriesFragmentToCategoryItemFragment4(category.categoryName)
        navController.navigate(action)
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}