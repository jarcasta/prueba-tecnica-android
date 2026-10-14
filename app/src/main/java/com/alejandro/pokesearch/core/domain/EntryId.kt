package com.alejandro.pokesearch.core.domain

@JvmInline
value class EntryId private constructor(val value: Int) {
    companion object {
        fun of(value: Int): Outcome<EntryId, InvalidEntryId> =
            if (value > 0) Outcome.Success(EntryId(value)) else Outcome.Failure(InvalidEntryId)

        /** Obtiene el ultimo id obtenido de la url, e.j. `.../pokemon/25/`. */
        fun fromResourceUrl(url: String): Outcome<EntryId, InvalidEntryId> {
            val lastSegment = url.trimEnd('/').substringAfterLast('/')
            return lastSegment.toIntOrNull()?.let(::of) ?: Outcome.Failure(InvalidEntryId)
        }
    }
}

data object InvalidEntryId
