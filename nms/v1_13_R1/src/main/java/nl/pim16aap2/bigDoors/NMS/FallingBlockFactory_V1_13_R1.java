package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.server.v1_13_R1.Block;
import net.minecraft.server.v1_13_R1.IBlockData;
import nl.pim16aap2.bigDoors.util.ILoggableDoor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class FallingBlockFactory_V1_13_R1 implements FallingBlockFactory
{
    // Make a falling block.
    @Override
    public CustomCraftFallingBlock fallingBlockFactory(Location loc, NMSBlock block, byte matData, Material mat,
        ILoggableDoor door)
    {
        IBlockData blockData = ((Block) block).getBlockData();
        CustomEntityFallingBlock_V1_13_R1 fBlockNMS = new CustomEntityFallingBlock_V1_13_R1(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), blockData);
        CustomCraftFallingBlock_V1_13_R1 entity = new CustomCraftFallingBlock_V1_13_R1(Bukkit.getServer(), fBlockNMS);
        entity.setCustomName("BigDoorsEntity");
        entity.setCustomNameVisible(false);
        return entity;
    }

    @Override
    public NMSBlock nmsBlockFactory(World world, int x, int y, int z, ILoggableDoor door)
    {
        return new NMSBlock_V1_13_R1(world, x, y, z);
    }
}
