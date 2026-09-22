package com.dchung.dexcompanion.complication

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

class GlucoseDataListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {

            if (event.type == DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == "/glucose") {

                val dataMap =
                    DataMapItem.fromDataItem(event.dataItem).dataMap

                val glucose = dataMap.getInt("glucose")

                Log.d(
                    "DexCompanion",
                    "Received glucose: $glucose"
                )

                getSharedPreferences("glucose", MODE_PRIVATE)
                    .edit()
                    .putInt("current_glucose", glucose)
                    .apply()

                ComplicationDataSourceUpdateRequester
                    .create(
                        this,
                        android.content.ComponentName(
                            this,
                            GlucoseComplicationService::class.java
                        )
                    )
                    .requestUpdateAll()
            }
        }
    }
}