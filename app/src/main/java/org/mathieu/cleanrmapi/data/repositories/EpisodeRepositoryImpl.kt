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

    override suspend fun getEpisodes(): Flow<List<Episode>> =
        episodeLocal
            .getEpisodes()
            .mapElement(transform = EpisodeObject::toModel)
            .also { if (it.first().isEmpty()) fetchEpisodes() }

    /**
     * Fetches the next batch of characters and saves them to local storage.
     *
     * This function works as follows:
     * 1. Reads the next page number from the data store.
     * 2. If there's a valid next page (i.e., page is not -1), it fetches characters from the API for that page.
     * 3. Extracts the next page number from the API response and updates the data store with it.
     * 4. Transforms the fetched character data into their corresponding realm objects.
     * 5. Saves the transformed realm objects to the local database.
     *
     * Note: If the `next` attribute from the API response is null or missing, the page number is set to -1, indicating there's no more data to fetch.
     */
    private suspend fun fetchEpisodes() {

        val page = context.dataStore.data.map { prefs -> prefs[nextPage] }.first()

        if (page != -1) {

            val response = episodeApi.getEpisodes(page)

            val nextPageToLoad = response.info.next?.split("?page=")?.last()?.toInt() ?: -1

            context.dataStore.edit { prefs -> prefs[nextPage] = nextPageToLoad }

            val objects = response.results.map(transform = EpisodeResponse::toRealmObject)

            episodeLocal.saveEpisodes(objects)
        }

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