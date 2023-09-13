package net.minecraft.world.entity.vehicle;

import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerHopper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.IHopper;
import net.minecraft.world.level.block.entity.TileEntityHopper;
import net.minecraft.world.level.block.state.IBlockData;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper {

    public static final int MOVE_ITEM_SPEED = 4;
    private boolean enabled = true;
    private int cooldownTime = -1;
    private final BlockPosition lastPosition;

    public EntityMinecartHopper(EntityTypes<? extends EntityMinecartHopper> entitytypes, World world) {
        super(entitytypes, world);
        this.lastPosition = BlockPosition.ZERO;
    }

    public EntityMinecartHopper(World world, double d0, double d1, double d2) {
        super(EntityTypes.HOPPER_MINECART, d0, d1, d2, world);
        this.lastPosition = BlockPosition.ZERO;
    }

    @Override
    public EntityMinecartAbstract.EnumMinecartType getMinecartType() {
        return EntityMinecartAbstract.EnumMinecartType.HOPPER;
    }

    @Override
    public IBlockData getDefaultDisplayBlockState() {
        return Blocks.HOPPER.defaultBlockState();
    }

    @Override
    public int getDefaultDisplayOffset() {
        return 1;
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    @Override
    public void activateMinecart(int i, int j, int k, boolean flag) {
        boolean flag1 = !flag;

        if (flag1 != this.isEnabled()) {
            this.setEnabled(flag1);
        }

    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean flag) {
        this.enabled = flag;
    }

    @Override
    public double getLevelX() {
        return this.getX();
    }

    @Override
    public double getLevelY() {
        return this.getY() + 0.5D;
    }

    @Override
    public double getLevelZ() {
        return this.getZ();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && this.isAlive() && this.isEnabled()) {
            BlockPosition blockposition = this.blockPosition();

            if (blockposition.equals(this.lastPosition)) {
                --this.cooldownTime;
            } else {
                this.setCooldown(0);
            }

            if (!this.isOnCooldown()) {
                this.setCooldown(0);
                if (this.suckInItems()) {
                    this.setCooldown(4);
                    this.setChanged();
                }
            }
        }

    }

    public boolean suckInItems() {
        if (TileEntityHopper.suckInItems(this.level, this)) {
            return true;
        } else {
            List<EntityItem> list = this.level.getEntitiesOfClass(EntityItem.class, this.getBoundingBox().inflate(0.25D, 0.0D, 0.25D), IEntitySelector.ENTITY_STILL_ALIVE);

            if (!list.isEmpty()) {
                TileEntityHopper.addItem(this, (EntityItem) list.get(0));
            }

            return false;
        }
    }

    @Override
    public void destroy(DamageSource damagesource) {
        super.destroy(damagesource);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation((IMaterial) Blocks.HOPPER);
        }

    }

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("TransferCooldown", this.cooldownTime);
        nbttagcompound.putBoolean("Enabled", this.enabled);
    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.cooldownTime = nbttagcompound.getInt("TransferCooldown");
        this.enabled = nbttagcompound.contains("Enabled") ? nbttagcompound.getBoolean("Enabled") : true;
    }

    public void setCooldown(int i) {
        this.cooldownTime = i;
    }

    public boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    @Override
    public Container createMenu(int i, PlayerInventory playerinventory) {
        return new ContainerHopper(i, playerinventory, this);
    }
}
