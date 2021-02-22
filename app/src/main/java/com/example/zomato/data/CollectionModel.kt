package com.example.zomato.data

data class CollectionResponse(
    val collections: List<Collections>,
    val share_url: String,
)

data class Collections(
    val collection: Collection
)

data class Collection(
    val collection_id: Int,
    val description: String,
    val image_url: String,
    val res_count: Int,
    val share_url: String,
    val title: String,
    val url: String
)