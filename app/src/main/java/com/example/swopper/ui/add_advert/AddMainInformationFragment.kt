package com.example.swopper.ui.add_advert

import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.database.USER
import com.example.swopper.models.AdvertModel
import com.example.swopper.utils.*
import kotlinx.android.synthetic.main.fragment_add_main_information.*

class AddMainInformationFragment : Fragment(R.layout.fragment_add_main_information) {
    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.VISIBLE
        APP_ACTIVITY.supportActionBar?.show()
        APP_ACTIVITY.title = "Добавить новое объявление"

        setHasOptionsMenu(true)

        initSpinner()

        add_main_information_next_button.setOnClickListener {
            val type = when(add_type.checkedRadioButtonId) {
                R.id.type_give -> "Отдам даром"
                R.id.type_accept -> "Приму в дар"
                R.id.type_change -> "Обменяю"
                else -> ""
            }
            val name = add_name.text.toString()
            val location = add_location.selectedItem.toString()
            next(type, name, location)
        }
    }

    override fun onResume() {
        super.onResume()

        val locations: MutableList<String> = mutableListOf()
        locations.addAll(APP_ACTIVITY.resources.getStringArray(R.array.locations))

        add_location.setSelection(locations.indexOf(USER.location))

        add_type.check(R.id.type_give)
    }

    private fun initSpinner() {
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            APP_ACTIVITY,
            R.array.locations,
            R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        add_location.adapter = adapter
    }

    private fun next(type: String, name: String, location: String) {
        if(name.isNotBlank()) {
            if (!search(name)) {
                var advert = AdvertModel(
                    name = name.trim(),
                    type = type,
                    location = location
                )
                replaceFragment(AddAdditionalInformationFragment(advert), R.id.addAdvertFrameLayout)
            } else  showToast(APP_ACTIVITY.getString(R.string.enter_obscene_name_toast))
        } else showToast(APP_ACTIVITY.getString(R.string.enter_empty_name_toast))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.filters)?.isVisible = false
        menu.findItem(R.id.filters)?.isEnabled = false
    }
}