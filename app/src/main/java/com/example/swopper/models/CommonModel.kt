package com.example.swopper.models

data class CommonModel(
    var id: String = "",
    var user: String = "",
    var username: String = "",
    var advert: String = "",
    var name: String = "",
    var photoUrl: String = "empty",
    var text: String = "",
    var sended: Any = ""
) {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}