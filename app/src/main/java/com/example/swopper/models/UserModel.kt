package com.example.swopper.models

data class UserModel(
        var id: String = "",
        var username: String = "",
        var phone: String = "",
        var hidden: String = "",
        var photoUrl: String = "empty",
        var status: String = "",
        var registrated: Any = "",
        var location: String = ""
) {
    override fun equals(other: Any?): Boolean {
        return (other as UserModel).id == id
    }
}