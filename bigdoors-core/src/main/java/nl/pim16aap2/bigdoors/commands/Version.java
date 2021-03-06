package nl.pim16aap2.bigdoors.commands;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import lombok.ToString;
import nl.pim16aap2.bigdoors.api.IBigDoorsPlatform;
import nl.pim16aap2.bigdoors.api.IBigDoorsPlatformProvider;
import nl.pim16aap2.bigdoors.localization.ILocalizer;

import java.util.concurrent.CompletableFuture;

/**
 * Represents the command that shows the {@link ICommandSender} the current version of the plugin that is running.
 *
 * @author Pim
 */
@ToString
public class Version extends BaseCommand
{
    private final IBigDoorsPlatformProvider platformProvider;

    @AssistedInject //
    Version(@Assisted ICommandSender commandSender, ILocalizer localizer, IBigDoorsPlatformProvider platformProvider)
    {
        super(commandSender, localizer);
        this.platformProvider = platformProvider;
    }

    @Override
    public CommandDefinition getCommand()
    {
        return CommandDefinition.VERSION;
    }

    @Override
    protected CompletableFuture<Boolean> executeCommand(PermissionsStatus permissions)
    {
        // TODO: Localization
        getCommandSender().sendMessage(
            platformProvider.getPlatform().map(IBigDoorsPlatform::getVersion).orElse("NULL"));
        return CompletableFuture.completedFuture(true);
    }

    @AssistedFactory
    interface IFactory
    {
        /**
         * Creates (but does not execute!) a new {@link Version} command.
         *
         * @param commandSender
         *     The {@link ICommandSender} responsible for executing the command and the target for sending the message
         *     containing the current version.
         * @return See {@link BaseCommand#run()}.
         */
        Version newVersion(ICommandSender commandSender);
    }
}
