package ch.hesso.l8erproject.l8er

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import ch.hesso.l8erproject.l8er.tools.SMSDBHelper
import kotlinx.android.synthetic.main.activity_list_view.*

class ListViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        //read the data

        val smsDBHelper = SMSDBHelper(this)
        var listSMS = smsDBHelper.readAllSMS()
        listSMS.forEach {
            Log.d("SQLiteHandler", "sms on db : " +
                    "${it.receiver.toString()}, ${it.content.toString()}")
        }


        val listItems = arrayOfNulls<String>(listSMS.size)

        for (i in 0 until listItems.size){
            val sms = listSMS[i]
            listItems[i] = sms.receiver
        }

        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,listItems)
        sms_list_view.adapter = adapter


    }
}
