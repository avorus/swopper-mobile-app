package com.example.swopper

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.swopper.databinding.ActivityMainBinding
import com.example.swopper.database.*
import com.example.swopper.utils.*
import com.example.swopper.ui.registration.EnterPhoneFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mBottomNavigationView: BottomNavigationView
    lateinit var mActionBarToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        APP_ACTIVITY = this

        initFirebase()

        initNavigationBar()

        setStartFragment()
    }

    override fun onStart() {
        super.onStart()

        initUser()

        if (USER.phone.isNotEmpty()) {
            UserStatus.update(UserStatus.ONLINE)
        }
    }

    private fun initNavigationBar() {
        mBottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        mBottomNavigationView.setupWithNavController(navController)

        mActionBarToolbar = mBinding.mainToolbar
        setSupportActionBar(mActionBarToolbar)
    }

    private fun setStartFragment() {
        if (AUTH.currentUser == null) {
            replaceFragment(EnterPhoneFragment(), false)
        }
    }

    override fun onStop() {
        super.onStop()

        UserStatus.update(UserStatus.OFFLINE)
    }
}