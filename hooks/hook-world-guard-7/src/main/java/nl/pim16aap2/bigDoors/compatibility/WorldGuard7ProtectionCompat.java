package nl.pim16aap2.bigDoors.compatibility;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Compatibility hook for version 7 of WorldGuard.
 *
 * @see IProtectionCompat
 * @author Pim
 */
class WorldGuard7ProtectionCompat implements IProtectionCompat
{
    private static final StateFlag[] FLAGS = new StateFlag[]{Flags.BLOCK_BREAK, Flags.BLOCK_PLACE, Flags.BUILD};

    private final WorldGuard worldGuard;
    private final WorldGuardPlugin worldGuardPlugin;

    private boolean success = false;

    public WorldGuard7ProtectionCompat(HookContext hookContext)
    {
        worldGuard = WorldGuard.getInstance();

        final @Nullable Plugin wgPlugin =
            Bukkit.getServer().getPluginManager().getPlugin(hookContext.getProtectionCompatDefinition().getName());

        if (!(wgPlugin instanceof WorldGuardPlugin))
        {
            worldGuardPlugin = null;
            return;
        }
        worldGuardPlugin = (WorldGuardPlugin) wgPlugin;
        success = true;
    }

    private boolean enabledInWorld(com.sk89q.worldedit.world.World world)
    {
        final @Nullable RegionManager regionManager = worldGuard.getPlatform().getRegionContainer().get(world);
        return regionManager != null && regionManager.size() > 0;
    }

    private com.sk89q.worldedit.world.World toWorldGuardWorld(@Nullable World world)
    {
        return BukkitAdapter.adapt(Objects.requireNonNull(world, "World cannot be null!"));
    }

    private RegionAssociable regionAssociableFromPlayer(Player player)
    {
        if (Bukkit.getPlayer(player.getUniqueId()) == null)
            return worldGuardPlugin.wrapOfflinePlayer(player);
        else
            return new BukkitPlayer(worldGuardPlugin, player);
    }

    @Override
    public boolean canBreakBlock(Player player, Location loc)
    {
        if (!enabledInWorld(toWorldGuardWorld(loc.getWorld())))
            return true;

        final RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        final com.sk89q.worldedit.world.World world = toWorldGuardWorld(loc.getWorld());
        final com.sk89q.worldedit.util.Location wgLoc =
            new com.sk89q.worldedit.util.Location(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        return query.testState(wgLoc, regionAssociableFromPlayer(player), FLAGS);
    }

    @Override
    public boolean canBreakBlocksBetweenLocs(Player player, Location loc1, Location loc2)
    {
        final com.sk89q.worldedit.world.World world = toWorldGuardWorld(loc1.getWorld());
        if (!enabledInWorld(world))
            return true;

        final int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        final int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        final int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        final int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        final int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        final int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        final RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        final RegionAssociable regionAssociable = regionAssociableFromPlayer(player);

        for (int xPos = x1; xPos <= x2; ++xPos)
            for (int yPos = y1; yPos <= y2; ++yPos)
                for (int zPos = z1; zPos <= z2; ++zPos)
                {
                    final com.sk89q.worldedit.util.Location wgLoc =
                        new com.sk89q.worldedit.util.Location(world, xPos, yPos, zPos);

                    if (!query.testState(wgLoc, regionAssociable, FLAGS))
                        return false;
                }
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
        return worldGuardPlugin.getName();
    }
}
