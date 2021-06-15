package com.example.studentcatalogue.course

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.studentcatalogue.R
import com.example.studentcatalogue.lecturer.TotalAdapter

class TotalLecturers : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var toolbar: Toolbar

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var totalLecturersAdapter: TotalLecturersAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_lecturers)

        toolbar=findViewById(R.id.v_totalLecturers_bar)


        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        displayTotal()


    }

    private fun displayTotal() {
        swipeRefreshLayout =findViewById(R.id.v_refresh_layout2)

        recyclerView=findViewById(R.id.v_recycleView)

        totalLecturersAdapter = TotalLecturersAdapter(applicationContext)

        recyclerView.adapter = totalLecturersAdapter



        swipeRefreshLayout.setOnRefreshListener {
            displayTotal()
            swipeRefreshLayout.isRefreshing=false
        }

    }
}