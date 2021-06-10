package com.example.studentcatalogue.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.studentcatalogue.R

class LoginType : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_type)

    }

    fun proceedStudent(view: View) {
        val intent= Intent(applicationContext,StudentId::class.java)
        startActivity(intent)
    }

    fun proceedLecturer(view: View) {
        val intent= Intent(applicationContext,LecturerId  ::class.java)
        startActivity(intent)
    }
}