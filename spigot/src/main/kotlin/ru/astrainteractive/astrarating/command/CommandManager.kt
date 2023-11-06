import ru.astrainteractive.astrarating.command.di.CommandsDependencies
import ru.astrainteractive.astrarating.command.rating.RatingCommand
import ru.astrainteractive.astrarating.command.reload.ReloadCommand
import ru.astrainteractive.astrarating.command.tabCompleter

/**
 * Command handler for your plugin
 * It's better to create different executors for different commands
 * @see Reload
 */
class CommandManager(
    dependencies: CommandsDependencies
) : CommandsDependencies by dependencies {
    /**
     * Here you should declare commands for your plugin
     *
     * Commands stored in plugin.yml
     *
     * etemp has TabCompleter
     */
    init {
        tabCompleter()

        RatingCommand(
            addRatingUseCase = dependencies.addRatingUseCase,
            translation = dependencies.translation,
            coroutineScope = dependencies.scope,
            dispatchers = dependencies.dispatchers,
            translationContext = dependencies.translationContext
        ).register(plugin)

        ReloadCommand(
            permissionManager = dependencies.permissionManager,
            translation = dependencies.translation,
            translationContext = dependencies.translationContext
        ).register(plugin)
    }
}
