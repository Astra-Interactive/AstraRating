@file:Suppress("MaximumLineLength", "MaxLineLength", "LongParameterList")

package ru.astrainteractive.astrarating.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PluginTranslation(
    @SerialName("database.success")
    val dbSuccess: String = "#1B76CAУспешно подключено к базе данных",
    @SerialName("database.fail")
    val dbFail: String = "#ca271bНет подключения к базе данных",
    @SerialName("database.error")
    val dbError: String = "#ca271bПроизошла ошибка в базе данных",
    @SerialName("general.unknown_error")
    val unknownError: String = "#ca271bНеизвестная ошибка",
    @SerialName("general.prefix")
    val prefix: String = "#1B76CA[EmpireItems]",
    @SerialName("general.wrong_usage")
    val wrongUsage: String = "#1B76CAНеверное использование команды",
    @SerialName("general.reload")
    val reload: String = "#1B76CAПерезагрузка плагина",
    @SerialName("general.reload_complete")
    val reloadComplete: String = "#1B76CAПерезагрузка успешно завершена",
    @SerialName("general.no_permission")
    val noPermission: String = "#ca271bУ вас нет прав!",
    @SerialName("general.only_player_command")
    val onlyPlayerCommand: String = "#ca271bКоманда доступна только игрокам!",
    @SerialName("general.player_not_exists")
    val playerNotExists: String = "#ca271bТакого игрока нет!",
    @SerialName("general.cant_rate_self")
    val cantRateSelf: String = "#ca271bВы не можете поставить рейтинг самому себe!",
    @SerialName("general.wrong_message_len")
    val wrongMessageLen: String = "#ca271bДлина причина рейтинга должна быть в диапазоне [5;30]",
    @SerialName("general.liked_user")
    val likedUser: String = "#1B76CAВы повысили рейтинг игрока %player%",
    @SerialName("general.please_wait")
    val pleaseWait: String = "#1B76CAПожалуйста, подождите...",
    @SerialName("general.disliked_user")
    val dislikedUser: String = "#ca271bВы понизили рейтинг игрока %player%",
    @SerialName("general.max_day_voted")
    val alreadyMaxDayVotes: String = "#ca271bВы уже проголосовали максимальное количество раз за день",
    @SerialName("general.max_player_voted")
    val alreadyMaxPlayerVotes: String = "#ca271bСегодня вы выдали максимальное возможное количество голосов этому игроку",
    @SerialName("general.click_to_delete_report")
    val clickToDeleteReport: String = "#ca271bНажмите ЛКМ чтобы удалить",
    @SerialName("general.not_enough_on_server")
    val notEnoughOnServer: String = "#ca271bВы недостаточно долго были на сервере",
    @SerialName("general.not_enough_on_discord")
    val notEnoughOnDiscord: String = "#ca271bВы недостаточно долго были на дискорд-сервере",
    @SerialName("general.need_discord_link")
    val needDiscordLinked: String = "#ca271bВы должны привязать дискорд /discord link",
    @SerialName("general.player_name_color")
    val playerNameColor: String = "#DDB92B",
    @SerialName("general.positive_color")
    val positiveColor: String = "#1B76CA",
    @SerialName("general.negative_color")
    val negativeColor: String = "#ca271b",
    @SerialName("general.first_connection")
    val firstConnection: String = "#1B76CAВпервые зашёл:",
    @SerialName("general.last_connection")
    val lastConnection: String = "#1B76CAБыл в сети:",
    @SerialName("menu.ratings_title")
    val ratingsTitle: String = "#1B76CAРейтинг",
    @SerialName("menu.player_rating_title")
    val playerRatingTitle: String = "#1B76CAРейтинг игрока %player%",
    @SerialName("menu.prev_page")
    val menuPrevPage: String = "#1B76CAПред. страницы",
    @SerialName("menu.next_page")
    val menuNextPage: String = "#1B76CAСлед. страница",
    @SerialName("menu.back")
    val menuBack: String = "#1B76CAНазад",
    @SerialName("menu.close")
    val menuClose: String = "#1B76CAЗакрыть",
    @SerialName("menu.sort.PLAYER_ASC")
    val sortPlayerASC: String = "#1B76CAИгрок по возрастанию",
    @SerialName("menu.sort.PLAYER_DESC")
    val sortPlayerDESC: String = "#1B76CAИгрок по убыванию",
    @SerialName("menu.sort.DATE_DESC")
    val sortDateASC: String = "#1B76CAЗДата по убыванию",
    @SerialName("menu.sort.DATE_ASC")
    val sortDateDESC: String = "#1B76CAДата по возрастанию",
    @SerialName("menu.sort.RATING_DESC")
    val sortRatingDESC: String = "#1B76CAСначала положительные",
    @SerialName("menu.sort.RATING_ASC")
    val sortRatingASC: String = "#1B76CAСначала отрицательные",
    @SerialName("menu.sort.ASC")
    val sortASC: String = "#1B76CAПо возрастанию",
    @SerialName("menu.sort.DESC")
    val sortDESC: String = "#1B76CAПо убыванию",
    @SerialName("menu.sort.rating_sort")
    val sortRating: String = "#1B76CAСортировка",
    @SerialName("menu.sort.sort")
    val sort: String = "#1B76CAСортировка",
    @SerialName("menu.rating")
    val rating: String = "#1B76CAРейтинг",
    @SerialName("menu.message")
    val message: String = "#1B76CAСообщение:",
    @SerialName("menu.loading")
    val loading: String = "#1B76CAЗагрузка...",
    @SerialName("events.kill_player")
    private val killedPlayer: String = "#9c0303Убил игрока %killed_player%"
) {
    fun killedPlayer(playerName: String) = killedPlayer.replace("%killed_player%", playerName)
}
