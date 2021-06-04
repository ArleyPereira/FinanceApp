package com.br.apsmobile.activity.app

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.br.apsmobile.R
import com.br.apsmobile.activity.auth.LoginActivity
import com.br.apsmobile.helper.GetFirebase

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(mainLooper).postDelayed(this::checkAutentication, 3000)

    }

    private fun checkAutentication(){
        if(GetFirebase.getAutenticado()){
            startActivity(Intent(this, MainActivity::class.java))
        }else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

}