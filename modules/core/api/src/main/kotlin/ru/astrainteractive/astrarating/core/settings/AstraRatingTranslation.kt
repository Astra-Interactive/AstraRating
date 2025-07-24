@file:Suppress("MaximumLineLength", "MaxLineLength", "LongParameterList")

package ru.astrainteractive.astrarating.core.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.plus
import ru.astrainteractive.astralibs.string.replace
import ru.astrainteractive.astralibs.string.toRaw

@Serializable
class AstraRatingTranslation(
    @SerialName("general.unknown_error")
    val unknownError: StringDesc.Raw = StringDesc.Raw("&4Неизвестная ошибка"),
    @SerialName("general.prefix")
    val prefix: StringDesc.Raw = StringDesc.Raw("&6[AR] "),
    @SerialName("general.wrong_usage")
    val wrongUsage: StringDesc.Raw = prefix
        .plus("&3Неверное использование команды")
        .toRaw(),
    @SerialName("general.reload")
    val reload: StringDesc.Raw = prefix
        .plus("&3Перезагрузка плагина")
        .toRaw(),
    @SerialName("general.reload_complete")
    val reloadComplete: StringDesc.Raw = prefix
        .plus("&3Перезагрузка успешно завершена")
        .toRaw(),
    @SerialName("general.no_permission")
    val noPermission: StringDesc.Raw = prefix
        .plus("&4У вас нет прав!")
        .toRaw(),
    @SerialName("general.only_player_command")
    val onlyPlayerCommand: StringDesc.Raw = prefix
        .plus("&4Команда доступна только игрокам!")
        .toRaw(),
    @SerialName("general.player_not_exists")
    val playerNotExists: StringDesc.Raw = prefix
        .plus("&4Такого игрока нет!")
        .toRaw(),
    @SerialName("general.cant_rate_self")
    val cantRateSelf: StringDesc.Raw = prefix
        .plus("&4Вы не можете поставить рейтинг самому себe!")
        .toRaw(),
    @SerialName("general.wrong_message_len")
    val wrongMessageLen: StringDesc.Raw = prefix
        .plus("&4Длина причина рейтинга должна быть в диапазоне [5;30]")
        .toRaw(),
    @SerialName("general.liked_user")
    private val likedUser: StringDesc.Raw = prefix
        .plus("&3Вы повысили рейтинг игрока %player%")
        .toRaw(),
    @SerialName("general.disliked_user")
    private val dislikedUser: StringDesc.Raw = prefix
        .plus("&4Вы понизили рейтинг игрока %player%")
        .toRaw(),
    @SerialName("general.max_day_voted")
    val alreadyMaxDayVotes: StringDesc.Raw = prefix
        .plus("&4Вы уже проголосовали максимальное количество раз за день")
        .toRaw(),
    @SerialName("general.max_player_voted")
    val alreadyMaxPlayerVotes: StringDesc.Raw = prefix
        .plus("&4Сегодня вы выдали максимальное возможное количество голосов этому игроку")
        .toRaw(),
    @SerialName("general.click_to_delete_report")
    val clickToDeleteReport: StringDesc.Raw = StringDesc.Raw("&4Нажмите ЛКМ чтобы удалить"),
    @SerialName("general.not_enough_on_server")
    val notEnoughOnServer: StringDesc.Raw = prefix
        .plus("&4Вы недостаточно долго были на сервере")
        .toRaw(),
    @SerialName("general.player_name_color")
    val playerNameColor: StringDesc.Raw = StringDesc.Raw("&#DDB92B"),
    @SerialName("general.positive_color")
    val positiveColor: StringDesc.Raw = StringDesc.Raw("&3"),
    @SerialName("general.negative_color")
    val negativeColor: StringDesc.Raw = StringDesc.Raw("&4"),
    @SerialName("general.first_connection")
    val firstConnection: StringDesc.Raw = StringDesc.Raw("&3Впервые зашёл:"),
    @SerialName("general.last_connection")
    val lastConnection: StringDesc.Raw = StringDesc.Raw("&3Был в сети:"),
    @SerialName("menu.ratings_title")
    val ratingsTitle: StringDesc.Raw = StringDesc.Raw("&3Рейтинг"),
    @SerialName("menu.player_rating_title")
    val playerRatingTitle: StringDesc.Raw = StringDesc.Raw("&3Рейтинг игрока %player%"),
    @SerialName("menu.prev_page")
    val menuPrevPage: StringDesc.Raw = StringDesc.Raw("&3Пред. страницы"),
    @SerialName("menu.next_page")
    val menuNextPage: StringDesc.Raw = StringDesc.Raw("&3След. страница"),
    @SerialName("menu.close")
    val menuClose: StringDesc.Raw = StringDesc.Raw("&3Закрыть"),
    @SerialName("menu.sort.PLAYER_ASC")
    val sortPlayerASC: StringDesc.Raw = StringDesc.Raw("&3Игрок по возрастанию"),
    @SerialName("menu.sort.PLAYER_DESC")
    val sortPlayerDESC: StringDesc.Raw = StringDesc.Raw("&3Игрок по убыванию"),
    @SerialName("menu.sort.DATE_DESC")
    val sortDateASC: StringDesc.Raw = StringDesc.Raw("&3ЗДата по убыванию"),
    @SerialName("menu.sort.DATE_ASC")
    val sortDateDESC: StringDesc.Raw = StringDesc.Raw("&3Дата по возрастанию"),
    @SerialName("menu.sort.RATING_DESC")
    val sortRatingDESC: StringDesc.Raw = StringDesc.Raw("&3Сначала положительные"),
    @SerialName("menu.sort.RATING_ASC")
    val sortRatingASC: StringDesc.Raw = StringDesc.Raw("&3Сначала отрицательные"),
    @SerialName("menu.sort.ASC")
    val sortASC: StringDesc.Raw = StringDesc.Raw("&3По возрастанию"),
    @SerialName("menu.sort.DESC")
    val sortDESC: StringDesc.Raw = StringDesc.Raw("&3По убыванию"),
    @SerialName("menu.sort.rating_sort")
    val sortRating: StringDesc.Raw = StringDesc.Raw("&3Сортировка"),
    @SerialName("menu.sort.sort")
    val sort: StringDesc.Raw = StringDesc.Raw("&3Сортировка"),
    @SerialName("menu.rating")
    val ratingTotal: StringDesc.Raw = StringDesc.Raw("&3Рейтинг"),
    @SerialName("menu.rating_counts")
    val ratingCounts: StringDesc.Raw = StringDesc.Raw("&3Рейтингов"),
    @SerialName("menu.message")
    val message: StringDesc.Raw = StringDesc.Raw("&3Сообщение:"),
    @SerialName("menu.loading")
    val loading: StringDesc.Raw = StringDesc.Raw("&3Загрузка..."),
    @SerialName("events.title")
    val eventsTitle: StringDesc.Raw = StringDesc.Raw("&3Остальное"),
    @SerialName("events.kill.amount")
    private val eventKillAmount: StringDesc.Raw = StringDesc.Raw("&7Количество убийств: %kills%"),
    @SerialName("events.kill_player")
    private val killedPlayer: StringDesc.Raw = StringDesc.Raw("&7Убил игрока %killed_player%"),
    @SerialName("events.you_killed_player")
    private val youKilledPlayer: StringDesc.Raw = prefix
        .plus("&7Вы убили игрока %killed_player%, ваш рейтинг был понижен")
        .toRaw()
) {
    fun killedPlayer(playerName: String) = killedPlayer.replace("%killed_player%", playerName)
    fun youKilledPlayer(playerName: String) = youKilledPlayer.replace("%killed_player%", playerName)
    fun likedUser(playerName: String) = likedUser.replace("%player%", playerName)
    fun dislikedUser(playerName: String) = dislikedUser.replace("%player%", playerName)
    fun eventKillAmount(count: Int) = eventKillAmount.replace("%kills%", count.toString())
}
