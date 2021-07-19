package com.example.studentcatalogue.lecturer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.studentcatalogue.PdfDocumentAdapter
import com.example.studentcatalogue.R
import com.google.android.gms.location.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.itextpdf.barcodes.BarcodeQRCode
import com.itextpdf.kernel.color.Color
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.text.DocumentException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class LectureSetup : AppCompatActivity() {

    private val storagerequestcode = 101


    private lateinit var courseText: TextInputEditText
    private lateinit var toolbar: Toolbar
    private lateinit var spinner: Spinner
    private lateinit var dateText: TextView
    private lateinit var coordi: TextView
    private lateinit var timeText: TextView
    private lateinit var generateBtn: Button
    private lateinit var succesText: TextView
    private lateinit var selectDate: Button
    private lateinit var selectTime: Button
    private lateinit var output: ImageView

    val user: ParseUser = ParseUser.getCurrentUser()
    private val courseCode = user.getString("code").toString()

    private val fileprint = "$courseCode Barcode.pdf"

    private val appPath: String
        get() {
            val dir = File(
                getExternalFilesDir(null)
                    .toString() + File.separator + resources.getString(R.string.app_name) + File.separator
            )


            if (!dir.exists()) dir.mkdir()
            return dir.path + File.separator

        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            storagerequestcode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        applicationContext,
                        "You need the storage permission ",
                        Toast.LENGTH_LONG
                    ).show()
                }


            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecture_setup)

        setPermissions()
        toolbar = findViewById(R.id.setupbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

       spinner = findViewById(R.id.v_classrooms)


        selectDate = findViewById(R.id.v_setDate)
        selectTime = findViewById(R.id.v_setTime)
        coordi = findViewById(R.id.v_cordinates)
        courseText = findViewById(R.id.c_coursename)

        succesText = findViewById(R.id.v_checked_Text)
        output = findViewById(R.id.v_output)
        dateText = findViewById(R.id.v_display_date)
        timeText = findViewById(R.id.v_display_time)
        generateBtn = findViewById(R.id.v_generate)



      setUpLocationListener()





        generateBtn.text = "Generate"
        val user = ParseUser.getCurrentUser()
        val courseCode = user.getString("code").toString()

        val classrooms= arrayListOf<String>()
        classrooms.add(0,"select a classrom")



         ParseQuery.getQuery<ParseObject>("ClassRoom").findInBackground { objects, e ->
             if(e == null )
             {

                 objects.forEach {
                     classrooms.add(it.getString("lectureHall").toString())
                 }



             }else{
                 Toast.makeText(applicationContext,e.message,Toast.LENGTH_LONG).show()
             }
         }
        val adapter= ArrayAdapter(applicationContext,android.R.layout.simple_spinner_item,classrooms)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter=adapter
        spinner.setSelection(0)


        courseText.setText(courseCode)

        generateBtn.setOnClickListener {

            val date = dateText.text.trim().toString()

            val time = timeText.text.trim().toString()


            val current= getRandomString(10)



            val appSecreteCode = "$time $date $courseCode $current YHWH"

            if (date == ("") || time == "") {
                Toast.makeText(
                    applicationContext,
                    "Please set date and time to generate code",
                    Toast.LENGTH_LONG
                ).show()

            }
           else if(spinner.selectedItemPosition == 0) {
                Toast.makeText(
                    applicationContext,
                    "Please select a room for lecture",
                    Toast.LENGTH_LONG
                ).show()

            }else{
                AlertDialog.Builder(this)
                    .setTitle("Submit?")
                    .setMessage("Do you want to confirm current lecture setup?")
                    .setPositiveButton("Yes") {
                            dialog, _ ->


                        val spinnerItem=spinner.selectedItem.toString()

                        Toast.makeText(applicationContext,spinnerItem,Toast.LENGTH_LONG).show()






                        val coordinate=ParseQuery.getQuery<ParseObject>("ClassRoom").whereEqualTo("lectureHall",spinnerItem).first

                        val latitude=coordinate.get("latitude")
                        val longitude=coordinate.get("longitude")




                        val className="CustomData"

                        val obj = ParseObject.create(className)


                        val queryCode: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>(className)

                        val value:String = courseCode.replace("\\s".toRegex(), "")

                        queryCode.whereEqualTo("course",value)

                        queryCode.getFirstInBackground { `object`, e ->

                            if(e == null)
                            {
                                `object`.increment("totalLectures")
                                `object`.put("latestCode",current)
                                `object`.put("latitude", latitude!!)
                                `object`.put("longitude", longitude!!)

                                `object`.saveEventually()

                            }else{
                                when (e.code) {
                                    ParseException.OBJECT_NOT_FOUND -> {

                                        obj.put("course",value)
                                        obj.put("latestCode",current)
                                        obj.put("totalLectures",1)
                                        obj.put("latitude", latitude!!)
                                        obj.put("longitude", longitude!!)


                                        obj.saveEventually()

                                    }
                                    ParseException.CONNECTION_FAILED -> {
                                        Toast.makeText(this,"No internet detected",Toast.LENGTH_LONG).show()


                                    }
                                    else -> {
                                        Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                        }

                        if (generateBtn.text != "Print") {
                            val writer = MultiFormatWriter()


                            val matrix = writer.encode(appSecreteCode, BarcodeFormat.QR_CODE, 250, 250)

                            val encoder = BarcodeEncoder()

                            val bitmap = encoder.createBitmap(matrix)


                            output.setImageBitmap(bitmap)
                            output.visibility = View.VISIBLE
                            succesText.visibility = View.VISIBLE

                            Toast.makeText(this,appSecreteCode,Toast.LENGTH_LONG).show()

                            createPDF(
                                StringBuilder(appPath).append(fileprint).toString(),
                                appSecreteCode,
                                date
                            )
                            generateBtn.text = "Print"

                            setDataToFalse()


                        } else {
                            printPDF()
                        }





                        dialog.dismiss()
                    }
                    .setNegativeButton("No"){
                            dialog,_->dialog.dismiss()
                    }
                    .show()
            }






        }










        selectTime.setOnClickListener {

            val isSystem24Hour = DateFormat.is24HourFormat(this)
            val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(clockFormat)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Select Lecture time")
                    .build()

            picker.show(supportFragmentManager, "Time Picker")


            picker.addOnPositiveButtonClickListener {


              //  val dateString = "${picker.hour}:${picker.minute}:00"


                //   val millisToAdd = 7_200_000L
                // final long millisToAdd = 7_200_000; //two hours

                val hours24 = object : SimpleDateFormat("HH:mm:ss") {}


                //  val dateformat = object : SimpleDateFormat("hh:mm aa") {}


                val calendar1 = Calendar.getInstance()
                calendar1.set(0, 0, 0, picker.hour, picker.minute)
                val oldDate = hours24.format(calendar1.time)


                //minutes to be added
                calendar1.add(Calendar.MINUTE,60)


                val newdate = hours24.format(calendar1.time)

                timeText.text = "$oldDate $newdate"


            }
        }


        selectDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.show(supportFragmentManager, "Date Picker")

            datePicker.addOnPositiveButtonClickListener {


                // val simpleFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
                val simpleFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)


                val date = Date(datePicker.headerText)

                dateText.text = simpleFormat.format(date)


            }
        }


    }

    private fun setDataToFalse() {
        val className = user.getString("code").toString().replace("\\s".toRegex(), "")

        val queryCode: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>(className).whereEqualTo("checked",true)

      queryCode.findInBackground { objects, e ->

          if (e == null ){
             
              objects.forEach { 
                 it.put("checked",false)
                  it.saveEventually()
              }

          }else{
              Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
          }

      }


    }

    private fun setPermissions() {
        val permission = this.let {
            ContextCompat.checkSelfPermission(
                it,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        }


        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun createPDF(path: String, e: String, date: String) {

        val user = ParseUser.getCurrentUser()

        val courseCode = user.getString("code").toString()

        try {


            if (File(path).exists()) File(path).delete()


            val file = File(path)

            val outputStream = FileOutputStream(file)

            val writer = PdfWriter(outputStream)

            val pdfDoc = PdfDocument(writer)

            val document = Document(pdfDoc)

            val qrcode = BarcodeQRCode(e)

            val barcodeObject = qrcode.createFormXObject(Color.BLACK, pdfDoc)

            val barcodeImage = Image(barcodeObject).setWidth(220f).setHeight(220f)

            pdfDoc.defaultPageSize = PageSize.A4

            val paragraph =
                Paragraph("$courseCode Barcode QR codes  for $date").setBold().setFontSize(24f)
                    .setTextAlignment(TextAlignment.CENTER)

            val line = SolidLine(1f)
            line.color = Color.BLACK

            val ls = LineSeparator(line)
            ls.setWidthPercent(40f)
            ls.marginRight = 100f




            document.add(paragraph)
            document.add(barcodeImage).setHorizontalAlignment(HorizontalAlignment.CENTER)
            document.add(ls)
            document.add(barcodeImage).setHorizontalAlignment(HorizontalAlignment.CENTER)
            document.add(ls)
            document.add(barcodeImage).setHorizontalAlignment(HorizontalAlignment.CENTER)

            document.close()




        } catch (ef: FileNotFoundException) {
            ef.printStackTrace()
        } catch (ei: IOException) {
            ei.printStackTrace()
        } catch (doc: DocumentException) {
            doc.printStackTrace()
        } finally {

        }


    }


    @SuppressLint("MissingPermission")
    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        coordi = findViewById(R.id.v_cordinates)


        val locationRequest = LocationRequest.create().apply {
            setInterval(2000).fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    for ( i in p0.locations){
                        val latitude=i.latitude
                        val longitude=i.longitude

                        coordi.text = "Latitude :$latitude\nLongitude :$longitude"
                    }
                }
            }, Looper.myLooper()!!)
    }

    private fun printPDF() {

        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager

        try {
            val printDoc =
                PdfDocumentAdapter(StringBuilder(appPath).append(fileprint).toString(), fileprint)

            printManager.print("document", printDoc, PrintAttributes.Builder().build())

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun makeRequest() {
        requestPermissions(
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            storagerequestcode
        )
    }

    private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }


}