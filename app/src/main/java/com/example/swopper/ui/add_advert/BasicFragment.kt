package com.example.swopper.ui.add_advert

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.utils.replaceFragment

class BasicFragment : Fragment(R.layout.fragment_add_advert_basic) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        replaceFragment(AddMainInformationFragment(), R.id.addAdvertFrameLayout, false)
    }
}