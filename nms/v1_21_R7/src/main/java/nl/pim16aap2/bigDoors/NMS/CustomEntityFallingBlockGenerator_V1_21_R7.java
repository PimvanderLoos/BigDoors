package nl.pim16aap2.bigDoors.NMS;

/**
 * Represents a class that can generate a subclass of {@link CustomEntityFallingBlock_V1_21_R7}.
 */
class CustomEntityFallingBlockGenerator_V1_21_R7
{
    /**
     * Gets a {@link FallingBlockFactory_V1_21_R7.CustomEntityFallingBlockFactory} that can be used to create new
     * {@link CustomEntityFallingBlock_V1_21_R7} instances.
     *
     * @return The supplier for new {@link CustomEntityFallingBlock_V1_21_R7} instances.
     */
    public FallingBlockFactory_V1_21_R7.CustomEntityFallingBlockFactory getEntityFallingBlockSupplier()
    {
        return (world, x, y, z, blockData) -> {
            try
            {
                return new CustomEntityFallingBlock_V1_21_R7(world, x, y, z, blockData);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to create a new CustomEntityFallingBlock instance!", e);
            }
        };
    }
}
