package com.world.pockyapp

object Constant {
//172.20.10.12
    private const val DEV_ENV = "http://192.168.100.53:443/api/v1"
    private const val PROD_ENV = "https://pockyappbackend.onrender.com/api/v1"

    private const val DEV_ENV_HOST = "192.168.100.53:443"
    private const val PROD_ENV_HOST = "pockyappbackend.onrender.com"

    var SHARED_LINK = DEV_ENV
    private var SHARED_LINK_HOST = DEV_ENV_HOST

    val ws = "http://$SHARED_LINK_HOST"

    //fun getUrl(id:String?):String = "$SHARED_LINK/stream/media/$id"
}