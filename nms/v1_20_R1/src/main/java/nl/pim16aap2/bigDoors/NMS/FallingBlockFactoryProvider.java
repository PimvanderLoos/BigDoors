package nl.pim16aap2.bigDoors.NMS;

import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Provides a {@link FallingBlockFactory} for version 1.20.R1.
 * <p>
 * The exact type of {@link FallingBlockFactory} returned depends on the specific build of Spigot/Paper/etc.
 * <p>
 * Use {@link #getFactory()} to get a new {@link FallingBlockFactory} instance.
 */
public class FallingBlockFactoryProvider
{
    /**
     * Creates a new {@link FallingBlockFactory} instance for version 1.20.R1 of Minecraft for the current server build.
     *
     * @return A new {@link FallingBlockFactory} instance.
     */
    public static FallingBlockFactory getFactory()
    {
        return new FallingBlockFactory_V1_20_R1(getCraftEntitySupplier());
    }

    private static FallingBlockFactory_V1_20_R1.CustomCraftFallingBlockFactory getCraftEntitySupplier()
    {
        if (methodGetTypeIsFinal())
            return CustomCraftFallingBlock_V1_20_R1::new;
        else
            return new CustomCraftEntityGenerator().getCraftEntitySupplier();
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
