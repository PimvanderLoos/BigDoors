package NMS;

/**
 * Represents a class that can generate a subclass of {@link CustomEntityFallingBlock_V1_20_R4}.
 */
class CustomEntityFallingBlockGenerator_V1_20_R4
{
    /**
     * Gets a {@link FallingBlockFactory_V1_20_R4.CustomEntityFallingBlockFactory} that can be used to create new
     * {@link CustomEntityFallingBlock_V1_20_R4} instances.
     *
     * @return The supplier for new {@link CustomEntityFallingBlock_V1_20_R4} instances.
     */
    public FallingBlockFactory_V1_20_R4.CustomEntityFallingBlockFactory getEntityFallingBlockSupplier()
    {
        return (world, x, y, z, blockData) -> {
            try
            {
                return new CustomEntityFallingBlock_V1_20_R4(world, x, y, z, blockData);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to create a new CustomEntityFallingBlock instance!", e);
            }
        };
    }
}
