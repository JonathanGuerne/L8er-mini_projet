package ch.hesso.l8erproject.l8er.tools

import android.content.Context
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.widget.Toast

fun sendSMS(context: Context,number: String,text: String) {
    SmsManager.getDefault().sendTextMessage(number, null, text, null, null)
    Toast.makeText(context, "sms sent.", Toast.LENGTH_SHORT).show()
}