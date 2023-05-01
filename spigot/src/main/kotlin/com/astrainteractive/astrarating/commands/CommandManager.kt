import com.astrainteractive.astrarating.AstraRating
import com.astrainteractive.astrarating.commands.di.CommandsModule
import com.astrainteractive.astrarating.commands.rating.ratingCommand
import com.astrainteractive.astrarating.commands.reload.reload
import com.astrainteractive.astrarating.commands.tabCompleter

/**
 * Command handler for your plugin
 * It's better to create different executors for different commands
 * @see Reload
 */
class CommandManager(
    val plugin: AstraRating,
    module: CommandsModule
) {
    /**
     * Here you should declare commands for your plugin
     *
     * Commands stored in plugin.yml
     *
     * etemp has TabCompleter
     */
    init {
        tabCompleter(plugin)
        reload(plugin, module)
        ratingCommand(plugin, module)
    }
}
