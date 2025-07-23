@file:Suppress("MaximumLineLength", "MaxLineLength", "LongParameterList")

package ru.astrainteractive.astrarating.core.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.replace

@Serializable
class AstraRatingTranslation(
    @SerialName("database.success")
    val dbSuccess: StringDesc.Raw = StringDesc.Raw("&#1B76CAУспешно подключено к базе данных"),
    @SerialName("database.fail")
    val dbFail: StringDesc.Raw = StringDesc.Raw("&#ca271bНет подключения к базе данных"),
    @SerialName("database.error")
    val dbError: StringDesc.Raw = StringDesc.Raw("&#ca271bПроизошла ошибка в базе данных"),
    @SerialName("general.unknown_error")
    val unknownError: StringDesc.Raw = StringDesc.Raw("&#ca271bНеизвестная ошибка"),
    @SerialName("general.prefix")
    val prefix: StringDesc.Raw = StringDesc.Raw("&#1B76CA[EmpireItems]"),
    @SerialName("general.wrong_usage")
    val wrongUsage: StringDesc.Raw = StringDesc.Raw("&#1B76CAНеверное использование команды"),
    @SerialName("general.reload")
    val reload: StringDesc.Raw = StringDesc.Raw("&#1B76CAПерезагрузка плагина"),
    @SerialName("general.reload_complete")
    val reloadComplete: StringDesc.Raw = StringDesc.Raw("&#1B76CAПерезагрузка успешно завершена"),
    @SerialName("general.no_permission")
    val noPermission: StringDesc.Raw = StringDesc.Raw("&#ca271bУ вас нет прав!"),
    @SerialName("general.only_player_command")
    val onlyPlayerCommand: StringDesc.Raw = StringDesc.Raw("&#ca271bКоманда доступна только игрокам!"),
    @SerialName("general.player_not_exists")
    val playerNotExists: StringDesc.Raw = StringDesc.Raw("&#ca271bТакого игрока нет!"),
    @SerialName("general.cant_rate_self")
    val cantRateSelf: StringDesc.Raw = StringDesc.Raw("&#ca271bВы не можете поставить рейтинг самому себe!"),
    @SerialName("general.wrong_message_len")
    val wrongMessageLen: StringDesc.Raw = StringDesc.Raw(
        "&#ca271bДлина причина рейтинга должна быть в диапазоне [5;30]"
    ),
    @SerialName("general.liked_user")
    private val likedUser: StringDesc.Raw = StringDesc.Raw("&#1B76CAВы повысили рейтинг игрока %player%"),
    @SerialName("general.please_wait")
    val pleaseWait: StringDesc.Raw = StringDesc.Raw("&#1B76CAПожалуйста), подождите..."),
    @SerialName("general.disliked_user")
    private val dislikedUser: StringDesc.Raw = StringDesc.Raw("&#ca271bВы понизили рейтинг игрока %player%"),
    @SerialName("general.max_day_voted")
    val alreadyMaxDayVotes: StringDesc.Raw = StringDesc.Raw(
        "&#ca271bВы уже проголосовали максимальное количество раз за день"
    ),
    @SerialName("general.max_player_voted")
    val alreadyMaxPlayerVotes: StringDesc.Raw = StringDesc.Raw(
        "&#ca271bСегодня вы выдали максимальное возможное количество голосов этому игроку"
    ),
    @SerialName("general.click_to_delete_report")
    val clickToDeleteReport: StringDesc.Raw = StringDesc.Raw("&#ca271bНажмите ЛКМ чтобы удалить"),
    @SerialName("general.not_enough_on_server")
    val notEnoughOnServer: StringDesc.Raw = StringDesc.Raw("&#ca271bВы недостаточно долго были на сервере"),
    @SerialName("general.not_enough_on_discord")
    val notEnoughOnDiscord: StringDesc.Raw = StringDesc.Raw("&#ca271bВы недостаточно долго были на дискорд-сервере"),
    @SerialName("general.need_discord_link")
    val needDiscordLinked: StringDesc.Raw = StringDesc.Raw("&#ca271bВы должны привязать дискорд /discord link"),
    @SerialName("general.player_name_color")
    val playerNameColor: StringDesc.Raw = StringDesc.Raw("&#DDB92B"),
    @SerialName("general.positive_color")
    val positiveColor: StringDesc.Raw = StringDesc.Raw("&#1B76CA"),
    @SerialName("general.negative_color")
    val negativeColor: StringDesc.Raw = StringDesc.Raw("&#ca271b"),
    @SerialName("general.first_connection")
    val firstConnection: StringDesc.Raw = StringDesc.Raw("&#1B76CAВпервые зашёл:"),
    @SerialName("general.last_connection")
    val lastConnection: StringDesc.Raw = StringDesc.Raw("&#1B76CAБыл в сети:"),
    @SerialName("menu.ratings_title")
    val ratingsTitle: StringDesc.Raw = StringDesc.Raw("&#1B76CAРейтинг"),
    @SerialName("menu.player_rating_title")
    val playerRatingTitle: StringDesc.Raw = StringDesc.Raw("&#1B76CAРейтинг игрока %player%"),
    @SerialName("menu.prev_page")
    val menuPrevPage: StringDesc.Raw = StringDesc.Raw("&#1B76CAПред. страницы"),
    @SerialName("menu.next_page")
    val menuNextPage: StringDesc.Raw = StringDesc.Raw("&#1B76CAСлед. страница"),
    @SerialName("menu.back")
    val menuBack: StringDesc.Raw = StringDesc.Raw("&#1B76CAНазад"),
    @SerialName("menu.close")
    val menuClose: StringDesc.Raw = StringDesc.Raw("&#1B76CAЗакрыть"),
    @SerialName("menu.sort.PLAYER_ASC")
    val sortPlayerASC: StringDesc.Raw = StringDesc.Raw("&#1B76CAИгрок по возрастанию"),
    @SerialName("menu.sort.PLAYER_DESC")
    val sortPlayerDESC: StringDesc.Raw = StringDesc.Raw("&#1B76CAИгрок по убыванию"),
    @SerialName("menu.sort.DATE_DESC")
    val sortDateASC: StringDesc.Raw = StringDesc.Raw("&#1B76CAЗДата по убыванию"),
    @SerialName("menu.sort.DATE_ASC")
    val sortDateDESC: StringDesc.Raw = StringDesc.Raw("&#1B76CAДата по возрастанию"),
    @SerialName("menu.sort.RATING_DESC")
    val sortRatingDESC: StringDesc.Raw = StringDesc.Raw("&#1B76CAСначала положительные"),
    @SerialName("menu.sort.RATING_ASC")
    val sortRatingASC: StringDesc.Raw = StringDesc.Raw("&#1B76CAСначала отрицательные"),
    @SerialName("menu.sort.ASC")
    val sortASC: StringDesc.Raw = StringDesc.Raw("&#1B76CAПо возрастанию"),
    @SerialName("menu.sort.DESC")
    val sortDESC: StringDesc.Raw = StringDesc.Raw("&#1B76CAПо убыванию"),
    @SerialName("menu.sort.rating_sort")
    val sortRating: StringDesc.Raw = StringDesc.Raw("&#1B76CAСортировка"),
    @SerialName("menu.sort.sort")
    val sort: StringDesc.Raw = StringDesc.Raw("&#1B76CAСортировка"),
    @SerialName("menu.rating")
    val ratingTotal: StringDesc.Raw = StringDesc.Raw("&#1B76CAРейтинг"),
    @SerialName("menu.rating_counts")
    val ratingCounts: StringDesc.Raw = StringDesc.Raw("&#1B76CAРейтингов"),
    @SerialName("menu.message")
    val message: StringDesc.Raw = StringDesc.Raw("&#1B76CAСообщение:"),
    @SerialName("menu.loading")
    val loading: StringDesc.Raw = StringDesc.Raw("&#1B76CAЗагрузка..."),
    @SerialName("events.title")
    val eventsTitle: StringDesc.Raw = StringDesc.Raw("&#1B76CAОстальное"),
    @SerialName("events.kill.amount")
    private val eventKillAmount: StringDesc.Raw = StringDesc.Raw("&#9c0303Количество убийств: %kills%"),
    @SerialName("events.kill_player")
    private val killedPlayer: StringDesc.Raw = StringDesc.Raw("&#9c0303Убил игрока %killed_player%"),
    @SerialName("events.you_killed_player")
    private val youKilledPlayer: StringDesc.Raw = StringDesc.Raw(
        "&#9c0303Вы убили игрока %killed_player%, ваш рейтинг был понижен"
    )
) {
    fun killedPlayer(playerName: String) = killedPlayer.replace("%killed_player%", playerName)
    fun youKilledPlayer(playerName: String) = youKilledPlayer.replace("%killed_player%", playerName)
    fun likedUser(playerName: String) = likedUser.replace("%player%", playerName)
    fun dislikedUser(playerName: String) = dislikedUser.replace("%player%", playerName)
    fun eventKillAmount(count: Int) = eventKillAmount.replace("%kills%", count.toString())
}
