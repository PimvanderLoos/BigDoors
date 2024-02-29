package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.server.v1_16_R3.BlockBase;
import net.minecraft.server.v1_16_R3.BlockBase.Info;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class FallingBlockFactory_V1_16_R3 implements FallingBlockFactory
{
    // Make a falling block.
    @Override
    public CustomCraftFallingBlock createFallingBlock(Location loc, NMSBlock block, byte matData, Material mat)
    {
        IBlockData blockData = ((NMSBlock_V1_16_R3) block).getMyBlockData();
        CustomEntityFallingBlock_V1_16_R3 fBlockNMS = new CustomEntityFallingBlock_V1_16_R3(loc.getWorld(), loc.getX(),
                                                                                            loc.getY(), loc.getZ(),
                                                                                            blockData);
        return new CustomCraftFallingBlock_V1_16_R3(Bukkit.getServer(), fBlockNMS);
    }

    @Override
    public NMSBlock nmsBlockFactory(World world, int x, int y, int z)
    {

        Info blockInfo = net.minecraft.server.v1_16_R3.BlockBase.Info
            .a((BlockBase) ((CraftWorld) world).getHandle().getType(new BlockPosition(x, y, z)).getBlock());
        return new NMSBlock_V1_16_R3(world, x, y, z, blockInfo);
    }
}
