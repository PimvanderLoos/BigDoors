package nl.pim16aap2.bigDoors.NMS;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import nl.pim16aap2.bigDoors.util.DoorDirection;
import nl.pim16aap2.bigDoors.util.NMSUtil;
import nl.pim16aap2.bigDoors.util.RotateDirection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Fence;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Set;

public class NMSBlock_V26_R1 extends BlockBehaviour implements NMSBlock
{
    private static final MapCodec<? extends Block> CODEC =
        simpleCodec(properties ->
        {
            throw new UnsupportedOperationException("Custom block data cannot be deserialized from codec");
        });

    private BlockState blockData;
    private CraftBlockData craftBlockData;
    private final XMaterial xMat;
    private Location loc;

    public NMSBlock_V26_R1(
        World world,
        int x,
        int y,
        int z,
        BlockBehaviour.Properties blockInfo
    )
    {
        super(blockInfo);

        loc = new Location(world, x, y, z);

        craftBlockData = (CraftBlockData) world.getBlockAt(x, y, z).getBlockData();
        if (craftBlockData instanceof Waterlogged)
        {
            ((Waterlogged) craftBlockData).setWaterlogged(false);
        }

        updateBlockData();

        xMat = XMaterial.matchXMaterial(world.getBlockAt(x, y, z).getType().toString()).orElse(XMaterial.BEDROCK);
    }

    @Override
    public boolean canRotate()
    {
        return craftBlockData instanceof MultipleFacing;
    }

    @Override
    public void rotateBlock(RotateDirection rotDir)
    {
        Rotation rot;
        switch (rotDir)
        {
            case CLOCKWISE:
                rot = Rotation.CLOCKWISE_90;
                break;
            case COUNTERCLOCKWISE:
                rot = Rotation.COUNTERCLOCKWISE_90;
                break;
            default:
                rot = Rotation.NONE;
        }
        blockData = blockData.rotate(rot);
        updateCraftBlockData();
    }

    private void updateBlockData()
    {
        blockData = craftBlockData.getState();
    }

    private void updateCraftBlockData()
    {
        this.craftBlockData = CraftBlockDataFactory.fromState(blockData);
    }

    @Override
    public void putBlock(Location loc)
    {
        this.loc = loc;

        if (craftBlockData instanceof MultipleFacing)
        {
            updateCraftBlockDataMultipleFacing();
        }

        final CraftWorld craftWorld = Objects.requireNonNull((CraftWorld) loc.getWorld());

        craftWorld
            .getHandle()
            .setBlock(BlockPos.containing(loc.getX(), loc.getY(), loc.getZ()), blockData, 1);
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
                {
                    ((MultipleFacing) craftBlockData).setFace(blockFace, true);
                }
                else if (otherBlock.getType().isSolid())
                {

                    ((MultipleFacing) craftBlockData).setFace(blockFace, true);

                    final boolean isOtherMultipleFacing = otherData instanceof MultipleFacing;
                    final boolean materialMatch = otherBlock.getType().equals(xMat.parseMaterial());
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
                {
                    ((MultipleFacing) craftBlockData).setFace(blockFace, false);
                }
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
        Direction.Axis axis = blockData.getValue(RotatedPillarBlock.AXIS);
        Direction.Axis newAxis = axis;
        switch (axis)
        {
            case X:
                newAxis = ns ? Direction.Axis.X : Direction.Axis.Y;
                break;
            case Y:
                newAxis = ns ? Direction.Axis.Z : Direction.Axis.X;
                break;
            case Z:
                newAxis = ns ? Direction.Axis.Y : Direction.Axis.Z;
                break;
        }
        blockData = blockData.setValue(RotatedPillarBlock.AXIS, newAxis);
        this.updateCraftBlockData();
    }

    @Override
    public void rotateCylindrical(RotateDirection rotDir)
    {
        if (rotDir.equals(RotateDirection.CLOCKWISE))
        {
            blockData = blockData.rotate(Rotation.CLOCKWISE_90);
        }
        else
        {
            blockData = blockData.rotate(Rotation.COUNTERCLOCKWISE_90);
        }
        this.updateCraftBlockData();
    }

    /**
     * Gets the BlockState (NMS) of this block.
     *
     * @return The BlockState (NMS) of this block.
     */
    BlockState getMyBlockData()
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
    protected @NonNull MapCodec<? extends Block> codec()
    {
        return CODEC;
    }

    @Override
    public @NonNull Item asItem()
    {
        throw new UnsupportedOperationException("Custom block data cannot be converted to an item");
    }

    @Override
    protected @NonNull Block asBlock()
    {
        return this.blockData.getBlock();
    }
}
