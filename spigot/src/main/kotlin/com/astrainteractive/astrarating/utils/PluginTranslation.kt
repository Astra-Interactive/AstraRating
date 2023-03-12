package com.astrainteractive.astrarating.utils

import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astralibs.utils.BaseTranslation

/**
 * All translation stored here
 */
class PluginTranslation : BaseTranslation() {
    /**
     * This is a default translation file. Don't forget to create translation.yml in resources of the plugin
     */
    override val translationFile: FileManager = FileManager("translations.yml")

    // LightBlue #1B76CA
    // Yellow #DDB92B
    // Red #ca271b
    //Database
    val dbSuccess: String = translationValue("database.success", "#1B76CAУспешно подключено к базе данных")
    val dbFail: String = translationValue("database.fail", "#ca271bНет подключения к базе данных")
    val dbError: String = translationValue("database.error", "#ca271bПроизошла ошибка в базе данных")


    //General
    val prefix: String = translationValue("general.prefix", "#1B76CA[EmpireItems]")
    val wrongUsage: String = translationValue("general.wrong_usage", "#1B76CAНеверное использование команды")
    val reload: String = translationValue("general.reload", "#1B76CAПерезагрузка плагина")
    val reloadComplete: String = translationValue("general.reload_complete", "#1B76CAПерезагрузка успешно завершена")
    val noPermission: String = translationValue("general.no_permission", "#ca271bУ вас нет прав!")
    val onlyPlayerCommand: String =
        translationValue("general.only_player_command", "#ca271bКоманда доступна только игрокам!")
    val playerNotExists: String = translationValue("general.player_not_exists", "#ca271bТакого игрока нет!")
    val cantRateSelf: String =
        translationValue("general.cant_rate_self", "#ca271bВы не можете поставить рейтинг самому себe!")
    val wrongMessageLen: String =
        translationValue("general.wrong_message_len", "#ca271bДлина причина рейтинга должна быть в диапазоне [5;30]")
    val likedUser: String = translationValue("general.liked_user", "#1B76CAВы повысили рейтинг игрока %player%")
    val dislikedUser: String = translationValue("general.disliked_user", "#ca271bВы понизили рейтинг игрока %player%")
    val alreadyMaxDayVotes: String =
        translationValue("general.max_day_voted", "#ca271bВы уже проголосовали максимальное количество раз за день")
    val alreadyMaxPlayerVotes: String = translationValue(
        "general.max_player_voted", "#ca271bСегодня вы выдали максимальное возможное количество голосов этому игроку"
    )
    val clickToDeleteReport: String =
        translationValue("general.click_to_delete_report", "#ca271bНажмите ЛКМ чтобы удалить")
    val notEnoughOnServer: String =
        translationValue("general.not_enough_on_server", "#ca271bВы недостаточно долго были на сервере")
    val notEnoughOnDiscord: String =
        translationValue("general.not_enough_on_discord", "#ca271bВы недостаточно долго были на дискорд-сервере")
    val needDiscordLinked: String =
        translationValue("general.need_discord_link", "#ca271bВы должны привязать дискорд /discord link")
    val playerNameColor: String = translationValue("general.player_name_color", "#DDB92B")
    val positiveColor: String = translationValue("general.positive_color", "#1B76CA")
    val negativeColor: String = translationValue("general.negative_color", "#ca271b")
    val firstConnection: String = translationValue("general.first_connection", "#1B76CAВпервые зашёл:")
    val lastConnection: String = translationValue("general.last_connection", "#1B76CAБыл в сети:")

    //Menu
    val ratingsTitle: String = translationValue("menu.ratings_title", "#1B76CAРейтинг")
    val playerRatingTitle: String = translationValue("menu.player_rating_title", "#1B76CAРейтинг игрока %player%")
    val menuPrevPage: String = translationValue("menu.prev_page", "#1B76CAПред. страницы")
    val menuNextPage: String = translationValue("menu.next_page", "#1B76CAСлед. страница")
    val menuBack: String = translationValue("menu.back", "#1B76CAНазад")
    val menuClose: String = translationValue("menu.close", "#1B76CAЗакрыть")
    val sortPlayerASC: String = translationValue("menu.sort.PLAYER_ASC", "#1B76CAИгрок по возрастанию")
    val sortPlayerDESC: String = translationValue("menu.sort.PLAYER_DESC", "#1B76CAИгрок по убыванию")
    val sortDateASC: String = translationValue("menu.sort.DATE_DESC", "#1B76CAЗДата по убыванию")
    val sortDateDESC: String = translationValue("menu.sort.DATE_ASC", "#1B76CAДата по возрастанию")
    val sortRatingDESC: String = translationValue("menu.sort.RATING_DESC", "#1B76CAСначала положительные")
    val sortRatingASC: String = translationValue("menu.sort.RATING_ASC", "#1B76CAСначала отрицательные")
    val sortASC: String = translationValue("menu.sort.ASC", "#1B76CAПо возрастанию")
    val sortDESC: String = translationValue("menu.sort.DESC", "#1B76CAПо убыванию")
    val sortRating: String = translationValue("menu.sort.rating_sort", "#1B76CAСортировка")
    val sort: String = translationValue("menu.sort.sort", "#1B76CAСортировка")
    val rating: String = translationValue("menu.rating", "#1B76CAРейтинг")
    val message: String = translationValue("menu.message", "#1B76CAСообщение:")

    // Events
    private val killedPlayer = translationValue("events.kill_player", "#9c0303Убил игрока %killed_player%")
    fun killedPlayer(playerName: String) = killedPlayer.replace("%killed_player%", playerName)

}