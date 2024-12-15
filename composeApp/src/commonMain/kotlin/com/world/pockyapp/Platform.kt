package com.world.pockyapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform