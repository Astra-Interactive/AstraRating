package com.astrainteractive.astrarating.plugin

import ru.astrainteractive.astralibs.filemanager.SpigotFileManager


/**
 * All plugin files such as config.yml and other should only be stored here!
 */
object Files {
    val configFile: SpigotFileManager = SpigotFileManager("config.yml")
}