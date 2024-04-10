package org.mathieu.cleanrmapi.data.repositories

import CharacterResponse
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.mathieu.cleanrmapi.data.local.CharacterLocal
import org.mathieu.cleanrmapi.data.local.objects.CharacterObject
import org.mathieu.cleanrmapi.data.local.objects.toModel
import org.mathieu.cleanrmapi.data.local.objects.toRealmObject
import org.mathieu.cleanrmapi.data.remote.CharacterApi
import org.mathieu.cleanrmapi.domain.models.character.Character
import org.mathieu.cleanrmapi.domain.repositories.CharacterRepository

private const val CHARACTER_PREFS = "character_repository_preferences"
private val nextPage = intPreferencesKey("next_characters_page_to_load")

private val Context.dataStore by preferencesDataStore(
    name = CHARACTER_PREFS
)

internal class CharacterRepositoryImpl(
    private val context: Context,
    private val characterApi: CharacterApi,
    private val characterLocal: CharacterLocal
) : CharacterRepository {

    override suspend fun getCharacters(): Flow<List<Character>> =
        characterLocal
            .getCharacters()
            .mapElement(transform = CharacterObject::toModel)
            .also { if (it.first().isEmpty()) fetchNext() }

    private suspend fun fetchNext() {

        val page = context.dataStore.data.map { prefs -> prefs[nextPage] }.first()

        if (page != -1) {

            val response = characterApi.getCharacters(page)

            val nextPageToLoad = response.info.next?.split("?page=")?.last()?.toInt() ?: -1

            context.dataStore.edit { prefs -> prefs[nextPage] = nextPageToLoad }

            val objects = response.results.map(transform = CharacterResponse::toRealmObject)

            characterLocal.saveCharacters(objects)
        }

    }


    override suspend fun loadMore() = fetchNext()

    override suspend fun getCharacter(id: Int): Character =
        characterLocal.getCharacter(id)?.toModel()
            ?: characterApi.getCharacter(id = id)?.let { response ->
                val obj = response.toRealmObject()
                characterLocal.insert(obj)
                obj.toModel()
            }
            ?: throw Exception("Character not found.")


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