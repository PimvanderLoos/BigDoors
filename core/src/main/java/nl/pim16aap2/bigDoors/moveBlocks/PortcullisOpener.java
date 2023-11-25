package nl.pim16aap2.bigDoors.moveBlocks;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.util.ChunkUtils.ChunkLoadMode;
import nl.pim16aap2.bigDoors.util.ChunkUtils.ChunkLoadResult;
import nl.pim16aap2.bigDoors.util.DoorOpenResult;
import nl.pim16aap2.bigDoors.util.DoorType;
import nl.pim16aap2.bigDoors.util.Pair;
import nl.pim16aap2.bigDoors.util.RotateDirection;
import nl.pim16aap2.bigDoors.util.Util;
import nl.pim16aap2.bigDoors.util.Vector2D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
    public DoorType getType()
    {
        return DoorType.PORTCULLIS;
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
        plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format("Final blocks to move: %d", blocksToMove));

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
        plugin.assertSchedulerRunning();

        if (!plugin.getCommander().animationsAllowed())
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

        plugin.getMyLogger().logMessageToLogFile(""); // Empty line for readability.
        plugin.getMyLogger().logMessageToLogFileForDoor(
            door,
            String.format(
                "Trying to open portcullis at coordinates [%d, %d, %d] - [%d, %d, %d] with blocksToMove %d and isOpen %b",
                door.getMinimum().getBlockX(), door.getMinimum().getBlockY(), door.getMinimum().getBlockZ(),
                door.getMaximum().getBlockX(), door.getMaximum().getBlockY(), door.getMaximum().getBlockZ(),
                door.getBlocksToMove(), door.isOpen()
            ));

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
        plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format("Final blocks to move: %d", blocksToMove));
        if (blocksToMove == 0)
        {
            plugin.getMyLogger().logMessageToLogFileForDoor(
                door, "Received invalid blocksToMove value of 0 for portcullis: " + door);
            return CompletableFuture.completedFuture(abort(DoorOpenResult.NODIRECTION, door.getDoorUID()));
        }

        final Location newMin = door.getMinimum().add(0, blocksToMove, 0);
        final Location newMax = door.getMaximum().add(0, blocksToMove, 0);

        if (fireDoorEventTogglePrepare(door, instantOpen))
            return CompletableFuture.completedFuture(abort(DoorOpenResult.CANCELLED, door.getDoorUID()));

        if (bypassProtectionHooks)
        {
            plugin.getMyLogger().logMessageToLogFileForDoor(
                door, "Bypassing protection hooks for portcullis: " + door.toSimpleString());
            return CompletableFuture.completedFuture(openDoor0(door, blocksToMove, instantOpen, time));
        }

        final boolean instantOpen0 = instantOpen;
        return hasAccessToLocations(door, newMin, newMax).thenCompose(
            hasAccess ->
            {
                if (!hasAccess)
                {
                    plugin.getMyLogger().logMessageToLogFileForDoor(
                        door, "Player does not have access to portcullis: " + door.toSimpleString());
                    return CompletableFuture.completedFuture(abort(DoorOpenResult.NOPERMISSION, door.getDoorUID()));
                }
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

        plugin.getMyLogger().logMessageToLogFileForDoor(door, "Starting portcullis animation!");
        plugin.getCommander()
              .addBlockMover(new VerticalMover(plugin, door.getWorld(), time, door, instantOpen, blocksToMove,
                                               plugin.getConfigLoader().pcMultiplier()));
        fireDoorEventToggleStart(door, instantOpen);
        return DoorOpenResult.SUCCESS;
    }

    /**
     * Checks if a block that obstructs the portcullis corresponds to a block in the portcullis that can be filled in.
     *
     * @param door
     *     The portcullis to check.
     * @param xAxis
     *     The x coordinate of the block to check.
     * @param yAxis
     *     The y coordinate of the block to check.
     * @param zAxis
     *     The z coordinate of the block to check.
     * @param delta
     *     The direction to check in. -1 for down, 1 for up.
     * @return True if there is an empty gap of air in the portcullis that can be filled in, otherwise false.
     */
    private boolean hasGapForBlock(
        Door door,
        int xAxis,
        int yAxis,
        int zAxis,
        int targetBlocksToMove,
        int delta)
    {
        final int checkYAxis = yAxis - targetBlocksToMove * delta;

        if (!door.isInsideDoor(xAxis, checkYAxis, zAxis))
        {
            final Location min = door.getMinimum();
            final Location max = door.getMaximum();

            plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
                "Tried to look for a gap at [%d, %d, %d], but this is not inside the portcullis!" +
                    " The portcullis is at [%d, %d, %d] - [%d, %d, %d].",
                xAxis, checkYAxis, zAxis,
                min.getBlockX(), min.getBlockY(), min.getBlockZ(),
                max.getBlockX(), max.getBlockY(), max.getBlockZ()
            ));

            return false;
        }

        final Material blockType = door.getWorld().getBlockAt(xAxis, checkYAxis, zAxis).getType();

        plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
            "Found block of type %s at [%d, %d, %d] in the portcullis. Checking if it can be filled in...",
            blockType.name(), xAxis, checkYAxis, zAxis
        ));

        return Util.canOverwriteMaterial(blockType);
    }

    private int getBlocksInDir(Door door, RotateDirection upDown, boolean allowFillingInGaps)
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
        {
            plugin.getMyLogger().logMessageToLogFileForDoor(
                door, "Distance to check is 0 or less (" + distanceToCheck + ")" +
                    ": blocksToMoveLimit = " + blocksToMoveLimit +
                    ", distanceToWorldLimit = " + distanceToWorldLimit +
                    ", blocksToMove = " + blocksToMove +
                    ". Returning a blocks to move value of 0 in direction " + upDown.name() + "."
            );
            return 0;
        }

        int xAxis, yAxis, zAxis, yGoal;
        World world = door.getWorld();
        delta = upDown == RotateDirection.DOWN ? -1 : 1;
        yAxis = upDown == RotateDirection.DOWN ? yMin - 1 : yMax + 1;
        yGoal = upDown == RotateDirection.DOWN ? yMin - distanceToCheck - 1 : yMax + distanceToCheck + 1;

        plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
            "Trying to find number of blocks to move in direction %s. " +
                "Provided target: %s, fixed target: %s, delta: %d, yAxis: %d, yGoal: %d",
            upDown.name(),
            door.getBlocksToMove() == 0 ? "default" : door.getBlocksToMove(),
            distanceToCheck,
            delta,
            yAxis,
            yGoal
        ));

        int resolvedObstructions = 0;
        int failedObstructions = 0;
        @Nullable Integer returnValue = null;
        while (yAxis != yGoal)
        {
            for (xAxis = xMin; xAxis <= xMax; ++xAxis)
                for (zAxis = zMin; zAxis <= zMax; ++zAxis)
                    if (!Util.canOverwriteMaterial(world.getBlockAt(xAxis, yAxis, zAxis).getType()))
                    {
                        final boolean canBeResolved;
                        if (allowFillingInGaps &&
                            // Check if there is an empty gap of air in the portcullis that can be filled in.
                            hasGapForBlock(door, xAxis, yAxis, zAxis, distanceToCheck, delta))
                        {
                            canBeResolved = true;
                            resolvedObstructions++;
                        }
                        else
                        {
                            canBeResolved = false;
                            failedObstructions++;
                            if (returnValue == null)
                                returnValue = blocksUp;
                        }

                        plugin.getMyLogger().logMessageToLogFileForDoor(
                            door, String.format(
                                "Found obstruction at [%d, %d, %d] in direction %s: %s! Can be resolved: %b.",
                                xAxis, yAxis, zAxis,
                                upDown.name(),
                                world.getBlockAt(xAxis, yAxis, zAxis).getType().name(),
                                canBeResolved
                            ));
                    }
            yAxis += delta;
            blocksUp += delta;
        }
        if (resolvedObstructions > 0 || failedObstructions > 0)
        {
            plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
                "%sEncountered %d obstruction(s)! Resolved: %d, Failed: %d.",
                (failedObstructions > 0 ? "WARNING: POTENTIALLY BROKEN PORTCULLIS! " : ""),
                resolvedObstructions + failedObstructions,
                resolvedObstructions,
                failedObstructions
            ));
        }

        return returnValue == null ? blocksUp : returnValue;
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
            return getBlocksInDir(door, openDir, true);
        else
        {
            int blocksUp = getBlocksInDir(door, RotateDirection.UP, false);
            int blocksDown = getBlocksInDir(door, RotateDirection.DOWN, false);
            return blocksUp > -1 * blocksDown ? blocksUp : blocksDown;
        }
    }
}
