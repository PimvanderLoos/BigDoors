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
    protected static final DataWatcherObject<EnumDirection> DATA_ATTACH_FACE_ID = DataWatcher.a(EntityShulker.class, DataWatcherRegistry.DIRECTION);
    protected static final DataWatcherObject<Byte> DATA_PEEK_ID = DataWatcher.a(EntityShulker.class, DataWatcherRegistry.BYTE);
    public static final DataWatcherObject<Byte> DATA_COLOR_ID = DataWatcher.a(EntityShulker.class, DataWatcherRegistry.BYTE);
    private static final int TELEPORT_STEPS = 6;
    private static final byte NO_COLOR = 16;
    private static final byte DEFAULT_COLOR = 16;
    private static final int MAX_TELEPORT_DISTANCE = 8;
    private static final int OTHER_SHULKER_SCAN_RADIUS = 8;
    private static final int OTHER_SHULKER_LIMIT = 5;
    private static final float PEEK_PER_TICK = 0.05F;
    static final Vector3fa FORWARD = (Vector3fa) SystemUtils.a(() -> {
        BaseBlockPosition baseblockposition = EnumDirection.SOUTH.p();

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
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F, 0.02F, true));
        this.goalSelector.a(4, new EntityShulker.a());
        this.goalSelector.a(7, new EntityShulker.f());
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{this.getClass()})).a());
        this.targetSelector.a(2, new EntityShulker.e(this));
        this.targetSelector.a(3, new EntityShulker.c(this));
    }

    @Override
    protected Entity.MovementEmission aI() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.SHULKER_AMBIENT;
    }

    @Override
    public void K() {
        if (!this.fA()) {
            super.K();
        }

    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.SHULKER_DEATH;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.fA() ? SoundEffects.SHULKER_HURT_CLOSED : SoundEffects.SHULKER_HURT;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityShulker.DATA_ATTACH_FACE_ID, EnumDirection.DOWN);
        this.entityData.register(EntityShulker.DATA_PEEK_ID, (byte) 0);
        this.entityData.register(EntityShulker.DATA_COLOR_ID, (byte) 16);
    }

    public static AttributeProvider.Builder n() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 30.0D);
    }

    @Override
    protected EntityAIBodyControl z() {
        return new EntityShulker.b(this);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setAttachFace(EnumDirection.fromType1(nbttagcompound.getByte("AttachFace")));
        this.entityData.set(EntityShulker.DATA_PEEK_ID, nbttagcompound.getByte("Peek"));
        if (nbttagcompound.hasKeyOfType("Color", 99)) {
            this.entityData.set(EntityShulker.DATA_COLOR_ID, nbttagcompound.getByte("Color"));
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setByte("AttachFace", (byte) this.getAttachFace().b());
        nbttagcompound.setByte("Peek", (Byte) this.entityData.get(EntityShulker.DATA_PEEK_ID));
        nbttagcompound.setByte("Color", (Byte) this.entityData.get(EntityShulker.DATA_COLOR_ID));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && !this.isPassenger() && !this.a(this.getChunkCoordinates(), this.getAttachFace())) {
            this.fx();
        }

        if (this.fy()) {
            this.fz();
        }

        if (this.level.isClientSide) {
            if (this.clientSideTeleportInterpolation > 0) {
                --this.clientSideTeleportInterpolation;
            } else {
                this.clientOldAttachPosition = null;
            }
        }

    }

    private void fx() {
        EnumDirection enumdirection = this.g(this.getChunkCoordinates());

        if (enumdirection != null) {
            this.setAttachFace(enumdirection);
        } else {
            this.p();
        }

    }

    @Override
    protected AxisAlignedBB ag() {
        float f = B(this.currentPeekAmount);
        EnumDirection enumdirection = this.getAttachFace().opposite();
        float f1 = this.getEntityType().k() / 2.0F;

        return a(enumdirection, f).d(this.locX() - (double) f1, this.locY(), this.locZ() - (double) f1);
    }

    private static float B(float f) {
        return 0.5F - MathHelper.sin((0.5F + f) * 3.1415927F) * 0.5F;
    }

    private boolean fy() {
        this.currentPeekAmountO = this.currentPeekAmount;
        float f = (float) this.getPeek() * 0.01F;

        if (this.currentPeekAmount == f) {
            return false;
        } else {
            if (this.currentPeekAmount > f) {
                this.currentPeekAmount = MathHelper.a(this.currentPeekAmount - 0.05F, f, 1.0F);
            } else {
                this.currentPeekAmount = MathHelper.a(this.currentPeekAmount + 0.05F, 0.0F, f);
            }

            return true;
        }
    }

    private void fz() {
        this.ah();
        float f = B(this.currentPeekAmount);
        float f1 = B(this.currentPeekAmountO);
        EnumDirection enumdirection = this.getAttachFace().opposite();
        float f2 = f - f1;

        if (f2 > 0.0F) {
            List<Entity> list = this.level.getEntities(this, a(enumdirection, f1, f).d(this.locX() - 0.5D, this.locY(), this.locZ() - 0.5D), IEntitySelector.NO_SPECTATORS.and((entity) -> {
                return !entity.isSameVehicle(this);
            }));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (!(entity instanceof EntityShulker) && !entity.noPhysics) {
                    entity.move(EnumMoveType.SHULKER, new Vec3D((double) (f2 * (float) enumdirection.getAdjacentX()), (double) (f2 * (float) enumdirection.getAdjacentY()), (double) (f2 * (float) enumdirection.getAdjacentZ())));
                }
            }

        }
    }

    public static AxisAlignedBB a(EnumDirection enumdirection, float f) {
        return a(enumdirection, -1.0F, f);
    }

    public static AxisAlignedBB a(EnumDirection enumdirection, float f, float f1) {
        double d0 = (double) Math.max(f, f1);
        double d1 = (double) Math.min(f, f1);

        return (new AxisAlignedBB(BlockPosition.ZERO)).b((double) enumdirection.getAdjacentX() * d0, (double) enumdirection.getAdjacentY() * d0, (double) enumdirection.getAdjacentZ() * d0).a((double) (-enumdirection.getAdjacentX()) * (1.0D + d1), (double) (-enumdirection.getAdjacentY()) * (1.0D + d1), (double) (-enumdirection.getAdjacentZ()) * (1.0D + d1));
    }

    @Override
    public double bk() {
        EntityTypes<?> entitytypes = this.getVehicle().getEntityType();

        return entitytypes != EntityTypes.BOAT && entitytypes != EntityTypes.MINECART ? super.bk() : 0.1875D - this.getVehicle().bl();
    }

    @Override
    public boolean a(Entity entity, boolean flag) {
        if (this.level.isClientSide()) {
            this.clientOldAttachPosition = null;
            this.clientSideTeleportInterpolation = 0;
        }

        this.setAttachFace(EnumDirection.DOWN);
        return super.a(entity, flag);
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
        if (this.level.isClientSide) {
            this.clientOldAttachPosition = this.getChunkCoordinates();
        }

        this.yBodyRotO = 0.0F;
        this.yBodyRot = 0.0F;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setYRot(0.0F);
        this.yHeadRot = this.getYRot();
        this.aZ();
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        if (enummovetype == EnumMoveType.SHULKER_BOX) {
            this.p();
        } else {
            super.move(enummovetype, vec3d);
        }

    }

    @Override
    public Vec3D getMot() {
        return Vec3D.ZERO;
    }

    @Override
    public void setMot(Vec3D vec3d) {}

    @Override
    public void setPosition(double d0, double d1, double d2) {
        BlockPosition blockposition = this.getChunkCoordinates();

        if (this.isPassenger()) {
            super.setPosition(d0, d1, d2);
        } else {
            super.setPosition((double) MathHelper.floor(d0) + 0.5D, (double) MathHelper.floor(d1 + 0.5D), (double) MathHelper.floor(d2) + 0.5D);
        }

        if (this.tickCount != 0) {
            BlockPosition blockposition1 = this.getChunkCoordinates();

            if (!blockposition1.equals(blockposition)) {
                this.entityData.set(EntityShulker.DATA_PEEK_ID, (byte) 0);
                this.hasImpulse = true;
                if (this.level.isClientSide && !this.isPassenger() && !blockposition1.equals(this.clientOldAttachPosition)) {
                    this.clientOldAttachPosition = blockposition;
                    this.clientSideTeleportInterpolation = 6;
                    this.xOld = this.locX();
                    this.yOld = this.locY();
                    this.zOld = this.locZ();
                }
            }

        }
    }

    @Nullable
    protected EnumDirection g(BlockPosition blockposition) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.a(blockposition, enumdirection)) {
                return enumdirection;
            }
        }

        return null;
    }

    boolean a(BlockPosition blockposition, EnumDirection enumdirection) {
        if (this.h(blockposition)) {
            return false;
        } else {
            EnumDirection enumdirection1 = enumdirection.opposite();

            if (!this.level.a(blockposition.shift(enumdirection), (Entity) this, enumdirection1)) {
                return false;
            } else {
                AxisAlignedBB axisalignedbb = a(enumdirection1, 1.0F).a(blockposition).shrink(1.0E-6D);

                return this.level.getCubes(this, axisalignedbb);
            }
        }
    }

    private boolean h(BlockPosition blockposition) {
        IBlockData iblockdata = this.level.getType(blockposition);

        if (iblockdata.isAir()) {
            return false;
        } else {
            boolean flag = iblockdata.a(Blocks.MOVING_PISTON) && blockposition.equals(this.getChunkCoordinates());

            return !flag;
        }
    }

    protected boolean p() {
        if (!this.isNoAI() && this.isAlive()) {
            BlockPosition blockposition = this.getChunkCoordinates();

            for (int i = 0; i < 5; ++i) {
                BlockPosition blockposition1 = blockposition.c(MathHelper.b(this.random, -8, 8), MathHelper.b(this.random, -8, 8), MathHelper.b(this.random, -8, 8));

                if (blockposition1.getY() > this.level.getMinBuildHeight() && this.level.isEmpty(blockposition1) && this.level.getWorldBorder().a(blockposition1) && this.level.getCubes(this, (new AxisAlignedBB(blockposition1)).shrink(1.0E-6D))) {
                    EnumDirection enumdirection = this.g(blockposition1);

                    if (enumdirection != null) {
                        this.decouple();
                        this.setAttachFace(enumdirection);
                        this.playSound(SoundEffects.SHULKER_TELEPORT, 1.0F, 1.0F);
                        this.setPosition((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY(), (double) blockposition1.getZ() + 0.5D);
                        this.entityData.set(EntityShulker.DATA_PEEK_ID, (byte) 0);
                        this.setGoalTarget((EntityLiving) null);
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
    public void a(double d0, double d1, double d2, float f, float f1, int i, boolean flag) {
        this.lerpSteps = 0;
        this.setPosition(d0, d1, d2);
        this.setYawPitch(f, f1);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        Entity entity;

        if (this.fA()) {
            entity = damagesource.k();
            if (entity instanceof EntityArrow) {
                return false;
            }
        }

        if (!super.damageEntity(damagesource, f)) {
            return false;
        } else {
            if ((double) this.getHealth() < (double) this.getMaxHealth() * 0.5D && this.random.nextInt(4) == 0) {
                this.p();
            } else if (damagesource.b()) {
                entity = damagesource.k();
                if (entity != null && entity.getEntityType() == EntityTypes.SHULKER_BULLET) {
                    this.fB();
                }
            }

            return true;
        }
    }

    private boolean fA() {
        return this.getPeek() == 0;
    }

    private void fB() {
        Vec3D vec3d = this.getPositionVector();
        AxisAlignedBB axisalignedbb = this.getBoundingBox();

        if (!this.fA() && this.p()) {
            int i = this.level.a((EntityTypeTest) EntityTypes.SHULKER, axisalignedbb.g(8.0D), Entity::isAlive).size();
            float f = (float) (i - 1) / 5.0F;

            if (this.level.random.nextFloat() >= f) {
                EntityShulker entityshulker = (EntityShulker) EntityTypes.SHULKER.a(this.level);
                EnumColor enumcolor = this.fw();

                if (enumcolor != null) {
                    entityshulker.a(enumcolor);
                }

                entityshulker.d(vec3d);
                this.level.addEntity(entityshulker);
            }
        }
    }

    @Override
    public boolean bi() {
        return this.isAlive();
    }

    public EnumDirection getAttachFace() {
        return (EnumDirection) this.entityData.get(EntityShulker.DATA_ATTACH_FACE_ID);
    }

    public void setAttachFace(EnumDirection enumdirection) {
        this.entityData.set(EntityShulker.DATA_ATTACH_FACE_ID, enumdirection);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityShulker.DATA_ATTACH_FACE_ID.equals(datawatcherobject)) {
            this.a(this.ag());
        }

        super.a(datawatcherobject);
    }

    public int getPeek() {
        return (Byte) this.entityData.get(EntityShulker.DATA_PEEK_ID);
    }

    public void setPeek(int i) {
        if (!this.level.isClientSide) {
            this.getAttributeInstance(GenericAttributes.ARMOR).removeModifier(EntityShulker.COVERED_ARMOR_MODIFIER);
            if (i == 0) {
                this.getAttributeInstance(GenericAttributes.ARMOR).addModifier(EntityShulker.COVERED_ARMOR_MODIFIER);
                this.playSound(SoundEffects.SHULKER_CLOSE, 1.0F, 1.0F);
                this.a(GameEvent.SHULKER_CLOSE);
            } else {
                this.playSound(SoundEffects.SHULKER_OPEN, 1.0F, 1.0F);
                this.a(GameEvent.SHULKER_OPEN);
            }
        }

        this.entityData.set(EntityShulker.DATA_PEEK_ID, (byte) i);
    }

    public float z(float f) {
        return MathHelper.h(f, this.currentPeekAmountO, this.currentPeekAmount);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.5F;
    }

    @Override
    public void a(PacketPlayOutSpawnEntityLiving packetplayoutspawnentityliving) {
        super.a(packetplayoutspawnentityliving);
        this.yBodyRot = 0.0F;
    }

    @Override
    public int eZ() {
        return 180;
    }

    @Override
    public int fa() {
        return 180;
    }

    @Override
    public void collide(Entity entity) {}

    @Override
    public float bp() {
        return 0.0F;
    }

    public Optional<Vec3D> A(float f) {
        if (this.clientOldAttachPosition != null && this.clientSideTeleportInterpolation > 0) {
            double d0 = (double) ((float) this.clientSideTeleportInterpolation - f) / 6.0D;

            d0 *= d0;
            BlockPosition blockposition = this.getChunkCoordinates();
            double d1 = (double) (blockposition.getX() - this.clientOldAttachPosition.getX()) * d0;
            double d2 = (double) (blockposition.getY() - this.clientOldAttachPosition.getY()) * d0;
            double d3 = (double) (blockposition.getZ() - this.clientOldAttachPosition.getZ()) * d0;

            return Optional.of(new Vec3D(-d1, -d2, -d3));
        } else {
            return Optional.empty();
        }
    }

    private void a(EnumColor enumcolor) {
        this.entityData.set(EntityShulker.DATA_COLOR_ID, (byte) enumcolor.getColorIndex());
    }

    @Nullable
    public EnumColor fw() {
        byte b0 = (Byte) this.entityData.get(EntityShulker.DATA_COLOR_ID);

        return b0 != 16 && b0 <= 15 ? EnumColor.fromColorIndex(b0) : null;
    }

    private class d extends ControllerLook {

        public d(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        protected void b() {}

        @Override
        protected Optional<Float> i() {
            EnumDirection enumdirection = EntityShulker.this.getAttachFace().opposite();
            Vector3fa vector3fa = EntityShulker.FORWARD.e();

            vector3fa.a(enumdirection.a());
            BaseBlockPosition baseblockposition = enumdirection.p();
            Vector3fa vector3fa1 = new Vector3fa((float) baseblockposition.getX(), (float) baseblockposition.getY(), (float) baseblockposition.getZ());

            vector3fa1.e(vector3fa);
            double d0 = this.wantedX - this.mob.locX();
            double d1 = this.wantedY - this.mob.getHeadY();
            double d2 = this.wantedZ - this.mob.locZ();
            Vector3fa vector3fa2 = new Vector3fa((float) d0, (float) d1, (float) d2);
            float f = vector3fa1.d(vector3fa2);
            float f1 = vector3fa.d(vector3fa2);

            return Math.abs(f) <= 1.0E-5F && Math.abs(f1) <= 1.0E-5F ? Optional.empty() : Optional.of((float) (MathHelper.d((double) (-f), (double) f1) * 57.2957763671875D));
        }

        @Override
        protected Optional<Float> h() {
            return Optional.of(0.0F);
        }
    }

    private class a extends PathfinderGoal {

        private int attackTime;

        public a() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            EntityLiving entityliving = EntityShulker.this.getGoalTarget();

            return entityliving != null && entityliving.isAlive() ? EntityShulker.this.level.getDifficulty() != EnumDifficulty.PEACEFUL : false;
        }

        @Override
        public void c() {
            this.attackTime = 20;
            EntityShulker.this.setPeek(100);
        }

        @Override
        public void d() {
            EntityShulker.this.setPeek(0);
        }

        @Override
        public void e() {
            if (EntityShulker.this.level.getDifficulty() != EnumDifficulty.PEACEFUL) {
                --this.attackTime;
                EntityLiving entityliving = EntityShulker.this.getGoalTarget();

                EntityShulker.this.getControllerLook().a(entityliving, 180.0F, 180.0F);
                double d0 = EntityShulker.this.f((Entity) entityliving);

                if (d0 < 400.0D) {
                    if (this.attackTime <= 0) {
                        this.attackTime = 20 + EntityShulker.this.random.nextInt(10) * 20 / 2;
                        EntityShulker.this.level.addEntity(new EntityShulkerBullet(EntityShulker.this.level, EntityShulker.this, entityliving, EntityShulker.this.getAttachFace().n()));
                        EntityShulker.this.playSound(SoundEffects.SHULKER_SHOOT, 2.0F, (EntityShulker.this.random.nextFloat() - EntityShulker.this.random.nextFloat()) * 0.2F + 1.0F);
                    }
                } else {
                    EntityShulker.this.setGoalTarget((EntityLiving) null);
                }

                super.e();
            }
        }
    }

    private class f extends PathfinderGoal {

        private int peekTime;

        f() {}

        @Override
        public boolean a() {
            return EntityShulker.this.getGoalTarget() == null && EntityShulker.this.random.nextInt(40) == 0 && EntityShulker.this.a(EntityShulker.this.getChunkCoordinates(), EntityShulker.this.getAttachFace());
        }

        @Override
        public boolean b() {
            return EntityShulker.this.getGoalTarget() == null && this.peekTime > 0;
        }

        @Override
        public void c() {
            this.peekTime = 20 * (1 + EntityShulker.this.random.nextInt(3));
            EntityShulker.this.setPeek(30);
        }

        @Override
        public void d() {
            if (EntityShulker.this.getGoalTarget() == null) {
                EntityShulker.this.setPeek(0);
            }

        }

        @Override
        public void e() {
            --this.peekTime;
        }
    }

    private class e extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        public e(EntityShulker entityshulker) {
            super(entityshulker, EntityHuman.class, true);
        }

        @Override
        public boolean a() {
            return EntityShulker.this.level.getDifficulty() == EnumDifficulty.PEACEFUL ? false : super.a();
        }

        @Override
        protected AxisAlignedBB a(double d0) {
            EnumDirection enumdirection = ((EntityShulker) this.mob).getAttachFace();

            return enumdirection.n() == EnumDirection.EnumAxis.X ? this.mob.getBoundingBox().grow(4.0D, d0, d0) : (enumdirection.n() == EnumDirection.EnumAxis.Z ? this.mob.getBoundingBox().grow(d0, d0, 4.0D) : this.mob.getBoundingBox().grow(d0, 4.0D, d0));
        }
    }

    private static class c extends PathfinderGoalNearestAttackableTarget<EntityLiving> {

        public c(EntityShulker entityshulker) {
            super(entityshulker, EntityLiving.class, 10, true, false, (entityliving) -> {
                return entityliving instanceof IMonster;
            });
        }

        @Override
        public boolean a() {
            return this.mob.getScoreboardTeam() == null ? false : super.a();
        }

        @Override
        protected AxisAlignedBB a(double d0) {
            EnumDirection enumdirection = ((EntityShulker) this.mob).getAttachFace();

            return enumdirection.n() == EnumDirection.EnumAxis.X ? this.mob.getBoundingBox().grow(4.0D, d0, d0) : (enumdirection.n() == EnumDirection.EnumAxis.Z ? this.mob.getBoundingBox().grow(d0, d0, 4.0D) : this.mob.getBoundingBox().grow(d0, 4.0D, d0));
        }
    }

    private static class b extends EntityAIBodyControl {

        public b(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        public void a() {}
    }
}
