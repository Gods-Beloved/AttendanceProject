package com.example.studentcatalogue.lecturer

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcatalogue.R

class TotalStudents : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    //private lateinit var spinner: Spinner


    private lateinit var recyclerView: RecyclerView

    private lateinit var totalAdapter: TotalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_students)


      //  spinner=findViewById(R.id.v_spinner)

        toolbar=findViewById(R.id.total_students_bar)

      val array=  ArrayAdapter.createFromResource(this,R.array.numbers,android.R.layout.simple_spinner_item)

        array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //spinner.adapter=array



        //spinner.onItemSelectedListener=this



        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Cumulative Attendance"

        recyclerView=findViewById(R.id.v_recycleView)





        totalAdapter= TotalAdapter(applicationContext)

        recyclerView.adapter=totalAdapter



    }


}