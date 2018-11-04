package ch.hesso.l8erproject.l8er

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import ch.hesso.l8erproject.l8er.models.SMSModel
import ch.hesso.l8erproject.l8er.tools.SMSDBHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val RequestCodeSendSMS = 2
    private val RequestCodeReadContact = 3


    private val ServiceSmsSenderID = 0

    lateinit var smsDBHelper: SMSDBHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        smsDBHelper = SMSDBHelper(this)

        //use this line to "reboot" the db. CAUTION this will erase all the content
        //smsDBHelper.onUpgrade(smsDBHelper.writableDatabase,0,1)

        checkPermission()

        btnCancel.setOnClickListener {
            deleteAlarm()
        }

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

            setAlarm(calendar.timeInMillis)

        }

        btnChangeView.setOnClickListener {
            val intent = Intent(this, ListViewActivity::class.java)
            startActivity(intent)
        }

        keepOldSmsWorkingOnStart()
    }

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


    private fun setAlarm(time: Long, _number: String = "", _text_content: String = "") {
        val am = getSystemService(Context.ALARM_SERVICE)
        val intent = Intent(this, SMSSenderBroadcastReceiver::class.java)

        val number = if (_number.equals("")) edtxtNumber.text.toString() else _number
        val text_content = if (_text_content.equals("")) edtxtText.text.toString() else _text_content

        intent.putExtra("number", number)
        intent.putExtra("text_content", text_content)


        val pIntent = PendingIntent.getBroadcast(this, ServiceSmsSenderID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)


        var tr = time - System.currentTimeMillis()

        Log.d("SMS-sender", "Time remaining $tr")

        if (am is AlarmManager) {
            am.set(AlarmManager.RTC, time, pIntent)
            if( _number.equals("")){
                Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show()
            }
        }

        if (_number.equals("")){
            Log.d("SavingSMS","new sms saved to db")
            smsDBHelper.insertSMS(SMSModel(smsDBHelper.getLastId(), number, text_content, time))
        }
    }


    private fun deleteAlarm() {

        val am: AlarmManager? = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val cancelIntent = Intent(this, SMSSenderBroadcastReceiver::class.java)
        val cancelPendingIntent = PendingIntent.getBroadcast(this, ServiceSmsSenderID, cancelIntent, 0)

        am!!.cancel(cancelPendingIntent)
    }

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

    private fun keepOldSmsWorkingOnStart() {
        var listSMS = smsDBHelper.readAllSMS()
        listSMS.forEach {
            setAlarm(it.date, it.receiver, it.content)
        }
    }

    private fun closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity()
        } else {
            finish()
        }
    }

    private fun sendSMS() {
        var number = "0786656369"

        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")

        while (phones!!.moveToNext()) {
            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            if (name.toLowerCase().equals("jonathan guerne")) {
                number = phoneNumber
                break
            }
        }
        phones.close()


        val text = "SMS automatique depuis kotlin :D"

        SmsManager.getDefault().sendTextMessage(number, null, text, null, null)

        Toast.makeText(this, "sms sent.", Toast.LENGTH_SHORT).show()
    }

}
