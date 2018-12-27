package ch.hesso.l8erproject.l8er

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View


class IntervalDialog(var c: Activity)
    : Dialog(c), android.view.View.OnClickListener {

    var d: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle) {

    }

    override fun onClick(v: View) {
        dismiss()
    }
}