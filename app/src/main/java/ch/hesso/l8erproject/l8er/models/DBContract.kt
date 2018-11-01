package ch.hesso.l8erproject.l8er.models

import android.provider.BaseColumns

object DBContract {

    /* Inner class that defines the table contents */
    class SMSEntry : BaseColumns {

        companion object {
            val TABLE_NAME = "PROGRAMMED_SMS"
            val COL_ID = "id"
            val COL_REC = "receiver"
            val COL_CON = "content"
            val COL_DATE = "date"
        }
    }
}