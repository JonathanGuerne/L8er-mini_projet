package ch.hesso.l8erproject.l8er

import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.widget.Toast


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
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RequestCodeSendSMS) sendSMS()
    }

    private fun sendSMS() {
        var number = ""

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
            while (phones!!.moveToNext()) {
                val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                if (name.toLowerCase().equals("jonathan guerne")){
                    number = phoneNumber
                    break;
                }

            }
            phones.close()
        }
        else{
            number = "0786656369"
        }

        val text = "SMS automatique depuis kotlin :D"

        SmsManager.getDefault().sendTextMessage(number, null, text, null, null)

        Toast.makeText(this, "sms sent.", Toast.LENGTH_SHORT).show()
    }

}
