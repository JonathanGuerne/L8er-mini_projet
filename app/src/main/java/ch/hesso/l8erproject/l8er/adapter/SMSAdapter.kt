package ch.hesso.l8erproject.l8er.adapter

import android.content.Context
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ch.hesso.l8erproject.l8er.R
import ch.hesso.l8erproject.l8er.models.SMSModel
import ch.hesso.l8erproject.l8er.tools.setNewPlannedSMS
import kotlinx.android.synthetic.main.sms_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class SMSAdapter(private val smsArray: ArrayList<SMSModel>) : RecyclerView.Adapter<SMSAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sms_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = smsArray.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val interval_dict = mapOf(86400000.toLong() to "Every day", 604800000.toLong() to "Every week",
                1209600000.toLong() to "Every two weeks", 2678400000 to "Every Month",
                31536000000 to "Every year", -1 to "")
        val sms = smsArray[position]
        val content = sms.content
        val contact = if (sms.receiver_name == "") sms.receiver else sms.receiver_name
        val format = SimpleDateFormat("HH:mm dd/MM/yy")
        val date = format.format(Date(sms.date))
        val recurrence = interval_dict[sms.interval]
        val header = if (sms.interval < 0) contact else "$contact - $recurrence"

        holder.sms_content.text = content
        holder.date.text = date
        holder.contact.text = header
    }

    fun restoreItem(sms: SMSModel, list_view: View, context: Context, position: Int) {
        val contact = if(sms.receiver_name == "") sms.receiver else sms.receiver_name
        val snackbar = Snackbar.make(list_view, "Cancel planned sms for $contact", Snackbar.LENGTH_LONG)
        snackbar.setAction("UNDO", View.OnClickListener {
            smsArray.add(position, sms)
            setNewPlannedSMS(context, sms, true)
            notifyItemInserted(position)
        })
        snackbar.setActionTextColor(Color.YELLOW)
        snackbar.show()
    }

    fun removeAt(position: Int) {
        smsArray.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sms_content : TextView = itemView.sms_content
        val contact : TextView = itemView.contact
        val date : TextView = itemView.date
    }

}
