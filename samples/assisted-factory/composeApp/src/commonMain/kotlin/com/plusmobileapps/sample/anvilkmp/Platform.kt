package com.plusmobileapps.sample.anvilkmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform