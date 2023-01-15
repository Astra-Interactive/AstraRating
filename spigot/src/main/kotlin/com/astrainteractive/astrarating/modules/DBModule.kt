package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingTable
import com.astrainteractive.astrarating.domain.entities.tables.UserTable
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.di.IModule
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.Database
import java.io.File

object DBModule : IModule<Database>() {
    override fun initializer(): Database = runBlocking {
        val db = Database()
        db.openConnection("${AstraRating.instance.dataFolder}${File.separator}data.db", DBConnection.SQLite)
        UserRatingTable.create(db)
        UserTable.create(db)
        db
    }
}