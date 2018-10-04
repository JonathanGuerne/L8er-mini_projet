package ch.hesso.l8erproject.l8er

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyAlarm : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("MyAlarm", "Alarm just fired")
    }

}