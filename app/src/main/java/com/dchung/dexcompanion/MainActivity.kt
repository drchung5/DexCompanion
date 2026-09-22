package com.dchung.dexcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.dchung.dexcompanion.ui.theme.DexCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DexCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        sendGlucoseToWatch(147)
    }

    private fun sendGlucoseToWatch(glucose: Int) {
        val putDataMapRequest = PutDataMapRequest.create("/glucose").apply {
            dataMap.putInt("glucose", glucose)

            // Forces each test update to be treated as new data.
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }

        val request = putDataMapRequest.asPutDataRequest().setUrgent()

        Wearable.getDataClient(this)
            .putDataItem(request)
            .addOnSuccessListener {
                println("Glucose sent: $glucose")
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DexCompanionTheme {
        Greeting("Android")
    }
}