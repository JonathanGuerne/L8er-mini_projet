package ch.hesso.l8erproject.l8er.models

import java.io.Serializable

class SMSModel(val smsid: Int, val receiver: String, val receiver_name: String, val content: String, val date: Long)  : Serializable