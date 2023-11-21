package nl.pim16aap2.bigDoors.NMS;

import nl.pim16aap2.bigDoors.ILogger;
import nl.pim16aap2.bigDoors.util.ILoggableDoor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftMagicNumbers;
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
        setDropItem(false);
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
    public void setVisibleByDefault(boolean flag)
    {
    }

    @Override
    public boolean isVisibleByDefault()
    {
        return true;
    }

    @Override public boolean isInWorld()
    {
        return false;
    }

    @Nullable @Override public EntitySnapshot createSnapshot()
    {
        return null;
    }

    @NotNull @Override public Entity copy()
    {
        return null;
    }

    @NotNull @Override public Entity copy(@NotNull Location location)
    {
        return null;
    }

    @Override
    @Deprecated
    public @NotNull Material getMaterial()
    {
        return CraftMagicNumbers.getMaterial(getHandle().t()).getItemType();
    }

    @Override
    public @NotNull BlockData getBlockData()
    {
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
    }

    @Override
    public boolean getCancelDrop()
    {
        return true;
    }

    @Override
    public void setCancelDrop(boolean b)
    {

    }

    @Override
    public boolean canHurtEntities()
    {
        return false;
    }

    @Override
    public void setHurtEntities(final boolean hurtEntities)
    {
    }

    @Override
    public float getDamagePerBlock()
    {
        return 0;
    }

    @Override
    public void setDamagePerBlock(float damage)
    {
    }

    @Override
    public int getMaxDamage()
    {
        return 0;
    }

    @Override
    public void setMaxDamage(int damage)
    {
    }

    @Override
    public void setTicksLived(final int value)
    {
        super.setTicksLived(value);
        getHandle().b = value;
    }

    @Override
    public void setHeadPose(EulerAngle pose)
    {
    }

    @Override
    public void setBodyPose(EulerAngle eulerAngle)
    {
    }
}
