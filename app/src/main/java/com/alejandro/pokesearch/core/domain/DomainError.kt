package com.alejandro.pokesearch.core.domain

sealed interface DomainError {
    data object NoConnection : DomainError

    data object NotFound : DomainError

    data class Server(val code: Int) : DomainError

    data object Unexpected : DomainError
}
