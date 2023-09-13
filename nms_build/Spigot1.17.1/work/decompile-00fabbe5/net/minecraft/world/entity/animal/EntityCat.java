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
    private static final RecipeItemStack TEMPT_INGREDIENT = RecipeItemStack.a(Items.COD, Items.SALMON);
    private static final DataWatcherObject<Integer> DATA_TYPE_ID = DataWatcher.a(EntityCat.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> IS_LYING = DataWatcher.a(EntityCat.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> RELAX_STATE_ONE = DataWatcher.a(EntityCat.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> DATA_COLLAR_COLOR = DataWatcher.a(EntityCat.class, DataWatcherRegistry.INT);
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
    public static final Map<Integer, MinecraftKey> TEXTURE_BY_TYPE = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
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

    public MinecraftKey fE() {
        return (MinecraftKey) EntityCat.TEXTURE_BY_TYPE.getOrDefault(this.getCatType(), (MinecraftKey) EntityCat.TEXTURE_BY_TYPE.get(0));
    }

    @Override
    protected void initPathfinder() {
        this.temptGoal = new EntityCat.PathfinderGoalTemptChance(this, 0.6D, EntityCat.TEMPT_INGREDIENT, true);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalSit(this));
        this.goalSelector.a(2, new EntityCat.b(this));
        this.goalSelector.a(3, this.temptGoal);
        this.goalSelector.a(5, new PathfinderGoalCatSitOnBed(this, 1.1D, 8));
        this.goalSelector.a(6, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 5.0F, false));
        this.goalSelector.a(7, new PathfinderGoalJumpOnBlock(this, 0.8D));
        this.goalSelector.a(8, new PathfinderGoalLeapAtTarget(this, 0.3F));
        this.goalSelector.a(9, new PathfinderGoalOcelotAttack(this));
        this.goalSelector.a(10, new PathfinderGoalBreed(this, 0.8D));
        this.goalSelector.a(11, new PathfinderGoalRandomStrollLand(this, 0.8D, 1.0000001E-5F));
        this.goalSelector.a(12, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
        this.targetSelector.a(1, new PathfinderGoalRandomTargetNonTamed<>(this, EntityRabbit.class, false, (Predicate) null));
        this.targetSelector.a(1, new PathfinderGoalRandomTargetNonTamed<>(this, EntityTurtle.class, false, EntityTurtle.BABY_ON_LAND_SELECTOR));
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

    public void z(boolean flag) {
        this.entityData.set(EntityCat.IS_LYING, flag);
    }

    public boolean fG() {
        return (Boolean) this.entityData.get(EntityCat.IS_LYING);
    }

    public void A(boolean flag) {
        this.entityData.set(EntityCat.RELAX_STATE_ONE, flag);
    }

    public boolean fH() {
        return (Boolean) this.entityData.get(EntityCat.RELAX_STATE_ONE);
    }

    public EnumColor getCollarColor() {
        return EnumColor.fromColorIndex((Integer) this.entityData.get(EntityCat.DATA_COLLAR_COLOR));
    }

    public void setCollarColor(EnumColor enumcolor) {
        this.entityData.set(EntityCat.DATA_COLLAR_COLOR, enumcolor.getColorIndex());
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityCat.DATA_TYPE_ID, 1);
        this.entityData.register(EntityCat.IS_LYING, false);
        this.entityData.register(EntityCat.RELAX_STATE_ONE, false);
        this.entityData.register(EntityCat.DATA_COLLAR_COLOR, EnumColor.RED.getColorIndex());
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("CatType", this.getCatType());
        nbttagcompound.setByte("CollarColor", (byte) this.getCollarColor().getColorIndex());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setCatType(nbttagcompound.getInt("CatType"));
        if (nbttagcompound.hasKeyOfType("CollarColor", 99)) {
            this.setCollarColor(EnumColor.fromColorIndex(nbttagcompound.getInt("CollarColor")));
        }

    }

    @Override
    public void mobTick() {
        if (this.getControllerMove().b()) {
            double d0 = this.getControllerMove().c();

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
    protected SoundEffect getSoundAmbient() {
        return this.isTamed() ? (this.isInLove() ? SoundEffects.CAT_PURR : (this.random.nextInt(4) == 0 ? SoundEffects.CAT_PURREOW : SoundEffects.CAT_AMBIENT)) : SoundEffects.CAT_STRAY_AMBIENT;
    }

    @Override
    public int J() {
        return 120;
    }

    public void fJ() {
        this.playSound(SoundEffects.CAT_HISS, this.getSoundVolume(), this.ep());
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.CAT_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.CAT_DEATH;
    }

    public static AttributeProvider.Builder fK() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 10.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    protected void a(EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack) {
        if (this.isBreedItem(itemstack)) {
            this.playSound(SoundEffects.CAT_EAT, 1.0F, 1.0F);
        }

        super.a(entityhuman, enumhand, itemstack);
    }

    private float fL() {
        return (float) this.b(GenericAttributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean attackEntity(Entity entity) {
        return entity.damageEntity(DamageSource.mobAttack(this), this.fL());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.temptGoal != null && this.temptGoal.h() && !this.isTamed() && this.tickCount % 100 == 0) {
            this.playSound(SoundEffects.CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
        }

        this.fM();
    }

    private void fM() {
        if ((this.fG() || this.fH()) && this.tickCount % 5 == 0) {
            this.playSound(SoundEffects.CAT_PURR, 0.6F + 0.4F * (this.random.nextFloat() - this.random.nextFloat()), 1.0F);
        }

        this.fN();
        this.fO();
    }

    private void fN() {
        this.lieDownAmountO = this.lieDownAmount;
        this.lieDownAmountOTail = this.lieDownAmountTail;
        if (this.fG()) {
            this.lieDownAmount = Math.min(1.0F, this.lieDownAmount + 0.15F);
            this.lieDownAmountTail = Math.min(1.0F, this.lieDownAmountTail + 0.08F);
        } else {
            this.lieDownAmount = Math.max(0.0F, this.lieDownAmount - 0.22F);
            this.lieDownAmountTail = Math.max(0.0F, this.lieDownAmountTail - 0.13F);
        }

    }

    private void fO() {
        this.relaxStateOneAmountO = this.relaxStateOneAmount;
        if (this.fH()) {
            this.relaxStateOneAmount = Math.min(1.0F, this.relaxStateOneAmount + 0.1F);
        } else {
            this.relaxStateOneAmount = Math.max(0.0F, this.relaxStateOneAmount - 0.13F);
        }

    }

    public float z(float f) {
        return MathHelper.h(f, this.lieDownAmountO, this.lieDownAmount);
    }

    public float A(float f) {
        return MathHelper.h(f, this.lieDownAmountOTail, this.lieDownAmountTail);
    }

    public float B(float f) {
        return MathHelper.h(f, this.relaxStateOneAmountO, this.relaxStateOneAmount);
    }

    @Override
    public EntityCat createChild(WorldServer worldserver, EntityAgeable entityageable) {
        EntityCat entitycat = (EntityCat) EntityTypes.CAT.a((World) worldserver);

        if (entityageable instanceof EntityCat) {
            if (this.random.nextBoolean()) {
                entitycat.setCatType(this.getCatType());
            } else {
                entitycat.setCatType(((EntityCat) entityageable).getCatType());
            }

            if (this.isTamed()) {
                entitycat.setOwnerUUID(this.getOwnerUUID());
                entitycat.setTamed(true);
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
    public boolean mate(EntityAnimal entityanimal) {
        if (!this.isTamed()) {
            return false;
        } else if (!(entityanimal instanceof EntityCat)) {
            return false;
        } else {
            EntityCat entitycat = (EntityCat) entityanimal;

            return entitycat.isTamed() && super.mate(entityanimal);
        }
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        if (worldaccess.ak() > 0.9F) {
            this.setCatType(this.random.nextInt(11));
        } else {
            this.setCatType(this.random.nextInt(10));
        }

        WorldServer worldserver = worldaccess.getLevel();

        if (worldserver instanceof WorldServer && ((WorldServer) worldserver).getStructureManager().a(this.getChunkCoordinates(), true, StructureGenerator.SWAMP_HUT).e()) {
            this.setCatType(10);
            this.setPersistent();
        }

        return groupdataentity;
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        Item item = itemstack.getItem();

        if (this.level.isClientSide) {
            return this.isTamed() && this.j((EntityLiving) entityhuman) ? EnumInteractionResult.SUCCESS : (this.isBreedItem(itemstack) && (this.getHealth() < this.getMaxHealth() || !this.isTamed()) ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS);
        } else {
            EnumInteractionResult enuminteractionresult;

            if (this.isTamed()) {
                if (this.j((EntityLiving) entityhuman)) {
                    if (!(item instanceof ItemDye)) {
                        if (item.isFood() && this.isBreedItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
                            this.a(entityhuman, enumhand, itemstack);
                            this.heal((float) item.getFoodInfo().getNutrition());
                            return EnumInteractionResult.CONSUME;
                        }

                        enuminteractionresult = super.b(entityhuman, enumhand);
                        if (!enuminteractionresult.a() || this.isBaby()) {
                            this.setWillSit(!this.isWillSit());
                        }

                        return enuminteractionresult;
                    }

                    EnumColor enumcolor = ((ItemDye) item).d();

                    if (enumcolor != this.getCollarColor()) {
                        this.setCollarColor(enumcolor);
                        if (!entityhuman.getAbilities().instabuild) {
                            itemstack.subtract(1);
                        }

                        this.setPersistent();
                        return EnumInteractionResult.CONSUME;
                    }
                }
            } else if (this.isBreedItem(itemstack)) {
                this.a(entityhuman, enumhand, itemstack);
                if (this.random.nextInt(3) == 0) {
                    this.tame(entityhuman);
                    this.setWillSit(true);
                    this.level.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEffect(this, (byte) 6);
                }

                this.setPersistent();
                return EnumInteractionResult.CONSUME;
            }

            enuminteractionresult = super.b(entityhuman, enumhand);
            if (enuminteractionresult.a()) {
                this.setPersistent();
            }

            return enuminteractionresult;
        }
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return EntityCat.TEMPT_INGREDIENT.test(itemstack);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.5F;
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isTamed() && this.tickCount > 2400;
    }

    @Override
    protected void t() {
        if (this.avoidPlayersGoal == null) {
            this.avoidPlayersGoal = new EntityCat.a<>(this, EntityHuman.class, 16.0F, 0.8D, 1.33D);
        }

        this.goalSelector.a((PathfinderGoal) this.avoidPlayersGoal);
        if (!this.isTamed()) {
            this.goalSelector.a(4, this.avoidPlayersGoal);
        }

    }

    @Override
    public boolean bE() {
        return this.getPose() == EntityPose.CROUCHING || super.bE();
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
        public void e() {
            super.e();
            if (this.selectedPlayer == null && this.mob.getRandom().nextInt(600) == 0) {
                this.selectedPlayer = this.player;
            } else if (this.mob.getRandom().nextInt(500) == 0) {
                this.selectedPlayer = null;
            }

        }

        @Override
        protected boolean g() {
            return this.selectedPlayer != null && this.selectedPlayer.equals(this.player) ? false : super.g();
        }

        @Override
        public boolean a() {
            return super.a() && !this.cat.isTamed();
        }
    }

    private static class b extends PathfinderGoal {

        private final EntityCat cat;
        private EntityHuman ownerPlayer;
        private BlockPosition goalPos;
        private int onBedTicks;

        public b(EntityCat entitycat) {
            this.cat = entitycat;
        }

        @Override
        public boolean a() {
            if (!this.cat.isTamed()) {
                return false;
            } else if (this.cat.isWillSit()) {
                return false;
            } else {
                EntityLiving entityliving = this.cat.getOwner();

                if (entityliving instanceof EntityHuman) {
                    this.ownerPlayer = (EntityHuman) entityliving;
                    if (!entityliving.isSleeping()) {
                        return false;
                    }

                    if (this.cat.f((Entity) this.ownerPlayer) > 100.0D) {
                        return false;
                    }

                    BlockPosition blockposition = this.ownerPlayer.getChunkCoordinates();
                    IBlockData iblockdata = this.cat.level.getType(blockposition);

                    if (iblockdata.a((Tag) TagsBlock.BEDS)) {
                        this.goalPos = (BlockPosition) iblockdata.d(BlockBed.FACING).map((enumdirection) -> {
                            return blockposition.shift(enumdirection.opposite());
                        }).orElseGet(() -> {
                            return new BlockPosition(blockposition);
                        });
                        return !this.g();
                    }
                }

                return false;
            }
        }

        private boolean g() {
            List<EntityCat> list = this.cat.level.a(EntityCat.class, (new AxisAlignedBB(this.goalPos)).g(2.0D));
            Iterator iterator = list.iterator();

            EntityCat entitycat;

            do {
                do {
                    if (!iterator.hasNext()) {
                        return false;
                    }

                    entitycat = (EntityCat) iterator.next();
                } while (entitycat == this.cat);
            } while (!entitycat.fG() && !entitycat.fH());

            return true;
        }

        @Override
        public boolean b() {
            return this.cat.isTamed() && !this.cat.isWillSit() && this.ownerPlayer != null && this.ownerPlayer.isSleeping() && this.goalPos != null && !this.g();
        }

        @Override
        public void c() {
            if (this.goalPos != null) {
                this.cat.setSitting(false);
                this.cat.getNavigation().a((double) this.goalPos.getX(), (double) this.goalPos.getY(), (double) this.goalPos.getZ(), 1.100000023841858D);
            }

        }

        @Override
        public void d() {
            this.cat.z(false);
            float f = this.cat.level.f(1.0F);

            if (this.ownerPlayer.fn() >= 100 && (double) f > 0.77D && (double) f < 0.8D && (double) this.cat.level.getRandom().nextFloat() < 0.7D) {
                this.h();
            }

            this.onBedTicks = 0;
            this.cat.A(false);
            this.cat.getNavigation().o();
        }

        private void h() {
            Random random = this.cat.getRandom();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            blockposition_mutableblockposition.g(this.cat.getChunkCoordinates());
            this.cat.a((double) (blockposition_mutableblockposition.getX() + random.nextInt(11) - 5), (double) (blockposition_mutableblockposition.getY() + random.nextInt(5) - 2), (double) (blockposition_mutableblockposition.getZ() + random.nextInt(11) - 5), false);
            blockposition_mutableblockposition.g(this.cat.getChunkCoordinates());
            LootTable loottable = this.cat.level.getMinecraftServer().getLootTableRegistry().getLootTable(LootTables.CAT_MORNING_GIFT);
            LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.cat.level)).set(LootContextParameters.ORIGIN, this.cat.getPositionVector()).set(LootContextParameters.THIS_ENTITY, this.cat).a(random);
            List<ItemStack> list = loottable.populateLoot(loottableinfo_builder.build(LootContextParameterSets.GIFT));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator.next();

                this.cat.level.addEntity(new EntityItem(this.cat.level, (double) blockposition_mutableblockposition.getX() - (double) MathHelper.sin(this.cat.yBodyRot * 0.017453292F), (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + (double) MathHelper.cos(this.cat.yBodyRot * 0.017453292F), itemstack));
            }

        }

        @Override
        public void e() {
            if (this.ownerPlayer != null && this.goalPos != null) {
                this.cat.setSitting(false);
                this.cat.getNavigation().a((double) this.goalPos.getX(), (double) this.goalPos.getY(), (double) this.goalPos.getZ(), 1.100000023841858D);
                if (this.cat.f((Entity) this.ownerPlayer) < 2.5D) {
                    ++this.onBedTicks;
                    if (this.onBedTicks > 16) {
                        this.cat.z(true);
                        this.cat.A(false);
                    } else {
                        this.cat.a((Entity) this.ownerPlayer, 45.0F, 45.0F);
                        this.cat.A(true);
                    }
                } else {
                    this.cat.z(false);
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
        public boolean a() {
            return !this.cat.isTamed() && super.a();
        }

        @Override
        public boolean b() {
            return !this.cat.isTamed() && super.b();
        }
    }
}
