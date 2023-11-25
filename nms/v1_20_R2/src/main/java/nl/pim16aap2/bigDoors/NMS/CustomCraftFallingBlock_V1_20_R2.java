package nl.pim16aap2.bigDoors.NMS;

import nl.pim16aap2.bigDoors.ILogger;
import nl.pim16aap2.bigDoors.util.ILoggableDoor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomCraftFallingBlock_V1_20_R2 extends CraftEntity implements FallingBlock, CustomCraftFallingBlock
{
    private final ILogger logger;
    private final ILoggableDoor door;
    private final CustomEntityFallingBlock_V1_20_R2 customEntity;

    public CustomCraftFallingBlock_V1_20_R2(
        CraftServer server,
        CustomEntityFallingBlock_V1_20_R2 entity,
        ILogger logger,
        ILoggableDoor door)
    {
        super(server, entity);
        this.customEntity = entity;
        this.logger = logger;
        this.door = door;
        setVelocity(new Vector(0, 0, 0));

        log("Created new CustomCraftFallingBlock_V1_20_R2 instance for block: " +
                customEntity.getBlock().toString());
    }

    private void log(String msg)
    {
        log(false, msg);
    }

    private void log(boolean dumpStack, String msg)
    {
        final @Nullable Throwable throwable = dumpStack ? new Exception("Stack trace") : null;
        logger.logMessageToLogFileForDoor(door, throwable, String.format(
            "Animated Block [%s]: %s",
            getUniqueId(),
            msg
        ));
    }

    @Override
    public ILoggableDoor getDoor()
    {
        return door;
    }

    @Override
    public void privateRemove()
    {
        this.customEntity.privateRemoval();
    }

    @Override
    public CustomEntityFallingBlock_V1_20_R2 getHandle()
    {
        return (CustomEntityFallingBlock_V1_20_R2) entity;
    }

    @Override
    public boolean isOnGround()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "CraftFallingBlock";
    }

    @Override
    public void remove()
    {
        log(true, "Attempted to remove animated falling block!");
        super.remove();
    }

    @Override
    public void setVisibleByDefault(boolean flag)
    {
        log("Attempted to set visibility of animated falling block to '" + flag + "'!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.setVisibleByDefault() is not supported.");
    }

    @Override
    public boolean isVisibleByDefault()
    {
        return true;
    }

    @Override
    public boolean isInWorld()
    {
        log("isInWorld() called for animated falling block!");
        return customEntity.isInWorld();
    }

    @Override
    public EntitySnapshot createSnapshot()
    {
        log("Attempted to create snapshot of animated falling block!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.createSnapshot() is not supported.");
    }

    @Override
    public Entity copy()
    {
        log("Attempted to copy animated falling block!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.copy() is not supported.");
    }

    @Override
    public Entity copy(@NotNull Location location)
    {
        log("Attempted to copy animated falling block at location '" + location + "'!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.copy(Location) is not supported.");
    }

    @Override
    @Deprecated
    public @NotNull Material getMaterial()
    {
        log(true, "Attempted to get material of animated falling block!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.getMaterial() is not supported.");
    }

    @Override
    public @NotNull BlockData getBlockData()
    {
        log(true, "Attempted to get block data of animated falling block!");
        return CraftBlockData.fromData(this.getHandle().t());
    }

    @Override
    public boolean getDropItem()
    {
        return false;
    }

    @Override
    public void setDropItem(final boolean drop)
    {
        log("Attempted to set drop item of animated falling block to '" + drop + "'!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.setDropItem() is not supported.");
    }

    @Override
    public boolean getCancelDrop()
    {
        return true;
    }

    @Override
    public void setCancelDrop(boolean b)
    {
        log("Attempted to set cancel drop of animated falling block to '" + b + "'!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.setCancelDrop() is not supported.");
    }

    @Override
    public boolean canHurtEntities()
    {
        return false;
    }

    @Override
    public void setHurtEntities(final boolean hurtEntities)
    {
        log("Attempted to set hurt entities of animated falling block to '" + hurtEntities + "'!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.setHurtEntities() is not supported.");
    }

    @Override
    public float getDamagePerBlock()
    {
        return 0;
    }

    @Override
    public void setDamagePerBlock(float damage)
    {
        log("Attempted to set damage per block of animated falling block to '" + damage + "'!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.setDamagePerBlock() is not supported.");
    }

    @Override
    public int getMaxDamage()
    {
        return 0;
    }

    @Override
    public void setMaxDamage(int damage)
    {
        log("Attempted to set max damage of animated falling block to '" + damage + "'!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.setMaxDamage() is not supported.");
    }

    @Override
    public void setTicksLived(final int value)
    {
        if (value > 10_000)
            log(true, "Attempted to set ticks lived of animated falling block to '" + value + "'!");

        super.setTicksLived(value);
        getHandle().b = value;
    }

    @Override
    public void setHeadPose(EulerAngle pose)
    {
        log("Attempted to set head pose of animated falling block to '" + pose + "'!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.setHeadPose() is not supported.");
    }

    @Override
    public void setBodyPose(EulerAngle eulerAngle)
    {
        log("Attempted to set body pose of animated falling block to '" + eulerAngle + "'!");
        throw new UnsupportedOperationException("CustomCraftFallingBlock_V1_20_R2.setBodyPose() is not supported.");
    }
}
