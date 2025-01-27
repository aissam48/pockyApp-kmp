package com.world.pockyapp.navigation

import RegisterScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.world.pockyapp.network.models.model.MomentModel
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.network.models.model.StreetModel
import com.world.pockyapp.screens.camera.CameraView
import com.world.pockyapp.screens.edit_location.EditLocationScreen
import com.world.pockyapp.screens.edit_profile.EditProfileScreen
import com.world.pockyapp.screens.home.HomeScreen
import com.world.pockyapp.screens.auth.login.LoginScreen
import com.world.pockyapp.screens.blocked.BlockedScreen
import com.world.pockyapp.screens.change_password.ChangePasswordScreen
import com.world.pockyapp.screens.chat.ChatScreen
import com.world.pockyapp.screens.friend_request.FriendRequestsScreen
import com.world.pockyapp.screens.moment_by_locationscreen.MomentsByLocationScreen
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

        composable(route = "${NavRoutes.MOMENTS.route}/{moments}/{index}/{myID}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")
            val myID = backStackEntry.arguments?.getString("myID")
            println("---------------- $index")
            val modulesJson = backStackEntry.arguments?.getString("moments")?.replace("%", "/")
            val moments = modulesJson?.let {
                Json.decodeFromString<List<ProfileModel>>(it)
            } ?: emptyList()


            MomentsScreen(navController, moments, index, myID)
        }

        composable(route = "${NavRoutes.MOMENTS_BY_LOCATION.route}/{moments}/{index}/{myID}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")
            val myID = backStackEntry.arguments?.getString("myID")
            println("---------------- $index")
            val modulesJson = backStackEntry.arguments?.getString("moments")?.replace("%", "/")
            val moments = modulesJson?.let {
                Json.decodeFromString<List<StreetModel>>(it)
            } ?: emptyList()


            MomentsByLocationScreen(navController, moments, index, myID)
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
            ChatScreen(navController, conversationID = conversationID, profileID = profileID, chatRequestID = chatRequestID)
        }
    }
}