package com.example.swopper.ui.chat.message_recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.swopper.ui.chat.message_recycler_view.holders.HolderFactory
import com.example.swopper.ui.chat.message_recycler_view.holders.MessageHolder
import com.example.swopper.ui.chat.message_recycler_view.views.MessageView


class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mMessagesCache = mutableListOf<MessageView>()
    private var mHolders = mutableListOf<MessageHolder>()


    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        return HolderFactory.getHolder(parent, type)
    }

    override fun getItemViewType(position: Int): Int {
        return mMessagesCache[position].getTypeView()
    }

    override fun getItemCount(): Int = mMessagesCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolder).drawMessage(mMessagesCache[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onAttach(mMessagesCache[holder.adapterPosition])
        mHolders.add((holder as MessageHolder))
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onDetach()
        mHolders.remove((holder as MessageHolder))
        super.onViewDetachedFromWindow(holder)
    }

    fun addItemToBottom(
        item: MessageView,
        onSuccess: () -> Unit
    ) {
        if (!mMessagesCache.contains(item)) {
            mMessagesCache.add(item)
            notifyItemInserted(mMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(
        item: MessageView,
        onSuccess: () -> Unit
    ) {
        if (!mMessagesCache.contains(item)) {
            mMessagesCache.add(item)
            mMessagesCache.sortBy { it.sended.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun onDestroy() {
        mHolders.forEach {
            it.onDetach()
        }
    }
}