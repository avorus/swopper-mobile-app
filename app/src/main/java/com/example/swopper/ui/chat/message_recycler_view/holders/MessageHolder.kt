package com.example.swopper.ui.chat.message_recycler_view.holders

import com.example.swopper.ui.chat.message_recycler_view.views.MessageView

interface MessageHolder {
    fun drawMessage(view: MessageView)
    fun onAttach(view: MessageView)
    fun onDetach()
}