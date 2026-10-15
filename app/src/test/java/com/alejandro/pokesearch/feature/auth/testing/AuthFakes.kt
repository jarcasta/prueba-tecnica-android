package com.alejandro.pokesearch.feature.auth.testing

import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.auth.domain.Clock
import com.alejandro.pokesearch.feature.auth.domain.FavoriteCountReader
import com.alejandro.pokesearch.feature.auth.domain.SessionRepository
import com.alejandro.pokesearch.feature.auth.domain.User
import com.alejandro.pokesearch.feature.auth.domain.UserId
import com.alejandro.pokesearch.feature.auth.domain.UserRepository
import com.alejandro.pokesearch.feature.auth.domain.Username
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

fun username(raw: String): Username = requireNotNull(Username.of(raw).getOrNull()) { "Invalid test username: $raw" }

class FixedClock(var now: Instant = Instant.parse("2026-10-14T12:00:00Z")) : Clock {
    override fun now(): Instant = now
}

class FakeUserRepository : UserRepository {
    private val users = MutableStateFlow<List<User>>(emptyList())
    private var nextId = 1L
    var createCalls = 0
        private set

    val all: List<User> get() = users.value

    fun givenUser(raw: String, createdAt: Instant, lastLoginAt: Instant = createdAt): User {
        val user = User(UserId(nextId++), username(raw), createdAt, lastLoginAt)
        users.update { it + user }
        return user
    }

    override suspend fun findByUsername(username: Username): User? = users.value.firstOrNull { it.username == username }

    override suspend fun create(username: Username, at: Instant): User {
        createCalls++
        val user = User(UserId(nextId++), username, at, at)
        users.update { it + user }
        return user
    }

    override suspend fun update(user: User) {
        users.update { list -> list.map { if (it.id == user.id) user else it } }
    }

    override fun observeAll(): Flow<List<User>> = users
}

class FakeSessionRepository(private val userRepository: FakeUserRepository) : SessionRepository {
    private val activeUserId = MutableStateFlow<UserId?>(null)

    val activeId: UserId? get() = activeUserId.value

    override fun observeActiveUser(): Flow<User?> =
        combine(activeUserId, userRepository.observeAll()) { id, users -> users.firstOrNull { it.id == id } }

    override suspend fun open(userId: UserId) {
        activeUserId.value = userId
    }

    override suspend fun close() {
        activeUserId.value = null
    }
}

class FakeFavoriteCountReader : FavoriteCountReader {
    val counts = MutableStateFlow<Map<UserId, Int>>(emptyMap())

    override fun observeCountsByUser(): Flow<Map<UserId, Int>> = counts
}
