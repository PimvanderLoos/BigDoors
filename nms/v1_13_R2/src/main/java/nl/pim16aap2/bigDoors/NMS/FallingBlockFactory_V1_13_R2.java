package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.server.v1_13_R2.Block;
import net.minecraft.server.v1_13_R2.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class FallingBlockFactory_V1_13_R2 implements FallingBlockFactory
{
    // Make a falling block.
    @Override
    public CustomCraftFallingBlock createFallingBlock(Location loc, NMSBlock block, byte matData, Material mat)
    {
        IBlockData blockData = ((Block) block).getBlockData();
        CustomEntityFallingBlock_V1_13_R2 fBlockNMS = new CustomEntityFallingBlock_V1_13_R2(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), blockData);
        return new CustomCraftFallingBlock_V1_13_R2(Bukkit.getServer(), fBlockNMS);
    }

    @Override
    public NMSBlock nmsBlockFactory(World world, int x, int y, int z)
    {
        return new NMSBlock_V1_13_R2(world, x, y, z);
    }
}
