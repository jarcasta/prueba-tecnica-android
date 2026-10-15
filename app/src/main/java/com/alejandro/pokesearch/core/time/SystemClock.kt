package com.alejandro.pokesearch.core.time

import com.alejandro.pokesearch.core.domain.Clock
import java.time.Instant
import javax.inject.Inject

class SystemClock @Inject constructor() : Clock {
    override fun now(): Instant = Instant.now()
}
