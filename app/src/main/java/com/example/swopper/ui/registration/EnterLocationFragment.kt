package com.example.swopper.ui.registration

import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.database.*
import com.example.swopper.models.UserModel
import com.example.swopper.utils.*
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.fragment_enter_location.*

class EnterLocationFragment(var user: UserModel) : Fragment(R.layout.fragment_enter_location) {

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.GONE
        APP_ACTIVITY.supportActionBar?.hide()

        initSpinner()

        enter_location_next_button.setOnClickListener { completeRegistration(enter_location.selectedItem.toString()) }
    }

    private fun initSpinner() {
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            APP_ACTIVITY,
            R.array.locations,
            R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        enter_location.adapter = adapter
    }

    private fun completeRegistration(location: String) {
        var data: MutableMap<String, Any> = mutableMapOf()
        data[CHILD_ID] = user.id
        data[CHILD_USERNAME] = user.username
        data[CHILD_PHONE] = user.phone
        data[CHILD_HIDDEN] = "false"
        data[CHILD_STATUS] = "в сети"
        data[CHILD_REGISTRATED] = ServerValue.TIMESTAMP
        data[CHILD_LOCATION] = location
        data[CHILD_PUBLIC_KEY] = user.publicKey

        REF_DATABASE_ROOT.child(NODE_USERS).child(user.id).updateChildren(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    restartActivity()
                } else showToast(it.exception?.message.toString())
            }
    }
}