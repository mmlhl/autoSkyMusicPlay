package me.mm.sky.auto.music.tools

import androidx.appcompat.app.AlertDialog
import android.os.Build
import android.view.WindowManager


object GlobalAlertDialog {
    fun showGlobalAlertDialog(builder: AlertDialog.Builder) {
        val alertDialog = builder.create()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alertDialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            alertDialog.window?.setType(WindowManager.LayoutParams.TYPE_PHONE)
        }
        alertDialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        alertDialog.show()
    }
}