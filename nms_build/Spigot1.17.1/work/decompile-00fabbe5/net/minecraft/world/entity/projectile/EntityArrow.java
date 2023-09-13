package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class EntityArrow extends IProjectile {

    private static final double ARROW_BASE_DAMAGE = 2.0D;
    private static final DataWatcherObject<Byte> ID_FLAGS = DataWatcher.a(EntityArrow.class, DataWatcherRegistry.BYTE);
    private static final DataWatcherObject<Byte> PIERCE_LEVEL = DataWatcher.a(EntityArrow.class, DataWatcherRegistry.BYTE);
    private static final int FLAG_CRIT = 1;
    private static final int FLAG_NOPHYSICS = 2;
    private static final int FLAG_CROSSBOW = 4;
    @Nullable
    private IBlockData lastState;
    public boolean inGround;
    protected int inGroundTime;
    public EntityArrow.PickupStatus pickup;
    public int shakeTime;
    public int life;
    private double baseDamage;
    public int knockback;
    private SoundEffect soundEvent;
    @Nullable
    private IntOpenHashSet piercingIgnoreEntityIds;
    @Nullable
    private List<Entity> piercedAndKilledEntities;

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, World world) {
        super(entitytypes, world);
        this.pickup = EntityArrow.PickupStatus.DISALLOWED;
        this.baseDamage = 2.0D;
        this.soundEvent = this.i();
    }

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world) {
        this(entitytypes, world);
        this.setPosition(d0, d1, d2);
    }

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, EntityLiving entityliving, World world) {
        this(entitytypes, entityliving.locX(), entityliving.getHeadY() - 0.10000000149011612D, entityliving.locZ(), world);
        this.setShooter(entityliving);
        if (entityliving instanceof EntityHuman) {
            this.pickup = EntityArrow.PickupStatus.ALLOWED;
        }

    }

    public void a(SoundEffect soundeffect) {
        this.soundEvent = soundeffect;
    }

    @Override
    public boolean a(double d0) {
        double d1 = this.getBoundingBox().a() * 10.0D;

        if (Double.isNaN(d1)) {
            d1 = 1.0D;
        }

        d1 *= 64.0D * cl();
        return d0 < d1 * d1;
    }

    @Override
    protected void initDatawatcher() {
        this.entityData.register(EntityArrow.ID_FLAGS, (byte) 0);
        this.entityData.register(EntityArrow.PIERCE_LEVEL, (byte) 0);
    }

    @Override
    public void shoot(double d0, double d1, double d2, float f, float f1) {
        super.shoot(d0, d1, d2, f, f1);
        this.life = 0;
    }

    @Override
    public void a(double d0, double d1, double d2, float f, float f1, int i, boolean flag) {
        this.setPosition(d0, d1, d2);
        this.setYawPitch(f, f1);
    }

    @Override
    public void k(double d0, double d1, double d2) {
        super.k(d0, d1, d2);
        this.life = 0;
    }

    @Override
    public void tick() {
        super.tick();
        boolean flag = this.t();
        Vec3D vec3d = this.getMot();

        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3d.h();

            this.setYRot((float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D));
            this.setXRot((float) (MathHelper.d(vec3d.y, d0) * 57.2957763671875D));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPosition blockposition = this.getChunkCoordinates();
        IBlockData iblockdata = this.level.getType(blockposition);
        Vec3D vec3d1;

        if (!iblockdata.isAir() && !flag) {
            VoxelShape voxelshape = iblockdata.getCollisionShape(this.level, blockposition);

            if (!voxelshape.isEmpty()) {
                vec3d1 = this.getPositionVector();
                Iterator iterator = voxelshape.toList().iterator();

                while (iterator.hasNext()) {
                    AxisAlignedBB axisalignedbb = (AxisAlignedBB) iterator.next();

                    if (axisalignedbb.a(blockposition).d(vec3d1)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain() || iblockdata.a(Blocks.POWDER_SNOW)) {
            this.extinguish();
        }

        if (this.inGround && !flag) {
            if (this.lastState != iblockdata && this.v()) {
                this.A();
            } else if (!this.level.isClientSide) {
                this.h();
            }

            ++this.inGroundTime;
        } else {
            this.inGroundTime = 0;
            Vec3D vec3d2 = this.getPositionVector();

            vec3d1 = vec3d2.e(vec3d);
            Object object = this.level.rayTrace(new RayTrace(vec3d2, vec3d1, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this));

            if (((MovingObjectPosition) object).getType() != MovingObjectPosition.EnumMovingObjectType.MISS) {
                vec3d1 = ((MovingObjectPosition) object).getPos();
            }

            while (!this.isRemoved()) {
                MovingObjectPositionEntity movingobjectpositionentity = this.a(vec3d2, vec3d1);

                if (movingobjectpositionentity != null) {
                    object = movingobjectpositionentity;
                }

                if (object != null && ((MovingObjectPosition) object).getType() == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
                    Entity entity = ((MovingObjectPositionEntity) object).getEntity();
                    Entity entity1 = this.getShooter();

                    if (entity instanceof EntityHuman && entity1 instanceof EntityHuman && !((EntityHuman) entity1).a((EntityHuman) entity)) {
                        object = null;
                        movingobjectpositionentity = null;
                    }
                }

                if (object != null && !flag) {
                    this.a((MovingObjectPosition) object);
                    this.hasImpulse = true;
                }

                if (movingobjectpositionentity == null || this.getPierceLevel() <= 0) {
                    break;
                }

                object = null;
            }

            vec3d = this.getMot();
            double d1 = vec3d.x;
            double d2 = vec3d.y;
            double d3 = vec3d.z;

            if (this.isCritical()) {
                for (int i = 0; i < 4; ++i) {
                    this.level.addParticle(Particles.CRIT, this.locX() + d1 * (double) i / 4.0D, this.locY() + d2 * (double) i / 4.0D, this.locZ() + d3 * (double) i / 4.0D, -d1, -d2 + 0.2D, -d3);
                }
            }

            double d4 = this.locX() + d1;
            double d5 = this.locY() + d2;
            double d6 = this.locZ() + d3;
            double d7 = vec3d.h();

            if (flag) {
                this.setYRot((float) (MathHelper.d(-d1, -d3) * 57.2957763671875D));
            } else {
                this.setYRot((float) (MathHelper.d(d1, d3) * 57.2957763671875D));
            }

            this.setXRot((float) (MathHelper.d(d2, d7) * 57.2957763671875D));
            this.setXRot(d(this.xRotO, this.getXRot()));
            this.setYRot(d(this.yRotO, this.getYRot()));
            float f = 0.99F;
            float f1 = 0.05F;

            if (this.isInWater()) {
                for (int j = 0; j < 4; ++j) {
                    float f2 = 0.25F;

                    this.level.addParticle(Particles.BUBBLE, d4 - d1 * 0.25D, d5 - d2 * 0.25D, d6 - d3 * 0.25D, d1, d2, d3);
                }

                f = this.s();
            }

            this.setMot(vec3d.a((double) f));
            if (!this.isNoGravity() && !flag) {
                Vec3D vec3d3 = this.getMot();

                this.setMot(vec3d3.x, vec3d3.y - 0.05000000074505806D, vec3d3.z);
            }

            this.setPosition(d4, d5, d6);
            this.checkBlockCollisions();
        }
    }

    private boolean v() {
        return this.inGround && this.level.b((new AxisAlignedBB(this.getPositionVector(), this.getPositionVector())).g(0.06D));
    }

    private void A() {
        this.inGround = false;
        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.d((double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F)));
        this.life = 0;
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        super.move(enummovetype, vec3d);
        if (enummovetype != EnumMoveType.SELF && this.v()) {
            this.A();
        }

    }

    protected void h() {
        ++this.life;
        if (this.life >= 1200) {
            this.die();
        }

    }

    private void B() {
        if (this.piercedAndKilledEntities != null) {
            this.piercedAndKilledEntities.clear();
        }

        if (this.piercingIgnoreEntityIds != null) {
            this.piercingIgnoreEntityIds.clear();
        }

    }

    @Override
    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        super.a(movingobjectpositionentity);
        Entity entity = movingobjectpositionentity.getEntity();
        float f = (float) this.getMot().f();
        int i = MathHelper.e(MathHelper.a((double) f * this.baseDamage, 0.0D, 2.147483647E9D));

        if (this.getPierceLevel() > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }

            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                this.die();
                return;
            }

            this.piercingIgnoreEntityIds.add(entity.getId());
        }

        if (this.isCritical()) {
            long j = (long) this.random.nextInt(i / 2 + 2);

            i = (int) Math.min(j + (long) i, 2147483647L);
        }

        Entity entity1 = this.getShooter();
        DamageSource damagesource;

        if (entity1 == null) {
            damagesource = DamageSource.arrow(this, this);
        } else {
            damagesource = DamageSource.arrow(this, entity1);
            if (entity1 instanceof EntityLiving) {
                ((EntityLiving) entity1).x(entity);
            }
        }

        boolean flag = entity.getEntityType() == EntityTypes.ENDERMAN;
        int k = entity.getFireTicks();

        if (this.isBurning() && !flag) {
            entity.setOnFire(5);
        }

        if (entity.damageEntity(damagesource, (float) i)) {
            if (flag) {
                return;
            }

            if (entity instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) entity;

                if (!this.level.isClientSide && this.getPierceLevel() <= 0) {
                    entityliving.setArrowCount(entityliving.getArrowCount() + 1);
                }

                if (this.knockback > 0) {
                    Vec3D vec3d = this.getMot().d(1.0D, 0.0D, 1.0D).d().a((double) this.knockback * 0.6D);

                    if (vec3d.g() > 0.0D) {
                        entityliving.i(vec3d.x, 0.1D, vec3d.z);
                    }
                }

                if (!this.level.isClientSide && entity1 instanceof EntityLiving) {
                    EnchantmentManager.a(entityliving, entity1);
                    EnchantmentManager.b((EntityLiving) entity1, (Entity) entityliving);
                }

                this.a(entityliving);
                if (entity1 != null && entityliving != entity1 && entityliving instanceof EntityHuman && entity1 instanceof EntityPlayer && !this.isSilent()) {
                    ((EntityPlayer) entity1).connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.ARROW_HIT_PLAYER, 0.0F));
                }

                if (!entity.isAlive() && this.piercedAndKilledEntities != null) {
                    this.piercedAndKilledEntities.add(entityliving);
                }

                if (!this.level.isClientSide && entity1 instanceof EntityPlayer) {
                    EntityPlayer entityplayer = (EntityPlayer) entity1;

                    if (this.piercedAndKilledEntities != null && this.isShotFromCrossbow()) {
                        CriterionTriggers.KILLED_BY_CROSSBOW.a(entityplayer, (Collection) this.piercedAndKilledEntities);
                    } else if (!entity.isAlive() && this.isShotFromCrossbow()) {
                        CriterionTriggers.KILLED_BY_CROSSBOW.a(entityplayer, (Collection) Arrays.asList(entity));
                    }
                }
            }

            this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0) {
                this.die();
            }
        } else {
            entity.setFireTicks(k);
            this.setMot(this.getMot().a(-0.1D));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (!this.level.isClientSide && this.getMot().g() < 1.0E-7D) {
                if (this.pickup == EntityArrow.PickupStatus.ALLOWED) {
                    this.a(this.getItemStack(), 0.1F);
                }

                this.die();
            }
        }

    }

    @Override
    protected void a(MovingObjectPositionBlock movingobjectpositionblock) {
        this.lastState = this.level.getType(movingobjectpositionblock.getBlockPosition());
        super.a(movingobjectpositionblock);
        Vec3D vec3d = movingobjectpositionblock.getPos().a(this.locX(), this.locY(), this.locZ());

        this.setMot(vec3d);
        Vec3D vec3d1 = vec3d.d().a(0.05000000074505806D);

        this.setPositionRaw(this.locX() - vec3d1.x, this.locY() - vec3d1.y, this.locZ() - vec3d1.z);
        this.playSound(this.getSoundHit(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.inGround = true;
        this.shakeTime = 7;
        this.setCritical(false);
        this.setPierceLevel((byte) 0);
        this.a(SoundEffects.ARROW_HIT);
        this.setShotFromCrossbow(false);
        this.B();
    }

    protected SoundEffect i() {
        return SoundEffects.ARROW_HIT;
    }

    protected final SoundEffect getSoundHit() {
        return this.soundEvent;
    }

    protected void a(EntityLiving entityliving) {}

    @Nullable
    protected MovingObjectPositionEntity a(Vec3D vec3d, Vec3D vec3d1) {
        return ProjectileHelper.a(this.level, this, vec3d, vec3d1, this.getBoundingBox().b(this.getMot()).g(1.0D), this::a);
    }

    @Override
    protected boolean a(Entity entity) {
        return super.a(entity) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId()));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setShort("life", (short) this.life);
        if (this.lastState != null) {
            nbttagcompound.set("inBlockState", GameProfileSerializer.a(this.lastState));
        }

        nbttagcompound.setByte("shake", (byte) this.shakeTime);
        nbttagcompound.setBoolean("inGround", this.inGround);
        nbttagcompound.setByte("pickup", (byte) this.pickup.ordinal());
        nbttagcompound.setDouble("damage", this.baseDamage);
        nbttagcompound.setBoolean("crit", this.isCritical());
        nbttagcompound.setByte("PierceLevel", this.getPierceLevel());
        nbttagcompound.setString("SoundEvent", IRegistry.SOUND_EVENT.getKey(this.soundEvent).toString());
        nbttagcompound.setBoolean("ShotFromCrossbow", this.isShotFromCrossbow());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.life = nbttagcompound.getShort("life");
        if (nbttagcompound.hasKeyOfType("inBlockState", 10)) {
            this.lastState = GameProfileSerializer.c(nbttagcompound.getCompound("inBlockState"));
        }

        this.shakeTime = nbttagcompound.getByte("shake") & 255;
        this.inGround = nbttagcompound.getBoolean("inGround");
        if (nbttagcompound.hasKeyOfType("damage", 99)) {
            this.baseDamage = nbttagcompound.getDouble("damage");
        }

        this.pickup = EntityArrow.PickupStatus.a(nbttagcompound.getByte("pickup"));
        this.setCritical(nbttagcompound.getBoolean("crit"));
        this.setPierceLevel(nbttagcompound.getByte("PierceLevel"));
        if (nbttagcompound.hasKeyOfType("SoundEvent", 8)) {
            this.soundEvent = (SoundEffect) IRegistry.SOUND_EVENT.getOptional(new MinecraftKey(nbttagcompound.getString("SoundEvent"))).orElse(this.i());
        }

        this.setShotFromCrossbow(nbttagcompound.getBoolean("ShotFromCrossbow"));
    }

    @Override
    public void setShooter(@Nullable Entity entity) {
        super.setShooter(entity);
        if (entity instanceof EntityHuman) {
            this.pickup = ((EntityHuman) entity).getAbilities().instabuild ? EntityArrow.PickupStatus.CREATIVE_ONLY : EntityArrow.PickupStatus.ALLOWED;
        }

    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (!this.level.isClientSide && (this.inGround || this.t()) && this.shakeTime <= 0) {
            if (this.a(entityhuman)) {
                entityhuman.receive(this, 1);
                this.die();
            }

        }
    }

    protected boolean a(EntityHuman entityhuman) {
        switch (this.pickup) {
            case ALLOWED:
                return entityhuman.getInventory().pickup(this.getItemStack());
            case CREATIVE_ONLY:
                return entityhuman.getAbilities().instabuild;
            default:
                return false;
        }
    }

    protected abstract ItemStack getItemStack();

    @Override
    protected Entity.MovementEmission aI() {
        return Entity.MovementEmission.NONE;
    }

    public void setDamage(double d0) {
        this.baseDamage = d0;
    }

    public double getDamage() {
        return this.baseDamage;
    }

    public void setKnockbackStrength(int i) {
        this.knockback = i;
    }

    public int o() {
        return this.knockback;
    }

    @Override
    public boolean ca() {
        return false;
    }

    @Override
    protected float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.13F;
    }

    public void setCritical(boolean flag) {
        this.a(1, flag);
    }

    public void setPierceLevel(byte b0) {
        this.entityData.set(EntityArrow.PIERCE_LEVEL, b0);
    }

    private void a(int i, boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityArrow.ID_FLAGS);

        if (flag) {
            this.entityData.set(EntityArrow.ID_FLAGS, (byte) (b0 | i));
        } else {
            this.entityData.set(EntityArrow.ID_FLAGS, (byte) (b0 & ~i));
        }

    }

    public boolean isCritical() {
        byte b0 = (Byte) this.entityData.get(EntityArrow.ID_FLAGS);

        return (b0 & 1) != 0;
    }

    public boolean isShotFromCrossbow() {
        byte b0 = (Byte) this.entityData.get(EntityArrow.ID_FLAGS);

        return (b0 & 4) != 0;
    }

    public byte getPierceLevel() {
        return (Byte) this.entityData.get(EntityArrow.PIERCE_LEVEL);
    }

    public void a(EntityLiving entityliving, float f) {
        int i = EnchantmentManager.a(Enchantments.POWER_ARROWS, entityliving);
        int j = EnchantmentManager.a(Enchantments.PUNCH_ARROWS, entityliving);

        this.setDamage((double) (f * 2.0F) + this.random.nextGaussian() * 0.25D + (double) ((float) this.level.getDifficulty().a() * 0.11F));
        if (i > 0) {
            this.setDamage(this.getDamage() + (double) i * 0.5D + 0.5D);
        }

        if (j > 0) {
            this.setKnockbackStrength(j);
        }

        if (EnchantmentManager.a(Enchantments.FLAMING_ARROWS, entityliving) > 0) {
            this.setOnFire(100);
        }

    }

    protected float s() {
        return 0.6F;
    }

    public void p(boolean flag) {
        this.noPhysics = flag;
        this.a(2, flag);
    }

    public boolean t() {
        return !this.level.isClientSide ? this.noPhysics : ((Byte) this.entityData.get(EntityArrow.ID_FLAGS) & 2) != 0;
    }

    public void setShotFromCrossbow(boolean flag) {
        this.a(4, flag);
    }

    public static enum PickupStatus {

        DISALLOWED, ALLOWED, CREATIVE_ONLY;

        private PickupStatus() {}

        public static EntityArrow.PickupStatus a(int i) {
            if (i < 0 || i > values().length) {
                i = 0;
            }

            return values()[i];
        }
    }
}
