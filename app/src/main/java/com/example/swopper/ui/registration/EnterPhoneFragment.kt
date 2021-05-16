package com.example.swopper.ui.registration

import android.view.View
import androidx.fragment.app.Fragment
import com.example.swopper.R
import com.example.swopper.database.AUTH
import com.example.swopper.utils.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_phone.*
import java.util.concurrent.TimeUnit

class EnterPhoneFragment : Fragment(R.layout.fragment_enter_phone) {
    private lateinit var mPhone: String
    private lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.GONE
        APP_ACTIVITY.supportActionBar?.hide()

        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        UserStatus.update(UserStatus.ONLINE)
                        restartActivity()
                    } else showToast(task.exception?.message.toString())
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(
                    EnterCodeFragment(
                        mPhone,
                        id
                    )
                )
            }
        }

        enter_phone.addTextChangedListener(AppTextWatcher {
            if (enter_phone.rawText.length == 10) {
                hideKeyboard()
            }
        })

        enter_phone_next_button.setOnClickListener { sendCode() }
    }

    private fun sendCode() {
        if (enter_phone.rawText.toString().length != 10) {
            showToast(APP_ACTIVITY.getString(R.string.enter_empty_phone_toast))
        } else {
            authenticateUser()
        }
    }

    private fun authenticateUser() {
        mPhone = enter_phone.text.toString()

        val options = PhoneAuthOptions.newBuilder(AUTH)
            .setPhoneNumber(mPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(APP_ACTIVITY)
            .setCallbacks(mCallback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}