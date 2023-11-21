package nl.pim16aap2.bigDoors.NMS;

import nl.pim16aap2.bigDoors.util.ILoggableDoor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public interface FallingBlockFactory
{
    CustomCraftFallingBlock fallingBlockFactory(
        Location loc,
        NMSBlock block,
        byte matData,
        Material mat,
        ILoggableDoor door);

    NMSBlock nmsBlockFactory(World world, int x, int y, int z, ILoggableDoor door);
}
