package net.minecraft.world.entity.animal;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalCatSitOnBed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowOwner;
import net.minecraft.world.entity.ai.goal.PathfinderGoalJumpOnBlock;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLeapAtTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalOcelotAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSit;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalRandomTargetNonTamed;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDye;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.AxisAlignedBB;

public class EntityCat extends EntityTameableAnimal {

    public static final double TEMPT_SPEED_MOD = 0.6D;
    public static final double WALK_SPEED_MOD = 0.8D;
    public static final double SPRINT_SPEED_MOD = 1.33D;
    private static final RecipeItemStack TEMPT_INGREDIENT = RecipeItemStack.of(Items.COD, Items.SALMON);
    private static final DataWatcherObject<Integer> DATA_TYPE_ID = DataWatcher.defineId(EntityCat.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> IS_LYING = DataWatcher.defineId(EntityCat.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> RELAX_STATE_ONE = DataWatcher.defineId(EntityCat.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> DATA_COLLAR_COLOR = DataWatcher.defineId(EntityCat.class, DataWatcherRegistry.INT);
    public static final int TYPE_TABBY = 0;
    public static final int TYPE_BLACK = 1;
    public static final int TYPE_RED = 2;
    public static final int TYPE_SIAMESE = 3;
    public static final int TYPE_BRITISH = 4;
    public static final int TYPE_CALICO = 5;
    public static final int TYPE_PERSIAN = 6;
    public static final int TYPE_RAGDOLL = 7;
    public static final int TYPE_WHITE = 8;
    public static final int TYPE_JELLIE = 9;
    public static final int TYPE_ALL_BLACK = 10;
    private static final int NUMBER_OF_CAT_TYPES = 11;
    private static final int NUMBER_OF_CAT_TYPES_EXCEPT_ALL_BLACK = 10;
    public static final Map<Integer, MinecraftKey> TEXTURE_BY_TYPE = (Map) SystemUtils.make(Maps.newHashMap(), (hashmap) -> {
        hashmap.put(0, new MinecraftKey("textures/entity/cat/tabby.png"));
        hashmap.put(1, new MinecraftKey("textures/entity/cat/black.png"));
        hashmap.put(2, new MinecraftKey("textures/entity/cat/red.png"));
        hashmap.put(3, new MinecraftKey("textures/entity/cat/siamese.png"));
        hashmap.put(4, new MinecraftKey("textures/entity/cat/british_shorthair.png"));
        hashmap.put(5, new MinecraftKey("textures/entity/cat/calico.png"));
        hashmap.put(6, new MinecraftKey("textures/entity/cat/persian.png"));
        hashmap.put(7, new MinecraftKey("textures/entity/cat/ragdoll.png"));
        hashmap.put(8, new MinecraftKey("textures/entity/cat/white.png"));
        hashmap.put(9, new MinecraftKey("textures/entity/cat/jellie.png"));
        hashmap.put(10, new MinecraftKey("textures/entity/cat/all_black.png"));
    });
    private EntityCat.a<EntityHuman> avoidPlayersGoal;
    @Nullable
    private PathfinderGoalTempt temptGoal;
    private float lieDownAmount;
    private float lieDownAmountO;
    private float lieDownAmountTail;
    private float lieDownAmountOTail;
    private float relaxStateOneAmount;
    private float relaxStateOneAmountO;

    public EntityCat(EntityTypes<? extends EntityCat> entitytypes, World world) {
        super(entitytypes, world);
    }

    public MinecraftKey getResourceLocation() {
        return (MinecraftKey) EntityCat.TEXTURE_BY_TYPE.getOrDefault(this.getCatType(), (MinecraftKey) EntityCat.TEXTURE_BY_TYPE.get(0));
    }

    @Override
    protected void registerGoals() {
        this.temptGoal = new EntityCat.PathfinderGoalTemptChance(this, 0.6D, EntityCat.TEMPT_INGREDIENT, true);
        this.goalSelector.addGoal(1, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(1, new PathfinderGoalSit(this));
        this.goalSelector.addGoal(2, new EntityCat.b(this));
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(5, new PathfinderGoalCatSitOnBed(this, 1.1D, 8));
        this.goalSelector.addGoal(6, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 5.0F, false));
        this.goalSelector.addGoal(7, new PathfinderGoalJumpOnBlock(this, 0.8D));
        this.goalSelector.addGoal(8, new PathfinderGoalLeapAtTarget(this, 0.3F));
        this.goalSelector.addGoal(9, new PathfinderGoalOcelotAttack(this));
        this.goalSelector.addGoal(10, new PathfinderGoalBreed(this, 0.8D));
        this.goalSelector.addGoal(11, new PathfinderGoalRandomStrollLand(this, 0.8D, 1.0000001E-5F));
        this.goalSelector.addGoal(12, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
        this.targetSelector.addGoal(1, new PathfinderGoalRandomTargetNonTamed<>(this, EntityRabbit.class, false, (Predicate) null));
        this.targetSelector.addGoal(1, new PathfinderGoalRandomTargetNonTamed<>(this, EntityTurtle.class, false, EntityTurtle.BABY_ON_LAND_SELECTOR));
    }

    public int getCatType() {
        return (Integer) this.entityData.get(EntityCat.DATA_TYPE_ID);
    }

    public void setCatType(int i) {
        if (i < 0 || i >= 11) {
            i = this.random.nextInt(10);
        }

        this.entityData.set(EntityCat.DATA_TYPE_ID, i);
    }

    public void setLying(boolean flag) {
        this.entityData.set(EntityCat.IS_LYING, flag);
    }

    public boolean isLying() {
        return (Boolean) this.entityData.get(EntityCat.IS_LYING);
    }

    public void setRelaxStateOne(boolean flag) {
        this.entityData.set(EntityCat.RELAX_STATE_ONE, flag);
    }

    public boolean isRelaxStateOne() {
        return (Boolean) this.entityData.get(EntityCat.RELAX_STATE_ONE);
    }

    public EnumColor getCollarColor() {
        return EnumColor.byId((Integer) this.entityData.get(EntityCat.DATA_COLLAR_COLOR));
    }

    public void setCollarColor(EnumColor enumcolor) {
        this.entityData.set(EntityCat.DATA_COLLAR_COLOR, enumcolor.getId());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityCat.DATA_TYPE_ID, 1);
        this.entityData.define(EntityCat.IS_LYING, false);
        this.entityData.define(EntityCat.RELAX_STATE_ONE, false);
        this.entityData.define(EntityCat.DATA_COLLAR_COLOR, EnumColor.RED.getId());
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("CatType", this.getCatType());
        nbttagcompound.putByte("CollarColor", (byte) this.getCollarColor().getId());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setCatType(nbttagcompound.getInt("CatType"));
        if (nbttagcompound.contains("CollarColor", 99)) {
            this.setCollarColor(EnumColor.byId(nbttagcompound.getInt("CollarColor")));
        }

    }

    @Override
    public void customServerAiStep() {
        if (this.getMoveControl().hasWanted()) {
            double d0 = this.getMoveControl().getSpeedModifier();

            if (d0 == 0.6D) {
                this.setPose(EntityPose.CROUCHING);
                this.setSprinting(false);
            } else if (d0 == 1.33D) {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(true);
            } else {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(false);
            }
        } else {
            this.setPose(EntityPose.STANDING);
            this.setSprinting(false);
        }

    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        return this.isTame() ? (this.isInLove() ? SoundEffects.CAT_PURR : (this.random.nextInt(4) == 0 ? SoundEffects.CAT_PURREOW : SoundEffects.CAT_AMBIENT)) : SoundEffects.CAT_STRAY_AMBIENT;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    public void hiss() {
        this.playSound(SoundEffects.CAT_HISS, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.CAT_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.CAT_DEATH;
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 10.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).add(GenericAttributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    protected void usePlayerItem(EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack) {
        if (this.isFood(itemstack)) {
            this.playSound(SoundEffects.CAT_EAT, 1.0F, 1.0F);
        }

        super.usePlayerItem(entityhuman, enumhand, itemstack);
    }

    private float getAttackDamage() {
        return (float) this.getAttributeValue(GenericAttributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        return entity.hurt(DamageSource.mobAttack(this), this.getAttackDamage());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.temptGoal != null && this.temptGoal.isRunning() && !this.isTame() && this.tickCount % 100 == 0) {
            this.playSound(SoundEffects.CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
        }

        this.handleLieDown();
    }

    private void handleLieDown() {
        if ((this.isLying() || this.isRelaxStateOne()) && this.tickCount % 5 == 0) {
            this.playSound(SoundEffects.CAT_PURR, 0.6F + 0.4F * (this.random.nextFloat() - this.random.nextFloat()), 1.0F);
        }

        this.updateLieDownAmount();
        this.updateRelaxStateOneAmount();
    }

    private void updateLieDownAmount() {
        this.lieDownAmountO = this.lieDownAmount;
        this.lieDownAmountOTail = this.lieDownAmountTail;
        if (this.isLying()) {
            this.lieDownAmount = Math.min(1.0F, this.lieDownAmount + 0.15F);
            this.lieDownAmountTail = Math.min(1.0F, this.lieDownAmountTail + 0.08F);
        } else {
            this.lieDownAmount = Math.max(0.0F, this.lieDownAmount - 0.22F);
            this.lieDownAmountTail = Math.max(0.0F, this.lieDownAmountTail - 0.13F);
        }

    }

    private void updateRelaxStateOneAmount() {
        this.relaxStateOneAmountO = this.relaxStateOneAmount;
        if (this.isRelaxStateOne()) {
            this.relaxStateOneAmount = Math.min(1.0F, this.relaxStateOneAmount + 0.1F);
        } else {
            this.relaxStateOneAmount = Math.max(0.0F, this.relaxStateOneAmount - 0.13F);
        }

    }

    public float getLieDownAmount(float f) {
        return MathHelper.lerp(f, this.lieDownAmountO, this.lieDownAmount);
    }

    public float getLieDownAmountTail(float f) {
        return MathHelper.lerp(f, this.lieDownAmountOTail, this.lieDownAmountTail);
    }

    public float getRelaxStateOneAmount(float f) {
        return MathHelper.lerp(f, this.relaxStateOneAmountO, this.relaxStateOneAmount);
    }

    @Override
    public EntityCat getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        EntityCat entitycat = (EntityCat) EntityTypes.CAT.create(worldserver);

        if (entityageable instanceof EntityCat) {
            if (this.random.nextBoolean()) {
                entitycat.setCatType(this.getCatType());
            } else {
                entitycat.setCatType(((EntityCat) entityageable).getCatType());
            }

            if (this.isTame()) {
                entitycat.setOwnerUUID(this.getOwnerUUID());
                entitycat.setTame(true);
                if (this.random.nextBoolean()) {
                    entitycat.setCollarColor(this.getCollarColor());
                } else {
                    entitycat.setCollarColor(((EntityCat) entityageable).getCollarColor());
                }
            }
        }

        return entitycat;
    }

    @Override
    public boolean canMate(EntityAnimal entityanimal) {
        if (!this.isTame()) {
            return false;
        } else if (!(entityanimal instanceof EntityCat)) {
            return false;
        } else {
            EntityCat entitycat = (EntityCat) entityanimal;

            return entitycat.isTame() && super.canMate(entityanimal);
        }
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        if (worldaccess.getMoonBrightness() > 0.9F) {
            this.setCatType(this.random.nextInt(11));
        } else {
            this.setCatType(this.random.nextInt(10));
        }

        WorldServer worldserver = worldaccess.getLevel();

        if (worldserver instanceof WorldServer && ((WorldServer) worldserver).structureFeatureManager().getStructureWithPieceAt(this.blockPosition(), StructureGenerator.SWAMP_HUT).isValid()) {
            this.setCatType(10);
            this.setPersistenceRequired();
        }

        return groupdataentity;
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        Item item = itemstack.getItem();

        if (this.level.isClientSide) {
            return this.isTame() && this.isOwnedBy(entityhuman) ? EnumInteractionResult.SUCCESS : (this.isFood(itemstack) && (this.getHealth() < this.getMaxHealth() || !this.isTame()) ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS);
        } else {
            EnumInteractionResult enuminteractionresult;

            if (this.isTame()) {
                if (this.isOwnedBy(entityhuman)) {
                    if (!(item instanceof ItemDye)) {
                        if (item.isEdible() && this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                            this.usePlayerItem(entityhuman, enumhand, itemstack);
                            this.heal((float) item.getFoodProperties().getNutrition());
                            return EnumInteractionResult.CONSUME;
                        }

                        enuminteractionresult = super.mobInteract(entityhuman, enumhand);
                        if (!enuminteractionresult.consumesAction() || this.isBaby()) {
                            this.setOrderedToSit(!this.isOrderedToSit());
                        }

                        return enuminteractionresult;
                    }

                    EnumColor enumcolor = ((ItemDye) item).getDyeColor();

                    if (enumcolor != this.getCollarColor()) {
                        this.setCollarColor(enumcolor);
                        if (!entityhuman.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }

                        this.setPersistenceRequired();
                        return EnumInteractionResult.CONSUME;
                    }
                }
            } else if (this.isFood(itemstack)) {
                this.usePlayerItem(entityhuman, enumhand, itemstack);
                if (this.random.nextInt(3) == 0) {
                    this.tame(entityhuman);
                    this.setOrderedToSit(true);
                    this.level.broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                }

                this.setPersistenceRequired();
                return EnumInteractionResult.CONSUME;
            }

            enuminteractionresult = super.mobInteract(entityhuman, enumhand);
            if (enuminteractionresult.consumesAction()) {
                this.setPersistenceRequired();
            }

            return enuminteractionresult;
        }
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return EntityCat.TEMPT_INGREDIENT.test(itemstack);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.5F;
    }

    @Override
    public boolean removeWhenFarAway(double d0) {
        return !this.isTame() && this.tickCount > 2400;
    }

    @Override
    protected void reassessTameGoals() {
        if (this.avoidPlayersGoal == null) {
            this.avoidPlayersGoal = new EntityCat.a<>(this, EntityHuman.class, 16.0F, 0.8D, 1.33D);
        }

        this.goalSelector.removeGoal(this.avoidPlayersGoal);
        if (!this.isTame()) {
            this.goalSelector.addGoal(4, this.avoidPlayersGoal);
        }

    }

    @Override
    public boolean isSteppingCarefully() {
        return this.getPose() == EntityPose.CROUCHING || super.isSteppingCarefully();
    }

    private static class PathfinderGoalTemptChance extends PathfinderGoalTempt {

        @Nullable
        private EntityHuman selectedPlayer;
        private final EntityCat cat;

        public PathfinderGoalTemptChance(EntityCat entitycat, double d0, RecipeItemStack recipeitemstack, boolean flag) {
            super(entitycat, d0, recipeitemstack, flag);
            this.cat = entitycat;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.selectedPlayer == null && this.mob.getRandom().nextInt(this.adjustedTickDelay(600)) == 0) {
                this.selectedPlayer = this.player;
            } else if (this.mob.getRandom().nextInt(this.adjustedTickDelay(500)) == 0) {
                this.selectedPlayer = null;
            }

        }

        @Override
        protected boolean canScare() {
            return this.selectedPlayer != null && this.selectedPlayer.equals(this.player) ? false : super.canScare();
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.cat.isTame();
        }
    }

    private static class b extends PathfinderGoal {

        private final EntityCat cat;
        @Nullable
        private EntityHuman ownerPlayer;
        @Nullable
        private BlockPosition goalPos;
        private int onBedTicks;

        public b(EntityCat entitycat) {
            this.cat = entitycat;
        }

        @Override
        public boolean canUse() {
            if (!this.cat.isTame()) {
                return false;
            } else if (this.cat.isOrderedToSit()) {
                return false;
            } else {
                EntityLiving entityliving = this.cat.getOwner();

                if (entityliving instanceof EntityHuman) {
                    this.ownerPlayer = (EntityHuman) entityliving;
                    if (!entityliving.isSleeping()) {
                        return false;
                    }

                    if (this.cat.distanceToSqr((Entity) this.ownerPlayer) > 100.0D) {
                        return false;
                    }

                    BlockPosition blockposition = this.ownerPlayer.blockPosition();
                    IBlockData iblockdata = this.cat.level.getBlockState(blockposition);

                    if (iblockdata.is((Tag) TagsBlock.BEDS)) {
                        this.goalPos = (BlockPosition) iblockdata.getOptionalValue(BlockBed.FACING).map((enumdirection) -> {
                            return blockposition.relative(enumdirection.getOpposite());
                        }).orElseGet(() -> {
                            return new BlockPosition(blockposition);
                        });
                        return !this.spaceIsOccupied();
                    }
                }

                return false;
            }
        }

        private boolean spaceIsOccupied() {
            List<EntityCat> list = this.cat.level.getEntitiesOfClass(EntityCat.class, (new AxisAlignedBB(this.goalPos)).inflate(2.0D));
            Iterator iterator = list.iterator();

            EntityCat entitycat;

            do {
                do {
                    if (!iterator.hasNext()) {
                        return false;
                    }

                    entitycat = (EntityCat) iterator.next();
                } while (entitycat == this.cat);
            } while (!entitycat.isLying() && !entitycat.isRelaxStateOne());

            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.cat.isTame() && !this.cat.isOrderedToSit() && this.ownerPlayer != null && this.ownerPlayer.isSleeping() && this.goalPos != null && !this.spaceIsOccupied();
        }

        @Override
        public void start() {
            if (this.goalPos != null) {
                this.cat.setInSittingPose(false);
                this.cat.getNavigation().moveTo((double) this.goalPos.getX(), (double) this.goalPos.getY(), (double) this.goalPos.getZ(), 1.100000023841858D);
            }

        }

        @Override
        public void stop() {
            this.cat.setLying(false);
            float f = this.cat.level.getTimeOfDay(1.0F);

            if (this.ownerPlayer.getSleepTimer() >= 100 && (double) f > 0.77D && (double) f < 0.8D && (double) this.cat.level.getRandom().nextFloat() < 0.7D) {
                this.giveMorningGift();
            }

            this.onBedTicks = 0;
            this.cat.setRelaxStateOne(false);
            this.cat.getNavigation().stop();
        }

        private void giveMorningGift() {
            Random random = this.cat.getRandom();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            blockposition_mutableblockposition.set(this.cat.blockPosition());
            this.cat.randomTeleport((double) (blockposition_mutableblockposition.getX() + random.nextInt(11) - 5), (double) (blockposition_mutableblockposition.getY() + random.nextInt(5) - 2), (double) (blockposition_mutableblockposition.getZ() + random.nextInt(11) - 5), false);
            blockposition_mutableblockposition.set(this.cat.blockPosition());
            LootTable loottable = this.cat.level.getServer().getLootTables().get(LootTables.CAT_MORNING_GIFT);
            LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.cat.level)).withParameter(LootContextParameters.ORIGIN, this.cat.position()).withParameter(LootContextParameters.THIS_ENTITY, this.cat).withRandom(random);
            List<ItemStack> list = loottable.getRandomItems(loottableinfo_builder.create(LootContextParameterSets.GIFT));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator.next();

                this.cat.level.addFreshEntity(new EntityItem(this.cat.level, (double) blockposition_mutableblockposition.getX() - (double) MathHelper.sin(this.cat.yBodyRot * 0.017453292F), (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + (double) MathHelper.cos(this.cat.yBodyRot * 0.017453292F), itemstack));
            }

        }

        @Override
        public void tick() {
            if (this.ownerPlayer != null && this.goalPos != null) {
                this.cat.setInSittingPose(false);
                this.cat.getNavigation().moveTo((double) this.goalPos.getX(), (double) this.goalPos.getY(), (double) this.goalPos.getZ(), 1.100000023841858D);
                if (this.cat.distanceToSqr((Entity) this.ownerPlayer) < 2.5D) {
                    ++this.onBedTicks;
                    if (this.onBedTicks > this.adjustedTickDelay(16)) {
                        this.cat.setLying(true);
                        this.cat.setRelaxStateOne(false);
                    } else {
                        this.cat.lookAt(this.ownerPlayer, 45.0F, 45.0F);
                        this.cat.setRelaxStateOne(true);
                    }
                } else {
                    this.cat.setLying(false);
                }
            }

        }
    }

    private static class a<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityCat cat;

        public a(EntityCat entitycat, Class<T> oclass, float f, double d0, double d1) {
            Predicate predicate = IEntitySelector.NO_CREATIVE_OR_SPECTATOR;

            Objects.requireNonNull(predicate);
            super(entitycat, oclass, f, d0, d1, predicate::test);
            this.cat = entitycat;
        }

        @Override
        public boolean canUse() {
            return !this.cat.isTame() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.cat.isTame() && super.canContinueToUse();
        }
    }
}
