package nl.pim16aap2.bigDoors.moveBlocks;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.NMS.CustomCraftFallingBlock;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory;
import nl.pim16aap2.bigDoors.NMS.NMSBlock;
import nl.pim16aap2.bigDoors.moveBlocks.Bridge.getNewLocation.GetNewLocation;
import nl.pim16aap2.bigDoors.moveBlocks.Bridge.getNewLocation.GetNewLocationEast;
import nl.pim16aap2.bigDoors.moveBlocks.Bridge.getNewLocation.GetNewLocationNorth;
import nl.pim16aap2.bigDoors.moveBlocks.Bridge.getNewLocation.GetNewLocationSouth;
import nl.pim16aap2.bigDoors.moveBlocks.Bridge.getNewLocation.GetNewLocationWest;
import nl.pim16aap2.bigDoors.util.DoorDirection;
import nl.pim16aap2.bigDoors.util.MyBlockData;
import nl.pim16aap2.bigDoors.util.RotateDirection;
import nl.pim16aap2.bigDoors.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BridgeMover extends BlockMover
{
    private final World world;
    private final BigDoors plugin;
    private final int tickRate;
    private final double multiplier;
    private final double time;
    private final FallingBlockFactory fabf;
    private final boolean NS;
    private final Door door;
    private final RotateDirection upDown;
    private final DoorDirection engineSide;
    private final double endStepSum;
    private final DoorDirection openDirection;
    private final int dx;
    private final int dz;
    private final int xMin;
    private final int yMin;
    private final int zMin;
    private final int xMax;
    private final int yMax;
    private final int zMax;
    private final int stepMultiplier;
    private final Location turningPoint;
    private final double startStepSum;
    private final Location pointOpposite;

    private volatile int endCount;
    private volatile GetNewLocation gnl;
    private volatile BukkitRunnable animationRunnable;

    public BridgeMover(BigDoors plugin, World world, double time, Door door, RotateDirection upDown,
        DoorDirection openDirection, boolean instantOpen, double multiplier)
    {
        super(plugin, door, instantOpen);
        fabf = plugin.getFABF();
        engineSide = door.getEngSide();
        NS = engineSide == DoorDirection.NORTH || engineSide == DoorDirection.SOUTH;
        this.door = door;
        this.world = world;
        this.plugin = plugin;
        this.upDown = upDown;
        this.openDirection = openDirection;

        xMin = door.getMinimum().getBlockX();
        yMin = door.getMinimum().getBlockY();
        zMin = door.getMinimum().getBlockZ();
        xMax = door.getMaximum().getBlockX();
        yMax = door.getMaximum().getBlockY();
        zMax = door.getMaximum().getBlockZ();
        int xLen = Math.abs(door.getMaximum().getBlockX() - door.getMinimum().getBlockX());
        int yLen = Math.abs(door.getMaximum().getBlockY() - door.getMinimum().getBlockY());
        int zLen = Math.abs(door.getMaximum().getBlockZ() - door.getMinimum().getBlockZ());
        int doorSize = Math.max(xLen, Math.max(yLen, zLen)) + 1;
        double[] vars = Util.calculateTimeAndTickRate(doorSize, time, multiplier, 5.2);
        this.time = vars[0];
        tickRate = (int) vars[1];
        this.multiplier = vars[2];

        Location pointOppositeTmp = null;
        Location turningPointTmp = null;
        double startStepSumTmp = -1;
        int stepMultiplierTmp = 0;
        int dxTmp = 0;
        int dzTmp = 0;

        // Regarding dx, dz. These variables determine whether loops get incremented (1)
        // or decremented (-1)
        // When looking in the direction of the opposite point from the engine side, the
        // blocks should get
        // Processed from left to right and from the engine to the opposite.
        /*
         * Pointing: Degrees: UP 0 or 360 EAST 90 WEST 270 NORTH 270 SOUTH 90
         */
        // Calculate turning point and pointOpposite.
        switch (engineSide)
        {
        case NORTH:
            // When EngineSide is North, x goes from low to high and z goes from low to high
            turningPointTmp = new Location(world, xMin, yMin, zMin);
            dxTmp = 1;
            dzTmp = 1;

            if (upDown.equals(RotateDirection.UP))
            {
                pointOppositeTmp = new Location(world, xMax, yMin, zMax);
                startStepSumTmp = Math.PI / 2;
                stepMultiplierTmp = -1;
            }
            else
            {
                pointOppositeTmp = new Location(world, xMax, yMax, zMin);
                if (openDirection.equals(DoorDirection.NORTH))
                    stepMultiplierTmp = -1;
                else if (openDirection.equals(DoorDirection.SOUTH))
                    stepMultiplierTmp = 1;
            }
            break;

        case SOUTH:
            // When EngineSide is South, x goes from high to low and z goes from high to low
            turningPointTmp = new Location(world, xMax, yMin, zMax);
            dxTmp = -1;
            dzTmp = -1;

            if (upDown.equals(RotateDirection.UP))
            {
                pointOppositeTmp = new Location(world, xMin, yMin, zMin);
                startStepSumTmp = -Math.PI / 2;
                stepMultiplierTmp = 1;
            }
            else
            {
                pointOppositeTmp = new Location(world, xMin, yMax, zMax);
                if (openDirection.equals(DoorDirection.NORTH))
                    stepMultiplierTmp = -1;
                else if (openDirection.equals(DoorDirection.SOUTH))
                    stepMultiplierTmp = 1;
            }
            break;

        case EAST:
            // When EngineSide is East, x goes from high to low and z goes from low to high
            turningPointTmp = new Location(world, xMax, yMin, zMin);
            dxTmp = -1;
            dzTmp = 1;

            if (upDown.equals(RotateDirection.UP))
            {
                pointOppositeTmp = new Location(world, xMin, yMin, zMax);
                startStepSumTmp = -Math.PI / 2;
                stepMultiplierTmp = 1;
            }
            else
            {
                pointOppositeTmp = new Location(world, xMax, yMax, zMax);
                if (openDirection.equals(DoorDirection.EAST))
                    stepMultiplierTmp = 1;
                else if (openDirection.equals(DoorDirection.WEST))
                    stepMultiplierTmp = -1;
            }
            break;

        case WEST:
            // When EngineSide is West, x goes from low to high and z goes from high to low
            turningPointTmp = new Location(world, xMin, yMin, zMax);
            dxTmp = 1;
            dzTmp = -1;

            if (upDown.equals(RotateDirection.UP))
            {
                pointOppositeTmp = new Location(world, xMax, yMin, zMin);
                startStepSumTmp = Math.PI / 2;
                stepMultiplierTmp = -1;
            }
            else
            {
                pointOppositeTmp = new Location(world, xMin, yMax, zMin);
                if (openDirection.equals(DoorDirection.EAST))
                    stepMultiplierTmp = 1;
                else if (openDirection.equals(DoorDirection.WEST))
                    stepMultiplierTmp = -1;
            }
            break;
        }

        stepMultiplier = assertNotZero(stepMultiplierTmp, "stepMultiplier");
        dx = assertNotZero(dxTmp, "dx");
        dz = assertNotZero(dzTmp, "dz");

        pointOpposite = assertNotNull(pointOppositeTmp, "pointOpposite");
        turningPoint = assertNotNull(turningPointTmp, "turningPoint");

        endStepSum = upDown.equals(RotateDirection.UP) ? 0 : Math.PI / 2 * stepMultiplier;
        startStepSum = upDown.equals(RotateDirection.DOWN) ? 0 : startStepSumTmp;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::createAnimatedBlocks, 2L);
    }

    private void createAnimatedBlocks()
    {
        final List<MyBlockData> savedBlocks = new ArrayList<>(door.getBlockCount());

        // This will reserve a bit too much memory, but not enough to worry about.
        final List<NMSBlock> edges =
            new ArrayList<>(Math.min(door.getBlockCount(),
                                     (xMax - xMin + 1) * 2 + (yMax - yMin + 1) * 2 + (zMax - zMin + 1) * 2));

        int xAxis = turningPoint.getBlockX();
        do
        {
            int zAxis = turningPoint.getBlockZ();
            do
            {
                // Get the radius of this row.
                double radius = 0;
                if (upDown == RotateDirection.UP)
                {
                    if (NS)
                        radius = Math.abs(zAxis - turningPoint.getBlockZ());
                    else
                        radius = Math.abs(xAxis - turningPoint.getBlockX());
                }

                for (int yAxis = yMin; yAxis <= yMax; ++yAxis)
                {
                    Location startLocation = new Location(world, xAxis + 0.5, yAxis, zAxis + 0.5);
                    if (upDown == RotateDirection.DOWN)
                        radius = yAxis - turningPoint.getBlockY();

                    Location newFBlockLocation = new Location(world, xAxis + 0.5, yAxis, zAxis + 0.5);

                    Block vBlock = world.getBlockAt(xAxis, yAxis, zAxis);
                    Material mat = vBlock.getType();
                    if (Util.isAllowedBlock(mat))
                    {
                        byte matData = vBlock.getData();
                        BlockState bs = vBlock.getState();
                        MaterialData materialData = bs.getData();

                        NMSBlock block = fabf.nmsBlockFactory(world, xAxis, yAxis, zAxis);
                        NMSBlock block2 = null;

                        int canRotate = 0;
                        byte matByte = matData;

                        canRotate = Util.canRotate(mat);
                        // Rotate blocks here so they don't interrupt the rotation animation.
                        if (canRotate != 4 && canRotate != 5)
                        {
                            if (canRotate == 7)
                                rotateEndRotBlockData(matData);
                            if (canRotate != 6 && canRotate < 8)
                                matByte = canRotate == 7 ? rotateEndRotBlockData(matData) : rotateBlockData(matData);
                            Block b = world.getBlockAt(xAxis, yAxis, zAxis);
                            materialData.setData(matByte);

                            if (BigDoors.isOnFlattenedVersion())
                            {
                                if (canRotate == 6)
                                {
                                    block2 = fabf.nmsBlockFactory(world, xAxis, yAxis, zAxis);
                                    block2.rotateBlockUpDown(NS);
                                }
                                else if (canRotate == 8 || canRotate == 9)
                                {
                                    block2 = fabf.nmsBlockFactory(world, xAxis, yAxis, zAxis);
                                    block2.rotateVerticallyInDirection(openDirection);
                                }
                                else
                                {
                                    b.setType(mat);
                                    BlockState bs2 = b.getState();
                                    bs2.setData(materialData);
                                    bs2.update();
                                    block2 = fabf.nmsBlockFactory(world, xAxis, yAxis, zAxis);
                                }
                            }
                        }
                        if (!BigDoors.isOnFlattenedVersion())
                            vBlock.setType(Material.AIR);

                        CustomCraftFallingBlock fBlock = null;
                        if (!instantOpen)
                            fBlock = fabf.fallingBlockFactory(newFBlockLocation, block, matData, mat);

                        savedBlocks.add(new MyBlockData(mat, matByte, fBlock, radius, materialData,
                                                        block2 == null ? block : block2, canRotate, startLocation));

                        if (xAxis == xMin || xAxis == xMax ||
                            yAxis == yMin || yAxis == yMax ||
                            zAxis == zMin || zAxis == zMax)
                            edges.add(block);
                    }
                }
                zAxis += dz;
            }
            while (zAxis >= pointOpposite.getBlockZ() && dz == -1 || zAxis <= pointOpposite.getBlockZ() && dz == 1);
            xAxis += dx;
        }
        while (xAxis >= pointOpposite.getBlockX() && dx == -1 || xAxis <= pointOpposite.getBlockX() && dx == 1);

        switch (openDirection)
        {
        case NORTH:
            gnl = new GetNewLocationNorth(world, xMin, xMax, yMin, yMax, zMin, zMax, upDown, openDirection);
            break;
        case EAST:
            gnl = new GetNewLocationEast(world, xMin, xMax, yMin, yMax, zMin, zMax, upDown, openDirection);
            break;
        case SOUTH:
            gnl = new GetNewLocationSouth(world, xMin, xMax, yMin, yMax, zMin, zMax, upDown, openDirection);
            break;
        case WEST:
            gnl = new GetNewLocationWest(world, xMin, xMax, yMin, yMax, zMin, zMax, upDown, openDirection);
            break;
        }

        // This is only supported on 1.13
        if (BigDoors.isOnFlattenedVersion())
        {
            savedBlocks.forEach(myBlockData -> myBlockData.getBlock().deleteOriginalBlock(false));
            // Update the physics around the edges after we've removed all our blocks.
            edges.forEach(block -> block.deleteOriginalBlock(true));
        }

        registerSavedBlocks(savedBlocks);

        if (!instantOpen)
            rotateEntities();
        else
            putBlocks(false);
    }

    @Override
    public synchronized void cancel0(boolean onDisable)
    {
        if (this.animationRunnable == null)
        {
            plugin.getMyLogger().logMessageToLogFile(String.format(
                "[%s] animationRunnable is null, not cancelling anything!",
                formatDoorInfo()
            ));
            return;
        }
        this.animationRunnable.cancel();
        this.putBlocks(onDisable);
    }

    @Override
    public synchronized void putBlocks0(boolean onDisable)
    {
        super.putBlocks(onDisable, time, endCount,
                        gnl::getNewLocation,
                        () -> updateCoords(door, openDirection, upDown, -1, false));
    }

    // Method that takes care of the rotation aspect.
    private void rotateEntities()
    {
        endCount = (int) (20.0f / tickRate * time);

        animationRunnable = new BukkitRunnable()
        {
            final Location center = new Location(world, turningPoint.getBlockX() + 0.5, yMin, turningPoint.getBlockZ() + 0.5);
            final double step = (Math.PI / 2) / endCount * stepMultiplier;
            final int totalTicks = (int) (endCount * multiplier);
            final int replaceCount = endCount / 2;

            volatile boolean replace = false;
            volatile double counter = 0;
            volatile double stepSum = startStepSum;
            volatile long startTime = System.nanoTime();
            volatile long lastTime;
            volatile long currentTime = System.nanoTime();

            @Override
            public synchronized void cancel()
                throws IllegalStateException
            {
                plugin.getMyLogger().logMessageToLogFile(String.format(
                    "[%s] Canceling animationRunnable",
                    formatDoorInfo()
                ));
                super.cancel();
            }

            @Override
            public void run()
            {
                if (counter == 0 || (counter < endCount - 45 / tickRate && counter % (6 * tickRate / 4) == 0))
                    Util.playSound(door.getEngine(), "bd.drawbridge-rattling", 0.8f, 0.7f);

                lastTime = currentTime;
                currentTime = System.nanoTime();
                long msSinceStart = (currentTime - startTime) / 1000000;
                if (plugin.getCommander().isPaused())
                {
                    final long oldStartTime = startTime;
                    startTime = oldStartTime + currentTime - lastTime;
                }
                else
                    counter = msSinceStart / (50 * tickRate);

                if (counter < endCount - 1)
                    stepSum = startStepSum + step * counter;
                else
                    stepSum = endStepSum;
                replace = counter == replaceCount;

                if (counter > totalTicks)
                {
                    Util.playSound(door.getEngine(), "bd.thud", 2f, 0.15f);
                    for (MyBlockData savedBlock : getSavedBlocks())
                        if (!savedBlock.getMat().equals(Material.AIR))
                            savedBlock.getFBlock().setVelocity(new Vector(0D, 0D, 0D));
                    Bukkit.getScheduler().callSyncMethod(plugin, () ->
                    {
                        putBlocks(false);
                        return null;
                    });
                    cancel();
                }
                else
                {
                    // It is not pssible to edit falling block blockdata (client won't update it),
                    // so delete the current fBlock and replace it by one that's been rotated.
                    // Also, this stuff needs to be done on the main thread.
                    if (replace)
                    {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        {
                            for (MyBlockData block : getSavedBlocks())
                            {
                                if (block.canRot() != 0 && block.canRot() != 4)
                                {
                                    Material mat = block.getMat();
                                    Location loc = block.getFBlock().getLocation();
                                    byte matData = block.getBlockByte();
                                    Vector veloc = block.getFBlock().getVelocity();

                                    CustomCraftFallingBlock fBlock;
                                    // Because the block in savedBlocks is already rotated where applicable, just
                                    // use that block now.
                                    fBlock = fabf.fallingBlockFactory(loc, block.getBlock(), matData, mat);

                                    block.getFBlock().remove();
                                    block.setFBlock(fBlock);

                                    block.getFBlock().setVelocity(veloc);
                                }
                            }
                        }, 0);
                    }

                    for (MyBlockData block : getSavedBlocks())
                    {
                        if (!block.getMat().equals(Material.AIR))
                        {
                            double radius = block.getRadius();
                            if (radius != 0)
                            {
                                double posX, posY, posZ;
                                posY = center.getY() + radius * Math.cos(stepSum);
                                if (!NS)
                                {
                                    posX = center.getX() + radius * Math.sin(stepSum);
                                    posZ = block.getFBlock().getLocation().getZ();
                                }
                                else
                                {
                                    posX = block.getFBlock().getLocation().getX();
                                    posZ = center.getZ() + radius * Math.sin(stepSum);
                                }
                                Location loc = new Location(null, posX, posY, posZ);
                                Vector vec = loc.toVector().subtract(block.getFBlock().getLocation().toVector());
                                vec.multiply(0.101);
                                block.getFBlock().setVelocity(vec);
                            }
                        }
                    }
                }
            }
        };
        plugin.getMyLogger().logMessageToLogFile(String.format(
            "[%s] Scheduling animationRunnable",
            formatDoorInfo()
        ));
        animationRunnable.runTaskTimerAsynchronously(plugin, 14, tickRate);
    }

    // Rotate blocks such a logs by modifying its material data.
    private byte rotateBlockData(byte matData)
    {
        if (!NS)
        {
            if (matData >= 0 && matData < 4)
                return (byte) (matData + 4);
            if (matData >= 4 && matData < 8)
                return (byte) (matData - 4);
            return matData;
        }

        if (matData >= 0 && matData < 4)
            return (byte) (matData + 8);
        if (matData >= 8 && matData < 12)
            return (byte) (matData - 8);
        return matData;
    }

    // Rotate blocks such a logs by modifying its material data.
    private byte rotateEndRotBlockData(byte matData)
    {
        /*
         * 0: Pointing Down (upside down (purple on top))
         * 1: Pointing Up (normal)
         * 2: Pointing North
         * 3: Pointing South
         * 4: Pointing West
         * 5: Pointing East
         */
        if (!NS)
        {
            if (matData == 0)
                return (byte) (openDirection.equals(DoorDirection.EAST) ? 4 : 5);
            if (matData == 1)
                return (byte) (openDirection.equals(DoorDirection.EAST) ? 5 : 4);
            if (matData == 4)
                return (byte) (openDirection.equals(DoorDirection.EAST) ? 1 : 0);
            if (matData == 5)
                return (byte) (openDirection.equals(DoorDirection.EAST) ? 0 : 1);
            return matData;
        }

        if (matData == 0)
            return (byte) (openDirection.equals(DoorDirection.NORTH) ? 3 : 2);
        if (matData == 1)
            return (byte) (openDirection.equals(DoorDirection.NORTH) ? 2 : 3);
        if (matData == 2)
            return (byte) (openDirection.equals(DoorDirection.NORTH) ? 0 : 1);
        if (matData == 3)
            return (byte) (openDirection.equals(DoorDirection.NORTH) ? 1 : 0);
        return matData;
    }

    // Update the coordinates of a door based on its location, direction it's
    // pointing in and rotation direction.
    @SuppressWarnings("null")
    public static void updateCoords(Door door, DoorDirection openDirection, RotateDirection upDown, int moved,
                                    boolean shadow)
    {
        int xMin = door.getMinimum().getBlockX();
        int yMin = door.getMinimum().getBlockY();
        int zMin = door.getMinimum().getBlockZ();
        int xMax = door.getMaximum().getBlockX();
        int yMax = door.getMaximum().getBlockY();
        int zMax = door.getMaximum().getBlockZ();
        int xLen = xMax - xMin;
        int yLen = yMax - yMin;
        int zLen = zMax - zMin;
        Location newMax = null;
        Location newMin = null;
        DoorDirection newEngSide = door.getEngSide();

        switch (openDirection)
        {
        case NORTH:
            if (upDown == RotateDirection.UP)
            {
                newEngSide = DoorDirection.NORTH;
                newMin = new Location(door.getWorld(), xMin, yMin, zMin);
                newMax = new Location(door.getWorld(), xMax, yMin + zLen, zMin);
            }
            else
            {
                newEngSide = DoorDirection.SOUTH;
                newMin = new Location(door.getWorld(), xMin, yMin, zMin - yLen);
                newMax = new Location(door.getWorld(), xMax, yMin, zMin);
            }
            break;

        case EAST:
            if (upDown == RotateDirection.UP)
            {
                newEngSide = DoorDirection.EAST;
                newMin = new Location(door.getWorld(), xMax, yMin, zMin);
                newMax = new Location(door.getWorld(), xMax, yMin + xLen, zMax);
            }
            else
            {
                newEngSide = DoorDirection.WEST;
                newMin = new Location(door.getWorld(), xMax, yMin, zMin);
                newMax = new Location(door.getWorld(), xMax + yLen, yMin, zMax);
            }
            break;

        case SOUTH:
            if (upDown == RotateDirection.UP)
            {
                newEngSide = DoorDirection.SOUTH;
                newMin = new Location(door.getWorld(), xMin, yMin, zMax);
                newMax = new Location(door.getWorld(), xMax, yMin + zLen, zMax);
            }
            else
            {
                newEngSide = DoorDirection.NORTH;
                newMin = new Location(door.getWorld(), xMin, yMin, zMax);
                newMax = new Location(door.getWorld(), xMax, yMin, zMax + yLen);
            }
            break;

        case WEST:
            if (upDown == RotateDirection.UP)
            {
                newEngSide = DoorDirection.WEST;
                newMin = new Location(door.getWorld(), xMin, yMin, zMin);
                newMax = new Location(door.getWorld(), xMin, yMin + xLen, zMax);
            }
            else
            {
                newEngSide = DoorDirection.EAST;
                newMin = new Location(door.getWorld(), xMin - yLen, yMin, zMin);
                newMax = new Location(door.getWorld(), xMin, yMin, zMax);
            }
            break;
        }

        final Location oldMin = door.getMinimum();
        final Location oldMax = door.getMaximum();
        BigDoors.get().getMyLogger().logMessageToLogFile(String.format(
            "[%3d - %-12s] Updating coords from [%d, %d, %d] - [%d, %d, %d] to [%d, %d, %d] - [%d, %d, %d] (shadow: %b, moved: %d, openDirection: %s, upDown: %s)",
            door.getDoorUID(), door.getType(),
            oldMin.getBlockX(), oldMin.getBlockY(), oldMin.getBlockZ(),
            oldMax.getBlockX(), oldMax.getBlockY(), oldMax.getBlockZ(),
            newMin.getBlockX(), newMin.getBlockY(), newMin.getBlockZ(),
            newMax.getBlockX(), newMax.getBlockY(), newMax.getBlockZ(),
            shadow, moved, openDirection, upDown
        ));

        door.setMaximum(newMax);
        door.setMinimum(newMin);
        door.setEngineSide(newEngSide);

        boolean isOpen = shadow ? door.isOpen() : !door.isOpen();
        BigDoors.get().getCommander().updateDoorCoords(door.getDoorUID(), isOpen, newMin.getBlockX(),
                                                       newMin.getBlockY(), newMin.getBlockZ(), newMax.getBlockX(),
                                                       newMax.getBlockY(), newMax.getBlockZ(), newEngSide);
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

    /**
     * Asserts that a given value is not 0.
     * <p>
     * If it is, an {@link IllegalStateException} is thrown with a message containing the door's information.
     *
     * @param value The value to check.
     * @param valueName The name of the value to check. This will be used in the exception message if the value is 0.
     *
     * @return The value, if it is not 0.
     *
     * @throws IllegalStateException If the value is 0.
     */
    private int assertNotZero(int value, String valueName)
        throws IllegalStateException
    {
        if (value != 0)
            return value;

        throw new IllegalArgumentException(String.format(
            "[%s] %s cannot be 0! EngineSide: %s, upDown: %s, openDirection: %s, time: %.2f, tickRate: %d, Coordinates: [%d, %d, %d] - [%d, %d, %d]",
            formatDoorInfo(), valueName, engineSide, upDown, openDirection, time, tickRate, xMin, yMin, zMin, xMax, yMax, zMax
        ));
    }

    /**
     * Asserts that a given value is not null.
     * <p>
     * If it is, an {@link IllegalStateException} is thrown with a message containing the door's information.
     *
     * @param value The value to check.
     * @param valueName The name of the value to check. This will be used in the exception message if the value is 0.
     *
     * @param <T> The type of the value.
     *
     * @return The value, if it is not 0.
     *
     * @throws IllegalStateException If the value is 0.
     */
    private <T> T assertNotNull(T value, String valueName)
        throws IllegalStateException
    {
        if (value != null)
            return value;

        throw new IllegalArgumentException(String.format(
            "[%s] %s cannot be null! EngineSide: %s, upDown: %s, openDirection: %s, time: %.2f, tickRate: %d, Coordinates: [%d, %d, %d] - [%d, %d, %d]",
            formatDoorInfo(), valueName, engineSide, upDown, openDirection, time, tickRate, xMin, yMin, zMin, xMax, yMax, zMax
        ));
    }
}
