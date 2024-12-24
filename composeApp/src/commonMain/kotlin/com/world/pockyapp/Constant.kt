package com.world.pockyapp

object Constant {
    val BASE_URL = "192.168.0.171"

    fun getUrl(id:String):String = "http://$BASE_URL:3000/api/v1/stream/media/$id"
}