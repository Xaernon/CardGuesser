package com.example.cardguesser

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform