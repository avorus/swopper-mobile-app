package com.example.swopper.database

import android.net.Uri
import com.example.swopper.models.*
import com.example.swopper.utils.AppValueEventListener
import com.example.swopper.utils.encryptMessage
import com.example.swopper.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    AUTH.languageCode = "ru"

    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference

    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

fun initUser() {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
        })
}

fun setChangesToDatabase(username: String, location: String, hidden: String) {
    var data: MutableMap<String, Any> = mutableMapOf()
    data[CHILD_USERNAME] = username
    data[CHILD_HIDDEN] = hidden
    data[CHILD_LOCATION] = location

    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).updateChildren(data)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                USER.username = username
                USER.hidden = hidden
                USER.location = location
                showToast("Изменения сохранены")
            } else showToast("Изменения не сохранены, попробуйте позже")
        }
}

inline fun setImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun setUrlToDatabase(ref: DatabaseReference, url: String, crossinline function: () -> Unit) {
    ref.setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendTextMessage(message: String, recipient: String, function: () -> Unit) {
    val refChatSender = "$NODE_MESSAGES/$CURRENT_UID/$recipient"
    val refChatRecipient = "$NODE_MESSAGES/$recipient/$CURRENT_UID"
    val messageKey = REF_DATABASE_ROOT.child(refChatSender).push().key

    lateinit var recipientKey: String
    REF_DATABASE_ROOT.child(NODE_USERS).child(recipient)
        .addListenerForSingleValueEvent(AppValueEventListener {
            recipientKey = (it.getValue(UserModel::class.java) ?: UserModel()).publicKey
        })

    val mapSenderMessage = hashMapOf<String, Any>()
    mapSenderMessage[CHILD_ID] = messageKey.toString()
    mapSenderMessage[CHILD_TYPE] = TYPE_MESSAGE_TEXT
    mapSenderMessage[CHILD_TEXT] = encryptMessage(message, USER.publicKey)
    mapSenderMessage[CHILD_FROM] = CURRENT_UID
    mapSenderMessage[CHILD_SENDED] = ServerValue.TIMESTAMP

    val mapRecipientMessage = hashMapOf<String, Any>()
    mapRecipientMessage[CHILD_ID] = messageKey.toString()
    mapRecipientMessage[CHILD_TYPE] = TYPE_MESSAGE_TEXT
    mapRecipientMessage[CHILD_TEXT] = encryptMessage(message, recipientKey)
    mapRecipientMessage[CHILD_FROM] = CURRENT_UID
    mapRecipientMessage[CHILD_SENDED] = ServerValue.TIMESTAMP

    val mapChat = hashMapOf<String, Any>()
    mapChat["$refChatSender/$messageKey"] = mapSenderMessage
    mapChat["$refChatRecipient/$messageKey"] = mapRecipientMessage

    REF_DATABASE_ROOT
        .updateChildren(mapChat)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun uploadImageToStorage(uri: Uri, messageKey: String, recipient: String) {
    val path = REF_STORAGE_ROOT.child(FOLDER_MESSAGE_IMAGE).child(messageKey)
    setImageToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendImageMessage(it, messageKey, recipient)
        }
    }
}

fun sendImageMessage(url: String, messageKey: String, recipient: String) {
    val refChatSender = "$NODE_MESSAGES/$CURRENT_UID/$recipient"
    val refChatRecipient = "$NODE_MESSAGES/$recipient/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TYPE] = TYPE_MESSAGE_IMAGE
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_SENDED] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = url

    val mapChat = hashMapOf<String, Any>()
    mapChat["$refChatSender/$messageKey"] = mapMessage
    mapChat["$refChatRecipient/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapChat)
        .addOnFailureListener { showToast(it.message.toString()) }
}

private fun getId(first: String, second: String): String {
    return "${first.length}x${second.length}x${first}${second}"
}

fun saveToChatList(userId: String, advertId: String) {
    val keyChatSender = getId(userId, advertId)
    val refChatSender = "$NODE_CHATS/$CURRENT_UID/$keyChatSender"
    val keyChatRecipient = getId(CURRENT_UID, advertId)
    val refChatRecipient = "$NODE_CHATS/$userId/$keyChatRecipient"

    val mapChatSender = hashMapOf<String, Any>()
    val mapChatRecipient = hashMapOf<String, Any>()

    mapChatSender[CHILD_ID] = keyChatSender
    mapChatSender[CHILD_USER] = userId
    mapChatSender[CHILD_ADVERT] = advertId

    mapChatRecipient[CHILD_ID] = keyChatRecipient
    mapChatRecipient[CHILD_USER] = CURRENT_UID
    mapChatRecipient[CHILD_ADVERT] = advertId

    val commonMap = hashMapOf<String, Any>()
    commonMap[refChatSender] = mapChatSender
    commonMap[refChatRecipient] = mapChatRecipient

    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}
