package com.alejandro.pokesearch.feature.auth.domain

import java.time.Instant
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun findByUsername(username: Username): User?

    suspend fun create(username: Username, at: Instant): User

    suspend fun update(user: User)

    fun observeAll(): Flow<List<User>>
}

interface SessionRepository {
    fun observeActiveUser(): Flow<User?>

    suspend fun open(userId: UserId)

    suspend fun close()
}


fun interface FavoriteCountReader {
    fun observeCountsByUser(): Flow<Map<UserId, Int>>
}

fun interface Clock {
    fun now(): Instant
}
