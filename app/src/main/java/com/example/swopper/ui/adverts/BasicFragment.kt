package com.example.swopper.ui.adverts

import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.models.UserModel
import com.example.swopper.utils.replaceFragment
import com.example.swopper.utils.showToast

class BasicFragment : Fragment(R.layout.fragment_adverts_basic) {

    override fun onStart() {
        super.onStart()
        replaceFragment(AdvertsFragment(), R.id.advertsFrameLayout, false)
    }
}