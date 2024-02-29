package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockBase.Info;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;

public class FallingBlockFactory_V1_19_R2 implements FallingBlockFactory
{
    @Override
    public CustomCraftFallingBlock createFallingBlock(Location loc, NMSBlock block, byte matData, Material mat)
    {
        IBlockData blockData = ((NMSBlock_V1_19_R2) block).getMyBlockData();
        CustomEntityFallingBlock_V1_19_R2 fBlockNMS
            = new CustomEntityFallingBlock_V1_19_R2(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), blockData);
        return new CustomCraftFallingBlock_V1_19_R2(Bukkit.getServer(), fBlockNMS);
    }

    @Override
    public NMSBlock nmsBlockFactory(World world, int x, int y, int z)
    {
        final Info blockInfo =
            Info.a((BlockBase) ((CraftWorld) world).getHandle().a_(new BlockPosition(x, y, z)).b());
        return new NMSBlock_V1_19_R2(world, x, y, z, blockInfo);
    }
}
