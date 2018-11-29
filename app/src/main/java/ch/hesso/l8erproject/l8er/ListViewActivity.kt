package ch.hesso.l8erproject.l8er

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import ch.hesso.l8erproject.l8er.adapter.SMSAdapter
import ch.hesso.l8erproject.l8er.tools.SMSDBHelper
import ch.hesso.l8erproject.l8er.tools.SwipeToDelete
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
            val adapter_SMS = SMSAdapter(listItems)
            adapter = adapter_SMS
            addItemDecoration(DividerItemDecoration(this@ListViewActivity, DividerItemDecoration.VERTICAL))

            val swipeHandler = object : SwipeToDelete(this@ListViewActivity) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapter_SMS.removeAt(viewHolder.adapterPosition)
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(this)
        }

    }
}
