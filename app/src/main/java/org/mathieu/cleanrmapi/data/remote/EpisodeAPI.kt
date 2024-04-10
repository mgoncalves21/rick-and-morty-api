package org.mathieu.cleanrmapi.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse

internal class EpisodeApi(private val client: HttpClient) {

    suspend fun getEpisodes(ids: List<String>): List<EpisodeResponse> {
        val response = client.get("https://rickandmortyapi.com/api/episode/${ids.joinToString(",")}")
        if (response.status == HttpStatusCode.OK) {
            return response.body()
        } else {
            throw Exception("Failed to fetch episodes: ${response.status}")
        }
    }

}
