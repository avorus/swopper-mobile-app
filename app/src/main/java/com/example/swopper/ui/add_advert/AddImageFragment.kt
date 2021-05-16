package com.example.swopper.ui.add_advert

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.database.*
import com.example.swopper.models.AdvertModel
import com.example.swopper.utils.*
import com.google.firebase.database.ServerValue
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_add_image.*
import java.util.*

class AddImageFragment(var advert: AdvertModel) : Fragment(R.layout.fragment_add_image) {

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.GONE
        APP_ACTIVITY.title = "Добавить новое объявление"

        setHasOptionsMenu(true)

        add_image.setOnClickListener { addPhoto() }

        add_image_next_button.setOnClickListener { complete() }
    }

    private fun addPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            val uri = CropImage.getActivityResult(data).uri
            val key = REF_DATABASE_ROOT.child(NODE_ADVERTS).push().key
            val path = REF_STORAGE_ROOT.child(FOLDER_ADVERT_IMAGE).child(key!!)
            setImageToStorage(uri, path) {
                getUrlFromStorage(path) {
                        add_image.setImage(it, R.drawable.im_default_advert)
                        advert.id = key
                        advert.photoUrl = it
                        add_image_next_button.isEnabled = true
                }
            }
        }
    }

    private fun complete() {
        var data: MutableMap<String, Any> = mutableMapOf()
        data[CHILD_ID] = advert.id
        data[CHILD_NAME] = advert.name
        data[CHILD_DESCRIPTION] = advert.description
        data[CHILD_TYPE] = advert.type
        data[CHILD_CATEGORY] = advert.category
        data[CHILD_PHOTO_URL] = advert.photoUrl
        data[CHILD_LOCATION] = advert.location
        data[CHILD_POSTED] = ServerValue.TIMESTAMP
        data[CHILD_OWNER] = CURRENT_UID

        REF_DATABASE_ROOT.child(NODE_ADVERTS).child(advert.id).updateChildren(data)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    showToast("Объявление добавлено")
                    restartActivity()
                } else showToast(it.exception?.message.toString())
            }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.filters)?.isVisible = false
        menu.findItem(R.id.filters)?.isEnabled = false
    }
}