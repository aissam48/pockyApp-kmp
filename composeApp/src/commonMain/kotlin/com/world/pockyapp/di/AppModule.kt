package com.world.pockyapp.di


import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.screens.auth.login.LoginScreenViewModel
import com.world.pockyapp.screens.auth.register.RegisterScreenViewModel
import com.world.pockyapp.screens.blocked.BlockedViewModel
import com.world.pockyapp.screens.change_password.ChangePasswordViewModel
import com.world.pockyapp.screens.chat.ChatViewModel
import com.world.pockyapp.screens.edit_location.EditLocationViewModel
import com.world.pockyapp.screens.edit_profile.EditProfileViewModel
import com.world.pockyapp.screens.friend_request.FriendRequestsViewModel
import com.world.pockyapp.screens.home.HomeViewModel
import com.world.pockyapp.screens.home.navigations.conversations.ConversationsViewModel
import com.world.pockyapp.screens.home.navigations.discover.DiscoverViewModel
import com.world.pockyapp.screens.home.navigations.hot.HotViewModel
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


val appModule = module {

    single { ApiManager(dataStore = get()) }

    viewModel { SplashViewModel(dataStore = get()) }
    viewModel { LoginScreenViewModel(sdk = get(), dataStore = get()) }
    viewModel { RegisterScreenViewModel(sdk = get(), dataStore = get()) }
    viewModel { HomeViewModel(sdk = get()) }
    viewModel { PostViewModel(sdk = get()) }
    viewModel { ProfileViewModel(sdk = get()) }
    viewModel { DiscoverViewModel(sdk = get()) }
    viewModel { EditProfileViewModel(sdk = get()) }
    viewModel { EditLocationViewModel(sdk = get()) }
    viewModel { SearchViewModel(sdk = get()) }
    viewModel { ProfilePreviewViewModel(sdk = get()) }
    viewModel { ChangePasswordViewModel(sdk = get()) }
    viewModel { ChatViewModel(sdk = get()) }
    viewModel { ConversationsViewModel(sdk = get()) }
    viewModel { MomentsViewModel(sdk = get()) }
    viewModel { ViewPostViewModel(sdk = get()) }
    viewModel { FriendRequestsViewModel(sdk = get()) }
    viewModel { SettingsViewModel(sdk = get(),dataStore = get()) }
    viewModel { BlockedViewModel(sdk = get()) }
    viewModel { HotViewModel(sdk = get()) }
    viewModel { ReportProfileViewModel(sdk = get()) }

}
