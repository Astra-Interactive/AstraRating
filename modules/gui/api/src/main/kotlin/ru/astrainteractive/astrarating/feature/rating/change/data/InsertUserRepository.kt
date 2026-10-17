package ru.astrainteractive.astrarating.feature.rating.change.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.astrarating.data.dao.rating.util.upsertUser
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface InsertUserRepository {
    suspend fun upsertUser(newUser: NewUser): Result<User>
}

internal class InsertUserRepositoryImpl(
    private val dbApi: RatingRepository,
    private val dispatchers: KotlinDispatchers
) : InsertUserRepository {
    override suspend fun upsertUser(newUser: NewUser): Result<User> {
        return withContext(dispatchers.IO) {
            dbApi.upsertUser(newUser)
        }
    }
}
