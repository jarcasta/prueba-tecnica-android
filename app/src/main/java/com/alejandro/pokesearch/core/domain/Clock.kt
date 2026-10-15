package com.alejandro.pokesearch.core.domain

import java.time.Instant

fun interface Clock {
    fun now(): Instant
}
