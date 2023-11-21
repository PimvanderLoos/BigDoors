package nl.pim16aap2.bigDoors.NMS;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.IBlockData;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * Represents a class that can generate a subclass of {@link CustomEntityFallingBlock_V1_20_R2} that uses the correct
 * method to obtain the delta movement vector.
 * <p>
 * The obfuscated method is called 'Entity#do()', which we cannot use, as 'do' is a reserved keyword in Java.
 */
class CustomEntityFallingBlockGenerator_V1_20_R2
{
    /**
     * Gets a {@link FallingBlockFactory_V1_20_R2.CustomEntityFallingBlockFactory} that can be used to create new
     * {@link CustomEntityFallingBlock_V1_20_R2} instances.
     *
     * @return The supplier for new {@link CustomEntityFallingBlock_V1_20_R2} instances.
     */
    public FallingBlockFactory_V1_20_R2.CustomEntityFallingBlockFactory getEntityFallingBlockSupplier()
    {
        final Constructor<CustomEntityFallingBlock_V1_20_R2> ctor = Objects.requireNonNull(
            getEntityFallingBlockConstructor(), "Redefined CustomEntityFallingBlock constructor cannot be null!");

        return (logger, door, world, x, y, z, blockData) -> {
            try
            {
                return ctor.newInstance(logger, door, world, x, y, z, blockData);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to create a new CustomEntityFallingBlock instance!", e);
            }
        };
    }

    private Constructor<CustomEntityFallingBlock_V1_20_R2> getEntityFallingBlockConstructor()
    {
        try
        {
            final Class<?> clz = getRedefinedClass();
            //noinspection unchecked
            return (Constructor<CustomEntityFallingBlock_V1_20_R2>)
                clz.getConstructor(
                    nl.pim16aap2.bigDoors.ILogger.class,
                    nl.pim16aap2.bigDoors.util.ILoggableDoor.class,
                    org.bukkit.World.class,
                    double.class,
                    double.class,
                    double.class,
                    IBlockData.class
                );
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to get the redefined class.", e);
        }
    }

    private Class<?> getRedefinedClass()
        throws NoSuchMethodException
    {
       DynamicType.Builder<?> builder = new ByteBuddy()
           .subclass(CustomEntityFallingBlock_V1_20_R2.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
           .name("CustomEntityFallingBlock_V1_20_R2$generated");

       builder = builder
           .define(CustomEntityFallingBlock_V1_20_R2.class.getMethod("getDeltaMovement0"))
           .intercept(MethodCall.invoke(Entity.class.getMethod("do")));

        //noinspection resource
        return builder.make().load(getClass().getClassLoader()).getLoaded();
    }
}
