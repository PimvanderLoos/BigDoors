package net.minecraft.world.entity.animal.allay;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.entity.ai.control.ControllerMoveFlying;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Allay extends EntityCreature implements InventoryCarrier {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int VIBRATION_EVENT_LISTENER_RANGE = 16;
    private static final BaseBlockPosition ITEM_PICKUP_REACH = new BaseBlockPosition(1, 1, 1);
    private static final int LIFTING_ITEM_ANIMATION_DURATION = 5;
    private static final float DANCING_LOOP_DURATION = 55.0F;
    private static final float SPINNING_ANIMATION_DURATION = 15.0F;
    private static final float PATHFINDING_BOUNDING_BOX_PADDING = 0.5F;
    private static final RecipeItemStack DUPLICATION_ITEM = RecipeItemStack.of(Items.AMETHYST_SHARD);
    private static final int DUPLICATION_COOLDOWN_TICKS = 6000;
    private static final int NUM_OF_DUPLICATION_HEARTS = 3;
    private static final DataWatcherObject<Boolean> DATA_DANCING = DataWatcher.defineId(Allay.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_CAN_DUPLICATE = DataWatcher.defineId(Allay.class, DataWatcherRegistry.BOOLEAN);
    protected static final ImmutableList<SensorType<? extends Sensor<? super Allay>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.PATH, MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.HURT_BY, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.LIKED_PLAYER, MemoryModuleType.LIKED_NOTEBLOCK_POSITION, MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleType.IS_PANICKING, new MemoryModuleType[0]);
    public static final ImmutableList<Float> THROW_SOUND_PITCHES = ImmutableList.of(0.5625F, 0.625F, 0.75F, 0.9375F, 1.0F, 1.0F, 1.125F, 1.25F, 1.5F, 1.875F, 2.0F, 2.25F, new Float[]{2.5F, 3.0F, 3.75F, 4.0F});
    private final DynamicGameEventListener<VibrationListener> dynamicVibrationListener;
    private final VibrationListener.b vibrationListenerConfig;
    private final DynamicGameEventListener<Allay.b> dynamicJukeboxListener;
    private final InventorySubcontainer inventory = new InventorySubcontainer(1);
    @Nullable
    private BlockPosition jukeboxPos;
    private long duplicationCooldown;
    private float holdingItemAnimationTicks;
    private float holdingItemAnimationTicks0;
    private float dancingAnimationTicks;
    private float spinningAnimationTicks;
    private float spinningAnimationTicks0;

    public Allay(EntityTypes<? extends Allay> entitytypes, World world) {
        super(entitytypes, world);
        this.moveControl = new ControllerMoveFlying(this, 20, true);
        this.setCanPickUpLoot(this.canPickUpLoot());
        EntityPositionSource entitypositionsource = new EntityPositionSource(this, this.getEyeHeight());

        this.vibrationListenerConfig = new Allay.a();
        this.dynamicVibrationListener = new DynamicGameEventListener<>(new VibrationListener(entitypositionsource, 16, this.vibrationListenerConfig, (VibrationListener.a) null, 0.0F, 0));
        this.dynamicJukeboxListener = new DynamicGameEventListener<>(new Allay.b(entitypositionsource, GameEvent.JUKEBOX_PLAY.getNotificationRadius()));
    }

    @Override
    protected BehaviorController.b<Allay> brainProvider() {
        return BehaviorController.provider(Allay.MEMORY_TYPES, Allay.SENSOR_TYPES);
    }

    @Override
    protected BehaviorController<?> makeBrain(Dynamic<?> dynamic) {
        return AllayAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public BehaviorController<Allay> getBrain() {
        return super.getBrain();
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 20.0D).add(GenericAttributes.FLYING_SPEED, 0.10000000149011612D).add(GenericAttributes.MOVEMENT_SPEED, 0.10000000149011612D).add(GenericAttributes.ATTACK_DAMAGE, 2.0D).add(GenericAttributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        NavigationFlying navigationflying = new NavigationFlying(this, world);

        navigationflying.setCanOpenDoors(false);
        navigationflying.setCanFloat(true);
        navigationflying.setCanPassDoors(true);
        return navigationflying;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Allay.DATA_DANCING, false);
        this.entityData.define(Allay.DATA_CAN_DUPLICATE, true);
    }

    @Override
    public void travel(Vec3D vec3d) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, vec3d);
                this.move(EnumMoveType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.800000011920929D));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, vec3d);
                this.move(EnumMoveType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), vec3d);
                this.move(EnumMoveType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9100000262260437D));
            }
        }

        this.calculateEntityAnimation(this, false);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.6F;
    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        Entity entity = damagesource.getEntity();

        if (entity instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman) entity;
            Optional<UUID> optional = this.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);

            if (optional.isPresent() && entityhuman.getUUID().equals(optional.get())) {
                return false;
            }
        }

        return super.hurt(damagesource, f);
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {}

    @Override
    protected void checkFallDamage(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    @Override
    protected SoundEffect getAmbientSound() {
        return this.hasItemInSlot(EnumItemSlot.MAINHAND) ? SoundEffects.ALLAY_AMBIENT_WITH_ITEM : SoundEffects.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.ALLAY_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.ALLAY_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("allayBrain");
        this.getBrain().tick((WorldServer) this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("allayActivityUpdate");
        AllayAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && this.isAlive() && this.tickCount % 10 == 0) {
            this.heal(1.0F);
        }

        if (this.isDancing() && this.shouldStopDancing() && this.tickCount % 20 == 0) {
            this.setDancing(false);
            this.jukeboxPos = null;
        }

        this.updateDuplicationCooldown();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.holdingItemAnimationTicks0 = this.holdingItemAnimationTicks;
            if (this.hasItemInHand()) {
                this.holdingItemAnimationTicks = MathHelper.clamp(this.holdingItemAnimationTicks + 1.0F, 0.0F, 5.0F);
            } else {
                this.holdingItemAnimationTicks = MathHelper.clamp(this.holdingItemAnimationTicks - 1.0F, 0.0F, 5.0F);
            }

            if (this.isDancing()) {
                ++this.dancingAnimationTicks;
                this.spinningAnimationTicks0 = this.spinningAnimationTicks;
                if (this.isSpinning()) {
                    ++this.spinningAnimationTicks;
                } else {
                    --this.spinningAnimationTicks;
                }

                this.spinningAnimationTicks = MathHelper.clamp(this.spinningAnimationTicks, 0.0F, 15.0F);
            } else {
                this.dancingAnimationTicks = 0.0F;
                this.spinningAnimationTicks = 0.0F;
                this.spinningAnimationTicks0 = 0.0F;
            }
        } else {
            ((VibrationListener) this.dynamicVibrationListener.getListener()).tick(this.level);
        }

    }

    @Override
    public boolean canPickUpLoot() {
        return !this.isOnPickupCooldown() && this.hasItemInHand();
    }

    public boolean hasItemInHand() {
        return !this.getItemInHand(EnumHand.MAIN_HAND).isEmpty();
    }

    @Override
    public boolean canTakeItem(ItemStack itemstack) {
        return false;
    }

    private boolean isOnPickupCooldown() {
        return this.getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_PRESENT);
    }

    @Override
    protected EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        ItemStack itemstack1 = this.getItemInHand(EnumHand.MAIN_HAND);

        if (this.isDancing() && this.isDuplicationItem(itemstack) && this.canDuplicate()) {
            this.duplicateAllay();
            this.level.broadcastEntityEvent(this, (byte) 18);
            this.level.playSound(entityhuman, (Entity) this, SoundEffects.AMETHYST_BLOCK_CHIME, SoundCategory.NEUTRAL, 2.0F, 1.0F);
            this.removeInteractionItem(entityhuman, itemstack);
            return EnumInteractionResult.SUCCESS;
        } else if (itemstack1.isEmpty() && !itemstack.isEmpty()) {
            ItemStack itemstack2 = itemstack.copy();

            itemstack2.setCount(1);
            this.setItemInHand(EnumHand.MAIN_HAND, itemstack2);
            this.removeInteractionItem(entityhuman, itemstack);
            this.level.playSound(entityhuman, (Entity) this, SoundEffects.ALLAY_ITEM_GIVEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
            this.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, (Object) entityhuman.getUUID());
            return EnumInteractionResult.SUCCESS;
        } else if (!itemstack1.isEmpty() && enumhand == EnumHand.MAIN_HAND && itemstack.isEmpty()) {
            this.setItemSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
            this.level.playSound(entityhuman, (Entity) this, SoundEffects.ALLAY_ITEM_TAKEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
            this.swing(EnumHand.MAIN_HAND);
            Iterator iterator = this.getInventory().removeAllItems().iterator();

            while (iterator.hasNext()) {
                ItemStack itemstack3 = (ItemStack) iterator.next();

                BehaviorUtil.throwItem(this, itemstack3, this.position());
            }

            this.getBrain().eraseMemory(MemoryModuleType.LIKED_PLAYER);
            entityhuman.addItem(itemstack1);
            return EnumInteractionResult.SUCCESS;
        } else {
            return super.mobInteract(entityhuman, enumhand);
        }
    }

    public void setJukeboxPlaying(BlockPosition blockposition, boolean flag) {
        if (flag) {
            if (!this.isDancing()) {
                this.jukeboxPos = blockposition;
                this.setDancing(true);
            }
        } else if (blockposition.equals(this.jukeboxPos) || this.jukeboxPos == null) {
            this.jukeboxPos = null;
            this.setDancing(false);
        }

    }

    @Override
    public InventorySubcontainer getInventory() {
        return this.inventory;
    }

    @Override
    protected BaseBlockPosition getPickupReach() {
        return Allay.ITEM_PICKUP_REACH;
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemstack) {
        ItemStack itemstack1 = this.getItemInHand(EnumHand.MAIN_HAND);

        return !itemstack1.isEmpty() && itemstack1.sameItemStackIgnoreDurability(itemstack) && this.inventory.canAddItem(itemstack) && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }

    @Override
    protected void pickUpItem(EntityItem entityitem) {
        InventoryCarrier.pickUpItem(this, this, entityitem);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        PacketDebug.sendEntityBrain(this);
    }

    @Override
    public boolean isFlapping() {
        return !this.isOnGround();
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, WorldServer> biconsumer) {
        World world = this.level;

        if (world instanceof WorldServer) {
            WorldServer worldserver = (WorldServer) world;

            biconsumer.accept(this.dynamicVibrationListener, worldserver);
            biconsumer.accept(this.dynamicJukeboxListener, worldserver);
        }

    }

    public boolean isDancing() {
        return (Boolean) this.entityData.get(Allay.DATA_DANCING);
    }

    public void setDancing(boolean flag) {
        if (!this.level.isClientSide) {
            this.entityData.set(Allay.DATA_DANCING, flag);
        }
    }

    private boolean shouldStopDancing() {
        return this.jukeboxPos == null || !this.jukeboxPos.closerToCenterThan(this.position(), (double) GameEvent.JUKEBOX_PLAY.getNotificationRadius()) || !this.level.getBlockState(this.jukeboxPos).is(Blocks.JUKEBOX);
    }

    public float getHoldingItemAnimationProgress(float f) {
        return MathHelper.lerp(f, this.holdingItemAnimationTicks0, this.holdingItemAnimationTicks) / 5.0F;
    }

    public boolean isSpinning() {
        float f = this.dancingAnimationTicks % 55.0F;

        return f < 15.0F;
    }

    public float getSpinningProgress(float f) {
        return MathHelper.lerp(f, this.spinningAnimationTicks0, this.spinningAnimationTicks) / 15.0F;
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
        ItemStack itemstack = this.getItemBySlot(EnumItemSlot.MAINHAND);

        if (!itemstack.isEmpty() && !EnchantmentManager.hasVanishingCurse(itemstack)) {
            this.spawnAtLocation(itemstack);
            this.setItemSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
        }

    }

    @Override
    public boolean removeWhenFarAway(double d0) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.put("Inventory", this.inventory.createTag());
        DataResult dataresult = VibrationListener.codec(this.vibrationListenerConfig).encodeStart(DynamicOpsNBT.INSTANCE, (VibrationListener) this.dynamicVibrationListener.getListener());
        Logger logger = Allay.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
            nbttagcompound.put("listener", nbtbase);
        });
        nbttagcompound.putLong("DuplicationCooldown", this.duplicationCooldown);
        nbttagcompound.putBoolean("CanDuplicate", this.canDuplicate());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.inventory.fromTag(nbttagcompound.getList("Inventory", 10));
        if (nbttagcompound.contains("listener", 10)) {
            DataResult dataresult = VibrationListener.codec(this.vibrationListenerConfig).parse(new Dynamic(DynamicOpsNBT.INSTANCE, nbttagcompound.getCompound("listener")));
            Logger logger = Allay.LOGGER;

            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent((vibrationlistener) -> {
                this.dynamicVibrationListener.updateListener(vibrationlistener, this.level);
            });
        }

        this.duplicationCooldown = (long) nbttagcompound.getInt("DuplicationCooldown");
        this.entityData.set(Allay.DATA_CAN_DUPLICATE, nbttagcompound.getBoolean("CanDuplicate"));
    }

    @Override
    protected boolean shouldStayCloseToLeashHolder() {
        return false;
    }

    @Override
    public Iterable<BlockPosition> iteratePathfindingStartNodeCandidatePositions() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX - 0.5D);
        int j = MathHelper.floor(axisalignedbb.maxX + 0.5D);
        int k = MathHelper.floor(axisalignedbb.minZ - 0.5D);
        int l = MathHelper.floor(axisalignedbb.maxZ + 0.5D);
        int i1 = MathHelper.floor(axisalignedbb.minY - 0.5D);
        int j1 = MathHelper.floor(axisalignedbb.maxY + 0.5D);

        return BlockPosition.betweenClosed(i, i1, k, j, j1, l);
    }

    private void updateDuplicationCooldown() {
        if (this.duplicationCooldown > 0L) {
            --this.duplicationCooldown;
        }

        if (!this.level.isClientSide() && this.duplicationCooldown == 0L && !this.canDuplicate()) {
            this.entityData.set(Allay.DATA_CAN_DUPLICATE, true);
        }

    }

    private boolean isDuplicationItem(ItemStack itemstack) {
        return Allay.DUPLICATION_ITEM.test(itemstack);
    }

    private void duplicateAllay() {
        Allay allay = (Allay) EntityTypes.ALLAY.create(this.level);

        if (allay != null) {
            allay.moveTo(this.position());
            allay.setPersistenceRequired();
            allay.resetDuplicationCooldown();
            this.resetDuplicationCooldown();
            this.level.addFreshEntity(allay);
        }

    }

    private void resetDuplicationCooldown() {
        this.duplicationCooldown = 6000L;
        this.entityData.set(Allay.DATA_CAN_DUPLICATE, false);
    }

    private boolean canDuplicate() {
        return (Boolean) this.entityData.get(Allay.DATA_CAN_DUPLICATE);
    }

    private void removeInteractionItem(EntityHuman entityhuman, ItemStack itemstack) {
        if (!entityhuman.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

    }

    @Override
    public Vec3D getLeashOffset() {
        return new Vec3D(0.0D, (double) this.getEyeHeight() * 0.6D, (double) this.getBbWidth() * 0.1D);
    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 18) {
            for (int i = 0; i < 3; ++i) {
                this.spawnHeartParticle();
            }
        } else {
            super.handleEntityEvent(b0);
        }

    }

    private void spawnHeartParticle() {
        double d0 = this.random.nextGaussian() * 0.02D;
        double d1 = this.random.nextGaussian() * 0.02D;
        double d2 = this.random.nextGaussian() * 0.02D;

        this.level.addParticle(Particles.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
    }

    private class a implements VibrationListener.b {

        a() {}

        @Override
        public boolean shouldListen(WorldServer worldserver, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, GameEvent.a gameevent_a) {
            if (Allay.this.getLevel() == worldserver && !Allay.this.isRemoved() && !Allay.this.isNoAi()) {
                Optional<GlobalPos> optional = Allay.this.getBrain().getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);

                if (optional.isEmpty()) {
                    return true;
                } else {
                    GlobalPos globalpos = (GlobalPos) optional.get();

                    return globalpos.dimension().equals(worldserver.dimension()) && globalpos.pos().equals(blockposition);
                }
            } else {
                return false;
            }
        }

        @Override
        public void onSignalReceive(WorldServer worldserver, GameEventListener gameeventlistener, BlockPosition blockposition, GameEvent gameevent, @Nullable Entity entity, @Nullable Entity entity1, float f) {
            if (gameevent == GameEvent.NOTE_BLOCK_PLAY) {
                AllayAi.hearNoteblock(Allay.this, new BlockPosition(blockposition));
            }

        }

        @Override
        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.ALLAY_CAN_LISTEN;
        }
    }

    private class b implements GameEventListener {

        private final PositionSource listenerSource;
        private final int listenerRadius;

        public b(PositionSource positionsource, int i) {
            this.listenerSource = positionsource;
            this.listenerRadius = i;
        }

        @Override
        public PositionSource getListenerSource() {
            return this.listenerSource;
        }

        @Override
        public int getListenerRadius() {
            return this.listenerRadius;
        }

        @Override
        public boolean handleGameEvent(WorldServer worldserver, GameEvent.b gameevent_b) {
            if (gameevent_b.gameEvent() == GameEvent.JUKEBOX_PLAY) {
                Allay.this.setJukeboxPlaying(new BlockPosition(gameevent_b.source()), true);
                return true;
            } else if (gameevent_b.gameEvent() == GameEvent.JUKEBOX_STOP_PLAY) {
                Allay.this.setJukeboxPlaying(new BlockPosition(gameevent_b.source()), false);
                return true;
            } else {
                return false;
            }
        }
    }
}
