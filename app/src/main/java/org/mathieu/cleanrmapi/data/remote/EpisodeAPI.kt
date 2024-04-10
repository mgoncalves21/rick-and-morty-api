package org.mathieu.cleanrmapi.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse
import org.mathieu.cleanrmapi.data.remote.responses.PaginatedResponse

internal class EpisodeApi(private val client: HttpClient) {

    suspend fun getEpisodes(page: Int?): PaginatedResponse<EpisodeResponse> = client
        .get("episode/") {
            if (page != null)
                url {
                    parameter("page", page)
                }
        }
        .accept(HttpStatusCode.OK)
        .body()

}
