package com.example.zomato.data

import java.io.Serializable

data class CategoryResponse(
    val categories: List<Category>
)

data class Category(
    val categories: Categories
) : Serializable

data class Categories(
    val id: Int,
    val name: String
)