package com.alejandro.pokesearch.core.domain

class DomainException(val error: DomainError, cause: Throwable? = null) : Exception(error.toString(), cause)
