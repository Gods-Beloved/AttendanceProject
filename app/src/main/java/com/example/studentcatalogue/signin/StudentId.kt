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
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.studentcatalogue.R
import com.google.android.material.snackbar.Snackbar
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.r0adkll.slidr.Slidr

class StudentId : AppCompatActivity() {

    private lateinit var idSignUp: EditText

    private lateinit var idSignUpButton: Button

    private lateinit var constraintLayout: ConstraintLayout


    // private lateinit var shimmer:ShimmerFrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_idcheck)

        idSignUp = findViewById(R.id.v_username)

        idSignUpButton = findViewById(R.id.v_next_btn)

        idSignUpButton.setOnClickListener {

            constraintLayout=findViewById(R.id.v_constraint_layout)



            val dialog = ProgressDialog.show(
                    this, "",
                    "Please wait...", true
            )

            dialog.dismiss()



            idSignUp = findViewById(R.id.v_username)

            val studentId = idSignUp.text.toString()


            if (idSignUp.text.isEmpty()) {
                showSnackbar(constraintLayout,"Please enter your student ID number")
            } else {

                dialog.show()

                val query = ParseQuery.getQuery<ParseObject>("Student")
                query.whereEqualTo("studentID", studentId)
                query.getFirstInBackground { `object`, e ->
                    if (e == null) {
                        val userName = `object`.getString("secretUsername").toString()
                        val password = `object`.getString("secretPassword").toString()
                        val username=`object`.getString("userName").toString()
                        val email = `object`.getString("email").toString()
                        val level=`object`.getString("level").toString()
                        val indexNum=`object`.get("indexNumber").toString()


                        Toast.makeText(this, userName + password, Toast.LENGTH_LONG).show()

                        signUp(userName, password, email,level,username,indexNum)

                        dialog.dismiss()

                    } else {
                        when (e.code) {
                            ParseException.OBJECT_NOT_FOUND -> {

                                dialog.dismiss()
                                Toast.makeText(
                                    this,
                                    "Student with Id $studentId was not found in the current semester.Contact your exams officer",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                            ParseException.CONNECTION_FAILED ->
                            {
                                dialog.dismiss()
                                Toast.makeText(applicationContext,"No internet detected",Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                dialog.dismiss()
                                Toast.makeText(this, e.message + e.code.toString(), Toast.LENGTH_LONG).show()
                            }
                        }

                        //shimmer.stopShimmer()
                        // shimmer.hideShimmer()
                    }

                }

            }

            closeKeyboard()
        }

        //shimmer=findViewById(R.id.v_shimmer)

        Slidr.attach(this)
        supportActionBar?.hide()
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    private fun signUp(
        username: String,
        password: String,
        email: String,
        lev: String,
        user2: String,
        indexNum: String
    ) {
        val user = ParseUser()

        // Set the user's username and password, which can be obtained by a forms
        user.username = username
        user.email = email
        user.setPassword(password)
        user.put("level", lev)
        user.put("accountName",user2)
        user.put("indexNumber",indexNum)
        user.signUpInBackground {
            if (it == null) {

                ParseUser.logOut()
                val intent = Intent(applicationContext, StudentVerification::class.java)
                intent.putExtra("username", user2)
                startActivity(intent)
            } else {
                when (it.code) {
                    ParseException.USERNAME_TAKEN -> {
                        ParseUser.logOut()
                        val intent = Intent(applicationContext, StudentVerification::class.java)
                        intent.putExtra("username", username)
                        startActivity(intent)
                    }
                    ParseException.CONNECTION_FAILED -> {
                        ParseUser.logOut()
                        Toast.makeText(applicationContext,"No internet detected",Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(this, it.code.toString(), Toast.LENGTH_LONG).show()
                        ParseUser.logOut()
                    }
                }

                //                shimmer.stopShimmer()
                //                shimmer.hideShimmer()


            }
        }

    }

    private fun showSnackbar(v:View,message:String){
        val snackbar= Snackbar.make(v,message, Snackbar.LENGTH_SHORT)
        snackbar.show()

    }

    fun constraintClick3(view: View) {

        closeKeyboard()
    }


}