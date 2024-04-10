package org.mathieu.cleanrmapi.data.local.objects

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
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
    var episode: String = ""
    var characters: RealmList<String> = realmListOf() // Les URLs des personnages concaténés en une seule chaîne
    var url: String = ""
    var created: String = ""
}



internal fun EpisodeResponse.toRealmObject() = EpisodeObject().also { obj ->
    obj.id = id
    obj.name = name
    obj.airDate = air_date
    obj.episode = episode
    obj.characters.addAll(characters)
    obj.url = url
    obj.created = created
}



internal fun EpisodeObject.toModel() = Episode(
    id = id,
    name = name,
    airDate = airDate,
    episode = episode,
    characters = characters,
    url = url,
    created = created
)
