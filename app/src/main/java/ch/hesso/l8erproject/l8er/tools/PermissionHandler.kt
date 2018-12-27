package ch.hesso.l8erproject.l8er.tools

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.finishAffinity
import android.widget.Toast
import ch.hesso.l8erproject.l8er.R
import java.lang.ref.ReferenceQueue
import java.util.*

class PermissionHandler {

    //value needed to get back te result when asking for specific user permission
    private val RequestAllPermissions = 101

    /**
     * will check if permission are granted, if not will ask the user
     * results are send to onRequestPermissionsResult()
     */
    private fun isPermissionGrantedAndAsk(activity: Activity) {

        val smsPerm = ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS)
        val contactPerm = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)

        var permissionToAsk = ArrayList<String>()

        if (contactPerm != PackageManager.PERMISSION_GRANTED) {
            permissionToAsk.add(Manifest.permission.READ_CONTACTS)
        }

        if (smsPerm != PackageManager.PERMISSION_GRANTED) {
            permissionToAsk.add(Manifest.permission.SEND_SMS)
        }

        val array = arrayOfNulls<String>(permissionToAsk.size)
        permissionToAsk.toArray(array)


        if (!array.isEmpty())
            ActivityCompat.requestPermissions(activity, array, RequestAllPermissions)
    }


    /**
     * handle permission request results
     */
    private fun checkPermissionResultAndExit(activity: Activity, requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            RequestAllPermissions -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, R.string.permission_not_granted, Toast.LENGTH_SHORT).show()
                    closeNow(activity)
                }
            }
        }
    }


    private fun closeNow(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity(activity)
        } else {
            activity.finish()
        }
    }

    companion object {

        val instance = PermissionHandler()
        var alreadyCalled = false

        fun checkPersmission(activity: Activity) {

            if (!alreadyCalled) {
                alreadyCalled = true
                instance.isPermissionGrantedAndAsk(activity)
            }

        }


        fun checkPermissionResult(activity: Activity, requestCode: Int, grantResults: IntArray) {
            instance.checkPermissionResultAndExit(activity, requestCode, grantResults)
        }


    }

}