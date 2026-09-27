@file:Suppress("MaximumLineLength", "MaxLineLength", "LongParameterList")

package ru.astrainteractive.astrarating.core.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.component.replace
import ru.astrainteractive.astralibs.localization.locale.MinecraftLocales
import ru.astrainteractive.astralibs.localization.text.LocalizedText

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
        val unknownError: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&4Неизвестная ошибка")
            translation(MinecraftLocales.EN_US, "&4Unknown error")
        },
        @SerialName("wrong_usage")
        val wrongUsage: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&3Неверное использование команды")
                translation(MinecraftLocales.EN_US, "&3Wrong command usage")
            }
        ),
        @SerialName("reload")
        val reload: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&3Перезагрузка плагина")
                translation(MinecraftLocales.EN_US, "&3Reloading the plugin")
            }
        ),
        @SerialName("reload_complete")
        val reloadComplete: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&3Перезагрузка успешно завершена")
                translation(MinecraftLocales.EN_US, "&3Reload complete")
            }
        ),
        @SerialName("no_permission")
        val noPermission: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&4У вас нет прав!")
                translation(MinecraftLocales.EN_US, "&4You don't have permission!")
            }
        ),
        @SerialName("only_player_command")
        val onlyPlayerCommand: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&4Команда доступна только игрокам!")
                translation(MinecraftLocales.EN_US, "&4Only players can use this command!")
            }
        ),
        @SerialName("player_not_exist")
        val playerNotExist: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&4Такого игрока нет!")
                translation(MinecraftLocales.EN_US, "&4There is no such player!")
            }
        ),
    )

    @Serializable
    data class Messages(
        @SerialName("cant_rate_self")
        val cantRateSelf: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&4Вы не можете поставить рейтинг самому себe!")
                translation(MinecraftLocales.EN_US, "&4You can't rate yourself!")
            }
        ),
        @SerialName("wrong_message_len")
        val wrongMessageLen: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&4Длина причина рейтинга должна быть в диапазоне [5;30]")
                translation(MinecraftLocales.EN_US, "&4The rating reason must be [5;30] characters long")
            }
        ),
        @SerialName("liked_user")
        private val likedUser: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&3Вы повысили рейтинг игрока %player%")
                translation(MinecraftLocales.EN_US, "&3You raised the rating of %player%")
            }
        ),
        @SerialName("disliked_user")
        private val dislikedUser: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&4Вы понизили рейтинг игрока %player%")
                translation(MinecraftLocales.EN_US, "&4You lowered the rating of %player%")
            }
        ),
        @SerialName("already_max_day_voted")
        val alreadyMaxDayVotes: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&4Вы уже проголосовали максимальное количество раз за день")
                translation(MinecraftLocales.EN_US, "&4You have already voted the maximum number of times today")
            }
        ),
        @SerialName("already_max_player_voted")
        val alreadyMaxPlayerVoted: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.RU_RU,
                    "&4Сегодня вы выдали максимальное возможное количество голосов этому игроку"
                )
                translation(
                    MinecraftLocales.EN_US,
                    "&4You have already given this player the maximum number of votes today"
                )
            }
        ),
        @SerialName("not_enough_on_server")
        val notEnoughOnServer: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&4Вы недостаточно долго были на сервере")
                translation(MinecraftLocales.EN_US, "&4You haven't played on the server long enough")
            }
        ),
        @SerialName("you_killed_player")
        private val youKilledPlayer: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&7Вы убили игрока %killed_player%, ваш рейтинг был понижен")
                translation(MinecraftLocales.EN_US, "&7You killed %killed_player%, your rating was lowered")
            }
        )
    ) {
        fun likedUser(playerName: String): LocalizableComponent = likedUser.replace("%player%", playerName)

        fun dislikedUser(playerName: String): LocalizableComponent = dislikedUser.replace("%player%", playerName)

        fun youKilledPlayer(playerName: String): LocalizableComponent {
            return youKilledPlayer.replace("%killed_player%", playerName)
        }
    }

    @Serializable
    data class Gui(
        @SerialName("click_to_delete_report")
        val clickToDeleteReport: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&4Нажмите ЛКМ чтобы удалить")
            translation(MinecraftLocales.EN_US, "&4Left-click to delete")
        },
        @SerialName("player_name")
        private val playerName: LocalizedText = LocalizedText.shared("&2%player%"),
        @SerialName("positive_value")
        private val positiveValue: LocalizedText = LocalizedText.shared("&2%value%"),
        @SerialName("negative_value")
        private val negativeValue: LocalizedText = LocalizedText.shared("&4%value%"),
        @SerialName("first_connection")
        private val firstConnection: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Впервые зашёл: %time%")
            translation(MinecraftLocales.EN_US, "&7First joined: %time%")
        },
        @SerialName("last_connection")
        private val lastConnection: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Был в сети: %time%")
            translation(MinecraftLocales.EN_US, "&7Last seen: %time%")
        },
        @SerialName("ratings_title")
        val ratingsTitle: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&2Рейтинг")
            translation(MinecraftLocales.EN_US, "&2Rating")
        },
        @SerialName("player_rating_title")
        private val playerRatingTitle: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&2Рейтинг игрока %player%")
            translation(MinecraftLocales.EN_US, "&2Rating of %player%")
        },
        @SerialName("prev_page")
        val menuPrevPage: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Пред. страницы")
            translation(MinecraftLocales.EN_US, "&7Previous page")
        },
        @SerialName("next_page")
        val menuNextPage: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7След. страница")
            translation(MinecraftLocales.EN_US, "&7Next page")
        },
        @SerialName("close")
        val menuClose: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Закрыть")
            translation(MinecraftLocales.EN_US, "&7Close")
        },
        @SerialName("sort.player")
        val sortPlayer: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Игроки")
            translation(MinecraftLocales.EN_US, "Players")
        },
        @SerialName("sort.date")
        val sortDate: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Дата")
            translation(MinecraftLocales.EN_US, "Date")
        },
        @SerialName("sort.rating")
        val sortRating: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "Рейтинг")
            translation(MinecraftLocales.EN_US, "Rating")
        },
        @SerialName("sort.sort")
        val sort: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Сортировка")
            translation(MinecraftLocales.EN_US, "&7Sort")
        },
        @SerialName("sort.option")
        private val sortOption: LocalizedText = LocalizedText.shared("&f%sort%"),
        @SerialName("sort.selected_asc")
        private val sortSelectedAsc: LocalizedText = LocalizedText.shared("&6%sort% &6&l↓"),
        @SerialName("sort.selected_desc")
        private val sortSelectedDesc: LocalizedText = LocalizedText.shared("&6%sort% &6&l↑"),
        @SerialName("rating")
        private val ratingTotal: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Рейтинг: %rating%")
            translation(MinecraftLocales.EN_US, "&7Rating: %rating%")
        },
        @SerialName("rating_counts")
        private val ratingCounts: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Рейтингов: %count%")
            translation(MinecraftLocales.EN_US, "&7Ratings: %count%")
        },
        @SerialName("message")
        private val message: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Сообщение: %message%")
            translation(MinecraftLocales.EN_US, "&7Message: %message%")
        },
        @SerialName("loading")
        val loading: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Загрузка...")
            translation(MinecraftLocales.EN_US, "&7Loading...")
        },
        @SerialName("title")
        val eventsTitle: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Остальное")
            translation(MinecraftLocales.EN_US, "&7Other")
        },
        @SerialName("kill.amount")
        private val eventKillAmount: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Количество убийств: &2%kills%")
            translation(MinecraftLocales.EN_US, "&7Kills: &2%kills%")
        },
        @SerialName("kill_player")
        private val killedPlayer: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&7Убил игрока &2%killed_player%")
            translation(MinecraftLocales.EN_US, "&7Killed &2%killed_player%")
        },
    ) {
        fun playerName(name: String): LocalizableComponent = playerName.replace("%player%", name)

        /** Colors [text] by the sign of the rating it belongs to. */
        fun ratingValue(isPositive: Boolean, text: String): LocalizableComponent {
            val template = if (isPositive) positiveValue else negativeValue
            return template.replace("%value%", text)
        }

        fun firstConnection(time: String): LocalizableComponent = firstConnection.replace("%time%", time)

        fun lastConnection(time: String): LocalizableComponent = lastConnection.replace("%time%", time)

        fun playerRatingTitle(playerName: String): LocalizableComponent {
            return playerRatingTitle.replace("%player%", playerName)
        }

        /** A line of the sort button: only the selected [sort] shows its direction. */
        fun sortOption(sort: LocalizableComponent, isSelected: Boolean, isAscending: Boolean): LocalizableComponent {
            val template = when {
                !isSelected -> sortOption
                isAscending -> sortSelectedAsc
                else -> sortSelectedDesc
            }
            return template.replace("%sort%", sort)
        }

        fun ratingTotal(rating: LocalizableComponent): LocalizableComponent = ratingTotal.replace("%rating%", rating)

        fun ratingCounts(count: Long): LocalizableComponent = ratingCounts.replace("%count%", count.toString())

        fun message(text: LocalizableComponent): LocalizableComponent = message.replace("%message%", text)

        fun eventKillAmount(count: Int): LocalizableComponent = eventKillAmount.replace("%kills%", count.toString())

        fun killedPlayer(playerName: String): LocalizableComponent {
            return killedPlayer.replace("%killed_player%", playerName)
        }
    }

    companion object {
        private val PREFIX = LocalizedText.shared("&6[AR] ")
    }
}
