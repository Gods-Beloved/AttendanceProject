package com.example.studentcatalogue

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.budiyev.android.codescanner.*
import com.example.studentcatalogue.course.CourseEnroll
import com.example.studentcatalogue.util.PermissionUtil
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*


class Scanner : AppCompatActivity() {


    private lateinit var codeScanner: CodeScanner

    private lateinit var codeScannerView: CodeScannerView

    private lateinit var successText: TextView

    private lateinit var timer: TextView

    private val startTimeInMilliSeconds = 300000L

    private lateinit var countTime: CountDownTimer

    var timerIsRunning: Boolean = false

    private var timerEnd: Long? = null

    var timeLeftInMilli = startTimeInMilliSeconds

    private val locationRequestCode = 102



    private lateinit var codeResults: TextView

    private val millisLeft = "millisLeft"
    private val timerRunnig = "timerRunning"
    private val endTime = "endTime"
    private val prefs = "prefs"


    override fun onStart() {
        super.onStart()


        val prefs = getSharedPreferences(prefs, Context.MODE_PRIVATE)

        timeLeftInMilli = prefs.getLong(millisLeft, startTimeInMilliSeconds)
        timerIsRunning = prefs.getBoolean(timerRunnig, false)
        updateCountDowntText()

        if (timerIsRunning) {
            successText.visibility = View.VISIBLE

            codeResults.visibility =
                View.GONE
            codeScannerView.visibility =
                View.GONE
            timerEnd = prefs.getLong(endTime, 0)
            timeLeftInMilli = timerEnd!! - System.currentTimeMillis()

            if (timeLeftInMilli < 0) {

                // timeLeftInMilli=0
                timerIsRunning = false
                resetTimer()

                successText.visibility = View.GONE
                codeResults.visibility =
                    View.VISIBLE
                codeScannerView.visibility =
                    View.VISIBLE

            } else {
                startTimer()
            }
        }

        when {
            PermissionUtil.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtil.isLocationEnabled(this) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        PermissionUtil.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                PermissionUtil.requestAccessFineLocationPermission(
                    this,
                    locationRequestCode
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()

        val prefs = getSharedPreferences(prefs, Context.MODE_PRIVATE)

        val editor = prefs.edit()

        editor.putLong(millisLeft, timeLeftInMilli)
        editor.putBoolean(timerRunnig, timerIsRunning)
        timerEnd?.let { editor.putLong(endTime, it) }



        editor.apply()


    }

    @SuppressLint("MissingPermission")
    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)



        val locationRequest = LocationRequest.create().apply {
            setInterval(2000).fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
            }, Looper.myLooper()!!)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtil.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtil.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Permission not granted",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

   private lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)


