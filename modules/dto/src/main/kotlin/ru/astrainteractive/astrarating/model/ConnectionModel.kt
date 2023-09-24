package ru.astrainteractive.astrarating.model

import kotlinx.serialization.Serializable

@Serializable
data class ConnectionModel(
    val sqlite: Boolean = true,
    val mysql: MySqlConnection? = null
) {
    @Serializable
    data class MySqlConnection(
        val database: String,
        val host: String,
        val port: Int,
        val username: String,
        val password: String

    )
}
