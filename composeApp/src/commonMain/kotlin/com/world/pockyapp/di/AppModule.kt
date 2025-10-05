package com.world.pockyapp.di


import androidx.room.Room
import androidx.room.RoomDatabase
import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.network.localDB.ProfileDB
import com.world.pockyapp.network.localDB.getRoomDatabase
import com.world.pockyapp.screens.auth.login.LoginScreenViewModel
import com.world.pockyapp.screens.auth.register.RegisterScreenViewModel
import com.world.pockyapp.screens.challengeDetails.ChallengeDetailsViewModel
import com.world.pockyapp.screens.settings.blocked.BlockedViewModel
import com.world.pockyapp.screens.settings.change_password.ChangePasswordViewModel
import com.world.pockyapp.screens.chat.ChatViewModel
import com.world.pockyapp.screens.createShot.CreateShotViewModel
import com.world.pockyapp.screens.create_challenge.CreateChallengeViewModel
import com.world.pockyapp.screens.followers.FollowersViewModel
import com.world.pockyapp.screens.followers.followings.FollowingsViewModel
import com.world.pockyapp.screens.followers.friends.FriendsViewModel
import com.world.pockyapp.screens.settings.controlAccount.ControlAccountViewModel
import com.world.pockyapp.screens.settings.edit_location.EditLocationViewModel
import com.world.pockyapp.screens.settings.edit_profile.EditProfileViewModel
import com.world.pockyapp.screens.friend_request.FriendRequestsViewModel
import com.world.pockyapp.screens.google_maps.GoogleMapsViewModel
import com.world.pockyapp.screens.home.HomeViewModel
import com.world.pockyapp.screens.home.navigations.challenges.ChallengesViewModel
import com.world.pockyapp.screens.home.navigations.conversations.ConversationsViewModel
import com.world.pockyapp.screens.home.navigations.discover.DiscoverViewModel
import com.world.pockyapp.screens.home.navigations.shots.ShotsViewModel
import com.world.pockyapp.screens.moment_preview.MomentPreviewViewModel
import com.world.pockyapp.screens.moment_screen.MomentsViewModel
import com.world.pockyapp.screens.profile.ProfileViewModel
import com.world.pockyapp.screens.post_preview.PostViewModel
import com.world.pockyapp.screens.profile_preview.ProfilePreviewViewModel
import com.world.pockyapp.screens.report_profile.ReportProfileViewModel
import com.world.pockyapp.screens.search.SearchViewModel
import com.world.pockyapp.screens.settings.SettingsViewModel
import com.world.pockyapp.screens.splash_screen.SplashViewModel
import com.world.pockyapp.screens.view_post.ViewPostViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.mp.KoinPlatform


val appModule = module {

    single { ApiManager(dataStore = get()) }
    single { getRoomDatabase(builder = get()).profileDoa() }
    viewModel { SplashViewModel(dataStore = get()) }
    viewModel { LoginScreenViewModel(sdk = get(), dataStore = get()) }
    viewModel { RegisterScreenViewModel(sdk = get(), dataStore = get()) }
    single { HomeViewModel(sdk = get(), localDB = get()) }
    viewModel { PostViewModel(sdk = get()) }
    viewModel { ProfileViewModel(sdk = get()) }
    viewModel { DiscoverViewModel(sdk = get()) }
    viewModel { EditProfileViewModel(sdk = get()) }
    viewModel { EditLocationViewModel(sdk = get()) }
    viewModel { SearchViewModel(sdk = get()) }
    viewModel { ProfilePreviewViewModel(sdk = get()) }
    viewModel { ChangePasswordViewModel(sdk = get()) }
    single { ChatViewModel(sdk = get()) }
    viewModel { ConversationsViewModel(sdk = get()) }
    single { MomentsViewModel(sdk = get()) }
    viewModel { ViewPostViewModel(sdk = get()) }
    viewModel { FriendRequestsViewModel(sdk = get()) }
    viewModel { SettingsViewModel(sdk = get(), dataStore = get()) }
    viewModel { BlockedViewModel(sdk = get()) }
    viewModel { ChallengesViewModel(sdk = get()) }
    viewModel { ReportProfileViewModel(sdk = get()) }
    viewModel { GoogleMapsViewModel(sdk = get()) }
    viewModel { ControlAccountViewModel(sdk = get()) }
    viewModel { FollowersViewModel(sdk = get()) }
    viewModel { FollowingsViewModel(sdk = get()) }
    viewModel { FriendsViewModel(sdk = get()) }
    single { MomentPreviewViewModel(sdk = get()) }
    single { CreateShotViewModel(sdk = get()) }

    single { ShotsViewModel(sdk = get()) }
    single { CreateChallengeViewModel(sdk = get()) }
    single { ChallengeDetailsViewModel(sdk = get()) }

}

object ViewModelProvider {
    fun getGoogleMapsViewModel(): GoogleMapsViewModel {
        return KoinPlatform.getKoin().get()
    }

    fun getMomentsViewModel(): MomentsViewModel {
        return KoinPlatform.getKoin().get()
    }

    fun getProfileViewModel(): ProfileViewModel {
        return KoinPlatform.getKoin().get()
    }

    fun getCreateShotViewModel(): CreateShotViewModel {
        return KoinPlatform.getKoin().get()
    }

}
