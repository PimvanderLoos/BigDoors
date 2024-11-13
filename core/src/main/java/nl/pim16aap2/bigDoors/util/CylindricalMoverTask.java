package nl.pim16aap2.bigDoors.util;

import java.util.ArrayList;
import java.util.List;

import nl.pim16aap2.bigDoors.Door;
import org.bukkit.Bukkit;
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

public class CylindricalMoverTask extends BukkitRunnable {
    private ArrayList<MyBlockData> savedBlocks;
    private final List<NMSBlock> edges;
    private final int yMax, zMin, zMax;
    private final int xMin, xMax, yMin;
    private final double radius;
    private Location startLocation;
    private Location newFBlockLocation;
    private RotateDirection rotDir;
    private boolean instantOpen;
    private FallingBlockFactory fabf;
    private World world;
    private Block vBlock;
    private int xAxis;
    private int yAxis;
    private int zAxis;
    private FallingBlockFactory.Specification spec;
    private final RotateDirection rotDirection;

    public CylindricalMoverTask(RotateDirection rotateDirection, FallingBlockFactory.Specification spec, ArrayList<MyBlockData> savedBlocks, List<NMSBlock> edges, int yMax, int zMin, int zMax, int xMin, int xMax, int yMin, double radius, Location startLocation, Location newFBlockLocation, RotateDirection rotDir, boolean instantOpen, FallingBlockFactory fabf, World world, Block vBlock, int xAxis, int yAxis, int zAxis) {
        this.spec = spec;
        this.rotDirection = rotateDirection;
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
        this.rotDir = rotDir;
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
        final Material mat = vBlock.getType();

        if (Util.isAllowedBlock(mat))
        {
            final byte matData = vBlock.getData();
            final BlockState bs = vBlock.getState();
            final MaterialData materialData = bs.getData();

            final NMSBlock block = fabf.nmsBlockFactory(world, xAxis, yAxis, zAxis);
            NMSBlock block2 = null;

            byte matByte = matData;
            final int canRotate = Util.canRotate(mat);

            // Rotate blocks here so they don't interrupt the rotation animation.
            if (canRotate != 0)
            {
                Location pos = new Location(world, xAxis, yAxis, zAxis);
                if (canRotate == 1 || canRotate == 3)
                    matByte = rotateBlockDataLog(matData);
                else if (canRotate == 2)
                    matByte = rotateBlockDataStairs(matData);
                else if (canRotate == 4)
                    matByte = rotateBlockDataAnvil(matData);
                else if (canRotate == 7)
                    matByte = rotateBlockDataEndRod(matData);

                Block b = world.getBlockAt(pos);
                materialData.setData(matByte);

                if (BigDoors.isOnFlattenedVersion())
                {
                    if (canRotate == 6 || canRotate == 8 || canRotate == 9)
                    {
                        block2 = fabf.nmsBlockFactory(world, xAxis, yAxis, zAxis);
                        block2.rotateCylindrical(this.rotDirection);
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
                fBlock = fabf.createFallingBlockWithMetadata(spec, newFBlockLocation, block, matData, mat);

            savedBlocks.add(new MyBlockData(mat, matByte, fBlock, radius, materialData,
                                            block2 == null ? block : block2, canRotate, startLocation));

            if (xAxis == xMin || xAxis == xMax ||
                yAxis == yMin || yAxis == yMax ||
                zAxis == zMin || zAxis == zMax)
                edges.add(block);
        }
    }


    // Rotate logs by modifying its material data.
    private byte rotateBlockDataLog(byte matData)
    {
        if (matData >= 4 && matData <= 7)
            matData = (byte) (matData + 4);
        else if (matData >= 7 && matData <= 11)
            matData = (byte) (matData - 4);
        return matData;
    }

    private byte rotateBlockDataEndRod(byte matData)
    {
        /*
         * 0: Pointing Down (upside down (purple on top))
         * 1: Pointing Up (normal)
         * 2: Pointing North
         * 3: Pointing South
         * 4: Pointing West
         * 5: Pointing East
         */
        if (matData == 0 || matData == 1)
            return matData;

        if (rotDirection == RotateDirection.CLOCKWISE)
        {
            switch (matData)
            {
                case 2: return 5; // North -> East
                case 3: return 4; // South -> West
                case 4: return 2; // West  -> North
                case 5: return 3; // East  -> South
                default: return matData;
            }
        }

        switch (matData)
        {
            case 2: return 5; // North -> West
            case 3: return 4; // South -> East
            case 4: return 3; // West  -> South
            case 5: return 2; // East  -> North
            default: return matData;
        }
    }

    private byte rotateBlockDataAnvil(byte matData)
    {
        if (rotDirection == RotateDirection.CLOCKWISE)
        {
            if (matData == 0 || matData == 4 || matData == 8)
                matData = (byte) (matData + 1);
            else if (matData == 1 || matData == 5 || matData == 9)
                matData = (byte) (matData + 1);
            else if (matData == 2 || matData == 6 || matData == 10)
                matData = (byte) (matData + 1);
            else if (matData == 3 || matData == 7 || matData == 11)
                matData = (byte) (matData - 3);
        }
        else if (matData == 0 || matData == 4 || matData == 8)
            matData = (byte) (matData + 3);
        else if (matData == 1 || matData == 5 || matData == 9)
            matData = (byte) (matData - 1);
        else if (matData == 2 || matData == 6 || matData == 10)
            matData = (byte) (matData - 1);
        else if (matData == 3 || matData == 7 || matData == 11)
            matData = (byte) (matData - 1);
        return matData;
    }

    // Rotate stairs by modifying its material data.
    private byte rotateBlockDataStairs(byte matData)
    {
        if (rotDirection == RotateDirection.CLOCKWISE)
        {
            if (matData == 0 || matData == 4)
                matData = (byte) (matData + 2);
            else if (matData == 1 || matData == 5)
                matData = (byte) (matData + 2);
            else if (matData == 2 || matData == 6)
                matData = (byte) (matData - 1);
            else if (matData == 3 || matData == 7)
                matData = (byte) (matData - 3);
        }
        else if (matData == 0 || matData == 4)
            matData = (byte) (matData + 3);
        else if (matData == 1 || matData == 5)
            matData = (byte) (matData + 1);
        else if (matData == 2 || matData == 6)
            matData = (byte) (matData - 2);
        else if (matData == 3 || matData == 7)
            matData = (byte) (matData - 2);
        return matData;
    }
}
