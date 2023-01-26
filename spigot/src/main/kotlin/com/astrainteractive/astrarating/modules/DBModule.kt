package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.entities.tables.UserRatingTable
import com.astrainteractive.astrarating.domain.entities.tables.UserTable
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.di.IModule
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.orm.DefaultDatabase
import java.io.File

object DBModule : IModule<Database>() {
    private val syntax = DBSyntax.MySQL
    private val dbconnection = DBConnection.SQLite(
        "${AstraRating.instance.dataFolder}${File.separator}data.db"
    )
    override fun initializer(): Database = runBlocking {
        val db = DefaultDatabase(dbconnection, syntax)
        db.openConnection()
        UserRatingTable.create(db)
        UserTable.create(db)
        db
    }
}