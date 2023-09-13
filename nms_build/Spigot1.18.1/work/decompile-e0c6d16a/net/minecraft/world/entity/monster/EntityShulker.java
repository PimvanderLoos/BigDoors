package net.minecraft.world.entity.monster;

import com.mojang.math.Vector3fa;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.control.EntityAIBodyControl;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityGolem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityShulkerBullet;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityShulker extends EntityGolem implements IMonster {

    private static final UUID COVERED_ARMOR_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
    private static final AttributeModifier COVERED_ARMOR_MODIFIER = new AttributeModifier(EntityShulker.COVERED_ARMOR_MODIFIER_UUID, "Covered armor bonus", 20.0D, AttributeModifier.Operation.ADDITION);
    protected static final DataWatcherObject<EnumDirection> DATA_ATTACH_FACE_ID = DataWatcher.defineId(EntityShulker.class, DataWatcherRegistry.DIRECTION);
    protected static final DataWatcherObject<Byte> DATA_PEEK_ID = DataWatcher.defineId(EntityShulker.class, DataWatcherRegistry.BYTE);
    public static final DataWatcherObject<Byte> DATA_COLOR_ID = DataWatcher.defineId(EntityShulker.class, DataWatcherRegistry.BYTE);
    private static final int TELEPORT_STEPS = 6;
    private static final byte NO_COLOR = 16;
    private static final byte DEFAULT_COLOR = 16;
    private static final int MAX_TELEPORT_DISTANCE = 8;
    private static final int OTHER_SHULKER_SCAN_RADIUS = 8;
    private static final int OTHER_SHULKER_LIMIT = 5;
    private static final float PEEK_PER_TICK = 0.05F;
    static final Vector3fa FORWARD = (Vector3fa) SystemUtils.make(() -> {
        BaseBlockPosition baseblockposition = EnumDirection.SOUTH.getNormal();

        return new Vector3fa((float) baseblockposition.getX(), (float) baseblockposition.getY(), (float) baseblockposition.getZ());
    });
    private float currentPeekAmountO;
    private float currentPeekAmount;
    @Nullable
    private BlockPosition clientOldAttachPosition;
    private int clientSideTeleportInterpolation;
    private static final float MAX_LID_OPEN = 1.0F;

    public EntityShulker(EntityTypes<? extends EntityShulker> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 5;
        this.lookControl = new EntityShulker.d(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F, 0.02F, true));
        this.goalSelector.addGoal(4, new EntityShulker.a());
        this.goalSelector.addGoal(7, new EntityShulker.f());
        this.goalSelector.addGoal(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.addGoal(1, (new PathfinderGoalHurtByTarget(this, new Class[]{this.getClass()})).setAlertOthers());
        this.targetSelector.addGoal(2, new EntityShulker.e(this));
        this.targetSelector.addGoal(3, new EntityShulker.c(this));
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.SHULKER_AMBIENT;
    }

    @Override
    public void playAmbientSound() {
        if (!this.isClosed()) {
            super.playAmbientSound();
        }

    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.SHULKER_DEATH;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return this.isClosed() ? SoundEffects.SHULKER_HURT_CLOSED : SoundEffects.SHULKER_HURT;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityShulker.DATA_ATTACH_FACE_ID, EnumDirection.DOWN);
        this.entityData.define(EntityShulker.DATA_PEEK_ID, (byte) 0);
        this.entityData.define(EntityShulker.DATA_COLOR_ID, (byte) 16);
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 30.0D);
    }

    @Override
    protected EntityAIBodyControl createBodyControl() {
        return new EntityShulker.b(this);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setAttachFace(EnumDirection.from3DDataValue(nbttagcompound.getByte("AttachFace")));
        this.entityData.set(EntityShulker.DATA_PEEK_ID, nbttagcompound.getByte("Peek"));
        if (nbttagcompound.contains("Color", 99)) {
            this.entityData.set(EntityShulker.DATA_COLOR_ID, nbttagcompound.getByte("Color"));
        }

    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putByte("AttachFace", (byte) this.getAttachFace().get3DDataValue());
        nbttagcompound.putByte("Peek", (Byte) this.entityData.get(EntityShulker.DATA_PEEK_ID));
        nbttagcompound.putByte("Color", (Byte) this.entityData.get(EntityShulker.DATA_COLOR_ID));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && !this.isPassenger() && !this.canStayAt(this.blockPosition(), this.getAttachFace())) {
            this.findNewAttachment();
        }

        if (this.updatePeekAmount()) {
            this.onPeekAmountChange();
        }

        if (this.level.isClientSide) {
            if (this.clientSideTeleportInterpolation > 0) {
                --this.clientSideTeleportInterpolation;
            } else {
                this.clientOldAttachPosition = null;
            }
        }

    }

    private void findNewAttachment() {
        EnumDirection enumdirection = this.findAttachableSurface(this.blockPosition());

        if (enumdirection != null) {
            this.setAttachFace(enumdirection);
        } else {
            this.teleportSomewhere();
        }

    }

    @Override
    protected AxisAlignedBB makeBoundingBox() {
        float f = getPhysicalPeek(this.currentPeekAmount);
        EnumDirection enumdirection = this.getAttachFace().getOpposite();
        float f1 = this.getType().getWidth() / 2.0F;

        return getProgressAabb(enumdirection, f).move(this.getX() - (double) f1, this.getY(), this.getZ() - (double) f1);
    }

    private static float getPhysicalPeek(float f) {
        return 0.5F - MathHelper.sin((0.5F + f) * 3.1415927F) * 0.5F;
    }

    private boolean updatePeekAmount() {
        this.currentPeekAmountO = this.currentPeekAmount;
        float f = (float) this.getRawPeekAmount() * 0.01F;

        if (this.currentPeekAmount == f) {
            return false;
        } else {
            if (this.currentPeekAmount > f) {
                this.currentPeekAmount = MathHelper.clamp(this.currentPeekAmount - 0.05F, f, 1.0F);
            } else {
                this.currentPeekAmount = MathHelper.clamp(this.currentPeekAmount + 0.05F, 0.0F, f);
            }

            return true;
        }
    }

    private void onPeekAmountChange() {
        this.reapplyPosition();
        float f = getPhysicalPeek(this.currentPeekAmount);
        float f1 = getPhysicalPeek(this.currentPeekAmountO);
        EnumDirection enumdirection = this.getAttachFace().getOpposite();
        float f2 = f - f1;

        if (f2 > 0.0F) {
            List<Entity> list = this.level.getEntities((Entity) this, getProgressDeltaAabb(enumdirection, f1, f).move(this.getX() - 0.5D, this.getY(), this.getZ() - 0.5D), IEntitySelector.NO_SPECTATORS.and((entity) -> {
                return !entity.isPassengerOfSameVehicle(this);
            }));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (!(entity instanceof EntityShulker) && !entity.noPhysics) {
                    entity.move(EnumMoveType.SHULKER, new Vec3D((double) (f2 * (float) enumdirection.getStepX()), (double) (f2 * (float) enumdirection.getStepY()), (double) (f2 * (float) enumdirection.getStepZ())));
                }
            }

        }
    }

    public static AxisAlignedBB getProgressAabb(EnumDirection enumdirection, float f) {
        return getProgressDeltaAabb(enumdirection, -1.0F, f);
    }

    public static AxisAlignedBB getProgressDeltaAabb(EnumDirection enumdirection, float f, float f1) {
        double d0 = (double) Math.max(f, f1);
        double d1 = (double) Math.min(f, f1);

        return (new AxisAlignedBB(BlockPosition.ZERO)).expandTowards((double) enumdirection.getStepX() * d0, (double) enumdirection.getStepY() * d0, (double) enumdirection.getStepZ() * d0).contract((double) (-enumdirection.getStepX()) * (1.0D + d1), (double) (-enumdirection.getStepY()) * (1.0D + d1), (double) (-enumdirection.getStepZ()) * (1.0D + d1));
    }

    @Override
    public double getMyRidingOffset() {
        EntityTypes<?> entitytypes = this.getVehicle().getType();

        return entitytypes != EntityTypes.BOAT && entitytypes != EntityTypes.MINECART ? super.getMyRidingOffset() : 0.1875D - this.getVehicle().getPassengersRidingOffset();
    }

    @Override
    public boolean startRiding(Entity entity, boolean flag) {
        if (this.level.isClientSide()) {
            this.clientOldAttachPosition = null;
            this.clientSideTeleportInterpolation = 0;
        }

        this.setAttachFace(EnumDirection.DOWN);
        return super.startRiding(entity, flag);
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
        if (this.level.isClientSide) {
            this.clientOldAttachPosition = this.blockPosition();
        }

        this.yBodyRotO = 0.0F;
        this.yBodyRot = 0.0F;
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setYRot(0.0F);
        this.yHeadRot = this.getYRot();
        this.setOldPosAndRot();
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        if (enummovetype == EnumMoveType.SHULKER_BOX) {
            this.teleportSomewhere();
        } else {
            super.move(enummovetype, vec3d);
        }

    }

    @Override
    public Vec3D getDeltaMovement() {
        return Vec3D.ZERO;
    }

    @Override
    public void setDeltaMovement(Vec3D vec3d) {}

    @Override
    public void setPos(double d0, double d1, double d2) {
        BlockPosition blockposition = this.blockPosition();

        if (this.isPassenger()) {
            super.setPos(d0, d1, d2);
        } else {
            super.setPos((double) MathHelper.floor(d0) + 0.5D, (double) MathHelper.floor(d1 + 0.5D), (double) MathHelper.floor(d2) + 0.5D);
        }

        if (this.tickCount != 0) {
            BlockPosition blockposition1 = this.blockPosition();

            if (!blockposition1.equals(blockposition)) {
                this.entityData.set(EntityShulker.DATA_PEEK_ID, (byte) 0);
                this.hasImpulse = true;
                if (this.level.isClientSide && !this.isPassenger() && !blockposition1.equals(this.clientOldAttachPosition)) {
                    this.clientOldAttachPosition = blockposition;
                    this.clientSideTeleportInterpolation = 6;
                    this.xOld = this.getX();
                    this.yOld = this.getY();
                    this.zOld = this.getZ();
                }
            }

        }
    }

    @Nullable
    protected EnumDirection findAttachableSurface(BlockPosition blockposition) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.canStayAt(blockposition, enumdirection)) {
                return enumdirection;
            }
        }

        return null;
    }

    boolean canStayAt(BlockPosition blockposition, EnumDirection enumdirection) {
        if (this.isPositionBlocked(blockposition)) {
            return false;
        } else {
            EnumDirection enumdirection1 = enumdirection.getOpposite();

            if (!this.level.loadedAndEntityCanStandOnFace(blockposition.relative(enumdirection), this, enumdirection1)) {
                return false;
            } else {
                AxisAlignedBB axisalignedbb = getProgressAabb(enumdirection1, 1.0F).move(blockposition).deflate(1.0E-6D);

                return this.level.noCollision(this, axisalignedbb);
            }
        }
    }

    private boolean isPositionBlocked(BlockPosition blockposition) {
        IBlockData iblockdata = this.level.getBlockState(blockposition);

        if (iblockdata.isAir()) {
            return false;
        } else {
            boolean flag = iblockdata.is(Blocks.MOVING_PISTON) && blockposition.equals(this.blockPosition());

            return !flag;
        }
    }

    protected boolean teleportSomewhere() {
        if (!this.isNoAi() && this.isAlive()) {
            BlockPosition blockposition = this.blockPosition();

            for (int i = 0; i < 5; ++i) {
                BlockPosition blockposition1 = blockposition.offset(MathHelper.randomBetweenInclusive(this.random, -8, 8), MathHelper.randomBetweenInclusive(this.random, -8, 8), MathHelper.randomBetweenInclusive(this.random, -8, 8));

                if (blockposition1.getY() > this.level.getMinBuildHeight() && this.level.isEmptyBlock(blockposition1) && this.level.getWorldBorder().isWithinBounds(blockposition1) && this.level.noCollision(this, (new AxisAlignedBB(blockposition1)).deflate(1.0E-6D))) {
                    EnumDirection enumdirection = this.findAttachableSurface(blockposition1);

                    if (enumdirection != null) {
                        this.unRide();
                        this.setAttachFace(enumdirection);
                        this.playSound(SoundEffects.SHULKER_TELEPORT, 1.0F, 1.0F);
                        this.setPos((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY(), (double) blockposition1.getZ() + 0.5D);
                        this.entityData.set(EntityShulker.DATA_PEEK_ID, (byte) 0);
                        this.setTarget((EntityLiving) null);
                        return true;
                    }
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public void lerpTo(double d0, double d1, double d2, float f, float f1, int i, boolean flag) {
        this.lerpSteps = 0;
        this.setPos(d0, d1, d2);
        this.setRot(f, f1);
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        Entity entity;

        if (this.isClosed()) {
            entity = damagesource.getDirectEntity();
            if (entity instanceof EntityArrow) {
                return false;
            }
        }

        if (!super.hurt(damagesource, f)) {
            return false;
        } else {
            if ((double) this.getHealth() < (double) this.getMaxHealth() * 0.5D && this.random.nextInt(4) == 0) {
                this.teleportSomewhere();
            } else if (damagesource.isProjectile()) {
                entity = damagesource.getDirectEntity();
                if (entity != null && entity.getType() == EntityTypes.SHULKER_BULLET) {
                    this.hitByShulkerBullet();
                }
            }

            return true;
        }
    }

    private boolean isClosed() {
        return this.getRawPeekAmount() == 0;
    }

    private void hitByShulkerBullet() {
        Vec3D vec3d = this.position();
        AxisAlignedBB axisalignedbb = this.getBoundingBox();

        if (!this.isClosed() && this.teleportSomewhere()) {
            int i = this.level.getEntities((EntityTypeTest) EntityTypes.SHULKER, axisalignedbb.inflate(8.0D), Entity::isAlive).size();
            float f = (float) (i - 1) / 5.0F;

            if (this.level.random.nextFloat() >= f) {
                EntityShulker entityshulker = (EntityShulker) EntityTypes.SHULKER.create(this.level);
                EnumColor enumcolor = this.getColor();

                if (enumcolor != null) {
                    entityshulker.setColor(enumcolor);
                }

                entityshulker.moveTo(vec3d);
                this.level.addFreshEntity(entityshulker);
            }
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    public EnumDirection getAttachFace() {
        return (EnumDirection) this.entityData.get(EntityShulker.DATA_ATTACH_FACE_ID);
    }

    public void setAttachFace(EnumDirection enumdirection) {
        this.entityData.set(EntityShulker.DATA_ATTACH_FACE_ID, enumdirection);
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (EntityShulker.DATA_ATTACH_FACE_ID.equals(datawatcherobject)) {
            this.setBoundingBox(this.makeBoundingBox());
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    public int getRawPeekAmount() {
        return (Byte) this.entityData.get(EntityShulker.DATA_PEEK_ID);
    }

    public void setRawPeekAmount(int i) {
        if (!this.level.isClientSide) {
            this.getAttribute(GenericAttributes.ARMOR).removeModifier(EntityShulker.COVERED_ARMOR_MODIFIER);
            if (i == 0) {
                this.getAttribute(GenericAttributes.ARMOR).addPermanentModifier(EntityShulker.COVERED_ARMOR_MODIFIER);
                this.playSound(SoundEffects.SHULKER_CLOSE, 1.0F, 1.0F);
                this.gameEvent(GameEvent.SHULKER_CLOSE);
            } else {
                this.playSound(SoundEffects.SHULKER_OPEN, 1.0F, 1.0F);
                this.gameEvent(GameEvent.SHULKER_OPEN);
            }
        }

        this.entityData.set(EntityShulker.DATA_PEEK_ID, (byte) i);
    }

    public float getClientPeekAmount(float f) {
        return MathHelper.lerp(f, this.currentPeekAmountO, this.currentPeekAmount);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.5F;
    }

    @Override
    public void recreateFromPacket(PacketPlayOutSpawnEntityLiving packetplayoutspawnentityliving) {
        super.recreateFromPacket(packetplayoutspawnentityliving);
        this.yBodyRot = 0.0F;
        this.yBodyRotO = 0.0F;
    }

    @Override
    public int getMaxHeadXRot() {
        return 180;
    }

    @Override
    public int getMaxHeadYRot() {
        return 180;
    }

    @Override
    public void push(Entity entity) {}

    @Override
    public float getPickRadius() {
        return 0.0F;
    }

    public Optional<Vec3D> getRenderPosition(float f) {
        if (this.clientOldAttachPosition != null && this.clientSideTeleportInterpolation > 0) {
            double d0 = (double) ((float) this.clientSideTeleportInterpolation - f) / 6.0D;

            d0 *= d0;
            BlockPosition blockposition = this.blockPosition();
            double d1 = (double) (blockposition.getX() - this.clientOldAttachPosition.getX()) * d0;
            double d2 = (double) (blockposition.getY() - this.clientOldAttachPosition.getY()) * d0;
            double d3 = (double) (blockposition.getZ() - this.clientOldAttachPosition.getZ()) * d0;

            return Optional.of(new Vec3D(-d1, -d2, -d3));
        } else {
            return Optional.empty();
        }
    }

    private void setColor(EnumColor enumcolor) {
        this.entityData.set(EntityShulker.DATA_COLOR_ID, (byte) enumcolor.getId());
    }

    @Nullable
    public EnumColor getColor() {
        byte b0 = (Byte) this.entityData.get(EntityShulker.DATA_COLOR_ID);

        return b0 != 16 && b0 <= 15 ? EnumColor.byId(b0) : null;
    }

    private class d extends ControllerLook {

        public d(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        protected void clampHeadRotationToBody() {}

        @Override
        protected Optional<Float> getYRotD() {
            EnumDirection enumdirection = EntityShulker.this.getAttachFace().getOpposite();
            Vector3fa vector3fa = EntityShulker.FORWARD.copy();

            vector3fa.transform(enumdirection.getRotation());
            BaseBlockPosition baseblockposition = enumdirection.getNormal();
            Vector3fa vector3fa1 = new Vector3fa((float) baseblockposition.getX(), (float) baseblockposition.getY(), (float) baseblockposition.getZ());

            vector3fa1.cross(vector3fa);
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedY - this.mob.getEyeY();
            double d2 = this.wantedZ - this.mob.getZ();
            Vector3fa vector3fa2 = new Vector3fa((float) d0, (float) d1, (float) d2);
            float f = vector3fa1.dot(vector3fa2);
            float f1 = vector3fa.dot(vector3fa2);

            return Math.abs(f) <= 1.0E-5F && Math.abs(f1) <= 1.0E-5F ? Optional.empty() : Optional.of((float) (MathHelper.atan2((double) (-f), (double) f1) * 57.2957763671875D));
        }

        @Override
        protected Optional<Float> getXRotD() {
            return Optional.of(0.0F);
        }
    }

    private class a extends PathfinderGoal {

        private int attackTime;

        public a() {
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            EntityLiving entityliving = EntityShulker.this.getTarget();

            return entityliving != null && entityliving.isAlive() ? EntityShulker.this.level.getDifficulty() != EnumDifficulty.PEACEFUL : false;
        }

        @Override
        public void start() {
            this.attackTime = 20;
            EntityShulker.this.setRawPeekAmount(100);
        }

        @Override
        public void stop() {
            EntityShulker.this.setRawPeekAmount(0);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (EntityShulker.this.level.getDifficulty() != EnumDifficulty.PEACEFUL) {
                --this.attackTime;
                EntityLiving entityliving = EntityShulker.this.getTarget();

                if (entityliving != null) {
                    EntityShulker.this.getLookControl().setLookAt(entityliving, 180.0F, 180.0F);
                    double d0 = EntityShulker.this.distanceToSqr((Entity) entityliving);

                    if (d0 < 400.0D) {
                        if (this.attackTime <= 0) {
                            this.attackTime = 20 + EntityShulker.this.random.nextInt(10) * 20 / 2;
                            EntityShulker.this.level.addFreshEntity(new EntityShulkerBullet(EntityShulker.this.level, EntityShulker.this, entityliving, EntityShulker.this.getAttachFace().getAxis()));
                            EntityShulker.this.playSound(SoundEffects.SHULKER_SHOOT, 2.0F, (EntityShulker.this.random.nextFloat() - EntityShulker.this.random.nextFloat()) * 0.2F + 1.0F);
                        }
                    } else {
                        EntityShulker.this.setTarget((EntityLiving) null);
                    }

                    super.tick();
                }
            }
        }
    }

    private class f extends PathfinderGoal {

        private int peekTime;

        f() {}

        @Override
        public boolean canUse() {
            return EntityShulker.this.getTarget() == null && EntityShulker.this.random.nextInt(reducedTickDelay(40)) == 0 && EntityShulker.this.canStayAt(EntityShulker.this.blockPosition(), EntityShulker.this.getAttachFace());
        }

        @Override
        public boolean canContinueToUse() {
            return EntityShulker.this.getTarget() == null && this.peekTime > 0;
        }

        @Override
        public void start() {
            this.peekTime = this.adjustedTickDelay(20 * (1 + EntityShulker.this.random.nextInt(3)));
            EntityShulker.this.setRawPeekAmount(30);
        }

        @Override
        public void stop() {
            if (EntityShulker.this.getTarget() == null) {
                EntityShulker.this.setRawPeekAmount(0);
            }

        }

        @Override
        public void tick() {
            --this.peekTime;
        }
    }

    private class e extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        public e(EntityShulker entityshulker) {
            super(entityshulker, EntityHuman.class, true);
        }

        @Override
        public boolean canUse() {
            return EntityShulker.this.level.getDifficulty() == EnumDifficulty.PEACEFUL ? false : super.canUse();
        }

        @Override
        protected AxisAlignedBB getTargetSearchArea(double d0) {
            EnumDirection enumdirection = ((EntityShulker) this.mob).getAttachFace();

            return enumdirection.getAxis() == EnumDirection.EnumAxis.X ? this.mob.getBoundingBox().inflate(4.0D, d0, d0) : (enumdirection.getAxis() == EnumDirection.EnumAxis.Z ? this.mob.getBoundingBox().inflate(d0, d0, 4.0D) : this.mob.getBoundingBox().inflate(d0, 4.0D, d0));
        }
    }

    private static class c extends PathfinderGoalNearestAttackableTarget<EntityLiving> {

        public c(EntityShulker entityshulker) {
            super(entityshulker, EntityLiving.class, 10, true, false, (entityliving) -> {
                return entityliving instanceof IMonster;
            });
        }

        @Override
        public boolean canUse() {
            return this.mob.getTeam() == null ? false : super.canUse();
        }

        @Override
        protected AxisAlignedBB getTargetSearchArea(double d0) {
            EnumDirection enumdirection = ((EntityShulker) this.mob).getAttachFace();

            return enumdirection.getAxis() == EnumDirection.EnumAxis.X ? this.mob.getBoundingBox().inflate(4.0D, d0, d0) : (enumdirection.getAxis() == EnumDirection.EnumAxis.Z ? this.mob.getBoundingBox().inflate(d0, d0, 4.0D) : this.mob.getBoundingBox().inflate(d0, 4.0D, d0));
        }
    }

    private static class b extends EntityAIBodyControl {

        public b(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        public void clientTick() {}
    }
}
