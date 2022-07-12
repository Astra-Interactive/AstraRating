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


    //Database
    val dbSuccess: String =
        getHEXString("database.success") ?: "#18dbd1Успешно подключено к базе данных".HEX()
    val dbFail: String = getHEXString("database.fail") ?: "#db2c18Нет подключения к базе данных".HEX()
    val dbError: String = getHEXString("database.error") ?: "#db2c18Произошла ошибка в базе данных".HEX()


    //General
    val prefix: String = getHEXString("general.prefix") ?: "#18dbd1[EmpireItems]".HEX()
    val wrongUsage: String = getHEXString("general.wrong_usage") ?: "#18dbd1Неверное использование команды".HEX()
    val reload: String = getHEXString("general.reload") ?: "#dbbb18Перезагрузка плагина".HEX()
    val reloadComplete: String =
        getHEXString("general.reload_complete") ?: "#42f596Перезагрузка успешно завершена".HEX()
    val noPermission: String =
        getHEXString("general.no_permission") ?: "#db2c18У вас нет прав!".HEX()
    val onlyPlayerCommand:String = getHEXString("general.only_player_command") ?: "#db2c18Команда доступна только игрокам!".HEX()
    val playerNotExists:String = getHEXString("general.player_not_exists") ?: "#db2c18Такого игрока нет!".HEX()
    val cantRateSelf:String = getHEXString("general.cant_rate_self") ?: "#db2c18Вы не можете поставить рейтинг самому себe!".HEX()
    val wrongMessageLen:String = getHEXString("general.cant_rate_self") ?: "#db2c18Длина причина рейтинга должна быть в диапазоне [5;30]".HEX()
    val likedUser:String = getHEXString("general.liked_user") ?: "#db2c18Вы повысили рейтинг игрока %player%".HEX()
    val dislikedUser:String = getHEXString("general.disliked_user") ?: "#db2c18Вы повысили рейтинг игрока %player%".HEX()
    val alreadyMaxDayVotes:String = getHEXString("general.max_day_voted") ?: "#db2c18Вы уже проголосовали максимальное количество раз за день".HEX()
    val alreadyMaxPlayerVotes:String = getHEXString("general.max_player_voted") ?: "#db2c18Сегодня вы выдали максимальное возможное количество голосов этому игроку".HEX()
    val clickToDeleteReport:String = getHEXString("general.click_to_delete_report") ?: "#db2c18Нажмите ЛКМ чтобы удалить".HEX()
    //Menu
    val menuTitle: String = getHEXString("menu.title") ?: "#18dbd1Меню".HEX()
    val menuPrevPage: String = getHEXString("menu.prev_page") ?: "#18dbd1Пред. страницы".HEX()
    val menuNextPage: String = getHEXString("menu.next_page") ?: "#18dbd1След. страница".HEX()
    val menuBack: String = getHEXString("menu.back") ?: "#18dbd1Назад".HEX()
    val menuClose: String = getHEXString("menu.close") ?: "#18dbd1Закрыть".HEX()
    val sortPlayerASC: String = getHEXString("menu.sort.PLAYER_ASC") ?: "#18dbd1Игрок по возрастанию".HEX()
    val sortPlayerDESC: String = getHEXString("menu.sort.PLAYER_DESC") ?: "#18dbd1Игрок по убыванию".HEX()
    val sortDateASC: String = getHEXString("menu.sort.DATE_DESC") ?: "#18dbd1ЗДата по убыванию".HEX()
    val sortDateDESC: String = getHEXString("menu.sort.DATE_ASC") ?: "#18dbd1Дата по возрастанию".HEX()
    val sortRatingDESC: String = getHEXString("menu.sort.RATING_DESC") ?: "#18dbd1Сначала положительные".HEX()
    val sortRatingASC: String = getHEXString("menu.sort.RATING_ASC") ?: "#18dbd1Сначала отрицательные".HEX()
    val sortASC: String = getHEXString("menu.sort.ASC") ?: "#18dbd1По возрастанию".HEX()
    val sortDESC: String = getHEXString("menu.sort.DESC") ?: "#18dbd1По убыванию".HEX()
    val sortRating: String = getHEXString("menu.sort.rating_sort") ?: "#18dbd1Сортировка рейтинга".HEX()
    val sort: String = getHEXString("menu.sort.sort") ?: "#18dbd1Сортировка".HEX()
    val rating: String = getHEXString("menu.sort") ?: "#18dbd1Рейтинг".HEX()
    val message: String = getHEXString("menu.message") ?: "#18dbd1Сообщение".HEX()

}