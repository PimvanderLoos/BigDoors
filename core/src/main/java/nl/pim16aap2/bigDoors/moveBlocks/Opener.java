package nl.pim16aap2.bigDoors.moveBlocks;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.WorldHeightManager;
import nl.pim16aap2.bigDoors.events.DoorEventToggle.ToggleType;
import nl.pim16aap2.bigDoors.events.DoorEventTogglePrepare;
import nl.pim16aap2.bigDoors.events.DoorEventToggleStart;
import nl.pim16aap2.bigDoors.util.ChunkUtils;
import nl.pim16aap2.bigDoors.util.ChunkUtils.ChunkLoadMode;
import nl.pim16aap2.bigDoors.util.ChunkUtils.ChunkLoadResult;
import nl.pim16aap2.bigDoors.util.ConfigLoader;
import nl.pim16aap2.bigDoors.util.DoorOpenResult;
import nl.pim16aap2.bigDoors.util.Pair;
import nl.pim16aap2.bigDoors.util.RotateDirection;
import nl.pim16aap2.bigDoors.util.Util;
import nl.pim16aap2.bigDoors.util.Vector2D;
import nl.pim16aap2.bigDoors.util.WorldHeightLimits;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Opener
{
    default boolean hasValidCoordinates(Door door)
    {
        if (door.getWorld() != null)
            return true;

        BigDoors.get().getMyLogger().logMessage("World is null for door " + door.toSimpleString() + "!", true, false);
        return false;
    }

    /**
     * Gets the minimum and maximum chunk coordinates this door may visit. Note that the output may not be 100% correct.
     * The accuracy depends on the type and how much is known about the door.
     *
     * @param door The door for which to check it.
     * @return A pair of coordinates in chunk-space (hence 2d) containing the lower-bound coordinates first and the
     * upper bound second.
     */
    @SuppressWarnings("unused")
    Pair<Vector2D, Vector2D> getChunkRange(Door door);

    /**
     * Gets the minimum and maximum chunk coordinates of the door according to its current location.
     *
     * @param door The door for which to check it.
     * @return A pair of coordinates in chunk-space (hence 2d) containing the lower-bound coordinates first and the
     * upper bound second.
     */
    default Pair<Vector2D, Vector2D> getCurrentChunkRange(Door door)
    {
        return ChunkUtils.getChunkRangeBetweenCoords(door.getMinimum(), door.getMaximum());
    }

    default ChunkLoadResult chunksLoaded(Door door, ChunkLoadMode mode)
    {
        if (!hasValidCoordinates(door))
            return ChunkLoadResult.FAIL;

        return ChunkUtils.checkChunks(door.getWorld(), getCurrentChunkRange(door), mode);
    }

    /**
     * Attempts to toggle a door using default values for everything.
     *
     * @param door                  The door to attempt to toggle.
     * @param bypassProtectionHooks Whether to bypass the protection hooks when trying to toggle the door.
     * @return The result of the toggle attempt.
     */
    default @NotNull CompletableFuture<DoorOpenResult> openDoorFuture(
        @NotNull Door door, boolean bypassProtectionHooks)
    {
        return openDoorFuture(door, 0.0, false, false, getChunkLoadMode(door), bypassProtectionHooks)
            .exceptionally(throwable -> Util.exceptionally(throwable, DoorOpenResult.ERROR));
    }

    /**
     * See {@link #openDoorFuture(Door, double, boolean, boolean, ChunkLoadMode, boolean)}.
     */
    default @NotNull CompletableFuture<DoorOpenResult> openDoorFuture(
        @NotNull Door door, double time)
    {
        return openDoorFuture(door, time, false, false)
            .exceptionally(throwable -> Util.exceptionally(throwable, DoorOpenResult.ERROR));
    }

    /**
     * See {@link #openDoorFuture(Door, double, boolean, boolean, ChunkLoadMode, boolean)}.
     */
    default @NotNull CompletableFuture<DoorOpenResult> openDoorFuture(
        @NotNull Door door, double time, boolean instantOpen)
    {
        return openDoorFuture(door, time, instantOpen, false)
            .exceptionally(throwable -> Util.exceptionally(throwable, DoorOpenResult.ERROR));
    }

    /**
     * See {@link #openDoorFuture(Door, double, boolean, boolean, ChunkLoadMode, boolean)}.
     */
    default @NotNull CompletableFuture<DoorOpenResult> openDoorFuture(
        @NotNull Door door, double time, boolean instantOpen, boolean silent)
    {
        return openDoorFuture(door, time, instantOpen, silent, getChunkLoadMode(door))
            .exceptionally(throwable -> Util.exceptionally(throwable, DoorOpenResult.ERROR));
    }

    /**
     * Figures out which {@link ChunkLoadMode} to use for a given door.
     * <p>
     * The mode is determined based on the config and the current state of the door.
     *
     * @param door The door for which to get the {@link ChunkLoadMode}.
     * @return The {@link ChunkLoadMode} to use for the door.
     */
    default @NotNull ChunkLoadMode getChunkLoadMode(@NotNull Door door)
    {
        ChunkLoadMode mode = BigDoors.get().getConfigLoader().getChunkLoadMode();

        // When "skipUnloadedAutoCloseToggle" is enabled, doors will not try to load
        // chunks if they have an autoCloseTimer. This only affects closed doors,
        // because opened doors cannot initiate an autoCloseTimer.
        if (mode == ChunkLoadMode.ATTEMPT_LOAD && (!door.isOpen()) &&
            BigDoors.get().getConfigLoader().skipUnloadedAutoCloseToggle() && door.getAutoClose() > 0)
            mode = ChunkLoadMode.VERIFY_LOADED;
        return mode;
    }

    /**
     * See {@link #openDoorFuture(Door, double, boolean, boolean, ChunkLoadMode, boolean)}.
     */
    default @NotNull CompletableFuture<DoorOpenResult> openDoorFuture(
        @NotNull Door door, double time, boolean instantOpen, boolean silent, @NotNull ChunkLoadMode mode)
    {
        return openDoorFuture(door, time, instantOpen, silent, mode, false)
            .exceptionally(throwable -> Util.exceptionally(throwable, DoorOpenResult.ERROR));
    }

    /**
     * Attempts to toggle a door.
     *
     * @param door                  The door to attempt to toggle.
     * @param time                  The amount of time the animation should (try to) take to complete. This will not be
     *                              the actual amount of time the full toggle will take because 1) some parts of the
     *                              animation are not included in this timing value and 2) There are some limits that
     *                              cannot be exceeded.
     *                              <p>
     *                              Setting this value to 0 will result in the default time value being used.
     * @param instantOpen           When this is true, the animation will be skipped.
     * @param silent                Whether to suppress messages.
     * @param mode                  Determines how to deal with unloaded chunks. See {@link ConfigLoader#getChunkLoadMode()}.
     * @param bypassProtectionHooks Whether to bypass the protection hooks when trying to toggle the door.
     * @return The result of the toggle attempt.
     */
    @NotNull CompletableFuture<DoorOpenResult> openDoorFuture(
        @NotNull Door door, double time, boolean instantOpen, boolean silent,
        @NotNull ChunkLoadMode mode, boolean bypassProtectionHooks);

    default DoorOpenResult abort(DoorOpenResult reason, long doorUID)
    {
        if (ConfigLoader.DEBUG)
            BigDoors.get().getMyLogger().info("Aborted toggle for door " + doorUID + ". Reason: " + reason.name());
        // If the door was busy, this new attempt should leave that alone.
        if (reason != DoorOpenResult.BUSY)
            BigDoors.get().getCommander().setDoorAvailable(doorUID);
        return reason;
    }

    /**
     * Attempts to get the result stored in a CompletableFuture that contains a DoorOpenResult.
     * </p>
     * Because most steps of the toggle process are performed on the main thread, the resulting CompletableFuture will
     * often contain the actual value.
     * </p>
     * However, in certain circumstances the CompletableFuture may actually include asynchronous steps. When this
     * happens, this method is unable to retrieve the real value.
     *
     * @param result The future result of attempting to toggle a door.
     * @return The DoorOpenResult in the {@link CompletableFuture} if it is available. When it is not (yet) available,
     * {@link DoorOpenResult#ERROR} is returned instead.
     */
    static DoorOpenResult processFutureResult(CompletableFuture<DoorOpenResult> result)
    {
        try
        {
            return result.getNow(DoorOpenResult.ERROR);
        }
        catch (Exception e)
        {
            BigDoors.get().getMyLogger().log("Failed to process DoorOpenResult", e);
            return DoorOpenResult.ERROR;
        }
    }

    /**
     * @deprecated Use {@link #openDoorFuture(Door, double, boolean, boolean, ChunkLoadMode, boolean)} instead.
     * </p>
     * This method may not return to real result; see {@link #processFutureResult(CompletableFuture)}.
     */
    @Deprecated
    default @Nonnull DoorOpenResult openDoor(
        @Nonnull Door door, double time, boolean instantOpen, boolean silent, @Nonnull ChunkLoadMode mode,
        boolean bypassProtectionHooks)
    {
        return processFutureResult(openDoorFuture(door, time, instantOpen, silent, mode, bypassProtectionHooks));
    }

    /**
     * @deprecated Use {@link #openDoorFuture(Door, double, boolean, boolean, ChunkLoadMode)} instead.
     * </p>
     * This method may not return to real result; see {@link #processFutureResult(CompletableFuture)}.
     */
    @Deprecated
    default @Nonnull DoorOpenResult openDoor(
        @Nonnull Door door, double time, boolean instantOpen, boolean silent, @Nonnull ChunkLoadMode mode)
    {
        return openDoor(door, time, instantOpen, silent, mode, false);
    }

    /**
     * @deprecated Use {@link #openDoorFuture(Door, boolean)} instead.
     * </p>
     * This method may not return to real result; see {@link #processFutureResult(CompletableFuture)}.
     */
    @Deprecated
    default @Nonnull DoorOpenResult openDoor(@Nonnull Door door, boolean bypassProtectionHooks)
    {
        return openDoor(door, 0.0, false, false, getChunkLoadMode(door), bypassProtectionHooks);
    }

    /**
     * @deprecated Use {@link #openDoorFuture(Door, double)} instead.
     * </p>
     * This method may not return to real result; see {@link #processFutureResult(CompletableFuture)}.
     */
    @Deprecated
    default @Nonnull DoorOpenResult openDoor(@Nonnull Door door, double time)
    {
        return openDoor(door, time, false, false);
    }

    /**
     * @deprecated Use {@link #openDoorFuture(Door, double, boolean)} instead.
     * </p>
     * This method may not return to real result; see {@link #processFutureResult(CompletableFuture)}.
     */
    @Deprecated
    default @Nonnull DoorOpenResult openDoor(@Nonnull Door door, double time, boolean instantOpen)
    {
        return openDoor(door, time, instantOpen, false);
    }

    /**
     * @deprecated Use {@link #openDoorFuture(Door, double, boolean, boolean)} instead.
     * </p>
     * This method may not return to real result; see {@link #processFutureResult(CompletableFuture)}.
     */
    @Deprecated
    default @Nonnull DoorOpenResult openDoor(@Nonnull Door door, double time, boolean instantOpen, boolean silent)
    {
        return openDoor(door, time, instantOpen, silent, getChunkLoadMode(door));
    }

    /**
     * Gets the number of blocks between this door and the world limit.
     *
     * @param door               The door for which to find the distances to the world limits.
     * @param worldHeightManager The world height manager.
     * @param upDown             Whether to check in the up or down direction.
     * @return The number of blocks to the world limit in either up or down direction.
     * @throws IllegalArgumentException When the provided upDown direction is not either {@link RotateDirection#UP} or
     * {@link RotateDirection#DOWN}.
     */
    default int getDistanceToWorldLimit(Door door, WorldHeightManager worldHeightManager, RotateDirection upDown)
    {
        final WorldHeightLimits worldLimits = worldHeightManager.getWorldHeightLimits(door.getWorld());
        if (upDown.equals(RotateDirection.UP))
            return worldLimits.getUpperLimit() - door.getMaximum().getBlockY();
        else if (upDown.equals(RotateDirection.DOWN))
            return door.getMinimum().getBlockY() - worldLimits.getLowerLimit();
        throw new IllegalArgumentException("Cannot check distance to world limit in direction: " + upDown.name());
    }

    RotateDirection getRotateDirection(Door door);

    default boolean isValidOpenDirection(@Nullable RotateDirection rotateDirection)
    {
        if (rotateDirection == null)
            return false;
        return getValidRotateDirections().contains(rotateDirection);
    }

    default boolean isRotateDirectionValid(@NotNull Door door)
    {
        return isValidOpenDirection(door.getOpenDir());
    }

    /**
     * Retrieves the coordinate pair describing the region the door would move into if it were toggled right now.
     * <p>
     * Note that this will check the blocks in the world. If the server hasn't loaded the required chunk(s), the
     * chunk(s) will be loaded!
     * <p>
     * This method may not always be able to find the future coordinates for the following reasons:
     * <p>
     * 1) The opening direction is specified, but it is obstructed. So there are no new coordinates, because it could
     * not go anywhere.
     * <p>
     * 2) The opening direction is not specified and all possible directions (exact number depends on the type, e.g. 2
     * for doors, 4 for sliding doors) are obstructed. In this case there are no new coordinates, because it cannot go
     * anywhere and because even if it wanted to, it wouldn't know which direction to go.
     * <p>
     * 3) The door's size ({@link #getSizeLimit(Door)}) exceeds {@link Door#getBlockCount()}.
     * <p>
     * 4) The door's {@link Door#getBlocksToMove()} exceeds {@link ConfigLoader#getMaxBlocksToMove()}. This only applies
     * to doors which actually use this (i.e. portcullis, sliding door).
     *
     * @param door The door for which to find the new coordinates.
     * @return The new minimum and maximum coordinates the door would take up if it were toggled now.
     */
    @SuppressWarnings("unused")
    @NotNull Optional<Pair<Location, Location>> getNewCoordinates(@NotNull Door door);

    default int getSizeLimit(final Door door)
    {
        int globalLimit = BigDoors.get().getConfigLoader().maxDoorSize();
        Player player = Bukkit.getPlayer(door.getPlayerUUID());
        int personalLimit = player == null ? -1 : Util.getMaxDoorSizeForPlayer(player);

        return Util.minPositive(personalLimit, globalLimit);
    }

    /**
     * Checks if there aren't any obstructions between two positions.
     *
     * @param doorUID The UID of the door.
     * @param world   The world to check in.
     * @param min     The minimum coordinates.
     * @param max     The maximum coordinates.
     * @return True if all blocks in the region defined by the min/max coordinates do not obstruct doors (e.g. water or
     * air). If any blocks are in the way or if the locations are out of range of the
     * {@link WorldHeightManager#getWorldHeightLimits(World)}, this method will return false.
     */
    default boolean isPosFree(long doorUID, @NotNull World world, @NotNull Location min, @NotNull Location max)
    {
        final WorldHeightLimits worldLimits = BigDoors.get().getWorldHeightManager().getWorldHeightLimits(world);
        if (min.getBlockY() < worldLimits.getLowerLimit() || max.getBlockY() > worldLimits.getUpperLimit())
        {
            BigDoors.get().getMyLogger()
                    .info(String.format("Door %d with y bounds [%d, %d] exceeds world limits: [%d, %d]",
                                        doorUID, min.getBlockY(), max.getBlockY(),
                                        worldLimits.getLowerLimit(), worldLimits.getUpperLimit()));
            return false;
        }

        for (int xAxis = min.getBlockX(); xAxis <= max.getBlockX(); ++xAxis)
            for (int yAxis = min.getBlockY(); yAxis <= max.getBlockY(); ++yAxis)
                for (int zAxis = min.getBlockZ(); zAxis <= max.getBlockZ(); ++zAxis)
                {
                    final Material mat = world.getBlockAt(xAxis, yAxis, zAxis).getType();
                    if (!Util.canOverwriteMaterial(mat))
                    {
                        BigDoors.get().getMyLogger().info("Found a block of material " + mat.name() +
                                                              " in the way when toggling door " + doorUID);
                        return false;
                    }
                }
        return true;
    }

    /**
     * See {@link #isPosFree(long, World, Location, Location)}.
     *
     * @param locations The min and max locations respectively.
     */
    default boolean isPosFree(long doorUID, @NotNull World world, @NotNull Pair<Location, Location> locations)
    {
        return isPosFree(doorUID, world, locations.first, locations.second);
    }

    /**
     * Checks if a player has access to both the new and the old positions of a door.
     * <p>
     * See {@link BigDoors#canBreakBlocksBetweenLocs(UUID, String, World, Location, Location)}.
     *
     * @param door   The door which data to use for check the owner's access to the new and the old locations.
     * @param newMin The new minimum coordinates.
     * @param newMax The new maximum coordinates.
     * @return True if the owner of the door has access to both the current area of the door and the new area of the
     * door.
     */
    default CompletableFuture<Boolean> hasAccessToLocations(
        @NotNull Door door, @NotNull Location newMin, @NotNull Location newMax)
    {
        if (BigDoors.get().getProtectionCompatManager().registeredCompatsCount() == 0)
            return CompletableFuture.completedFuture(true);

        final CompletableFuture<@Nullable String> oldLoc =
            BigDoors.get().canBreakBlocksBetweenLocs(
                door.getPlayerUUID(), door.getPlayerName(), door.getWorld(), door.getMinimum(), door.getMaximum());

        final CompletableFuture<@Nullable String> newLoc =
            BigDoors.get().canBreakBlocksBetweenLocs(
                door.getPlayerUUID(), door.getPlayerName(), door.getWorld(), newMin, newMax);

        return Util.getAllCompletableFutureResults(oldLoc, newLoc).thenApply(
            lst ->
            {
                final @Nullable String canBreakOldBlocks = lst.get(0);
                if (canBreakOldBlocks != null)
                {
                    BigDoors.get().getMyLogger().logMessageToLogFile(
                        "Toggle denied because access to new location was prevented by " + canBreakOldBlocks +
                            " for door: " + door);
                    return false;
                }

                final @Nullable String canBreakNewBlocks = lst.get(1);
                if (canBreakNewBlocks != null)
                {
                    BigDoors.get().getMyLogger().logMessageToLogFile(
                        "Toggle denied because access to old location was prevented by " + canBreakNewBlocks +
                            " for door: " + door);
                    return false;
                }
                return true;
            }).exceptionally(throwable -> Util.exceptionally(throwable, false));
    }

    /**
     * Fires a {@link DoorEventTogglePrepare} for the given door.
     *
     * @param door The door that will be toggled.
     * @return True if the event has been cancelled.
     */
    default boolean fireDoorEventTogglePrepare(final Door door, final boolean instantOpen)
    {
        final ToggleType toggleType = door.isOpen() ? ToggleType.CLOSE : ToggleType.OPEN;
        DoorEventTogglePrepare preparationEvent = new DoorEventTogglePrepare(door, toggleType, instantOpen);
        Bukkit.getPluginManager().callEvent(preparationEvent);
        return preparationEvent.isCancelled();
    }

    /**
     * Fires a {@link DoorEventToggleStart} for the given door.
     *
     * @param door The door that is being toggled.
     */
    default void fireDoorEventToggleStart(final Door door, final boolean instantOpen)
    {
        final ToggleType toggleType = door.isOpen() ? ToggleType.CLOSE : ToggleType.OPEN;
        DoorEventToggleStart startEvent = new DoorEventToggleStart(door, toggleType, instantOpen);
        Bukkit.getPluginManager().callEvent(startEvent);
    }

    /**
     * Retrieves all valid {@link RotateDirection}s for this opener.
     *
     * @return A list with all valid {@link RotateDirection}s for this opener.
     */
    @NotNull List<RotateDirection> getValidRotateDirections();
}
