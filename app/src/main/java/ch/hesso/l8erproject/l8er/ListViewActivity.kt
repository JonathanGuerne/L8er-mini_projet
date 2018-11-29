package ch.hesso.l8erproject.l8er

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import ch.hesso.l8erproject.l8er.adapter.SMSAdapter
import ch.hesso.l8erproject.l8er.tools.SMSDBHelper
import ch.hesso.l8erproject.l8er.tools.SwipeToDelete
import ch.hesso.l8erproject.l8er.tools.SwipeToEdit
import ch.hesso.l8erproject.l8er.tools.deletePlannedSMS
import kotlinx.android.synthetic.main.activity_list_view.*

class ListViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        //read the data
        val smsDBHelper = SMSDBHelper(this)
        var listItems = smsDBHelper.readAllSMS()

        sms_list_view.apply {

            val adapter_SMS = SMSAdapter(listItems)
            val list_view = findViewById<RecyclerView>(R.id.sms_list_view)

            layoutManager = LinearLayoutManager(this@ListViewActivity)
            adapter = adapter_SMS

            addItemDecoration(DividerItemDecoration(this@ListViewActivity, DividerItemDecoration.VERTICAL))

            val swipeHandler = object : SwipeToDelete(this@ListViewActivity) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val sms = listItems[viewHolder.adapterPosition]

                    if (direction == ItemTouchHelper.LEFT){
                        adapter_SMS.removeAt(viewHolder.adapterPosition)
                        deletePlannedSMS(applicationContext, sms.smsid)
                        smsDBHelper.deleteSMS(sms.smsid)
                        adapter_SMS.restoreItem(sms, list_view, applicationContext)
                    }
                }
            }

            val swipeHandler2 = object : SwipeToEdit(this@ListViewActivity) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val sms = listItems[viewHolder.adapterPosition]

                    if (direction == ItemTouchHelper.RIGHT){
                        adapter_SMS.updateItem(viewHolder.adapterPosition, sms)

                    }
                }
            }

            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            val itemTouchHelper2 = ItemTouchHelper(swipeHandler2)
            itemTouchHelper.attachToRecyclerView(this)
            itemTouchHelper2.attachToRecyclerView(this)
        }

    }
}
