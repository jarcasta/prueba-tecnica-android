package com.alejandro.pokesearch.feature.auth.adapter.out.room

import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.auth.domain.SessionRepository
import com.alejandro.pokesearch.feature.auth.domain.User
import com.alejandro.pokesearch.feature.auth.domain.UserId
import com.alejandro.pokesearch.feature.auth.domain.UserRepository
import com.alejandro.pokesearch.feature.auth.domain.Username
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomUserRepository @Inject constructor(
    private val userDao: UserDao,
) : UserRepository {
    override suspend fun findByUsername(username: Username): User? =
        userDao.findByUsername(username.value)?.toDomain()

    override suspend fun create(username: Username, at: Instant): User {
        val entity = UserEntity(
            username = username.value,
            createdAt = at.toEpochMilli(),
            lastLoginAt = at.toEpochMilli(),
        )
        val id = userDao.insert(entity)
        return User(id = UserId(id), username = username, createdAt = at, lastLoginAt = at)
    }

    override suspend fun update(user: User) = userDao.update(user.toEntity())

    override fun observeAll(): Flow<List<User>> =
        userDao.observeAll().map { users -> users.mapNotNull { it.toDomain() } }
}

class RoomSessionRepository @Inject constructor(
    private val sessionDao: SessionDao,
) : SessionRepository {
    override fun observeActiveUser(): Flow<User?> = sessionDao.observeActiveUser().map { it?.toDomain() }

    override suspend fun open(userId: UserId) = sessionDao.upsert(SessionEntity(userId = userId.value))

    override suspend fun close() = sessionDao.clear()
}

private fun UserEntity.toDomain(): User? = Username.of(username).getOrNull()?.let { validUsername ->
    User(
        id = UserId(id),
        username = validUsername,
        createdAt = Instant.ofEpochMilli(createdAt),
        lastLoginAt = Instant.ofEpochMilli(lastLoginAt),
    )
}

private fun User.toEntity() = UserEntity(
    id = id.value,
    username = username.value,
    createdAt = createdAt.toEpochMilli(),
    lastLoginAt = lastLoginAt.toEpochMilli(),
)
