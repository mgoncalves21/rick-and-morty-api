package org.mathieu.cleanrmapi.data.local.objects

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse
import org.mathieu.cleanrmapi.domain.models.episode.Episode

/**
 * Represents a character entity stored in the SQLite database. This object provides fields
 * necessary to represent all the attributes of a character from the data source.
 * The object is specifically tailored for SQLite storage using Realm.
 *
 * @property id Unique identifier of the character.
 * @property name Name of the character.

 */
internal class EpisodeObject: RealmObject {
    var id: Int = -1
    var name: String = ""
    var airDate: String = ""
    var episodeCode: String = ""
    var characterUrls: String = "" // Les URLs des personnages concaténés en une seule chaîne
    var url: String = ""
    var created: String = ""
}



internal fun EpisodeResponse.toRealmObject(): EpisodeObject = EpisodeObject().apply {
    id = this@toRealmObject.id
    name = name
    airDate = air_date
    episodeCode = episode
    characterUrls = characters.joinToString(",") // Convertir la liste d'URLs en une chaîne
    url = url
    created = created
}



internal fun EpisodeObject.toModel() = Episode(
    id = id,
    name = name,
    airDate = airDate,
    episodeCode = episodeCode,
    characterUrls = characterUrls.split(","),
    url = url,
    created = created
)
