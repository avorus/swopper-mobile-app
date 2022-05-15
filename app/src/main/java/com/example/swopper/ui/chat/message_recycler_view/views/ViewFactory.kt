package com.example.swopper.ui.chat.message_recycler_view.views

import com.example.swopper.database.KEY
import com.example.swopper.database.TYPE_MESSAGE_IMAGE
import com.example.swopper.models.MessageModel
import com.example.swopper.utils.decryptMessage

class ViewFactory {
    companion object {
        fun getView(message: MessageModel): MessageView {
            return when (message.type) {
                TYPE_MESSAGE_IMAGE -> ImageMessageView(
                    id = message.id,
                    from = message.from,
                    sended = message.sended.toString(),
                    fileUrl = message.fileUrl
                )
                else -> TextMessageView(
                    id = message.id,
                    text = decryptMessage(message.text, KEY),
                    from = message.from,
                    sended = message.sended.toString()
                )
            }
        }
    }
}