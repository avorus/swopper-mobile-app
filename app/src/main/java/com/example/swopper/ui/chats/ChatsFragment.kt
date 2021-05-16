package com.example.swopper.ui.chats

import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.swopper.R
import com.example.swopper.database.*
import com.example.swopper.models.AdvertModel
import com.example.swopper.models.CommonModel
import com.example.swopper.models.MessageModel
import com.example.swopper.models.UserModel
import com.example.swopper.utils.*
import kotlinx.android.synthetic.main.fragment_chats.*

class ChatsFragment : Fragment(R.layout.fragment_chats) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ChatsAdapter
    private var mChats = listOf<CommonModel>()

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.VISIBLE
        APP_ACTIVITY.supportActionBar?.show()
        APP_ACTIVITY.title = "Чаты"

        setHasOptionsMenu(true)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = chats_recycler_view
        mAdapter = ChatsAdapter()

        REF_DATABASE_ROOT.child(NODE_CHATS).child(CURRENT_UID).addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            mChats = dataSnapshot.children.map { it.getValue(CommonModel::class.java)?: CommonModel() }
            mChats.forEach { chat ->

                REF_DATABASE_ROOT.child(NODE_USERS).child(chat.user)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
                        val userModel = dataSnapshot1.getValue(UserModel::class.java)?: UserModel()
                        chat.username = userModel.username

                        REF_DATABASE_ROOT.child(NODE_ADVERTS).child(chat.advert)
                            .addListenerForSingleValueEvent(AppValueEventListener {dataSnapshot2 ->
                                val advertModel = dataSnapshot2.getValue(AdvertModel::class.java)?: AdvertModel()
                                chat.name = advertModel.name
                                chat.photoUrl = advertModel.photoUrl

                                REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(chat.user).limitToLast(1)
                                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot3 ->
                                        val messageModel = dataSnapshot3.children.map { it.getValue(MessageModel::class.java)?: MessageModel() }
                                        chat.text = messageModel[0].text
                                        chat.sended = messageModel[0].sended

                                        mAdapter.update(chat)
                                    })
                            })
                    })
            }
        })

        mRecyclerView.adapter = mAdapter
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.filters)?.isVisible = false
        menu.findItem(R.id.filters)?.isEnabled = false
    }
}