package nl.pim16aap2.bigDoors.compatibility;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

/**
 * Compatibility hook for GriefPrevention.
 *
 * @see IProtectionCompat
 * @author Pim
 */
class GriefPreventionProtectionCompat implements IProtectionCompat
{
    private final GriefPrevention griefPrevention;
    private boolean success = false;

    public GriefPreventionProtectionCompat(HookContext hookContext)
    {
        final Plugin griefPreventionPlugin =
            Bukkit.getServer().getPluginManager().getPlugin(hookContext.getProtectionCompatDefinition().getName());

        if (!(griefPreventionPlugin instanceof GriefPrevention))
        {
            griefPrevention = null;
            return;
        }
        griefPrevention = (GriefPrevention) griefPreventionPlugin;
        success = true;
    }

    @Override
    public boolean canBreakBlock(Player player, Location loc)
    {
        Block block = loc.getBlock();
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
        return griefPrevention.allowBreak(player, block, loc, blockBreakEvent) == null;
    }

    @Override
    public boolean canBreakBlocksBetweenLocs(Player player, Location loc1, Location loc2)
    {
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        for (int xPos = x1; xPos <= x2; ++xPos)
            for (int yPos = y1; yPos <= y2; ++yPos)
                for (int zPos = z1; zPos <= z2; ++zPos)
                    if (!canBreakBlock(player, new Location(loc1.getWorld(), xPos, yPos, zPos)))
                        return false;
        return true;
    }

    @Override
    public boolean success()
    {
        return success;
    }

    @Override
    public String getName()
    {
        return griefPrevention.getName();
    }
}
