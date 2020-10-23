package com.acmpo6ou.myaccounts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // here we show splash screen for 1s
        Handler().postDelayed(
                {
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }, 650L)
    }
}