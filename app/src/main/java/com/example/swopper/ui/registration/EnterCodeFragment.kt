package com.example.swopper.ui.registration

import android.view.View
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.database.*
import com.example.swopper.models.UserModel
import com.example.swopper.utils.*
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_code.*

class EnterCodeFragment(val phone: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.GONE
        APP_ACTIVITY.supportActionBar?.hide()

        enter_code_hint_label.text =
            String.format(getString(R.string.enter_code_hint_label_text), phone)

        enter_code.addTextChangedListener(AppTextWatcher {
            val code = enter_code.text.toString()
            if (code.length == 6) {
                hideKeyboard()
                verifyCode(code)
            }
        })
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                REF_DATABASE_ROOT.child(NODE_USERS)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        if (it.hasChild(AUTH.currentUser?.uid.toString())) {
                            restartActivity()
                        } else {
                            var user = UserModel(
                                id = AUTH.currentUser?.uid.toString(),
                                phone = phone,
                                publicKey = generateKeys(context)
                            )
                            replaceFragment(EnterUsernameFragment(user))
                        }
                    })
            } else showToast(APP_ACTIVITY.getString(R.string.enter_wrong_code_toast))
        }
    }
}