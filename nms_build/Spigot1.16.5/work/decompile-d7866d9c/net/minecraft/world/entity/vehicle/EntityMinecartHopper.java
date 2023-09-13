package net.minecraft.world.entity.vehicle;

import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IInventory;
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

    private boolean d = true;
    private int e = -1;
    private final BlockPosition f;

    public EntityMinecartHopper(EntityTypes<? extends EntityMinecartHopper> entitytypes, World world) {
        super(entitytypes, world);
        this.f = BlockPosition.ZERO;
    }

    public EntityMinecartHopper(World world, double d0, double d1, double d2) {
        super(EntityTypes.HOPPER_MINECART, d0, d1, d2, world);
        this.f = BlockPosition.ZERO;
    }

    @Override
    public EntityMinecartAbstract.EnumMinecartType getMinecartType() {
        return EntityMinecartAbstract.EnumMinecartType.HOPPER;
    }

    @Override
    public IBlockData q() {
        return Blocks.HOPPER.getBlockData();
    }

    @Override
    public int s() {
        return 1;
    }

    @Override
    public int getSize() {
        return 5;
    }

    @Override
    public void a(int i, int j, int k, boolean flag) {
        boolean flag1 = !flag;

        if (flag1 != this.isEnabled()) {
            this.setEnabled(flag1);
        }

    }

    public boolean isEnabled() {
        return this.d;
    }

    public void setEnabled(boolean flag) {
        this.d = flag;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public double x() {
        return this.locX();
    }

    @Override
    public double z() {
        return this.locY() + 0.5D;
    }

    @Override
    public double A() {
        return this.locZ();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClientSide && this.isAlive() && this.isEnabled()) {
            BlockPosition blockposition = this.getChunkCoordinates();

            if (blockposition.equals(this.f)) {
                --this.e;
            } else {
                this.setCooldown(0);
            }

            if (!this.C()) {
                this.setCooldown(0);
                if (this.B()) {
                    this.setCooldown(4);
                    this.update();
                }
            }
        }

    }

    public boolean B() {
        if (TileEntityHopper.a((IHopper) this)) {
            return true;
        } else {
            List<EntityItem> list = this.world.a(EntityItem.class, this.getBoundingBox().grow(0.25D, 0.0D, 0.25D), IEntitySelector.a);

            if (!list.isEmpty()) {
                TileEntityHopper.a((IInventory) this, (EntityItem) list.get(0));
            }

            return false;
        }
    }

    @Override
    public void a(DamageSource damagesource) {
        super.a(damagesource);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.a((IMaterial) Blocks.HOPPER);
        }

    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("TransferCooldown", this.e);
        nbttagcompound.setBoolean("Enabled", this.d);
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.e = nbttagcompound.getInt("TransferCooldown");
        this.d = nbttagcompound.hasKey("Enabled") ? nbttagcompound.getBoolean("Enabled") : true;
    }

    public void setCooldown(int i) {
        this.e = i;
    }

    public boolean C() {
        return this.e > 0;
    }

    @Override
    public Container a(int i, PlayerInventory playerinventory) {
        return new ContainerHopper(i, playerinventory, this);
    }
}
