package nl.pim16aap2.bigDoors.moveBlocks;

import com.cryptomorin.xseries.XMaterial;
import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.events.DoorEventAutoToggle;
import nl.pim16aap2.bigDoors.events.DoorEventToggleEnd;
import nl.pim16aap2.bigDoors.util.MyBlockData;
import nl.pim16aap2.bigDoors.util.MyBlockFace;
import nl.pim16aap2.bigDoors.util.Util;
import nl.pim16aap2.bigDoors.util.Vector3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static nl.pim16aap2.bigDoors.events.DoorEventToggle.ToggleType;

public abstract class BlockMover
{
    private final BigDoors plugin;
    private final @Nullable Door door;
    private final List<MyBlockData> savedBlocks = new CopyOnWriteArrayList<>();
    private final List<MyBlockData> publicSavedBlocks = Collections.unmodifiableList(savedBlocks);

    protected final boolean instantOpen;
    protected final AtomicBoolean blocksPlaced = new AtomicBoolean(false);

    protected BlockMover(BigDoors plugin, @Nullable Door door, boolean instantOpen)
    {
        this.plugin = plugin;
        this.door = door;
        this.instantOpen = instantOpen;

        if (door == null)
            return;

        plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
            "Created new BlockMover; instantOpen: %s",
            instantOpen
        ));

        plugin.getAutoCloseScheduler().cancelTimer(door.getDoorUID());
        preprocess();
    }

    /**
     * Add saved blocks to the list of saved blocks.
     * <p>
     * After this method has been called, the list of saved blocks returned by {@link #getSavedBlocks()} will contain
     * all the blocks from the provided collection.
     *
     * @param blocks The blocks to register.
     */
    protected void registerSavedBlocks(Collection<MyBlockData> blocks)
    {
        savedBlocks.addAll(blocks);
    }

    private void preprocess()
    {
        if (this.door == null)
            return;

        final Location powerBlockLoc = door.getPowerBlockLoc();
        final Location min = door.getMinimum();
        final Location max = door.getMaximum();

        if (!Util.isPosInCuboid(powerBlockLoc, min.clone().add(-1, -1, -1), max.clone().add(1, 1, 1)))
            return;

        plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
            "Preprocessing blocks; Powerblock outside door; powerBlockLoc: %s, min: %s, max: %s",
            powerBlockLoc, min, max
        ));

        for (MyBlockFace blockFace : MyBlockFace.getValues())
        {
            final Vector3D vec = MyBlockFace.getDirection(blockFace);
            final Location newLocation = powerBlockLoc.clone().add(vec.getX(), vec.getY(), vec.getZ());

            if (!Util.isPosInCuboid(newLocation, min, max))
                continue;

            final Block block = newLocation.getBlock();
            if (block.getPistonMoveReaction() == PistonMoveReaction.BREAK)
            {
                plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
                    "Preprocessing blocks; Block at [%d, %d, %d] is a piston; breaking it",
                    newLocation.getBlockX(), newLocation.getBlockY(), newLocation.getBlockZ()
                ));
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, block::breakNaturally, 1L);
            }
        }

        for (Entity entity : powerBlockLoc.getWorld().getNearbyEntities(powerBlockLoc, 1.1, 1.1, 1.1))
        {
            if (entity instanceof ItemFrame && Util.isPosInCuboid(entity.getLocation(), min, max))
            {
                final ItemFrame itemFrame = (ItemFrame) entity;
                powerBlockLoc.getWorld().dropItemNaturally(itemFrame.getLocation(), itemFrame.getItem());
                powerBlockLoc.getWorld().dropItemNaturally(itemFrame.getLocation(), XMaterial.ITEM_FRAME.parseItem());
                itemFrame.remove();
                plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
                    "Preprocessing blocks; Removed item frame at [%d, %d, %d]",
                    itemFrame.getLocation().getBlockX(), itemFrame.getLocation().getBlockY(), itemFrame.getLocation().getBlockZ()
                ));
            }
        }
    }

    // Put blocks in their final position.
    // Use onDisable = false to make it safe to use during onDisable().
    public final synchronized void putBlocks(boolean onDisable)
    {
        plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
            "Putting blocks; onDisable: %s",
            onDisable
        ));
        try
        {
            putBlocks0(onDisable);
        }
        catch (Exception e)
        {
            plugin.getMyLogger().log("Error while putting blocks for door: " + getDoorUID(), e);
        }
    }

    protected abstract void putBlocks0(boolean onDisable);

    public abstract long getDoorUID();

    public abstract Door getDoor();

    public final synchronized void cancel(boolean onDisable)
    {
        plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
            "Cancelling animation; onDisable: %s",
            onDisable
        ));
        try
        {
            cancel0(onDisable);
        }
        catch (Exception e)
        {
            plugin.getMyLogger().log("Error while cancelling animation for door: " + getDoorUID(), e);
        }
    }

    protected abstract void cancel0(boolean onDisable);

    /**
     * Gets the number of ticks the door should to the delay to make sure the second
     * toggle of a button doesn't toggle the door again.
     *
     * @param endCount The number of ticks the animation took.
     * @return The number of ticks to wait before a button cannot toggle the door again.
     */
    public int buttonDelay(final int endCount)
    {
        return Math.max(0, 17 - endCount);
    }

    protected synchronized void putBlocks(
        boolean onDisable, double time, int endCount, LocationFinder locationUpdater, Runnable coordinateUpdater)
    {
        plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
            "Putting blocks; onDisable: %b, time: %.2f, endCount: %d, blocksPlaced: %b, instantOpen: %b, locationUpdater: %s",
            onDisable, time, endCount, blocksPlaced.get(), instantOpen, (locationUpdater == null ? "null" : locationUpdater.getClass().getName())
        ));
        if (blocksPlaced.getAndSet(true))
            return;

        final World world = Objects.requireNonNull(door).getWorld();

        final DoorRegion oldRegion = new DoorRegion();
        final DoorRegion newRegion = new DoorRegion();

        for (MyBlockData savedBlock : savedBlocks)
        {
            Material mat = savedBlock.getMat();
            byte matByte = savedBlock.getBlockByte();
            Location newPos = locationUpdater.apply(savedBlock.getRadius(), savedBlock.getStartX(),
                                                    savedBlock.getStartY(), savedBlock.getStartZ());

            oldRegion.processLocation(savedBlock.getStartX(), savedBlock.getStartY(), savedBlock.getStartZ());
            newRegion.processLocation(newPos);

            if (!instantOpen)
                savedBlock.getFBlock().privateRemove();

            if (!savedBlock.getMat().equals(Material.AIR))
            {
                if (BigDoors.isOnFlattenedVersion())
                {
                    savedBlock.getBlock().putBlock(newPos);
                    Block b = world.getBlockAt(newPos);
                    BlockState bs = b.getState();
                    bs.update();
                }
                else
                {
                    Block b = world.getBlockAt(newPos);
                    MaterialData matData = savedBlock.getMatData();
                    matData.setData(matByte);

                    b.setType(mat);
                    BlockState bs = b.getState();
                    bs.setData(matData);
                    bs.update();
                }
            }
        }

        plugin.getMyLogger().logMessageToLogFileForDoor(door, String.format(
            "Moved blocks from    %s to %s",
            oldRegion, newRegion
        ));

        coordinateUpdater.run();
        toggleOpen(door);

        if (onDisable)
            return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                plugin.getCommander().setDoorAvailable(door.getDoorUID());

                final ToggleType toggleType = door.isOpen() ? ToggleType.OPEN : ToggleType.CLOSE;
                Bukkit.getPluginManager().callEvent(new DoorEventToggleEnd(door, toggleType, instantOpen));

                if (canAutoToggle(door) && plugin.getAutoCloseScheduler().autoCloseAllowed())
                    plugin.getAutoCloseScheduler().scheduleAutoClose(door, time, instantOpen);
            }
        }.runTaskLater(plugin, getDelay(endCount));
    }

    private int getDelay(int endCount)
    {
        return buttonDelay(endCount) + Math.min(plugin.getMinimumDoorDelay(),
                                                plugin.getConfigLoader().coolDown() * 20);
    }

    private boolean canAutoToggle(Door door)
    {
        if (!door.isOpen() || door.getAutoClose() <= 0)
            return false;

        final DoorEventAutoToggle autoToggleEvent = new DoorEventAutoToggle(door);
        Bukkit.getPluginManager().callEvent(autoToggleEvent);
        if (autoToggleEvent.isCancelled())
            BigDoors.get().getMyLogger().logMessageToLogFileForDoor(
                door,
                "AutoToggle cancelled by event! Registered event Listeners:\n" +
                    Util.getFormattedEventListeners(autoToggleEvent));
        return !autoToggleEvent.isCancelled();
    }

    protected void toggleOpen(Door door)
    {
        door.setOpenStatus(!door.isOpen());
    }

    /**
     * @return An unmodifiable list of the 'saved blocks' (i.e. the animated blocks).
     * This list will be empty until the animated blocks have been created.
     */
    @SuppressWarnings("unused")
    public final List<MyBlockData> getSavedBlocks()
    {
        return publicSavedBlocks;
    }

    @FunctionalInterface
    public interface LocationFinder
    {
        Location apply(double radius, double startX, double startY, double startZ);
    }

    private static final class DoorRegion
    {
        private int minX = Integer.MAX_VALUE;
        private int minY = Integer.MAX_VALUE;
        private int minZ = Integer.MAX_VALUE;

        private int maxX = Integer.MIN_VALUE;
        private int maxY = Integer.MIN_VALUE;
        private int maxZ = Integer.MIN_VALUE;

        public void processLocation(int x, int y, int z)
        {
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);

            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }

        public void processLocation(double x, double y, double z)
        {
            processLocation((int) x, (int) y, (int) z);
        }

        public void processLocation(Location loc)
        {
            processLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        }

        @Override
        public String toString()
        {
            return String.format(
                "[%d, %d, %d] - [%d, %d, %d]",
                minX, minY, minZ, maxX, maxY, maxZ);
        }
    }
}
