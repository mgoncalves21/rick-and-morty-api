package org.mathieu.cleanrmapi.ui.screens.characterdetails

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.mathieu.cleanrmapi.domain.models.episode.Episode
import org.mathieu.cleanrmapi.domain.repositories.CharacterRepository
import org.mathieu.cleanrmapi.domain.repositories.EpisodeRepository
import org.mathieu.cleanrmapi.ui.core.ViewModel


class CharacterDetailsViewModel(application: Application) : ViewModel<CharacterDetailsState>(CharacterDetailsState(), application) {

    private val characterRepository: CharacterRepository by inject()
    private val episodeRepository: EpisodeRepository by inject()

    fun init(characterId: Int) {
        updateState { copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val character = characterRepository.getCharacter(id = characterId)
                val episodeIds = character.episodes // Assurez-vous que ceci retourne une liste d'IDs d'épisodes
                val episodesFlow = episodeRepository.getEpisodes(episodeIds)

                episodesFlow.collect { episodes ->
                    updateState {
                        copy(
                            avatarUrl = character.avatarUrl,
                            name = character.name,
                            episodes = episodes,
                            isLoading = false,
                            error = null
                        )
                    }
                }

            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        error = e.toString()
                    )
                }
            }
        }
    }
}



data class CharacterDetailsState(
    val isLoading: Boolean = true,
    val avatarUrl: String = "",
    val name: String = "",
    val episodes: Flow<List<Episode>> = emptyFlow(),
    val error: String? = null
)