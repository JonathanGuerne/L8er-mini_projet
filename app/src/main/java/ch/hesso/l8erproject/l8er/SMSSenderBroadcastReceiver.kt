package ch.hesso.l8erproject.l8er

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ch.hesso.l8erproject.l8er.tools.sendSMS


class SMSSenderBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {

            val bundle = intent.getExtras()
            if (bundle != null) {
                for (key in bundle!!.keySet()) {
                    val value = bundle!!.get(key)
                    Log.d("SMS-sender", String.format("%s %s (%s)", key,
                            value!!.toString(), value!!.javaClass.getName()))
                }
            }

            val number = intent.extras.getString("number")
            val text_content = intent.extras.getString("text_content")
            sendSMS(context, number, text_content)
        }
    }

}