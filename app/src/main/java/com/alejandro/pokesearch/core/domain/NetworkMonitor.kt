package com.alejandro.pokesearch.core.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transformLatest

interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}

const val RECONNECTION_SETTLE_MILLIS = 2_000L


@OptIn(ExperimentalCoroutinesApi::class)
fun Flow<Boolean>.reconnections(settleMillis: Long = RECONNECTION_SETTLE_MILLIS): Flow<Unit> =
    distinctUntilChanged()
        .isReconnection()
        .transformLatest { reconnected ->
            if (reconnected) {
                delay(settleMillis)
                emit(Unit)
            }
        }

private fun Flow<Boolean>.isReconnection(): Flow<Boolean> = flow {
    var wasOnline: Boolean? = null
    collect { online ->
        emit(wasOnline == false && online)
        wasOnline = online
    }
}
