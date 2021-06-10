package com.example.studentcatalogue.screen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.studentcatalogue.MainActivity
import com.example.studentcatalogue.R
import com.example.studentcatalogue.signin.LecturerId
import com.example.studentcatalogue.signin.LoginType
import com.parse.ParseUser

class Splashscreen : AppCompatActivity() {

    private lateinit var topanimation: Animation
    private lateinit var bottomanimation: Animation

    private lateinit var fisrt: View
    private lateinit var second: View
    private lateinit var third: View
    private lateinit var fourth: View
    private lateinit var fifth: View
    private lateinit var sixth: View
    private lateinit var text: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splashscreen)

        supportActionBar?.hide()

        topanimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomanimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)


        fisrt = findViewById(R.id.v_firsline)
        second = findViewById(R.id.v_secondLine)
        third = findViewById(R.id.v_thirdline)
        fourth = findViewById(R.id.v_fourthline)
        fifth = findViewById(R.id.v_fifthline)
        sixth = findViewById(R.id.v_sixthline)

        text = findViewById(R.id.v_relative)

        fisrt.animation = topanimation
        second.animation = topanimation
        third.animation = topanimation
        fourth.animation = topanimation
        fifth.animation = topanimation
        sixth.animation = topanimation

        text.animation = bottomanimation

        val user = ParseUser.getCurrentUser()

        if (user != null) {
            val course = user.getString("courseName").toString()

            if (course == "null") {

                val intent = Intent(this@Splashscreen, MainActivity::class.java)
                startActivity(intent)
            } else {

                val intent = Intent(applicationContext, LecturerId::class.java)
                startActivity(intent)
            }

        } else {


            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@Splashscreen, LoginType::class.java)
                startActivity(intent)
                finish()
            }, 5000)
        }


    }
}