package ch.hesso.l8erproject.l8er.tools

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import ch.hesso.l8erproject.l8er.SMSSenderBroadcastReceiver
import ch.hesso.l8erproject.l8er.models.SMSModel

fun setAlarm(context: Context, smsModel: SMSModel, newSms: Boolean = true) {

    val number = smsModel.receiver
    val text_content = smsModel.content

    val intent = getBroadcastIntent(context, number, text_content)
    val pIntent = PendingIntent.getBroadcast(context, smsModel.smsid, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

    //debug
    var tr = smsModel.date - System.currentTimeMillis()
    Log.d("SMS-sender", "Time remaining $tr")

    //use alarm manager to plan a broadcast event
    planSMS(context, pIntent, smsModel.date, newSms)

    //save to db if necessary
    if (newSms){
        saveToDB(context,smsModel)
    }
}

/**
 * simply create an Intent with specifics parameters
 */
private fun getBroadcastIntent(context: Context, number: String, textContent: String): Intent {
    val intent = Intent(context, SMSSenderBroadcastReceiver::class.java)
    intent.putExtra("number", number)
    intent.putExtra("textContent", textContent)
    return intent
}

/**
 * use teh alarm manager to plan an sms in time
 */
private fun planSMS(context: Context, pIntent: PendingIntent, date: Long, showToast: Boolean) {

    val am = context.getSystemService(Context.ALARM_SERVICE)
    if (am is AlarmManager) {
        am.set(AlarmManager.RTC, date, pIntent)
        if (showToast) {
            Toast.makeText(context, "Alarm is set", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * save sms to the db
 * TODO handle group 
 */
private fun saveToDB(context: Context, smsModel: SMSModel) {
    val smsDBHelper = SMSDBHelper(context)

    Log.d("SavingSMS", "new sms saved to db")
    smsDBHelper.insertSMS(smsModel)
}