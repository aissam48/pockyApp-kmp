package com.world.pockyapp.navigation

import RegisterScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.screens.create_challenge.CreateChallengeScreen
import com.world.pockyapp.screens.settings.controlZone.ControlZoneScreen
import com.world.pockyapp.screens.auth.login.LoginScreen
import com.world.pockyapp.screens.settings.blocked.BlockedScreen
import com.world.pockyapp.screens.camera.CameraView
import com.world.pockyapp.screens.challengeDetails.ChallengeDetailsScreen
import com.world.pockyapp.screens.settings.change_password.ChangePasswordScreen
import com.world.pockyapp.screens.chat.ChatScreen
import com.world.pockyapp.screens.createShot.CreateShotScreen
import com.world.pockyapp.screens.followers.FollowersListScreen
import com.world.pockyapp.screens.followers.followings.FollowingListScreen
import com.world.pockyapp.screens.followers.friends.FriendsScreen
import com.world.pockyapp.screens.settings.controlAccount.ControlAccountScreen
import com.world.pockyapp.screens.settings.edit_location.EditLocationScreen
import com.world.pockyapp.screens.settings.edit_profile.EditProfileScreen
import com.world.pockyapp.screens.friend_request.FriendRequestsScreen
import com.world.pockyapp.screens.google_maps.GoogleMapsScreen
import com.world.pockyapp.screens.google_maps.MapComponentScreen
import com.world.pockyapp.screens.home.HomeScreen
import com.world.pockyapp.screens.home.navigations.shots.ShotsScreen
import com.world.pockyapp.screens.moment_preview.MomentPreview
import com.world.pockyapp.screens.moment_screen.MomentsScreen
import com.world.pockyapp.screens.post_preview.PostPreview
import com.world.pockyapp.screens.profile.ProfileScreen
import com.world.pockyapp.screens.profile_preview.ProfilePreviewScreen
import com.world.pockyapp.screens.report_profile.ReportProfileScreen
import com.world.pockyapp.screens.search.SearchScreen
import com.world.pockyapp.screens.settings.SettingsScreen
import com.world.pockyapp.screens.show_moment.ShowMoments
import com.world.pockyapp.screens.splash_screen.SplashScreen
import com.world.pockyapp.screens.view_post.ViewPostScreen
import kotlinx.serialization.json.Json

@Composable
fun NavigationHost(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH.route,
    ) {

        composable(NavRoutes.CREATE_CHALLENGE.route) {
            CreateChallengeScreen(navController)
        }

        composable(NavRoutes.CONTROL_ZONE.route) {
            ControlZoneScreen(navController)
        }

        composable(NavRoutes.MAP_COMPONENT.route) {
            MapComponentScreen(navController)
        }

        composable(NavRoutes.GOOGLE_MAPS.route) {
            GoogleMapsScreen(navController)
        }

        composable(NavRoutes.SPLASH.route) {
            SplashScreen(navController)
        }

        composable(NavRoutes.LOGIN.route) {
            LoginScreen(navController)
        }

        composable(NavRoutes.REGISTER.route) {
            RegisterScreen(navController)
        }

        composable(NavRoutes.HOME.route) {
            HomeScreen(navController)
        }

        composable(NavRoutes.CAMERA.route) {
            CameraView(navController)
        }

        composable(NavRoutes.POST_PREVIEW.route) {
            PostPreview(navController)
        }

        composable(NavRoutes.SETTINGS.route) {
            SettingsScreen(navController)
        }

        composable(NavRoutes.EDIT_PROFILE.route) {
            EditProfileScreen(navController)
        }

        composable(NavRoutes.EDIT_LOCATION.route) {
            EditLocationScreen(navController)
        }

        composable(NavRoutes.SEARCH.route) {
            SearchScreen(navController)
        }

        composable(NavRoutes.CHANGE_PASSWORD.route) {
            ChangePasswordScreen(navController)
        }

        composable(NavRoutes.FRIEND_REQUESTS.route) {
            FriendRequestsScreen(navController)
        }

        composable(NavRoutes.MY_PROFILE.route) {
            ProfileScreen(navController)
        }

        composable(NavRoutes.BLOCKED.route) {
            BlockedScreen(navController)
        }

        composable(NavRoutes.CONTROL_ACCOUNT.route) {
            ControlAccountScreen(navController)
        }

        composable(route = "${NavRoutes.MOMENTS.route}") { backStackEntry ->
            MomentsScreen(navController)
        }

        composable(route = "${NavRoutes.FOLLOWERS.route}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            FollowersListScreen(navController, id = id)
        }

        composable(route = "${NavRoutes.FRIENDS.route}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            FriendsScreen(navController, id = id)
        }

        composable(route = "${NavRoutes.FOLLOWINGS.route}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            FollowingListScreen(navController, id = id)
        }

        composable(route = "${NavRoutes.SHOW_MOMENTS.route}/{moments}") { backStackEntry ->
            val modulesJson = backStackEntry.arguments?.getString("moments")?.replace("%", "/")
            val moments = modulesJson?.let {
                Json.decodeFromString<List<MomentModel>>(it)
            } ?: emptyList()
            ShowMoments(navController, moments)
        }

        composable(route = "${NavRoutes.MOMENT_PREVIEW.route}/{path}") { navBackStackEntry ->
            val path = navBackStackEntry.arguments?.getString("path") ?: ""
            MomentPreview(navController, path)
        }

        composable(route = "${NavRoutes.PROFILE_PREVIEW.route}/{id}") { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getString("id") ?: ""
            ProfilePreviewScreen(navController, id = id)
        }

        composable(route = "${NavRoutes.REPORT_PROFILE.route}/{id}") { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getString("id") ?: ""
            ReportProfileScreen(navController, id = id)
        }

        composable(route = "${NavRoutes.POST.route}/{id}/{myID}") { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getString("id") ?: ""
            val myID = navBackStackEntry.arguments?.getString("myID") ?: ""
            ViewPostScreen(navController, postID = id, myID = myID)
        }

        composable(route = "${NavRoutes.CHAT.route}/{conversationID}/{profileID}/{chatRequestID}") { navBackStackEntry ->
            val conversationID = navBackStackEntry.arguments?.getString("conversationID") ?: ""
            val profileID = navBackStackEntry.arguments?.getString("profileID") ?: ""
            val chatRequestID = navBackStackEntry.arguments?.getString("chatRequestID") ?: ""
            ChatScreen(
                navController,
                conversationID = conversationID,
                profileID = profileID,
                chatRequestID = chatRequestID
            )
        }

        composable(route = NavRoutes.CREATE_SHOT.route) { navBackStackEntry ->
            CreateShotScreen(navController)
        }

        composable(route = NavRoutes.SHOTS.route) { navBackStackEntry ->
            ShotsScreen(navController)
        }

        composable(route = NavRoutes.CHALLENGE_DETAILS.route) { navBackStackEntry ->
            ChallengeDetailsScreen("", navController)
        }
    }
}