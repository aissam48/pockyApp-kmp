import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization")
    id("com.google.gms.google-services")
    alias(libs.plugins.cocoapods)

}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            export("io.github.mirzemehdi:kmpnotifier:1.3.0")
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "15.4"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        pod("GoogleMaps") {
            version = libs.versions.pods.google.maps.get()
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("Google-Maps-iOS-Utils") {
            moduleName = "GoogleMapsUtils"
            version = libs.versions.pods.google.ios.maps.utils.get()
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
            implementation(libs.koin.androidx.compose)

            // CameraX core library
            implementation(libs.androidx.camera.core)
            // CameraX Camera2 extensions
            implementation(libs.androidx.camera.camera2)
            // CameraX Lifecycle library
            implementation(libs.androidx.camera.lifecycle)
            // CameraX View class
            implementation(libs.androidx.camera.view)
            //implementation("io.ktor:ktor-client-okhttp:3.0.1")

            implementation(libs.maps.compose)
            implementation(libs.maps.compose.utils)
            implementation(libs.play.services.maps)

        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(compose.material3)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.jetbrains.kotlinx.serialization.json)
            implementation("io.ktor:ktor-client-websockets:3.0.1")

            api(libs.datastore)
            api(libs.datastore.prefrences)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.kotlinx.coroutines.swing)


            implementation("io.coil-kt.coil3:coil-compose:3.0.4")
            implementation("io.coil-kt.coil3:coil-network-ktor2:3.0.4")
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.4")

            implementation("io.github.vinceglb:filekit-compose:0.8.8")

            implementation("io.github.onseok:peekaboo-image-picker:0.5.2")

            api("io.github.mirzemehdi:kmpnotifier:1.3.0")

            // For CIO engine
            implementation("io.ktor:ktor-client-cio:3.0.1")

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

            //location
            // Geocoding
            implementation(libs.compass.geocoder)

            // To use geocoding you need to use one or more of the following

            // Optional - Geocoder support for only iOS and Android
            implementation(libs.compass.geocoder.mobile)


            // Geolocation
            implementation(libs.compass.geolocation)

            // To use geolocation you need to use one or more of the following

            // Optional - Geolocation support for only iOS and Android
            implementation(libs.compass.geolocation.mobile)


            // Autocomplete
            implementation(libs.compass.autocomplete)

            // Optional - Autocomplete support for only iOS and Android using native Geocoder
            implementation(libs.compass.autocomplete.mobile)


            // Optional - Location permissions for mobile
            implementation(libs.compass.permissions.mobile)

        }
    }
}

android {
    namespace = "com.world.pockyapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.world.pockyapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 14
        versionName = "1.1.3.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    debugImplementation(compose.uiTooling)


}

