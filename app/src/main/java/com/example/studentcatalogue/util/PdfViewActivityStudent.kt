package com.example.studentcatalogue.util

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.studentcatalogue.R
import com.github.barteksc.pdfviewer.PDFView
import com.parse.ParseObject
import com.parse.ParseQuery
import java.io.File

@Suppress("DEPRECATION")
class PdfViewActivityStudent : AppCompatActivity() {

private lateinit var refreshLayout: SwipeRefreshLayout



    private lateinit var toolbar: Toolbar



    private lateinit var pdfView: PDFView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view_student)

        toolbar=findViewById(R.id.v_ttable_bar)


        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

refreshLayout=findViewById(R.id.v_swipe_reload)
        displayPDF()

        refreshLayout.setOnRefreshListener {
            pdfView.recycle()
            displayPDF()
            refreshLayout.isRefreshing=false
        }



    }

    private fun displayPDF(){


        pdfView = findViewById(R.id.v_pdfView)

      //  pdfView.fromAsset("cos_revised.pdf").load()

        var pdfUri: File
          ParseQuery.getQuery<ParseObject>("Timetable")
            .getFirstInBackground { `object`, e ->
                if (e == null){
                    pdfUri= `object`.getParseFile("currentSemester")!!.file
                    pdfView.fromFile(pdfUri)
                        .enableSwipe(true)
                        .load()


                }else{

                    if(!isNetworkAvailable(this)){
                        Toast.makeText(applicationContext,"No internet,please connect to internet and refresh",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(applicationContext,e.message,Toast.LENGTH_LONG).show()

                    }


                }


            }



        }





    private fun  isNetworkAvailable(context:Context):Boolean {
        val conMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return conMan.activeNetworkInfo != null && conMan.activeNetworkInfo!!.isConnected
    }



}