package nl.pim16aap2.bigDoors.NMS;

import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class FallingBlockFactoryProvider
{
    public static FallingBlockFactory getFactory()
    {
        if (methodGetTypeIsFinal())
            return new FallingBlockFactory_V1_20_R1();
        else
            throw new UnsupportedOperationException(
                "This version of Spigot/Paper/etc is not supported! Please update your server to a more recent build!");
    }

    /**
     * Checks the method {@link CraftEntity#getType()} to see if it is final.
     *
     * @return True if the method is final, false otherwise.
     */
    private static boolean methodGetTypeIsFinal()
    {
        try
        {
            final Method method = CraftEntity.class.getMethod("getType");
            return Modifier.isFinal(method.getModifiers());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to analyze method CraftEntity#getType().", e);
        }
    }
}
