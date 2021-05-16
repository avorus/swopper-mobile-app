package com.example.swopper.utils

import com.example.swopper.database.*

enum class UserStatus(val status: String) {
    ONLINE("в сети"),
    OFFLINE("был(-а) недавно"),
    TYPING("печатает");

    companion object {
        fun update(newStatus: UserStatus) {
            if (AUTH.currentUser != null) {
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_STATUS)
                    .setValue(newStatus.status)
                    .addOnSuccessListener { USER.status = newStatus.status }
                    .addOnFailureListener { showToast(it.message.toString()) }
            }
        }
    }
}