package ch.hesso.l8erproject.l8er

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ch.hesso.l8erproject.l8er.tools.sendSMS


/**
 *  service use to send sms to a specific number
 */
class SMSSenderBroadcastReceiver : BroadcastReceiver() {

    /**
     * function call by the alarm manager at a specific time
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {

            //debug : display the content of the intent
            val bundle = intent.getExtras()
            if (bundle != null) {
                for (key in bundle!!.keySet()) {
                    val value = bundle!!.get(key)
                    Log.d("SMS-sender", String.format("%s %s (%s)", key,
                            value!!.toString(), value!!.javaClass.getName()))
                }
            }

            //get number and content and send the sms
            val number = intent.extras.getString("number")
            val text_content = intent.extras.getString("text_content")
            //sendSMS(context, number, text_content)
        }
    }

}