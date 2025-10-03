package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.repository

import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.dao.CharacterDao
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.model.Character
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.model.CharacterEntity
import app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.network.RickMortyApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CharactersRepository {
    suspend fun getCharacters(): List<Character>
    fun observeCharacters(): Flow<List<Character>>
}
class NetworkCharactersRepository  @Inject constructor(
    private val rickMortyApiService: RickMortyApiService,
    private val characterDao: CharacterDao
) : CharactersRepository {

    override suspend fun getCharacters(): List<Character> {
        val remoteCharacters = rickMortyApiService.getCharacters().results
        val entities = remoteCharacters.map {
            CharacterEntity(
                id = it.id,
                name = it.name,
                species = it.species,
                gender = it.gender,
                image = it.image
            )
        }
        characterDao.insertCharacters(entities)
        return remoteCharacters
    }

    override fun observeCharacters(): Flow<List<Character>> {
        return characterDao.getAllCharacters().map { entities ->
            entities.map { entity ->
                Character(
                    id = entity.id,
                    name = entity.name,
                    species = entity.species,
                    gender = entity.gender,
                    image = entity.image,
                )
            }
        }
    }
}