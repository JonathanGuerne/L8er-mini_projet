package ch.hesso.l8erproject.l8er.tools

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.telephony.SmsManager
import android.widget.Toast

val updateDBIntentName = "DB-UPDATE"

fun sendSMS(context: Context, smsId: Int, number: String, text: String, mustDelete: Boolean) {
    SmsManager.getDefault().sendTextMessage(number, null, text, null, null)
    val smsDBHelper = SMSDBHelper(context)

    if (mustDelete) {
        smsDBHelper.deleteSMS(smsId)
    }
    
    Toast.makeText(context, "sms sent.", Toast.LENGTH_SHORT).show()

    val intent: Intent = Intent(updateDBIntentName)
    intent.putExtra("event","remove sms $smsId from db")

    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
}

//private fun contact(){
//        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                null, null, null,
//                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
//
//        while (phones!!.moveToNext()) {
//            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//            val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//
//            if (name.toLowerCase().equals("jonathan guerne")) {
//                number = phoneNumber
//                break
//            }
//        }
//        phones.close()
//}