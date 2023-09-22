package nl.pim16aap2.bigDoors.NMS;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FixedValue;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * Represents a class that can generate a subclass of {@link CustomCraftFallingBlock_V1_20_R1} that overrides the
 * {@link org.bukkit.entity.Entity#getType()} method to return {@link EntityType#FALLING_BLOCK}.
 * <p>
 * Newer versions of Spigot/Paper/etc. have a final {@link org.bukkit.entity.Entity#getType()} method while older
 * versions only have an abstract one.
 */
class CustomCraftEntityGenerator_V1_20_R1
{
    /**
     * Gets a {@link FallingBlockFactory_V1_20_R1.CustomCraftFallingBlockFactory} that can be used to create new
     * {@link CustomCraftFallingBlock_V1_20_R1} instances.
     *
     * @return The supplier for new {@link CustomCraftFallingBlock_V1_20_R1} instances.
     */
    public FallingBlockFactory_V1_20_R1.CustomCraftFallingBlockFactory getCraftEntitySupplier()
    {
        final Constructor<CustomCraftFallingBlock_V1_20_R1> ctor = Objects.requireNonNull(
            getCraftEntityConstructor(), "Redefined CustomCraftFallingBlock constructor cannot be null!");

        return (server, entity) -> {
            try
            {
                return ctor.newInstance(server, entity);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to create a new CustomCraftFallingBlock instance!", e);
            }
        };
    }

    private Constructor<CustomCraftFallingBlock_V1_20_R1> getCraftEntityConstructor()
    {
        try
        {
            final Class<?> clz = getRedefinedClass();
            //noinspection unchecked
            return (Constructor<CustomCraftFallingBlock_V1_20_R1>)
                clz.getConstructor(CraftServer.class, CustomEntityFallingBlock_V1_20_R1.class);
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
           .subclass(CustomCraftFallingBlock_V1_20_R1.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
           .name("CustomCraftFallingBlock_V1_20_R1$generated");

       builder = builder
           .define(org.bukkit.entity.Entity.class.getMethod("getType"))
           .intercept(FixedValue.value(EntityType.FALLING_BLOCK));

        //noinspection resource
        return builder.make().load(getClass().getClassLoader()).getLoaded();
    }
}
