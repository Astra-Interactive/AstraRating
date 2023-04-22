import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.commands.*
import com.astrainteractive.astrarating.modules.ServiceLocator
import com.astrainteractive.astrarating.plugin.AstraPermission
import org.bukkit.command.CommandSender
import ru.astrainteractive.astralibs.di.getValue


/**
 * Command handler for your plugin
 * It's better to create different executors for different commands
 * @see Reload
 */
class CommandManager {
    /**
     * Here you should declare commands for your plugin
     *
     * Commands stored in plugin.yml
     *
     * etemp has TabCompleter
     */
    init {
        tabCompleter()
        reload()
        ratingCommand()
    }

    companion object {
        private val translation by ServiceLocator.translation
        fun reload(sender: CommandSender) {
            if (!AstraPermission.Reload.hasPermission(sender)) {
                sender.sendMessage(translation.noPermission)
                return
            }
            sender.sendMessage(translation.reload)
            AstraRating.instance.reloadPlugin()
            sender.sendMessage(translation.reloadComplete)
        }
    }

}