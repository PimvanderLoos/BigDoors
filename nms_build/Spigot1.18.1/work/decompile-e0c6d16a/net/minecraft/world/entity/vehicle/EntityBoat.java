package net.minecraft.world.entity.vehicle;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayInBoatMove;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.EntityWaterAnimal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockWaterLily;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class EntityBoat extends Entity {

    private static final DataWatcherObject<Integer> DATA_ID_HURT = DataWatcher.defineId(EntityBoat.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_ID_HURTDIR = DataWatcher.defineId(EntityBoat.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Float> DATA_ID_DAMAGE = DataWatcher.defineId(EntityBoat.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Integer> DATA_ID_TYPE = DataWatcher.defineId(EntityBoat.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> DATA_ID_PADDLE_LEFT = DataWatcher.defineId(EntityBoat.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_ID_PADDLE_RIGHT = DataWatcher.defineId(EntityBoat.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> DATA_ID_BUBBLE_TIME = DataWatcher.defineId(EntityBoat.class, DataWatcherRegistry.INT);
    public static final int PADDLE_LEFT = 0;
    public static final int PADDLE_RIGHT = 1;
    private static final int TIME_TO_EJECT = 60;
    private static final double PADDLE_SPEED = 0.39269909262657166D;
    public static final double PADDLE_SOUND_TIME = 0.7853981852531433D;
    public static final int BUBBLE_TIME = 60;
    private final float[] paddlePositions;
    private float invFriction;
    private float outOfControlTicks;
    private float deltaRotation;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputUp;
    private boolean inputDown;
    private double waterLevel;
    private float landFriction;
    private EntityBoat.EnumStatus status;
    private EntityBoat.EnumStatus oldStatus;
    private double lastYd;
    private boolean isAboveBubbleColumn;
    private boolean bubbleColumnDirectionIsDown;
    private float bubbleMultiplier;
    private float bubbleAngle;
    private float bubbleAngleO;

    public EntityBoat(EntityTypes<? extends EntityBoat> entitytypes, World world) {
        super(entitytypes, world);
        this.paddlePositions = new float[2];
        this.blocksBuilding = true;
    }

    public EntityBoat(World world, double d0, double d1, double d2) {
        this(EntityTypes.BOAT, world);
        this.setPos(d0, d1, d2);
        this.xo = d0;
        this.yo = d1;
        this.zo = d2;
    }

    @Override
    protected float getEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(EntityBoat.DATA_ID_HURT, 0);
        this.entityData.define(EntityBoat.DATA_ID_HURTDIR, 1);
        this.entityData.define(EntityBoat.DATA_ID_DAMAGE, 0.0F);
        this.entityData.define(EntityBoat.DATA_ID_TYPE, EntityBoat.EnumBoatType.OAK.ordinal());
        this.entityData.define(EntityBoat.DATA_ID_PADDLE_LEFT, false);
        this.entityData.define(EntityBoat.DATA_ID_PADDLE_RIGHT, false);
        this.entityData.define(EntityBoat.DATA_ID_BUBBLE_TIME, 0);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return canVehicleCollide(this, entity);
    }

    public static boolean canVehicleCollide(Entity entity, Entity entity1) {
        return (entity1.canBeCollidedWith() || entity1.isPushable()) && !entity.isPassengerOfSameVehicle(entity1);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected Vec3D getRelativePortalPosition(EnumDirection.EnumAxis enumdirection_enumaxis, BlockUtil.Rectangle blockutil_rectangle) {
        return EntityLiving.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(enumdirection_enumaxis, blockutil_rectangle));
    }

    @Override
    public double getPassengersRidingOffset() {
        return -0.1D;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else if (!this.level.isClientSide && !this.isRemoved()) {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.setDamage(this.getDamage() + f * 10.0F);
            this.markHurt();
            this.gameEvent(GameEvent.ENTITY_DAMAGED, damagesource.getEntity());
            boolean flag = damagesource.getEntity() instanceof EntityHuman && ((EntityHuman) damagesource.getEntity()).getAbilities().instabuild;

            if (flag || this.getDamage() > 40.0F) {
                if (!flag && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnAtLocation((IMaterial) this.getDropItem());
                }

                this.discard();
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    public void onAboveBubbleCol(boolean flag) {
        if (!this.level.isClientSide) {
            this.isAboveBubbleColumn = true;
            this.bubbleColumnDirectionIsDown = flag;
            if (this.getBubbleTime() == 0) {
                this.setBubbleTime(60);
            }
        }

        this.level.addParticle(Particles.SPLASH, this.getX() + (double) this.random.nextFloat(), this.getY() + 0.7D, this.getZ() + (double) this.random.nextFloat(), 0.0D, 0.0D, 0.0D);
        if (this.random.nextInt(20) == 0) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), this.getSwimSplashSound(), this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat(), false);
        }

        this.gameEvent(GameEvent.SPLASH, this.getControllingPassenger());
    }

    @Override
    public void push(Entity entity) {
        if (entity instanceof EntityBoat) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entity);
        }

    }

    public Item getDropItem() {
        switch (this.getBoatType()) {
            case OAK:
            default:
                return Items.OAK_BOAT;
            case SPRUCE:
                return Items.SPRUCE_BOAT;
            case BIRCH:
                return Items.BIRCH_BOAT;
            case JUNGLE:
                return Items.JUNGLE_BOAT;
            case ACACIA:
                return Items.ACACIA_BOAT;
            case DARK_OAK:
                return Items.DARK_OAK_BOAT;
        }
    }

    @Override
    public void animateHurt() {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() * 11.0F);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void lerpTo(double d0, double d1, double d2, float f, float f1, int i, boolean flag) {
        this.lerpX = d0;
        this.lerpY = d1;
        this.lerpZ = d2;
        this.lerpYRot = (double) f;
        this.lerpXRot = (double) f1;
        this.lerpSteps = 10;
    }

    @Override
    public EnumDirection getMotionDirection() {
        return this.getDirection().getClockWise();
    }

    @Override
    public void tick() {
        this.oldStatus = this.status;
        this.status = this.getStatus();
        if (this.status != EntityBoat.EnumStatus.UNDER_WATER && this.status != EntityBoat.EnumStatus.UNDER_FLOWING_WATER) {
            this.outOfControlTicks = 0.0F;
        } else {
            ++this.outOfControlTicks;
        }

        if (!this.level.isClientSide && this.outOfControlTicks >= 60.0F) {
            this.ejectPassengers();
        }

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        super.tick();
        this.tickLerp();
        if (this.isControlledByLocalInstance()) {
            if (!(this.getFirstPassenger() instanceof EntityHuman)) {
                this.setPaddleState(false, false);
            }

            this.floatBoat();
            if (this.level.isClientSide) {
                this.controlBoat();
                this.level.sendPacketToServer(new PacketPlayInBoatMove(this.getPaddleState(0), this.getPaddleState(1)));
            }

            this.move(EnumMoveType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3D.ZERO);
        }

        this.tickBubbleColumn();

        for (int i = 0; i <= 1; ++i) {
            if (this.getPaddleState(i)) {
                if (!this.isSilent() && (double) (this.paddlePositions[i] % 6.2831855F) <= 0.7853981852531433D && ((double) this.paddlePositions[i] + 0.39269909262657166D) % 6.2831854820251465D >= 0.7853981852531433D) {
                    SoundEffect soundeffect = this.getPaddleSound();

                    if (soundeffect != null) {
                        Vec3D vec3d = this.getViewVector(1.0F);
                        double d0 = i == 1 ? -vec3d.z : vec3d.z;
                        double d1 = i == 1 ? vec3d.x : -vec3d.x;

                        this.level.playSound((EntityHuman) null, this.getX() + d0, this.getY(), this.getZ() + d1, soundeffect, this.getSoundSource(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
                        this.level.gameEvent(this.getControllingPassenger(), GameEvent.SPLASH, new BlockPosition(this.getX() + d0, this.getY(), this.getZ() + d1));
                    }
                }

                this.paddlePositions[i] = (float) ((double) this.paddlePositions[i] + 0.39269909262657166D);
            } else {
                this.paddlePositions[i] = 0.0F;
            }
        }

        this.checkInsideBlocks();
        List<Entity> list = this.level.getEntities((Entity) this, this.getBoundingBox().inflate(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), IEntitySelector.pushableBy(this));

        if (!list.isEmpty()) {
            boolean flag = !this.level.isClientSide && !(this.getControllingPassenger() instanceof EntityHuman);

            for (int j = 0; j < list.size(); ++j) {
                Entity entity = (Entity) list.get(j);

                if (!entity.hasPassenger((Entity) this)) {
                    if (flag && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getBbWidth() < this.getBbWidth() && entity instanceof EntityLiving && !(entity instanceof EntityWaterAnimal) && !(entity instanceof EntityHuman)) {
                        entity.startRiding(this);
                    } else {
                        this.push(entity);
                    }
                }
            }
        }

    }

    private void tickBubbleColumn() {
        int i;

        if (this.level.isClientSide) {
            i = this.getBubbleTime();
            if (i > 0) {
                this.bubbleMultiplier += 0.05F;
            } else {
                this.bubbleMultiplier -= 0.1F;
            }

            this.bubbleMultiplier = MathHelper.clamp(this.bubbleMultiplier, 0.0F, 1.0F);
            this.bubbleAngleO = this.bubbleAngle;
            this.bubbleAngle = 10.0F * (float) Math.sin((double) (0.5F * (float) this.level.getGameTime())) * this.bubbleMultiplier;
        } else {
            if (!this.isAboveBubbleColumn) {
                this.setBubbleTime(0);
            }

            i = this.getBubbleTime();
            if (i > 0) {
                --i;
                this.setBubbleTime(i);
                int j = 60 - i - 1;

                if (j > 0 && i == 0) {
                    this.setBubbleTime(0);
                    Vec3D vec3d = this.getDeltaMovement();

                    if (this.bubbleColumnDirectionIsDown) {
                        this.setDeltaMovement(vec3d.add(0.0D, -0.7D, 0.0D));
                        this.ejectPassengers();
                    } else {
                        this.setDeltaMovement(vec3d.x, this.hasPassenger((entity) -> {
                            return entity instanceof EntityHuman;
                        }) ? 2.7D : 0.6D, vec3d.z);
                    }
                }

                this.isAboveBubbleColumn = false;
            }
        }

    }

    @Nullable
    protected SoundEffect getPaddleSound() {
        switch (this.getStatus()) {
            case IN_WATER:
            case UNDER_WATER:
            case UNDER_FLOWING_WATER:
                return SoundEffects.BOAT_PADDLE_WATER;
            case ON_LAND:
                return SoundEffects.BOAT_PADDLE_LAND;
            case IN_AIR:
            default:
                return null;
        }
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.lerpYRot - (double) this.getYRot());

            this.setYRot(this.getYRot() + (float) d3 / (float) this.lerpSteps);
            this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    public void setPaddleState(boolean flag, boolean flag1) {
        this.entityData.set(EntityBoat.DATA_ID_PADDLE_LEFT, flag);
        this.entityData.set(EntityBoat.DATA_ID_PADDLE_RIGHT, flag1);
    }

    public float getRowingTime(int i, float f) {
        return this.getPaddleState(i) ? (float) MathHelper.clampedLerp((double) this.paddlePositions[i] - 0.39269909262657166D, (double) this.paddlePositions[i], (double) f) : 0.0F;
    }

    private EntityBoat.EnumStatus getStatus() {
        EntityBoat.EnumStatus entityboat_enumstatus = this.isUnderwater();

        if (entityboat_enumstatus != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return entityboat_enumstatus;
        } else if (this.checkInWater()) {
            return EntityBoat.EnumStatus.IN_WATER;
        } else {
            float f = this.getGroundFriction();

            if (f > 0.0F) {
                this.landFriction = f;
                return EntityBoat.EnumStatus.ON_LAND;
            } else {
                return EntityBoat.EnumStatus.IN_AIR;
            }
        }
    }

    public float getWaterLevelAbove() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.ceil(axisalignedbb.maxY - this.lastYd);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int k1 = k;

        while (k1 < l) {
            float f = 0.0F;
            int l1 = i;

            label35:
            while (true) {
                if (l1 < j) {
                    int i2 = i1;

                    while (true) {
                        if (i2 >= j1) {
                            ++l1;
                            continue label35;
                        }

                        blockposition_mutableblockposition.set(l1, k1, i2);
                        Fluid fluid = this.level.getFluidState(blockposition_mutableblockposition);

                        if (fluid.is((Tag) TagsFluid.WATER)) {
                            f = Math.max(f, fluid.getHeight(this.level, blockposition_mutableblockposition));
                        }

                        if (f >= 1.0F) {
                            break;
                        }

                        ++i2;
                    }
                } else if (f < 1.0F) {
                    return (float) blockposition_mutableblockposition.getY() + f;
                }

                ++k1;
                break;
            }
        }

        return (float) (l + 1);
    }

    public float getGroundFriction() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        int i = MathHelper.floor(axisalignedbb1.minX) - 1;
        int j = MathHelper.ceil(axisalignedbb1.maxX) + 1;
        int k = MathHelper.floor(axisalignedbb1.minY) - 1;
        int l = MathHelper.ceil(axisalignedbb1.maxY) + 1;
        int i1 = MathHelper.floor(axisalignedbb1.minZ) - 1;
        int j1 = MathHelper.ceil(axisalignedbb1.maxZ) + 1;
        VoxelShape voxelshape = VoxelShapes.create(axisalignedbb1);
        float f = 0.0F;
        int k1 = 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l1 = i; l1 < j; ++l1) {
            for (int i2 = i1; i2 < j1; ++i2) {
                int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);

                if (j2 != 2) {
                    for (int k2 = k; k2 < l; ++k2) {
                        if (j2 <= 0 || k2 != k && k2 != l - 1) {
                            blockposition_mutableblockposition.set(l1, k2, i2);
                            IBlockData iblockdata = this.level.getBlockState(blockposition_mutableblockposition);

                            if (!(iblockdata.getBlock() instanceof BlockWaterLily) && VoxelShapes.joinIsNotEmpty(iblockdata.getCollisionShape(this.level, blockposition_mutableblockposition).move((double) l1, (double) k2, (double) i2), voxelshape, OperatorBoolean.AND)) {
                                f += iblockdata.getBlock().getFriction();
                                ++k1;
                            }
                        }
                    }
                }
            }
        }

        return f / (float) k1;
    }

    private boolean checkInWater() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.ceil(axisalignedbb.minY + 0.001D);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;

        this.waterLevel = -1.7976931348623157E308D;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockposition_mutableblockposition.set(k1, l1, i2);
                    Fluid fluid = this.level.getFluidState(blockposition_mutableblockposition);

                    if (fluid.is((Tag) TagsFluid.WATER)) {
                        float f = (float) l1 + fluid.getHeight(this.level, blockposition_mutableblockposition);

                        this.waterLevel = Math.max((double) f, this.waterLevel);
                        flag |= axisalignedbb.minY < (double) f;
                    }
                }
            }
        }

        return flag;
    }

    @Nullable
    private EntityBoat.EnumStatus isUnderwater() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        double d0 = axisalignedbb.maxY + 0.001D;
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.ceil(d0);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockposition_mutableblockposition.set(k1, l1, i2);
                    Fluid fluid = this.level.getFluidState(blockposition_mutableblockposition);

                    if (fluid.is((Tag) TagsFluid.WATER) && d0 < (double) ((float) blockposition_mutableblockposition.getY() + fluid.getHeight(this.level, blockposition_mutableblockposition))) {
                        if (!fluid.isSource()) {
                            return EntityBoat.EnumStatus.UNDER_FLOWING_WATER;
                        }

                        flag = true;
                    }
                }
            }
        }

        return flag ? EntityBoat.EnumStatus.UNDER_WATER : null;
    }

    private void floatBoat() {
        double d0 = -0.03999999910593033D;
        double d1 = this.isNoGravity() ? 0.0D : -0.03999999910593033D;
        double d2 = 0.0D;

        this.invFriction = 0.05F;
        if (this.oldStatus == EntityBoat.EnumStatus.IN_AIR && this.status != EntityBoat.EnumStatus.IN_AIR && this.status != EntityBoat.EnumStatus.ON_LAND) {
            this.waterLevel = this.getY(1.0D);
            this.setPos(this.getX(), (double) (this.getWaterLevelAbove() - this.getBbHeight()) + 0.101D, this.getZ());
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            this.lastYd = 0.0D;
            this.status = EntityBoat.EnumStatus.IN_WATER;
        } else {
            if (this.status == EntityBoat.EnumStatus.IN_WATER) {
                d2 = (this.waterLevel - this.getY()) / (double) this.getBbHeight();
                this.invFriction = 0.9F;
            } else if (this.status == EntityBoat.EnumStatus.UNDER_FLOWING_WATER) {
                d1 = -7.0E-4D;
                this.invFriction = 0.9F;
            } else if (this.status == EntityBoat.EnumStatus.UNDER_WATER) {
                d2 = 0.009999999776482582D;
                this.invFriction = 0.45F;
            } else if (this.status == EntityBoat.EnumStatus.IN_AIR) {
                this.invFriction = 0.9F;
            } else if (this.status == EntityBoat.EnumStatus.ON_LAND) {
                this.invFriction = this.landFriction;
                if (this.getControllingPassenger() instanceof EntityHuman) {
                    this.landFriction /= 2.0F;
                }
            }

            Vec3D vec3d = this.getDeltaMovement();

            this.setDeltaMovement(vec3d.x * (double) this.invFriction, vec3d.y + d1, vec3d.z * (double) this.invFriction);
            this.deltaRotation *= this.invFriction;
            if (d2 > 0.0D) {
                Vec3D vec3d1 = this.getDeltaMovement();

                this.setDeltaMovement(vec3d1.x, (vec3d1.y + d2 * 0.06153846016296973D) * 0.75D, vec3d1.z);
            }
        }

    }

    private void controlBoat() {
        if (this.isVehicle()) {
            float f = 0.0F;

            if (this.inputLeft) {
                --this.deltaRotation;
            }

            if (this.inputRight) {
                ++this.deltaRotation;
            }

            if (this.inputRight != this.inputLeft && !this.inputUp && !this.inputDown) {
                f += 0.005F;
            }

            this.setYRot(this.getYRot() + this.deltaRotation);
            if (this.inputUp) {
                f += 0.04F;
            }

            if (this.inputDown) {
                f -= 0.005F;
            }

            this.setDeltaMovement(this.getDeltaMovement().add((double) (MathHelper.sin(-this.getYRot() * 0.017453292F) * f), 0.0D, (double) (MathHelper.cos(this.getYRot() * 0.017453292F) * f)));
            this.setPaddleState(this.inputRight && !this.inputLeft || this.inputUp, this.inputLeft && !this.inputRight || this.inputUp);
        }
    }

    @Override
    public void positionRider(Entity entity) {
        if (this.hasPassenger(entity)) {
            float f = 0.0F;
            float f1 = (float) ((this.isRemoved() ? 0.009999999776482582D : this.getPassengersRidingOffset()) + entity.getMyRidingOffset());

            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(entity);

                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }

                if (entity instanceof EntityAnimal) {
                    f = (float) ((double) f + 0.2D);
                }
            }

            Vec3D vec3d = (new Vec3D((double) f, 0.0D, 0.0D)).yRot(-this.getYRot() * 0.017453292F - 1.5707964F);

            entity.setPos(this.getX() + vec3d.x, this.getY() + (double) f1, this.getZ() + vec3d.z);
            entity.setYRot(entity.getYRot() + this.deltaRotation);
            entity.setYHeadRot(entity.getYHeadRot() + this.deltaRotation);
            this.clampRotation(entity);
            if (entity instanceof EntityAnimal && this.getPassengers().size() > 1) {
                int j = entity.getId() % 2 == 0 ? 90 : 270;

                entity.setYBodyRot(((EntityAnimal) entity).yBodyRot + (float) j);
                entity.setYHeadRot(entity.getYHeadRot() + (float) j);
            }

        }
    }

    @Override
    public Vec3D getDismountLocationForPassenger(EntityLiving entityliving) {
        Vec3D vec3d = getCollisionHorizontalEscapeVector((double) (this.getBbWidth() * MathHelper.SQRT_OF_TWO), (double) entityliving.getBbWidth(), entityliving.getYRot());
        double d0 = this.getX() + vec3d.x;
        double d1 = this.getZ() + vec3d.z;
        BlockPosition blockposition = new BlockPosition(d0, this.getBoundingBox().maxY, d1);
        BlockPosition blockposition1 = blockposition.below();

        if (!this.level.isWaterAt(blockposition1)) {
            List<Vec3D> list = Lists.newArrayList();
            double d2 = this.level.getBlockFloorHeight(blockposition);

            if (DismountUtil.isBlockFloorValid(d2)) {
                list.add(new Vec3D(d0, (double) blockposition.getY() + d2, d1));
            }

            double d3 = this.level.getBlockFloorHeight(blockposition1);

            if (DismountUtil.isBlockFloorValid(d3)) {
                list.add(new Vec3D(d0, (double) blockposition1.getY() + d3, d1));
            }

            UnmodifiableIterator unmodifiableiterator = entityliving.getDismountPoses().iterator();

            while (unmodifiableiterator.hasNext()) {
                EntityPose entitypose = (EntityPose) unmodifiableiterator.next();
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Vec3D vec3d1 = (Vec3D) iterator.next();

                    if (DismountUtil.canDismountTo(this.level, vec3d1, entityliving, entitypose)) {
                        entityliving.setPose(entitypose);
                        return vec3d1;
                    }
                }
            }
        }

        return super.getDismountLocationForPassenger(entityliving);
    }

    protected void clampRotation(Entity entity) {
        entity.setYBodyRot(this.getYRot());
        float f = MathHelper.wrapDegrees(entity.getYRot() - this.getYRot());
        float f1 = MathHelper.clamp(f, -105.0F, 105.0F);

        entity.yRotO += f1 - f;
        entity.setYRot(entity.getYRot() + f1 - f);
        entity.setYHeadRot(entity.getYRot());
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        this.clampRotation(entity);
    }

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.putString("Type", this.getBoatType().getName());
    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.contains("Type", 8)) {
            this.setType(EntityBoat.EnumBoatType.byName(nbttagcompound.getString("Type")));
        }

    }

    @Override
    public EnumInteractionResult interact(EntityHuman entityhuman, EnumHand enumhand) {
        return entityhuman.isSecondaryUseActive() ? EnumInteractionResult.PASS : (this.outOfControlTicks < 60.0F ? (!this.level.isClientSide ? (entityhuman.startRiding(this) ? EnumInteractionResult.CONSUME : EnumInteractionResult.PASS) : EnumInteractionResult.SUCCESS) : EnumInteractionResult.PASS);
    }

    @Override
    protected void checkFallDamage(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        this.lastYd = this.getDeltaMovement().y;
        if (!this.isPassenger()) {
            if (flag) {
                if (this.fallDistance > 3.0F) {
                    if (this.status != EntityBoat.EnumStatus.ON_LAND) {
                        this.resetFallDistance();
                        return;
                    }

                    this.causeFallDamage(this.fallDistance, 1.0F, DamageSource.FALL);
                    if (!this.level.isClientSide && !this.isRemoved()) {
                        this.kill();
                        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            int i;

                            for (i = 0; i < 3; ++i) {
                                this.spawnAtLocation((IMaterial) this.getBoatType().getPlanks());
                            }

                            for (i = 0; i < 2; ++i) {
                                this.spawnAtLocation((IMaterial) Items.STICK);
                            }
                        }
                    }
                }

                this.resetFallDistance();
            } else if (!this.level.getFluidState(this.blockPosition().below()).is((Tag) TagsFluid.WATER) && d0 < 0.0D) {
                this.fallDistance = (float) ((double) this.fallDistance - d0);
            }

        }
    }

    public boolean getPaddleState(int i) {
        return (Boolean) this.entityData.get(i == 0 ? EntityBoat.DATA_ID_PADDLE_LEFT : EntityBoat.DATA_ID_PADDLE_RIGHT) && this.getControllingPassenger() != null;
    }

    public void setDamage(float f) {
        this.entityData.set(EntityBoat.DATA_ID_DAMAGE, f);
    }

    public float getDamage() {
        return (Float) this.entityData.get(EntityBoat.DATA_ID_DAMAGE);
    }

    public void setHurtTime(int i) {
        this.entityData.set(EntityBoat.DATA_ID_HURT, i);
    }

    public int getHurtTime() {
        return (Integer) this.entityData.get(EntityBoat.DATA_ID_HURT);
    }

    private void setBubbleTime(int i) {
        this.entityData.set(EntityBoat.DATA_ID_BUBBLE_TIME, i);
    }

    private int getBubbleTime() {
        return (Integer) this.entityData.get(EntityBoat.DATA_ID_BUBBLE_TIME);
    }

    public float getBubbleAngle(float f) {
        return MathHelper.lerp(f, this.bubbleAngleO, this.bubbleAngle);
    }

    public void setHurtDir(int i) {
        this.entityData.set(EntityBoat.DATA_ID_HURTDIR, i);
    }

    public int getHurtDir() {
        return (Integer) this.entityData.get(EntityBoat.DATA_ID_HURTDIR);
    }

    public void setType(EntityBoat.EnumBoatType entityboat_enumboattype) {
        this.entityData.set(EntityBoat.DATA_ID_TYPE, entityboat_enumboattype.ordinal());
    }

    public EntityBoat.EnumBoatType getBoatType() {
        return EntityBoat.EnumBoatType.byId((Integer) this.entityData.get(EntityBoat.DATA_ID_TYPE));
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengers().size() < 2 && !this.isEyeInFluid(TagsFluid.WATER);
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return this.getFirstPassenger();
    }

    public void setInput(boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        this.inputLeft = flag;
        this.inputRight = flag1;
        this.inputUp = flag2;
        this.inputDown = flag3;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new PacketPlayOutSpawnEntity(this);
    }

    @Override
    public boolean isUnderWater() {
        return this.status == EntityBoat.EnumStatus.UNDER_WATER || this.status == EntityBoat.EnumStatus.UNDER_FLOWING_WATER;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    public static enum EnumBoatType {

        OAK(Blocks.OAK_PLANKS, "oak"), SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"), BIRCH(Blocks.BIRCH_PLANKS, "birch"), JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"), ACACIA(Blocks.ACACIA_PLANKS, "acacia"), DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

        private final String name;
        private final Block planks;

        private EnumBoatType(Block block, String s) {
            this.name = s;
            this.planks = block;
        }

        public String getName() {
            return this.name;
        }

        public Block getPlanks() {
            return this.planks;
        }

        public String toString() {
            return this.name;
        }

        public static EntityBoat.EnumBoatType byId(int i) {
            EntityBoat.EnumBoatType[] aentityboat_enumboattype = values();

            if (i < 0 || i >= aentityboat_enumboattype.length) {
                i = 0;
            }

            return aentityboat_enumboattype[i];
        }

        public static EntityBoat.EnumBoatType byName(String s) {
            EntityBoat.EnumBoatType[] aentityboat_enumboattype = values();

            for (int i = 0; i < aentityboat_enumboattype.length; ++i) {
                if (aentityboat_enumboattype[i].getName().equals(s)) {
                    return aentityboat_enumboattype[i];
                }
            }

            return aentityboat_enumboattype[0];
        }
    }

    public static enum EnumStatus {

        IN_WATER, UNDER_WATER, UNDER_FLOWING_WATER, ON_LAND, IN_AIR;

        private EnumStatus() {}
    }
}
