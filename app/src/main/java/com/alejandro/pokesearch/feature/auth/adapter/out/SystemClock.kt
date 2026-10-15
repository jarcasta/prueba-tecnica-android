package com.alejandro.pokesearch.feature.auth.adapter.out

import com.alejandro.pokesearch.feature.auth.domain.Clock
import java.time.Instant
import javax.inject.Inject

class SystemClock @Inject constructor() : Clock {
    override fun now(): Instant = Instant.now()
}
