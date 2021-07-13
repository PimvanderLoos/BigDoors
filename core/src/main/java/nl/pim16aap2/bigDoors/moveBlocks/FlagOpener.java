package nl.pim16aap2.bigDoors.moveBlocks;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.util.ChunkUtils.ChunkLoadMode;
import nl.pim16aap2.bigDoors.util.ChunkUtils.ChunkLoadResult;
import nl.pim16aap2.bigDoors.util.DoorOpenResult;
import nl.pim16aap2.bigDoors.util.Pair;
import nl.pim16aap2.bigDoors.util.RotateDirection;
import nl.pim16aap2.bigDoors.util.Vector2D;

import java.util.logging.Level;

public class FlagOpener implements Opener
{
    private final BigDoors plugin;

    public FlagOpener(BigDoors plugin)
    {
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Vector2D, Vector2D> getChunkRange(Door door)
    {
        return getCurrentChunkRange(door);
    }

    @Override
    public boolean isRotateDirectionValid(Door door)
    {
        return door.getOpenDir().equals(RotateDirection.NORTH) || door.getOpenDir().equals(RotateDirection.EAST) ||
            door.getOpenDir().equals(RotateDirection.SOUTH) || door.getOpenDir().equals(RotateDirection.WEST);
    }

    @Override
    public RotateDirection getRotateDirection(Door door)
    {
        if (isRotateDirectionValid(door))
            return door.getOpenDir();
        return door.getMinimum().getBlockX() == door.getMaximum().getBlockX() ? RotateDirection.NORTH :
               RotateDirection.EAST;
    }

    @Override
    public DoorOpenResult openDoor(Door door, double time)
    {
        return openDoor(door, time, false, false);
    }

    // Open a door.
    @Override
    public DoorOpenResult openDoor(Door door, double time, boolean instantOpen, boolean silent, ChunkLoadMode mode)
    {
        if (!plugin.getCommander().canGo())
        {
            plugin.getMyLogger()
                  .info("Failed to toggle: " + door.toSimpleString() + ", as door toggles are currently disabled!");
            return abort(DoorOpenResult.ERROR, door.getDoorUID());
        }

        if (plugin.getCommander().isDoorBusyRegisterIfNot(door.getDoorUID()))
        {
            if (!silent)
                plugin.getMyLogger()
                      .myLogger(Level.INFO, "Flag " + door.toSimpleString() + " is not available right now!");
            return abort(DoorOpenResult.BUSY, door.getDoorUID());
        }

        final ChunkLoadResult chunkLoadResult = chunksLoaded(door, mode);
        if (chunkLoadResult == ChunkLoadResult.FAIL)
        {
            plugin.getMyLogger()
                  .logMessage("Chunks for flag " + door.toSimpleString() + " are not loaded!", true, false);
            return abort(DoorOpenResult.CHUNKSNOTLOADED, door.getDoorUID());
        }
        if (chunkLoadResult == ChunkLoadResult.REQUIRED_LOAD)
            instantOpen = true;

        // Make sure the doorSize does not exceed the total doorSize.
        // If it does, open the door instantly.
        int maxDoorSize = getSizeLimit(door);
        if (maxDoorSize > 0 && door.getBlockCount() > maxDoorSize)
        {
            plugin.getMyLogger().logMessage("Flag " + door.toSimpleString() + " Exceeds the size limit: " + maxDoorSize,
                                            true, false);
            return abort(DoorOpenResult.ERROR, door.getDoorUID());
        }

        // The door's owner does not have permission to move the door into the new
        // position (e.g. worldguard doens't allow it.
        if (plugin.canBreakBlocksBetweenLocs(door.getPlayerUUID(), door.getPlayerName(), door.getWorld(),
                                             door.getMinimum(), door.getMinimum()) != null)
            return abort(DoorOpenResult.NOPERMISSION, door.getDoorUID());

        if (!isRotateDirectionValid(door))
        {
            RotateDirection rotDir = getRotateDirection(door);
            plugin.getMyLogger()
                  .logMessage("Updating openDirection of flag " + door.toSimpleString() + " to " + rotDir.name()
                                  + ". If this is undesired, change it via the GUI.", true, false);
            plugin.getCommander().updateDoorOpenDirection(door.getDoorUID(), rotDir);
        }

//        // THIS TYPE IS NOT ENABLED!
//        if (fireDoorEventTogglePrepare(door, false))
//            return DoorOpenResult.CANCELLED;
//
//        // Change door availability so it cannot be opened again (just temporarily, don't worry!).
//        plugin.getCommander().setDoorBusy(door.getDoorUID());
//
//        plugin.getCommander().addBlockMover(new FlagMover(plugin, door.getWorld(), 60, door));
//        fireDoorEventToggleStart(door, false);

        return abort(DoorOpenResult.SUCCESS, door.getDoorUID());
    }
}