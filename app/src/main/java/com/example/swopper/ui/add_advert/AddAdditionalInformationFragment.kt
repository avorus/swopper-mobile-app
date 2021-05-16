package com.example.swopper.ui.add_advert

import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.models.AdvertModel
import com.example.swopper.utils.*
import kotlinx.android.synthetic.main.fragment_add_additional_information.*

class AddAdditionalInformationFragment(var advert: AdvertModel) : Fragment(R.layout.fragment_add_additional_information) {
    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.GONE
        APP_ACTIVITY.title = "Добавить новое объявление"

        setHasOptionsMenu(true)

        initSpinner()

        add_additional_information_next_button.setOnClickListener {
            val description = add_description.text.toString()
            val category = add_category.selectedItem.toString()
            next(description, category)
        }
    }

    private fun initSpinner() {
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            APP_ACTIVITY,
            R.array.categories,
            R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        add_category.adapter = adapter
    }

    private fun next(description: String, category: String) {
        if(description.isNotBlank()) {
            if (!search(description)) {
                advert.description = description.trim()
                advert.category = category
                replaceFragment(AddImageFragment(advert), R.id.addAdvertFrameLayout)
            } else  showToast(APP_ACTIVITY.getString(R.string.enter_obscene_description_toast))
        } else showToast(APP_ACTIVITY.getString(R.string.enter_empty_description_toast))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.filters)?.isVisible = false
        menu.findItem(R.id.filters)?.isEnabled = false
    }
}