package com.alejandro.pokesearch.core.domain

enum class AttributeKind(val apiPath: String) {
    TYPE(apiPath = "type"),
    POCKET(apiPath = "item-pocket"),
}

sealed interface AttributeFilter {
    data object None : AttributeFilter

    @ConsistentCopyVisibility
    data class ByAttribute private constructor(val kind: AttributeKind, val value: String) : AttributeFilter {
        companion object {
            fun of(kind: AttributeKind, value: String): Outcome<ByAttribute, InvalidAttributeValue> {
                val normalized = value.trim().lowercase()
                return if (normalized.isEmpty()) {
                    Outcome.Failure(InvalidAttributeValue)
                } else {
                    Outcome.Success(ByAttribute(kind, normalized))
                }
            }
        }
    }
}

data object InvalidAttributeValue
