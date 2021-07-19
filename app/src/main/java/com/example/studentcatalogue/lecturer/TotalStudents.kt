@file:Suppress("DEPRECATION")

package com.example.studentcatalogue.lecturer

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.studentcatalogue.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.border.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.text.DocumentException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat

class TotalStudents : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var toolbar: Toolbar

    private lateinit var spinner: Spinner



    private lateinit var totalStudents: TextView


    private lateinit var extendedFloatingActionButton: ExtendedFloatingActionButton

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    private lateinit var recyclerView: RecyclerView

    private lateinit var totalAdapter: TotalAdapter



    private lateinit var totalAdapterCurrent: TotalAdapterCurrent

    val user: ParseUser = ParseUser.getCurrentUser()
    val level = user.get("level").toString()
    private var className = user.getString("code").toString().replace("\\s".toRegex(), "")

    private val fileprint = "$className Lecture"

    private val appPath: String
        get() {
            val dir = File(
                getExternalFilesDir(null)
                    .toString() + File.separator + resources.getString(R.string.app_name) + File.separator
            )


            if (!dir.exists()) dir.mkdir()
            return dir.path + File.separator

        }


    //  private val level = user.get("level").toString()


    private val query: ParseQuery<ParseObject> =
        ParseQuery.getQuery<ParseObject>(className)
    private val queryStudents: ParseQuery<ParseObject> =
        ParseQuery.getQuery<ParseObject>("Student").whereEqualTo("level", level)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_students)

        spinner = findViewById(R.id.v_spinner)

        toolbar = findViewById(R.id.total_students_bar)




        val array = ArrayAdapter.createFromResource(
            this,
            R.array.cummulate,R.layout.spinner_item_view
        )

        array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = array




        spinner.onItemSelectedListener = this

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

//        (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
//        (parent.getChildAt(0) as TextView).textSize = 19F

        when (parent!!.getItemAtPosition(position).toString()) {


            "Total Students" ->
                displayTotal()

            "Student Present" -> displayCurrent()

        }

    }



    private fun displayCurrent() {
        recyclerView = findViewById(R.id.v_recycleView)

        swipeRefreshLayout = findViewById(R.id.v_refresh_layout)

        extendedFloatingActionButton=findViewById(R.id.v_ext_fab)

        totalStudents = findViewById(R.id.v_toolbar_total)

        extendedFloatingActionButton=findViewById(R.id.v_ext_fab)

        extendedFloatingActionButton.visibility=View.VISIBLE

        val valid = query.whereEqualTo("checked", true).count()

        "${valid}/${queryStudents.count()}".also { totalStudents.text = it }

        extendedFloatingActionButton.setOnClickListener {


createStoreDialog()


        }

        totalAdapterCurrent = TotalAdapterCurrent(applicationContext)

        recyclerView.adapter = totalAdapterCurrent


        swipeRefreshLayout.setOnRefreshListener {
            displayCurrent()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun createStoreDialog() {
        AlertDialog.Builder(this)
            .setView(R.layout.lecturedetails)
            .setPositiveButton("Save") { dialog, _ ->
                dialog as AlertDialog
                val lecturerTitle = dialog.findViewById<TextInputEditText>(R.id.v_lecture_title)
                val picker= dialog.findViewById<com.shawnlin.numberpicker.NumberPicker>(R.id.number_picker)

            if (lecturerTitle.text?.isEmpty() == true){
                Toast.makeText(applicationContext,"Please provide lecture title",Toast.LENGTH_LONG).show()
            }else{


                picker.wrapSelectorWheel = true
                picker.isFadingEdgeEnabled=true



                val title = lecturerTitle?.text?.toString()
                val number2=picker?.value.toString()

if(query.whereEqualTo("checked",true).count() == 0){
    Toast.makeText(applicationContext,"No student has checked attendance",Toast.LENGTH_LONG).show()
}else{
    createPDF(StringBuilder(appPath).append("$fileprint $number2.pdf" ).toString(),title.toString(),number2)
    Toast.makeText(applicationContext,"Attendance list generated successfully",Toast.LENGTH_LONG).show()
}

            }



                dialog.dismiss()
            }
            .setNegativeButton("Cancel"){
                dialog,_->dialog.dismiss()
            }
            .show()
    }

    private fun displayTotal() {
        swipeRefreshLayout = findViewById(R.id.v_refresh_layout)

        recyclerView = findViewById(R.id.v_recycleView)

        totalStudents = findViewById(R.id.v_toolbar_total)

        totalAdapter = TotalAdapter(applicationContext)

       // totalAdapter.setOnItemClickListener(this)

        extendedFloatingActionButton=findViewById(R.id.v_ext_fab)

        extendedFloatingActionButton.visibility=View.GONE
        


        val valid = query.whereEqualTo("checked", true).count()
            val valid2=query.whereEqualTo("checked",false).count()

        "${valid+valid2}/${queryStudents.count()}".also { totalStudents.text = it }


        recyclerView.adapter = totalAdapter




        swipeRefreshLayout.setOnRefreshListener {
            displayTotal()
            swipeRefreshLayout.isRefreshing = false
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
//        (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
//        (parent.getChildAt(0) as TextView).textSize = 18F

        displayCurrent()


    }

    private fun createPDF(path: String,title:String,number:String) {



        try {


            if (File(path).exists()) File(path)


            val file = File(path)

            val outputStream = FileOutputStream(file)

            val writer = PdfWriter(outputStream)

            val pdfDoc = PdfDocument(writer)

            val document = Document(pdfDoc)



            pdfDoc.defaultPageSize = PageSize.A4


query.whereEqualTo("checked",true)
            query.findInBackground { objects, e ->

                if (e==null){
                    val calenderDate = object : SimpleDateFormat("EEE, d MMM yyyy") {}

                    val dateScanned=calenderDate.format(objects[0].updatedAt.time)

                    val paragraphTitle =
                    Paragraph("Lecture $number [${title.capitalize()}] Attendance List On $dateScanned").setBold().setFontSize(25f)
                        .setTextAlignment(TextAlignment.CENTER)

                    document.add(paragraphTitle)


                    val hours24 = object : SimpleDateFormat("HH:mm") {}
                    val table = object: Table(3){}
                    table.setFontSize(18F)
                    objects.forEach {


                        val name=it.getString("name").toString()
                        val indexNum=it.getString("indexNumber").toString()

                        val date=hours24.format(it.updatedAt.time)

                        table.addCell(getCell(name, TextAlignment.LEFT))
                        table.addCell(getCell(indexNum, TextAlignment.CENTER))
                        table.addCell(getCell(date, TextAlignment.RIGHT))




                    }
                    document.add(table)
                    document.close()
                }else{
                 Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
                }


            }


        } catch (ef: FileNotFoundException) {
            ef.printStackTrace()
        } catch (ei: IOException) {
            ei.printStackTrace()
        } catch (doc: DocumentException) {
            doc.printStackTrace()
        } finally {

        }


    }

   private fun getCell(text:String, alignment:TextAlignment): Cell {
        val cell = Cell().add(object :Paragraph(text){})
        cell.setPadding(0F)
        cell.setTextAlignment(alignment)
        cell.setBorder(Border.NO_BORDER)
        return cell
    }






}