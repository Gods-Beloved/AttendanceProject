package com.example.studentcatalogue

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.studentcatalogue.course.CourseEnroll
import com.example.studentcatalogue.course.TotalLecturers
import com.example.studentcatalogue.signin.LoginType
import com.example.studentcatalogue.util.PdfViewActivityStudent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.parse.ParseUser
import us.zoom.sdk.*

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private var doubleBackToExitPressedOnce = false

   private lateinit var drawerLayout2: DrawerLayout

   private lateinit var toolbar: Toolbar

   //private lateinit var studentName:TextView
   // private lateinit var studentNameHeader:TextView

    val user: ParseUser = ParseUser.getCurrentUser()
    val username= user.getString("accountName")
    private val level=user.get("level").toString()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


      // studentName=findViewById(R.id.v_stud_dash_name)

        //studentNameHeader=findViewById(R.id.v_stud_name)

        //studentNameHeader.text=username

      //  studentName.text = username

        initializeSdk(this@MainActivity)

        toolbar=findViewById(R.id.v_lec_toolbar)

setUpNavigationDrawerMeny()


    }

   private fun setUpNavigationDrawerMeny(){
val navigationView=findViewById<NavigationView>(R.id.nav_view2)


       drawerLayout2=findViewById(R.id.v_drawer_layout)

       navigationView.setNavigationItemSelectedListener(this@MainActivity)

       val user=ParseUser.getCurrentUser()

       val name=user.getString("accountName")

       val headerView = navigationView.getHeaderView(0)
       val navUsername = headerView.findViewById(R.id.v_stud_lec_name) as TextView
       navUsername.text=name

       val actionBarDrawerToggle=ActionBarDrawerToggle(this,drawerLayout2,toolbar,R.string.draweropen,R.string.drawerclose)

       drawerLayout2.addDrawerListener(actionBarDrawerToggle)

       actionBarDrawerToggle.syncState()

    }



    private fun createJoinMeetingDialog() {
        AlertDialog.Builder(this)
                .setView(R.layout.zoomjoin)
                .setPositiveButton("Join") { dialog, _ ->
                    dialog as AlertDialog
                    val numberInput = dialog.findViewById<TextInputEditText>(R.id.meeting_no_input)
                    val passwordInput = dialog.findViewById<TextInputEditText>(R.id.password_input)
                    val meetingNumber = numberInput?.text?.toString()
                    val password = passwordInput?.text?.toString()
                    meetingNumber?.takeIf { it.isNotEmpty() }?.let { meetingNo ->
                        password?.let { pw ->
                            joinMeeting(this@MainActivity, meetingNo, pw)
                        }
                    }

                }

                .show()
    }

    private fun joinMeeting(context: Context, meetingNumber: String, pw: String) {
        val meetingService = ZoomSDK.getInstance().meetingService
        val options = JoinMeetingOptions()
        val params = JoinMeetingParams().apply {
            val currentUser=ParseUser.getCurrentUser()
            val name=currentUser.get("accountName").toString()
            val index=currentUser.get("indexNumber").toString()

            displayName ="$name ($index)"
            meetingNo = meetingNumber
            password = pw
        }
        meetingService.joinMeetingWithParams(context, params, options)
    }

    private fun initializeSdk(context: Context) {
        val sdk = ZoomSDK.getInstance()



        val params = ZoomSDKInitParams().apply {
            appKey = context.getString(R.string.appKey) //
            appSecret = context.getString(R.string.appSecret) //
            domain = "zoom.us"
            enableLog = true // Optional: enable logging for debugging
        }

        val listener = object : ZoomSDKInitializeListener {
            /**
             * If the [errorCode] is [ZoomError.ZOOM_ERROR_SUCCESS], the SDK was initialized and can
             * now be used to join/start a meeting.
             */
            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) = Unit
            override fun onZoomAuthIdentityExpired() = Unit
        }

        sdk.initialize(context, listener, params)
    }


    override fun onBackPressed() {
        if (drawerLayout2.isDrawerOpen(GravityCompat.START)){
closeDrawer()
        }else{
            if (doubleBackToExitPressedOnce) {
                finishAffinity()
                return
            }
        }



        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_LONG).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }



    fun onClick(v:MenuItem){
        when(v.itemId)
        {
            R.id.v_logout->
            MaterialAlertDialogBuilder(this)
                    .setTitle("Log Out?")
                    .setMessage("Are you sure you want to log out")

                    .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                        // Respond to negative button press
                        dialog.cancel()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                        // Respond to positive button press
                        //  val course=ParseUser.getCurrentUser().getString("courseName").toString()
                        ParseUser.logOut()
                        val intent = Intent(applicationContext, LoginType::class.java)
                        startActivity(intent)
//                        if (course == "null"){
//
                        // ParseUser.logOut()
//                            val intent= Intent(applicationContext,StudentId::class.java)
//                            startActivity(intent)
//                        }else{
//
//                            ParseUser.logOut()
//
//                            val intent= Intent(applicationContext,LecturerId::class.java)
//                            startActivity(intent)
//                        }


                    }
                    .show()


        }
    }

    fun dashBoardItemClick(view: View) {

        when(view.id){
            R.id.v_stud_enroll->
                enroll()
            R.id.v_stud_online->
                    onlineStudent()
            R.id.v_stud_time_table->
                timetableStudent()
            R.id.v_stud_totalAttended->
                    totalLectures()
        }

    }

    private fun totalLectures() {
        val intent = Intent(applicationContext, TotalLecturers::class.java)
        startActivity(intent)
    }

    private fun timetableStudent() {
        val intent = Intent(applicationContext, PdfViewActivityStudent::class.java)
        startActivity(intent)
    }

    private fun onlineStudent() {
        createJoinMeetingDialog()

    }

    private fun enroll() {
        val intent =Intent(this,CourseEnroll::class.java)
        intent.putExtra("level",level)
        startActivity(intent)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val itemName=item.title

        Toast.makeText(this,itemName,Toast.LENGTH_LONG).show()

        closeDrawer()

        when(item.itemId){

            R.id.v_table->
                timetableStudent()
            R.id.v_videocall->
                onlineStudent()
            R.id.v_attendance->
                enroll()
            R.id.v_total->totalLectures()

        }
        return true
    }

    private fun closeDrawer() {
        drawerLayout2.closeDrawer(GravityCompat.START)
    }


}