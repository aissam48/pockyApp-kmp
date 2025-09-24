package com.world.pockyapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.world.pockyapp.screens.chat.ChatViewModel
import com.world.pockyapp.screens.home.HomeViewModel
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

class MainActivity : ComponentActivity() {

    val homeViewModel: HomeViewModel = KoinPlatform.getKoin().get()

    val chatViewModel: ChatViewModel = KoinPlatform.getKoin().get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //WindowCompat.setDecorFitsSystemWindows(window, false) // Draw behind system bars

        //enableEdgeToEdge() // Add this

        lifecycleScope.launch {

            homeViewModel.informInHomeSharedFlow.collect {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        homeViewModel.updateFcmToken(token)
                    }
                }
            }
        }

        lifecycleScope.launch {
            chatViewModel.subscribeConversationSharedFlow.collect {
                println("ChatViewModel subscribe mainActivity $it")
                FirebaseMessaging.getInstance().subscribeToTopic(it)
            }
        }

        lifecycleScope.launch {
            chatViewModel.unSubscribeConversationSharedFlow.collect {
                println("ChatViewModel unSubscribe mainActivity $it")
                FirebaseMessaging.getInstance().unsubscribeFromTopic(it)
            }
        }


        setContent {
            App()
        }
    }
}
