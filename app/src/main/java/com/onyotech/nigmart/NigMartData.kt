package com.onyotech.nigmart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Category(val categoryImage: Int, val categoryName: String)

@Parcelize
data class CategoryItem(
    var id: Int = 0,
    var image: String? = null,
    var name: String? = null,
    var price: Int? = null,
    var quantity: String? = null
): Parcelable


data class User(var username: String, val email: String)