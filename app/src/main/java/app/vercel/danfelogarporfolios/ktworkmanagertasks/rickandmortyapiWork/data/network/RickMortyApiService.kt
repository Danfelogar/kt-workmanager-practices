package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.network

import retrofit2.http.GET
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.model.CharacterResponse

interface RickMortyApiService {

    @GET("character")
    suspend fun getCharacters(): CharacterResponse
}