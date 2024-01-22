package com.iviplay.gozemtest.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Toolkit(internal var context: Context) {

    fun isConnected(): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    fun displayPromptForEnablingGPS(activity: Activity) {
        val builder = MaterialAlertDialogBuilder(activity)
        val action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        val title = "Position"
        val message = "Veuillez activer votre service de localisation"

        builder.setTitle(title)
        builder.setMessage(message)
            .setPositiveButton("OK",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    activity.startActivity(Intent(action))
                    dialogInterface.dismiss()
                })
            .setNegativeButton("Annuler",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialogInterface.cancel()

                })
        builder.create().show()
    }

}