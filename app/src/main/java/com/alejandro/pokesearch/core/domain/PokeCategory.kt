package com.alejandro.pokesearch.core.domain

/** Listado de las categorias a mostrar de PokeAPI. */
enum class PokeCategory(
    val apiPath: String,
    val filterKind: AttributeKind?,
) {
    POKEMON(apiPath = "pokemon", filterKind = AttributeKind.TYPE),
    ITEM_CATEGORY(apiPath = "item-category", filterKind = AttributeKind.POCKET),
    REGION(apiPath = "region", filterKind = null),
    GENERATION(apiPath = "generation", filterKind = null),
}
