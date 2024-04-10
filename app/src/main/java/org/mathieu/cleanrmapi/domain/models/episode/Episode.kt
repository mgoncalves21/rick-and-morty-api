package org.mathieu.cleanrmapi.domain.models.episode


/**
 * Represents a detailed characterization, typically derived from a data source or API.
 *
 * @property id The unique identifier for the character.
 * @property name The name of the character.
 */
data class Episode(
    val id: Int,
    val name: String,
    val airDate: String,
    val episode: String,
    val characters: List<String>, // Liste d'URLs de personnages
    val url: String,
    val created: String
)
