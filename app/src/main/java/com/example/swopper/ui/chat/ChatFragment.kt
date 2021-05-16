package com.example.swopper.ui.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.swopper.R
import com.example.swopper.database.*
import com.example.swopper.models.AdvertModel
import com.example.swopper.models.UserModel
import com.example.swopper.models.MessageModel
import com.example.swopper.ui.chat.message_recycler_view.ChatAdapter
import com.example.swopper.ui.chat.message_recycler_view.views.ViewFactory
import com.example.swopper.utils.*
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.chat_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment(var contactId: String, var advertId: String) : Fragment(R.layout.fragment_chat) {
    private lateinit var mChatToolbar: View
    private lateinit var mUserToolbarListener: AppValueEventListener
    private lateinit var mAdvertToolbarListener: AppValueEventListener

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: ChatAdapter

    private lateinit var mMessagesListener: AppChildEventListener
    private var mCountMessages = 10
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.GONE

        setHasOptionsMenu(true)

        init()

        initToolbar()

        initRecyclerView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        mSwipeRefreshLayout = chat_swipe_refresh
        mLayoutManager = LinearLayoutManager(this.context)
        chat_enter_message.addTextChangedListener(AppTextWatcher {
            val input = chat_enter_message.text.toString()
            if (input.isEmpty()) {
                chat_send_message.visibility = View.GONE
                chat_attach_image.visibility = View.VISIBLE
            } else {
                chat_send_message.visibility = View.VISIBLE
                chat_attach_image.visibility = View.GONE
            }
        })

        chat_send_message.setOnClickListener {
            mSmoothScrollToPosition = true
            val message = chat_enter_message.text.toString()
            if (message.isNotBlank()) {
                sendTextMessage(
                    message,
                    contactId
                ) {
                    saveToChatList(contactId, advertId)
                    chat_enter_message.setText("")
                }
            }
        }

        chat_attach_image.setOnClickListener { attachImage() }
    }

    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            val uri = CropImage.getActivityResult(data).uri
            val messageKey = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
                .child(contactId).push().key.toString()
            uploadImageToStorage(uri,messageKey,contactId)
            mSmoothScrollToPosition = true
        }
    }

    private fun initToolbar() {
        mChatToolbar = APP_ACTIVITY.mActionBarToolbar.chat_toolbar
        mChatToolbar.visibility = View.VISIBLE
        mUserToolbarListener = AppValueEventListener {
            val contact = it.getValue(UserModel::class.java) ?: UserModel()
            mChatToolbar.chat_toolbar_user_image.setImage(contact.photoUrl, R.drawable.im_default_user)
            mChatToolbar.chat_toolbar_user_username.text = contact.username
            mChatToolbar.chat_toolbar_user_status.text = contact.status
        }

        REF_DATABASE_ROOT.child(NODE_USERS).child(contactId)
            .addValueEventListener(mUserToolbarListener)

        mAdvertToolbarListener = AppValueEventListener {
            val advert = it.getValue(AdvertModel::class.java) ?: AdvertModel()
            chat_toolbar_advert_name?.text = advert.name
            chat_toolbar_advert_image?.
            setImage(advert.photoUrl, R.drawable.im_default_advert)
        }

        REF_DATABASE_ROOT.child(NODE_ADVERTS).child(advertId)
            .addValueEventListener(mAdvertToolbarListener)
    }

    private fun initRecyclerView() {
        mRecyclerView = messages_recycler_view
        mAdapter = ChatAdapter()
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager

        mMessagesListener = AppChildEventListener {
            val message = it.getValue(MessageModel::class.java) ?: MessageModel()

            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(ViewFactory.getView(message)) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(ViewFactory.getView(message)) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }

        }
        REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contactId)
            .limitToLast(mCountMessages).addChildEventListener(mMessagesListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10
        REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contactId)
            .removeEventListener(mMessagesListener)
        REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contactId)
            .limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
    }

    override fun onStop() {
        super.onStop()
        APP_ACTIVITY.mActionBarToolbar.chat_toolbar.visibility = View.GONE
        REF_DATABASE_ROOT.child(NODE_USERS).child(contactId).removeEventListener(mUserToolbarListener)
        REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contactId)
            .removeEventListener(mMessagesListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAdapter.onDestroy()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.filters)?.isVisible = false
        menu.findItem(R.id.filters)?.isEnabled = false
    }
}