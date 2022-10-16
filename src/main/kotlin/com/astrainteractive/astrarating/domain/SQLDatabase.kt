package com.astrainteractive.astrarating.domain

import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.database.DatabaseCore
import ru.astrainteractive.astralibs.database.isConnected
import ru.astrainteractive.astralibs.utils.catching
import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.entities.User
import com.astrainteractive.astrarating.domain.entities.UserRating
import com.astrainteractive.astrarating.modules.TranslationProvider
import kotlinx.coroutines.*
import java.io.File
import java.sql.Connection
import java.sql.DriverManager


class SQLDatabase(filePath: String ) : DatabaseCore() {

    override val connectionBuilder: () -> Connection? = {
        catching { DriverManager.getConnection(("jdbc:sqlite:${filePath}")) }
    }

    override suspend fun onEnable() {
        if (connection.isConnected)
            Logger.log(TranslationProvider.value.dbSuccess, "Database")
        else {
            Logger.error(TranslationProvider.value.dbFail, "Database")
            return
        }
        createTable<User>()
        createTable<UserRating>()
        select<User>()?.filter { it.minecraftName != it.minecraftName.uppercase() }?.map {
            coroutineScope {
                async {
                    update(it.copy(minecraftName = it.minecraftName.uppercase()))
                }
            }
        }?.awaitAll()
    }

    companion object {
        const val NON_EXISTS_KEY = -1L
    }
}