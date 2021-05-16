package com.example.swopper.ui.adverts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swopper.R
import com.example.swopper.models.AdvertModel
import com.example.swopper.utils.*
import kotlinx.android.synthetic.main.item_advert.view.*

class AdvertsAdapter: RecyclerView.Adapter<AdvertsAdapter.AdvertsHolder>() {

    private var adverts = emptyList<AdvertModel>()

    class AdvertsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemAdvertImage: ImageView = view.adverts_image
        val itemAdvertName: TextView = view.adverts_name
        val itemAdvertType: TextView = view.adverts_type
        val itemAdvertLocation: TextView = view.adverts_location
        val itemAdvertPosted: TextView = view.adverts_posted
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_advert, parent, false)
        val holder = AdvertsHolder(view)
        holder.itemView.setOnClickListener {
                replaceFragment(AdvertFragment(adverts[holder.adapterPosition]), R.id.advertsFrameLayout)
        }
        return holder
    }

    override fun getItemCount(): Int = adverts.size

    override fun onBindViewHolder(holder: AdvertsHolder, position: Int) {
        holder.itemAdvertImage.setImage(adverts[position].photoUrl, R.drawable.im_default_advert)
        holder.itemAdvertName.text = adverts[position].name
        holder.itemAdvertType.text = adverts[position].type
        holder.itemAdvertLocation.text = adverts[position].location
        holder.itemAdvertPosted.text = adverts[position].posted.toString().asDateTime()
    }

    fun setList(list: List<AdvertModel>) {
        adverts = list
        notifyDataSetChanged()
    }
}