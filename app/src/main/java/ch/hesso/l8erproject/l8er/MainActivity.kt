package ch.hesso.l8erproject.l8er

import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import java.util.*


class MainActivity : AppCompatActivity() {

    private val RequestCodeSendSMS = 2
    private val RequestCodeReadContact = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSms.setOnClickListener {

            val smsPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            val contactPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)


            if (contactPerm != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), RequestCodeReadContact)
            }

            if (smsPerm != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), RequestCodeSendSMS)
            } else {
                sendSMS()
            }
        }


        btnTmr.setOnClickListener {

            var calendar = Calendar.getInstance()

            if (android.os.Build.VERSION.SDK_INT >= 23) {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        timePicker.hour, timePicker.minute, 0)
            } else {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        timePicker.currentHour, timePicker.currentMinute, 0)
            }

            setAlarm(calendar.timeInMillis)

        }
    }

    private fun setAlarm(time: Long) {
        val am = getSystemService(Context.ALARM_SERVICE)

        val intent = Intent(this, MyAlarm::class.java)

        val pIntent = PendingIntent.getBroadcast(this, 0, intent, 0)


        var tr = time - System.currentTimeMillis()

        Log.d("MyAlarm", "Time remaining $tr")

        if (am is AlarmManager) {

            am.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY, pIntent);
            Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();

        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RequestCodeSendSMS) sendSMS()
    }

    private fun sendSMS() {
        var number = ""

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
            while (phones!!.moveToNext()) {
                val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                if (name.toLowerCase().equals("jonathan guerne")) {
                    number = phoneNumber
                    break;
                }

            }
            phones.close()
        } else {
            number = "0786656369"
        }

        val text = "SMS automatique depuis kotlin :D"

        SmsManager.getDefault().sendTextMessage(number, null, text, null, null)

        Toast.makeText(this, "sms sent.", Toast.LENGTH_SHORT).show()
    }

}
