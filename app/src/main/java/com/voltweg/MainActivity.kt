package com.voltweg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.voltweg.ui.VoltwegApp
import com.voltweg.ui.VoltwegViewModel
import com.voltweg.ui.theme.VoltwegTheme

class MainActivity : ComponentActivity() {
    private val viewModel: VoltwegViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoltwegTheme {
                VoltwegApp(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

