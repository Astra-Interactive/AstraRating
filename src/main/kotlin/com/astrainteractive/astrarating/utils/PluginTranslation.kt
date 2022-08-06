package com.astrainteractive.astrarating.utils

import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.utils.HEX
import com.astrainteractive.astralibs.utils.getHEXString

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

    private fun getHEXString(path: String,default:String): String {
        val msg = translation.getHEXString(path)?:default.HEX()
        if (!translation.contains(path)) {
            translation.set(path, default)
            _translationFile.saveConfig()
        }
        return msg
    }

    // LightBlue #1B76CA
    // Yellow #DDB92B
    // Red #ca271b
    //Database
    val dbSuccess: String =
        getHEXString("database.success","#1B76CAУспешно подключено к базе данных")
    val dbFail: String = getHEXString("database.fail","#ca271bНет подключения к базе данных")
    val dbError: String = getHEXString("database.error","#ca271bПроизошла ошибка в базе данных")


    //General
    val prefix: String = getHEXString("general.prefix","#1B76CA[EmpireItems]")
    val wrongUsage: String = getHEXString("general.wrong_usage","#1B76CAНеверное использование команды")
    val reload: String = getHEXString("general.reload","#1B76CAПерезагрузка плагина")
    val reloadComplete: String =
        getHEXString("general.reload_complete","#1B76CAПерезагрузка успешно завершена")
    val noPermission: String =
        getHEXString("general.no_permission","#ca271bУ вас нет прав!")
    val onlyPlayerCommand:String = getHEXString("general.only_player_command","#ca271bКоманда доступна только игрокам!")
    val playerNotExists:String = getHEXString("general.player_not_exists","#ca271bТакого игрока нет!")
    val cantRateSelf:String = getHEXString("general.cant_rate_self","#ca271bВы не можете поставить рейтинг самому себe!")
    val wrongMessageLen:String = getHEXString("general.cant_rate_self","#ca271bДлина причина рейтинга должна быть в диапазоне [5;30]")
    val likedUser:String = getHEXString("general.liked_user","#1B76CAВы повысили рейтинг игрока %player%")
    val dislikedUser:String = getHEXString("general.disliked_user","#ca271bВы понизили рейтинг игрока %player%")
    val alreadyMaxDayVotes:String = getHEXString("general.max_day_voted","#ca271bВы уже проголосовали максимальное количество раз за день")
    val alreadyMaxPlayerVotes:String = getHEXString("general.max_player_voted","#ca271bСегодня вы выдали максимальное возможное количество голосов этому игроку")
    val clickToDeleteReport:String = getHEXString("general.click_to_delete_report","#ca271bНажмите ЛКМ чтобы удалить")
    val notEnoughOnServer:String = getHEXString("general.not_enough_on_server","#ca271bВы недостаточно долго были на сервере")
    val notEnoughOnDiscord:String = getHEXString("general.not_enough_on_discord","#ca271bВы недостаточно долго были на дискорд-сервере")
    val needDiscordLinked:String = getHEXString("general.need_discord_link","#ca271bВы должны привязать дискорд /discord link")
    val playerNameColor:String = getHEXString("general.player_name_color","#DDB92B")
    val positiveColor:String = getHEXString("general.positive_color","#1B76CA")
    val negativeColor:String = getHEXString("general.negative_color","#ca271b")
    val firstConnection:String = getHEXString("general.first_connection","#1B76CAВпервые зашёл:")
    val lastConnection:String = getHEXString("general.last_connection","#1B76CAБыл в сети:")
    //Menu
    val ratingsTitle: String = getHEXString("menu.ratings_title","#1B76CAРейтинг")
    val playerRatingTitle: String = getHEXString("menu.player_rating_title","#1B76CAРейтинг игрока %player%")
    val menuPrevPage: String = getHEXString("menu.prev_page","#1B76CAПред. страницы")
    val menuNextPage: String = getHEXString("menu.next_page","#1B76CAСлед. страница")
    val menuBack: String = getHEXString("menu.back","#1B76CAНазад")
    val menuClose: String = getHEXString("menu.close","#1B76CAЗакрыть")
    val sortPlayerASC: String = getHEXString("menu.sort.PLAYER_ASC","#1B76CAИгрок по возрастанию")
    val sortPlayerDESC: String = getHEXString("menu.sort.PLAYER_DESC","#1B76CAИгрок по убыванию")
    val sortDateASC: String = getHEXString("menu.sort.DATE_DESC","#1B76CAЗДата по убыванию")
    val sortDateDESC: String = getHEXString("menu.sort.DATE_ASC","#1B76CAДата по возрастанию")
    val sortRatingDESC: String = getHEXString("menu.sort.RATING_DESC","#1B76CAСначала положительные")
    val sortRatingASC: String = getHEXString("menu.sort.RATING_ASC","#1B76CAСначала отрицательные")
    val sortASC: String = getHEXString("menu.sort.ASC","#1B76CAПо возрастанию")
    val sortDESC: String = getHEXString("menu.sort.DESC","#1B76CAПо убыванию")
    val sortRating: String = getHEXString("menu.sort.rating_sort","#1B76CAСортировка")
    val sort: String = getHEXString("menu.sort.sort","#1B76CAСортировка")
    val rating: String = getHEXString("menu.rating","#1B76CAРейтинг")
    val message: String = getHEXString("menu.message","#1B76CAСообщение:")

}