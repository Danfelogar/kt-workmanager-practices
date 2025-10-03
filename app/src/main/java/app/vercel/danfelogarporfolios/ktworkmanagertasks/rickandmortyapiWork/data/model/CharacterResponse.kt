package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponse(
    val results: List<Character>
)