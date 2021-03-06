package ch.hesso.l8erproject.l8er.tools

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.telephony.SmsManager
import android.util.Log
import ch.hesso.l8erproject.l8er.models.DBContract
import ch.hesso.l8erproject.l8er.models.SMSModel
import kotlin.system.exitProcess

// https://www.tutorialkart.com/kotlin-android/android-sqlite-example-application/

class SMSDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    //TODO handle group

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "ProgrammedSMS.db"

        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.SMSEntry.TABLE_NAME + " (" +
                        DBContract.SMSEntry.COL_ID + " INTEGER PRIMARY KEY," +
                        DBContract.SMSEntry.COL_REC + " TEXT," +
                        DBContract.SMSEntry.COL_REC_NAME + " TEXT," +
                        DBContract.SMSEntry.COL_CON + " TEXT," +
                        DBContract.SMSEntry.COL_DATE + " INTEGER," +
                        DBContract.SMSEntry.COL_INT + " INTEGER)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.SMSEntry.TABLE_NAME

    }


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }



    @Throws(SQLiteConstraintException::class)
    fun insertSMS(sms: SMSModel): Boolean {
        val db = writableDatabase

        val values = ContentValues()

        values.put(DBContract.SMSEntry.COL_ID, sms.smsid)
        values.put(DBContract.SMSEntry.COL_REC, sms.receiver)
        values.put(DBContract.SMSEntry.COL_REC_NAME, sms.receiver_name)
        values.put(DBContract.SMSEntry.COL_CON, sms.content)
        values.put(DBContract.SMSEntry.COL_DATE, sms.date)
        values.put(DBContract.SMSEntry.COL_INT, sms.interval)

        Log.d("SMS-sender", "values : $values")

        db.insert(DBContract.SMSEntry.TABLE_NAME, null, values)

        return true
    }


    @Throws(SQLiteConstraintException::class)
    fun deleteSMS(smsid: Int): Boolean {

        if (!readSMS(smsid).isEmpty()) {
            val db = writableDatabase

            val selection = DBContract.SMSEntry.COL_ID + " LIKE ?"

            val selectionArgs = arrayOf(smsid.toString())

            db.delete(DBContract.SMSEntry.TABLE_NAME, selection, selectionArgs)

            return true
        }

        return false
    }


    fun readSMS(smsid: Int): ArrayList<SMSModel> {
        val listSMS = ArrayList<SMSModel>()
        val db = writableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery("select * from " + DBContract.SMSEntry.TABLE_NAME + " WHERE " +
                    DBContract.SMSEntry.COL_ID + "='" + smsid + "'", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }


        var receiver: String
        var receiver_name: String
        var content: String
        var date: Long
        var interval: Long

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                receiver = cursor.getString(cursor.getColumnIndex(DBContract.SMSEntry.COL_REC))
                receiver_name = cursor.getString(cursor.getColumnIndex(DBContract.SMSEntry.COL_REC_NAME))
                content = cursor.getString(cursor.getColumnIndex(DBContract.SMSEntry.COL_CON))
                date = cursor.getLong(cursor.getColumnIndex(DBContract.SMSEntry.COL_DATE))
                interval = cursor.getLong(cursor.getColumnIndex(DBContract.SMSEntry.COL_INT))

                listSMS.add(SMSModel(smsid, receiver, receiver_name, content, date, interval))
                cursor.moveToNext()
            }
        }
        return listSMS
    }

    fun getLastId(): Int{
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from ${DBContract.SMSEntry.TABLE_NAME}" +
                    " order by ${DBContract.SMSEntry.COL_ID} desc limit 1", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return -1
        }

        var smsid = 0

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                smsid = cursor.getInt(cursor.getColumnIndex(DBContract.SMSEntry.COL_ID))
                cursor.moveToNext()
            }
        }

        return smsid + 1
    }

    fun readAllSMS(): ArrayList<SMSModel> {
        val listSMS = ArrayList<SMSModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.SMSEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var smsid: Int
        var receiver: String
        var receiver_name: String
        var content: String
        var date: Long
        var interval: Long

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                smsid = cursor.getInt(cursor.getColumnIndex(DBContract.SMSEntry.COL_ID))
                receiver = cursor.getString(cursor.getColumnIndex(DBContract.SMSEntry.COL_REC))
                receiver_name = cursor.getString(cursor.getColumnIndex(DBContract.SMSEntry.COL_REC_NAME))
                content = cursor.getString(cursor.getColumnIndex(DBContract.SMSEntry.COL_CON))
                date = cursor.getLong(cursor.getColumnIndex(DBContract.SMSEntry.COL_DATE))
                interval = cursor.getLong(cursor.getColumnIndex(DBContract.SMSEntry.COL_INT))

                listSMS.add(SMSModel(smsid, receiver, receiver_name, content, date, interval))
                cursor.moveToNext()
            }
        }

        return listSMS
    }


}
