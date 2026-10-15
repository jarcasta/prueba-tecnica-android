package com.alejandro.pokesearch.core.domain

sealed interface Outcome<out T, out E> {
    data class Success<out T>(val value: T) : Outcome<T, Nothing>

    data class Failure<out E>(val error: E) : Outcome<Nothing, E>
}

fun <T, E> Outcome<T, E>.getOrNull(): T? = (this as? Outcome.Success)?.value
