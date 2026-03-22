package ru.astrainteractive.astrarating.feature.rating.change.domain.check

internal interface CheckValidator {
    suspend fun isValid(check: Check): Boolean
}
