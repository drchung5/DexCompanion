package com.dchung.dexcompanion

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.TextView
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class DexcomNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        if (sbn.packageName != "com.dexcom.g7") {
            return
        }

        val glucose = findGlucose(sbn.notification.bigContentView)
            ?: findGlucose(sbn.notification.contentView)

        if (glucose != null) {
            Log.d("DexCompanion", "Dexcom glucose = $glucose")
            sendGlucoseToWatch(glucose)
        } else {
            Log.d("DexCompanion", "No glucose value found")
        }
    }

    private fun findGlucose(remoteViews: RemoteViews?): Int? {
        if (remoteViews == null) return null

        return try {
            val root = remoteViews.apply(this, null)
            findGlucoseInView(root)
        } catch (e: Exception) {
            Log.e("DexCompanion", "Unable to inspect Dexcom notification", e)
            null
        }
    }

    private fun findGlucoseInView(view: View): Int? {

        if (view is TextView) {
            val text = view.text.toString().trim()

            val value = text.toIntOrNull()

            // Reasonable CGM glucose range
            if (value != null && value in 40..400) {
                return value
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val result = findGlucoseInView(view.getChildAt(i))

                if (result != null) {
                    return result
                }
            }
        }

        return null
    }

    private fun sendGlucoseToWatch(glucose: Int) {

        val putDataMapRequest = PutDataMapRequest.create("/glucose").apply {
            dataMap.putInt("glucose", glucose)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }

        val request = putDataMapRequest
            .asPutDataRequest()
            .setUrgent()

        Wearable.getDataClient(this)
            .putDataItem(request)
            .addOnSuccessListener {
                Log.d("DexCompanion", "Glucose sent to watch: $glucose")
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "DexCompanion",
                    "Failed to send glucose to watch",
                    exception
                )
            }
    }
}