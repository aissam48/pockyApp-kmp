package com.world.pockyapp.screens.challengeDetails

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
expect fun ChallengeDetailsVideoComponent(mediaUrl: String, navController: NavController)