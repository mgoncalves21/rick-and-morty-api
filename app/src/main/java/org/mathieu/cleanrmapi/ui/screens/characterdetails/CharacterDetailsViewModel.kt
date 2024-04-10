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

                updateState {
                    copy(
                        avatarUrl = character.avatarUrl,
                        name = character.name,
                        isLoading = false,
                        error = null
                    )
                }

                val episodes = episodeRepository.getEpisodes()
                    updateState { copy(
                        episodes = episodes
                    )
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