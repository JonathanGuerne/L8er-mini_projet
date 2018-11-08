package ch.hesso.l8erproject.l8er.tools

import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast

fun sendSMS(context: Context,number: String,text: String) {
    SmsManager.getDefault().sendTextMessage(number, null, text, null, null)
    Toast.makeText(context, "sms sent.", Toast.LENGTH_SHORT).show()
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