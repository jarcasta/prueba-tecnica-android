package com.alejandro.pokesearch.feature.auth.domain

import java.time.Instant

@JvmInline
value class UserId(val value: Long)

data class User(
    val id: UserId,
    val username: Username,
    val createdAt: Instant,
    val lastLoginAt: Instant,
) {
    fun recordLogin(at: Instant): User = copy(lastLoginAt = at)
}

data class KnownUser(
    val user: User,
    val favoriteCount: Int,
)
