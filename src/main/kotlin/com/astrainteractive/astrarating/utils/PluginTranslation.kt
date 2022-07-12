package com.astrainteractive.astrarating.utils

import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.getHEXString

val Translation: PluginTranslation
    get() = PluginTranslation.instance

/**
 * All translation stored here
 */
class PluginTranslation {
    /**
     * For better access better to create [instance]
     */
    companion object {
        internal lateinit var instance: PluginTranslation
    }
    init {
        instance = this
    }

    /**
     * This is a default translation file. Don't forget to create translation.yml in resources of the plugin
     */
    private val _translationFile: FileManager = FileManager("translations.yml")
    private val translation = _translationFile.getConfig()

    private fun getHEXString(path: String) = translation.getHEXString(path)
    private fun getHEXString(path: String, default: String) = translation.getHEXString(path) ?: default.HEX()

    // LightBlue #1B76CA
    // Yellow #DDB92B
    // Red #ca271b
    //Database
    val dbSuccess: String =
        getHEXString("database.success") ?: "#1B76CAУспешно подключено к базе данных".HEX()
    val dbFail: String = getHEXString("database.fail") ?: "#ca271bНет подключения к базе данных".HEX()
    val dbError: String = getHEXString("database.error") ?: "#ca271bПроизошла ошибка в базе данных".HEX()


    //General
    val prefix: String = getHEXString("general.prefix") ?: "#1B76CA[EmpireItems]".HEX()
    val wrongUsage: String = getHEXString("general.wrong_usage") ?: "#1B76CAНеверное использование команды".HEX()
    val reload: String = getHEXString("general.reload") ?: "#1B76CAПерезагрузка плагина".HEX()
    val reloadComplete: String =
        getHEXString("general.reload_complete") ?: "#1B76CAПерезагрузка успешно завершена".HEX()
    val noPermission: String =
        getHEXString("general.no_permission") ?: "#ca271bУ вас нет прав!".HEX()
    val onlyPlayerCommand:String = getHEXString("general.only_player_command") ?: "#ca271bКоманда доступна только игрокам!".HEX()
    val playerNotExists:String = getHEXString("general.player_not_exists") ?: "#ca271bТакого игрока нет!".HEX()
    val cantRateSelf:String = getHEXString("general.cant_rate_self") ?: "#ca271bВы не можете поставить рейтинг самому себe!".HEX()
    val wrongMessageLen:String = getHEXString("general.cant_rate_self") ?: "#ca271bДлина причина рейтинга должна быть в диапазоне [5;30]".HEX()
    val likedUser:String = getHEXString("general.liked_user") ?: "#1B76CAВы повысили рейтинг игрока %player%".HEX()
    val dislikedUser:String = getHEXString("general.disliked_user") ?: "#ca271bВы понизили рейтинг игрока %player%".HEX()
    val alreadyMaxDayVotes:String = getHEXString("general.max_day_voted") ?: "#ca271bВы уже проголосовали максимальное количество раз за день".HEX()
    val alreadyMaxPlayerVotes:String = getHEXString("general.max_player_voted") ?: "#ca271bСегодня вы выдали максимальное возможное количество голосов этому игроку".HEX()
    val clickToDeleteReport:String = getHEXString("general.click_to_delete_report") ?: "#1B76CAНажмите ЛКМ чтобы удалить".HEX()
    //Menu
    val menuTitle: String = getHEXString("menu.title") ?: "#1B76CAМеню".HEX()
    val menuPrevPage: String = getHEXString("menu.prev_page") ?: "#1B76CAПред. страницы".HEX()
    val menuNextPage: String = getHEXString("menu.next_page") ?: "#1B76CAСлед. страница".HEX()
    val menuBack: String = getHEXString("menu.back") ?: "#1B76CAНазад".HEX()
    val menuClose: String = getHEXString("menu.close") ?: "#1B76CAЗакрыть".HEX()
    val sortPlayerASC: String = getHEXString("menu.sort.PLAYER_ASC") ?: "#1B76CAИгрок по возрастанию".HEX()
    val sortPlayerDESC: String = getHEXString("menu.sort.PLAYER_DESC") ?: "#1B76CAИгрок по убыванию".HEX()
    val sortDateASC: String = getHEXString("menu.sort.DATE_DESC") ?: "#1B76CAЗДата по убыванию".HEX()
    val sortDateDESC: String = getHEXString("menu.sort.DATE_ASC") ?: "#1B76CAДата по возрастанию".HEX()
    val sortRatingDESC: String = getHEXString("menu.sort.RATING_DESC") ?: "#1B76CAСначала положительные".HEX()
    val sortRatingASC: String = getHEXString("menu.sort.RATING_ASC") ?: "#1B76CAСначала отрицательные".HEX()
    val sortASC: String = getHEXString("menu.sort.ASC") ?: "#1B76CAПо возрастанию".HEX()
    val sortDESC: String = getHEXString("menu.sort.DESC") ?: "#1B76CAПо убыванию".HEX()
    val sortRating: String = getHEXString("menu.sort.rating_sort") ?: "#1B76CAСортировка рейтинга".HEX()
    val sort: String = getHEXString("menu.sort.sort") ?: "#1B76CAСортировка".HEX()
    val rating: String = getHEXString("menu.sort") ?: "#1B76CAРейтинг".HEX()
    val message: String = getHEXString("menu.message") ?: "#1B76CAСообщение".HEX()

}