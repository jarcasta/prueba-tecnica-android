package com.alejandro.pokesearch.core.domain

/** Listado de las categorias a mostrar de PokeAPI. */
enum class PokeCategory(
    val filterKind: AttributeKind?,
) {
    POKEMON(filterKind = AttributeKind.TYPE),
    ITEM_CATEGORY(filterKind = AttributeKind.POCKET),
    REGION(filterKind = null),
    GENERATION(filterKind = null),
}
