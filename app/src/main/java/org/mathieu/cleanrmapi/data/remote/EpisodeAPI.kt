package org.mathieu.cleanrmapi.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse

internal class EpisodeApi(private val client: HttpClient) {

    suspend fun getEpisodes(ids: List<String>): List<EpisodeResponse> {
        return when {
            ids.isEmpty() -> {
                // Gestion des cas où aucune ID n'est fournie
                emptyList()
            }
            ids.size == 1 -> {
                // Cas où un seul ID est fourni
                val response = client.get("episode/${ids.first()}")
                if (response.status == HttpStatusCode.OK) {
                    listOf(response.body())
                } else {
                    throw Exception("Failed to fetch episode: ${response.status}")
                }
            }
            else -> {
                // Cas où plusieurs IDs sont fournis
                val response = client.get("episode/${ids.joinToString(",")}")
                if (response.status == HttpStatusCode.OK) {
                    response.body()
                } else {
                    throw Exception("Failed to fetch episodes: ${response.status}")
                }
            }
        }
    }
}
