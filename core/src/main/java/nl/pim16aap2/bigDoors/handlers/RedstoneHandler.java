package nl.pim16aap2.bigDoors.handlers;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.util.ConfigLoader;
import nl.pim16aap2.bigDoors.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RedstoneHandler implements Listener
{
    private static final List<BlockFace> FACES = Collections.unmodifiableList(
        Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN));

    private final BigDoors plugin;

    public RedstoneHandler(BigDoors plugin)
    {
        this.plugin = plugin;
    }

    private void checkDoor(Location loc)
    {
        final Door door = plugin.getCommander().doorFromPowerBlockLoc(loc);
        if (door != null && !door.isLocked())
            plugin.getDoorOpener(door.getType()).openDoorFuture(door, 0.0, false, true).exceptionally(Util::exceptionally);
    }

    private void checkAroundLocation(Block block)
    {
        for (final BlockFace dir : FACES)
        {
            final Block relative = block.getRelative(dir);
            if (plugin.getConfigLoader().getPowerBlockTypes().contains(relative.getType()))
                checkDoor(relative.getLocation());
        }
    }

    private void checkAroundLocation(Location location)
    {
        checkAroundLocation(location.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockRedstoneChange(BlockRedstoneEvent event)
    {
        try
        {
            final Block block = event.getBlock();
            if (event.getOldCurrent() != 0 && event.getNewCurrent() != 0)
                return;
            checkAroundLocation(block);
        }
        catch (Exception e)
        {
            plugin.getMyLogger().logMessage("Exception thrown while handling redstone event!", true, false);
            plugin.getMyLogger().logMessageToLogFile(Util.throwableToString(e));
            if (ConfigLoader.DEBUG)
                e.printStackTrace();
        }
    }

    private void handleBlocksMovedByPiston(BlockFace direction, List<Block> blocks)
    {
        final Set<Location> addedPositions = new HashSet<>();
        final Set<Location> removedPositions = new HashSet<>();

        for (final Block block : blocks)
        {
            if (block.getType() != Material.REDSTONE_BLOCK)
                continue;

            final Location oldLocation = block.getLocation();
            final Block newBlock = block.getRelative(direction);
            final Location newLocation = newBlock.getLocation();

            // Nothing changes if another redstone block replaces the current one.
            if (addedPositions.contains(oldLocation))
                addedPositions.remove(oldLocation);
            else
                removedPositions.add(oldLocation);

            // Nothing happens if the current redstone block replaces another one.
            if (removedPositions.contains(newLocation))
                removedPositions.remove(newLocation);
            else
                addedPositions.add(newLocation);
        }

        addedPositions.forEach(this::checkAroundLocation);
        removedPositions.forEach(this::checkAroundLocation);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent event)
    {
        handleBlocksMovedByPiston(event.getDirection(), event.getBlocks());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonRetract(BlockPistonRetractEvent event)
    {
        handleBlocksMovedByPiston(event.getDirection(), event.getBlocks());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        final Block block = event.getBlock();
        if (plugin.getConfigLoader().getPowerBlockTypes().contains(block.getType()))
            checkAroundLocation(block);
    }
}
