package com.astrainteractive.astrarating.utils

import ru.astrainteractive.astralibs.file_manager.FileManager


val Files: _Files
    get() = _Files.instance

/**
 * All plugin files such as config.yml and other should only be stored here!
 */
class _Files {
    companion object {
        lateinit var instance: _Files
    }
    init {
        instance = this
    }

    val configFile: FileManager =
        FileManager("config.yml")
}