package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;

class FallingBlockFactory_V26_2_R1 implements FallingBlockFactory
{
    private final CustomEntityFallingBlockFactory customEntityFallingBlockFactory;
    private final ResourceKey<Block> blockInfoResourceKey;

    FallingBlockFactory_V26_2_R1(CustomEntityFallingBlockFactory customEntityFallingBlockFactory)
    {
        this.customEntityFallingBlockFactory = customEntityFallingBlockFactory;
        this.blockInfoResourceKey = ResourceKey.create(
            Registries.BLOCK,
            Identifier.fromNamespaceAndPath("big-doors", "block-info")
        );
    }

    @Override
    public CustomCraftFallingBlock createFallingBlock(Location loc, NMSBlock block, byte matData, Material mat)
    {
        final BlockState blockData = ((NMSBlock_V26_2_R1) block).getMyBlockData();
        final CustomEntityFallingBlock_V26_2_R1 fBlockNMS =
            customEntityFallingBlockFactory.newEntityFallingBlock(
                loc.getWorld(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                blockData
            );

        return new CustomCraftFallingBlock_V26_2_R1((CraftServer) Bukkit.getServer(), fBlockNMS);
    }

    @Override
    public NMSBlock nmsBlockFactory(World world, int x, int y, int z)
    {
        final BlockBehaviour.Properties blockInfo =
            BlockBehaviour.Properties.ofFullCopy(((CraftWorld) world).getHandle().getBlockState(new BlockPos(x, y, z)).getBlock()).setId(blockInfoResourceKey);
        return new NMSBlock_V26_2_R1(world, x, y, z, blockInfo);
    }

    /**
     * Factory for creating {@link CustomEntityFallingBlock} instances.
     */
    interface CustomEntityFallingBlockFactory
    {
        CustomEntityFallingBlock_V26_2_R1 newEntityFallingBlock(
            World world,
            double x,
            double y,
            double z,
            BlockState blockData
        );
    }
}
