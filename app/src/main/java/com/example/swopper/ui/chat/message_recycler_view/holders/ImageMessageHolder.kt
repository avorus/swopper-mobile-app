package com.example.swopper.ui.chat.message_recycler_view.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.swopper.R
import com.example.swopper.database.CURRENT_UID
import com.example.swopper.ui.chat.message_recycler_view.views.MessageView
import com.example.swopper.utils.asDateTime
import com.example.swopper.utils.setImage
import kotlinx.android.synthetic.main.item_image_message.view.*

class ImageMessageHolder(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private  val inboundMessageContainer: ConstraintLayout = view.inbound_image_message_container
    private  val inboundMessage: ImageView = view.inbound_image_message
    private  val inboundMessageDatetime: TextView = view.inbound_image_message_datetime
    private  val outboundMessageContainer: ConstraintLayout = view.outbound_image_message_container
    private  val outboundMessage: ImageView = view.outbound_image_message
    private  val outboundMessageDatetime: TextView = view.outbound_image_message_datetime


    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            inboundMessageContainer.visibility = View.VISIBLE
            outboundMessageContainer.visibility = View.GONE
            inboundMessage.setImage(view.fileUrl, R.drawable.im_default_photo)
            inboundMessageDatetime.text = view.sended.asDateTime()
        } else {
            inboundMessageContainer.visibility = View.GONE
            outboundMessageContainer.visibility = View.VISIBLE
            outboundMessage.setImage(view.fileUrl, R.drawable.im_default_photo)
            outboundMessageDatetime.text = view.sended.asDateTime()
        }
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDetach() {
    }
}