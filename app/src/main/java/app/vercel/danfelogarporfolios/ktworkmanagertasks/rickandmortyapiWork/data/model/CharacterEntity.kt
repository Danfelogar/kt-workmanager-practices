package app.vercel.danfelogarporfolios.ktworkmanagertasks.rickandmortyapiWork.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity (
    @PrimaryKey
    val id: Int = 0,
    val name: String = "",
    val species: String = "",
    val gender: String = "",
    val image: String = ""
)