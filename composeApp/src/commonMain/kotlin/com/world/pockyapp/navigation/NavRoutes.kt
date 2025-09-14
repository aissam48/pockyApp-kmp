package com.world.pockyapp.navigation

enum class NavRoutes(val route: String) {
    GOOGLE_MAPS("google_maps"),

    SPLASH("splash"),
    LOGIN("login"),
    REGISTER("register"),
    HOME("home"),
    CAMERA("camera"),
    PICKER("picker"),
    POST_PREVIEW("post_preview"),
    MOMENT_PREVIEW("moment_preview"),
    SHOW_MOMENTS("show_moments"),
    SETTINGS("settings"),
    EDIT_PROFILE("edit_profile"),
    EDIT_LOCATION("edit_location"),
    MOMENTS("moments"),
    MOMENTS_BY_LOCATION("moments_by_location"),
    SEARCH("search"),
    MY_PROFILE("my_profile"),
    PROFILE_PREVIEW("profile_preview"),
    CHANGE_PASSWORD("change_password"),
    CHAT("chat"),
    POST("post"),
    FRIEND_REQUESTS("friend_requests"),
    BLOCKED("blocked"),
    REPORT_PROFILE("report_profile"),
}