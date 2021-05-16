package com.example.swopper.ui.settings

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.database.*
import com.example.swopper.utils.*
import io.ghyeok.stickyswitch.widget.StickySwitch
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.VISIBLE
        APP_ACTIVITY.supportActionBar?.show()
        APP_ACTIVITY.title = "Настройки"

        setHasOptionsMenu(true)

        initSpinner()

        settings_exit.setOnClickListener { exit() }
    }

    private fun initSpinner() {
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            APP_ACTIVITY,
            R.array.locations,
            R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        settings_change_location.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        settings_change_username.setText(USER.username)

        val locations: MutableList<String> = mutableListOf()
        locations.addAll(APP_ACTIVITY.resources.getStringArray(R.array.locations))

        settings_change_location.setSelection(locations.indexOf(USER.location))

        if (USER.hidden == "true") {
            settings_change_hidden.setDirection(StickySwitch.Direction.RIGHT, false)
        } else settings_change_hidden.setDirection(StickySwitch.Direction.LEFT, false)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.confirm_changes_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.filters)?.isVisible = false
        menu.findItem(R.id.filters)?.isEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.changes_confirm -> {
                change(
                    settings_change_username.text.toString(),
                    settings_change_location.selectedItem.toString(),
                    settings_change_hidden.getDirection() == StickySwitch.Direction.RIGHT
                )
            }
        }
        return true
    }

    private fun change(username: String, location: String, hidden: Boolean) {
        if (username.isNotBlank()) {
            if (!search(username)) {
                setChangesToDatabase(username.trim(), location, if (hidden) "true" else "false")
            } else showToast(getString(R.string.enter_obscene_username_toast))
        } else showToast(getString(R.string.enter_empty_username_toast))
    }

    private fun exit() {
        UserStatus.update(UserStatus.OFFLINE)
        AUTH.signOut()
        restartActivity()
    }
}