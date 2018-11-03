package ch.hesso.l8erproject.l8er

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import ch.hesso.l8erproject.l8er.models.SMSModel
import ch.hesso.l8erproject.l8er.tools.setAlarm
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    //value needed to get back te result when asking for specific user permission
    private val RequestCodeSendSMS = 2
    private val RequestCodeReadContact = 3

    // value use to identify the broadcast intent linked to a specific planned sms
    // TODO change this value to be increment at the creation of a new sms. But we should always be able to delete a plan sms first
    private val SVCSMSSENDERID = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //use this line to "reboot" the db. CAUTION this will earse all the content
//        val smsDBHelper: SMSDBHelper = SMSDBHelper(this)
//        smsDBHelper.onUpgrade(smsDBHelper.writableDatabase,0,1)

        // will check if permission are granted, if not will ask the user
        checkPermission()

        btnCancel.setOnClickListener {
            deleteAlarm()
        }


        setUpHourEditText()
        setUpDateEditText()

        //change the clock to have a 24 hours display
        timePicker.setIs24HourView(true)


        btnTmr.setOnClickListener {

            var calendar = Calendar.getInstance()

            if (android.os.Build.VERSION.SDK_INT >= 23) {
                calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth,
                        timePicker.hour, timePicker.minute, 0)
            } else {
                calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth,
                        timePicker.currentHour, timePicker.currentMinute, 0)
            }

            // create a sms model using UI information
            val smsModel: SMSModel = SMSModel(
                    SVCSMSSENDERID,
                    edtxtNumber.text.toString(),
                    edtxtText.text.toString(),
                    calendar.timeInMillis)

            // set an alarm trough the sms planner
            setAlarm(this, smsModel)

        }

        btnChangeView.setOnClickListener {
            val intent = Intent(this, ListViewActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setUpHourEditText() {
        val myCalendar = Calendar.getInstance()


        val time = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            myCalendar.set(Calendar.SECOND, 0)

            val myFormat = "HH:mm" //In which you need put here
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

            edtxtHour.setText(sdf.format(myCalendar.getTime()))
        }


        edtxtHour.setOnClickListener {
            TimePickerDialog(this, time,
                    myCalendar.get(Calendar.HOUR_OF_DAY),
                    myCalendar.get(Calendar.MINUTE),
                    true).show()
        }

        
    }


    private fun setUpDateEditText() {
        val myCalendar = Calendar.getInstance()


        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            val myFormat = "dd/MM/yy"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

            edtxtDate.setText(sdf.format(myCalendar.getTime()))
        }


        edtxtDate.setOnClickListener {
            DatePickerDialog(this, date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }


    }




    /**
     * will check if permission are granted, if not will ask the user
     * results are send to onRequestPermissionsResult()
     */
    private fun checkPermission() {

        val smsPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        val contactPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)

        if (contactPerm != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), RequestCodeReadContact)
        }

        if (smsPerm != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), RequestCodeSendSMS)
        }
    }

    /**
     * handle permission request results
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RequestCodeSendSMS, RequestCodeReadContact -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show()
                    closeNow()
                }
            }
        }
    }

    /**
     * delete a specific alarm
     * TODO: change the code to take an smsid in parameter.
     * TODO: Move the code to the smsPlanner script ?
     */
    private fun deleteAlarm() {

        val am: AlarmManager? = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val cancelIntent = Intent(this, SMSSenderBroadcastReceiver::class.java)
        val cancelPendingIntent = PendingIntent.getBroadcast(this, SVCSMSSENDERID, cancelIntent, 0)

        am!!.cancel(cancelPendingIntent)
    }


    private fun closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity()
        } else {
            finish()
        }
    }

}
