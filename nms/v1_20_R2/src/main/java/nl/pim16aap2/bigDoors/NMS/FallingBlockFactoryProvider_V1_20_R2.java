package nl.pim16aap2.bigDoors.NMS;

import nl.pim16aap2.bigDoors.ILogger;

/**
 * Provides a specialized {@link FallingBlockFactory} for the given version.
 * <p>
 * The exact type of {@link FallingBlockFactory} returned depends on the specific build of Spigot/Paper/etc.
 * <p>
 * Use {@link #getFactory(ILogger)} to get a new {@link FallingBlockFactory} instance.
 */
public class FallingBlockFactoryProvider_V1_20_R2
{
    /**
     * Creates a new {@link FallingBlockFactory} instance for a specific version of Minecraft for the current server build.
     *
     * @return A new {@link FallingBlockFactory} instance.
     */
    public static FallingBlockFactory getFactory(ILogger logger)
    {
        return new FallingBlockFactory_V1_20_R2(
            new CustomEntityFallingBlockGenerator_V1_20_R2().getEntityFallingBlockSupplier(),
            logger
        );
    }
}
