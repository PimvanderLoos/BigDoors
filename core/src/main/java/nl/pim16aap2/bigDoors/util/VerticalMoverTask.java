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

public class VerticalMoverTask extends BukkitRunnable {
    private ArrayList<MyBlockData> savedBlocks;
    private final List<NMSBlock> edges;
    private final int yMax, zMin, zMax;
    private final int xMin, xMax, yMin;
    private Location startLocation;
    private Location newFBlockLocation;
    private boolean instantOpen;
    private FallingBlockFactory fabf;
    private World world;
    private Block vBlock;
    private int xAxis;
    private int yAxis;
    private int zAxis;

    public VerticalMoverTask(ArrayList<MyBlockData> savedBlocks, List<NMSBlock> edges, int yMax, int zMin, int zMax, int xMin, int xMax, int yMin, Location startLocation, Location newFBlockLocation, boolean instantOpen, FallingBlockFactory fabf, World world, Block vBlock, int xAxis, int yAxis, int zAxis) {
        this.savedBlocks = savedBlocks;
        this.edges = edges;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
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

            if (!BigDoors.isOnFlattenedVersion())
                vBlock.setType(Material.AIR);

            CustomCraftFallingBlock fBlock = null;
            if (!instantOpen)
                fBlock = fabf.fallingBlockFactory(newFBlockLocation, block, matData, mat);
            savedBlocks
                    .add(new MyBlockData(mat, matData, fBlock, 0, materialData, block, 0, startLocation));

            if (xAxis == xMin || xAxis == xMax ||
                    yAxis == yMin || yAxis == yMax ||
                    zAxis == zMin || zAxis == zMax)
                edges.add(block);
        }
    }

    
}
