package com.example.swopper.ui.chat.message_recycler_view.holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.swopper.database.CURRENT_UID
import com.example.swopper.ui.chat.message_recycler_view.views.MessageView
import com.example.swopper.utils.asDateTime
import kotlinx.android.synthetic.main.item_text_message.view.*

class TextMessageHolder(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private  val inboundMessageContainer: ConstraintLayout = view.inbound_text_message_container
    private  val inboundMessage: TextView = view.inbound_text_message
    private  val inboundMessageDatetime: TextView = view.inbound_text_message_datetime
    private  val outboundMessageContainer: ConstraintLayout = view.outbound_text_message_container
    private  val outboundMessage: TextView = view.outbound_text_message
    private  val outboundMessageDatetime: TextView = view.outbound_text_message_datetime

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            inboundMessageContainer.visibility = View.VISIBLE
            outboundMessageContainer.visibility = View.GONE
            inboundMessage.text = view.text
            inboundMessageDatetime.text = view.sended.asDateTime()
        } else {
            inboundMessageContainer.visibility = View.GONE
            outboundMessageContainer.visibility = View.VISIBLE
            outboundMessage.text = view.text
            outboundMessageDatetime.text = view.sended.asDateTime()
        }
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDetach() {
    }
}