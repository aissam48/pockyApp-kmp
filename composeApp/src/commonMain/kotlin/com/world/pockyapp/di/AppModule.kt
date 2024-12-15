package com.world.pockyapp.di


import com.world.pockyapp.network.ApiManager
import com.world.pockyapp.screens.home.HomeViewModel
import com.world.pockyapp.screens.login.LoginScreenViewModel
import com.world.pockyapp.screens.register.RegisterScreenViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module


val appModule = module {

    single { ApiManager(dataStore = get()) }

    viewModel { LoginScreenViewModel(sdk = get(), dataStore = get()) }
    viewModel { RegisterScreenViewModel(sdk = get()) }
    viewModel { HomeViewModel(sdk = get()) }


}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}