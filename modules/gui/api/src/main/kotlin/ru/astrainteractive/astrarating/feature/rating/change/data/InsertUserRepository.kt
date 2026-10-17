package ru.astrainteractive.astrarating.feature.rating.change.data

import kotlinx.coroutines.withContext
import ru.astrainteractive.astrarating.data.dao.rating.api.RatingRepository
import ru.astrainteractive.astrarating.data.dao.rating.model.NewUser
import ru.astrainteractive.astrarating.data.dao.rating.model.User
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

internal interface InsertUserRepository {
    suspend fun selectUser(uuid: UUID): Result<User>
    suspend fun insertUser(newUser: NewUser): Result<Long>
}

internal class InsertUserRepositoryImpl(
    private val dbApi: RatingRepository,
    private val dispatchers: KotlinDispatchers
) : InsertUserRepository {
    override suspend fun selectUser(uuid: UUID): Result<User> {
        return withContext(dispatchers.IO) {
            dbApi.selectUser(uuid)
        }
    }

    override suspend fun insertUser(newUser: NewUser): Result<Long> {
        return withContext(dispatchers.IO) {
            dbApi.insertUser(newUser)
        }
    }
}
