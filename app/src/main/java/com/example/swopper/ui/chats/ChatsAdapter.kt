package com.example.swopper.ui.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swopper.R
import com.example.swopper.models.CommonModel
import com.example.swopper.ui.chat.ChatFragment
import com.example.swopper.utils.*
import com.github.siyamed.shapeimageview.RoundedImageView
import kotlinx.android.synthetic.main.item_chat.view.*

class ChatsAdapter : RecyclerView.Adapter<ChatsAdapter.ChatsHolder>() {

    private var chats = mutableListOf<CommonModel>()

    class ChatsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemChatAdvertImage: RoundedImageView = view.item_chat_advert_image
        val itemChatUserUsername: TextView = view.item_chat_user_username
        val itemChatAdvertName: TextView = view.item_chat_advert_name
        val itemChatLastMessageText: TextView = view.item_chat_last_message_text
        val itemChatLastMessageData: TextView = view.item_chat_last_message_data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)

        val holder = ChatsHolder(view)
        holder.itemView.setOnClickListener {
            replaceFragment(ChatFragment(chats[holder.adapterPosition].user, chats[holder.adapterPosition].advert), R.id.chatsFrameLayout)
        }
        return holder
    }

    override fun getItemCount(): Int = chats.size

    override fun onBindViewHolder(holder: ChatsHolder, position: Int) {
        holder.itemChatAdvertImage.setImage(chats[position].photoUrl, R.drawable.im_default_advert)
        holder.itemChatUserUsername.text = chats[position].username
        holder.itemChatAdvertName.text = chats[position].name
        holder.itemChatLastMessageText.text = chats[position].text
        holder.itemChatLastMessageData.text = chats[position].sended.toString().asDate()
    }

    fun update(item: CommonModel){
        chats.add(item)
        notifyItemInserted(chats.size)
    }
}