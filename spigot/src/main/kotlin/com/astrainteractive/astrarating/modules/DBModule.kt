package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.SQLDatabase
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingTable
import com.astrainteractive.astrarating.domain.entities.tables.UserTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.database_v2.Database
import ru.astrainteractive.astralibs.di.IModule
import java.io.File

object DBModule : IModule<Database>() {
    override fun initializer(): Database = runBlocking {
        val db = Database()
        db.openConnection("jdbc:sqlite:${AstraRating.instance.dataFolder}${File.separator}data.db", "org.sqlite.JDBC")
        UserRatingTable.create()
        UserTable.create()
        db
    }
}