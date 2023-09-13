package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMoveFlying;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowEntity;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowOwner;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPerch;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomFly;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSit;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.BlockLeaves;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityParrot extends EntityPerchable implements EntityBird {

    private static final DataWatcherObject<Integer> DATA_VARIANT_ID = DataWatcher.defineId(EntityParrot.class, DataWatcherRegistry.INT);
    private static final Predicate<EntityInsentient> NOT_PARROT_PREDICATE = new Predicate<EntityInsentient>() {
        public boolean test(@Nullable EntityInsentient entityinsentient) {
            return entityinsentient != null && EntityParrot.MOB_SOUND_MAP.containsKey(entityinsentient.getType());
        }
    };
    private static final Item POISONOUS_FOOD = Items.COOKIE;
    private static final Set<Item> TAME_FOOD = Sets.newHashSet(new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS});
    private static final int VARIANTS = 5;
    static final Map<EntityTypes<?>, SoundEffect> MOB_SOUND_MAP = (Map) SystemUtils.make(Maps.newHashMap(), (hashmap) -> {
        hashmap.put(EntityTypes.BLAZE, SoundEffects.PARROT_IMITATE_BLAZE);
        hashmap.put(EntityTypes.CAVE_SPIDER, SoundEffects.PARROT_IMITATE_SPIDER);
        hashmap.put(EntityTypes.CREEPER, SoundEffects.PARROT_IMITATE_CREEPER);
        hashmap.put(EntityTypes.DROWNED, SoundEffects.PARROT_IMITATE_DROWNED);
        hashmap.put(EntityTypes.ELDER_GUARDIAN, SoundEffects.PARROT_IMITATE_ELDER_GUARDIAN);
        hashmap.put(EntityTypes.ENDER_DRAGON, SoundEffects.PARROT_IMITATE_ENDER_DRAGON);
        hashmap.put(EntityTypes.ENDERMITE, SoundEffects.PARROT_IMITATE_ENDERMITE);
        hashmap.put(EntityTypes.EVOKER, SoundEffects.PARROT_IMITATE_EVOKER);
        hashmap.put(EntityTypes.GHAST, SoundEffects.PARROT_IMITATE_GHAST);
        hashmap.put(EntityTypes.GUARDIAN, SoundEffects.PARROT_IMITATE_GUARDIAN);
        hashmap.put(EntityTypes.HOGLIN, SoundEffects.PARROT_IMITATE_HOGLIN);
        hashmap.put(EntityTypes.HUSK, SoundEffects.PARROT_IMITATE_HUSK);
        hashmap.put(EntityTypes.ILLUSIONER, SoundEffects.PARROT_IMITATE_ILLUSIONER);
        hashmap.put(EntityTypes.MAGMA_CUBE, SoundEffects.PARROT_IMITATE_MAGMA_CUBE);
        hashmap.put(EntityTypes.PHANTOM, SoundEffects.PARROT_IMITATE_PHANTOM);
        hashmap.put(EntityTypes.PIGLIN, SoundEffects.PARROT_IMITATE_PIGLIN);
        hashmap.put(EntityTypes.PIGLIN_BRUTE, SoundEffects.PARROT_IMITATE_PIGLIN_BRUTE);
        hashmap.put(EntityTypes.PILLAGER, SoundEffects.PARROT_IMITATE_PILLAGER);
        hashmap.put(EntityTypes.RAVAGER, SoundEffects.PARROT_IMITATE_RAVAGER);
        hashmap.put(EntityTypes.SHULKER, SoundEffects.PARROT_IMITATE_SHULKER);
        hashmap.put(EntityTypes.SILVERFISH, SoundEffects.PARROT_IMITATE_SILVERFISH);
        hashmap.put(EntityTypes.SKELETON, SoundEffects.PARROT_IMITATE_SKELETON);
        hashmap.put(EntityTypes.SLIME, SoundEffects.PARROT_IMITATE_SLIME);
        hashmap.put(EntityTypes.SPIDER, SoundEffects.PARROT_IMITATE_SPIDER);
        hashmap.put(EntityTypes.STRAY, SoundEffects.PARROT_IMITATE_STRAY);
        hashmap.put(EntityTypes.VEX, SoundEffects.PARROT_IMITATE_VEX);
        hashmap.put(EntityTypes.VINDICATOR, SoundEffects.PARROT_IMITATE_VINDICATOR);
        hashmap.put(EntityTypes.WITCH, SoundEffects.PARROT_IMITATE_WITCH);
        hashmap.put(EntityTypes.WITHER, SoundEffects.PARROT_IMITATE_WITHER);
        hashmap.put(EntityTypes.WITHER_SKELETON, SoundEffects.PARROT_IMITATE_WITHER_SKELETON);
        hashmap.put(EntityTypes.ZOGLIN, SoundEffects.PARROT_IMITATE_ZOGLIN);
        hashmap.put(EntityTypes.ZOMBIE, SoundEffects.PARROT_IMITATE_ZOMBIE);
        hashmap.put(EntityTypes.ZOMBIE_VILLAGER, SoundEffects.PARROT_IMITATE_ZOMBIE_VILLAGER);
    });
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;
    private boolean partyParrot;
    @Nullable
    private BlockPosition jukebox;

    public EntityParrot(EntityTypes<? extends EntityParrot> entitytypes, World world) {
        super(entitytypes, world);
        this.moveControl = new ControllerMoveFlying(this, 10, false);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setVariant(this.random.nextInt(5));
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(false);
        }

        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.addGoal(0, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.addGoal(2, new PathfinderGoalSit(this));
        this.goalSelector.addGoal(2, new PathfinderGoalFollowOwner(this, 1.0D, 5.0F, 1.0F, true));
        this.goalSelector.addGoal(2, new EntityParrot.a(this, 1.0D));
        this.goalSelector.addGoal(3, new PathfinderGoalPerch(this));
        this.goalSelector.addGoal(3, new PathfinderGoalFollowEntity(this, 1.0D, 3.0F, 7.0F));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 6.0D).add(GenericAttributes.FLYING_SPEED, 0.4000000059604645D).add(GenericAttributes.MOVEMENT_SPEED, 0.20000000298023224D);
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
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.6F;
    }

    @Override
    public void aiStep() {
        if (this.jukebox == null || !this.jukebox.closerThan((IPosition) this.position(), 3.46D) || !this.level.getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
            this.partyParrot = false;
            this.jukebox = null;
        }

        if (this.level.random.nextInt(400) == 0) {
            imitateNearbyMobs(this.level, this);
        }

        super.aiStep();
        this.calculateFlapping();
    }

    @Override
    public void setRecordPlayingNearby(BlockPosition blockposition, boolean flag) {
        this.jukebox = blockposition;
        this.partyParrot = flag;
    }

    public boolean isPartyParrot() {
        return this.partyParrot;
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed = (float) ((double) this.flapSpeed + (double) (!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3D);
        this.flapSpeed = MathHelper.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping = (float) ((double) this.flapping * 0.9D);
        Vec3D vec3d = this.getDeltaMovement();

        if (!this.onGround && vec3d.y < 0.0D) {
            this.setDeltaMovement(vec3d.multiply(1.0D, 0.6D, 1.0D));
        }

        this.flap += this.flapping * 2.0F;
    }

    public static boolean imitateNearbyMobs(World world, Entity entity) {
        if (entity.isAlive() && !entity.isSilent() && world.random.nextInt(2) == 0) {
            List<EntityInsentient> list = world.getEntitiesOfClass(EntityInsentient.class, entity.getBoundingBox().inflate(20.0D), EntityParrot.NOT_PARROT_PREDICATE);

            if (!list.isEmpty()) {
                EntityInsentient entityinsentient = (EntityInsentient) list.get(world.random.nextInt(list.size()));

                if (!entityinsentient.isSilent()) {
                    SoundEffect soundeffect = getImitatedSound(entityinsentient.getType());

                    world.playSound((EntityHuman) null, entity.getX(), entity.getY(), entity.getZ(), soundeffect, entity.getSoundSource(), 0.7F, getPitch(world.random));
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (!this.isTame() && EntityParrot.TAME_FOOD.contains(itemstack.getItem())) {
            if (!entityhuman.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            if (!this.isSilent()) {
                this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), SoundEffects.PARROT_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            if (!this.level.isClientSide) {
                if (this.random.nextInt(10) == 0) {
                    this.tame(entityhuman);
                    this.level.broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                }
            }

            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else if (itemstack.is(EntityParrot.POISONOUS_FOOD)) {
            if (!entityhuman.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            this.addEffect(new MobEffect(MobEffects.POISON, 900));
            if (entityhuman.isCreative() || !this.isInvulnerable()) {
                this.hurt(DamageSource.playerAttack(entityhuman), Float.MAX_VALUE);
            }

            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else if (!this.isFlying() && this.isTame() && this.isOwnedBy(entityhuman)) {
            if (!this.level.isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }

            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(entityhuman, enumhand);
        }
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return false;
    }

    public static boolean checkParrotSpawnRules(EntityTypes<EntityParrot> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getBlockState(blockposition.below()).is((Tag) TagsBlock.PARROTS_SPAWNABLE_ON) && isBrightEnoughToSpawn(generatoraccess, blockposition);
    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    @Override
    public boolean canMate(EntityAnimal entityanimal) {
        return false;
    }

    @Nullable
    @Override
    public EntityAgeable getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        return null;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        return entity.hurt(DamageSource.mobAttack(this), 3.0F);
    }

    @Nullable
    @Override
    public SoundEffect getAmbientSound() {
        return getAmbient(this.level, this.level.random);
    }

    public static SoundEffect getAmbient(World world, Random random) {
        if (world.getDifficulty() != EnumDifficulty.PEACEFUL && random.nextInt(1000) == 0) {
            List<EntityTypes<?>> list = Lists.newArrayList(EntityParrot.MOB_SOUND_MAP.keySet());

            return getImitatedSound((EntityTypes) list.get(random.nextInt(list.size())));
        } else {
            return SoundEffects.PARROT_AMBIENT;
        }
    }

    private static SoundEffect getImitatedSound(EntityTypes<?> entitytypes) {
        return (SoundEffect) EntityParrot.MOB_SOUND_MAP.getOrDefault(entitytypes, SoundEffects.PARROT_AMBIENT);
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.PARROT_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.PARROT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.PARROT_STEP, 0.15F, 1.0F);
    }

    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void onFlap() {
        this.playSound(SoundEffects.PARROT_FLY, 0.15F, 1.0F);
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    @Override
    public float getVoicePitch() {
        return getPitch(this.random);
    }

    public static float getPitch(Random random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void doPush(Entity entity) {
        if (!(entity instanceof EntityHuman)) {
            super.doPush(entity);
        }
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else {
            this.setOrderedToSit(false);
            return super.hurt(damagesource, f);
        }
    }

    public int getVariant() {
        return MathHelper.clamp((Integer) this.entityData.get(EntityParrot.DATA_VARIANT_ID), (int) 0, (int) 4);
    }

    public void setVariant(int i) {
        this.entityData.set(EntityParrot.DATA_VARIANT_ID, i);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityParrot.DATA_VARIANT_ID, 0);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
    }

    @Override
    public boolean isFlying() {
        return !this.onGround;
    }

    @Override
    public Vec3D getLeashOffset() {
        return new Vec3D(0.0D, (double) (0.5F * this.getEyeHeight()), (double) (this.getBbWidth() * 0.4F));
    }

    private static class a extends PathfinderGoalRandomFly {

        public a(EntityCreature entitycreature, double d0) {
            super(entitycreature, d0);
        }

        @Nullable
        @Override
        protected Vec3D getPosition() {
            Vec3D vec3d = null;

            if (this.mob.isInWater()) {
                vec3d = LandRandomPos.getPos(this.mob, 15, 15);
            }

            if (this.mob.getRandom().nextFloat() >= this.probability) {
                vec3d = this.getTreePos();
            }

            return vec3d == null ? super.getPosition() : vec3d;
        }

        @Nullable
        private Vec3D getTreePos() {
            BlockPosition blockposition = this.mob.blockPosition();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();
            Iterable<BlockPosition> iterable = BlockPosition.betweenClosed(MathHelper.floor(this.mob.getX() - 3.0D), MathHelper.floor(this.mob.getY() - 6.0D), MathHelper.floor(this.mob.getZ() - 3.0D), MathHelper.floor(this.mob.getX() + 3.0D), MathHelper.floor(this.mob.getY() + 6.0D), MathHelper.floor(this.mob.getZ() + 3.0D));
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                if (!blockposition.equals(blockposition1)) {
                    IBlockData iblockdata = this.mob.level.getBlockState(blockposition_mutableblockposition1.setWithOffset(blockposition1, EnumDirection.DOWN));
                    boolean flag = iblockdata.getBlock() instanceof BlockLeaves || iblockdata.is((Tag) TagsBlock.LOGS);

                    if (flag && this.mob.level.isEmptyBlock(blockposition1) && this.mob.level.isEmptyBlock(blockposition_mutableblockposition.setWithOffset(blockposition1, EnumDirection.UP))) {
                        return Vec3D.atBottomCenterOf(blockposition1);
                    }
                }
            }

            return null;
        }
    }
}
