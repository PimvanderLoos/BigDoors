package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockBase.Info;
import net.minecraft.world.level.block.state.IBlockData;
import nl.pim16aap2.bigDoors.ILogger;
import nl.pim16aap2.bigDoors.util.ILoggableDoor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;

class FallingBlockFactory_V1_20_R2 implements FallingBlockFactory
{
    private final CustomEntityFallingBlockFactory customEntityFallingBlockFactory;
    private final ILogger logger;

    FallingBlockFactory_V1_20_R2(CustomEntityFallingBlockFactory customEntityFallingBlockFactory, ILogger logger)
    {
        this.customEntityFallingBlockFactory = customEntityFallingBlockFactory;
        this.logger = logger;
    }

    @Override
    public CustomCraftFallingBlock fallingBlockFactory(
        Location loc,
        NMSBlock block,
        byte matData,
        Material mat,
        ILoggableDoor door)
    {
        final IBlockData blockData = ((NMSBlock_V1_20_R2) block).getMyBlockData();
        final CustomEntityFallingBlock_V1_20_R2 fBlockNMS =
            customEntityFallingBlockFactory.newEntityFallingBlock(
                logger, door,
                loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), blockData);

        final CustomCraftFallingBlock_V1_20_R2 entity =
            new CustomCraftFallingBlock_V1_20_R2((CraftServer) Bukkit.getServer(), fBlockNMS, logger, door);
        entity.setCustomName("BigDoorsEntity");
        entity.setCustomNameVisible(false);
        return entity;
    }

    @Override
    public NMSBlock nmsBlockFactory(World world, int x, int y, int z, ILoggableDoor door)
    {
        final Info blockInfo =
            Info.a((BlockBase) ((CraftWorld) world).getHandle().a_(new BlockPosition(x, y, z)).b());
        return new NMSBlock_V1_20_R2(logger, door, world, x, y, z, blockInfo);
    }

    /**
     * Factory for creating {@link CustomEntityFallingBlock} instances.
     */
    interface CustomEntityFallingBlockFactory
    {
        CustomEntityFallingBlock_V1_20_R2 newEntityFallingBlock(
            ILogger logger, ILoggableDoor door,
            World world, double x, double y, double z, IBlockData blockData);
    }
}
