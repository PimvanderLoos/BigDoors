package nl.pim16aap2.bigDoors.NMS;

/**
 * Represents a class that can generate a subclass of {@link CustomEntityFallingBlock_V1_21_R6}.
 */
class CustomEntityFallingBlockGenerator_V1_21_R6
{
    /**
     * Gets a {@link FallingBlockFactory_V1_21_R6.CustomEntityFallingBlockFactory} that can be used to create new
     * {@link CustomEntityFallingBlock_V1_21_R6} instances.
     *
     * @return The supplier for new {@link CustomEntityFallingBlock_V1_21_R6} instances.
     */
    public FallingBlockFactory_V1_21_R6.CustomEntityFallingBlockFactory getEntityFallingBlockSupplier()
    {
        return (world, x, y, z, blockData) -> {
            try
            {
                return new CustomEntityFallingBlock_V1_21_R6(world, x, y, z, blockData);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to create a new CustomEntityFallingBlock instance!", e);
            }
        };
    }
}
