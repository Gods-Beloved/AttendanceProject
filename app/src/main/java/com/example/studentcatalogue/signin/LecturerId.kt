package com.example.studentcatalogue.signin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
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

class LecturerId : AppCompatActivity() {

    private lateinit var lecturer_signUp:EditText

    private lateinit var constraintLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecturer_idcheck)
        Slidr.attach(this)

    }

    fun enterLecturersDashboard(view: View) {
        val dialog = ProgressDialog.show(
                this, "",
                "Please wait...", true
        )

        dialog.dismiss()


        constraintLayout=findViewById(R.id.v_constraintLayout)



       lecturer_signUp= findViewById(R.id.v_username)

        val lecturerId = lecturer_signUp.text.toString()




        if (lecturer_signUp.text.isEmpty()) {

            showSnackbar(constraintLayout,"Enter your lecturer ID number")

        } else {
            dialog.show()

            val query = ParseQuery.getQuery<ParseObject>("Lecturer")
            query.whereEqualTo("lecturerID", lecturerId)
            query.getFirstInBackground { `object`, e ->
                if (e == null) {
                    val userName = `object`.getString("secretUsername").toString()
                    val password = `object`.getString("secretPassword").toString()
                  //  val email = `object`.getString("email").toString()
                    val level=`object`.getString("level").toString()
                    val username=`object`.getString("userName").toString()
                    val code=`object`.getString("code").toString()
                    val course=`object`.getString("courseName").toString()


                    Toast.makeText(this, userName + password, Toast.LENGTH_LONG).show()

                    signUp(userName, password,level,code,course,username)

                    dialog.dismiss()

                } else {
                    if (e.code == ParseException.OBJECT_NOT_FOUND) {

                        dialog.dismiss()
                        Toast.makeText(
                                this,
                                "Lecturer with Id $lecturerId was not found in the current semester.Contact your Head of Department",
                                Toast.LENGTH_LONG
                        ).show()

                    } else {
                        dialog.dismiss()
                        Toast.makeText(this, e.message + e.code.toString(), Toast.LENGTH_LONG).show()
                    }

                    //shimmer.stopShimmer()
                    // shimmer.hideShimmer()
                }

            }

        }

        closeKeyboard()
    }


    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showSnackbar(v:View,message:String){
        val snackbar= Snackbar.make(v,message, Snackbar.LENGTH_SHORT)
        snackbar.show()

    }

    private fun signUp(
        username: String,
        password: String,
        lev: String,
        code: String,
        course: String,
        user2: String
    ) {
        val user = ParseUser()

        // Set the user's username and password, which can be obtained by a forms
        user.username = username
        user.setPassword(password)
        user.put("level", lev)
        user.put("code",code)
        user.put("courseName",course)
        user.put("accountName",user2)
        user.signUpInBackground {
            if (it == null) {

                ParseUser.logOut()
                val intent = Intent(applicationContext, LecturerVerification::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
            } else {
                if (it.code == ParseException.USERNAME_TAKEN) {
                    ParseUser.logOut()
                    val intent = Intent(applicationContext, LecturerVerification::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, it.code.toString(), Toast.LENGTH_LONG).show()
                    ParseUser.logOut()
                }

                //                shimmer.stopShimmer()
                //                shimmer.hideShimmer()


            }
        }

    }

    fun constraintClick2(view: View) {
        closeKeyboard()
    }


}
