package com.acmpo6ou.myaccounts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.acmpo6ou.myaccounts.ui.UpdatesFragment

class UpdatesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.updates_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, UpdatesFragment.newInstance())
                .commitNow()
        }
    }
}