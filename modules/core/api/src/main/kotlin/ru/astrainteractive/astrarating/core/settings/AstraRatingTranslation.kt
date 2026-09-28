@file:Suppress("MaximumLineLength", "MaxLineLength", "LongParameterList")

package ru.astrainteractive.astrarating.core.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.component.replace
import ru.astrainteractive.astralibs.localization.locale.MinecraftLocales
import ru.astrainteractive.astralibs.localization.text.LocalizedText

/** Texts of the plugin, grouped by the feature that sends them. Every text has a default. */
@Serializable
data class AstraRatingTranslation(
    @SerialName("command_error")
    val commandError: CommandError = CommandError(),
    @SerialName("reload")
    val reload: Reload = Reload(),
    @SerialName("rating_change")
    val ratingChange: RatingChange = RatingChange(),
    @SerialName("player_kill")
    val playerKill: PlayerKill = PlayerKill(),
    @SerialName("menu")
    val menu: Menu = Menu(),
    @SerialName("ratings_menu")
    val ratingsMenu: RatingsMenu = RatingsMenu(),
    @SerialName("player_ratings_menu")
    val playerRatingsMenu: PlayerRatingsMenu = PlayerRatingsMenu(),
    @SerialName("sort")
    val sort: Sort = Sort()
) {
    /** Failures any command can report. */
    @Serializable
    data class CommandError(
        @SerialName("unknown_error")
        val unknownError: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&4Unknown error")
            translation(MinecraftLocales.RU_RU, "&4Неизвестная ошибка")
        },
        @SerialName("wrong_usage")
        val wrongUsage: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&3Wrong command usage")
                translation(MinecraftLocales.RU_RU, "&3Неверное использование команды")
            }
        ),
        @SerialName("no_permission")
        val noPermission: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&4You don't have permission!")
                translation(MinecraftLocales.RU_RU, "&4У вас нет прав!")
            }
        ),
        @SerialName("players_only")
        val playersOnly: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&4Only players can use this command!")
                translation(MinecraftLocales.RU_RU, "&4Команда доступна только игрокам!")
            }
        ),
        @SerialName("player_not_found")
        val playerNotFound: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&4There is no such player!")
                translation(MinecraftLocales.RU_RU, "&4Такого игрока нет!")
            }
        )
    )

    @Serializable
    data class Reload(
        @SerialName("started")
        val started: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&3Reloading the plugin")
                translation(MinecraftLocales.RU_RU, "&3Перезагрузка плагина")
            }
        ),
        @SerialName("completed")
        val completed: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&3Reload complete")
                translation(MinecraftLocales.RU_RU, "&3Перезагрузка успешно завершена")
            }
        )
    )

    /** Outcomes of a player rating another one. */
    @Serializable
    data class RatingChange(
        @SerialName("liked")
        private val liked: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&3You raised the rating of %player%")
                translation(MinecraftLocales.RU_RU, "&3Вы повысили рейтинг игрока %player%")
            }
        ),
        @SerialName("disliked")
        private val disliked: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&4You lowered the rating of %player%")
                translation(MinecraftLocales.RU_RU, "&4Вы понизили рейтинг игрока %player%")
            }
        ),
        @SerialName("cannot_rate_self")
        val cannotRateSelf: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&4You can't rate yourself!")
                translation(MinecraftLocales.RU_RU, "&4Вы не можете поставить рейтинг самому себе!")
            }
        ),
        @SerialName("wrong_message_length")
        val wrongMessageLength: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&4The rating reason must be [5;30] characters long")
                translation(MinecraftLocales.RU_RU, "&4Длина причины рейтинга должна быть в диапазоне [5;30]")
            }
        ),
        @SerialName("daily_limit")
        val dailyLimit: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&4You have already voted the maximum number of times today")
                translation(MinecraftLocales.RU_RU, "&4Вы уже проголосовали максимальное количество раз за день")
            }
        ),
        @SerialName("player_limit")
        val playerLimit: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "&4You have already given this player the maximum number of votes today"
                )
                translation(
                    MinecraftLocales.RU_RU,
                    "&4Сегодня вы выдали максимальное возможное количество голосов этому игроку"
                )
            }
        ),
        @SerialName("not_enough_playtime")
        val notEnoughPlaytime: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&4You haven't played on the server long enough")
                translation(MinecraftLocales.RU_RU, "&4Вы недостаточно долго были на сервере")
            }
        )
    ) {
        fun liked(playerName: String): LocalizableComponent = liked.replace("%player%", playerName)

        fun disliked(playerName: String): LocalizableComponent = disliked.replace("%player%", playerName)
    }

    @Serializable
    data class PlayerKill(
        @SerialName("rating_lowered")
        private val ratingLowered: LocalizedText = PREFIX.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&7You killed %killed_player%, your rating was lowered")
                translation(MinecraftLocales.RU_RU, "&7Вы убили игрока %killed_player%, ваш рейтинг был понижен")
            }
        ),
        @SerialName("reason")
        private val reason: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Killed &2%killed_player%")
            translation(MinecraftLocales.RU_RU, "&7Убил игрока &2%killed_player%")
        }
    ) {
        fun ratingLowered(playerName: String): LocalizableComponent {
            return ratingLowered.replace("%killed_player%", playerName)
        }

        /** The reason of the rating change the killer gets. */
        fun reason(playerName: String): LocalizableComponent = reason.replace("%killed_player%", playerName)
    }

    /** Parts shared by every menu. */
    @Serializable
    data class Menu(
        @SerialName("loading")
        val loading: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Loading...")
            translation(MinecraftLocales.RU_RU, "&7Загрузка...")
        },
        @SerialName("previous_page")
        val previousPage: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Previous page")
            translation(MinecraftLocales.RU_RU, "&7Пред. страница")
        },
        @SerialName("next_page")
        val nextPage: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Next page")
            translation(MinecraftLocales.RU_RU, "&7След. страница")
        },
        @SerialName("close")
        val close: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Close")
            translation(MinecraftLocales.RU_RU, "&7Закрыть")
        },
        @SerialName("player_name")
        private val playerName: LocalizedText = LocalizedText.shared("&2%player%"),
        @SerialName("first_connection")
        private val firstConnection: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7First joined: %time%")
            translation(MinecraftLocales.RU_RU, "&7Впервые зашёл: %time%")
        },
        @SerialName("last_connection")
        private val lastConnection: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Last seen: %time%")
            translation(MinecraftLocales.RU_RU, "&7Был в сети: %time%")
        },
        @SerialName("positive_value")
        private val positiveValue: LocalizedText = LocalizedText.shared("&2%value%"),
        @SerialName("negative_value")
        private val negativeValue: LocalizedText = LocalizedText.shared("&4%value%")
    ) {
        fun playerName(name: String): LocalizableComponent = playerName.replace("%player%", name)

        fun firstConnection(time: String): LocalizableComponent = firstConnection.replace("%time%", time)

        fun lastConnection(time: String): LocalizableComponent = lastConnection.replace("%time%", time)

        /** Colors [text] by the sign of the rating it belongs to. */
        fun ratingValue(isPositive: Boolean, text: String): LocalizableComponent {
            val template = if (isPositive) positiveValue else negativeValue
            return template.replace("%value%", text)
        }
    }

    /** The menu with the total rating of every player. */
    @Serializable
    data class RatingsMenu(
        @SerialName("title")
        val title: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&2Rating")
            translation(MinecraftLocales.RU_RU, "&2Рейтинг")
        },
        @SerialName("total")
        private val total: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Rating: %rating%")
            translation(MinecraftLocales.RU_RU, "&7Рейтинг: %rating%")
        },
        @SerialName("count")
        private val count: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Ratings: %count%")
            translation(MinecraftLocales.RU_RU, "&7Рейтингов: %count%")
        }
    ) {
        fun total(rating: LocalizableComponent): LocalizableComponent = total.replace("%rating%", rating)

        fun count(ratingCount: Long): LocalizableComponent = count.replace("%count%", ratingCount.toString())
    }

    /** The menu with every rating one player got. */
    @Serializable
    data class PlayerRatingsMenu(
        @SerialName("title")
        private val title: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&2Rating of %player%")
            translation(MinecraftLocales.RU_RU, "&2Рейтинг игрока %player%")
        },
        @SerialName("message")
        private val message: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Message: %message%")
            translation(MinecraftLocales.RU_RU, "&7Сообщение: %message%")
        },
        @SerialName("click_to_delete")
        val clickToDelete: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&4Left-click to delete")
            translation(MinecraftLocales.RU_RU, "&4Нажмите ЛКМ чтобы удалить")
        },
        @SerialName("events_title")
        val eventsTitle: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Other")
            translation(MinecraftLocales.RU_RU, "&7Остальное")
        },
        @SerialName("kill_count")
        private val killCount: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Kills: &2%kills%")
            translation(MinecraftLocales.RU_RU, "&7Количество убийств: &2%kills%")
        }
    ) {
        fun title(playerName: String): LocalizableComponent = title.replace("%player%", playerName)

        fun message(text: LocalizableComponent): LocalizableComponent = message.replace("%message%", text)

        fun killCount(count: Int): LocalizableComponent = killCount.replace("%kills%", count.toString())
    }

    @Serializable
    data class Sort(
        @SerialName("title")
        val title: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&7Sort")
            translation(MinecraftLocales.RU_RU, "&7Сортировка")
        },
        @SerialName("player")
        val player: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "Players")
            translation(MinecraftLocales.RU_RU, "Игроки")
        },
        @SerialName("date")
        val date: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "Date")
            translation(MinecraftLocales.RU_RU, "Дата")
        },
        @SerialName("rating")
        val rating: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "Rating")
            translation(MinecraftLocales.RU_RU, "Рейтинг")
        },
        @SerialName("option")
        private val option: LocalizedText = LocalizedText.shared("&f%sort%"),
        @SerialName("selected_ascending")
        private val selectedAscending: LocalizedText = LocalizedText.shared("&6%sort% &6&l↓"),
        @SerialName("selected_descending")
        private val selectedDescending: LocalizedText = LocalizedText.shared("&6%sort% &6&l↑")
    ) {
        /** A line of the sort button: only the selected [sort] shows its direction. */
        fun option(sort: LocalizableComponent, isSelected: Boolean, isAscending: Boolean): LocalizableComponent {
            val template = when {
                !isSelected -> option
                isAscending -> selectedAscending
                else -> selectedDescending
            }
            return template.replace("%sort%", sort)
        }
    }

    companion object {
        private val PREFIX = LocalizedText.shared("&6[AR] ")
    }
}
