package com.alejandro.pokesearch.feature.auth.adapter.out.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)],
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val createdAt: Long,
    val lastLoginAt: Long,
)

@Entity(
    tableName = "session",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["userId"])],
)
data class SessionEntity(
    @PrimaryKey val singleton: Int = SINGLETON_KEY,
    val userId: Long,
) {
    companion object {
        const val SINGLETON_KEY = 0
    }
}
