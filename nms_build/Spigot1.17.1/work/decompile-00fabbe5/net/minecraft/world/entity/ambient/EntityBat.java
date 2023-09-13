package net.minecraft.world.entity.ambient;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class EntityBat extends EntityAmbient {

    public static final float FLAP_DEGREES_PER_TICK = 74.48451F;
    public static final int TICKS_PER_FLAP = MathHelper.f(2.4166098F);
    private static final DataWatcherObject<Byte> DATA_ID_FLAGS = DataWatcher.a(EntityBat.class, DataWatcherRegistry.BYTE);
    private static final int FLAG_RESTING = 1;
    private static final PathfinderTargetCondition BAT_RESTING_TARGETING = PathfinderTargetCondition.b().a(4.0D);
    private BlockPosition targetPosition;

    public EntityBat(EntityTypes<? extends EntityBat> entitytypes, World world) {
        super(entitytypes, world);
        this.setAsleep(true);
    }

    @Override
    public boolean aF() {
        return !this.isAsleep() && this.tickCount % EntityBat.TICKS_PER_FLAP == 0;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityBat.DATA_ID_FLAGS, (byte) 0);
    }

    @Override
    protected float getSoundVolume() {
        return 0.1F;
    }

    @Override
    public float ep() {
        return super.ep() * 0.95F;
    }

    @Nullable
    @Override
    public SoundEffect getSoundAmbient() {
        return this.isAsleep() && this.random.nextInt(4) != 0 ? null : SoundEffects.BAT_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.BAT_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.BAT_DEATH;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    protected void A(Entity entity) {}

    @Override
    protected void collideNearby() {}

    public static AttributeProvider.Builder n() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 6.0D);
    }

    public boolean isAsleep() {
        return ((Byte) this.entityData.get(EntityBat.DATA_ID_FLAGS) & 1) != 0;
    }

    public void setAsleep(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityBat.DATA_ID_FLAGS);

        if (flag) {
            this.entityData.set(EntityBat.DATA_ID_FLAGS, (byte) (b0 | 1));
        } else {
            this.entityData.set(EntityBat.DATA_ID_FLAGS, (byte) (b0 & -2));
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAsleep()) {
            this.setMot(Vec3D.ZERO);
            this.setPositionRaw(this.locX(), (double) MathHelper.floor(this.locY()) + 1.0D - (double) this.getHeight(), this.locZ());
        } else {
            this.setMot(this.getMot().d(1.0D, 0.6D, 1.0D));
        }

    }

    @Override
    protected void mobTick() {
        super.mobTick();
        BlockPosition blockposition = this.getChunkCoordinates();
        BlockPosition blockposition1 = blockposition.up();

        if (this.isAsleep()) {
            boolean flag = this.isSilent();

            if (this.level.getType(blockposition1).isOccluding(this.level, blockposition)) {
                if (this.random.nextInt(200) == 0) {
                    this.yHeadRot = (float) this.random.nextInt(360);
                }

                if (this.level.a(EntityBat.BAT_RESTING_TARGETING, (EntityLiving) this) != null) {
                    this.setAsleep(false);
                    if (!flag) {
                        this.level.a((EntityHuman) null, 1025, blockposition, 0);
                    }
                }
            } else {
                this.setAsleep(false);
                if (!flag) {
                    this.level.a((EntityHuman) null, 1025, blockposition, 0);
                }
            }
        } else {
            if (this.targetPosition != null && (!this.level.isEmpty(this.targetPosition) || this.targetPosition.getY() <= this.level.getMinBuildHeight())) {
                this.targetPosition = null;
            }

            if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.a((IPosition) this.getPositionVector(), 2.0D)) {
                this.targetPosition = new BlockPosition(this.locX() + (double) this.random.nextInt(7) - (double) this.random.nextInt(7), this.locY() + (double) this.random.nextInt(6) - 2.0D, this.locZ() + (double) this.random.nextInt(7) - (double) this.random.nextInt(7));
            }

            double d0 = (double) this.targetPosition.getX() + 0.5D - this.locX();
            double d1 = (double) this.targetPosition.getY() + 0.1D - this.locY();
            double d2 = (double) this.targetPosition.getZ() + 0.5D - this.locZ();
            Vec3D vec3d = this.getMot();
            Vec3D vec3d1 = vec3d.add((Math.signum(d0) * 0.5D - vec3d.x) * 0.10000000149011612D, (Math.signum(d1) * 0.699999988079071D - vec3d.y) * 0.10000000149011612D, (Math.signum(d2) * 0.5D - vec3d.z) * 0.10000000149011612D);

            this.setMot(vec3d1);
            float f = (float) (MathHelper.d(vec3d1.z, vec3d1.x) * 57.2957763671875D) - 90.0F;
            float f1 = MathHelper.g(f - this.getYRot());

            this.zza = 0.5F;
            this.setYRot(this.getYRot() + f1);
            if (this.random.nextInt(100) == 0 && this.level.getType(blockposition1).isOccluding(this.level, blockposition1)) {
                this.setAsleep(true);
            }
        }

    }

    @Override
    protected Entity.MovementEmission aI() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    @Override
    public boolean isIgnoreBlockTrigger() {
        return true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if (!this.level.isClientSide && this.isAsleep()) {
                this.setAsleep(false);
            }

            return super.damageEntity(damagesource, f);
        }
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.entityData.set(EntityBat.DATA_ID_FLAGS, nbttagcompound.getByte("BatFlags"));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setByte("BatFlags", (Byte) this.entityData.get(EntityBat.DATA_ID_FLAGS));
    }

    public static boolean b(EntityTypes<EntityBat> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        if (blockposition.getY() >= generatoraccess.getSeaLevel()) {
            return false;
        } else {
            int i = generatoraccess.getLightLevel(blockposition);
            byte b0 = 4;

            if (t()) {
                b0 = 7;
            } else if (random.nextBoolean()) {
                return false;
            }

            return i > random.nextInt(b0) ? false : a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
        }
    }

    private static boolean t() {
        LocalDate localdate = LocalDate.now();
        int i = localdate.get(ChronoField.DAY_OF_MONTH);
        int j = localdate.get(ChronoField.MONTH_OF_YEAR);

        return j == 10 && i >= 20 || j == 11 && i <= 3;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height / 2.0F;
    }
}
