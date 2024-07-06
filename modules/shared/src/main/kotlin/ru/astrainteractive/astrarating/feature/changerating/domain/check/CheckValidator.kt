package ru.astrainteractive.astrarating.feature.changerating.domain.check

internal interface CheckValidator {
    suspend fun isValid(check: Check): Boolean
}
