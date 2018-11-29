package ch.hesso.l8erproject.l8er

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.util.Log
import android.widget.Toast


class WifiBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val action = intent.getAction()
            if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                val state: SupplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)
                if (SupplicantState.isValidState(state) && state == SupplicantState.COMPLETED) {
                    Log.d("WIFIManager", "New connexion to wifi")
                    Toast.makeText(context, "New connexion to wifi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
