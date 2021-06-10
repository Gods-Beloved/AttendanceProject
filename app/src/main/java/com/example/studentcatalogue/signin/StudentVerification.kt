package com.example.studentcatalogue.signin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentcatalogue.MainActivity
import com.example.studentcatalogue.R
import com.parse.ParseUser
import com.r0adkll.slidr.Slidr

class StudentVerification : AppCompatActivity() {

    private lateinit var userName:EditText
    private lateinit var passWord:EditText
    private lateinit var loginBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_verify)
        Slidr.attach(this)
        loginBtn=findViewById(R.id.v_login_button)

        loginBtn.setOnClickListener {
            userName=findViewById(R.id.v_username)

            passWord=findViewById(R.id.v_password)

            if (userName.text.isEmpty() || passWord.text.isEmpty())
            {
                Toast.makeText(this,"Entered credentials do not match selected student",Toast.LENGTH_LONG).show()
            }else{
                val dialog = ProgressDialog.show(
                this, "",
                "Please wait...", true
            )
                dialog.show()
                val user2= userName.text.trim().toString()
                val pword=passWord.text.trim().toString()

                ParseUser.logInInBackground(user2,pword
                ) { user, e ->

                    if (user != null) {

                        // Hooray! The user is logged in.
                        dialog.dismiss()


                        val extras = intent.extras

                        val value: String
                        if (extras != null) {

                            value = extras.getString("username").toString()


                            if (userName.text.toString() == value) {

                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }


                    } else {
                        // Signup failed. Look at the ParseException to see what happened.
                        ParseUser.logOut()
                        dialog.dismiss()
                        if (e != null) {
                            dialog.dismiss()
                            ParseUser.logOut()

                            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                        }
                    }

                }
            }

            closeKeyboard()
        }


    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun constraintClick4(view: View) {
        closeKeyboard()
    }


}

