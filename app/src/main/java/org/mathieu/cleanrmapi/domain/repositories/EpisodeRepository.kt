package org.mathieu.cleanrmapi.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.mathieu.cleanrmapi.domain.models.character.Character
import org.mathieu.cleanrmapi.domain.models.episode.Episode

interface EpisodeRepository {
    suspend fun getEpisodes(ids: List<String>): Flow<List<Episode>>

}