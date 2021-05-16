package com.example.swopper.utils

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.swopper.MainActivity
import com.example.swopper.R
import com.example.swopper.database.NODE_ADVERTS
import com.example.swopper.database.REF_DATABASE_ROOT
import com.example.swopper.models.AdvertModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

lateinit var APP_ACTIVITY: MainActivity

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun replaceFragment(fragment: Fragment, addStack: Boolean = true) {
    if (addStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.container,
                fragment
            ).commitAllowingStateLoss()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                fragment
            ).commitAllowingStateLoss()
    }
}

fun replaceFragment(fragment: Fragment, id: Int, addStack: Boolean = true) {
    if (addStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                id,
                fragment
            ).commitAllowingStateLoss()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(
                id,
                fragment
            ).commitAllowingStateLoss()
    }
}

fun hideKeyboard() {
    val imm: InputMethodManager = APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

fun String.asDate(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return timeFormat.format(time)
}

fun String.asDateTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("d MMMM HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

fun ImageView.setImage(url: String, default: Int) {
    Picasso.get()
        .load(url)
        .fit()
        .placeholder(default)
        .into(this)
}