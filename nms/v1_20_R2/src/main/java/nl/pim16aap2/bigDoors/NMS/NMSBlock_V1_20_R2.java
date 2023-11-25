package nl.pim16aap2.bigDoors.NMS;

import com.cryptomorin.xseries.XMaterial;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockRotatable;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import nl.pim16aap2.bigDoors.ILogger;
import nl.pim16aap2.bigDoors.util.DoorDirection;
import nl.pim16aap2.bigDoors.util.ILoggableDoor;
import nl.pim16aap2.bigDoors.util.NMSUtil;
import nl.pim16aap2.bigDoors.util.RotateDirection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Fence;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.block.data.CraftBlockData;

import java.util.Objects;
import java.util.Set;

public class NMSBlock_V1_20_R2 extends BlockBase implements NMSBlock
{
    private final ILogger logger;
    private final ILoggableDoor door;
    private IBlockData blockData;
    private CraftBlockData craftBlockData;
    private final XMaterial xmat;
    private Location loc;

    public NMSBlock_V1_20_R2(
        ILogger logger,
        ILoggableDoor door,
        World world,
        int x, int y, int z,
        Info blockInfo)
    {
        super(blockInfo);
        this.logger = logger;
        this.door = door;

        loc = new Location(world, x, y, z);

        craftBlockData = (CraftBlockData) world.getBlockAt(x, y, z).getBlockData();
        logger.logMessageToLogFileForDoor(door, String.format(
            "Created animated block from [%d, %d, %d]: %s",
            x, y, z,
            craftBlockData.getState()
        ));

        if (craftBlockData instanceof Waterlogged)
            ((Waterlogged) craftBlockData).setWaterlogged(false);

        updateBlockData();

        xmat = XMaterial.matchXMaterial(world.getBlockAt(x, y, z).getType().toString()).orElse(XMaterial.BEDROCK);
    }

    @Override
    public boolean canRotate()
    {
        return craftBlockData instanceof MultipleFacing;
    }

    @Override
    public void rotateBlock(RotateDirection rotDir)
    {
        EnumBlockRotation rot;
        switch (rotDir)
        {
        case CLOCKWISE:
            rot = EnumBlockRotation.b;
            break;
        case COUNTERCLOCKWISE:
            rot = EnumBlockRotation.d;
            break;
        default:
            rot = EnumBlockRotation.a;
        }
        blockData = blockData.a(rot);
        updateCraftBlockData();
    }

    private void updateBlockData()
    {
        blockData = craftBlockData.getState();
    }

    private void updateCraftBlockData()
    {
        this.craftBlockData = CraftBlockData.fromData(blockData);
    }

    @Override
    public void putBlock(Location loc)
    {
        this.loc = loc;

        if (craftBlockData instanceof MultipleFacing)
            updateCraftBlockDataMultipleFacing();

        logger.logMessageToLogFileForDoor(door, String.format(
            "Putting block at            [%d, %d, %d]: %s",
            loc.getBlockX(),
            loc.getBlockY(),
            loc.getBlockZ(),
            blockData
        ));

        ((CraftWorld) Objects.requireNonNull(loc.getWorld()))
            .getHandle().a(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), blockData, 1);
    }

    private void updateCraftBlockDataMultipleFacing()
    {
        final Set<BlockFace> allowedFaces = ((MultipleFacing) craftBlockData).getAllowedFaces();
        allowedFaces.forEach(
            (blockFace) ->
            {
                final org.bukkit.block.Block otherBlock =
                    loc.clone().add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()).getBlock();
                final org.bukkit.block.data.BlockData otherData = otherBlock.getBlockData();

                if (blockFace.equals(BlockFace.UP))
                    ((MultipleFacing) craftBlockData).setFace(blockFace, true);
                else if (otherBlock.getType().isSolid())
                {

                    ((MultipleFacing) craftBlockData).setFace(blockFace, true);

                    final boolean isOtherMultipleFacing = otherData instanceof MultipleFacing;
                    final boolean materialMatch = otherBlock.getType().equals(xmat.parseMaterial());
                    final boolean areBothFence = craftBlockData instanceof Fence && otherData instanceof Fence;

                    if (isOtherMultipleFacing && (materialMatch || areBothFence))
                    {
                        final Set<BlockFace> otherAllowedFaces = ((MultipleFacing) otherData).getAllowedFaces();
                        if (otherAllowedFaces.contains(blockFace.getOppositeFace()))
                        {
                            ((MultipleFacing) otherData).setFace(blockFace.getOppositeFace(), true);
                            otherBlock.setBlockData(otherData);
                        }
                    }
                }
                else
                    ((MultipleFacing) craftBlockData).setFace(blockFace, false);
            });
        this.updateBlockData();
    }

    @Override
    public void rotateVerticallyInDirection(DoorDirection openDirection)
    {
        NMSUtil.rotateVerticallyInDirection(openDirection, craftBlockData);
        this.updateBlockData();
    }

    @Override
    public void rotateBlockUpDown(boolean ns)
    {
        EnumDirection.EnumAxis axis = blockData.c(BlockRotatable.g);
        EnumDirection.EnumAxis newAxis = axis;
        switch (axis)
        {
        case a:
            newAxis = ns ? EnumDirection.EnumAxis.a : EnumDirection.EnumAxis.b;
            break;
        case b:
            newAxis = ns ? EnumDirection.EnumAxis.c : EnumDirection.EnumAxis.a;
            break;
        case c:
            newAxis = ns ? EnumDirection.EnumAxis.b : EnumDirection.EnumAxis.c;
            break;
        }
        blockData = blockData.a(BlockRotatable.g, newAxis);
        this.updateCraftBlockData();
    }

    @Override
    public void rotateCylindrical(RotateDirection rotDir)
    {
        if (rotDir.equals(RotateDirection.CLOCKWISE))
            blockData = blockData.a(EnumBlockRotation.b);
        else
            blockData = blockData.a(EnumBlockRotation.d);
        this.updateCraftBlockData();
    }

    /**
     * Gets the IBlockData (NMS) of this block.
     *
     * @return The IBlockData (NMS) of this block.
     */
    IBlockData getMyBlockData()
    {
        return blockData;
    }

    @Override
    public String toString()
    {
        return blockData.toString();
    }

    @Override
    public void deleteOriginalBlock(boolean applyPhysics)
    {
        logger.logMessageToLogFileForDoor(door, String.format(
            "Deleting original block at  [%d, %d, %d]. Apply physics: %b",
            loc.getBlockX(),
            loc.getBlockY(),
            loc.getBlockZ(),
            applyPhysics
        ));

        final World world = Objects.requireNonNull(loc.getWorld());
        if (!applyPhysics)
        {
            world.getBlockAt(loc).setType(Material.AIR, false);
        }
        else
        {
            world.getBlockAt(loc).setType(Material.CAVE_AIR, false);
            world.getBlockAt(loc).setType(Material.AIR, true);
        }
    }

    @Override
    public Item k()
    {
        return null;
    }

    @Override
    protected Block p()
    {
        return null;
    }
}
