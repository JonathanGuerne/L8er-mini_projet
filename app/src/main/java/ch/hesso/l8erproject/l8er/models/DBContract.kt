package ch.hesso.l8erproject.l8er.models

import android.provider.BaseColumns

object DBContract {

    //TODO add a group table

    /* Inner class that defines the sms table contents */
    class SMSEntry : BaseColumns {

        // specify the programmed_sms table
        companion object {
            val TABLE_NAME = "PROGRAMMED_SMS"
            val COL_ID = "id"
            val COL_REC = "receiver"
            val COL_CON = "content"
            val COL_DATE = "date"
        }
    }

    /* Inner class that defines the group table contents */
    class GroupEntry : BaseColumns {

        // specify the programmed_sms table
        companion object {
            val TABLE_NAME = "RVR_GROUP"
            val COL_ID = "id"
            val COL_NAME = "name"
        }
    }

    /* Inner class that defines the group table contents */
    class SMSGroupEntry : BaseColumns {

        // specify the programmed_sms table
        companion object {
            val TABLE_NAME = "SMS_GROUP"
            val COL_GROUP_ID = "grouid"
            val COL_SMS_ID = "smsid"
        }
    }

}

