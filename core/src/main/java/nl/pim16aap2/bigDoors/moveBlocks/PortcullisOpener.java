package nl.pim16aap2.bigDoors.moveBlocks;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.util.ChunkUtils.ChunkLoadMode;
import nl.pim16aap2.bigDoors.util.ChunkUtils.ChunkLoadResult;
import nl.pim16aap2.bigDoors.util.DoorOpenResult;
import nl.pim16aap2.bigDoors.util.Pair;
import nl.pim16aap2.bigDoors.util.RotateDirection;
import nl.pim16aap2.bigDoors.util.Util;
import nl.pim16aap2.bigDoors.util.Vector2D;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static nl.pim16aap2.bigDoors.moveBlocks.OpenerUtil.rescheduleTaskForLocationIfNeeded;

public class PortcullisOpener implements Opener
{
    private static final List<RotateDirection> VALID_ROTATE_DIRECTIONS = Collections
        .unmodifiableList(Arrays.asList(RotateDirection.UP, RotateDirection.DOWN));

    private final BigDoors plugin;

    public PortcullisOpener(BigDoors plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public @Nonnull List<RotateDirection> getValidRotateDirections()
    {
        return VALID_ROTATE_DIRECTIONS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Vector2D, Vector2D> getChunkRange(Door door)
    {
        return getCurrentChunkRange(door);
    }

    protected boolean isRotateDirectionValid(RotateDirection openDirection)
    {
        return openDirection.equals(RotateDirection.UP) || openDirection.equals(RotateDirection.DOWN);
    }

    @Override
    public boolean isRotateDirectionValid(@Nonnull Door door)
    {
        return isRotateDirectionValid(door.getOpenDir());
    }

    @Override
    public RotateDirection getRotateDirection(Door door)
    {
        if (isRotateDirectionValid(door))
            return door.getOpenDir();
        return RotateDirection.UP;
    }

    @Override
    @Nonnull public Optional<Pair<Location, Location>> getNewCoordinates(@Nonnull Door door)
    {
        if (door.getBlocksToMove() > plugin.getConfigLoader().getMaxBlocksToMove())
        {
            plugin.getMyLogger().warn("blocksToMove value of " + door.getBlocksToMove() +
                                          " exceeds limit of " + plugin.getConfigLoader().getMaxBlocksToMove() +
                                          " for portcullis: " + door);
            return Optional.empty();
        }

        final int maxDoorSize = getSizeLimit(door);
        if (maxDoorSize > 0 && door.getBlockCount() > maxDoorSize)
        {
            plugin.getMyLogger().warn("Size " + door.getBlockCount() +
                                          " exceeds limit of " + maxDoorSize +
                                          " for portcullis: " + door);
            return Optional.empty();
        }

        final int blocksToMove = getBlocksToMove(door);
        if (blocksToMove == 0)
        {
            plugin.getMyLogger().warn("Received invalid blocksToMove value of 0 for portcullis: " + door);
            return Optional.empty();
        }

        return Optional.of(new Pair<>(door.getMinimum().add(0, blocksToMove, 0),
                                      door.getMaximum().add(0, blocksToMove, 0)));
    }

    @Override
    public @Nonnull CompletableFuture<DoorOpenResult> openDoorFuture(
        @Nonnull Door door, double time, boolean instantOpen, boolean silent,
        @Nonnull ChunkLoadMode mode, boolean bypassProtectionHooks)
    {
        return rescheduleTaskForLocationIfNeeded(
            door,
            () -> openDoorFuture0(door, time, instantOpen, silent, mode, bypassProtectionHooks));
    }

    private @Nonnull CompletableFuture<DoorOpenResult> openDoorFuture0(
        @Nonnull Door door, double time, boolean instantOpen, boolean silent,
        @Nonnull ChunkLoadMode mode, boolean bypassProtectionHooks)
    {
        plugin.assertSchedulerRunning();

        if (!plugin.getCommander().canGo())
        {
            plugin.getMyLogger()
                  .info("Failed to toggle: " + door.toSimpleString() + ", as door toggles are currently disabled!");
            return CompletableFuture.completedFuture(abort(DoorOpenResult.ERROR, door.getDoorUID()));
        }

        if (plugin.getCommander().isDoorBusyRegisterIfNot(door.getDoorUID()))
        {
            if (!silent)
                plugin.getMyLogger()
                      .myLogger(Level.INFO, "Portcullis " + door.toSimpleString() + " is not available right now!");
            return CompletableFuture.completedFuture(abort(DoorOpenResult.BUSY, door.getDoorUID()));
        }

        final ChunkLoadResult chunkLoadResult = chunksLoaded(door, mode);
        if (chunkLoadResult == ChunkLoadResult.FAIL)
        {
            plugin.getMyLogger()
                  .logMessage("Chunks for portcullis " + door.toSimpleString() + " are not loaded!", true, false);
            return CompletableFuture.completedFuture(abort(DoorOpenResult.CHUNKSNOTLOADED, door.getDoorUID()));
        }
        if (chunkLoadResult == ChunkLoadResult.REQUIRED_LOAD)
            instantOpen = true;

        // Make sure the doorSize does not exceed the total doorSize.
        // If it does, open the door instantly.
        final int maxDoorSize = getSizeLimit(door);
        if (maxDoorSize > 0 && door.getBlockCount() > maxDoorSize)
        {
            plugin.getMyLogger()
                  .logMessage("Portcullis " + door.toSimpleString() + " Exceeds the size limit: " + maxDoorSize,
                              true, false);
            return CompletableFuture.completedFuture(abort(DoorOpenResult.ERROR, door.getDoorUID()));
        }

        if (door.getBlocksToMove() > BigDoors.get().getConfigLoader().getMaxBlocksToMove())
        {
            plugin.getMyLogger().logMessage("Portcullis " + door.toSimpleString() + " Exceeds blocksToMove limit: "
                                                + door.getBlocksToMove() + ". Limit = " +
                                                BigDoors.get().getConfigLoader().getMaxBlocksToMove(), true, false);
            return CompletableFuture.completedFuture(abort(DoorOpenResult.BLOCKSTOMOVEINVALID, door.getDoorUID()));
        }

        final int blocksToMove = getBlocksToMove(door);
        if (blocksToMove == 0)
            return CompletableFuture.completedFuture(abort(DoorOpenResult.NODIRECTION, door.getDoorUID()));

        final Location newMin = door.getMinimum().add(0, blocksToMove, 0);
        final Location newMax = door.getMaximum().add(0, blocksToMove, 0);

        if (fireDoorEventTogglePrepare(door, instantOpen))
            return CompletableFuture.completedFuture(abort(DoorOpenResult.CANCELLED, door.getDoorUID()));

        if (bypassProtectionHooks)
            return CompletableFuture.completedFuture(openDoor0(door, blocksToMove, instantOpen, time));

        final boolean instantOpen0 = instantOpen;
        return hasAccessToLocations(door, newMin, newMax).thenCompose(
            hasAccess ->
            {
                if (!hasAccess)
                    return CompletableFuture.completedFuture(abort(DoorOpenResult.NOPERMISSION, door.getDoorUID()));
                return Util.runSync(() -> openDoor0(door, blocksToMove, instantOpen0, time),
                                    1, TimeUnit.SECONDS, DoorOpenResult.ERROR);
            }).exceptionally(throwable -> Util.exceptionally(throwable, DoorOpenResult.ERROR));
    }

    private DoorOpenResult openDoor0(Door door, int blocksToMove, boolean instantOpen, double time)
    {
        if (!isRotateDirectionValid(door))
        {
            RotateDirection openDirection = door.isOpen() ?
                                            (blocksToMove > 0 ? RotateDirection.DOWN : RotateDirection.UP) :
                                            (blocksToMove > 0 ? RotateDirection.UP : RotateDirection.DOWN);
            plugin.getMyLogger().logMessage("Updating openDirection of portcullis " + door.toSimpleString() + " to "
                                                + openDirection.name() +
                                                ". If this is undesired, change it via the GUI.", true, false);
            plugin.getCommander().updateDoorOpenDirection(door.getDoorUID(), openDirection);
        }

        plugin.getCommander()
              .addBlockMover(new VerticalMover(plugin, door.getWorld(), time, door, instantOpen, blocksToMove,
                                               plugin.getConfigLoader().pcMultiplier()));
        fireDoorEventToggleStart(door, instantOpen);
        return DoorOpenResult.SUCCESS;
    }

    private int getBlocksInDir(Door door, RotateDirection upDown)
    {
        int xMin, xMax, zMin, zMax, yMin, yMax, yLen, blocksUp = 0, delta;
        xMin = door.getMinimum().getBlockX();
        yMin = door.getMinimum().getBlockY();
        zMin = door.getMinimum().getBlockZ();
        xMax = door.getMaximum().getBlockX();
        yMax = door.getMaximum().getBlockY();
        zMax = door.getMaximum().getBlockZ();
        yLen = yMax - yMin + 1;

        final int blocksToMoveLimit = BigDoors.get().getConfigLoader().getMaxBlocksToMove();
        final int distanceToWorldLimit = getDistanceToWorldLimit(door, plugin.getWorldHeightManager(), upDown);
        final int blocksToMove = door.getBlocksToMove() < 1 ? yLen : door.getBlocksToMove();

        final int distanceToCheck = Util.minPositive(blocksToMove, blocksToMoveLimit, distanceToWorldLimit);
        if (distanceToCheck <= 0)
            return 0;

        int xAxis, yAxis, zAxis, yGoal;
        World world = door.getWorld();
        delta = upDown == RotateDirection.DOWN ? -1 : 1;
        yAxis = upDown == RotateDirection.DOWN ? yMin - 1 : yMax + 1;
        yGoal = upDown == RotateDirection.DOWN ? yMin - distanceToCheck - 1 : yMax + distanceToCheck + 1;

        while (yAxis != yGoal)
        {
            for (xAxis = xMin; xAxis <= xMax; ++xAxis)
                for (zAxis = zMin; zAxis <= zMax; ++zAxis)
                    if (!Util.canOverwriteMaterial(world.getBlockAt(xAxis, yAxis, zAxis).getType()))
                        return blocksUp;
            yAxis += delta;
            blocksUp += delta;
        }
        return blocksUp;
    }

    private RotateDirection getCurrentDirection(Door door)
    {
        if (!door.isOpen())
            return door.getOpenDir();

        if (door.getOpenDir().equals(RotateDirection.UP))
            return RotateDirection.DOWN;

        if (door.getOpenDir().equals(RotateDirection.DOWN))
            return RotateDirection.UP;

        return RotateDirection.NONE;
    }

    private int getBlocksToMove(Door door)
    {
        RotateDirection openDir = getCurrentDirection(door);
        if (isRotateDirectionValid(openDir))
            return getBlocksInDir(door, openDir);
        else
        {
            int blocksUp = getBlocksInDir(door, RotateDirection.UP);
            int blocksDown = getBlocksInDir(door, RotateDirection.DOWN);
            return blocksUp > -1 * blocksDown ? blocksUp : blocksDown;
        }
    }
}
