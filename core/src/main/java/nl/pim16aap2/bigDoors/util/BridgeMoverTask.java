package nl.pim16aap2.bigDoors.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.NMS.CustomCraftFallingBlock;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory;
import nl.pim16aap2.bigDoors.NMS.NMSBlock;

public class BridgeMoverTask extends BukkitRunnable {
    private final DoorDirection openDirection;
    private final boolean NS;
    private ArrayList<MyBlockData> savedBlocks;
    private final List<NMSBlock> edges;
    private final int yMax, zMin, zMax;
    private final int xMin, xMax, yMin;
    private final double radius;
    private Location startLocation;
    private Location newFBlockLocation;
    private boolean instantOpen;
    private FallingBlockFactory fabf;
    private World world;
    private Block vBlock;
    private int xAxis;
    private int yAxis;
    private int zAxis;
    private final FallingBlockFactory.Specification spec;

    public BridgeMoverTask(FallingBlockFactory.Specification spec, DoorDirection openDirection, boolean NS, ArrayList<MyBlockData> savedBlocks, List<NMSBlock> edges, int yMax, int zMin, int zMax, int xMin, int xMax, int yMin, double radius, Location startLocation, Location newFBlockLocation, boolean instantOpen, FallingBlockFactory fabf, World world, Block vBlock, int xAxis, int yAxis, int zAxis) {
        this.spec = spec;
        this.openDirection = openDirection;
        this.NS = NS;
        this.savedBlocks = savedBlocks;
        this.edges = edges;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.radius = radius;
        this.startLocation = startLocation;
        this.newFBlockLocation = newFBlockLocation;
        this.instantOpen = instantOpen;
        this.fabf = fabf;
        this.world = world;
        this.vBlock = vBlock;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zAxis = zAxis;
    }

    @Override
    public void run() {
        Material mat = vBlock.getType();
        if (Util.isAllowedBlock(mat)) {
            byte matData = vBlock.getData();
            BlockState bs = vBlock.getState();
            MaterialData materialData = bs.getData();

            NMSBlock block = fabf.nmsBlockFactory(world, xAxis, yAxis, zAxis);
            NMSBlock block2 = null;

            int canRotate = 0;
            byte matByte = matData;

            canRotate = Util.canRotate(mat);
            // Rotate blocks here so they don't interrupt the rotation animation.
            if (canRotate != 4 && canRotate != 5) {
                if (canRotate == 7)
                    rotateEndRotBlockData(matData);
                if (canRotate != 6 && canRotate < 8)
                    matByte = canRotate == 7 ? rotateEndRotBlockData(matData) : rotateBlockData(matData);
                Block b = world.getBlockAt(xAxis, yAxis, zAxis);
                materialData.setData(matByte);

                if (BigDoors.isOnFlattenedVersion()) {
                    if (canRotate == 6) {
                        block2 = fabf.nmsBlockFactory(world, xAxis, yAxis, zAxis);
                        block2.rotateBlockUpDown(NS);
                    } else if (canRotate == 8 || canRotate == 9) {
                        block2 = fabf.nmsBlockFactory(world, xAxis, yAxis, zAxis);
                        block2.rotateVerticallyInDirection(openDirection);
                    } else {
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
                fBlock = fabf.createFallingBlockWithMetadata(spec, newFBlockLocation, block, matData, mat);

            savedBlocks.add(new MyBlockData(mat, matByte, fBlock, radius, materialData,
                    block2 == null ? block : block2, canRotate, startLocation));

            if (xAxis == xMin || xAxis == xMax ||
                    yAxis == yMin || yAxis == yMax ||
                    zAxis == zMin || zAxis == zMax)
                edges.add(block);
        }
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

}
