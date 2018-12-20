package ch.hesso.l8erproject.l8er

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import ch.hesso.l8erproject.l8er.adapter.SMSAdapter
import ch.hesso.l8erproject.l8er.models.SMSModel
import kotlinx.android.synthetic.main.activity_list_view.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import ch.hesso.l8erproject.l8er.tools.*


class ListViewActivity : AppCompatActivity() {

    val listItems: ArrayList<SMSModel>  = ArrayList()
    lateinit var smsDBHelper: SMSDBHelper
    lateinit var adapter_SMS: SMSAdapter

    private val updateDBEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val event = intent.getStringExtra("event")
            Log.d("UPDATEDBEVENT", "Got event: $event")
            refresh(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        //use this line to "reboot" the db. CAUTION this will earse all the content
        //val smsDBHelper_debug: SMSDBHelper = SMSDBHelper(this)
        //smsDBHelper_debug.onUpgrade(smsDBHelper_debug.writableDatabase,0,1)

        //read the data
        smsDBHelper = SMSDBHelper(this)

        refresh(null)

        sms_list_view.apply {

            adapter_SMS = SMSAdapter(listItems)
            val list_view = findViewById<RecyclerView>(R.id.sms_list_view)

            adapter = adapter_SMS
            layoutManager = LinearLayoutManager(this@ListViewActivity)

            addItemDecoration(DividerItemDecoration(this@ListViewActivity, DividerItemDecoration.VERTICAL))

            val swipeHandlerDelete = object : SwipeToDelete(this@ListViewActivity) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val sms = listItems[position]

                    if (direction == ItemTouchHelper.LEFT){
                        adapter_SMS.removeAt(position)
                        deletePlannedSMS(applicationContext, sms.smsid)
                        smsDBHelper.deleteSMS(sms.smsid)
                        adapter_SMS.restoreItem(sms, list_view, applicationContext, position)
                    }
                }
            }

            val swipeHandlerEdit = object : SwipeToEdit(this@ListViewActivity) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val sms = listItems[viewHolder.adapterPosition]

                    if (direction == ItemTouchHelper.RIGHT){
                        //val intent = Intent(context, SMSCreationActivity::class.java)
                        //intent.putExtra("UpdatedSms", sms)
                        //startActivity(intent)
                    }
                }
            }

            val itemTouchHelperDelete = ItemTouchHelper(swipeHandlerDelete)
            val itemTouchHelperEdit = ItemTouchHelper(swipeHandlerEdit)
            itemTouchHelperDelete.attachToRecyclerView(this)
            itemTouchHelperEdit.attachToRecyclerView(this)
        }

        btnNewSMS.setOnClickListener {
            Log.d("BTNNEWSMS","CLICKED")
            val intent = Intent(this, SMSCreationActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                updateDBEventReceiver,  IntentFilter(updateDBIntentName));
        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                updateDBEventReceiver);
        super.onPause()
    }

    /**
     * TODO udpate the sms list based on the db at this function call
     */
    fun refresh(view: View?){
        listItems.clear()
        listItems.addAll(smsDBHelper.readAllSMS())

        if (::adapter_SMS.isInitialized)
            adapter_SMS.notifyDataSetChanged()
    }
}
