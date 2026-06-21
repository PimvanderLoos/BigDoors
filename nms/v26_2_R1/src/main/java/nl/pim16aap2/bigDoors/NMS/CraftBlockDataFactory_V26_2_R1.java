package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

import java.lang.reflect.Method;
import java.util.function.Function;

final class CraftBlockDataFactory_V26_2_R1
{
    private static final Function<BlockState, CraftBlockData> FROM_STATE = findFromStateFunction();

    private CraftBlockDataFactory_V26_2_R1()
    {
    }

    public static CraftBlockData fromState(BlockState state)
    {
        return FROM_STATE.apply(state);
    }

    static boolean isInitialized()
    {
        return true;
    }

    private static Function<BlockState, CraftBlockData> findFromStateFunction()
    {
        // On Spigot, we can use CraftBlockData#fromData(BlockState) without reflection.
        // On Paper, it's CraftBlockData#createData(BlockState), which is not present in Spigot, so we'll use reflection.
        try
        {
            CraftBlockData.class.getMethod("fromData", BlockState.class);
            return CraftBlockData::fromData;
        }
        catch (NoSuchMethodException ignored)
        {
            // Ignored
        }

        try
        {
            Method method = CraftBlockData.class.getMethod("createData", BlockState.class);
            method.setAccessible(true);
            return state ->
            {
                try
                {
                    return (CraftBlockData) method.invoke(null, state);
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Failed to create block data from state: " + state, e);
                }
            };
        }
        catch (NoSuchMethodException ignored)
        {
            // ignored
        }
        throw new IllegalStateException("Failed to find method to create CraftBlockData from state");
    }
}
