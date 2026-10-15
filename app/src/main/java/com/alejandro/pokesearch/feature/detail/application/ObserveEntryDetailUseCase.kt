package com.alejandro.pokesearch.feature.detail.application

import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.feature.detail.domain.DetailRepository
import com.alejandro.pokesearch.feature.detail.domain.EntryDetail
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveEntryDetailUseCase @Inject constructor(
    private val detailRepository: DetailRepository,
) {
    operator fun invoke(key: EntryKey): Flow<EntryDetail?> = detailRepository.observe(key)
}
