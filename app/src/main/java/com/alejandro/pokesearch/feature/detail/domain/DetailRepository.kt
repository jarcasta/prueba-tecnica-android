package com.alejandro.pokesearch.feature.detail.domain

import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.Outcome
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun observe(key: EntryKey): Flow<EntryDetail?>

    suspend fun refresh(key: EntryKey): Outcome<Unit, DomainError>
}
