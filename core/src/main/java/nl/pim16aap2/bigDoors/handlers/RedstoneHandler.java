package nl.pim16aap2.bigDoors.handlers;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.util.ConfigLoader;
import nl.pim16aap2.bigDoors.util.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    @EventHandler
    public void onBlockRedstoneChange(BlockRedstoneEvent event)
    {
        try
        {
            final Block block = event.getBlock();
            if (event.getOldCurrent() != 0 && event.getNewCurrent() != 0)
                return;

            for (final BlockFace dir : FACES)
            {
                final Block relative = block.getRelative(dir);
                if (plugin.getConfigLoader().getPowerBlockTypes().contains(relative.getType()))
                    checkDoor(relative.getLocation());
            }
        }
        catch (Exception e)
        {
            plugin.getMyLogger().logMessage("Exception thrown while handling redstone event!", true, false);
            plugin.getMyLogger().logMessageToLogFile(Util.throwableToString(e));
            if (ConfigLoader.DEBUG)
                e.printStackTrace();
        }
    }
}
