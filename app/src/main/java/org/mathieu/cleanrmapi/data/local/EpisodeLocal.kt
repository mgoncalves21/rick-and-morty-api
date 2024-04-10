package org.mathieu.cleanrmapi.data.local

import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.mathieu.cleanrmapi.data.local.objects.EpisodeObject

internal class EpisodeLocal(private val database: RealmDatabase) {

    suspend fun getEpisodes(ids: List<String>): Flow<List<EpisodeObject>> {
        val queryValues = ids.joinToString(separator = ",", prefix = "{", postfix = "}")
        return database.use {
            query<EpisodeObject>("id IN $queryValues").find().asFlow().map { it.list }
        }
    }
    suspend fun saveEpisodes(episodes: List<EpisodeObject>) {
        episodes.forEach {
            insert(it)
        }
    }

    suspend fun insert(episode: EpisodeObject) {
        database.write {
            copyToRealm(episode, UpdatePolicy.ALL)
        }
    }

}