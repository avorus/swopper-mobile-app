package com.example.swopper.database

import com.example.swopper.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth

lateinit var CURRENT_UID: String
lateinit var USER: UserModel
lateinit var KEY: String

lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference

const val FOLDER_PROFILE_IMAGE = "profile_image"
const val FOLDER_ADVERT_IMAGE = "advert_image"
const val FOLDER_MESSAGE_IMAGE = "message_image"

const val TYPE_MESSAGE_TEXT = "text"
const val TYPE_MESSAGE_IMAGE = "image"

const val NODE_USERS = "users"
const val NODE_ADVERTS = "adverts"
const val NODE_MESSAGES = "messages"
const val NODE_CHATS = "chats"

const val CHILD_ID = "id"
const val CHILD_USERNAME = "username"
const val CHILD_PHONE = "phone"
const val CHILD_HIDDEN = "hidden"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATUS = "status"
const val CHILD_REGISTRATED = "registrated"
const val CHILD_LOCATION = "location"
const val CHILD_PUBLIC_KEY = "publicKey"

// const val CHILD_ID = "id"
const val CHILD_NAME = "name"
const val CHILD_DESCRIPTION = "description"
const val CHILD_TYPE = "type"
const val CHILD_CATEGORY = "category"

// const val CHILD_PHOTO_URL = "photoUrl"
// const val CHILD_LOCATION = "location"
const val CHILD_POSTED = "posted"
const val CHILD_OWNER = "owner"

// const val CHILD_STATUS = "status"
const val CHILD_ARCHIVED = "archived"

// const val CHILD_ID = "id"
const val CHILD_TEXT = "text"

//const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_SENDED = "sended"
const val CHILD_FILE_URL = "fileUrl"

// const val CHILD_ID = "id"
const val CHILD_USER = "user"
const val CHILD_ADVERT = "advert"