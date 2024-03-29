package nl.pim16aap2.bigDoors.compatibility;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Compatibility hook for version 6 of WorldGuard.
 *
 * @see IProtectionCompat
 * @author Pim
 */
class WorldGuard6ProtectionCompat implements IProtectionCompat
{
    private final WorldGuardPlugin worldGuard;
    private boolean success = false;

    public WorldGuard6ProtectionCompat(HookContext hookContext)
    {
        final @Nullable Plugin wgPlugin =
            Bukkit.getServer().getPluginManager().getPlugin(hookContext.getProtectionCompatDefinition().getName());

        if (!(wgPlugin instanceof WorldGuardPlugin))
        {
            worldGuard = null;
            return;
        }

        worldGuard = (WorldGuardPlugin) wgPlugin;
        success = true;
    }

    @Override
    public boolean canBreakBlock(Player player, Location loc)
    {
        return worldGuard.canBuild(player, loc);
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
        return worldGuard.getName();
    }
}
