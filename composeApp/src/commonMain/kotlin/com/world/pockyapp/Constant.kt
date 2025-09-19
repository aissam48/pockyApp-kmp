package com.world.pockyapp

object Constant {

    private const val DEV_ENV = "http://192.168.100.182:443/api/v1"
    private const val PROD_ENV = "https://pockyappbackend.onrender.com/api/v1"

    private const val DEV_ENV_HOST = "192.168.100.182:443"
    private const val PROD_ENV_HOST = "pockyappbackend.onrender.com"

    var SHARED_LINK = DEV_ENV
    private var SHARED_LINK_HOST = DEV_ENV_HOST

    //val ws = "ws://$SHARED_LINK_HOST"
    val ws = "http://192.168.100.182:443"

    fun getUrl(id:String?):String = "$SHARED_LINK/stream/media/$id"
}