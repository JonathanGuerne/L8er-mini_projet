package ch.hesso.l8erproject.l8er.tools

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast

val updateDBIntentName = "DB-UPDATE"
lateinit var locationManager: LocationManager
var locationGPS : Location? = null

fun sendSMS(context: Context, smsId: Int, number: String, text: String, mustDelete: Boolean) {
    var new_text = text

    if (text.contains("\$localisation", ignoreCase = true)){
        new_text = text.replace("\$localisation", getLocation(context))
        Log.d("Location_gps", new_text)
    }

    SmsManager.getDefault().sendTextMessage(number, null, new_text, null, null)

    val smsDBHelper = SMSDBHelper(context)

    if (mustDelete) {
        smsDBHelper.deleteSMS(smsId)
    }
    
    Toast.makeText(context, "sms sent.", Toast.LENGTH_SHORT).show()

    val intent: Intent = Intent(updateDBIntentName)
    intent.putExtra("event","remove sms $smsId from db")

    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
}

@SuppressLint("MissingPermission")
private fun getLocation(context: Context): String {
    var latitude = 0.0
    var longitude = 0.0

    locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            if (location != null){
                locationGPS = location
            }
        }
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
        override fun onProviderEnabled(p0: String?) {}
        override fun onProviderDisabled(p0: String?) {}
    })

    val localLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

    if (localLocationGPS != null)
        locationGPS = localLocationGPS

    if (locationGPS != null){
        latitude = locationGPS!!.latitude
        longitude = locationGPS!!.longitude
    }

    return "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
}

//private fun contact(){
//        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                null, null, null,
//                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
//
//        while (phones!!.moveToNext()) {
//            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//            val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//
//            if (name.toLowerCase().equals("jonathan guerne")) {
//                number = phoneNumber
//                break
//            }
//        }
//        phones.close()
//}