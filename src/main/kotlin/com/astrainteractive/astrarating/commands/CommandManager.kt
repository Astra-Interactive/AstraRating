import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.commands.*
import com.astrainteractive.astrarating.utils.AstraPermission
import com.astrainteractive.astrarating.utils.Translation
import org.bukkit.command.CommandSender


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
        fun reload(sender: CommandSender) {
            if (!AstraPermission.Reload.hasPermission(sender)) {
                sender.sendMessage(Translation.noPermission)
                return
            }
            sender.sendMessage(Translation.reload)
            AstraRating.instance.reloadPlugin()
            sender.sendMessage(Translation.reloadComplete)
        }
    }

}