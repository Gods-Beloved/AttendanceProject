package com.example.studentcatalogue

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.barteksc.pdfviewer.PDFView

class PdfViewActivity : AppCompatActivity() {




    private lateinit var pdfView: PDFView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)


        pdfView = findViewById(R.id.v_pdfView)

        pdfView.fromAsset("cos_revised.pdf").load()

    }
}