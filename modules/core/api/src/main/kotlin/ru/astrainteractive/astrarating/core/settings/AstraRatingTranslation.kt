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
    @SerialName("general")
    val general: General = General(),
    @SerialName("messages")
    val messages: Messages = Messages(),
    @SerialName("gui")
    val gui: Gui = Gui(),
) {
    @Serializable
    data class General(
        @SerialName("unknown_error")
        val unknownError: StringDesc.Raw = StringDesc.Raw("&4Неизвестная ошибка"),
        @SerialName("wrong_usage")
        val wrongUsage: StringDesc.Raw = PREFIX
            .plus("&3Неверное использование команды")
            .toRaw(),
        @SerialName("reload")
        val reload: StringDesc.Raw = PREFIX
            .plus("&3Перезагрузка плагина")
            .toRaw(),
        @SerialName("reload_complete")
        val reloadComplete: StringDesc.Raw = PREFIX
            .plus("&3Перезагрузка успешно завершена")
            .toRaw(),
        @SerialName("no_permission")
        val noPermission: StringDesc.Raw = PREFIX
            .plus("&4У вас нет прав!")
            .toRaw(),
        @SerialName("only_player_command")
        val onlyPlayerCommand: StringDesc.Raw = PREFIX
            .plus("&4Команда доступна только игрокам!")
            .toRaw(),
        @SerialName("player_not_exist")
        val playerNotExist: StringDesc.Raw = PREFIX
            .plus("&4Такого игрока нет!")
            .toRaw(),
    )

    @Serializable
    data class Messages(
        @SerialName("cant_rate_self")
        val cantRateSelf: StringDesc.Raw = PREFIX
            .plus("&4Вы не можете поставить рейтинг самому себe!")
            .toRaw(),
        @SerialName("wrong_message_len")
        val wrongMessageLen: StringDesc.Raw = PREFIX
            .plus("&4Длина причина рейтинга должна быть в диапазоне [5;30]")
            .toRaw(),
        @SerialName("liked_user")
        private val likedUser: StringDesc.Raw = PREFIX
            .plus("&3Вы повысили рейтинг игрока %player%")
            .toRaw(),
        @SerialName("disliked_user")
        private val dislikedUser: StringDesc.Raw = PREFIX
            .plus("&4Вы понизили рейтинг игрока %player%")
            .toRaw(),
        @SerialName("already_max_day_voted")
        val alreadyMaxDayVotes: StringDesc.Raw = PREFIX
            .plus("&4Вы уже проголосовали максимальное количество раз за день")
            .toRaw(),
        @SerialName("already_max_player_voted")
        val alreadyMaxPlayerVoted: StringDesc.Raw = PREFIX
            .plus("&4Сегодня вы выдали максимальное возможное количество голосов этому игроку")
            .toRaw(),
        @SerialName("not_enough_on_server")
        val notEnoughOnServer: StringDesc.Raw = PREFIX
            .plus("&4Вы недостаточно долго были на сервере")
            .toRaw(),
        @SerialName("you_killed_player")
        private val youKilledPlayer: StringDesc.Raw = PREFIX
            .plus("&7Вы убили игрока %killed_player%, ваш рейтинг был понижен")
            .toRaw()
    ) {
        fun likedUser(playerName: String) = likedUser.replace("%player%", playerName)
        fun dislikedUser(playerName: String) = dislikedUser.replace("%player%", playerName)
        fun youKilledPlayer(playerName: String) = youKilledPlayer.replace("%killed_player%", playerName)
    }

    @Serializable
    data class Gui(
        @SerialName("click_to_delete_report")
        val clickToDeleteReport: StringDesc.Raw = StringDesc.Raw("&4Нажмите ЛКМ чтобы удалить"),
        @SerialName("player_name_color")
        val playerNameColor: StringDesc.Raw = StringDesc.Raw("&2"),
        @SerialName("positive_color")
        val positiveColor: StringDesc.Raw = StringDesc.Raw("&2"),
        @SerialName("negative_color")
        val negativeColor: StringDesc.Raw = StringDesc.Raw("&4"),
        @SerialName("first_connection")
        private val firstConnection: StringDesc.Raw = StringDesc.Raw("&7Впервые зашёл: %time%"),
        @SerialName("last_connection")
        private val lastConnection: StringDesc.Raw = StringDesc.Raw("&7Был в сети: %time%"),
        @SerialName("ratings_title")
        val ratingsTitle: StringDesc.Raw = StringDesc.Raw("&2Рейтинг"),
        @SerialName("player_rating_title")
        val playerRatingTitle: StringDesc.Raw = StringDesc.Raw("&7Рейтинг игрока &2%player%"),
        @SerialName("prev_page")
        val menuPrevPage: StringDesc.Raw = StringDesc.Raw("&7Пред. страницы"),
        @SerialName("next_page")
        val menuNextPage: StringDesc.Raw = StringDesc.Raw("&7След. страница"),
        @SerialName("close")
        val menuClose: StringDesc.Raw = StringDesc.Raw("&7Закрыть"),
        @SerialName("sort.player")
        val sortPlayer: StringDesc.Raw = StringDesc.Raw("Игроки"),
        @SerialName("sort.date")
        val sortDate: StringDesc.Raw = StringDesc.Raw("Дата"),
        @SerialName("sort.rating")
        val sortRating: StringDesc.Raw = StringDesc.Raw("Рейтинг"),
        @SerialName("sort.sort")
        val sort: StringDesc.Raw = StringDesc.Raw("&7Сортировка"),
        @SerialName("sort.asc")
        val sortAscSymbol: StringDesc.Raw = StringDesc.Raw(" &6&l↓"),
        @SerialName("sort.desc")
        val sortDescSymbol: StringDesc.Raw = StringDesc.Raw(" &6&l↑"),
        @SerialName("enabled_color")
        val enabledColor: StringDesc.Raw = StringDesc.Raw("&6"),
        @SerialName("disabled_color")
        val disabledColor: StringDesc.Raw = StringDesc.Raw("&f"),
        @SerialName("rating")
        private val ratingTotal: StringDesc.Raw = StringDesc.Raw("&7Рейтинг: %rating%"),
        @SerialName("rating_counts")
        val ratingCounts: StringDesc.Raw = StringDesc.Raw("&7Рейтингов"),
        @SerialName("message")
        private val message: StringDesc.Raw = StringDesc.Raw("&7Сообщение: %message%"),
        @SerialName("loading")
        val loading: StringDesc.Raw = StringDesc.Raw("&7Загрузка..."),
        @SerialName("title")
        val eventsTitle: StringDesc.Raw = StringDesc.Raw("&7Остальное"),
        @SerialName("kill.amount")
        private val eventKillAmount: StringDesc.Raw = StringDesc.Raw("&7Количество убийств: &2%kills%"),
        @SerialName("kill_player")
        private val killedPlayer: StringDesc.Raw = StringDesc.Raw("&7Убил игрока &2%killed_player%"),
    ) {
        fun killedPlayer(playerName: String) = killedPlayer.replace("%killed_player%", playerName)
        fun eventKillAmount(count: Int) = eventKillAmount.replace("%kills%", count.toString())
        fun message(text: String) = message.replace("%message%", text)
        fun firstConnection(time: String) = firstConnection.replace("%time%", time)
        fun lastConnection(time: String) = lastConnection.replace("%time%", time)
        fun ratingTotal(formatted: String) = ratingTotal.replace("%rating%", formatted)
    }

    companion object {
        private val PREFIX = StringDesc.Raw("&6[AR] ")
    }
}
