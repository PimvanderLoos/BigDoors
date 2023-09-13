package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;

public class EntityFireworks extends IProjectile implements ItemSupplier {

    public static final DataWatcherObject<ItemStack> DATA_ID_FIREWORKS_ITEM = DataWatcher.a(EntityFireworks.class, DataWatcherRegistry.ITEM_STACK);
    private static final DataWatcherObject<OptionalInt> DATA_ATTACHED_TO_TARGET = DataWatcher.a(EntityFireworks.class, DataWatcherRegistry.OPTIONAL_UNSIGNED_INT);
    public static final DataWatcherObject<Boolean> DATA_SHOT_AT_ANGLE = DataWatcher.a(EntityFireworks.class, DataWatcherRegistry.BOOLEAN);
    private int life;
    public int lifetime;
    @Nullable
    private EntityLiving attachedToEntity;

    public EntityFireworks(EntityTypes<? extends EntityFireworks> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityFireworks(World world, double d0, double d1, double d2, ItemStack itemstack) {
        super(EntityTypes.FIREWORK_ROCKET, world);
        this.life = 0;
        this.setPosition(d0, d1, d2);
        int i = 1;

        if (!itemstack.isEmpty() && itemstack.hasTag()) {
            this.entityData.set(EntityFireworks.DATA_ID_FIREWORKS_ITEM, itemstack.cloneItemStack());
            i += itemstack.a("Fireworks").getByte("Flight");
        }

        this.setMot(this.random.nextGaussian() * 0.001D, 0.05D, this.random.nextGaussian() * 0.001D);
        this.lifetime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
    }

    public EntityFireworks(World world, @Nullable Entity entity, double d0, double d1, double d2, ItemStack itemstack) {
        this(world, d0, d1, d2, itemstack);
        this.setShooter(entity);
    }

    public EntityFireworks(World world, ItemStack itemstack, EntityLiving entityliving) {
        this(world, entityliving, entityliving.locX(), entityliving.locY(), entityliving.locZ(), itemstack);
        this.entityData.set(EntityFireworks.DATA_ATTACHED_TO_TARGET, OptionalInt.of(entityliving.getId()));
        this.attachedToEntity = entityliving;
    }

    public EntityFireworks(World world, ItemStack itemstack, double d0, double d1, double d2, boolean flag) {
        this(world, d0, d1, d2, itemstack);
        this.entityData.set(EntityFireworks.DATA_SHOT_AT_ANGLE, flag);
    }

    public EntityFireworks(World world, ItemStack itemstack, Entity entity, double d0, double d1, double d2, boolean flag) {
        this(world, itemstack, d0, d1, d2, flag);
        this.setShooter(entity);
    }

    @Override
    protected void initDatawatcher() {
        this.entityData.register(EntityFireworks.DATA_ID_FIREWORKS_ITEM, ItemStack.EMPTY);
        this.entityData.register(EntityFireworks.DATA_ATTACHED_TO_TARGET, OptionalInt.empty());
        this.entityData.register(EntityFireworks.DATA_SHOT_AT_ANGLE, false);
    }

    @Override
    public boolean a(double d0) {
        return d0 < 4096.0D && !this.o();
    }

    @Override
    public boolean j(double d0, double d1, double d2) {
        return super.j(d0, d1, d2) && !this.o();
    }

    @Override
    public void tick() {
        super.tick();
        Vec3D vec3d;

        if (this.o()) {
            if (this.attachedToEntity == null) {
                ((OptionalInt) this.entityData.get(EntityFireworks.DATA_ATTACHED_TO_TARGET)).ifPresent((i) -> {
                    Entity entity = this.level.getEntity(i);

                    if (entity instanceof EntityLiving) {
                        this.attachedToEntity = (EntityLiving) entity;
                    }

                });
            }

            if (this.attachedToEntity != null) {
                if (this.attachedToEntity.isGliding()) {
                    vec3d = this.attachedToEntity.getLookDirection();
                    double d0 = 1.5D;
                    double d1 = 0.1D;
                    Vec3D vec3d1 = this.attachedToEntity.getMot();

                    this.attachedToEntity.setMot(vec3d1.add(vec3d.x * 0.1D + (vec3d.x * 1.5D - vec3d1.x) * 0.5D, vec3d.y * 0.1D + (vec3d.y * 1.5D - vec3d1.y) * 0.5D, vec3d.z * 0.1D + (vec3d.z * 1.5D - vec3d1.z) * 0.5D));
                }

                this.setPosition(this.attachedToEntity.locX(), this.attachedToEntity.locY(), this.attachedToEntity.locZ());
                this.setMot(this.attachedToEntity.getMot());
            }
        } else {
            if (!this.isShotAtAngle()) {
                double d2 = this.horizontalCollision ? 1.0D : 1.15D;

                this.setMot(this.getMot().d(d2, 1.0D, d2).add(0.0D, 0.04D, 0.0D));
            }

            vec3d = this.getMot();
            this.move(EnumMoveType.SELF, vec3d);
            this.setMot(vec3d);
        }

        MovingObjectPosition movingobjectposition = ProjectileHelper.a((Entity) this, this::a);

        if (!this.noPhysics) {
            this.a(movingobjectposition);
            this.hasImpulse = true;
        }

        this.z();
        if (this.life == 0 && !this.isSilent()) {
            this.level.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
        }

        ++this.life;
        if (this.level.isClientSide && this.life % 2 < 2) {
            this.level.addParticle(Particles.FIREWORK, this.locX(), this.locY() - 0.3D, this.locZ(), this.random.nextGaussian() * 0.05D, -this.getMot().y * 0.5D, this.random.nextGaussian() * 0.05D);
        }

        if (!this.level.isClientSide && this.life > this.lifetime) {
            this.explode();
        }

    }

    private void explode() {
        this.level.broadcastEntityEffect(this, (byte) 17);
        this.a(GameEvent.EXPLODE, this.getShooter());
        this.n();
        this.die();
    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        super.a(movingobjectpositionentity);
        if (!this.level.isClientSide) {
            this.explode();
        }
    }

    @Override
    protected void a(MovingObjectPositionBlock movingobjectpositionblock) {
        BlockPosition blockposition = new BlockPosition(movingobjectpositionblock.getBlockPosition());

        this.level.getType(blockposition).a(this.level, blockposition, (Entity) this);
        if (!this.level.isClientSide() && this.hasExplosions()) {
            this.explode();
        }

        super.a(movingobjectpositionblock);
    }

    private boolean hasExplosions() {
        ItemStack itemstack = (ItemStack) this.entityData.get(EntityFireworks.DATA_ID_FIREWORKS_ITEM);
        NBTTagCompound nbttagcompound = itemstack.isEmpty() ? null : itemstack.b("Fireworks");
        NBTTagList nbttaglist = nbttagcompound != null ? nbttagcompound.getList("Explosions", 10) : null;

        return nbttaglist != null && !nbttaglist.isEmpty();
    }

    private void n() {
        float f = 0.0F;
        ItemStack itemstack = (ItemStack) this.entityData.get(EntityFireworks.DATA_ID_FIREWORKS_ITEM);
        NBTTagCompound nbttagcompound = itemstack.isEmpty() ? null : itemstack.b("Fireworks");
        NBTTagList nbttaglist = nbttagcompound != null ? nbttagcompound.getList("Explosions", 10) : null;

        if (nbttaglist != null && !nbttaglist.isEmpty()) {
            f = 5.0F + (float) (nbttaglist.size() * 2);
        }

        if (f > 0.0F) {
            if (this.attachedToEntity != null) {
                this.attachedToEntity.damageEntity(DamageSource.a(this, this.getShooter()), 5.0F + (float) (nbttaglist.size() * 2));
            }

            double d0 = 5.0D;
            Vec3D vec3d = this.getPositionVector();
            List<EntityLiving> list = this.level.a(EntityLiving.class, this.getBoundingBox().g(5.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityLiving entityliving = (EntityLiving) iterator.next();

                if (entityliving != this.attachedToEntity && this.f(entityliving) <= 25.0D) {
                    boolean flag = false;

                    for (int i = 0; i < 2; ++i) {
                        Vec3D vec3d1 = new Vec3D(entityliving.locX(), entityliving.e(0.5D * (double) i), entityliving.locZ());
                        MovingObjectPositionBlock movingobjectpositionblock = this.level.rayTrace(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this));

                        if (movingobjectpositionblock.getType() == MovingObjectPosition.EnumMovingObjectType.MISS) {
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        float f1 = f * (float) Math.sqrt((5.0D - (double) this.e((Entity) entityliving)) / 5.0D);

                        entityliving.damageEntity(DamageSource.a(this, this.getShooter()), f1);
                    }
                }
            }
        }

    }

    private boolean o() {
        return ((OptionalInt) this.entityData.get(EntityFireworks.DATA_ATTACHED_TO_TARGET)).isPresent();
    }

    public boolean isShotAtAngle() {
        return (Boolean) this.entityData.get(EntityFireworks.DATA_SHOT_AT_ANGLE);
    }

    @Override
    public void a(byte b0) {
        if (b0 == 17 && this.level.isClientSide) {
            if (!this.hasExplosions()) {
                for (int i = 0; i < this.random.nextInt(3) + 2; ++i) {
                    this.level.addParticle(Particles.POOF, this.locX(), this.locY(), this.locZ(), this.random.nextGaussian() * 0.05D, 0.005D, this.random.nextGaussian() * 0.05D);
                }
            } else {
                ItemStack itemstack = (ItemStack) this.entityData.get(EntityFireworks.DATA_ID_FIREWORKS_ITEM);
                NBTTagCompound nbttagcompound = itemstack.isEmpty() ? null : itemstack.b("Fireworks");
                Vec3D vec3d = this.getMot();

                this.level.a(this.locX(), this.locY(), this.locZ(), vec3d.x, vec3d.y, vec3d.z, nbttagcompound);
            }
        }

        super.a(b0);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Life", this.life);
        nbttagcompound.setInt("LifeTime", this.lifetime);
        ItemStack itemstack = (ItemStack) this.entityData.get(EntityFireworks.DATA_ID_FIREWORKS_ITEM);

        if (!itemstack.isEmpty()) {
            nbttagcompound.set("FireworksItem", itemstack.save(new NBTTagCompound()));
        }

        nbttagcompound.setBoolean("ShotAtAngle", (Boolean) this.entityData.get(EntityFireworks.DATA_SHOT_AT_ANGLE));
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.life = nbttagcompound.getInt("Life");
        this.lifetime = nbttagcompound.getInt("LifeTime");
        ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("FireworksItem"));

        if (!itemstack.isEmpty()) {
            this.entityData.set(EntityFireworks.DATA_ID_FIREWORKS_ITEM, itemstack);
        }

        if (nbttagcompound.hasKey("ShotAtAngle")) {
            this.entityData.set(EntityFireworks.DATA_SHOT_AT_ANGLE, nbttagcompound.getBoolean("ShotAtAngle"));
        }

    }

    @Override
    public ItemStack getSuppliedItem() {
        ItemStack itemstack = (ItemStack) this.entityData.get(EntityFireworks.DATA_ID_FIREWORKS_ITEM);

        return itemstack.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : itemstack;
    }

    @Override
    public boolean ca() {
        return false;
    }
}
