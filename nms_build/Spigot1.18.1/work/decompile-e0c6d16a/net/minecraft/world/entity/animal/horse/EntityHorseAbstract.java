package net.minecraft.world.entity.animal.horse;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.players.NameReferencingFileConverter;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.IInventory;
import net.minecraft.world.IInventoryListener;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IJumpable;
import net.minecraft.world.entity.ISaddleable;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTame;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.DismountUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundEffectType;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityHorseAbstract extends EntityAnimal implements IInventoryListener, IJumpable, ISaddleable {

    public static final int EQUIPMENT_SLOT_OFFSET = 400;
    public static final int CHEST_SLOT_OFFSET = 499;
    public static final int INVENTORY_SLOT_OFFSET = 500;
    private static final Predicate<EntityLiving> PARENT_HORSE_SELECTOR = (entityliving) -> {
        return entityliving instanceof EntityHorseAbstract && ((EntityHorseAbstract) entityliving).isBred();
    };
    private static final PathfinderTargetCondition MOMMY_TARGETING = PathfinderTargetCondition.forNonCombat().range(16.0D).ignoreLineOfSight().selector(EntityHorseAbstract.PARENT_HORSE_SELECTOR);
    private static final RecipeItemStack FOOD_ITEMS = RecipeItemStack.of(Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.asItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
    private static final DataWatcherObject<Byte> DATA_ID_FLAGS = DataWatcher.defineId(EntityHorseAbstract.class, DataWatcherRegistry.BYTE);
    private static final DataWatcherObject<Optional<UUID>> DATA_ID_OWNER_UUID = DataWatcher.defineId(EntityHorseAbstract.class, DataWatcherRegistry.OPTIONAL_UUID);
    private static final int FLAG_TAME = 2;
    private static final int FLAG_SADDLE = 4;
    private static final int FLAG_BRED = 8;
    private static final int FLAG_EATING = 16;
    private static final int FLAG_STANDING = 32;
    private static final int FLAG_OPEN_MOUTH = 64;
    public static final int INV_SLOT_SADDLE = 0;
    public static final int INV_SLOT_ARMOR = 1;
    public static final int INV_BASE_COUNT = 2;
    private int eatingCounter;
    private int mouthCounter;
    private int standCounter;
    public int tailCounter;
    public int sprintCounter;
    protected boolean isJumping;
    public InventorySubcontainer inventory;
    protected int temper;
    protected float playerJumpPendingScale;
    private boolean allowStandSliding;
    private float eatAnim;
    private float eatAnimO;
    private float standAnim;
    private float standAnimO;
    private float mouthAnim;
    private float mouthAnimO;
    protected boolean canGallop = true;
    protected int gallopSoundCounter;

    protected EntityHorseAbstract(EntityTypes<? extends EntityHorseAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.maxUpStep = 1.0F;
        this.createInventory();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PathfinderGoalPanic(this, 1.2D));
        this.goalSelector.addGoal(1, new PathfinderGoalTame(this, 1.2D));
        this.goalSelector.addGoal(2, new PathfinderGoalBreed(this, 1.0D, EntityHorseAbstract.class));
        this.goalSelector.addGoal(4, new PathfinderGoalFollowParent(this, 1.0D));
        this.goalSelector.addGoal(6, new PathfinderGoalRandomStrollLand(this, 0.7D));
        this.goalSelector.addGoal(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.addGoal(8, new PathfinderGoalRandomLookaround(this));
        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(0, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(3, new PathfinderGoalTempt(this, 1.25D, RecipeItemStack.of(Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE), false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityHorseAbstract.DATA_ID_FLAGS, (byte) 0);
        this.entityData.define(EntityHorseAbstract.DATA_ID_OWNER_UUID, Optional.empty());
    }

    protected boolean getFlag(int i) {
        return ((Byte) this.entityData.get(EntityHorseAbstract.DATA_ID_FLAGS) & i) != 0;
    }

    protected void setFlag(int i, boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityHorseAbstract.DATA_ID_FLAGS);

        if (flag) {
            this.entityData.set(EntityHorseAbstract.DATA_ID_FLAGS, (byte) (b0 | i));
        } else {
            this.entityData.set(EntityHorseAbstract.DATA_ID_FLAGS, (byte) (b0 & ~i));
        }

    }

    public boolean isTamed() {
        return this.getFlag(2);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.entityData.get(EntityHorseAbstract.DATA_ID_OWNER_UUID)).orElse((Object) null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(EntityHorseAbstract.DATA_ID_OWNER_UUID, Optional.ofNullable(uuid));
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void setTamed(boolean flag) {
        this.setFlag(2, flag);
    }

    public void setIsJumping(boolean flag) {
        this.isJumping = flag;
    }

    @Override
    protected void onLeashDistance(float f) {
        if (f > 6.0F && this.isEating()) {
            this.setEating(false);
        }

    }

    public boolean isEating() {
        return this.getFlag(16);
    }

    public boolean isStanding() {
        return this.getFlag(32);
    }

    public boolean isBred() {
        return this.getFlag(8);
    }

    public void setBred(boolean flag) {
        this.setFlag(8, flag);
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby() && this.isTamed();
    }

    @Override
    public void equipSaddle(@Nullable SoundCategory soundcategory) {
        this.inventory.setItem(0, new ItemStack(Items.SADDLE));
        if (soundcategory != null) {
            this.level.playSound((EntityHuman) null, (Entity) this, SoundEffects.HORSE_SADDLE, soundcategory, 0.5F, 1.0F);
        }

    }

    @Override
    public boolean isSaddled() {
        return this.getFlag(4);
    }

    public int getTemper() {
        return this.temper;
    }

    public void setTemper(int i) {
        this.temper = i;
    }

    public int modifyTemper(int i) {
        int j = MathHelper.clamp(this.getTemper() + i, (int) 0, this.getMaxTemper());

        this.setTemper(j);
        return j;
    }

    @Override
    public boolean isPushable() {
        return !this.isVehicle();
    }

    private void eating() {
        this.openMouth();
        if (!this.isSilent()) {
            SoundEffect soundeffect = this.getEatingSound();

            if (soundeffect != null) {
                this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), soundeffect, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }
        }

    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        if (f > 1.0F) {
            this.playSound(SoundEffects.HORSE_LAND, 0.4F, 1.0F);
        }

        int i = this.calculateFallDamage(f, f1);

        if (i <= 0) {
            return false;
        } else {
            this.hurt(damagesource, (float) i);
            if (this.isVehicle()) {
                Iterator iterator = this.getIndirectPassengers().iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    entity.hurt(damagesource, (float) i);
                }
            }

            this.playBlockFallSound();
            return true;
        }
    }

    @Override
    protected int calculateFallDamage(float f, float f1) {
        return MathHelper.ceil((f * 0.5F - 3.0F) * f1);
    }

    protected int getInventorySize() {
        return 2;
    }

    public void createInventory() {
        InventorySubcontainer inventorysubcontainer = this.inventory;

        this.inventory = new InventorySubcontainer(this.getInventorySize());
        if (inventorysubcontainer != null) {
            inventorysubcontainer.removeListener(this);
            int i = Math.min(inventorysubcontainer.getContainerSize(), this.inventory.getContainerSize());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = inventorysubcontainer.getItem(j);

                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.copy());
                }
            }
        }

        this.inventory.addListener(this);
        this.updateContainerEquipment();
    }

    protected void updateContainerEquipment() {
        if (!this.level.isClientSide) {
            this.setFlag(4, !this.inventory.getItem(0).isEmpty());
        }
    }

    @Override
    public void containerChanged(IInventory iinventory) {
        boolean flag = this.isSaddled();

        this.updateContainerEquipment();
        if (this.tickCount > 20 && !flag && this.isSaddled()) {
            this.playSound(SoundEffects.HORSE_SADDLE, 0.5F, 1.0F);
        }

    }

    public double getCustomJump() {
        return this.getAttributeValue(GenericAttributes.JUMP_STRENGTH);
    }

    @Nullable
    protected SoundEffect getEatingSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getDeathSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        if (this.random.nextInt(3) == 0) {
            this.stand();
        }

        return null;
    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        if (this.random.nextInt(10) == 0 && !this.isImmobile()) {
            this.stand();
        }

        return null;
    }

    @Nullable
    protected SoundEffect getAngrySound() {
        this.stand();
        return null;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        if (!iblockdata.getMaterial().isLiquid()) {
            IBlockData iblockdata1 = this.level.getBlockState(blockposition.above());
            SoundEffectType soundeffecttype = iblockdata.getSoundType();

            if (iblockdata1.is(Blocks.SNOW)) {
                soundeffecttype = iblockdata1.getSoundType();
            }

            if (this.isVehicle() && this.canGallop) {
                ++this.gallopSoundCounter;
                if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
                    this.playGallopSound(soundeffecttype);
                } else if (this.gallopSoundCounter <= 5) {
                    this.playSound(SoundEffects.HORSE_STEP_WOOD, soundeffecttype.getVolume() * 0.15F, soundeffecttype.getPitch());
                }
            } else if (soundeffecttype == SoundEffectType.WOOD) {
                this.playSound(SoundEffects.HORSE_STEP_WOOD, soundeffecttype.getVolume() * 0.15F, soundeffecttype.getPitch());
            } else {
                this.playSound(SoundEffects.HORSE_STEP, soundeffecttype.getVolume() * 0.15F, soundeffecttype.getPitch());
            }

        }
    }

    protected void playGallopSound(SoundEffectType soundeffecttype) {
        this.playSound(SoundEffects.HORSE_GALLOP, soundeffecttype.getVolume() * 0.15F, soundeffecttype.getPitch());
    }

    public static AttributeProvider.Builder createBaseHorseAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.JUMP_STRENGTH).add(GenericAttributes.MAX_HEALTH, 53.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.22499999403953552D);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 6;
    }

    public int getMaxTemper() {
        return 100;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 400;
    }

    public void openInventory(EntityHuman entityhuman) {
        if (!this.level.isClientSide && (!this.isVehicle() || this.hasPassenger((Entity) entityhuman)) && this.isTamed()) {
            entityhuman.openHorseInventory(this, this.inventory);
        }

    }

    public EnumInteractionResult fedFood(EntityHuman entityhuman, ItemStack itemstack) {
        boolean flag = this.handleEating(entityhuman, itemstack);

        if (!entityhuman.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return this.level.isClientSide ? EnumInteractionResult.CONSUME : (flag ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS);
    }

    protected boolean handleEating(EntityHuman entityhuman, ItemStack itemstack) {
        boolean flag = false;
        float f = 0.0F;
        short short0 = 0;
        byte b0 = 0;

        if (itemstack.is(Items.WHEAT)) {
            f = 2.0F;
            short0 = 20;
            b0 = 3;
        } else if (itemstack.is(Items.SUGAR)) {
            f = 1.0F;
            short0 = 30;
            b0 = 3;
        } else if (itemstack.is(Blocks.HAY_BLOCK.asItem())) {
            f = 20.0F;
            short0 = 180;
        } else if (itemstack.is(Items.APPLE)) {
            f = 3.0F;
            short0 = 60;
            b0 = 3;
        } else if (itemstack.is(Items.GOLDEN_CARROT)) {
            f = 4.0F;
            short0 = 60;
            b0 = 5;
            if (!this.level.isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                flag = true;
                this.setInLove(entityhuman);
            }
        } else if (itemstack.is(Items.GOLDEN_APPLE) || itemstack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            f = 10.0F;
            short0 = 240;
            b0 = 10;
            if (!this.level.isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                flag = true;
                this.setInLove(entityhuman);
            }
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f);
            flag = true;
        }

        if (this.isBaby() && short0 > 0) {
            this.level.addParticle(Particles.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
            if (!this.level.isClientSide) {
                this.ageUp(short0);
            }

            flag = true;
        }

        if (b0 > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
            flag = true;
            if (!this.level.isClientSide) {
                this.modifyTemper(b0);
            }
        }

        if (flag) {
            this.eating();
            this.gameEvent(GameEvent.EAT, this.eyeBlockPosition());
        }

        return flag;
    }

    protected void doPlayerRide(EntityHuman entityhuman) {
        this.setEating(false);
        this.setStanding(false);
        if (!this.level.isClientSide) {
            entityhuman.setYRot(this.getYRot());
            entityhuman.setXRot(this.getXRot());
            entityhuman.startRiding(this);
        }

    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() && this.isVehicle() && this.isSaddled() || this.isEating() || this.isStanding();
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return EntityHorseAbstract.FOOD_ITEMS.test(itemstack);
    }

    private void moveTail() {
        this.tailCounter = 1;
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.inventory != null) {
            for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);

                if (!itemstack.isEmpty() && !EnchantmentManager.hasVanishingCurse(itemstack)) {
                    this.spawnAtLocation(itemstack);
                }
            }

        }
    }

    @Override
    public void aiStep() {
        if (this.random.nextInt(200) == 0) {
            this.moveTail();
        }

        super.aiStep();
        if (!this.level.isClientSide && this.isAlive()) {
            if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
                this.heal(1.0F);
            }

            if (this.canEatGrass()) {
                if (!this.isEating() && !this.isVehicle() && this.random.nextInt(300) == 0 && this.level.getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
                    this.setEating(true);
                }

                if (this.isEating() && ++this.eatingCounter > 50) {
                    this.eatingCounter = 0;
                    this.setEating(false);
                }
            }

            this.followMommy();
        }
    }

    protected void followMommy() {
        if (this.isBred() && this.isBaby() && !this.isEating()) {
            EntityLiving entityliving = this.level.getNearestEntity(EntityHorseAbstract.class, EntityHorseAbstract.MOMMY_TARGETING, this, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().inflate(16.0D));

            if (entityliving != null && this.distanceToSqr((Entity) entityliving) > 4.0D) {
                this.navigation.createPath((Entity) entityliving, 0);
            }
        }

    }

    public boolean canEatGrass() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.mouthCounter > 0 && ++this.mouthCounter > 30) {
            this.mouthCounter = 0;
            this.setFlag(64, false);
        }

        if ((this.isControlledByLocalInstance() || this.isEffectiveAi()) && this.standCounter > 0 && ++this.standCounter > 20) {
            this.standCounter = 0;
            this.setStanding(false);
        }

        if (this.tailCounter > 0 && ++this.tailCounter > 8) {
            this.tailCounter = 0;
        }

        if (this.sprintCounter > 0) {
            ++this.sprintCounter;
            if (this.sprintCounter > 300) {
                this.sprintCounter = 0;
            }
        }

        this.eatAnimO = this.eatAnim;
        if (this.isEating()) {
            this.eatAnim += (1.0F - this.eatAnim) * 0.4F + 0.05F;
            if (this.eatAnim > 1.0F) {
                this.eatAnim = 1.0F;
            }
        } else {
            this.eatAnim += (0.0F - this.eatAnim) * 0.4F - 0.05F;
            if (this.eatAnim < 0.0F) {
                this.eatAnim = 0.0F;
            }
        }

        this.standAnimO = this.standAnim;
        if (this.isStanding()) {
            this.eatAnim = 0.0F;
            this.eatAnimO = this.eatAnim;
            this.standAnim += (1.0F - this.standAnim) * 0.4F + 0.05F;
            if (this.standAnim > 1.0F) {
                this.standAnim = 1.0F;
            }
        } else {
            this.allowStandSliding = false;
            this.standAnim += (0.8F * this.standAnim * this.standAnim * this.standAnim - this.standAnim) * 0.6F - 0.05F;
            if (this.standAnim < 0.0F) {
                this.standAnim = 0.0F;
            }
        }

        this.mouthAnimO = this.mouthAnim;
        if (this.getFlag(64)) {
            this.mouthAnim += (1.0F - this.mouthAnim) * 0.7F + 0.05F;
            if (this.mouthAnim > 1.0F) {
                this.mouthAnim = 1.0F;
            }
        } else {
            this.mouthAnim += (0.0F - this.mouthAnim) * 0.7F - 0.05F;
            if (this.mouthAnim < 0.0F) {
                this.mouthAnim = 0.0F;
            }
        }

    }

    private void openMouth() {
        if (!this.level.isClientSide) {
            this.mouthCounter = 1;
            this.setFlag(64, true);
        }

    }

    public void setEating(boolean flag) {
        this.setFlag(16, flag);
    }

    public void setStanding(boolean flag) {
        if (flag) {
            this.setEating(false);
        }

        this.setFlag(32, flag);
    }

    private void stand() {
        if (this.isControlledByLocalInstance() || this.isEffectiveAi()) {
            this.standCounter = 1;
            this.setStanding(true);
        }

    }

    public void makeMad() {
        if (!this.isStanding()) {
            this.stand();
            SoundEffect soundeffect = this.getAngrySound();

            if (soundeffect != null) {
                this.playSound(soundeffect, this.getSoundVolume(), this.getVoicePitch());
            }
        }

    }

    public boolean tameWithName(EntityHuman entityhuman) {
        this.setOwnerUUID(entityhuman.getUUID());
        this.setTamed(true);
        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.TAME_ANIMAL.trigger((EntityPlayer) entityhuman, (EntityAnimal) this);
        }

        this.level.broadcastEntityEvent(this, (byte) 7);
        return true;
    }

    @Override
    public void travel(Vec3D vec3d) {
        if (this.isAlive()) {
            if (this.isVehicle() && this.canBeControlledByRider() && this.isSaddled()) {
                EntityLiving entityliving = (EntityLiving) this.getControllingPassenger();

                this.setYRot(entityliving.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(entityliving.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float f = entityliving.xxa * 0.5F;
                float f1 = entityliving.zza;

                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                    this.gallopSoundCounter = 0;
                }

                if (this.onGround && this.playerJumpPendingScale == 0.0F && this.isStanding() && !this.allowStandSliding) {
                    f = 0.0F;
                    f1 = 0.0F;
                }

                if (this.playerJumpPendingScale > 0.0F && !this.isJumping() && this.onGround) {
                    double d0 = this.getCustomJump() * (double) this.playerJumpPendingScale * (double) this.getBlockJumpFactor();
                    double d1 = d0 + this.getJumpBoostPower();
                    Vec3D vec3d1 = this.getDeltaMovement();

                    this.setDeltaMovement(vec3d1.x, d1, vec3d1.z);
                    this.setIsJumping(true);
                    this.hasImpulse = true;
                    if (f1 > 0.0F) {
                        float f2 = MathHelper.sin(this.getYRot() * 0.017453292F);
                        float f3 = MathHelper.cos(this.getYRot() * 0.017453292F);

                        this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f2 * this.playerJumpPendingScale), 0.0D, (double) (0.4F * f3 * this.playerJumpPendingScale)));
                    }

                    this.playerJumpPendingScale = 0.0F;
                }

                this.flyingSpeed = this.getSpeed() * 0.1F;
                if (this.isControlledByLocalInstance()) {
                    this.setSpeed((float) this.getAttributeValue(GenericAttributes.MOVEMENT_SPEED));
                    super.travel(new Vec3D((double) f, vec3d.y, (double) f1));
                } else if (entityliving instanceof EntityHuman) {
                    this.setDeltaMovement(Vec3D.ZERO);
                }

                if (this.onGround) {
                    this.playerJumpPendingScale = 0.0F;
                    this.setIsJumping(false);
                }

                this.calculateEntityAnimation(this, false);
                this.tryCheckInsideBlocks();
            } else {
                this.flyingSpeed = 0.02F;
                super.travel(vec3d);
            }
        }
    }

    protected void playJumpSound() {
        this.playSound(SoundEffects.HORSE_JUMP, 0.4F, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putBoolean("EatingHaystack", this.isEating());
        nbttagcompound.putBoolean("Bred", this.isBred());
        nbttagcompound.putInt("Temper", this.getTemper());
        nbttagcompound.putBoolean("Tame", this.isTamed());
        if (this.getOwnerUUID() != null) {
            nbttagcompound.putUUID("Owner", this.getOwnerUUID());
        }

        if (!this.inventory.getItem(0).isEmpty()) {
            nbttagcompound.put("SaddleItem", this.inventory.getItem(0).save(new NBTTagCompound()));
        }

    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setEating(nbttagcompound.getBoolean("EatingHaystack"));
        this.setBred(nbttagcompound.getBoolean("Bred"));
        this.setTemper(nbttagcompound.getInt("Temper"));
        this.setTamed(nbttagcompound.getBoolean("Tame"));
        UUID uuid;

        if (nbttagcompound.hasUUID("Owner")) {
            uuid = nbttagcompound.getUUID("Owner");
        } else {
            String s = nbttagcompound.getString("Owner");

            uuid = NameReferencingFileConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            this.setOwnerUUID(uuid);
        }

        if (nbttagcompound.contains("SaddleItem", 10)) {
            ItemStack itemstack = ItemStack.of(nbttagcompound.getCompound("SaddleItem"));

            if (itemstack.is(Items.SADDLE)) {
                this.inventory.setItem(0, itemstack);
            }
        }

        this.updateContainerEquipment();
    }

    @Override
    public boolean canMate(EntityAnimal entityanimal) {
        return false;
    }

    protected boolean canParent() {
        return !this.isVehicle() && !this.isPassenger() && this.isTamed() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    @Nullable
    @Override
    public EntityAgeable getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        return null;
    }

    protected void setOffspringAttributes(EntityAgeable entityageable, EntityHorseAbstract entityhorseabstract) {
        double d0 = this.getAttributeBaseValue(GenericAttributes.MAX_HEALTH) + entityageable.getAttributeBaseValue(GenericAttributes.MAX_HEALTH) + (double) this.generateRandomMaxHealth();

        entityhorseabstract.getAttribute(GenericAttributes.MAX_HEALTH).setBaseValue(d0 / 3.0D);
        double d1 = this.getAttributeBaseValue(GenericAttributes.JUMP_STRENGTH) + entityageable.getAttributeBaseValue(GenericAttributes.JUMP_STRENGTH) + this.generateRandomJumpStrength();

        entityhorseabstract.getAttribute(GenericAttributes.JUMP_STRENGTH).setBaseValue(d1 / 3.0D);
        double d2 = this.getAttributeBaseValue(GenericAttributes.MOVEMENT_SPEED) + entityageable.getAttributeBaseValue(GenericAttributes.MOVEMENT_SPEED) + this.generateRandomSpeed();

        entityhorseabstract.getAttribute(GenericAttributes.MOVEMENT_SPEED).setBaseValue(d2 / 3.0D);
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof EntityLiving;
    }

    public float getEatAnim(float f) {
        return MathHelper.lerp(f, this.eatAnimO, this.eatAnim);
    }

    public float getStandAnim(float f) {
        return MathHelper.lerp(f, this.standAnimO, this.standAnim);
    }

    public float getMouthAnim(float f) {
        return MathHelper.lerp(f, this.mouthAnimO, this.mouthAnim);
    }

    @Override
    public void onPlayerJump(int i) {
        if (this.isSaddled()) {
            if (i < 0) {
                i = 0;
            } else {
                this.allowStandSliding = true;
                this.stand();
            }

            if (i >= 90) {
                this.playerJumpPendingScale = 1.0F;
            } else {
                this.playerJumpPendingScale = 0.4F + 0.4F * (float) i / 90.0F;
            }

        }
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    @Override
    public void handleStartJump(int i) {
        this.allowStandSliding = true;
        this.stand();
        this.playJumpSound();
    }

    @Override
    public void handleStopJump() {}

    protected void spawnTamingParticles(boolean flag) {
        ParticleType particletype = flag ? Particles.HEART : Particles.SMOKE;

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.level.addParticle(particletype, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 7) {
            this.spawnTamingParticles(true);
        } else if (b0 == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(b0);
        }

    }

    @Override
    public void positionRider(Entity entity) {
        super.positionRider(entity);
        if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) entity;

            this.yBodyRot = entityinsentient.yBodyRot;
        }

        if (this.standAnimO > 0.0F) {
            float f = MathHelper.sin(this.yBodyRot * 0.017453292F);
            float f1 = MathHelper.cos(this.yBodyRot * 0.017453292F);
            float f2 = 0.7F * this.standAnimO;
            float f3 = 0.15F * this.standAnimO;

            entity.setPos(this.getX() + (double) (f2 * f), this.getY() + this.getPassengersRidingOffset() + entity.getMyRidingOffset() + (double) f3, this.getZ() - (double) (f2 * f1));
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).yBodyRot = this.yBodyRot;
            }
        }

    }

    protected float generateRandomMaxHealth() {
        return 15.0F + (float) this.random.nextInt(8) + (float) this.random.nextInt(9);
    }

    protected double generateRandomJumpStrength() {
        return 0.4000000059604645D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D;
    }

    protected double generateRandomSpeed() {
        return (0.44999998807907104D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D) * 0.25D;
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.95F;
    }

    public boolean canWearArmor() {
        return false;
    }

    public boolean isWearingArmor() {
        return !this.getItemBySlot(EnumItemSlot.CHEST).isEmpty();
    }

    public boolean isArmor(ItemStack itemstack) {
        return false;
    }

    private SlotAccess createEquipmentSlotAccess(final int i, final Predicate<ItemStack> predicate) {
        return new SlotAccess() {
            @Override
            public ItemStack get() {
                return EntityHorseAbstract.this.inventory.getItem(i);
            }

            @Override
            public boolean set(ItemStack itemstack) {
                if (!predicate.test(itemstack)) {
                    return false;
                } else {
                    EntityHorseAbstract.this.inventory.setItem(i, itemstack);
                    EntityHorseAbstract.this.updateContainerEquipment();
                    return true;
                }
            }
        };
    }

    @Override
    public SlotAccess getSlot(int i) {
        int j = i - 400;

        if (j >= 0 && j < 2 && j < this.inventory.getContainerSize()) {
            if (j == 0) {
                return this.createEquipmentSlotAccess(j, (itemstack) -> {
                    return itemstack.isEmpty() || itemstack.is(Items.SADDLE);
                });
            }

            if (j == 1) {
                if (!this.canWearArmor()) {
                    return SlotAccess.NULL;
                }

                return this.createEquipmentSlotAccess(j, (itemstack) -> {
                    return itemstack.isEmpty() || this.isArmor(itemstack);
                });
            }
        }

        int k = i - 500 + 2;

        return k >= 2 && k < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, k) : super.getSlot(i);
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return this.getFirstPassenger();
    }

    @Nullable
    private Vec3D getDismountLocationInDirection(Vec3D vec3d, EntityLiving entityliving) {
        double d0 = this.getX() + vec3d.x;
        double d1 = this.getBoundingBox().minY;
        double d2 = this.getZ() + vec3d.z;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        UnmodifiableIterator unmodifiableiterator = entityliving.getDismountPoses().iterator();

        while (unmodifiableiterator.hasNext()) {
            EntityPose entitypose = (EntityPose) unmodifiableiterator.next();

            blockposition_mutableblockposition.set(d0, d1, d2);
            double d3 = this.getBoundingBox().maxY + 0.75D;

            while (true) {
                double d4 = this.level.getBlockFloorHeight(blockposition_mutableblockposition);

                if ((double) blockposition_mutableblockposition.getY() + d4 > d3) {
                    break;
                }

                if (DismountUtil.isBlockFloorValid(d4)) {
                    AxisAlignedBB axisalignedbb = entityliving.getLocalBoundsForPose(entitypose);
                    Vec3D vec3d1 = new Vec3D(d0, (double) blockposition_mutableblockposition.getY() + d4, d2);

                    if (DismountUtil.canDismountTo(this.level, entityliving, axisalignedbb.move(vec3d1))) {
                        entityliving.setPose(entitypose);
                        return vec3d1;
                    }
                }

                blockposition_mutableblockposition.move(EnumDirection.UP);
                if ((double) blockposition_mutableblockposition.getY() >= d3) {
                    break;
                }
            }
        }

        return null;
    }

    @Override
    public Vec3D getDismountLocationForPassenger(EntityLiving entityliving) {
        Vec3D vec3d = getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) entityliving.getBbWidth(), this.getYRot() + (entityliving.getMainArm() == EnumMainHand.RIGHT ? 90.0F : -90.0F));
        Vec3D vec3d1 = this.getDismountLocationInDirection(vec3d, entityliving);

        if (vec3d1 != null) {
            return vec3d1;
        } else {
            Vec3D vec3d2 = getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) entityliving.getBbWidth(), this.getYRot() + (entityliving.getMainArm() == EnumMainHand.LEFT ? 90.0F : -90.0F));
            Vec3D vec3d3 = this.getDismountLocationInDirection(vec3d2, entityliving);

            return vec3d3 != null ? vec3d3 : this.position();
        }
    }

    protected void randomizeAttributes() {}

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(0.2F);
        }

        this.randomizeAttributes();
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    public boolean hasInventoryChanged(IInventory iinventory) {
        return this.inventory != iinventory;
    }
}
