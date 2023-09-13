package net.minecraft.server;

import java.util.List;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper {

    private boolean a = true;
    private int b = -1;
    private final BlockPosition c;

    public EntityMinecartHopper(World world) {
        super(world);
        this.c = BlockPosition.ZERO;
    }

    public EntityMinecartHopper(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
        this.c = BlockPosition.ZERO;
    }

    public EntityMinecartAbstract.EnumMinecartType v() {
        return EntityMinecartAbstract.EnumMinecartType.HOPPER;
    }

    public IBlockData x() {
        return Blocks.HOPPER.getBlockData();
    }

    public int z() {
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
        return this.a;
    }

    public void setEnabled(boolean flag) {
        this.a = flag;
    }

    public World getWorld() {
        return this.world;
    }

    public double E() {
        return this.locX;
    }

    public double F() {
        return this.locY + 0.5D;
    }

    public double G() {
        return this.locZ;
    }

    public void B_() {
        super.B_();
        if (!this.world.isClientSide && this.isAlive() && this.isEnabled()) {
            BlockPosition blockposition = new BlockPosition(this);

            if (blockposition.equals(this.c)) {
                --this.b;
            } else {
                this.setCooldown(0);
            }

            if (!this.J()) {
                this.setCooldown(0);
                if (this.H()) {
                    this.setCooldown(4);
                    this.update();
                }
            }
        }

    }

    public boolean H() {
        if (TileEntityHopper.a((IHopper) this)) {
            return true;
        } else {
            List list = this.world.a(EntityItem.class, this.getBoundingBox().grow(0.25D, 0.0D, 0.25D), IEntitySelector.a);

            if (!list.isEmpty()) {
                TileEntityHopper.a((IInventory) null, this, (EntityItem) list.get(0));
            }

            return false;
        }
    }

    public void a(DamageSource damagesource) {
        super.a(damagesource);
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.a(Item.getItemOf(Blocks.HOPPER), 1, 0.0F);
        }

    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityMinecartContainer.b(dataconvertermanager, EntityMinecartHopper.class);
    }

    protected void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("TransferCooldown", this.b);
        nbttagcompound.setBoolean("Enabled", this.a);
    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.b = nbttagcompound.getInt("TransferCooldown");
        this.a = nbttagcompound.hasKey("Enabled") ? nbttagcompound.getBoolean("Enabled") : true;
    }

    public void setCooldown(int i) {
        this.b = i;
    }

    public boolean J() {
        return this.b > 0;
    }

    public String getContainerName() {
        return "minecraft:hopper";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerHopper(playerinventory, this, entityhuman);
    }
}
