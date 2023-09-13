package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.util.TimeRange;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSourceIndirect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IEntityAngerable;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalUniversalAngerReset;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class EntityEnderman extends EntityMonster implements IEntityAngerable {

    private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(EntityEnderman.SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.15000000596046448D, AttributeModifier.Operation.ADDITION);
    private static final int DELAY_BETWEEN_CREEPY_STARE_SOUND = 400;
    private static final int MIN_DEAGGRESSION_TIME = 600;
    private static final DataWatcherObject<Optional<IBlockData>> DATA_CARRY_STATE = DataWatcher.a(EntityEnderman.class, DataWatcherRegistry.BLOCK_STATE);
    private static final DataWatcherObject<Boolean> DATA_CREEPY = DataWatcher.a(EntityEnderman.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_STARED_AT = DataWatcher.a(EntityEnderman.class, DataWatcherRegistry.BOOLEAN);
    private int lastStareSound = Integer.MIN_VALUE;
    private int targetChangeTime;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeRange.a(20, 39);
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;

    public EntityEnderman(EntityTypes<? extends EntityEnderman> entitytypes, World world) {
        super(entitytypes, world);
        this.maxUpStep = 1.0F;
        this.a(PathType.WATER, -1.0F);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityEnderman.a(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(10, new EntityEnderman.PathfinderGoalEndermanPlaceBlock(this));
        this.goalSelector.a(11, new EntityEnderman.PathfinderGoalEndermanPickupBlock(this));
        this.targetSelector.a(1, new EntityEnderman.PathfinderGoalPlayerWhoLookedAtTarget(this, this::a_));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityEndermite.class, true, false));
        this.targetSelector.a(4, new PathfinderGoalUniversalAngerReset<>(this, false));
    }

    public static AttributeProvider.Builder n() {
        return EntityMonster.fB().a(GenericAttributes.MAX_HEALTH, 40.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.ATTACK_DAMAGE, 7.0D).a(GenericAttributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        super.setGoalTarget(entityliving);
        AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (entityliving == null) {
            this.targetChangeTime = 0;
            this.entityData.set(EntityEnderman.DATA_CREEPY, false);
            this.entityData.set(EntityEnderman.DATA_STARED_AT, false);
            attributemodifiable.removeModifier(EntityEnderman.SPEED_MODIFIER_ATTACKING);
        } else {
            this.targetChangeTime = this.tickCount;
            this.entityData.set(EntityEnderman.DATA_CREEPY, true);
            if (!attributemodifiable.a(EntityEnderman.SPEED_MODIFIER_ATTACKING)) {
                attributemodifiable.b(EntityEnderman.SPEED_MODIFIER_ATTACKING);
            }
        }

    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityEnderman.DATA_CARRY_STATE, Optional.empty());
        this.entityData.register(EntityEnderman.DATA_CREEPY, false);
        this.entityData.register(EntityEnderman.DATA_STARED_AT, false);
    }

    @Override
    public void anger() {
        this.setAnger(EntityEnderman.PERSISTENT_ANGER_TIME.a(this.random));
    }

    @Override
    public void setAnger(int i) {
        this.remainingPersistentAngerTime = i;
    }

    @Override
    public int getAnger() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    @Override
    public UUID getAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void p() {
        if (this.tickCount >= this.lastStareSound + 400) {
            this.lastStareSound = this.tickCount;
            if (!this.isSilent()) {
                this.level.a(this.locX(), this.getHeadY(), this.locZ(), SoundEffects.ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
            }
        }

    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityEnderman.DATA_CREEPY.equals(datawatcherobject) && this.fy() && this.level.isClientSide) {
            this.p();
        }

        super.a(datawatcherobject);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        IBlockData iblockdata = this.getCarried();

        if (iblockdata != null) {
            nbttagcompound.set("carriedBlockState", GameProfileSerializer.a(iblockdata));
        }

        this.c(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        IBlockData iblockdata = null;

        if (nbttagcompound.hasKeyOfType("carriedBlockState", 10)) {
            iblockdata = GameProfileSerializer.c(nbttagcompound.getCompound("carriedBlockState"));
            if (iblockdata.isAir()) {
                iblockdata = null;
            }
        }

        this.setCarried(iblockdata);
        this.a(this.level, nbttagcompound);
    }

    boolean g(EntityHuman entityhuman) {
        ItemStack itemstack = (ItemStack) entityhuman.getInventory().armor.get(3);

        if (itemstack.a(Blocks.CARVED_PUMPKIN.getItem())) {
            return false;
        } else {
            Vec3D vec3d = entityhuman.e(1.0F).d();
            Vec3D vec3d1 = new Vec3D(this.locX() - entityhuman.locX(), this.getHeadY() - entityhuman.getHeadY(), this.locZ() - entityhuman.locZ());
            double d0 = vec3d1.f();

            vec3d1 = vec3d1.d();
            double d1 = vec3d.b(vec3d1);

            return d1 > 1.0D - 0.025D / d0 ? entityhuman.hasLineOfSight(this) : false;
        }
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 2.55F;
    }

    @Override
    public void movementTick() {
        if (this.level.isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.level.addParticle(Particles.PORTAL, this.d(0.5D), this.da() - 0.25D, this.g(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }

        this.jumping = false;
        if (!this.level.isClientSide) {
            this.a((WorldServer) this.level, true);
        }

        super.movementTick();
    }

    @Override
    public boolean ex() {
        return true;
    }

    @Override
    protected void mobTick() {
        if (this.level.isDay() && this.tickCount >= this.targetChangeTime + 600) {
            float f = this.aY();

            if (f > 0.5F && this.level.g(this.getChunkCoordinates()) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.setGoalTarget((EntityLiving) null);
                this.t();
            }
        }

        super.mobTick();
    }

    protected boolean t() {
        if (!this.level.isClientSide() && this.isAlive()) {
            double d0 = this.locX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double d1 = this.locY() + (double) (this.random.nextInt(64) - 32);
            double d2 = this.locZ() + (this.random.nextDouble() - 0.5D) * 64.0D;

            return this.q(d0, d1, d2);
        } else {
            return false;
        }
    }

    boolean a(Entity entity) {
        Vec3D vec3d = new Vec3D(this.locX() - entity.locX(), this.e(0.5D) - entity.getHeadY(), this.locZ() - entity.locZ());

        vec3d = vec3d.d();
        double d0 = 16.0D;
        double d1 = this.locX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
        double d2 = this.locY() + (double) (this.random.nextInt(16) - 8) - vec3d.y * 16.0D;
        double d3 = this.locZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;

        return this.q(d1, d2, d3);
    }

    private boolean q(double d0, double d1, double d2) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(d0, d1, d2);

        while (blockposition_mutableblockposition.getY() > this.level.getMinBuildHeight() && !this.level.getType(blockposition_mutableblockposition).getMaterial().isSolid()) {
            blockposition_mutableblockposition.c(EnumDirection.DOWN);
        }

        IBlockData iblockdata = this.level.getType(blockposition_mutableblockposition);
        boolean flag = iblockdata.getMaterial().isSolid();
        boolean flag1 = iblockdata.getFluid().a((Tag) TagsFluid.WATER);

        if (flag && !flag1) {
            boolean flag2 = this.a(d0, d1, d2, true);

            if (flag2 && !this.isSilent()) {
                this.level.playSound((EntityHuman) null, this.xo, this.yo, this.zo, SoundEffects.ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEffects.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag2;
        } else {
            return false;
        }
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.fx() ? SoundEffects.ENDERMAN_SCREAM : SoundEffects.ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENDERMAN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENDERMAN_DEATH;
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        IBlockData iblockdata = this.getCarried();

        if (iblockdata != null) {
            this.a((IMaterial) iblockdata.getBlock());
        }

    }

    public void setCarried(@Nullable IBlockData iblockdata) {
        this.entityData.set(EntityEnderman.DATA_CARRY_STATE, Optional.ofNullable(iblockdata));
    }

    @Nullable
    public IBlockData getCarried() {
        return (IBlockData) ((Optional) this.entityData.get(EntityEnderman.DATA_CARRY_STATE)).orElse((Object) null);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource instanceof EntityDamageSourceIndirect) {
            for (int i = 0; i < 64; ++i) {
                if (this.t()) {
                    return true;
                }
            }

            return false;
        } else {
            boolean flag = super.damageEntity(damagesource, f);

            if (!this.level.isClientSide() && !(damagesource.getEntity() instanceof EntityLiving) && this.random.nextInt(10) != 0) {
                this.t();
            }

            return flag;
        }
    }

    public boolean fx() {
        return (Boolean) this.entityData.get(EntityEnderman.DATA_CREEPY);
    }

    public boolean fy() {
        return (Boolean) this.entityData.get(EntityEnderman.DATA_STARED_AT);
    }

    public void fz() {
        this.entityData.set(EntityEnderman.DATA_STARED_AT, true);
    }

    @Override
    public boolean isSpecialPersistence() {
        return super.isSpecialPersistence() || this.getCarried() != null;
    }

    private static class a extends PathfinderGoal {

        private final EntityEnderman enderman;
        private EntityLiving target;

        public a(EntityEnderman entityenderman) {
            this.enderman = entityenderman;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            this.target = this.enderman.getGoalTarget();
            if (!(this.target instanceof EntityHuman)) {
                return false;
            } else {
                double d0 = this.target.f((Entity) this.enderman);

                return d0 > 256.0D ? false : this.enderman.g((EntityHuman) this.target);
            }
        }

        @Override
        public void c() {
            this.enderman.getNavigation().o();
        }

        @Override
        public void e() {
            this.enderman.getControllerLook().a(this.target.locX(), this.target.getHeadY(), this.target.locZ());
        }
    }

    private static class PathfinderGoalEndermanPlaceBlock extends PathfinderGoal {

        private final EntityEnderman enderman;

        public PathfinderGoalEndermanPlaceBlock(EntityEnderman entityenderman) {
            this.enderman = entityenderman;
        }

        @Override
        public boolean a() {
            return this.enderman.getCarried() == null ? false : (!this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? false : this.enderman.getRandom().nextInt(2000) == 0);
        }

        @Override
        public void e() {
            Random random = this.enderman.getRandom();
            World world = this.enderman.level;
            int i = MathHelper.floor(this.enderman.locX() - 1.0D + random.nextDouble() * 2.0D);
            int j = MathHelper.floor(this.enderman.locY() + random.nextDouble() * 2.0D);
            int k = MathHelper.floor(this.enderman.locZ() - 1.0D + random.nextDouble() * 2.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = world.getType(blockposition);
            BlockPosition blockposition1 = blockposition.down();
            IBlockData iblockdata1 = world.getType(blockposition1);
            IBlockData iblockdata2 = this.enderman.getCarried();

            if (iblockdata2 != null) {
                iblockdata2 = Block.b(iblockdata2, (GeneratorAccess) this.enderman.level, blockposition);
                if (this.a(world, blockposition, iblockdata2, iblockdata, iblockdata1, blockposition1)) {
                    world.setTypeAndData(blockposition, iblockdata2, 3);
                    world.a((Entity) this.enderman, GameEvent.BLOCK_PLACE, blockposition);
                    this.enderman.setCarried((IBlockData) null);
                }

            }
        }

        private boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, BlockPosition blockposition1) {
            return iblockdata1.isAir() && !iblockdata2.isAir() && !iblockdata2.a(Blocks.BEDROCK) && iblockdata2.r(world, blockposition1) && iblockdata.canPlace(world, blockposition) && world.getEntities(this.enderman, AxisAlignedBB.a(Vec3D.b((BaseBlockPosition) blockposition))).isEmpty();
        }
    }

    private static class PathfinderGoalEndermanPickupBlock extends PathfinderGoal {

        private final EntityEnderman enderman;

        public PathfinderGoalEndermanPickupBlock(EntityEnderman entityenderman) {
            this.enderman = entityenderman;
        }

        @Override
        public boolean a() {
            return this.enderman.getCarried() != null ? false : (!this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? false : this.enderman.getRandom().nextInt(20) == 0);
        }

        @Override
        public void e() {
            Random random = this.enderman.getRandom();
            World world = this.enderman.level;
            int i = MathHelper.floor(this.enderman.locX() - 2.0D + random.nextDouble() * 4.0D);
            int j = MathHelper.floor(this.enderman.locY() + random.nextDouble() * 3.0D);
            int k = MathHelper.floor(this.enderman.locZ() - 2.0D + random.nextDouble() * 4.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = world.getType(blockposition);
            Vec3D vec3d = new Vec3D((double) this.enderman.cW() + 0.5D, (double) j + 0.5D, (double) this.enderman.dc() + 0.5D);
            Vec3D vec3d1 = new Vec3D((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D);
            MovingObjectPositionBlock movingobjectpositionblock = world.rayTrace(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.OUTLINE, RayTrace.FluidCollisionOption.NONE, this.enderman));
            boolean flag = movingobjectpositionblock.getBlockPosition().equals(blockposition);

            if (iblockdata.a((Tag) TagsBlock.ENDERMAN_HOLDABLE) && flag) {
                world.a(blockposition, false);
                world.a((Entity) this.enderman, GameEvent.BLOCK_DESTROY, blockposition);
                this.enderman.setCarried(iblockdata.getBlock().getBlockData());
            }

        }
    }

    private static class PathfinderGoalPlayerWhoLookedAtTarget extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        private final EntityEnderman enderman;
        private EntityHuman pendingTarget;
        private int aggroTime;
        private int teleportTime;
        private final PathfinderTargetCondition startAggroTargetConditions;
        private final PathfinderTargetCondition continueAggroTargetConditions = PathfinderTargetCondition.a().d();

        public PathfinderGoalPlayerWhoLookedAtTarget(EntityEnderman entityenderman, @Nullable Predicate<EntityLiving> predicate) {
            super(entityenderman, EntityHuman.class, 10, false, false, predicate);
            this.enderman = entityenderman;
            this.startAggroTargetConditions = PathfinderTargetCondition.a().a(this.k()).a((entityliving) -> {
                return entityenderman.g((EntityHuman) entityliving);
            });
        }

        @Override
        public boolean a() {
            this.pendingTarget = this.enderman.level.a(this.startAggroTargetConditions, (EntityLiving) this.enderman);
            return this.pendingTarget != null;
        }

        @Override
        public void c() {
            this.aggroTime = 5;
            this.teleportTime = 0;
            this.enderman.fz();
        }

        @Override
        public void d() {
            this.pendingTarget = null;
            super.d();
        }

        @Override
        public boolean b() {
            if (this.pendingTarget != null) {
                if (!this.enderman.g(this.pendingTarget)) {
                    return false;
                } else {
                    this.enderman.a((Entity) this.pendingTarget, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return this.target != null && this.continueAggroTargetConditions.a(this.enderman, this.target) ? true : super.b();
            }
        }

        @Override
        public void e() {
            if (this.enderman.getGoalTarget() == null) {
                super.a((EntityLiving) null);
            }

            if (this.pendingTarget != null) {
                if (--this.aggroTime <= 0) {
                    this.target = this.pendingTarget;
                    this.pendingTarget = null;
                    super.c();
                }
            } else {
                if (this.target != null && !this.enderman.isPassenger()) {
                    if (this.enderman.g((EntityHuman) this.target)) {
                        if (this.target.f((Entity) this.enderman) < 16.0D) {
                            this.enderman.t();
                        }

                        this.teleportTime = 0;
                    } else if (this.target.f((Entity) this.enderman) > 256.0D && this.teleportTime++ >= 30 && this.enderman.a((Entity) this.target)) {
                        this.teleportTime = 0;
                    }
                }

                super.e();
            }

        }
    }
}
