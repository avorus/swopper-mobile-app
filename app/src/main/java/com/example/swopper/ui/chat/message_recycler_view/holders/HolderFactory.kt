package com.example.swopper.ui.chat.message_recycler_view.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.swopper.R
import com.example.swopper.ui.chat.message_recycler_view.views.MessageView

class HolderFactory {
    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                MessageView.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_image_message, parent, false)
                    ImageMessageHolder(view)
                }

                else ->{
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_text_message, parent, false)
                    TextMessageHolder(view)
                }
            }
        }
    }
}