toolbar=findViewById(R.id.toolbar_qrcode)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        codeResults = findViewById(R.id.v_display_view)
        timer = findViewById(R.id.v_Timer)
        successText = findViewById(R.id.v_successText)


        codeScanner()


    }


    @SuppressLint("MissingPermission")
    private fun codeScanner() {


        codeScannerView = findViewById(R.id.v_code_scanner)

        codeResults = findViewById(R.id.v_display_view)

        codeScanner = CodeScanner(this, codeScannerView)

        //  updateCountDowntText()

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            isAutoFocusEnabled = true
            isFlashEnabled = false
            scanMode = ScanMode.SINGLE

            decodeCallback = DecodeCallback {
                runOnUiThread {

//lectureCode.text=it.text.toString().substring(14,it.text.length)
                    val extras = intent.extras

                    var value: String



                    if (extras != null) {
                        value = extras.getString("courseCode").toString()

                        val current = extras.getString("codeGen").toString()

                        val longitudeFinal = extras.getDouble("longitude")
                        val latitudeFinal = extras.getDouble("latitude")


                        val resultText = it.text.toString()

                        val simpleFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)

                        val calDate = Calendar.getInstance().time

                        val currentDate = simpleFormat.format(calDate)




                        if (resultText.endsWith("YHWH") && resultText.contains(currentDate) && resultText.contains(value) && resultText.contains(current))
                        // resultText.startsWith(value) &&
                        {
                            MaterialAlertDialogBuilder(this@Scanner)
                                .setCancelable(false)
                                .setMessage("Do you want to Enroll for $value")
                                .setNeutralButton("CANCEL") { _, _ ->
                                    val intent =
                                        Intent(applicationContext, CourseEnroll::class.java)
                                    startActivity(intent)
                                }
                                .setNegativeButton("DECLINE") { dialog, _ ->
                                    dialog.cancel()

                                }
                                .setPositiveButton("ACCEPT") { dialog, _ ->
                                 //   codeResults.text = it.text.toString()

                                    val timeOne = it.text.substring(0, 8)

                                    val timeTwo = it.text.substring(9, 17)

                                    val dateformat = object : SimpleDateFormat("HH:mm:ss") {}

                                    val calTime = Calendar.getInstance().time

                                    val currentTime = dateformat.format(calTime)

                                    val time1 = object : SimpleDateFormat("HH:mm:ss") {}.parse(timeOne)
                                    val calendar1 = Calendar.getInstance()
                                    calendar1.time = time1!!
                                    calendar1.add(Calendar.DATE, 1)


                                    val time2 = object : SimpleDateFormat("HH:mm:ss") {}.parse(timeTwo)
                                    val calendar2 = Calendar.getInstance()
                                    calendar2.time = time2!!
                                    calendar2.add(Calendar.DATE, 1)

                                    val d = object : SimpleDateFormat("HH:mm:ss") {}.parse(currentTime)
                                    val calendar3 = Calendar.getInstance()
                                    calendar3.time = d!!
                                    calendar3.add(Calendar.DATE, 1)

                                    val x = calendar3.time


                                    if (x.after(calendar1.time) && x.before(calendar2.time)) {
                                        Toast.makeText(this@Scanner, "On time", Toast.LENGTH_LONG).show()
                                        codeResults.text="On Time"
                                        var latitudeInit: Double

                                        var longitudeInit: Double
                                        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)

                                        val locationRequest = LocationRequest.create().apply {
                                            setInterval(2000).fastestInterval = 2000
                                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                                        }

                                        lateinit var locationCallback: LocationCallback

                                        locationCallback = object : LocationCallback() {
                                            override fun onLocationResult(locationResult: LocationResult?) {
                                                locationResult ?: return
                                                for (location in locationResult.locations) {
                                                    longitudeInit = location.longitude
                                                    latitudeInit = location.latitude

                                                    if (distance(
                                                            latitudeFinal,
                                                            longitudeFinal,

                                                            6.6732,
                                                            -1.5674
//                                                            latitudeInit,
//                                                            longitudeInit
                                                        ) > 0.3
                                                    ) {

                                                        Toast.makeText(
                                                            this@Scanner,
                                                            "Attendance From Outside Lecture Hall Detected",
                                                            Toast.LENGTH_LONG
                                                        ).show()

                                                        codeResults.text="Attendance From Outside Lecture Hall Detected"
                                                        Toast.makeText(
                                                            this@Scanner,
                                                            distance(
                                                                6.6732,
                                                                -1.5674,
                                                                latitudeInit,
                                                                longitudeInit
                                                            ).toString(),
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        fusedLocationProviderClient.removeLocationUpdates(
                                                            locationCallback
                                                        )
                                                    } else {
                                                        fusedLocationProviderClient.removeLocationUpdates(
                                                            locationCallback
                                                        )


                                                        val currentUser =
                                                            ParseUser.getCurrentUser()


                                                        value = value.replace("\\s".toRegex(), "")


                                                        val obj = ParseObject.create(value)

                                                        val query = ParseQuery.getQuery<ParseObject>(value)


                                                        query.whereEqualTo("name", currentUser.getString("accountName"))


                                                        query.getFirstInBackground { `object`, e ->


                                                            if (e == null) {
                                                                val valid=`object`.getBoolean("checked")
                                                                val username=`object`.getString("name").toString()
                                                                if (valid)
                                                                {
                                                                    Toast.makeText(applicationContext,"$username Has Already Checked Attendance",Toast.LENGTH_LONG).show()
                                                                    codeResults.text="$username Has Already Checked Attendance."
                                                                }else{
                                                                    `object`.increment("classAttended")
                                                                    `object`.put("checked",true)
                                                                    `object`.saveEventually()
                                                                    if (timerIsRunning) {
                                                                        codeScannerView.visibility =
                                                                            View.GONE
                                                                        successText.visibility =
                                                                            View.VISIBLE

                                                                    } else {

                                                                        startTimer()


                                                                    }
                                                                }



                                                            } else {
                                                                if (e.code == ParseException.OBJECT_NOT_FOUND) {


                                                                    obj.put(
                                                                        "name",
                                                                        currentUser.getString("accountName")
                                                                            .toString()
                                                                    )
                                                                    obj.put(
                                                                        "indexNumber",
                                                                        currentUser.getString("indexNumber")
                                                                            .toString()
                                                                    )
                                                                    obj.put("classAttended", 1)
                                                                    obj.put("checked",true)

                                                                    obj.saveEventually()
                                                                    if (timerIsRunning) {
                                                                        successText.visibility =
                                                                            View.VISIBLE

                                                                        codeScannerView.visibility =
                                                                            View.GONE
                                                                    } else {

                                                                        startTimer()


                                                                    }


                                                                }
                                                            }

                                                        }




                                                        dialog.dismiss()
                                                    }
                                                }
                                            }
                                        }

                                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)


                                    } else {

                                        Toast.makeText(this@Scanner, "Late Attendance Detected", Toast.LENGTH_SHORT).show()
                                        codeResults.text="Late Attendance Detected"
                                    }
                                }
                                .show()
                        } else {

                            Toast.makeText(applicationContext, "Wrong Code Detected", Toast.LENGTH_SHORT).show()
                            codeResults.text="Wrong Code Detected"
                          //Code result text here
                        //    val resultText2 = it.text.toString()

                        }

                    }


                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    //deprecated in API 26
                    @Suppress("DEPRECATION")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else vibrator.vibrate(100)


                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Toast.makeText(this@Scanner, "ERROR:${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        codeScannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun startTimer() {

        timerEnd = System.currentTimeMillis() + timeLeftInMilli

        countTime = object : CountDownTimer(timeLeftInMilli, 1000) {
            override fun onFinish() {

                timerIsRunning = false
                resetTimer()

                successText.visibility = View.GONE
                codeResults.visibility = View.GONE
                codeScannerView.visibility = View.VISIBLE

            }

            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMilli = millisUntilFinished
                updateCountDowntText()
//                val minutes2 = ((timeLeftInMilli / 1000) / 60).toInt()
//                val seconds2 = ((timeLeftInMilli / 1000) % 60).toInt()
//
//                val timeLeftFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes2, seconds2)
//
//                timer.text = timeLeftFormated
            }

        }.start()

        timerIsRunning = true


    }

    private fun resetTimer() {
        timeLeftInMilli = startTimeInMilliSeconds
        updateCountDowntText()
    }


    private fun updateCountDowntText() {
        val minutes2 = ((timeLeftInMilli / 1000) / 60).toInt()
        val seconds2 = ((timeLeftInMilli / 1000) % 60).toInt()

        val timeLeftFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes2, seconds2)

        timer.text = timeLeftFormated
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


    private fun distance(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Double {

        //college of science latitude 6.6732 longitude -1.5674

        //college of engineering latitude 8.5459,longitude -76.9063


        val earthRadius = 3958.75 // in miles, change to 6371 for kilometer output

        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)

        val sindLat = sin(dLat / 2)
        val sindLng = sin(dLng / 2)

        val a = sindLat.pow(2.0) + sindLng.pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }


}