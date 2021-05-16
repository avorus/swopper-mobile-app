package com.example.swopper.ui.chats

import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.ui.adverts.AdvertsFragment
import com.example.swopper.utils.replaceFragment

class BasicFragment : Fragment(R.layout.fragment_chats_basic) {
    override fun onStart() {
        super.onStart()
        replaceFragment(ChatsFragment(), R.id.chatsFrameLayout, false)
    }
}