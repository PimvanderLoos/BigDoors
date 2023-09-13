package net.minecraft.world.entity.projectile;

import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class EntityEnderSignal extends Entity implements ItemSupplier {

    private static final DataWatcherObject<ItemStack> DATA_ITEM_STACK = DataWatcher.a(EntityEnderSignal.class, DataWatcherRegistry.ITEM_STACK);
    public double tx;
    public double ty;
    public double tz;
    public int life;
    public boolean surviveAfterDeath;

    public EntityEnderSignal(EntityTypes<? extends EntityEnderSignal> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityEnderSignal(World world, double d0, double d1, double d2) {
        this(EntityTypes.EYE_OF_ENDER, world);
        this.setPosition(d0, d1, d2);
    }

    public void setItem(ItemStack itemstack) {
        if (!itemstack.a(Items.ENDER_EYE) || itemstack.hasTag()) {
            this.getDataWatcher().set(EntityEnderSignal.DATA_ITEM_STACK, (ItemStack) SystemUtils.a((Object) itemstack.cloneItemStack(), (itemstack1) -> {
                itemstack1.setCount(1);
            }));
        }

    }

    private ItemStack i() {
        return (ItemStack) this.getDataWatcher().get(EntityEnderSignal.DATA_ITEM_STACK);
    }

    @Override
    public ItemStack getSuppliedItem() {
        ItemStack itemstack = this.i();

        return itemstack.isEmpty() ? new ItemStack(Items.ENDER_EYE) : itemstack;
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityEnderSignal.DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public boolean a(double d0) {
        double d1 = this.getBoundingBox().a() * 4.0D;

        if (Double.isNaN(d1)) {
            d1 = 4.0D;
        }

        d1 *= 64.0D;
        return d0 < d1 * d1;
    }

    public void a(BlockPosition blockposition) {
        double d0 = (double) blockposition.getX();
        int i = blockposition.getY();
        double d1 = (double) blockposition.getZ();
        double d2 = d0 - this.locX();
        double d3 = d1 - this.locZ();
        double d4 = Math.sqrt(d2 * d2 + d3 * d3);

        if (d4 > 12.0D) {
            this.tx = this.locX() + d2 / d4 * 12.0D;
            this.tz = this.locZ() + d3 / d4 * 12.0D;
            this.ty = this.locY() + 8.0D;
        } else {
            this.tx = d0;
            this.ty = (double) i;
            this.tz = d1;
        }

        this.life = 0;
        this.surviveAfterDeath = this.random.nextInt(5) > 0;
    }

    @Override
    public void k(double d0, double d1, double d2) {
        this.setMot(d0, d1, d2);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);

            this.setYRot((float) (MathHelper.d(d0, d2) * 57.2957763671875D));
            this.setXRot((float) (MathHelper.d(d1, d3) * 57.2957763671875D));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

    }

    @Override
    public void tick() {
        super.tick();
        Vec3D vec3d = this.getMot();
        double d0 = this.locX() + vec3d.x;
        double d1 = this.locY() + vec3d.y;
        double d2 = this.locZ() + vec3d.z;
        double d3 = vec3d.h();

        this.setXRot(IProjectile.d(this.xRotO, (float) (MathHelper.d(vec3d.y, d3) * 57.2957763671875D)));
        this.setYRot(IProjectile.d(this.yRotO, (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D)));
        if (!this.level.isClientSide) {
            double d4 = this.tx - d0;
            double d5 = this.tz - d2;
            float f = (float) Math.sqrt(d4 * d4 + d5 * d5);
            float f1 = (float) MathHelper.d(d5, d4);
            double d6 = MathHelper.d(0.0025D, d3, (double) f);
            double d7 = vec3d.y;

            if (f < 1.0F) {
                d6 *= 0.8D;
                d7 *= 0.8D;
            }

            int i = this.locY() < this.ty ? 1 : -1;

            vec3d = new Vec3D(Math.cos((double) f1) * d6, d7 + ((double) i - d7) * 0.014999999664723873D, Math.sin((double) f1) * d6);
            this.setMot(vec3d);
        }

        float f2 = 0.25F;

        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                this.level.addParticle(Particles.BUBBLE, d0 - vec3d.x * 0.25D, d1 - vec3d.y * 0.25D, d2 - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
            }
        } else {
            this.level.addParticle(Particles.PORTAL, d0 - vec3d.x * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, d1 - vec3d.y * 0.25D - 0.5D, d2 - vec3d.z * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, vec3d.x, vec3d.y, vec3d.z);
        }

        if (!this.level.isClientSide) {
            this.setPosition(d0, d1, d2);
            ++this.life;
            if (this.life > 80 && !this.level.isClientSide) {
                this.playSound(SoundEffects.ENDER_EYE_DEATH, 1.0F, 1.0F);
                this.die();
                if (this.surviveAfterDeath) {
                    this.level.addEntity(new EntityItem(this.level, this.locX(), this.locY(), this.locZ(), this.getSuppliedItem()));
                } else {
                    this.level.triggerEffect(2003, this.getChunkCoordinates(), 0);
                }
            }
        } else {
            this.setPositionRaw(d0, d1, d2);
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        ItemStack itemstack = this.i();

        if (!itemstack.isEmpty()) {
            nbttagcompound.set("Item", itemstack.save(new NBTTagCompound()));
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("Item"));

        this.setItem(itemstack);
    }

    @Override
    public float aY() {
        return 1.0F;
    }

    @Override
    public boolean ca() {
        return false;
    }

    @Override
    public Packet<?> getPacket() {
        return new PacketPlayOutSpawnEntity(this);
    }
}
