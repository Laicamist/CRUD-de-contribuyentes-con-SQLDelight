package com.laicamist.crudsqldelight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.laicamist.crudsqldelight.cache.AndroidDatabaseDriverFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(AndroidDatabaseDriverFactory(this))
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(AndroidDatabaseDriverFactory(LocalContext.current))
}
