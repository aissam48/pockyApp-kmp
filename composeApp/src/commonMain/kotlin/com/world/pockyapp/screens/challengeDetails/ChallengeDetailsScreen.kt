package com.world.pockyapp.screens.challengeDetails

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
expect fun ChallengeDetailsScreen(challengeId: String, navController: NavHostController)