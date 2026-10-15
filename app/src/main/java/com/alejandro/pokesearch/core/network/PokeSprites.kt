package com.alejandro.pokesearch.core.network

/** Imagenes a obtener de PokeAPI segun el id o nombre del recurso. */

private const val SPRITES_BASE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites"

fun pokemonArtworkUrl(pokemonId: Int): String = "$SPRITES_BASE_URL/pokemon/other/official-artwork/$pokemonId.png"
fun itemSpriteUrl(itemName: String): String = "$SPRITES_BASE_URL/items/$itemName.png"
