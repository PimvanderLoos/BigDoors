package nl.pim16aap2.bigDoors.NMS;

import nl.pim16aap2.bigDoors.util.LazyInit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface FallingBlockFactory
{
    /**
     * Whether the server supports the PersistentDataContainer.
     */
    boolean SUPPORTS_PERSISTENT_DATA_CONTAINER = Helper.supportsPersistentDataContainer();

    /**
     * The custom name of the falling block entity.
     * <p>
     * This used to be the recommended way to identify the falling block as a BigDoorsEntity.
     * <p>
     * By default, this is no longer used on newer versions of Minecraft.
     * <p>
     * Instead, use {@link #ENTITY_KEY} to identify the falling block as a BigDoorsEntity.
     */
    String ENTITY_NAME = "BigDoorsEntity";

    /**
     * The key for the metadata of the falling block entity.
     * <p>
     * This is used to identify the falling block as a BigDoorsEntity.
     */
    LazyInit<NamespacedKey> ENTITY_KEY = new LazyInit<>(
        () -> new NamespacedKey(
            Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("BigDoors")),
            "bigdoors_entity"));

    /**
     * Use {@link #createFallingBlockWithMetadata(Specification, Location, NMSBlock, byte, Material)} instead.
     */
    CustomCraftFallingBlock createFallingBlock(Location loc, NMSBlock block, byte matData, Material mat);

    /**
     * Creates a falling block with metadata.
     *
     * @param data The data for the falling block factory.
     * @param loc The location to spawn the falling block at.
     * @param block The block to spawn as a falling block.
     * @param matData The material data of the falling block.
     * @param mat The material of the falling block.
     * @return The falling block entity.
     */
    default CustomCraftFallingBlock createFallingBlockWithMetadata(
        Specification data,
        Location loc,
        NMSBlock block,
        byte matData,
        Material mat)
    {
        final CustomCraftFallingBlock entity = createFallingBlock(loc, block, matData, mat);
        setFallingBlockMetadata(data, entity);
        return entity;
    }

    /**
     * Sets the metadata of the falling block.
     * <p>
     * This is used to identify the falling block as a BigDoorsEntity.
     *
     * @param entity The falling block to set the metadata for.
     */
    default void setFallingBlockMetadata(Specification data, CustomCraftFallingBlock entity)
    {
        if (data.setCustomName)
        {
            entity.setCustomName(ENTITY_NAME);
            entity.setCustomNameVisible(false);
        }

        if (SUPPORTS_PERSISTENT_DATA_CONTAINER)
            entity.getPersistentDataContainer().set(ENTITY_KEY.get(), PersistentDataType.BYTE, (byte) 1);
    }

    NMSBlock nmsBlockFactory(World world, int x, int y, int z);

    /**
     * Checks whether the entity is a BigDoorsEntity.
     *
     * @param entity The entity to check.
     * @return True if the entity is a BigDoorsEntity, false otherwise.
     */
    default boolean isBigDoorsEntity(@Nullable Entity entity)
    {
        if (entity == null)
            return false;

        if (ENTITY_NAME.equals(entity.getCustomName()))
            return true;

        return SUPPORTS_PERSISTENT_DATA_CONTAINER &&
            entity.getPersistentDataContainer().has(ENTITY_KEY.get(), PersistentDataType.BYTE);
    }

    /**
     * Data for the falling block factory.
     */
    final class Specification
    {
        private final boolean setCustomName;

        /**
         * Creates a new instance of the falling block factory data.
         *
         * @param setCustomName
         *   Whether to set the custom name of the falling block.
         */
        public Specification(boolean setCustomName)
        {
            this.setCustomName = setCustomName || !SUPPORTS_PERSISTENT_DATA_CONTAINER;
        }
    }

    /**
     * Helper class.
     */
    final class Helper
    {
        /**
         * Checks whether the server supports the PersistentDataContainer.
         *
         * @return True if the server supports the PersistentDataContainer, false otherwise.
         */
        private static boolean supportsPersistentDataContainer()
        {
            try
            {
                Class.forName("org.bukkit.persistence.PersistentDataContainer");
                return true;
            }
            catch (ClassNotFoundException ignored)
            {
                return false;
            }
        }
    }
}
