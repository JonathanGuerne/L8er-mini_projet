package ch.hesso.l8erproject.l8er.tools

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.DateInterval
import android.util.Log
import android.widget.Toast
import ch.hesso.l8erproject.l8er.SMSSenderBroadcastReceiver
import ch.hesso.l8erproject.l8er.models.SMSModel

fun setNewPlannedSMS(context: Context, smsModel: SMSModel, newSms: Boolean = true, update: Boolean = false) {

    val number = smsModel.receiver
    val text_content = smsModel.content
    val smsId = smsModel.smsid

    val gotInterval:Boolean = smsModel.interval >= 0

    val intent = getBroadcastIntent(context, smsId, number, text_content, gotInterval)

    val pIntent = PendingIntent.getBroadcast(context, smsModel.smsid, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

    //use alarm manager to plan a broadcast event
    if (smsModel.interval >= 0) {
        planSMS(context, pIntent, smsModel.date, smsModel.interval, newSms)
    } else {
        planSMS(context, pIntent, smsModel.date, newSms)
    }

    //save to db if necessary
    if (newSms && !update) {
        saveToDB(context, smsModel)
    }

    //update to db
    if (update && !newSms) {
        updatingDB(context, smsModel)
    }
}

/**
 * simply create an Intent with specifics parameters
 */
private fun getBroadcastIntent(context: Context, smsId: Int, number: String, textContent: String, gotInterval: Boolean): Intent {
    val intent = Intent(context, SMSSenderBroadcastReceiver::class.java)
    intent.putExtra("number", number)
    intent.putExtra("textContent", textContent)
    intent.putExtra("smsId", smsId)
    intent.putExtra("gotInterval", gotInterval)
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
            Toast.makeText(context, "SMS Planned", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * use teh alarm manager to plan an sms witch gonna repeat
 */
private fun planSMS(context: Context, pIntent: PendingIntent, date: Long, interval: Long, showToast: Boolean) {

    val am = context.getSystemService(Context.ALARM_SERVICE)
    if (am is AlarmManager) {
        am.setRepeating(AlarmManager.RTC, date, interval, pIntent)
        if (showToast) {
            Toast.makeText(context, "SMS Planned", Toast.LENGTH_SHORT).show()
        }
    }
}


/**
 * delete the sms that was planned with the corresponding id in the parameter
 */
fun deletePlannedSMS(context: Context, smsId: Int) {

    val am: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val cancelIntent = Intent(context, SMSSenderBroadcastReceiver::class.java)
    val cancelPendingIntent = PendingIntent.getBroadcast(context, smsId, cancelIntent, 0)

    am!!.cancel(cancelPendingIntent)
}

fun updatingDB(context: Context, smsModel: SMSModel) {
    val smsDBHelper = SMSDBHelper(context)

    Log.d("UpdatingSMS", "sms updated to db")
    smsDBHelper.deleteSMS(smsModel.smsid)
    smsDBHelper.insertSMS(smsModel)
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