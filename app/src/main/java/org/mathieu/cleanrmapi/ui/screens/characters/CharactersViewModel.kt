package org.mathieu.cleanrmapi.ui.screens.characters

import android.app.Application
import org.koin.core.component.inject
import org.mathieu.cleanrmapi.domain.models.character.Character
import org.mathieu.cleanrmapi.domain.repositories.CharacterRepository
import org.mathieu.cleanrmapi.ui.core.Destination
import org.mathieu.cleanrmapi.ui.core.ViewModel

sealed interface CharactersAction {
    data class SelectedCharacter(val character: Character): CharactersAction
    object LoadMore : CharactersAction
}

class CharactersViewModel(application: Application) : ViewModel<CharactersState>(CharactersState(), application) {

    private val characterRepository: CharacterRepository by inject()


    init {

        collectData(
            source = { characterRepository.getCharacters() }
        ) {

            onSuccess {
                updateState { copy(characters = it, error = null) }
            }

            onFailure {
                updateState { copy(characters = emptyList(), error = it.toString()) }
            }

            updateState { copy(isLoading = false) }
        }

    }

    fun handleAction(action: CharactersAction) {
        when(action) {
            is CharactersAction.SelectedCharacter -> selectedCharacter(action.character)
            is CharactersAction.LoadMore -> loadMoreCharacters()

        }
    }

    private fun loadMoreCharacters() {
        //viewModelScope.launch {
       //     characterRepository.loadMore()
        //}
    }
    private fun selectedCharacter(character: Character) =
        sendEvent(Destination.CharacterDetails(character.id.toString()))

}


data class CharactersState(
    val isLoading: Boolean = true,
    val characters: List<Character> = emptyList(),
    val error: String? = null
)