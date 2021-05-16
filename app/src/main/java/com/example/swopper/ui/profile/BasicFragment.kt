package com.example.swopper.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.utils.replaceFragment

class BasicFragment : Fragment(R.layout.fragment_profile_basic) {

    override fun onStart() {
        super.onStart()
        replaceFragment(ProfileFragment(), R.id.profileFrameLayout, false)
    }
}