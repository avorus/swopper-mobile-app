package com.example.swopper.ui.adverts

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.database.CURRENT_UID
import com.example.swopper.database.NODE_USERS
import com.example.swopper.database.REF_DATABASE_ROOT
import com.example.swopper.models.*
import com.example.swopper.ui.chat.ChatFragment
import com.example.swopper.utils.*
import kotlinx.android.synthetic.main.fragment_advert.*

class AdvertFragment(var advert: AdvertModel) : Fragment(R.layout.fragment_advert) {

    private lateinit var owner : UserModel

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.GONE

        advert_call_owner.setOnClickListener { call() }

        advert_chat_owner.setOnClickListener { replaceFragment(ChatFragment(owner.id, advert.id), R.id.advertsFrameLayout) }
    }

    override fun onResume() {
        super.onResume()

        initAdvert {
            initOwner {
                APP_ACTIVITY.title = "Объявление от ${owner.username}"
            }
        }
    }

    private inline fun initAdvert(crossinline function: () -> Unit) {
        advert_image.setImage(advert.photoUrl, R.drawable.im_default_advert)
        advert_name.text = advert.name
        advert_type.text = advert.type
        advert_location.text = advert.location
        advert_category.text = advert.category
        advert_posted.text = advert.posted.toString().asDateTime()
        advert_description.text = advert.description

        function()
    }

    private inline fun initOwner(crossinline function: () -> Unit) {
        if (advert.owner != CURRENT_UID) {
            REF_DATABASE_ROOT.child(NODE_USERS).child(advert.owner)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    owner = it.getValue(UserModel::class.java)?: UserModel()
                    if (owner.phone.isNotEmpty()) {
                        advert_owner_image.setImage(owner.photoUrl, R.drawable.im_default_user)
                        advert_owner_username.text = owner.username
                        advert_owner_status.text = owner.status
                        advert_call_owner.visibility =
                            if (owner.hidden == "true") View.GONE else View.VISIBLE
                        function()
                    } else {
                        showToast("Нельзя получить данные о пользователе")
                    }
                })
        }
        else {
            advert_owner_container.visibility = View.GONE
            advert_call_owner.visibility = View.GONE
            advert_chat_owner.visibility = View.GONE
        }
    }

    private fun call() {
        if (owner.phone.isNotEmpty()) {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${owner.phone}")))
        }
    }
}