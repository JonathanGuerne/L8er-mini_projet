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
import ch.hesso.l8erproject.l8er.tools.SMSDBHelper
import ch.hesso.l8erproject.l8er.tools.setNewPlannedSMS
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.provider.ContactsContract
import android.app.Activity
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log
import android.content.IntentFilter


class SMSCreationActivity : AppCompatActivity() {

    //value needed to get back te result when asking for specific user permission
    private val RequestCodeSendSMS = 2
    private val RequestCodeReadContact = 3

    // value use to identify the broadcast intent linked to a specific planned sms
    // TODO change this value to be increment at the creation of a new sms. But we should always be able to delete a plan sms first
    private var SVCSMSSENDERID = 0

    private val popupCalendar = Calendar.getInstance()

    private val smsdbHelper = SMSDBHelper(this)

    private val PICK_CONTACT = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //test()

        // will check if permission are granted, if not will ask the user
        checkPermission()

//        btnCancel.setOnClickListener {
//            deleteAlarm()
        //}

        /*
        val intent = getIntent()

        if (intent.hasExtra("UpdatedSMS")){
            val sms = intent.getExtras().getSerializable("UpdatedSms") as? SMSModel

            if (sms != null){
                popupCalendar.time = Date(sms.date)
            }
        }
        */

        setUpHourEditText()
        setUpDateEditText()

        btnTmr.setOnClickListener {

            SVCSMSSENDERID = smsdbHelper.getLastId()

            // create a sms model using UI information
            val smsModel: SMSModel = SMSModel(
                    SVCSMSSENDERID,
                    edtxtNumber.text.toString(),
                    edtxtText.text.toString(),
                    popupCalendar.timeInMillis)

            // create a new planned sms trough the sms planner
            setNewPlannedSMS(this, smsModel)

            val intent = Intent(this, ListViewActivity::class.java)
            startActivity(intent)
        }

        btnChangeView.setOnClickListener {
            val intent = Intent(this, ListViewActivity::class.java)
            startActivity(intent)
        }

        btnSearch.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE)
            startActivityForResult(intent, PICK_CONTACT);
        }

        //setupWIFIBroadcast()
    }

    private fun setupWIFIBroadcast() {
        val broadcastReceiver = WifiBroadcastReceiver()

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        this.registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun test() {
        val wifiManager = getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager

        var out = ""

        for (wifi: WifiConfiguration in wifiManager.configuredNetworks){
              out += wifi.SSID+"\n"
        }


        tvTest.setText(out)
    }

    public override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(reqCode, resultCode, data)

        when (reqCode) {
            PICK_CONTACT -> if (resultCode == Activity.RESULT_OK) {
                Log.d("data-contact", data.data.toString())
                val contactData = data.data
                val phones = contentResolver.query(contactData!!, null,
                        null, null, null)


                while (phones!!.moveToNext()) {

                    val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    txtviewName.setText(name)
                    edtxtNumber.setText(phoneNumber)

                }
                phones.close()
            }
        }
    }


    private fun setUpHourEditText() {

        val time = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            popupCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            popupCalendar.set(Calendar.MINUTE, minute)
            popupCalendar.set(Calendar.SECOND, 0)

            val myFormat = "HH:mm"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

            edtxtHour.setText(sdf.format(popupCalendar.getTime()))
        }


        edtxtHour.setOnClickListener {
            TimePickerDialog(this, time,
                    popupCalendar.get(Calendar.HOUR_OF_DAY),
                    popupCalendar.get(Calendar.MINUTE),
                    true).show()
        }

        val myFormat = "HH:mm"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        edtxtHour.setText(sdf.format(popupCalendar.getTime()))
    }


    private fun setUpDateEditText() {

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            popupCalendar.set(Calendar.YEAR, year)
            popupCalendar.set(Calendar.MONTH, monthOfYear)
            popupCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd/MM/yy"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            edtxtDate.setText(sdf.format(popupCalendar.getTime()))
        }


        edtxtDate.setOnClickListener {
            DatePickerDialog(this, date,
                    popupCalendar.get(Calendar.YEAR),
                    popupCalendar.get(Calendar.MONTH),
                    popupCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }


        val myFormat = "dd/MM/yy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        edtxtDate.setText(sdf.format(popupCalendar.getTime()))

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

//    /**
//     * delete a specific alarm
//     * TODO: change the code to take an smsid in parameter.
//     * TODO: Move the code to the smsPlanner script ?
//     */
//    private fun deleteAlarm() {
//
//        val am: AlarmManager? = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
//        val cancelIntent = Intent(this, SMSSenderBroadcastReceiver::class.java)
//        val cancelPendingIntent = PendingIntent.getBroadcast(this, SVCSMSSENDERID, cancelIntent, 0)
//
//        am!!.cancel(cancelPendingIntent)
//    }


    private fun closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity()
        } else {
            finish()
        }
    }

}
