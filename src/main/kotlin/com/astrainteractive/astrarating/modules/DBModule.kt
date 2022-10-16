package com.astrainteractive.astrarating.modules

import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.domain.SQLDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.di.IModule
import java.io.File

object DBModule : IModule<SQLDatabase>() {
    override fun initializer(): SQLDatabase {
        val db = SQLDatabase("${AstraRating.instance.dataFolder}${File.separator}data.db")
        runBlocking(Dispatchers.IO) {
            db.onEnable()
        }
        return db
    }
}