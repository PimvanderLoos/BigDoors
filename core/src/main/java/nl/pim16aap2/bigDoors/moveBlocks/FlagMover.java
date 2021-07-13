package nl.pim16aap2.bigDoors.moveBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.NMS.CustomCraftFallingBlock;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory;
import nl.pim16aap2.bigDoors.NMS.NMSBlock;
import nl.pim16aap2.bigDoors.events.DoorEventToggle.ToggleType;
import nl.pim16aap2.bigDoors.events.DoorEventToggleEnd;
import nl.pim16aap2.bigDoors.util.MyBlockData;
import nl.pim16aap2.bigDoors.util.Util;

public class FlagMover implements BlockMover
{
    private boolean NS;
    private FallingBlockFactory fabf;
    private Door door;
    private double time;
    private World world;
    private BigDoors plugin;
    private int tickRate;
    private boolean instantOpen;
    private int xMin, xMax, yMin;
    private int yMax, zMin, zMax;
    private List<MyBlockData> savedBlocks = new ArrayList<>();
    private final AtomicBoolean blocksPlaced = new AtomicBoolean(false);
    private int endCount;
    private BukkitRunnable animationRunnable;

    @SuppressWarnings("deprecation")
    public FlagMover(BigDoors plugin, World world, double time, Door door)
    {
        plugin.getAutoCloseScheduler().cancelTimer(door.getDoorUID());
        this.plugin = plugin;
        this.world = world;
        this.door = door;
        fabf = plugin.getFABF();

        xMin = door.getMinimum().getBlockX();
        yMin = door.getMinimum().getBlockY();
        zMin = door.getMinimum().getBlockZ();
        xMax = door.getMaximum().getBlockX();
        yMax = door.getMaximum().getBlockY();
        zMax = door.getMaximum().getBlockZ();
        int xLen = Math.abs(xMax - xMin) + 1;
        int zLen = Math.abs(zMax - zMin) + 1;
        NS = zLen > xLen ? true : false;

        double speed = 1;
        this.time = time;
        tickRate = Util.tickRateFromSpeed(speed);
        tickRate = 3;

        int index = 0;
        int yAxis = yMin;
        do
        {
            int zAxis = zMin;
            do
            {
                for (int xAxis = xMin; xAxis <= xMax; xAxis++)
                {
                    Location startLocation = new Location(world, xAxis + 0.5, yAxis, zAxis + 0.5);
                    Location newFBlockLocation = new Location(world, xAxis + 0.5, yAxis - 0.020, zAxis + 0.5);
                    // Move the lowest blocks up a little, so the client won't predict they're
                    // touching through the ground, which would make them slower than the rest.
                    if (yAxis == yMin)
                        newFBlockLocation.setY(newFBlockLocation.getY() + .010001);
                    Block vBlock = world.getBlockAt(xAxis, yAxis, zAxis);
                    Material mat = vBlock.getType();
                    if (!Util.isAirOrWater(mat) && Util.isAllowedBlock(mat))
                    {
                        Byte matData = vBlock.getData();
                        BlockState bs = vBlock.getState();
                        MaterialData materialData = bs.getData();
                        NMSBlock block = fabf.nmsBlockFactory(world, xAxis, yAxis, zAxis);

                        vBlock.setType(Material.AIR);

                        CustomCraftFallingBlock fBlock = null;
                        if (!instantOpen)
                            fBlock = fabf.fallingBlockFactory(newFBlockLocation, block, matData, mat);
                        savedBlocks
                            .add(index,
                                 new MyBlockData(mat, matData, fBlock, 0, materialData, block, 0, startLocation));
                    }
                    else
                        savedBlocks.add(index, new MyBlockData(Material.AIR));
                    ++index;
                }
                ++zAxis;
            }
            while (zAxis <= zMax);
            ++yAxis;
        }
        while (yAxis <= yMax);

        if (!instantOpen)
            rotateEntities();
        else
            putBlocks(false);
    }
    
    @Override
    public synchronized void cancel(boolean onDisable)
    {
        if (this.animationRunnable == null)
            return;
        this.animationRunnable.cancel();
        this.putBlocks(onDisable);
    }

