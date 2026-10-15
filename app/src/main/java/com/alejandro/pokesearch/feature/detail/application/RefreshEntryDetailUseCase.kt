package com.alejandro.pokesearch.feature.detail.application

import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.Outcome
import com.alejandro.pokesearch.feature.detail.domain.DetailRepository
import javax.inject.Inject

class RefreshEntryDetailUseCase @Inject constructor(
    private val detailRepository: DetailRepository,
) {
    suspend operator fun invoke(key: EntryKey): Outcome<Unit, DomainError> = detailRepository.refresh(key)
}
