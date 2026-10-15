package com.alejandro.pokesearch.core.network

import com.alejandro.pokesearch.core.domain.DomainError
import java.io.IOException
import retrofit2.HttpException

private const val HTTP_NOT_FOUND = 404

fun Throwable.toDomainError(): DomainError = when (this) {
    is IOException -> DomainError.NoConnection
    is HttpException -> if (code() == HTTP_NOT_FOUND) DomainError.NotFound else DomainError.Server(code())
    else -> DomainError.Unexpected
}
