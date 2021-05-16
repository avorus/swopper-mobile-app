package com.example.swopper.ui.chat.message_recycler_view.views

interface MessageView {

    val id: String
    val text: String
    val from: String
    val sended: String
    val fileUrl: String

    companion object {
        val MESSAGE_IMAGE: Int
            get() = 0
        val MESSAGE_TEXT: Int
            get() = 1
    }

    fun getTypeView(): Int
}