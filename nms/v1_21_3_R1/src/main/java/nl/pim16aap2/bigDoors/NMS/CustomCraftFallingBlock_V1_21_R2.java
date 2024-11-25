package nl.pim16aap2.bigDoors.NMS;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_21_R2.CraftServer;
import org.bukkit.craftbukkit.v1_21_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CustomCraftFallingBlock_V1_21_R2 extends CraftEntity implements FallingBlock, CustomCraftFallingBlock
{
    public CustomCraftFallingBlock_V1_21_R2(final CraftServer server, final CustomEntityFallingBlock_V1_21_R2 entity)
    {
        super(server, entity);
        setVelocity(new Vector(0, 0, 0));
        setDropItem(false);
    }

    @Override
    public CustomEntityFallingBlock_V1_21_R2 getHandle()
    {
        return (CustomEntityFallingBlock_V1_21_R2) entity;
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

    @Override
    public boolean isInWorld()
    {
        return false;
    }

    @Override
    public EntitySnapshot createSnapshot()
    {
        throw new UnsupportedOperationException("Not implemented for animated blocks!");
    }

    @Override
    public Entity copy()
    {
        throw new UnsupportedOperationException("Not implemented for animated blocks!");
    }

    @Override
    public Entity copy(@NotNull Location location)
    {
        throw new UnsupportedOperationException("Not implemented for animated blocks!");
    }

    @Override
    @Deprecated
    public @NotNull Material getMaterial()
    {
        return CraftMagicNumbers.getMaterial(getHandle().p()).getItemType();
    }

    @Override
    public @NotNull BlockData getBlockData()
    {
        return CraftBlockData.fromData(this.getHandle().p());
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
        getHandle().a = value;
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
