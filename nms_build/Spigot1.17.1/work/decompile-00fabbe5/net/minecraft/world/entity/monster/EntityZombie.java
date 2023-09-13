package net.minecraft.world.entity.monster;

import com.mojang.serialization.DynamicOps;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntityPositionTypes;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreakDoor;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveThroughVillage;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRemoveBlock;
import net.minecraft.world.entity.ai.goal.PathfinderGoalZombieAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.util.PathfinderGoalUtil;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntityTurtle;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class EntityZombie extends EntityMonster {

    private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(EntityZombie.SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final DataWatcherObject<Boolean> DATA_BABY_ID = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> DATA_SPECIAL_TYPE_ID = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.INT);
    public static final DataWatcherObject<Boolean> DATA_DROWNED_CONVERSION_ID = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.BOOLEAN);
    public static final float ZOMBIE_LEADER_CHANCE = 0.05F;
    public static final int REINFORCEMENT_ATTEMPTS = 50;
    public static final int REINFORCEMENT_RANGE_MAX = 40;
    public static final int REINFORCEMENT_RANGE_MIN = 7;
    private static final float BREAK_DOOR_CHANCE = 0.1F;
    private static final Predicate<EnumDifficulty> DOOR_BREAKING_PREDICATE = (enumdifficulty) -> {
        return enumdifficulty == EnumDifficulty.HARD;
    };
    private final PathfinderGoalBreakDoor breakDoorGoal;
    private boolean canBreakDoors;
    private int inWaterTime;
    public int conversionTime;

    public EntityZombie(EntityTypes<? extends EntityZombie> entitytypes, World world) {
        super(entitytypes, world);
        this.breakDoorGoal = new PathfinderGoalBreakDoor(this, EntityZombie.DOOR_BREAKING_PREDICATE);
    }

    public EntityZombie(World world) {
        this(EntityTypes.ZOMBIE, world);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(4, new EntityZombie.a(this, 1.0D, 3));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.n();
    }

    protected void n() {
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, true, 4, this::fE));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(EntityPigZombie.class));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeProvider.Builder fC() {
        return EntityMonster.fB().a(GenericAttributes.FOLLOW_RANGE, 35.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D).a(GenericAttributes.ATTACK_DAMAGE, 3.0D).a(GenericAttributes.ARMOR, 2.0D).a(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register(EntityZombie.DATA_BABY_ID, false);
        this.getDataWatcher().register(EntityZombie.DATA_SPECIAL_TYPE_ID, 0);
        this.getDataWatcher().register(EntityZombie.DATA_DROWNED_CONVERSION_ID, false);
    }

    public boolean isDrownConverting() {
        return (Boolean) this.getDataWatcher().get(EntityZombie.DATA_DROWNED_CONVERSION_ID);
    }

    public boolean fE() {
        return this.canBreakDoors;
    }

    public void w(boolean flag) {
        if (this.p() && PathfinderGoalUtil.a(this)) {
            if (this.canBreakDoors != flag) {
                this.canBreakDoors = flag;
                ((Navigation) this.getNavigation()).a(flag);
                if (flag) {
                    this.goalSelector.a(1, this.breakDoorGoal);
                } else {
                    this.goalSelector.a((PathfinderGoal) this.breakDoorGoal);
                }
            }
        } else if (this.canBreakDoors) {
            this.goalSelector.a((PathfinderGoal) this.breakDoorGoal);
            this.canBreakDoors = false;
        }

    }

    protected boolean p() {
        return true;
    }

    @Override
    public boolean isBaby() {
        return (Boolean) this.getDataWatcher().get(EntityZombie.DATA_BABY_ID);
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        if (this.isBaby()) {
            this.xpReward = (int) ((float) this.xpReward * 2.5F);
        }

        return super.getExpValue(entityhuman);
    }

    @Override
    public void setBaby(boolean flag) {
        this.getDataWatcher().set(EntityZombie.DATA_BABY_ID, flag);
        if (this.level != null && !this.level.isClientSide) {
            AttributeModifiable attributemodifiable = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

            attributemodifiable.removeModifier(EntityZombie.SPEED_MODIFIER_BABY);
            if (flag) {
                attributemodifiable.b(EntityZombie.SPEED_MODIFIER_BABY);
            }
        }

    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityZombie.DATA_BABY_ID.equals(datawatcherobject)) {
            this.updateSize();
        }

        super.a(datawatcherobject);
    }

    protected boolean fx() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && !this.isNoAI()) {
            if (this.isDrownConverting()) {
                --this.conversionTime;
                if (this.conversionTime < 0) {
                    this.fz();
                }
            } else if (this.fx()) {
                if (this.a((Tag) TagsFluid.WATER)) {
                    ++this.inWaterTime;
                    if (this.inWaterTime >= 600) {
                        this.startDrownedConversion(300);
                    }
                } else {
                    this.inWaterTime = -1;
                }
            }
        }

        super.tick();
    }

    @Override
    public void movementTick() {
        if (this.isAlive()) {
            boolean flag = this.I_() && this.fs();

            if (flag) {
                ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);

                if (!itemstack.isEmpty()) {
                    if (itemstack.f()) {
                        itemstack.setDamage(itemstack.getDamage() + this.random.nextInt(2));
                        if (itemstack.getDamage() >= itemstack.i()) {
                            this.broadcastItemBreak(EnumItemSlot.HEAD);
                            this.setSlot(EnumItemSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    this.setOnFire(8);
                }
            }
        }

        super.movementTick();
    }

    public void startDrownedConversion(int i) {
        this.conversionTime = i;
        this.getDataWatcher().set(EntityZombie.DATA_DROWNED_CONVERSION_ID, true);
    }

    protected void fz() {
        this.b(EntityTypes.DROWNED);
        if (!this.isSilent()) {
            this.level.a((EntityHuman) null, 1040, this.getChunkCoordinates(), 0);
        }

    }

    protected void b(EntityTypes<? extends EntityZombie> entitytypes) {
        EntityZombie entityzombie = (EntityZombie) this.a(entitytypes, true);

        if (entityzombie != null) {
            entityzombie.z(entityzombie.level.getDamageScaler(entityzombie.getChunkCoordinates()).d());
            entityzombie.w(entityzombie.p() && this.fE());
        }

    }

    protected boolean I_() {
        return true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!super.damageEntity(damagesource, f)) {
            return false;
        } else if (!(this.level instanceof WorldServer)) {
            return false;
        } else {
            WorldServer worldserver = (WorldServer) this.level;
            EntityLiving entityliving = this.getGoalTarget();

            if (entityliving == null && damagesource.getEntity() instanceof EntityLiving) {
                entityliving = (EntityLiving) damagesource.getEntity();
            }

            if (entityliving != null && this.level.getDifficulty() == EnumDifficulty.HARD && (double) this.random.nextFloat() < this.b(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE) && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                int i = MathHelper.floor(this.locX());
                int j = MathHelper.floor(this.locY());
                int k = MathHelper.floor(this.locZ());
                EntityZombie entityzombie = new EntityZombie(this.level);

                for (int l = 0; l < 50; ++l) {
                    int i1 = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int j1 = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int k1 = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    BlockPosition blockposition = new BlockPosition(i1, j1, k1);
                    EntityTypes<?> entitytypes = entityzombie.getEntityType();
                    EntityPositionTypes.Surface entitypositiontypes_surface = EntityPositionTypes.a(entitytypes);

                    if (SpawnerCreature.a(entitypositiontypes_surface, (IWorldReader) this.level, blockposition, entitytypes) && EntityPositionTypes.a(entitytypes, worldserver, EnumMobSpawn.REINFORCEMENT, blockposition, this.level.random)) {
                        entityzombie.setPosition((double) i1, (double) j1, (double) k1);
                        if (!this.level.isPlayerNearby((double) i1, (double) j1, (double) k1, 7.0D) && this.level.f((Entity) entityzombie) && this.level.getCubes(entityzombie) && !this.level.containsLiquid(entityzombie.getBoundingBox())) {
                            entityzombie.setGoalTarget(entityliving);
                            entityzombie.prepare(worldserver, this.level.getDamageScaler(entityzombie.getChunkCoordinates()), EnumMobSpawn.REINFORCEMENT, (GroupDataEntity) null, (NBTTagCompound) null);
                            worldserver.addAllEntities(entityzombie);
                            this.getAttributeInstance(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE).addModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                            entityzombie.getAttributeInstance(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE).addModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                            break;
                        }
                    }
                }
            }

            return true;
        }
    }

    @Override
    public boolean attackEntity(Entity entity) {
        boolean flag = super.attackEntity(entity);

        if (flag) {
            float f = this.level.getDamageScaler(this.getChunkCoordinates()).b();

            if (this.getItemInMainHand().isEmpty() && this.isBurning() && this.random.nextFloat() < f * 0.3F) {
                entity.setOnFire(2 * (int) f);
            }
        }

        return flag;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ZOMBIE_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ZOMBIE_DEATH;
    }

    protected SoundEffect getSoundStep() {
        return SoundEffects.ZOMBIE_STEP;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(this.getSoundStep(), 0.15F, 1.0F);
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        super.a(difficultydamagescaler);
        if (this.random.nextFloat() < (this.level.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
            int i = this.random.nextInt(3);

            if (i == 0) {
                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setBoolean("IsBaby", this.isBaby());
        nbttagcompound.setBoolean("CanBreakDoors", this.fE());
        nbttagcompound.setInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
        nbttagcompound.setInt("DrownedConversionTime", this.isDrownConverting() ? this.conversionTime : -1);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setBaby(nbttagcompound.getBoolean("IsBaby"));
        this.w(nbttagcompound.getBoolean("CanBreakDoors"));
        this.inWaterTime = nbttagcompound.getInt("InWaterTime");
        if (nbttagcompound.hasKeyOfType("DrownedConversionTime", 99) && nbttagcompound.getInt("DrownedConversionTime") > -1) {
            this.startDrownedConversion(nbttagcompound.getInt("DrownedConversionTime"));
        }

    }

    @Override
    public void a(WorldServer worldserver, EntityLiving entityliving) {
        super.a(worldserver, entityliving);
        if ((worldserver.getDifficulty() == EnumDifficulty.NORMAL || worldserver.getDifficulty() == EnumDifficulty.HARD) && entityliving instanceof EntityVillager) {
            if (worldserver.getDifficulty() != EnumDifficulty.HARD && this.random.nextBoolean()) {
                return;
            }

            EntityVillager entityvillager = (EntityVillager) entityliving;
            EntityZombieVillager entityzombievillager = (EntityZombieVillager) entityvillager.a(EntityTypes.ZOMBIE_VILLAGER, false);

            entityzombievillager.prepare(worldserver, worldserver.getDamageScaler(entityzombievillager.getChunkCoordinates()), EnumMobSpawn.CONVERSION, new EntityZombie.GroupDataZombie(false, true), (NBTTagCompound) null);
            entityzombievillager.setVillagerData(entityvillager.getVillagerData());
            entityzombievillager.a((NBTBase) entityvillager.fT().a((DynamicOps) DynamicOpsNBT.INSTANCE).getValue());
            entityzombievillager.setOffers(entityvillager.getOffers().a());
            entityzombievillager.a(entityvillager.getExperience());
            if (!this.isSilent()) {
                worldserver.a((EntityHuman) null, 1026, this.getChunkCoordinates(), 0);
            }
        }

    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? 0.93F : 1.74F;
    }

    @Override
    public boolean canPickup(ItemStack itemstack) {
        return itemstack.a(Items.EGG) && this.isBaby() && this.isPassenger() ? false : super.canPickup(itemstack);
    }

    @Override
    public boolean l(ItemStack itemstack) {
        return itemstack.a(Items.GLOW_INK_SAC) ? false : super.l(itemstack);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        float f = difficultydamagescaler.d();

        this.setCanPickupLoot(this.random.nextFloat() < 0.55F * f);
        if (object == null) {
            object = new EntityZombie.GroupDataZombie(a(worldaccess.getRandom()), true);
        }

        if (object instanceof EntityZombie.GroupDataZombie) {
            EntityZombie.GroupDataZombie entityzombie_groupdatazombie = (EntityZombie.GroupDataZombie) object;

            if (entityzombie_groupdatazombie.isBaby) {
                this.setBaby(true);
                if (entityzombie_groupdatazombie.canSpawnJockey) {
                    if ((double) worldaccess.getRandom().nextFloat() < 0.05D) {
                        List<EntityChicken> list = worldaccess.a(EntityChicken.class, this.getBoundingBox().grow(5.0D, 3.0D, 5.0D), IEntitySelector.ENTITY_NOT_BEING_RIDDEN);

                        if (!list.isEmpty()) {
                            EntityChicken entitychicken = (EntityChicken) list.get(0);

                            entitychicken.setChickenJockey(true);
                            this.startRiding(entitychicken);
                        }
                    } else if ((double) worldaccess.getRandom().nextFloat() < 0.05D) {
                        EntityChicken entitychicken1 = (EntityChicken) EntityTypes.CHICKEN.a(this.level);

                        entitychicken1.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.getYRot(), 0.0F);
                        entitychicken1.prepare(worldaccess, difficultydamagescaler, EnumMobSpawn.JOCKEY, (GroupDataEntity) null, (NBTTagCompound) null);
                        entitychicken1.setChickenJockey(true);
                        this.startRiding(entitychicken1);
                        worldaccess.addEntity(entitychicken1);
                    }
                }
            }

            this.w(this.p() && this.random.nextFloat() < f * 0.1F);
            this.a(difficultydamagescaler);
            this.b(difficultydamagescaler);
        }

        if (this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
            LocalDate localdate = LocalDate.now();
            int i = localdate.get(ChronoField.DAY_OF_MONTH);
            int j = localdate.get(ChronoField.MONTH_OF_YEAR);

            if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
                this.setSlot(EnumItemSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EnumItemSlot.HEAD.b()] = 0.0F;
            }
        }

        this.z(f);
        return (GroupDataEntity) object;
    }

    public static boolean a(Random random) {
        return random.nextFloat() < 0.05F;
    }

    protected void z(float f) {
        this.fF();
        this.getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE).addModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806D, AttributeModifier.Operation.ADDITION));
        double d0 = this.random.nextDouble() * 1.5D * (double) f;

        if (d0 > 1.0D) {
            this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).addModifier(new AttributeModifier("Random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        if (this.random.nextFloat() < f * 0.05F) {
            this.getAttributeInstance(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
            this.getAttributeInstance(GenericAttributes.MAX_HEALTH).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
            this.w(this.p());
        }

    }

    protected void fF() {
        this.getAttributeInstance(GenericAttributes.SPAWN_REINFORCEMENTS_CHANCE).setValue(this.random.nextDouble() * 0.10000000149011612D);
    }

    @Override
    public double bk() {
        return this.isBaby() ? 0.0D : -0.45D;
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        Entity entity = damagesource.getEntity();

        if (entity instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) entity;

            if (entitycreeper.canCauseHeadDrop()) {
                ItemStack itemstack = this.fw();

                if (!itemstack.isEmpty()) {
                    entitycreeper.setCausedHeadDrop();
                    this.b(itemstack);
                }
            }
        }

    }

    protected ItemStack fw() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }

    private class a extends PathfinderGoalRemoveBlock {

        a(EntityCreature entitycreature, double d0, int i) {
            super(Blocks.TURTLE_EGG, entitycreature, d0, i);
        }

        @Override
        public void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
            generatoraccess.playSound((EntityHuman) null, blockposition, SoundEffects.ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + EntityZombie.this.random.nextFloat() * 0.2F);
        }

        @Override
        public void a(World world, BlockPosition blockposition) {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        }

        @Override
        public double h() {
            return 1.14D;
        }
    }

    public static class GroupDataZombie implements GroupDataEntity {

        public final boolean isBaby;
        public final boolean canSpawnJockey;

        public GroupDataZombie(boolean flag, boolean flag1) {
            this.isBaby = flag;
            this.canSpawnJockey = flag1;
        }
    }
}
