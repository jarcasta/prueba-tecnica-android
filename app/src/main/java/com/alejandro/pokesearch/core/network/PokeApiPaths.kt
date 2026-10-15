package com.alejandro.pokesearch.core.network

import com.alejandro.pokesearch.core.domain.AttributeKind
import com.alejandro.pokesearch.core.domain.PokeCategory

fun PokeCategory.toApiPath(): String = when (this) {
    PokeCategory.POKEMON -> "pokemon"
    PokeCategory.ITEM_CATEGORY -> "item-category"
    PokeCategory.REGION -> "region"
    PokeCategory.GENERATION -> "generation"
}

fun AttributeKind.toApiPath(): String = when (this) {
    AttributeKind.TYPE -> "type"
    AttributeKind.POCKET -> "item-pocket"
}
