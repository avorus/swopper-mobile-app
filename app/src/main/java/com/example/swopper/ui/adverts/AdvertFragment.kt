package com.example.swopper.ui.adverts

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.database.*
import com.example.swopper.models.*
import com.example.swopper.ui.chat.ChatFragment
import com.example.swopper.ui.profile.ProfileFragment
import com.example.swopper.utils.*
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.fragment_advert.*

class AdvertFragment(var advert: AdvertModel) : Fragment(R.layout.fragment_advert) {

    private lateinit var owner: UserModel

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.GONE

        advert_call_owner.setOnClickListener { call() }

        advert_chat_owner.setOnClickListener {
            replaceFragment(
                ChatFragment(owner.id, advert.id),
                R.id.advertsFrameLayout
            )
        }

        advert_archive.setOnClickListener { archive() }

        advert_dearchive.setOnClickListener { dearchive() }

        advert_delete.setOnClickListener { delete() }
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
                    owner = it.getValue(UserModel::class.java) ?: UserModel()
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
        defineVisibility()
    }

    private fun defineVisibility() {
        if (advert.owner != CURRENT_UID) {
            advert_call_owner.visibility = View.VISIBLE
            advert_chat_owner.visibility = View.VISIBLE
            advert_archive.visibility = View.GONE
            advert_dearchive.visibility = View.GONE
            advert_delete.visibility = View.GONE
        } else {
            advert_owner_container.visibility = View.GONE
            advert_call_owner.visibility = View.GONE
            advert_chat_owner.visibility = View.GONE
            if (advert.status == AdvertStatus.ACTIVE.status) {
                advert_archive.visibility = View.VISIBLE
                advert_dearchive.visibility = View.GONE
            } else {
                advert_archive.visibility = View.GONE
                advert_dearchive.visibility = View.VISIBLE
            }
            advert_delete.visibility = View.VISIBLE
        }
    }

    private fun call() {
        if (owner.phone.isNotEmpty()) {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${owner.phone}")))
        }
    }

    private fun archive() {
        val builder = AlertDialog.Builder(APP_ACTIVITY, R.style.confirmDialog)
        val alert = builder.setTitle(R.string.confirm_archive_title)
            .setMessage(R.string.confirm_archive_message)
            .setPositiveButton("ОК") { dialog, _ ->
                run {
                    val timestamp = ServerValue.TIMESTAMP
                    val data: MutableMap<String, Any> = mutableMapOf()
                    data[CHILD_STATUS] = AdvertStatus.ARCHIVED.status
                    data[CHILD_ARCHIVED] = timestamp

                    REF_DATABASE_ROOT.child(NODE_ADVERTS).child(advert.id).updateChildren(data)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                advert.status = AdvertStatus.ARCHIVED.status
                                advert.archived = timestamp
                                advert_archive.visibility = View.GONE
                                advert_dearchive.visibility = View.VISIBLE
                                showToast("Объявление архивировано")
                            } else showToast(it.exception?.message.toString())
                        }
                    dialog.cancel()
                }
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                run {
                    dialog.cancel()
                }
            }.create()
        alert.show()

        alert.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.colorMainText))
        alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.colorMainText))
    }

    private fun dearchive() {
        val data: MutableMap<String, Any> = mutableMapOf()
        data[CHILD_STATUS] = AdvertStatus.ACTIVE.status
        data[CHILD_ARCHIVED] = ""

        REF_DATABASE_ROOT.child(NODE_ADVERTS).child(advert.id).updateChildren(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    advert.status = AdvertStatus.ACTIVE.status
                    advert.archived = ""
                    advert_archive.visibility = View.VISIBLE
                    advert_dearchive.visibility = View.GONE
                    showToast("Объявление разархивировано")
                } else showToast(it.exception?.message.toString())
            }
    }

    private fun delete() {
        val builder = AlertDialog.Builder(APP_ACTIVITY, R.style.confirmDialog)
        val alert = builder.setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setPositiveButton("ОК") { dialog, _ ->
                run {
                    REF_DATABASE_ROOT.child(NODE_ADVERTS).child(advert.id).child(CHILD_STATUS)
                        .setValue(AdvertStatus.DELETED.status)
                        .addOnSuccessListener {
                            advert.status = AdvertStatus.DELETED.status
                            replaceFragment(ProfileFragment(), R.id.profileFrameLayout, false)
                            showToast("Объявление удалено")
                        }
                        .addOnFailureListener { showToast(it.message.toString()) }
                    dialog.cancel()
                }
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                run {
                    dialog.cancel()
                }
            }.create()
        alert.show()

        alert.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.colorMainText))
        alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.colorMainText))
    }

}
