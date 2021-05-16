package com.example.swopper.ui.registration

import android.view.View
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.models.UserModel
import com.example.swopper.utils.*
import kotlinx.android.synthetic.main.fragment_enter_username.*

class EnterUsernameFragment(var user: UserModel) : Fragment(R.layout.fragment_enter_username) {
    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.GONE
        APP_ACTIVITY.supportActionBar?.hide()

        enter_username_next_button.setOnClickListener { validateUsername(enter_username.text.toString()) }
    }

    private fun validateUsername(username: String) {
        if(username.isNotBlank()) {
            if (!search(username)) {
                user.username = username.trim()
                replaceFragment(EnterLocationFragment(user))
            } else  showToast(APP_ACTIVITY.getString(R.string.enter_obscene_username_toast))
        } else showToast(APP_ACTIVITY.getString(R.string.enter_empty_username_toast))
    }
}