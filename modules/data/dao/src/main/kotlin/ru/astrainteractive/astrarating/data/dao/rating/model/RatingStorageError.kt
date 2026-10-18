package ru.astrainteractive.astrarating.data.dao.rating.model

sealed class RatingStorageError(message: String, cause: Throwable?) : Exception(message, cause) {
    class DatabaseUnavailable : RatingStorageError("The rating database is not connected", null)

    class QueryFailed(cause: Throwable) : RatingStorageError("A rating database query failed", cause)
}
