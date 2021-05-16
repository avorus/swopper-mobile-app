package com.example.swopper.ui.chat.message_recycler_view.views

data class ImageMessageView(
    override val id: String,
    override val text: String = "",
    override val from: String,
    override val sended: String,
    override val fileUrl: String
) : MessageView {
    override fun getTypeView(): Int {
        return MessageView.MESSAGE_IMAGE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}