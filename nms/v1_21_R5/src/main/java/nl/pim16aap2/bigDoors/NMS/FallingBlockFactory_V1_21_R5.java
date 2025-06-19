package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBase.Info;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;

class FallingBlockFactory_V1_21_R5 implements FallingBlockFactory
{
    private final CustomEntityFallingBlockFactory customEntityFallingBlockFactory;
    private final ResourceKey<Block> blockInfoResourceKey;

    FallingBlockFactory_V1_21_R5(CustomEntityFallingBlockFactory customEntityFallingBlockFactory)
    {
        this.customEntityFallingBlockFactory = customEntityFallingBlockFactory;
        this.blockInfoResourceKey = ResourceKey.a(Registries.i, MinecraftKey.b("big-doors", "block-info"));
    }

    @Override
    public CustomCraftFallingBlock createFallingBlock(Location loc, NMSBlock block, byte matData, Material mat)
    {
        final IBlockData blockData = ((NMSBlock_V1_21_R5) block).getMyBlockData();
        final CustomEntityFallingBlock_V1_21_R5 fBlockNMS =
            customEntityFallingBlockFactory.newEntityFallingBlock(
                loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), blockData);

        return new CustomCraftFallingBlock_V1_21_R5((CraftServer) Bukkit.getServer(), fBlockNMS);
    }

    @Override
    public NMSBlock nmsBlockFactory(World world, int x, int y, int z)
    {
        final Info blockInfo =
            Info.a(((CraftWorld) world).getHandle().a_(new BlockPosition(x, y, z)).b()).a(blockInfoResourceKey);
        return new NMSBlock_V1_21_R5(world, x, y, z, blockInfo);
    }

    /**
     * Factory for creating {@link CustomEntityFallingBlock} instances.
     */
    interface CustomEntityFallingBlockFactory
    {
        CustomEntityFallingBlock_V1_21_R5 newEntityFallingBlock(
            World world, double x, double y, double z, IBlockData blockData);
    }
}
