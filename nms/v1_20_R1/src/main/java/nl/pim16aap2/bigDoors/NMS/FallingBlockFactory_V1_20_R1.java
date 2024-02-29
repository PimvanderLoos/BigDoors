package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockBase.Info;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

class FallingBlockFactory_V1_20_R1 implements FallingBlockFactory
{
    private final CustomCraftFallingBlockFactory customCraftEntityConstructor;

    FallingBlockFactory_V1_20_R1(CustomCraftFallingBlockFactory customCraftEntityConstructor)
    {
        this.customCraftEntityConstructor = customCraftEntityConstructor;
    }

    @Override
    public CustomCraftFallingBlock createFallingBlock(Location loc, NMSBlock block, byte matData, Material mat)
    {
        final IBlockData blockData = ((NMSBlock_V1_20_R1) block).getMyBlockData();
        final CustomEntityFallingBlock_V1_20_R1 fBlockNMS
            = new CustomEntityFallingBlock_V1_20_R1(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), blockData);

        return customCraftEntityConstructor.newCraftEntity((CraftServer) Bukkit.getServer(), fBlockNMS);
    }

    @Override
    public NMSBlock nmsBlockFactory(World world, int x, int y, int z)
    {
        final Info blockInfo =
            Info.a((BlockBase) ((CraftWorld) world).getHandle().a_(new BlockPosition(x, y, z)).b());
        return new NMSBlock_V1_20_R1(world, x, y, z, blockInfo);
    }

    interface CustomCraftFallingBlockFactory
    {
        CustomCraftFallingBlock_V1_20_R1 newCraftEntity(CraftServer server, CustomEntityFallingBlock_V1_20_R1 entity);
    }
}
