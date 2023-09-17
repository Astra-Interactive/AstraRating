
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
    module: CommandsModule
) : CommandsModule by module {
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
}
