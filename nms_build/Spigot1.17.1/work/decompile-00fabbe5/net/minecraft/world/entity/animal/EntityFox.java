package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParamItem;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFleeSun;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalGotoTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLeapAtTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalNearestVillage;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWaterJumpAbstract;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockSweetBerryBush;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityFox extends EntityAnimal {

    private static final DataWatcherObject<Integer> DATA_TYPE_ID = DataWatcher.a(EntityFox.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.a(EntityFox.class, DataWatcherRegistry.BYTE);
    private static final int FLAG_SITTING = 1;
    public static final int FLAG_CROUCHING = 4;
    public static final int FLAG_INTERESTED = 8;
    public static final int FLAG_POUNCING = 16;
    private static final int FLAG_SLEEPING = 32;
    private static final int FLAG_FACEPLANTED = 64;
    private static final int FLAG_DEFENDING = 128;
    public static final DataWatcherObject<Optional<UUID>> DATA_TRUSTED_ID_0 = DataWatcher.a(EntityFox.class, DataWatcherRegistry.OPTIONAL_UUID);
    public static final DataWatcherObject<Optional<UUID>> DATA_TRUSTED_ID_1 = DataWatcher.a(EntityFox.class, DataWatcherRegistry.OPTIONAL_UUID);
    static final Predicate<EntityItem> ALLOWED_ITEMS = (entityitem) -> {
        return !entityitem.q() && entityitem.isAlive();
    };
    private static final Predicate<Entity> TRUSTED_TARGET_SELECTOR = (entity) -> {
        if (!(entity instanceof EntityLiving)) {
            return false;
        } else {
            EntityLiving entityliving = (EntityLiving) entity;

            return entityliving.dI() != null && entityliving.dJ() < entityliving.tickCount + 600;
        }
    };
    static final Predicate<Entity> STALKABLE_PREY = (entity) -> {
        return entity instanceof EntityChicken || entity instanceof EntityRabbit;
    };
    private static final Predicate<Entity> AVOID_PLAYERS = (entity) -> {
        return !entity.bG() && IEntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity);
    };
    private static final int MIN_TICKS_BEFORE_EAT = 600;
    private PathfinderGoal landTargetGoal;
    private PathfinderGoal turtleEggTargetGoal;
    private PathfinderGoal fishTargetGoal;
    private float interestedAngle;
    private float interestedAngleO;
    float crouchAmount;
    float crouchAmountO;
    private int ticksSinceEaten;

    public EntityFox(EntityTypes<? extends EntityFox> entitytypes, World world) {
        super(entitytypes, world);
        this.lookControl = new EntityFox.k();
        this.moveControl = new EntityFox.m();
        this.a(PathType.DANGER_OTHER, 0.0F);
        this.a(PathType.DAMAGE_OTHER, 0.0F);
        this.setCanPickupLoot(true);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityFox.DATA_TRUSTED_ID_0, Optional.empty());
        this.entityData.register(EntityFox.DATA_TRUSTED_ID_1, Optional.empty());
        this.entityData.register(EntityFox.DATA_TYPE_ID, 0);
        this.entityData.register(EntityFox.DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    protected void initPathfinder() {
        this.landTargetGoal = new PathfinderGoalNearestAttackableTarget<>(this, EntityAnimal.class, 10, false, false, (entityliving) -> {
            return entityliving instanceof EntityChicken || entityliving instanceof EntityRabbit;
        });
        this.turtleEggTargetGoal = new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, false, false, EntityTurtle.BABY_ON_LAND_SELECTOR);
        this.fishTargetGoal = new PathfinderGoalNearestAttackableTarget<>(this, EntityFish.class, 20, false, false, (entityliving) -> {
            return entityliving instanceof EntityFishSchool;
        });
        this.goalSelector.a(0, new EntityFox.g());
        this.goalSelector.a(1, new EntityFox.b());
        this.goalSelector.a(2, new EntityFox.n(2.2D));
        this.goalSelector.a(3, new EntityFox.e(1.0D));
        this.goalSelector.a(4, new PathfinderGoalAvoidTarget<>(this, EntityHuman.class, 16.0F, 1.6D, 1.4D, (entityliving) -> {
            return EntityFox.AVOID_PLAYERS.test(entityliving) && !this.c(entityliving.getUniqueID()) && !this.fJ();
        }));
        this.goalSelector.a(4, new PathfinderGoalAvoidTarget<>(this, EntityWolf.class, 8.0F, 1.6D, 1.4D, (entityliving) -> {
            return !((EntityWolf) entityliving).isTamed() && !this.fJ();
        }));
        this.goalSelector.a(4, new PathfinderGoalAvoidTarget<>(this, EntityPolarBear.class, 8.0F, 1.6D, 1.4D, (entityliving) -> {
            return !this.fJ();
        }));
        this.goalSelector.a(5, new EntityFox.u());
        this.goalSelector.a(6, new EntityFox.o());
        this.goalSelector.a(6, new EntityFox.s(1.25D));
        this.goalSelector.a(7, new EntityFox.l(1.2000000476837158D, true));
        this.goalSelector.a(7, new EntityFox.t());
        this.goalSelector.a(8, new EntityFox.h(this, 1.25D));
        this.goalSelector.a(9, new EntityFox.q(32, 200));
        this.goalSelector.a(10, new EntityFox.f(1.2000000476837158D, 12, 1));
        this.goalSelector.a(10, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(11, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(11, new EntityFox.p());
        this.goalSelector.a(12, new EntityFox.j(this, EntityHuman.class, 24.0F));
        this.goalSelector.a(13, new EntityFox.r());
        this.targetSelector.a(3, new EntityFox.a(EntityLiving.class, false, false, (entityliving) -> {
            return EntityFox.TRUSTED_TARGET_SELECTOR.test(entityliving) && !this.c(entityliving.getUniqueID());
        }));
    }

    @Override
    public SoundEffect e(ItemStack itemstack) {
        return SoundEffects.FOX_EAT;
    }

    @Override
    public void movementTick() {
        if (!this.level.isClientSide && this.isAlive() && this.doAITick()) {
            ++this.ticksSinceEaten;
            ItemStack itemstack = this.getEquipment(EnumItemSlot.MAINHAND);

            if (this.m(itemstack)) {
                if (this.ticksSinceEaten > 600) {
                    ItemStack itemstack1 = itemstack.a(this.level, (EntityLiving) this);

                    if (!itemstack1.isEmpty()) {
                        this.setSlot(EnumItemSlot.MAINHAND, itemstack1);
                    }

                    this.ticksSinceEaten = 0;
                } else if (this.ticksSinceEaten > 560 && this.random.nextFloat() < 0.1F) {
                    this.playSound(this.e(itemstack), 1.0F, 1.0F);
                    this.level.broadcastEntityEffect(this, (byte) 45);
                }
            }

            EntityLiving entityliving = this.getGoalTarget();

            if (entityliving == null || !entityliving.isAlive()) {
                this.setCrouching(false);
                this.y(false);
            }
        }

        if (this.isSleeping() || this.isFrozen()) {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
        }

        super.movementTick();
        if (this.fJ() && this.random.nextFloat() < 0.05F) {
            this.playSound(SoundEffects.FOX_AGGRO, 1.0F, 1.0F);
        }

    }

    @Override
    protected boolean isFrozen() {
        return this.dV();
    }

    private boolean m(ItemStack itemstack) {
        return itemstack.getItem().isFood() && this.getGoalTarget() == null && this.onGround && !this.isSleeping();
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        if (this.random.nextFloat() < 0.2F) {
            float f = this.random.nextFloat();
            ItemStack itemstack;

            if (f < 0.05F) {
                itemstack = new ItemStack(Items.EMERALD);
            } else if (f < 0.2F) {
                itemstack = new ItemStack(Items.EGG);
            } else if (f < 0.4F) {
                itemstack = this.random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
            } else if (f < 0.6F) {
                itemstack = new ItemStack(Items.WHEAT);
            } else if (f < 0.8F) {
                itemstack = new ItemStack(Items.LEATHER);
            } else {
                itemstack = new ItemStack(Items.FEATHER);
            }

            this.setSlot(EnumItemSlot.MAINHAND, itemstack);
        }

    }

    @Override
    public void a(byte b0) {
        if (b0 == 45) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                for (int i = 0; i < 8; ++i) {
                    Vec3D vec3d = (new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).a(-this.getXRot() * 0.017453292F).b(-this.getYRot() * 0.017453292F);

                    this.level.addParticle(new ParticleParamItem(Particles.ITEM, itemstack), this.locX() + this.getLookDirection().x / 2.0D, this.locY(), this.locZ() + this.getLookDirection().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else {
            super.a(b0);
        }

    }

    public static AttributeProvider.Builder p() {
        return EntityInsentient.w().a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.MAX_HEALTH, 10.0D).a(GenericAttributes.FOLLOW_RANGE, 32.0D).a(GenericAttributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    public EntityFox createChild(WorldServer worldserver, EntityAgeable entityageable) {
        EntityFox entityfox = (EntityFox) EntityTypes.FOX.a((World) worldserver);

        entityfox.setFoxType(this.random.nextBoolean() ? this.getFoxType() : ((EntityFox) entityageable).getFoxType());
        return entityfox;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Optional<ResourceKey<BiomeBase>> optional = worldaccess.j(this.getChunkCoordinates());
        EntityFox.Type entityfox_type = EntityFox.Type.a(optional);
        boolean flag = false;

        if (groupdataentity instanceof EntityFox.i) {
            entityfox_type = ((EntityFox.i) groupdataentity).type;
            if (((EntityFox.i) groupdataentity).a() >= 2) {
                flag = true;
            }
        } else {
            groupdataentity = new EntityFox.i(entityfox_type);
        }

        this.setFoxType(entityfox_type);
        if (flag) {
            this.setAgeRaw(-24000);
        }

        if (worldaccess instanceof WorldServer) {
            this.initializePathFinderGoals();
        }

        this.a(difficultydamagescaler);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    private void initializePathFinderGoals() {
        if (this.getFoxType() == EntityFox.Type.RED) {
            this.targetSelector.a(4, this.landTargetGoal);
            this.targetSelector.a(4, this.turtleEggTargetGoal);
            this.targetSelector.a(6, this.fishTargetGoal);
        } else {
            this.targetSelector.a(4, this.fishTargetGoal);
            this.targetSelector.a(6, this.landTargetGoal);
            this.targetSelector.a(6, this.turtleEggTargetGoal);
        }

    }

    @Override
    protected void a(EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack) {
        if (this.isBreedItem(itemstack)) {
            this.playSound(this.e(itemstack), 1.0F, 1.0F);
        }

        super.a(entityhuman, enumhand, itemstack);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? entitysize.height * 0.85F : 0.4F;
    }

    public EntityFox.Type getFoxType() {
        return EntityFox.Type.a((Integer) this.entityData.get(EntityFox.DATA_TYPE_ID));
    }

    public void setFoxType(EntityFox.Type entityfox_type) {
        this.entityData.set(EntityFox.DATA_TYPE_ID, entityfox_type.b());
    }

    List<UUID> fI() {
        List<UUID> list = Lists.newArrayList();

        list.add((UUID) ((Optional) this.entityData.get(EntityFox.DATA_TRUSTED_ID_0)).orElse((Object) null));
        list.add((UUID) ((Optional) this.entityData.get(EntityFox.DATA_TRUSTED_ID_1)).orElse((Object) null));
        return list;
    }

    void b(@Nullable UUID uuid) {
        if (((Optional) this.entityData.get(EntityFox.DATA_TRUSTED_ID_0)).isPresent()) {
            this.entityData.set(EntityFox.DATA_TRUSTED_ID_1, Optional.ofNullable(uuid));
        } else {
            this.entityData.set(EntityFox.DATA_TRUSTED_ID_0, Optional.ofNullable(uuid));
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        List<UUID> list = this.fI();
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            UUID uuid = (UUID) iterator.next();

            if (uuid != null) {
                nbttaglist.add(GameProfileSerializer.a(uuid));
            }
        }

        nbttagcompound.set("Trusted", nbttaglist);
        nbttagcompound.setBoolean("Sleeping", this.isSleeping());
        nbttagcompound.setString("Type", this.getFoxType().a());
        nbttagcompound.setBoolean("Sitting", this.isSitting());
        nbttagcompound.setBoolean("Crouching", this.isCrouching());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Trusted", 11);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            this.b(GameProfileSerializer.a(nbttaglist.get(i)));
        }

        this.setSleeping(nbttagcompound.getBoolean("Sleeping"));
        this.setFoxType(EntityFox.Type.a(nbttagcompound.getString("Type")));
        this.setSitting(nbttagcompound.getBoolean("Sitting"));
        this.setCrouching(nbttagcompound.getBoolean("Crouching"));
        if (this.level instanceof WorldServer) {
            this.initializePathFinderGoals();
        }

    }

    public boolean isSitting() {
        return this.u(1);
    }

    public void setSitting(boolean flag) {
        this.d(1, flag);
    }

    public boolean fx() {
        return this.u(64);
    }

    void z(boolean flag) {
        this.d(64, flag);
    }

    boolean fJ() {
        return this.u(128);
    }

    void A(boolean flag) {
        this.d(128, flag);
    }

    @Override
    public boolean isSleeping() {
        return this.u(32);
    }

    public void setSleeping(boolean flag) {
        this.d(32, flag);
    }

    private void d(int i, boolean flag) {
        if (flag) {
            this.entityData.set(EntityFox.DATA_FLAGS_ID, (byte) ((Byte) this.entityData.get(EntityFox.DATA_FLAGS_ID) | i));
        } else {
            this.entityData.set(EntityFox.DATA_FLAGS_ID, (byte) ((Byte) this.entityData.get(EntityFox.DATA_FLAGS_ID) & ~i));
        }

    }

    private boolean u(int i) {
        return ((Byte) this.entityData.get(EntityFox.DATA_FLAGS_ID) & i) != 0;
    }

    @Override
    public boolean g(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

        return !this.getEquipment(enumitemslot).isEmpty() ? false : enumitemslot == EnumItemSlot.MAINHAND && super.g(itemstack);
    }

    @Override
    public boolean canPickup(ItemStack itemstack) {
        Item item = itemstack.getItem();
        ItemStack itemstack1 = this.getEquipment(EnumItemSlot.MAINHAND);

        return itemstack1.isEmpty() || this.ticksSinceEaten > 0 && item.isFood() && !itemstack1.getItem().isFood();
    }

    private void o(ItemStack itemstack) {
        if (!itemstack.isEmpty() && !this.level.isClientSide) {
            EntityItem entityitem = new EntityItem(this.level, this.locX() + this.getLookDirection().x, this.locY() + 1.0D, this.locZ() + this.getLookDirection().z, itemstack);

            entityitem.setPickupDelay(40);
            entityitem.setThrower(this.getUniqueID());
            this.playSound(SoundEffects.FOX_SPIT, 1.0F, 1.0F);
            this.level.addEntity(entityitem);
        }
    }

    private void p(ItemStack itemstack) {
        EntityItem entityitem = new EntityItem(this.level, this.locX(), this.locY(), this.locZ(), itemstack);

        this.level.addEntity(entityitem);
    }

    @Override
    protected void b(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();

        if (this.canPickup(itemstack)) {
            int i = itemstack.getCount();

            if (i > 1) {
                this.p(itemstack.cloneAndSubtract(i - 1));
            }

            this.o(this.getEquipment(EnumItemSlot.MAINHAND));
            this.a(entityitem);
            this.setSlot(EnumItemSlot.MAINHAND, itemstack.cloneAndSubtract(1));
            this.handDropChances[EnumItemSlot.MAINHAND.b()] = 2.0F;
            this.receive(entityitem, itemstack.getCount());
            entityitem.die();
            this.ticksSinceEaten = 0;
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.doAITick()) {
            boolean flag = this.isInWater();

            if (flag || this.getGoalTarget() != null || this.level.Y()) {
                this.fK();
            }

            if (flag || this.isSleeping()) {
                this.setSitting(false);
            }

            if (this.fx() && this.level.random.nextFloat() < 0.2F) {
                BlockPosition blockposition = this.getChunkCoordinates();
                IBlockData iblockdata = this.level.getType(blockposition);

                this.level.triggerEffect(2001, blockposition, Block.getCombinedId(iblockdata));
            }
        }

        this.interestedAngleO = this.interestedAngle;
        if (this.fG()) {
            this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
        } else {
            this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
        }

        this.crouchAmountO = this.crouchAmount;
        if (this.isCrouching()) {
            this.crouchAmount += 0.2F;
            if (this.crouchAmount > 3.0F) {
                this.crouchAmount = 3.0F;
            }
        } else {
            this.crouchAmount = 0.0F;
        }

    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return itemstack.a((Tag) TagsItem.FOX_FOOD);
    }

    @Override
    protected void a(EntityHuman entityhuman, EntityInsentient entityinsentient) {
        ((EntityFox) entityinsentient).b(entityhuman.getUniqueID());
    }

    public boolean fy() {
        return this.u(16);
    }

    public void w(boolean flag) {
        this.d(16, flag);
    }

    public boolean fE() {
        return this.jumping;
    }

    public boolean fF() {
        return this.crouchAmount == 3.0F;
    }

    public void setCrouching(boolean flag) {
        this.d(4, flag);
    }

    @Override
    public boolean isCrouching() {
        return this.u(4);
    }

    public void y(boolean flag) {
        this.d(8, flag);
    }

    public boolean fG() {
        return this.u(8);
    }

    public float z(float f) {
        return MathHelper.h(f, this.interestedAngleO, this.interestedAngle) * 0.11F * 3.1415927F;
    }

    public float A(float f) {
        return MathHelper.h(f, this.crouchAmountO, this.crouchAmount);
    }

    @Override
    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        if (this.fJ() && entityliving == null) {
            this.A(false);
        }

        super.setGoalTarget(entityliving);
    }

    @Override
    protected int d(float f, float f1) {
        return MathHelper.f((f - 5.0F) * f1);
    }

    void fK() {
        this.setSleeping(false);
    }

    void fL() {
        this.y(false);
        this.setCrouching(false);
        this.setSitting(false);
        this.setSleeping(false);
        this.A(false);
        this.z(false);
    }

    boolean fM() {
        return !this.isSleeping() && !this.isSitting() && !this.fx();
    }

    @Override
    public void K() {
        SoundEffect soundeffect = this.getSoundAmbient();

        if (soundeffect == SoundEffects.FOX_SCREECH) {
            this.playSound(soundeffect, 2.0F, this.ep());
        } else {
            super.K();
        }

    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        if (this.isSleeping()) {
            return SoundEffects.FOX_SLEEP;
        } else {
            if (!this.level.isDay() && this.random.nextFloat() < 0.1F) {
                List<EntityHuman> list = this.level.a(EntityHuman.class, this.getBoundingBox().grow(16.0D, 16.0D, 16.0D), IEntitySelector.NO_SPECTATORS);

                if (list.isEmpty()) {
                    return SoundEffects.FOX_SCREECH;
                }
            }

            return SoundEffects.FOX_AMBIENT;
        }
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.FOX_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.FOX_DEATH;
    }

    boolean c(UUID uuid) {
        return this.fI().contains(uuid);
    }

    @Override
    protected void f(DamageSource damagesource) {
        ItemStack itemstack = this.getEquipment(EnumItemSlot.MAINHAND);

        if (!itemstack.isEmpty()) {
            this.b(itemstack);
            this.setSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
        }

        super.f(damagesource);
    }

    public static boolean a(EntityFox entityfox, EntityLiving entityliving) {
        double d0 = entityliving.locZ() - entityfox.locZ();
        double d1 = entityliving.locX() - entityfox.locX();
        double d2 = d0 / d1;
        boolean flag = true;

        for (int i = 0; i < 6; ++i) {
            double d3 = d2 == 0.0D ? 0.0D : d0 * (double) ((float) i / 6.0F);
            double d4 = d2 == 0.0D ? d1 * (double) ((float) i / 6.0F) : d3 / d2;

            for (int j = 1; j < 4; ++j) {
                if (!entityfox.level.getType(new BlockPosition(entityfox.locX() + d4, entityfox.locY() + (double) j, entityfox.locZ() + d3)).getMaterial().isReplaceable()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Vec3D cu() {
        return new Vec3D(0.0D, (double) (0.55F * this.getHeadHeight()), (double) (this.getWidth() * 0.4F));
    }

    public class k extends ControllerLook {

        public k() {
            super(EntityFox.this);
        }

        @Override
        public void a() {
            if (!EntityFox.this.isSleeping()) {
                super.a();
            }

        }

        @Override
        protected boolean c() {
            return !EntityFox.this.fy() && !EntityFox.this.isCrouching() && !EntityFox.this.fG() && !EntityFox.this.fx();
        }
    }

    private class m extends ControllerMove {

        public m() {
            super(EntityFox.this);
        }

        @Override
        public void a() {
            if (EntityFox.this.fM()) {
                super.a();
            }

        }
    }

    private class g extends PathfinderGoalFloat {

        public g() {
            super(EntityFox.this);
        }

        @Override
        public void c() {
            super.c();
            EntityFox.this.fL();
        }

        @Override
        public boolean a() {
            return EntityFox.this.isInWater() && EntityFox.this.b((Tag) TagsFluid.WATER) > 0.25D || EntityFox.this.aX();
        }
    }

    private class b extends PathfinderGoal {

        int countdown;

        public b() {
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK, PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            return EntityFox.this.fx();
        }

        @Override
        public boolean b() {
            return this.a() && this.countdown > 0;
        }

        @Override
        public void c() {
            this.countdown = 40;
        }

        @Override
        public void d() {
            EntityFox.this.z(false);
        }

        @Override
        public void e() {
            --this.countdown;
        }
    }

    private class n extends PathfinderGoalPanic {

        public n(double d0) {
            super(EntityFox.this, d0);
        }

        @Override
        public boolean a() {
            return !EntityFox.this.fJ() && super.a();
        }
    }

    private class e extends PathfinderGoalBreed {

        public e(double d0) {
            super(EntityFox.this, d0);
        }

        @Override
        public void c() {
            ((EntityFox) this.animal).fL();
            ((EntityFox) this.partner).fL();
            super.c();
        }

        @Override
        protected void g() {
            WorldServer worldserver = (WorldServer) this.level;
            EntityFox entityfox = (EntityFox) this.animal.createChild(worldserver, this.partner);

            if (entityfox != null) {
                EntityPlayer entityplayer = this.animal.getBreedCause();
                EntityPlayer entityplayer1 = this.partner.getBreedCause();
                EntityPlayer entityplayer2 = entityplayer;

                if (entityplayer != null) {
                    entityfox.b(entityplayer.getUniqueID());
                } else {
                    entityplayer2 = entityplayer1;
                }

                if (entityplayer1 != null && entityplayer != entityplayer1) {
                    entityfox.b(entityplayer1.getUniqueID());
                }

                if (entityplayer2 != null) {
                    entityplayer2.a(StatisticList.ANIMALS_BRED);
                    CriterionTriggers.BRED_ANIMALS.a(entityplayer2, this.animal, this.partner, (EntityAgeable) entityfox);
                }

                this.animal.setAgeRaw(6000);
                this.partner.setAgeRaw(6000);
                this.animal.resetLove();
                this.partner.resetLove();
                entityfox.setAgeRaw(-24000);
                entityfox.setPositionRotation(this.animal.locX(), this.animal.locY(), this.animal.locZ(), 0.0F, 0.0F);
                worldserver.addAllEntities(entityfox);
                this.level.broadcastEntityEffect(this.animal, (byte) 18);
                if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    this.level.addEntity(new EntityExperienceOrb(this.level, this.animal.locX(), this.animal.locY(), this.animal.locZ(), this.animal.getRandom().nextInt(7) + 1));
                }

            }
        }
    }

    private class u extends PathfinderGoal {

        public u() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            if (EntityFox.this.isSleeping()) {
                return false;
            } else {
                EntityLiving entityliving = EntityFox.this.getGoalTarget();

                return entityliving != null && entityliving.isAlive() && EntityFox.STALKABLE_PREY.test(entityliving) && EntityFox.this.f((Entity) entityliving) > 36.0D && !EntityFox.this.isCrouching() && !EntityFox.this.fG() && !EntityFox.this.jumping;
            }
        }

        @Override
        public void c() {
            EntityFox.this.setSitting(false);
            EntityFox.this.z(false);
        }

        @Override
        public void d() {
            EntityLiving entityliving = EntityFox.this.getGoalTarget();

            if (entityliving != null && EntityFox.a((EntityFox) EntityFox.this, entityliving)) {
                EntityFox.this.y(true);
                EntityFox.this.setCrouching(true);
                EntityFox.this.getNavigation().o();
                EntityFox.this.getControllerLook().a(entityliving, (float) EntityFox.this.fa(), (float) EntityFox.this.eZ());
            } else {
                EntityFox.this.y(false);
                EntityFox.this.setCrouching(false);
            }

        }

        @Override
        public void e() {
            EntityLiving entityliving = EntityFox.this.getGoalTarget();

            EntityFox.this.getControllerLook().a(entityliving, (float) EntityFox.this.fa(), (float) EntityFox.this.eZ());
            if (EntityFox.this.f((Entity) entityliving) <= 36.0D) {
                EntityFox.this.y(true);
                EntityFox.this.setCrouching(true);
                EntityFox.this.getNavigation().o();
            } else {
                EntityFox.this.getNavigation().a((Entity) entityliving, 1.5D);
            }

        }
    }

    public class o extends PathfinderGoalWaterJumpAbstract {

        public o() {}

        @Override
        public boolean a() {
            if (!EntityFox.this.fF()) {
                return false;
            } else {
                EntityLiving entityliving = EntityFox.this.getGoalTarget();

                if (entityliving != null && entityliving.isAlive()) {
                    if (entityliving.getAdjustedDirection() != entityliving.getDirection()) {
                        return false;
                    } else {
                        boolean flag = EntityFox.a((EntityFox) EntityFox.this, entityliving);

                        if (!flag) {
                            EntityFox.this.getNavigation().a((Entity) entityliving, 0);
                            EntityFox.this.setCrouching(false);
                            EntityFox.this.y(false);
                        }

                        return flag;
                    }
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean b() {
            EntityLiving entityliving = EntityFox.this.getGoalTarget();

            if (entityliving != null && entityliving.isAlive()) {
                double d0 = EntityFox.this.getMot().y;

                return (d0 * d0 >= 0.05000000074505806D || Math.abs(EntityFox.this.getXRot()) >= 15.0F || !EntityFox.this.onGround) && !EntityFox.this.fx();
            } else {
                return false;
            }
        }

        @Override
        public boolean C_() {
            return false;
        }

        @Override
        public void c() {
            EntityFox.this.setJumping(true);
            EntityFox.this.w(true);
            EntityFox.this.y(false);
            EntityLiving entityliving = EntityFox.this.getGoalTarget();

            EntityFox.this.getControllerLook().a(entityliving, 60.0F, 30.0F);
            Vec3D vec3d = (new Vec3D(entityliving.locX() - EntityFox.this.locX(), entityliving.locY() - EntityFox.this.locY(), entityliving.locZ() - EntityFox.this.locZ())).d();

            EntityFox.this.setMot(EntityFox.this.getMot().add(vec3d.x * 0.8D, 0.9D, vec3d.z * 0.8D));
            EntityFox.this.getNavigation().o();
        }

        @Override
        public void d() {
            EntityFox.this.setCrouching(false);
            EntityFox.this.crouchAmount = 0.0F;
            EntityFox.this.crouchAmountO = 0.0F;
            EntityFox.this.y(false);
            EntityFox.this.w(false);
        }

        @Override
        public void e() {
            EntityLiving entityliving = EntityFox.this.getGoalTarget();

            if (entityliving != null) {
                EntityFox.this.getControllerLook().a(entityliving, 60.0F, 30.0F);
            }

            if (!EntityFox.this.fx()) {
                Vec3D vec3d = EntityFox.this.getMot();

                if (vec3d.y * vec3d.y < 0.029999999329447746D && EntityFox.this.getXRot() != 0.0F) {
                    EntityFox.this.setXRot(MathHelper.k(EntityFox.this.getXRot(), 0.0F, 0.2F));
                } else {
                    double d0 = vec3d.h();
                    double d1 = Math.signum(-vec3d.y) * Math.acos(d0 / vec3d.f()) * 57.2957763671875D;

                    EntityFox.this.setXRot((float) d1);
                }
            }

            if (entityliving != null && EntityFox.this.e((Entity) entityliving) <= 2.0F) {
                EntityFox.this.attackEntity(entityliving);
            } else if (EntityFox.this.getXRot() > 0.0F && EntityFox.this.onGround && (float) EntityFox.this.getMot().y != 0.0F && EntityFox.this.level.getType(EntityFox.this.getChunkCoordinates()).a(Blocks.SNOW)) {
                EntityFox.this.setXRot(60.0F);
                EntityFox.this.setGoalTarget((EntityLiving) null);
                EntityFox.this.z(true);
            }

        }
    }

    private class s extends PathfinderGoalFleeSun {

        private int interval = 100;

        public s(double d0) {
            super(EntityFox.this, d0);
        }

        @Override
        public boolean a() {
            if (!EntityFox.this.isSleeping() && this.mob.getGoalTarget() == null) {
                if (EntityFox.this.level.Y()) {
                    return true;
                } else if (this.interval > 0) {
                    --this.interval;
                    return false;
                } else {
                    this.interval = 100;
                    BlockPosition blockposition = this.mob.getChunkCoordinates();

                    return EntityFox.this.level.isDay() && EntityFox.this.level.g(blockposition) && !((WorldServer) EntityFox.this.level).b(blockposition) && this.g();
                }
            } else {
                return false;
            }
        }

        @Override
        public void c() {
            EntityFox.this.fL();
            super.c();
        }
    }

    private class l extends PathfinderGoalMeleeAttack {

        public l(double d0, boolean flag) {
            super(EntityFox.this, d0, flag);
        }

        @Override
        protected void a(EntityLiving entityliving, double d0) {
            double d1 = this.a(entityliving);

            if (d0 <= d1 && this.h()) {
                this.g();
                this.mob.attackEntity(entityliving);
                EntityFox.this.playSound(SoundEffects.FOX_BITE, 1.0F, 1.0F);
            }

        }

        @Override
        public void c() {
            EntityFox.this.y(false);
            super.c();
        }

        @Override
        public boolean a() {
            return !EntityFox.this.isSitting() && !EntityFox.this.isSleeping() && !EntityFox.this.isCrouching() && !EntityFox.this.fx() && super.a();
        }
    }

    private class t extends EntityFox.d {

        private static final int WAIT_TIME_BEFORE_SLEEP = 140;
        private int countdown;

        public t() {
            super();
            this.countdown = EntityFox.this.random.nextInt(140);
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK, PathfinderGoal.Type.JUMP));
        }

        @Override
        public boolean a() {
            return EntityFox.this.xxa == 0.0F && EntityFox.this.yya == 0.0F && EntityFox.this.zza == 0.0F ? this.j() || EntityFox.this.isSleeping() : false;
        }

        @Override
        public boolean b() {
            return this.j();
        }

        private boolean j() {
            if (this.countdown > 0) {
                --this.countdown;
                return false;
            } else {
                return EntityFox.this.level.isDay() && this.g() && !this.h() && !EntityFox.this.isInPowderSnow;
            }
        }

        @Override
        public void d() {
            this.countdown = EntityFox.this.random.nextInt(140);
            EntityFox.this.fL();
        }

        @Override
        public void c() {
            EntityFox.this.setSitting(false);
            EntityFox.this.setCrouching(false);
            EntityFox.this.y(false);
            EntityFox.this.setJumping(false);
            EntityFox.this.setSleeping(true);
            EntityFox.this.getNavigation().o();
            EntityFox.this.getControllerMove().a(EntityFox.this.locX(), EntityFox.this.locY(), EntityFox.this.locZ(), 0.0D);
        }
    }

    private class h extends PathfinderGoalFollowParent {

        private final EntityFox fox;

        public h(EntityFox entityfox, double d0) {
            super(entityfox, d0);
            this.fox = entityfox;
        }

        @Override
        public boolean a() {
            return !this.fox.fJ() && super.a();
        }

        @Override
        public boolean b() {
            return !this.fox.fJ() && super.b();
        }

        @Override
        public void c() {
            this.fox.fL();
            super.c();
        }
    }

    private class q extends PathfinderGoalNearestVillage {

        public q(int i, int j) {
            super(EntityFox.this, j);
        }

        @Override
        public void c() {
            EntityFox.this.fL();
            super.c();
        }

        @Override
        public boolean a() {
            return super.a() && this.g();
        }

        @Override
        public boolean b() {
            return super.b() && this.g();
        }

        private boolean g() {
            return !EntityFox.this.isSleeping() && !EntityFox.this.isSitting() && !EntityFox.this.fJ() && EntityFox.this.getGoalTarget() == null;
        }
    }

    public class f extends PathfinderGoalGotoTarget {

        private static final int WAIT_TICKS = 40;
        protected int ticksWaited;

        public f(double d0, int i, int j) {
            super(EntityFox.this, d0, i, j);
        }

        @Override
        public double h() {
            return 2.0D;
        }

        @Override
        public boolean k() {
            return this.tryTicks % 100 == 0;
        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            IBlockData iblockdata = iworldreader.getType(blockposition);

            return iblockdata.a(Blocks.SWEET_BERRY_BUSH) && (Integer) iblockdata.get(BlockSweetBerryBush.AGE) >= 2 || CaveVines.a(iblockdata);
        }

        @Override
        public void e() {
            if (this.l()) {
                if (this.ticksWaited >= 40) {
                    this.n();
                } else {
                    ++this.ticksWaited;
                }
            } else if (!this.l() && EntityFox.this.random.nextFloat() < 0.05F) {
                EntityFox.this.playSound(SoundEffects.FOX_SNIFF, 1.0F, 1.0F);
            }

            super.e();
        }

        protected void n() {
            if (EntityFox.this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                IBlockData iblockdata = EntityFox.this.level.getType(this.blockPos);

                if (iblockdata.a(Blocks.SWEET_BERRY_BUSH)) {
                    this.b(iblockdata);
                } else if (CaveVines.a(iblockdata)) {
                    this.a(iblockdata);
                }

            }
        }

        private void a(IBlockData iblockdata) {
            CaveVines.harvest(iblockdata, EntityFox.this.level, this.blockPos);
        }

        private void b(IBlockData iblockdata) {
            int i = (Integer) iblockdata.get(BlockSweetBerryBush.AGE);

            iblockdata.set(BlockSweetBerryBush.AGE, 1);
            int j = 1 + EntityFox.this.level.random.nextInt(2) + (i == 3 ? 1 : 0);
            ItemStack itemstack = EntityFox.this.getEquipment(EnumItemSlot.MAINHAND);

            if (itemstack.isEmpty()) {
                EntityFox.this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
                --j;
            }

            if (j > 0) {
                Block.a(EntityFox.this.level, this.blockPos, new ItemStack(Items.SWEET_BERRIES, j));
            }

            EntityFox.this.playSound(SoundEffects.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
            EntityFox.this.level.setTypeAndData(this.blockPos, (IBlockData) iblockdata.set(BlockSweetBerryBush.AGE, 1), 2);
        }

        @Override
        public boolean a() {
            return !EntityFox.this.isSleeping() && super.a();
        }

        @Override
        public void c() {
            this.ticksWaited = 0;
            EntityFox.this.setSitting(false);
            super.c();
        }
    }

    private class p extends PathfinderGoal {

        public p() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            if (!EntityFox.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
                return false;
            } else if (EntityFox.this.getGoalTarget() == null && EntityFox.this.getLastDamager() == null) {
                if (!EntityFox.this.fM()) {
                    return false;
                } else if (EntityFox.this.getRandom().nextInt(10) != 0) {
                    return false;
                } else {
                    List<EntityItem> list = EntityFox.this.level.a(EntityItem.class, EntityFox.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityFox.ALLOWED_ITEMS);

                    return !list.isEmpty() && EntityFox.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty();
                }
            } else {
                return false;
            }
        }

        @Override
        public void e() {
            List<EntityItem> list = EntityFox.this.level.a(EntityItem.class, EntityFox.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityFox.ALLOWED_ITEMS);
            ItemStack itemstack = EntityFox.this.getEquipment(EnumItemSlot.MAINHAND);

            if (itemstack.isEmpty() && !list.isEmpty()) {
                EntityFox.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
            }

        }

        @Override
        public void c() {
            List<EntityItem> list = EntityFox.this.level.a(EntityItem.class, EntityFox.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityFox.ALLOWED_ITEMS);

            if (!list.isEmpty()) {
                EntityFox.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
            }

        }
    }

    private class j extends PathfinderGoalLookAtPlayer {

        public j(EntityInsentient entityinsentient, Class oclass, float f) {
            super(entityinsentient, oclass, f);
        }

        @Override
        public boolean a() {
            return super.a() && !EntityFox.this.fx() && !EntityFox.this.fG();
        }

        @Override
        public boolean b() {
            return super.b() && !EntityFox.this.fx() && !EntityFox.this.fG();
        }
    }

    private class r extends EntityFox.d {

        private double relX;
        private double relZ;
        private int lookTime;
        private int looksRemaining;

        public r() {
            super();
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            return EntityFox.this.getLastDamager() == null && EntityFox.this.getRandom().nextFloat() < 0.02F && !EntityFox.this.isSleeping() && EntityFox.this.getGoalTarget() == null && EntityFox.this.getNavigation().m() && !this.h() && !EntityFox.this.fy() && !EntityFox.this.isCrouching();
        }

        @Override
        public boolean b() {
            return this.looksRemaining > 0;
        }

        @Override
        public void c() {
            this.j();
            this.looksRemaining = 2 + EntityFox.this.getRandom().nextInt(3);
            EntityFox.this.setSitting(true);
            EntityFox.this.getNavigation().o();
        }

        @Override
        public void d() {
            EntityFox.this.setSitting(false);
        }

        @Override
        public void e() {
            --this.lookTime;
            if (this.lookTime <= 0) {
                --this.looksRemaining;
                this.j();
            }

            EntityFox.this.getControllerLook().a(EntityFox.this.locX() + this.relX, EntityFox.this.getHeadY(), EntityFox.this.locZ() + this.relZ, (float) EntityFox.this.fa(), (float) EntityFox.this.eZ());
        }

        private void j() {
            double d0 = 6.283185307179586D * EntityFox.this.getRandom().nextDouble();

            this.relX = Math.cos(d0);
            this.relZ = Math.sin(d0);
            this.lookTime = 80 + EntityFox.this.getRandom().nextInt(20);
        }
    }

    private class a extends PathfinderGoalNearestAttackableTarget<EntityLiving> {

        @Nullable
        private EntityLiving trustedLastHurtBy;
        private EntityLiving trustedLastHurt;
        private int timestamp;

        public a(Class oclass, boolean flag, boolean flag1, @Nullable Predicate predicate) {
            super(EntityFox.this, oclass, 10, flag, flag1, predicate);
        }

        @Override
        public boolean a() {
            if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
                return false;
            } else {
                Iterator iterator = EntityFox.this.fI().iterator();

                while (iterator.hasNext()) {
                    UUID uuid = (UUID) iterator.next();

                    if (uuid != null && EntityFox.this.level instanceof WorldServer) {
                        Entity entity = ((WorldServer) EntityFox.this.level).getEntity(uuid);

                        if (entity instanceof EntityLiving) {
                            EntityLiving entityliving = (EntityLiving) entity;

                            this.trustedLastHurt = entityliving;
                            this.trustedLastHurtBy = entityliving.getLastDamager();
                            int i = entityliving.dH();

                            return i != this.timestamp && this.a(this.trustedLastHurtBy, this.targetConditions);
                        }
                    }
                }

                return false;
            }
        }

        @Override
        public void c() {
            this.a(this.trustedLastHurtBy);
            this.target = this.trustedLastHurtBy;
            if (this.trustedLastHurt != null) {
                this.timestamp = this.trustedLastHurt.dH();
            }

            EntityFox.this.playSound(SoundEffects.FOX_AGGRO, 1.0F, 1.0F);
            EntityFox.this.A(true);
            EntityFox.this.fK();
            super.c();
        }
    }

    public static enum Type {

        RED(0, "red", new ResourceKey[]{Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.GIANT_SPRUCE_TAIGA_HILLS}), SNOW(1, "snow", new ResourceKey[]{Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS});

        private static final EntityFox.Type[] BY_ID = (EntityFox.Type[]) Arrays.stream(values()).sorted(Comparator.comparingInt(EntityFox.Type::b)).toArray((i) -> {
            return new EntityFox.Type[i];
        });
        private static final Map<String, EntityFox.Type> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(EntityFox.Type::a, (entityfox_type) -> {
            return entityfox_type;
        }));
        private final int id;
        private final String name;
        private final List<ResourceKey<BiomeBase>> biomes;

        private Type(int i, String s, ResourceKey... aresourcekey) {
            this.id = i;
            this.name = s;
            this.biomes = Arrays.asList(aresourcekey);
        }

        public String a() {
            return this.name;
        }

        public int b() {
            return this.id;
        }

        public static EntityFox.Type a(String s) {
            return (EntityFox.Type) EntityFox.Type.BY_NAME.getOrDefault(s, EntityFox.Type.RED);
        }

        public static EntityFox.Type a(int i) {
            if (i < 0 || i > EntityFox.Type.BY_ID.length) {
                i = 0;
            }

            return EntityFox.Type.BY_ID[i];
        }

        public static EntityFox.Type a(Optional<ResourceKey<BiomeBase>> optional) {
            return optional.isPresent() && EntityFox.Type.SNOW.biomes.contains(optional.get()) ? EntityFox.Type.SNOW : EntityFox.Type.RED;
        }
    }

    public static class i extends EntityAgeable.a {

        public final EntityFox.Type type;

        public i(EntityFox.Type entityfox_type) {
            super(false);
            this.type = entityfox_type;
        }
    }

    private abstract class d extends PathfinderGoal {

        private final PathfinderTargetCondition alertableTargeting = PathfinderTargetCondition.a().a(12.0D).d().a(EntityFox.this.new c());

        d() {}

        protected boolean g() {
            BlockPosition blockposition = new BlockPosition(EntityFox.this.locX(), EntityFox.this.getBoundingBox().maxY, EntityFox.this.locZ());

            return !EntityFox.this.level.g(blockposition) && EntityFox.this.f(blockposition) >= 0.0F;
        }

        protected boolean h() {
            return !EntityFox.this.level.a(EntityLiving.class, this.alertableTargeting, (EntityLiving) EntityFox.this, EntityFox.this.getBoundingBox().grow(12.0D, 6.0D, 12.0D)).isEmpty();
        }
    }

    public class c implements Predicate<EntityLiving> {

        public c() {}

        public boolean test(EntityLiving entityliving) {
            return entityliving instanceof EntityFox ? false : (!(entityliving instanceof EntityChicken) && !(entityliving instanceof EntityRabbit) && !(entityliving instanceof EntityMonster) ? (entityliving instanceof EntityTameableAnimal ? !((EntityTameableAnimal) entityliving).isTamed() : (entityliving instanceof EntityHuman && (entityliving.isSpectator() || ((EntityHuman) entityliving).isCreative()) ? false : (EntityFox.this.c(entityliving.getUniqueID()) ? false : !entityliving.isSleeping() && !entityliving.bG()))) : true);
        }
    }
}
