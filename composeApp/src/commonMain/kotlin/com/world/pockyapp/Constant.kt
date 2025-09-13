package com.world.pockyapp

object Constant {

    private const val DEV_ENV = "http://172.20.10.12:443/api/v1"
    private const val PROD_ENV = "https://pockyappbackend.onrender.com/api/v1"

    private const val DEV_ENV_HOST = "172.20.10.12:443"
    private const val PROD_ENV_HOST = "pockyappbackend.onrender.com"

    var SHARED_LINK = DEV_ENV
    private var SHARED_LINK_HOST = DEV_ENV_HOST

    val ws = "ws://$SHARED_LINK_HOST/ws"

    fun getUrl(id:String?):String = "$SHARED_LINK/stream/media/$id"
}