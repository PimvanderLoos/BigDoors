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

    private static final DataWatcherObject<ItemStack> DATA_ITEM_STACK = DataWatcher.defineId(EntityEnderSignal.class, DataWatcherRegistry.ITEM_STACK);
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
        this.setPos(d0, d1, d2);
    }

    public void setItem(ItemStack itemstack) {
        if (!itemstack.is(Items.ENDER_EYE) || itemstack.hasTag()) {
            this.getEntityData().set(EntityEnderSignal.DATA_ITEM_STACK, (ItemStack) SystemUtils.make(itemstack.copy(), (itemstack1) -> {
                itemstack1.setCount(1);
            }));
        }

    }

    private ItemStack getItemRaw() {
        return (ItemStack) this.getEntityData().get(EntityEnderSignal.DATA_ITEM_STACK);
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();

        return itemstack.isEmpty() ? new ItemStack(Items.ENDER_EYE) : itemstack;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(EntityEnderSignal.DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d0) {
        double d1 = this.getBoundingBox().getSize() * 4.0D;

        if (Double.isNaN(d1)) {
            d1 = 4.0D;
        }

        d1 *= 64.0D;
        return d0 < d1 * d1;
    }

    public void signalTo(BlockPosition blockposition) {
        double d0 = (double) blockposition.getX();
        int i = blockposition.getY();
        double d1 = (double) blockposition.getZ();
        double d2 = d0 - this.getX();
        double d3 = d1 - this.getZ();
        double d4 = Math.sqrt(d2 * d2 + d3 * d3);

        if (d4 > 12.0D) {
            this.tx = this.getX() + d2 / d4 * 12.0D;
            this.tz = this.getZ() + d3 / d4 * 12.0D;
            this.ty = this.getY() + 8.0D;
        } else {
            this.tx = d0;
            this.ty = (double) i;
            this.tz = d1;
        }

        this.life = 0;
        this.surviveAfterDeath = this.random.nextInt(5) > 0;
    }

    @Override
    public void lerpMotion(double d0, double d1, double d2) {
        this.setDeltaMovement(d0, d1, d2);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);

            this.setYRot((float) (MathHelper.atan2(d0, d2) * 57.2957763671875D));
            this.setXRot((float) (MathHelper.atan2(d1, d3) * 57.2957763671875D));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

    }

    @Override
    public void tick() {
        super.tick();
        Vec3D vec3d = this.getDeltaMovement();
        double d0 = this.getX() + vec3d.x;
        double d1 = this.getY() + vec3d.y;
        double d2 = this.getZ() + vec3d.z;
        double d3 = vec3d.horizontalDistance();

        this.setXRot(IProjectile.lerpRotation(this.xRotO, (float) (MathHelper.atan2(vec3d.y, d3) * 57.2957763671875D)));
        this.setYRot(IProjectile.lerpRotation(this.yRotO, (float) (MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D)));
        if (!this.level.isClientSide) {
            double d4 = this.tx - d0;
            double d5 = this.tz - d2;
            float f = (float) Math.sqrt(d4 * d4 + d5 * d5);
            float f1 = (float) MathHelper.atan2(d5, d4);
            double d6 = MathHelper.lerp(0.0025D, d3, (double) f);
            double d7 = vec3d.y;

            if (f < 1.0F) {
                d6 *= 0.8D;
                d7 *= 0.8D;
            }

            int i = this.getY() < this.ty ? 1 : -1;

            vec3d = new Vec3D(Math.cos((double) f1) * d6, d7 + ((double) i - d7) * 0.014999999664723873D, Math.sin((double) f1) * d6);
            this.setDeltaMovement(vec3d);
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
            this.setPos(d0, d1, d2);
            ++this.life;
            if (this.life > 80 && !this.level.isClientSide) {
                this.playSound(SoundEffects.ENDER_EYE_DEATH, 1.0F, 1.0F);
                this.discard();
                if (this.surviveAfterDeath) {
                    this.level.addFreshEntity(new EntityItem(this.level, this.getX(), this.getY(), this.getZ(), this.getItem()));
                } else {
                    this.level.levelEvent(2003, this.blockPosition(), 0);
                }
            }
        } else {
            this.setPosRaw(d0, d1, d2);
        }

    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        ItemStack itemstack = this.getItemRaw();

        if (!itemstack.isEmpty()) {
            nbttagcompound.put("Item", itemstack.save(new NBTTagCompound()));
        }

    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        ItemStack itemstack = ItemStack.of(nbttagcompound.getCompound("Item"));

        this.setItem(itemstack);
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new PacketPlayOutSpawnEntity(this);
    }
}
