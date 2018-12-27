package ch.hesso.l8erproject.l8er

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import android.util.Log
import ch.hesso.l8erproject.l8er.tools.PermissionHandler


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


        // will check if permission are granted, if not will ask the user
        PermissionHandler.checkPersmission(this)

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

            if (fieldconditions()) {

                // create a sms model using UI information
                val smsModel: SMSModel = SMSModel(
                        smsdbHelper.getLastId(),
                        edtxtNumber.text.toString(),
                        txtviewName.text.toString(),
                        edtxtText.text.toString(),
                        popupCalendar.timeInMillis)

                // create a new planned sms trough the sms planner
                setNewPlannedSMS(this, smsModel)

                val intent = Intent(this, ListViewActivity::class.java)
                startActivity(intent)
            }

        }

        btnSearch.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE)
            startActivityForResult(intent, PICK_CONTACT);
        }

    }


    override fun onResume() {
        super.onResume()
        PermissionHandler.checkPersmission(this)
    }

    /**
     * this function is called when the user click on the validate button
     * it is used to prevent the creation of an sms model
     * with problematic/absent data
     *
     * todo check for sql injection in text
     */
    private fun fieldconditions(): Boolean {

        var isNumberNotEmpy: Boolean = edtxtNumber.text.toString().length > 0
        var isTextNotEmpy: Boolean = edtxtText.text.toString().length > 0

        var isDateInFuture: Boolean = popupCalendar.timeInMillis > System.currentTimeMillis()


        return isNumberNotEmpy && isTextNotEmpy && isDateInFuture
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
            popupCalendar.set(Calendar.YEAR, year);
            popupCalendar.set(Calendar.MONTH, monthOfYear);
            popupCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        PermissionHandler.checkPermissionResult(this,requestCode,grantResults)
    }

}
