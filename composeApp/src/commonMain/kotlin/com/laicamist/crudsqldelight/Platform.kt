package com.laicamist.crudsqldelight

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform