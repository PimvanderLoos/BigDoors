package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.server.v1_15_R1.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class FallingBlockFactory_V1_15_R1 implements FallingBlockFactory
{
    // Make a falling block.
    @Override
    public CustomCraftFallingBlock createFallingBlock(Location loc, NMSBlock block, byte matData, Material mat)
    {
        IBlockData blockData = ((NMSBlock_V1_15_R1) block).getMyBlockData();
        CustomEntityFallingBlock_V1_15_R1 fBlockNMS = new CustomEntityFallingBlock_V1_15_R1(loc.getWorld(), loc.getX(),
                                                                                            loc.getY(), loc.getZ(),
                                                                                            blockData);
        return new CustomCraftFallingBlock_V1_15_R1(Bukkit.getServer(), fBlockNMS);
    }

    @Override
    public NMSBlock nmsBlockFactory(World world, int x, int y, int z)
    {
        return new NMSBlock_V1_15_R1(world, x, y, z);
    }
}
