package com.example.swopper.models

data class AdvertModel(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var type: String = "",
    var category: String = "",
    var photoUrl: String = "empty",
    var location: String = "",
    val posted: Any = "",
    val owner: String = ""
) {
    override fun equals(other: Any?): Boolean {
        return (other as AdvertModel).id == id
    }
}