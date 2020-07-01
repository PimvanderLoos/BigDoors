package nl.pim16aap2.bigDoors.moveBlocks;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.util.DoorOpenResult;
import nl.pim16aap2.bigDoors.util.RotateDirection;
import nl.pim16aap2.bigDoors.util.Util;

public class PortcullisOpener implements Opener
{
    private final BigDoors plugin;

    public PortcullisOpener(BigDoors plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean isRotateDirectionValid(Door door)
    {
        return door.getOpenDir().equals(RotateDirection.UP) || door.getOpenDir().equals(RotateDirection.DOWN);
    }

    @Override
    public RotateDirection getRotateDirection(Door door)
    {
        if (isRotateDirectionValid(door))
            return door.getOpenDir();
        return RotateDirection.UP;
    }

    // Check if the chunks at the minimum and maximum locations of the door are
    // loaded.
    private boolean chunksLoaded(Door door)
    {
        // Return true if the chunk at the max and at the min of the chunks were loaded
        // correctly.
        if (door.getWorld() == null)
            plugin.getMyLogger().logMessage("World is null for door \"" + door.getName().toString() + "\"", true,
                                            false);
        if (door.getWorld().getChunkAt(door.getMaximum()) == null)
            plugin.getMyLogger().logMessage("Chunk at maximum for door \"" + door.getName().toString() + "\" is null!",
                                            true, false);
        if (door.getWorld().getChunkAt(door.getMinimum()) == null)
            plugin.getMyLogger().logMessage("Chunk at minimum for door \"" + door.getName().toString() + "\" is null!",
                                            true, false);

        return door.getWorld().getChunkAt(door.getMaximum()).load() &&
               door.getWorld().getChunkAt(door.getMinimum()).isLoaded();
    }

    @Override
    public DoorOpenResult shadowToggle(Door door)
    {
        if (plugin.getCommander().isDoorBusy(door.getDoorUID()))
        {
            plugin.getMyLogger().myLogger(Level.INFO, "Door " + door.getName() + " is not available right now!");
            return DoorOpenResult.BUSY;
        }
        if (door.getOpenDir().equals(RotateDirection.NONE))
        {
            plugin.getMyLogger().myLogger(Level.INFO, "Door " + door.getName() + " has no open direction!");
            return DoorOpenResult.NODIRECTION;
        }

        // For the blocks to shadow move the door, prefer to use the BTM variable, but
        // use the height if that's unavailable.
        int moved = door.getBlocksToMove() > 0 ? door.getBlocksToMove() :
            (door.getMaximum().getBlockY() - door.getMinimum().getBlockY() + 1);
        int multiplier = door.getOpenDir().equals(RotateDirection.UP) ? 1 : -1;
        multiplier *= door.isOpen() ? -1 : 1; // The portcullis type is closed by default.

        VerticalMover.updateCoords(door, null, null, moved * multiplier, true);
        return DoorOpenResult.SUCCESS;
    }

    @Override
    public DoorOpenResult openDoor(Door door, double time)
    {
        return openDoor(door, time, false, false);
    }

    // Open a door.
    @Override
    public DoorOpenResult openDoor(Door door, double time, boolean instantOpen, boolean silent)
    {
        if (plugin.getCommander().isDoorBusy(door.getDoorUID()))
        {
            if (!silent)
                plugin.getMyLogger().myLogger(Level.INFO, "Door " + door.getName() + " is not available right now!");
            return DoorOpenResult.BUSY;
        }

        if (!chunksLoaded(door))
        {
            plugin.getMyLogger().logMessage(ChatColor.RED + "Chunk for door " + door.getName() + " is not loaded!",
                                            true, false);
            return DoorOpenResult.ERROR;
        }

        // Make sure the doorSize does not exceed the total doorSize.
        // If it does, open the door instantly.
        int maxDoorSize = getSizeLimit(door);
        if (maxDoorSize > 0 && door.getBlockCount() > maxDoorSize)
        {
            plugin.getMyLogger().logMessage("Door \"" + door.getDoorUID() + "\" Exceeds the size limit: " + maxDoorSize,
                                            true, false);
            return DoorOpenResult.ERROR;
        }

        int blocksToMove = getBlocksToMove(door);

        // The door's owner does not have permission to move the door into the new
        // position (e.g. worldguard doens't allow it.
        if (plugin.canBreakBlocksBetweenLocs(door.getPlayerUUID(), door.getPlayerName(), door.getWorld(), 
                                             door.getNewMin(), door.getNewMax()) != null ||
            plugin.canBreakBlocksBetweenLocs(door.getPlayerUUID(), door.getPlayerName(), door.getWorld(), 
                                             door.getMinimum(), door.getMinimum()) != null)
            return DoorOpenResult.NOPERMISSION;

        if (blocksToMove != 0)
        {
            if (!isRotateDirectionValid(door))
            {
                RotateDirection openDirection = door.isOpen() ?
                    (blocksToMove > 0 ? RotateDirection.DOWN : RotateDirection.UP) :
                    (blocksToMove > 0 ? RotateDirection.UP : RotateDirection.DOWN);
                plugin.getMyLogger().logMessage("Updating openDirection of portcullis " + door.getName() + " to "
                    + openDirection.name() + ". If this is undesired, change it via the GUI.", true, false);
                plugin.getCommander().updateDoorOpenDirection(door.getDoorUID(), openDirection);
            }

            if (fireDoorEventTogglePrepare(door, instantOpen))
                return DoorOpenResult.CANCELLED;

            // Change door availability so it cannot be opened again (just temporarily,
            // don't worry!).
            plugin.getCommander().setDoorBusy(door.getDoorUID());
            plugin.getCommander()
                .addBlockMover(new VerticalMover(plugin, door.getWorld(), time, door, instantOpen, blocksToMove,
                                                 plugin.getConfigLoader().pcMultiplier()));
            fireDoorEventToggleStart(door, instantOpen);
        }
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
//        int distanceToCheck = door.getOpenDir() == RotateDirection.NONE || door.getBlocksToMove() < 1 ? yLen : door.getBlocksToMove();
        int distanceToCheck = door.getBlocksToMove() < 1 ? yLen : door.getBlocksToMove();

        int xAxis, yAxis, zAxis, yGoal;
        World world = door.getWorld();
        delta = upDown == RotateDirection.DOWN ? -1 : 1;
        yAxis = upDown == RotateDirection.DOWN ? yMin - 1 : yMax + 1;
        yGoal = upDown == RotateDirection.DOWN ? yMin - distanceToCheck - 1 : yMax + distanceToCheck + 1;

        while (yAxis != yGoal)
        {
            for (xAxis = xMin; xAxis <= xMax; ++xAxis)
                for (zAxis = zMin; zAxis <= zMax; ++zAxis)
                    if (!Util.isAirOrWater(world.getBlockAt(xAxis, yAxis, zAxis).getType()))
                        return blocksUp;
            yAxis += delta;
            blocksUp += delta;
        }
        return blocksUp;
    }

    private int getBlocksToMove(Door door)
    {
        int blocksUp = getBlocksInDir(door, RotateDirection.UP);
        int blocksDown = getBlocksInDir(door, RotateDirection.DOWN);
        int blocksToMove = blocksUp > -1 * blocksDown ? blocksUp : blocksDown;
        door.setNewMin(new Location(door.getWorld(), door.getMinimum().getBlockX(),
                                    door.getMinimum().getBlockY() + blocksToMove, door.getMinimum().getBlockZ()));
        door.setNewMax(new Location(door.getWorld(), door.getMaximum().getBlockX(),
                                    door.getMaximum().getBlockY() + blocksToMove, door.getMaximum().getBlockZ()));
        return blocksToMove;
    }
}
