package com.example.swopper.models

data class MessageModel(
    val id: String = "",
    var type: String = "",
    var text: String = "",
    var from: String = "",
    var sended: Any = "",
    var fileUrl: String = "empty"
) {
    override fun equals(other: Any?): Boolean {
        return (other as MessageModel).id == id
    }
}