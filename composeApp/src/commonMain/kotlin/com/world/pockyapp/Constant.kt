package com.world.pockyapp

object Constant {
    val set = "env"
    val DEV_ENV = "http://192.168.112.248:443/api/v1"
    val PROD_ENV = "https://pockyappbackend.onrender.com/api/v1"

    val DEV_ENV_HOST = "192.168.112.248:443"
    val PROD_ENV_HOST = "pockyappbackend.onrender.com"


    var SHARED_LINK = DEV_ENV
    var SHARED_LINK_HOST = DEV_ENV_HOST

    val ws = "ws://${Constant.SHARED_LINK_HOST}/ws"

    fun getUrl(id:String?):String = "$SHARED_LINK/stream/media/$id"
}