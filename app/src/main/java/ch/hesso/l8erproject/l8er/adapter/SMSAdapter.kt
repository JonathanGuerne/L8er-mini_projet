package ch.hesso.l8erproject.l8er.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ch.hesso.l8erproject.l8er.R
import ch.hesso.l8erproject.l8er.models.SMSModel
import kotlinx.android.synthetic.main.sms_row.view.*

class SMSAdapter(private val smsArray: ArrayList<SMSModel>) : RecyclerView.Adapter<SMSAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sms_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = smsArray.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val content = smsArray[position].content
        val contact = smsArray[position].receiver
        holder.sms_content.text = content
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sms_content : TextView = itemView.sms_content
    }

}
