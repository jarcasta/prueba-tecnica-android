package com.alejandro.pokesearch.feature.catalog.domain

import com.alejandro.pokesearch.core.domain.EntryKey

data class CatalogEntry(
    val key: EntryKey,
    val name: String,
    val imageUrl: String?,
)
