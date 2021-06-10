package com.example.studentcatalogue.course

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcatalogue.R
import com.parse.ParseUser
import java.util.*

class CourseEnroll : AppCompatActivity(),CourseAdapter.OnItemClickListener {



    private  val camerarequestcode=101

    private lateinit var toolbar: Toolbar

    //private val ACTIVITY_REQUEST_CODE=1


    private lateinit var recyclerView: RecyclerView

//    private lateinit var nestedDetails: NestedScrollView
//
//    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
//
//
//
//    private lateinit var displayPic: CircleImageView
//
//   // private lateinit var progressIndicator: LinearProgressIndicator
//
//    private lateinit var Lecturerposition: TextView
//    private lateinit var behaveDown: Button
//
//    private lateinit var fullName: TextView
//
//    private lateinit var department: TextView

    //private lateinit var researcg: TextView

    private lateinit var courseAdapter: CourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_enroll)

toolbar=findViewById(R.id.v_attendance_check_bar)


        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        setPermissions()



        recyclerView = findViewById(R.id.v_recycleView)
        val user = ParseUser.getCurrentUser()





        // nestedDetails=findViewById(R.id.v_bottom_sheet)

//        bottomSheetBehavior=BottomSheetBehavior.from(nestedDetails)
//
//        displayPic=findViewById(R.id.lecture_image)
//        Lecturerposition=findViewById(R.id.v_lecturer_position)
//        fullName=findViewById(R.id.v_lecturer_name)
//        researcg=findViewById(R.id.v_lecturer_interest)
//        department=findViewById(R.id.v_lecturer_department)
//
//
//        behaveDown=findViewById(R.id.v_down_btn)


//        behaveDown.setOnClickListener {
//            bottomSheetBehavior.state=BottomSheetBehavior.STATE_COLLAPSED
//        }


        courseAdapter = CourseAdapter(applicationContext)

        courseAdapter.setOnItemClickListener(this)

        recyclerView.adapter = courseAdapter

    }


    private fun setPermissions(){
        val permission= this.let {
            ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA)

        }
        val permission2= this.let {
            ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)

        }
        val permission3= this.let {
            ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)

        }



        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED||permission3 != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }



    private fun makeRequest() {
        requestPermissions(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), camerarequestcode  )
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            camerarequestcode->{
                if(grantResults.isEmpty()||grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(applicationContext,"You need the camera and location permission ", Toast.LENGTH_LONG).show()
                }
                if(grantResults[1] != PackageManager.PERMISSION_GRANTED||grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(applicationContext,"You need the location permission", Toast.LENGTH_LONG).show()
                }



            }
        }
    }





    override fun onItemClick(position: Int,intent: Intent) {




        startActivity(intent)


    }


//    override fun onItemClick(position: Int) {
//
//        bottomSheetBehavior.state=BottomSheetBehavior.STATE_EXPANDED
//
//        val parse = ParseQuery.getQuery<ParseObject>("Course")
//
//        parse.findInBackground { objects, e ->
//
//            if (e==null){
//
//                val file=objects[position].getParseFile("image")?.url
//
//                Picasso.get()
//                    .load(file)
//                    .resize(96,96)
//                    .centerCrop()
//
//                    .into(displayPic)
//
//                fullName.text=objects[position].get("lecturersFullName").toString()
//                researcg.text="Research/Interest:"+objects[position].get("research_areas").toString()
//                department.text="Dept: "+objects[position].get("department").toString()
//                Lecturerposition.text=objects[position].get("position").toString()
//
//
//
//
//            }else{
//                Toast.makeText(applicationContext,e.message, Toast.LENGTH_LONG).show()
//
//
//            }
//
//        }
//
//
//    }


}