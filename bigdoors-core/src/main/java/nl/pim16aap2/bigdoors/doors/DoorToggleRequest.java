package nl.pim16aap2.bigdoors.doors;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.flogger.Flogger;
import nl.pim16aap2.bigdoors.api.IMessageable;
import nl.pim16aap2.bigdoors.api.IPExecutor;
import nl.pim16aap2.bigdoors.api.IPPlayer;
import nl.pim16aap2.bigdoors.api.factories.IPPlayerFactory;
import nl.pim16aap2.bigdoors.events.dooraction.DoorActionCause;
import nl.pim16aap2.bigdoors.events.dooraction.DoorActionType;
import nl.pim16aap2.bigdoors.localization.ILocalizer;
import nl.pim16aap2.bigdoors.moveblocks.AutoCloseScheduler;
import nl.pim16aap2.bigdoors.moveblocks.DoorActivityManager;
import nl.pim16aap2.bigdoors.util.DoorToggleResult;
import nl.pim16aap2.bigdoors.util.Util;
import nl.pim16aap2.bigdoors.util.doorretriever.DoorRetriever;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Getter
@ToString
@Flogger
public class DoorToggleRequest
{
    @Getter
    private final DoorRetriever doorRetriever;
    @Getter
    private final DoorActionCause doorActionCause;
    @Getter
    private final IMessageable messageReceiver;
    @Getter
    private final @Nullable CompletableFuture<IPPlayer> responsible;
    @Getter
    private final double time;
    @Getter
    private final boolean skipAnimation;
    @Getter
    private final DoorActionType doorActionType;

    private final ILocalizer localizer;
    private final DoorActivityManager doorActivityManager;
    private final AutoCloseScheduler autoCloseScheduler;
    private final IPPlayerFactory playerFactory;
    private final IPExecutor executor;

    @AssistedInject
    public DoorToggleRequest(
        @Assisted DoorRetriever doorRetriever, @Assisted DoorActionCause doorActionCause,
        @Assisted IMessageable messageReceiver, @Assisted @Nullable CompletableFuture<IPPlayer> responsible,
        @Assisted double time, @Assisted boolean skipAnimation, @Assisted DoorActionType doorActionType,
        ILocalizer localizer, DoorActivityManager doorActivityManager, AutoCloseScheduler autoCloseScheduler,
        IPPlayerFactory playerFactory, IPExecutor executor)
    {
        this.doorRetriever = doorRetriever;
        this.doorActionCause = doorActionCause;
        this.messageReceiver = messageReceiver;
        this.responsible = responsible;
        this.time = time;
        this.skipAnimation = skipAnimation;
        this.doorActionType = doorActionType;
        this.localizer = localizer;
        this.doorActivityManager = doorActivityManager;
        this.autoCloseScheduler = autoCloseScheduler;
        this.playerFactory = playerFactory;
        this.executor = executor;
    }

    /**
     * Executes the toggle request.
     *
     * @return The result of the request.
     */
    public CompletableFuture<DoorToggleResult> execute()
    {
        log.at(Level.FINE).log("Executing toggle request: %s", this);
        if (responsible != null)
            return execute(doorRetriever, responsible);
        else
            return doorRetriever.getDoor().thenCompose(this::execute)
                                .exceptionally(throwable -> Util.exceptionally(throwable, DoorToggleResult.ERROR));
    }

    private CompletableFuture<DoorToggleResult> execute(
        DoorRetriever doorRetriever, CompletableFuture<IPPlayer> responsible)
    {
        final CompletableFuture<Optional<AbstractDoor>> futureDoor = doorRetriever.getDoor();
        final CompletableFuture<Void> result = CompletableFuture.allOf(futureDoor, responsible);
        return result.thenComposeAsync(ignored -> execute(futureDoor.join(), responsible.join()))
                     .exceptionally(throwable -> Util.exceptionally(throwable, DoorToggleResult.ERROR));
    }

    private CompletableFuture<DoorToggleResult> execute(Optional<AbstractDoor> doorOpt)
    {
        if (doorOpt.isEmpty())
        {
            log.at(Level.INFO).log("Toggle failure (no door found): %s", this);
            return CompletableFuture.completedFuture(DoorToggleResult.ERROR);
        }

        final CompletableFuture<IPPlayer> responsibleResult =
            responsible == null ? playerFactory.create(doorOpt.get().getPrimeOwner()) : responsible;

        return responsibleResult.thenComposeAsync(actualResponsible -> execute(doorOpt, actualResponsible));
    }

    private CompletableFuture<DoorToggleResult> execute(Optional<AbstractDoor> doorOpt, IPPlayer responsible)
    {
        if (doorOpt.isEmpty())
        {
            log.at(Level.INFO).log("Toggle failure (no door found): %s", this);
            return CompletableFuture.completedFuture(DoorToggleResult.ERROR);
        }
        final AbstractDoor door = doorOpt.get();

        if (executor.isMainThread())
            return CompletableFuture.completedFuture(execute(door, responsible));
        return executor.scheduleOnMainThread(() -> execute(door, responsible));
    }

    private DoorToggleResult execute(AbstractDoor door, IPPlayer responsible)
    {
        executor.assertMainThread();
        return door.toggle(doorActionCause, messageReceiver, responsible, time, skipAnimation, doorActionType);
    }

    @AssistedFactory
    public interface IFactory
    {
        DoorToggleRequest create(
            DoorRetriever doorRetriever, DoorActionCause doorActionCause, IMessageable messageReceiver,
            @Nullable CompletableFuture<IPPlayer> responsible, double time, boolean skipAnimation,
            DoorActionType doorActionType);
    }
}
