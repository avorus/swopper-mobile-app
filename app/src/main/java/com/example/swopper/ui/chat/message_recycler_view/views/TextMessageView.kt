package com.example.swopper.ui.chat.message_recycler_view.views

data class TextMessageView(
    override val id: String,
    override val text: String,
    override val from: String,
    override val sended: String,
    override val fileUrl: String = ""
) : MessageView {
    override fun getTypeView(): Int {
        return MessageView.MESSAGE_TEXT
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}