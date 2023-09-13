package net.minecraft.world.entity.animal.sniffer;

import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.Particles;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.EnumRenderType;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class Sniffer extends EntityAnimal {

    private static final int DIGGING_PARTICLES_DELAY_TICKS = 1700;
    private static final int DIGGING_PARTICLES_DURATION_TICKS = 6000;
    private static final int DIGGING_PARTICLES_AMOUNT = 30;
    private static final int DIGGING_DROP_SEED_OFFSET_TICKS = 120;
    private static final int SNIFFING_PROXIMITY_DISTANCE = 10;
    private static final int SNIFFER_BABY_AGE_TICKS = 48000;
    private static final DataWatcherObject<Sniffer.a> DATA_STATE = DataWatcher.defineId(Sniffer.class, DataWatcherRegistry.SNIFFER_STATE);
    private static final DataWatcherObject<Integer> DATA_DROP_SEED_AT_TICK = DataWatcher.defineId(Sniffer.class, DataWatcherRegistry.INT);
    public final AnimationState walkingAnimationState = new AnimationState();
    public final AnimationState panicAnimationState = new AnimationState();
    public final AnimationState feelingHappyAnimationState = new AnimationState();
    public final AnimationState scentingAnimationState = new AnimationState();
    public final AnimationState sniffingAnimationState = new AnimationState();
    public final AnimationState searchingAnimationState = new AnimationState();
    public final AnimationState diggingAnimationState = new AnimationState();
    public final AnimationState risingAnimationState = new AnimationState();

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.10000000149011612D).add(GenericAttributes.MAX_HEALTH, 14.0D);
    }

    public Sniffer(EntityTypes<? extends EntityAnimal> entitytypes, World world) {
        super(entitytypes, world);
        this.entityData.define(Sniffer.DATA_STATE, Sniffer.a.IDLING);
        this.entityData.define(Sniffer.DATA_DROP_SEED_AT_TICK, 0);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathType.WATER, -2.0F);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return this.getDimensions(entitypose).height * 0.6F;
    }

    private boolean isMoving() {
        boolean flag = this.onGround || this.isInWaterOrBubble();

        return flag && this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }

    private boolean isMovingInWater() {
        return this.isMoving() && this.isInWater() && !this.isUnderWater() && this.getDeltaMovement().horizontalDistanceSqr() > 9.999999999999999E-6D;
    }

    private boolean isMovingOnLand() {
        return this.isMoving() && !this.isUnderWater() && !this.isInWater();
    }

    public boolean isPanicking() {
        return this.brain.getMemory(MemoryModuleType.IS_PANICKING).isPresent();
    }

    public boolean canPlayDiggingSound() {
        return this.getState() == Sniffer.a.DIGGING || this.getState() == Sniffer.a.SEARCHING;
    }

    private BlockPosition getHeadPosition() {
        Vec3D vec3d = this.position().add(this.getForward().scale(2.25D));

        return BlockPosition.containing(vec3d.x(), this.getY(), vec3d.z());
    }

    private Sniffer.a getState() {
        return (Sniffer.a) this.entityData.get(Sniffer.DATA_STATE);
    }

    private Sniffer setState(Sniffer.a sniffer_a) {
        this.entityData.set(Sniffer.DATA_STATE, sniffer_a);
        return this;
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (Sniffer.DATA_STATE.equals(datawatcherobject)) {
            Sniffer.a sniffer_a = this.getState();

            this.resetAnimations();
            switch (sniffer_a) {
                case SCENTING:
                    this.scentingAnimationState.startIfStopped(this.tickCount);
                    break;
                case SNIFFING:
                    this.sniffingAnimationState.startIfStopped(this.tickCount);
                    break;
                case SEARCHING:
                    this.searchingAnimationState.startIfStopped(this.tickCount);
                    break;
                case DIGGING:
                    this.diggingAnimationState.startIfStopped(this.tickCount);
                    break;
                case RISING:
                    this.risingAnimationState.startIfStopped(this.tickCount);
                    break;
                case FEELING_HAPPY:
                    this.feelingHappyAnimationState.startIfStopped(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    private void resetAnimations() {
        this.searchingAnimationState.stop();
        this.diggingAnimationState.stop();
        this.sniffingAnimationState.stop();
        this.risingAnimationState.stop();
        this.feelingHappyAnimationState.stop();
        this.scentingAnimationState.stop();
    }

    public Sniffer transitionTo(Sniffer.a sniffer_a) {
        switch (sniffer_a) {
            case SCENTING:
                this.playSound(SoundEffects.SNIFFER_SCENTING, 1.0F, 1.0F);
                this.setState(Sniffer.a.SCENTING);
                break;
            case SNIFFING:
                this.playSound(SoundEffects.SNIFFER_SNIFFING, 1.0F, 1.0F);
                this.setState(Sniffer.a.SNIFFING);
                break;
            case SEARCHING:
                this.setState(Sniffer.a.SEARCHING);
                break;
            case DIGGING:
                this.setState(Sniffer.a.DIGGING).onDiggingStart();
                break;
            case RISING:
                this.playSound(SoundEffects.SNIFFER_DIGGING_STOP, 1.0F, 1.0F);
                this.setState(Sniffer.a.RISING);
                break;
            case FEELING_HAPPY:
                this.playSound(SoundEffects.SNIFFER_HAPPY, 1.0F, 1.0F);
                this.setState(Sniffer.a.FEELING_HAPPY);
                break;
            case IDLING:
                this.setState(Sniffer.a.IDLING);
        }

        return this;
    }

    private Sniffer onDiggingStart() {
        this.entityData.set(Sniffer.DATA_DROP_SEED_AT_TICK, this.tickCount + 120);
        this.level.broadcastEntityEvent(this, (byte) 63);
        return this;
    }

    public Sniffer onDiggingComplete(boolean flag) {
        if (flag) {
            this.storeExploredPosition(this.getOnPos());
        }

        return this;
    }

    Optional<BlockPosition> calculateDigPosition() {
        return IntStream.range(0, 5).mapToObj((i) -> {
            return LandRandomPos.getPos(this, 10 + 2 * i, 3);
        }).filter(Objects::nonNull).map(BlockPosition::containing).map(BlockPosition::below).filter(this::canDig).findFirst();
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    boolean canDig() {
        return !this.isPanicking() && !this.isBaby() && !this.isInWater() && this.canDig(this.getHeadPosition().below());
    }

    private boolean canDig(BlockPosition blockposition) {
        boolean flag;

        if (this.level.getBlockState(blockposition).is(TagsBlock.SNIFFER_DIGGABLE_BLOCK) && this.level.getBlockState(blockposition.above()).isAir()) {
            Stream stream = this.getExploredPositions();

            Objects.requireNonNull(blockposition);
            if (stream.noneMatch(blockposition::equals)) {
                flag = true;
                return flag;
            }
        }

        flag = false;
        return flag;
    }

    private void dropSeed() {
        if (!this.level.isClientSide() && (Integer) this.entityData.get(Sniffer.DATA_DROP_SEED_AT_TICK) == this.tickCount) {
            ItemStack itemstack = new ItemStack(Items.TORCHFLOWER_SEEDS);
            BlockPosition blockposition = this.getHeadPosition();
            EntityItem entityitem = new EntityItem(this.level, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), itemstack);

            entityitem.setDefaultPickUpDelay();
            this.level.addFreshEntity(entityitem);
            this.playSound(SoundEffects.SNIFFER_DROP_SEED, 1.0F, 1.0F);
        }
    }

    private Sniffer emitDiggingParticles(AnimationState animationstate) {
        boolean flag = animationstate.getAccumulatedTime() > 1700L && animationstate.getAccumulatedTime() < 6000L;

        if (flag) {
            IBlockData iblockdata = this.getBlockStateOn();
            BlockPosition blockposition = this.getHeadPosition();

            if (iblockdata.getRenderShape() != EnumRenderType.INVISIBLE) {
                for (int i = 0; i < 30; ++i) {
                    Vec3D vec3d = Vec3D.atCenterOf(blockposition);

                    this.level.addParticle(new ParticleParamBlock(Particles.BLOCK, iblockdata), vec3d.x, vec3d.y, vec3d.z, 0.0D, 0.0D, 0.0D);
                }

                if (this.tickCount % 10 == 0) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), iblockdata.getSoundType().getHitSound(), this.getSoundSource(), 0.5F, 0.5F, false);
                }
            }
        }

        return this;
    }

    private Sniffer storeExploredPosition(BlockPosition blockposition) {
        List<BlockPosition> list = (List) this.getExploredPositions().limit(20L).collect(Collectors.toList());

        list.add(0, blockposition);
        this.getBrain().setMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, (Object) list);
        return this;
    }

    private Stream<BlockPosition> getExploredPositions() {
        return this.getBrain().getMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
    }

    @Override
    protected void jumpFromGround() {
        super.jumpFromGround();
        double d0 = this.moveControl.getSpeedModifier();

        if (d0 > 0.0D) {
            double d1 = this.getDeltaMovement().horizontalDistanceSqr();

            if (d1 < 0.01D) {
                this.moveRelative(0.1F, new Vec3D(0.0D, 0.0D, 1.0D));
            }
        }

    }

    @Override
    public void tick() {
        boolean flag = this.isInWater() && !this.isUnderWater();

        this.getAttribute(GenericAttributes.MOVEMENT_SPEED).setBaseValue(flag ? 0.20000000298023224D : 0.10000000149011612D);
        if (!this.isMovingOnLand() && !this.isMovingInWater()) {
            this.panicAnimationState.stop();
            this.walkingAnimationState.stop();
        } else if (this.isPanicking()) {
            this.walkingAnimationState.stop();
            this.panicAnimationState.startIfStopped(this.tickCount);
        } else {
            this.panicAnimationState.stop();
            this.walkingAnimationState.startIfStopped(this.tickCount);
        }

        switch (this.getState()) {
            case SEARCHING:
                this.playSearchingSound();
                break;
            case DIGGING:
                this.emitDiggingParticles(this.diggingAnimationState).dropSeed();
        }

        super.tick();
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        EnumInteractionResult enuminteractionresult = super.mobInteract(entityhuman, enumhand);

        if (enuminteractionresult.consumesAction() && this.isFood(itemstack)) {
            this.level.playSound((EntityHuman) null, (Entity) this, this.getEatingSound(itemstack), SoundCategory.NEUTRAL, 1.0F, MathHelper.randomBetween(this.level.random, 0.8F, 1.2F));
        }

        return enuminteractionresult;
    }

    private void playSearchingSound() {
        if (this.level.isClientSide() && this.tickCount % 20 == 0) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEffects.SNIFFER_SEARCHING, this.getSoundSource(), 1.0F, 1.0F, false);
        }

    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.SNIFFER_STEP, 0.15F, 1.0F);
    }

    @Override
    public SoundEffect getEatingSound(ItemStack itemstack) {
        return SoundEffects.SNIFFER_EAT;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return Set.of(Sniffer.a.DIGGING, Sniffer.a.SEARCHING).contains(this.getState()) ? null : SoundEffects.SNIFFER_IDLE;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.SNIFFER_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.SNIFFER_DEATH;
    }

    @Override
    public int getMaxHeadYRot() {
        return 50;
    }

    @Override
    public void setBaby(boolean flag) {
        this.setAge(flag ? -48000 : 0);
    }

    @Override
    public EntityAgeable getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.SNIFFER.create(worldserver);
    }

    @Override
    public boolean canMate(EntityAnimal entityanimal) {
        if (!(entityanimal instanceof Sniffer)) {
            return false;
        } else {
            Sniffer sniffer = (Sniffer) entityanimal;
            Set<Sniffer.a> set = Set.of(Sniffer.a.IDLING, Sniffer.a.SCENTING, Sniffer.a.FEELING_HAPPY);

            return set.contains(this.getState()) && set.contains(sniffer.getState()) && super.canMate(entityanimal);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(0.6000000238418579D);
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return itemstack.is(TagsItem.SNIFFER_FOOD);
    }

    @Override
    protected BehaviorController<?> makeBrain(Dynamic<?> dynamic) {
        return SnifferAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public BehaviorController<Sniffer> getBrain() {
        return super.getBrain();
    }

    @Override
    protected BehaviorController.b<Sniffer> brainProvider() {
        return BehaviorController.provider(SnifferAi.MEMORY_TYPES, SnifferAi.SENSOR_TYPES);
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("snifferBrain");
        this.getBrain().tick((WorldServer) this.level, this);
        this.level.getProfiler().popPush("snifferActivityUpdate");
        SnifferAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        PacketDebug.sendEntityBrain(this);
    }

    public static enum a {

        IDLING, FEELING_HAPPY, SCENTING, SNIFFING, SEARCHING, DIGGING, RISING;

        private a() {}
    }
}
