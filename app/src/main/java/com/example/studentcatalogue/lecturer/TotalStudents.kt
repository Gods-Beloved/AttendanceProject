package com.example.studentcatalogue.lecturer

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.studentcatalogue.R

class TotalStudents : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var toolbar: Toolbar

    private lateinit var spinner: Spinner

    private lateinit var totalStudents:TextView

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    private lateinit var recyclerView: RecyclerView

    private lateinit var totalAdapter: TotalAdapter

    private lateinit var totalAdapterCurrent: TotalAdapterCurrent



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_students)

        spinner=findViewById(R.id.v_spinner)

        toolbar=findViewById(R.id.total_students_bar)





      val array=  ArrayAdapter.createFromResource(this,R.array.cummulate,android.R.layout.simple_spinner_item)

        array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter=array


        spinner.onItemSelectedListener=this

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)



    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
        (parent.getChildAt(0) as TextView).setTextSize(18F)

        when(parent.getItemAtPosition(position).toString()){



            "Total"->
                displayTotal()

            "Current"->displayCurrent()

        }

    }

    private fun displayCurrent() {
        recyclerView=findViewById(R.id.v_recycleView)

        swipeRefreshLayout =findViewById(R.id.v_refresh_layout)



        totalAdapterCurrent=TotalAdapterCurrent(applicationContext)

        recyclerView.adapter=totalAdapterCurrent

        swipeRefreshLayout.setOnRefreshListener {
            displayCurrent()
            swipeRefreshLayout.isRefreshing=false
        }
    }

    private fun displayTotal() {
        swipeRefreshLayout =findViewById(R.id.v_refresh_layout)

        recyclerView=findViewById(R.id.v_recycleView)

        totalAdapter= TotalAdapter(applicationContext)

        recyclerView.adapter=totalAdapter



        swipeRefreshLayout.setOnRefreshListener {
            displayTotal()
            swipeRefreshLayout.isRefreshing=false
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
        (parent.getChildAt(0) as TextView).setTextSize(18F)

        displayCurrent()

    }


}