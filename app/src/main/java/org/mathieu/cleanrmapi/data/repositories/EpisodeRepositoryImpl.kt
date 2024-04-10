package org.mathieu.cleanrmapi.data.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.mathieu.cleanrmapi.data.local.CharacterLocal
import org.mathieu.cleanrmapi.data.local.EpisodeLocal
import org.mathieu.cleanrmapi.data.local.objects.CharacterObject
import org.mathieu.cleanrmapi.data.local.objects.EpisodeObject
import org.mathieu.cleanrmapi.data.local.objects.toModel
import org.mathieu.cleanrmapi.data.local.objects.toRealmObject
import org.mathieu.cleanrmapi.data.remote.CharacterApi
import org.mathieu.cleanrmapi.data.remote.EpisodeApi
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse
import org.mathieu.cleanrmapi.domain.models.character.Character
import org.mathieu.cleanrmapi.domain.models.episode.Episode
import org.mathieu.cleanrmapi.domain.repositories.CharacterRepository
import org.mathieu.cleanrmapi.domain.repositories.EpisodeRepository

private const val EPISODE_PREFS = "episodes_repository_preferences"
private val nextPage = intPreferencesKey("next_episodes_page_to_load")

private val Context.dataStore by preferencesDataStore(
    name = EPISODE_PREFS
)

internal class EpisodeRepositoryImpl(
    private val context: Context,
    private val episodeApi: EpisodeApi,
    private val episodeLocal: EpisodeLocal
) : EpisodeRepository {

    override suspend fun getEpisodes(ids: List<String>): Flow<List<Episode>> =
        episodeLocal
            .getEpisodes(ids)
            .mapElement(transform = EpisodeObject::toModel)
            .also { if (it.first().isEmpty()) fetchEpisodes(ids) }

    private suspend fun fetchEpisodes(ids: List<String>) {
        val responses = episodeApi.getEpisodes(ids)
        val episodes = responses.map { it.toRealmObject() }
        episodeLocal.saveEpisodes(episodes)
    }

    fun <T> tryOrNull(block: () -> T) = try {
        block()
    } catch (_: Exception) {
        null
    }

    inline fun <T, R> Flow<List<T>>.mapElement(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
        this.map { list ->
            list.map { element -> transform(element) }
        }
}