package ch.hesso.l8erproject.l8er

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import ch.hesso.l8erproject.l8er.adapter.SMSAdapter
import ch.hesso.l8erproject.l8er.tools.SMSDBHelper
import kotlinx.android.synthetic.main.activity_list_view.*

class ListViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        //read the data
        val smsDBHelper = SMSDBHelper(this)
        var listItems = smsDBHelper.readAllSMS()

        sms_list_view.apply {
            layoutManager = LinearLayoutManager(this@ListViewActivity)
            adapter = SMSAdapter(listItems)
        }

    }
}
