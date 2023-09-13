package net.minecraft.server;

import java.util.List;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper {

    private boolean d = true;
    private int e = -1;
    private final BlockPosition f;

    public EntityMinecartHopper(World world) {
        super(EntityTypes.HOPPER_MINECART, world);
        this.f = BlockPosition.ZERO;
    }

    public EntityMinecartHopper(World world, double d0, double d1, double d2) {
        super(EntityTypes.HOPPER_MINECART, d0, d1, d2, world);
        this.f = BlockPosition.ZERO;
    }

    public EntityMinecartAbstract.EnumMinecartType v() {
        return EntityMinecartAbstract.EnumMinecartType.HOPPER;
    }

    public IBlockData z() {
        return Blocks.HOPPER.getBlockData();
    }

    public int B() {
        return 1;
    }

    public int getSize() {
        return 5;
    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        if (!this.world.isClientSide) {
            entityhuman.openContainer(this);
        }

        return true;
    }

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

    public World getWorld() {
        return this.world;
    }

    public double G() {
        return this.locX;
    }

    public double H() {
        return this.locY + 0.5D;
    }

    public double I() {
        return this.locZ;
    }

    public void tick() {
        super.tick();
        if (!this.world.isClientSide && this.isAlive() && this.isEnabled()) {
            BlockPosition blockposition = new BlockPosition(this);

            if (blockposition.equals(this.f)) {
                --this.e;
            } else {
                this.setCooldown(0);
            }

            if (!this.K()) {
                this.setCooldown(0);
                if (this.J()) {
                    this.setCooldown(4);
                    this.update();
                }
            }
        }

    }

    public boolean J() {
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

    public void a(DamageSource damagesource) {
        super.a(damagesource);
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.a((IMaterial) Blocks.HOPPER);
        }

    }

    protected void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("TransferCooldown", this.e);
        nbttagcompound.setBoolean("Enabled", this.d);
    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.e = nbttagcompound.getInt("TransferCooldown");
        this.d = nbttagcompound.hasKey("Enabled") ? nbttagcompound.getBoolean("Enabled") : true;
    }

    public void setCooldown(int i) {
        this.e = i;
    }

    public boolean K() {
        return this.e > 0;
    }

    public String getContainerName() {
        return "minecraft:hopper";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerHopper(playerinventory, this, entityhuman);
    }
}
