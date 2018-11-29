package ch.hesso.l8erproject.l8er

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ch.hesso.l8erproject.l8er.tools.SMSDBHelper
import ch.hesso.l8erproject.l8er.tools.setNewPlannedSMS


class BootCompletedIntentReceiver : BroadcastReceiver() {

    /**
     * function call when the device reboot
     * will relaunch previous planned sms saved in the db
     */
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            val smsDBHelper = SMSDBHelper(context)
            val listSMS = smsDBHelper.readAllSMS()
            listSMS.forEach {
                Log.d("RestartedSMS", it.date.toString() + " " + it.receiver + " " + it.content)
                setNewPlannedSMS(context, smsModel = it, newSms = false)
            }
        }
    }

}