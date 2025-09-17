package com.world.pockyapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import com.world.pockyapp.navigation.NavigationHost
import com.world.pockyapp.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    AppTheme {
        Scaffold {
            Column(Modifier.fillMaxSize()) {
                MapComponent()
            }
        }
        /*KoinContext {

            val navController = rememberNavController()
            NavigationHost(navController)

            LaunchedEffect(Unit){
                NotifierManager.addListener(object : NotifierManager.Listener {

                    override fun onNewToken(token: String) {
                        super.onNewToken(token)
                    }

                    override fun onNotificationClicked(data: PayloadData) {
                        super.onNotificationClicked(data)
                    }

                    override fun onPayloadData(data: PayloadData) {
                        super.onPayloadData(data)
                    }

                    override fun onPushNotification(title: String?, body: String?) {
                        super.onPushNotification(title, body)

                    }
                    
                })
            }
        }*/
    }}