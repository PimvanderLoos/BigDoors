package nl.pim16aap2.bigdoors.spigot.util;

import lombok.extern.flogger.Flogger;
import nl.pim16aap2.bigdoors.api.IBigDoorsPlatformProvider;
import nl.pim16aap2.bigdoors.api.debugging.DebugReporter;
import nl.pim16aap2.bigdoors.api.debugging.DebuggableRegistry;
import nl.pim16aap2.bigdoors.spigot.BigDoorsPlugin;
import nl.pim16aap2.bigdoors.spigot.events.BigDoorsSpigotEvent;
import nl.pim16aap2.bigdoors.spigot.events.DoorCreatedEvent;
import nl.pim16aap2.bigdoors.spigot.events.DoorPrepareAddOwnerEvent;
import nl.pim16aap2.bigdoors.spigot.events.DoorPrepareCreateEvent;
import nl.pim16aap2.bigdoors.spigot.events.DoorPrepareDeleteEvent;
import nl.pim16aap2.bigdoors.spigot.events.DoorPrepareLockChangeEvent;
import nl.pim16aap2.bigdoors.spigot.events.DoorPrepareRemoveOwnerEvent;
import nl.pim16aap2.bigdoors.spigot.events.dooraction.DoorEventToggleEnd;
import nl.pim16aap2.bigdoors.spigot.events.dooraction.DoorEventTogglePrepare;
import nl.pim16aap2.bigdoors.spigot.events.dooraction.DoorEventToggleStart;
import nl.pim16aap2.bigdoors.spigot.util.api.IBigDoorsSpigotSubPlatform;
import nl.pim16aap2.bigdoors.util.Util;
import nl.pim16aap2.util.SafeStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.logging.Level;

@Singleton
@Flogger
public class DebugReporterSpigot extends DebugReporter
{
    private final BigDoorsPlugin bigDoorsPlugin;
    private final @Nullable IBigDoorsSpigotSubPlatform subPlatform;

    @Inject
    public DebugReporterSpigot(BigDoorsPlugin bigDoorsPlugin, IBigDoorsPlatformProvider platformProvider,
                               @Nullable IBigDoorsSpigotSubPlatform subPlatform, DebuggableRegistry debuggableRegistry)
    {
        super(debuggableRegistry, platformProvider);
        this.bigDoorsPlugin = bigDoorsPlugin;
        this.subPlatform = subPlatform;
    }

    @Override
    protected String getAdditionalDebugReport()
    {
        return new SafeStringBuilder()
            .append("Server version: ").append(() -> Bukkit.getServer().getVersion())
            .append('\n')
            .append("SpigotSubPlatform: ").append(() -> subPlatform == null ? "null" : subPlatform.getClass().getName())
            .append('\n')
            .append("Registered addons: ").append(bigDoorsPlugin::getRegisteredPlugins)
            .append('\n')

//            // TODO: Implement this:
//            .append("Enabled protection hooks: ")
//            .append(getAllProtectionHooksOrSomething())

            .append("EventListeners:\n").append(getListeners(DoorPrepareAddOwnerEvent.class,
                                                             DoorPrepareCreateEvent.class,
                                                             DoorPrepareDeleteEvent.class,
                                                             DoorPrepareLockChangeEvent.class,
                                                             DoorPrepareRemoveOwnerEvent.class,
                                                             DoorCreatedEvent.class,
                                                             DoorEventToggleEnd.class,
                                                             DoorEventTogglePrepare.class,
                                                             DoorEventToggleStart.class))
            .append('\n')
            .toString();
    }

    private String getListeners(Class<?>... classes)
    {
        final SafeStringBuilder sb = new SafeStringBuilder();
        for (final Class<?> clz : classes)
        {
            if (!(BigDoorsSpigotEvent.class.isAssignableFrom(clz)))
            {
                sb.append("ERROR: ").append(clz::getName).append('\n');
                continue;
            }
            try
            {
                final var handlerListMethod = clz.getDeclaredField("HANDLERS_LIST");
                handlerListMethod.setAccessible(true);
                final var handlers = (HandlerList) handlerListMethod.get(null);
                sb.append("    ").append(clz::getSimpleName).append(": ")
                  .append(() -> Util.toString(handlers.getRegisteredListeners(),
                                              DebugReporterSpigot::formatRegisteredListener))
                  .append('\n');
            }
            catch (Exception e)
            {
                log.at(Level.SEVERE).withCause(e).log("Failed to find MethodHandle for handlers!");
                sb.append("ERROR: ").append(clz::getName).append('\n');
            }
        }

        return sb.toString();
    }

    private static String formatRegisteredListener(RegisteredListener listener)
    {
        return String.format("{%s: %s (%s)}", listener.getPlugin(), listener.getListener(), listener.getPriority());
    }
}