    // Put the door blocks back, but change their state now.
    @SuppressWarnings("deprecation")
    @Override
    public synchronized void putBlocks(boolean onDisable)
    {
        if (blocksPlaced.getAndSet(true))
            return;        
        
        int index = 0;
        double yAxis = yMin;
        do
        {
            double zAxis = zMin;
            do
            {
                for (int xAxis = xMin; xAxis <= xMax; ++xAxis)
                {
                    Material mat = savedBlocks.get(index).getMat();
                    if (!mat.equals(Material.AIR))
                    {
                        Byte matByte = savedBlocks.get(index).getBlockByte();
                        Location newPos = getNewLocation(xAxis, yAxis, zAxis);

                        if (!instantOpen)
                            savedBlocks.get(index).getFBlock().remove();

                        if (!savedBlocks.get(index).getMat().equals(Material.AIR))
                            if (plugin.is1_13())
                            {
                                savedBlocks.get(index).getBlock().putBlock(newPos);

                                Block b = world.getBlockAt(newPos);
                                BlockState bs = b.getState();
                                bs.update();
                            }
                            else
                            {
                                Block b = world.getBlockAt(newPos);
                                MaterialData matData = savedBlocks.get(index).getMatData();
                                matData.setData(matByte);

                                b.setType(mat);
                                BlockState bs = b.getState();
                                bs.setData(matData);
                                bs.update();
                            }
                    }
                    ++index;
                }
                ++zAxis;
            }
            while (zAxis <= zMax);
            ++yAxis;
        }
        while (yAxis <= yMax);
        savedBlocks.clear();

        if (!onDisable)
        {
            int delay = buttonDelay(endCount)
                + Math.min(plugin.getMinimumDoorDelay(), plugin.getConfigLoader().coolDown() * 20);
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    plugin.getCommander().setDoorAvailable(door.getDoorUID());
                    Bukkit.getPluginManager()
                        .callEvent(new DoorEventToggleEnd(door, (door.isOpen() ? ToggleType.OPEN : ToggleType.CLOSE),
                                                          instantOpen));

                    if (door.isOpen())
                        plugin.getAutoCloseScheduler().scheduleAutoClose(door, time, instantOpen);
                }
            }.runTaskLater(plugin, delay);
        }
    }

    private Location getNewLocation(double xAxis, double yAxis, double zAxis)
    {
        return new Location(world, xAxis, yAxis, zAxis);
    }

    // Method that takes care of the rotation aspect.
    private void rotateEntities()
    {
        endCount = (int) (20.0f / tickRate * time);
        
        animationRunnable = new BukkitRunnable()
        {
            double counter = 0;
            int totalTicks = (int) (endCount * 1.1);
            long startTime = System.nanoTime();
            long lastTime;
            long currentTime = System.nanoTime();

            @Override
            public void run()
            {
//                if (counter == 0 || (counter < endCount - 27 / tickRate && counter % (5 * tickRate / 4) == 0))
//                    Util.playSound(door.getEngine(), "bd.dragging2", 0.5f, 0.6f);

                lastTime = currentTime;
                currentTime = System.nanoTime();
                long msSinceStart = (currentTime - startTime) / 1000000;
                if (!plugin.getCommander().isPaused())
                    counter = msSinceStart / (50 * tickRate);
                else
                    startTime += currentTime - lastTime;

                if (!plugin.getCommander().canGo() || counter > totalTicks)
                {
                    Util.playSound(door.getEngine(), "bd.thud", 2f, 0.15f);
                    for (int idx = 0; idx < savedBlocks.size(); ++idx)
                        if (!savedBlocks.get(idx).getMat().equals(Material.AIR))
                            savedBlocks.get(idx).getFBlock().setVelocity(new Vector(0D, 0D, 0D));
                    putBlocks(false);
                    cancel();
                }
                else
                {
                    for (MyBlockData block : savedBlocks)
                    {
                        if (!block.getMat().equals(Material.AIR))
                        {
                            double xOff = 0;
                            double zOff = 0;
                            if (NS)
                            {
                                xOff = 3 - 1 / (tickRate / 20);
                                int distanceToEng = Math
                                    .abs(block.getStartLocation().getBlockZ() - door.getEngine().getBlockZ());
                                if (distanceToEng > 0)
                                {
                                    double offset = Math.sin(0.5 * Math.PI * (counter * tickRate / 20) + distanceToEng);
                                    double maxVal = 0.25 * distanceToEng;
                                    maxVal = maxVal > 0.75 ? 0.75 : maxVal;
                                    xOff = offset > maxVal ? maxVal : offset;
                                }
                            }
                            else
                            {
                                int distanceToEng = Math
                                    .abs(block.getStartLocation().getBlockX() - door.getEngine().getBlockX());
                                if (distanceToEng > 0)
                                {
                                    double offset = Math.sin(0.5 * Math.PI * (counter * tickRate / 20) + distanceToEng);
                                    double maxVal = 0.25 * distanceToEng;
                                    maxVal = maxVal > 0.75 ? 0.75 : maxVal;
                                    zOff = offset > maxVal ? maxVal : offset;
                                }
                            }
                            Location loc = block.getStartLocation();
                            loc.add(xOff, 0, zOff);
                            Vector vec = loc.toVector().subtract(block.getFBlock().getLocation().toVector());
                            vec.multiply(0.101);
                            block.getFBlock().setVelocity(vec);
                        }
                    }
                }
            }
        };
        animationRunnable.runTaskTimer(plugin, 14, tickRate);
    }

    @Override
    public long getDoorUID()
    {
        return door.getDoorUID();
    }

    @Override
    public Door getDoor()
    {
        return door;
    }
}