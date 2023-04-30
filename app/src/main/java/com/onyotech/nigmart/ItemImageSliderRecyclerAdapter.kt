package com.onyotech.nigmart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ItemImageSliderRecyclerAdapter(
    private val context: Context,
    private val selectedImage: ImageView,
    private val imageUrls: List<String>
): RecyclerView.Adapter<ItemImageSliderRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageSliderItem: ImageView = itemView.findViewById(R.id.imageSliderItemImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.image_slider_list_items, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Picasso.get().load(imageUrl).into(holder.imageSliderItem)
        holder.imageSliderItem.setOnClickListener {
            Picasso.get().load(imageUrl).into(selectedImage)
        }
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}