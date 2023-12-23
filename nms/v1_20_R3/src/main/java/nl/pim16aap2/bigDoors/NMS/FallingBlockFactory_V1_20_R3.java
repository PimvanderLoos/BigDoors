package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockBase.Info;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;

class FallingBlockFactory_V1_20_R3 implements FallingBlockFactory
{
    private final CustomEntityFallingBlockFactory customEntityFallingBlockFactory;

    FallingBlockFactory_V1_20_R3(CustomEntityFallingBlockFactory customEntityFallingBlockFactory)
    {
        this.customEntityFallingBlockFactory = customEntityFallingBlockFactory;
    }

    @Override
    public CustomCraftFallingBlock fallingBlockFactory(Location loc, NMSBlock block, byte matData, Material mat)
    {
        final IBlockData blockData = ((NMSBlock_V1_20_R3) block).getMyBlockData();
        final CustomEntityFallingBlock_V1_20_R3 fBlockNMS =
            customEntityFallingBlockFactory.newEntityFallingBlock(
                loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), blockData);

        final CustomCraftFallingBlock_V1_20_R3 entity =
            new CustomCraftFallingBlock_V1_20_R3((CraftServer) Bukkit.getServer(), fBlockNMS);
        entity.setCustomName("BigDoorsEntity");
        entity.setCustomNameVisible(false);
        return entity;
    }

    @Override
    public NMSBlock nmsBlockFactory(World world, int x, int y, int z)
    {
        final Info blockInfo =
            Info.a((BlockBase) ((CraftWorld) world).getHandle().a_(new BlockPosition(x, y, z)).b());
        return new NMSBlock_V1_20_R3(world, x, y, z, blockInfo);
    }

    /**
     * Factory for creating {@link CustomEntityFallingBlock} instances.
     */
    interface CustomEntityFallingBlockFactory
    {
        CustomEntityFallingBlock_V1_20_R3 newEntityFallingBlock(
            World world, double x, double y, double z, IBlockData blockData);
    }
}
