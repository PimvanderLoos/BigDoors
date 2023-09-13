package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.util.TimeRange;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.IEntityAngerable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.control.ControllerMoveFlying;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowParent;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalUniversalAngerReset;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceRecord;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCrops;
import net.minecraft.world.level.block.BlockStem;
import net.minecraft.world.level.block.BlockSweetBerryBush;
import net.minecraft.world.level.block.BlockTallPlant;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IBlockFragilePlantElement;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBeehive;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyDoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityBee extends EntityAnimal implements IEntityAngerable, EntityBird {

    public static final float FLAP_DEGREES_PER_TICK = 120.32113F;
    public static final int TICKS_PER_FLAP = MathHelper.f(1.4959966F);
    private static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.a(EntityBee.class, DataWatcherRegistry.BYTE);
    private static final DataWatcherObject<Integer> DATA_REMAINING_ANGER_TIME = DataWatcher.a(EntityBee.class, DataWatcherRegistry.INT);
    private static final int FLAG_ROLL = 2;
    private static final int FLAG_HAS_STUNG = 4;
    private static final int FLAG_HAS_NECTAR = 8;
    private static final int STING_DEATH_COUNTDOWN = 1200;
    private static final int TICKS_BEFORE_GOING_TO_KNOWN_FLOWER = 2400;
    private static final int TICKS_WITHOUT_NECTAR_BEFORE_GOING_HOME = 3600;
    private static final int MIN_ATTACK_DIST = 4;
    private static final int MAX_CROPS_GROWABLE = 10;
    private static final int POISON_SECONDS_NORMAL = 10;
    private static final int POISON_SECONDS_HARD = 18;
    private static final int TOO_FAR_DISTANCE = 32;
    private static final int HIVE_CLOSE_ENOUGH_DISTANCE = 2;
    private static final int PATHFIND_TO_HIVE_WHEN_CLOSER_THAN = 16;
    private static final int HIVE_SEARCH_DISTANCE = 20;
    public static final String TAG_CROPS_GROWN_SINCE_POLLINATION = "CropsGrownSincePollination";
    public static final String TAG_CANNOT_ENTER_HIVE_TICKS = "CannotEnterHiveTicks";
    public static final String TAG_TICKS_SINCE_POLLINATION = "TicksSincePollination";
    public static final String TAG_HAS_STUNG = "HasStung";
    public static final String TAG_HAS_NECTAR = "HasNectar";
    public static final String TAG_FLOWER_POS = "FlowerPos";
    public static final String TAG_HIVE_POS = "HivePos";
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeRange.a(20, 39);
    private UUID persistentAngerTarget;
    private float rollAmount;
    private float rollAmountO;
    private int timeSinceSting;
    int ticksWithoutNectarSinceExitingHive;
    public int stayOutOfHiveCountdown;
    private int numCropsGrownSincePollination;
    private static final int COOLDOWN_BEFORE_LOCATING_NEW_HIVE = 200;
    int remainingCooldownBeforeLocatingNewHive;
    private static final int COOLDOWN_BEFORE_LOCATING_NEW_FLOWER = 200;
    int remainingCooldownBeforeLocatingNewFlower;
    @Nullable
    BlockPosition savedFlowerPos;
    @Nullable
    public BlockPosition hivePos;
    EntityBee.k beePollinateGoal;
    EntityBee.e goToHiveGoal;
    private EntityBee.f goToKnownFlowerGoal;
    private int underWaterTicks;

    public EntityBee(EntityTypes<? extends EntityBee> entitytypes, World world) {
        super(entitytypes, world);
        this.remainingCooldownBeforeLocatingNewFlower = MathHelper.nextInt(this.random, 20, 60);
        this.moveControl = new ControllerMoveFlying(this, 20, true);
        this.lookControl = new EntityBee.j(this);
        this.a(PathType.DANGER_FIRE, -1.0F);
        this.a(PathType.WATER, -1.0F);
        this.a(PathType.WATER_BORDER, 16.0F);
        this.a(PathType.COCOA, -1.0F);
        this.a(PathType.FENCE, -1.0F);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityBee.DATA_FLAGS_ID, (byte) 0);
        this.entityData.register(EntityBee.DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getType(blockposition).isAir() ? 10.0F : 0.0F;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new EntityBee.b(this, 1.399999976158142D, true));
        this.goalSelector.a(1, new EntityBee.d());
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.25D, RecipeItemStack.a((Tag) TagsItem.FLOWERS), false));
        this.beePollinateGoal = new EntityBee.k();
        this.goalSelector.a(4, this.beePollinateGoal);
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(5, new EntityBee.i());
        this.goToHiveGoal = new EntityBee.e();
        this.goalSelector.a(5, this.goToHiveGoal);
        this.goToKnownFlowerGoal = new EntityBee.f();
        this.goalSelector.a(6, this.goToKnownFlowerGoal);
        this.goalSelector.a(7, new EntityBee.g());
        this.goalSelector.a(8, new EntityBee.l());
        this.goalSelector.a(9, new PathfinderGoalFloat(this));
        this.targetSelector.a(1, (new EntityBee.h(this)).a(new Class[0]));
        this.targetSelector.a(2, new EntityBee.c(this));
        this.targetSelector.a(3, new PathfinderGoalUniversalAngerReset<>(this, true));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.hasHivePos()) {
            nbttagcompound.set("HivePos", GameProfileSerializer.a(this.getHivePos()));
        }

        if (this.hasFlowerPos()) {
            nbttagcompound.set("FlowerPos", GameProfileSerializer.a(this.getFlowerPos()));
        }

        nbttagcompound.setBoolean("HasNectar", this.hasNectar());
        nbttagcompound.setBoolean("HasStung", this.hasStung());
        nbttagcompound.setInt("TicksSincePollination", this.ticksWithoutNectarSinceExitingHive);
        nbttagcompound.setInt("CannotEnterHiveTicks", this.stayOutOfHiveCountdown);
        nbttagcompound.setInt("CropsGrownSincePollination", this.numCropsGrownSincePollination);
        this.c(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        this.hivePos = null;
        if (nbttagcompound.hasKey("HivePos")) {
            this.hivePos = GameProfileSerializer.b(nbttagcompound.getCompound("HivePos"));
        }

        this.savedFlowerPos = null;
        if (nbttagcompound.hasKey("FlowerPos")) {
            this.savedFlowerPos = GameProfileSerializer.b(nbttagcompound.getCompound("FlowerPos"));
        }

        super.loadData(nbttagcompound);
        this.setHasNectar(nbttagcompound.getBoolean("HasNectar"));
        this.setHasStung(nbttagcompound.getBoolean("HasStung"));
        this.ticksWithoutNectarSinceExitingHive = nbttagcompound.getInt("TicksSincePollination");
        this.stayOutOfHiveCountdown = nbttagcompound.getInt("CannotEnterHiveTicks");
        this.numCropsGrownSincePollination = nbttagcompound.getInt("CropsGrownSincePollination");
        this.a(this.level, nbttagcompound);
    }

    @Override
    public boolean attackEntity(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.b(this), (float) ((int) this.b(GenericAttributes.ATTACK_DAMAGE)));

        if (flag) {
            this.a((EntityLiving) this, entity);
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).r(((EntityLiving) entity).eh() + 1);
                byte b0 = 0;

                if (this.level.getDifficulty() == EnumDifficulty.NORMAL) {
                    b0 = 10;
                } else if (this.level.getDifficulty() == EnumDifficulty.HARD) {
                    b0 = 18;
                }

                if (b0 > 0) {
                    ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.POISON, b0 * 20, 0), this);
                }
            }

            this.setHasStung(true);
            this.pacify();
            this.playSound(SoundEffects.BEE_STING, 1.0F, 1.0F);
        }

        return flag;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasNectar() && this.getNumCropsGrownSincePollination() < 10 && this.random.nextFloat() < 0.05F) {
            for (int i = 0; i < this.random.nextInt(2) + 1; ++i) {
                this.a(this.level, this.locX() - 0.30000001192092896D, this.locX() + 0.30000001192092896D, this.locZ() - 0.30000001192092896D, this.locZ() + 0.30000001192092896D, this.e(0.5D), Particles.FALLING_NECTAR);
            }
        }

        this.fO();
    }

    private void a(World world, double d0, double d1, double d2, double d3, double d4, ParticleParam particleparam) {
        world.addParticle(particleparam, MathHelper.d(world.random.nextDouble(), d0, d1), d4, MathHelper.d(world.random.nextDouble(), d2, d3), 0.0D, 0.0D, 0.0D);
    }

    void h(BlockPosition blockposition) {
        Vec3D vec3d = Vec3D.c((BaseBlockPosition) blockposition);
        byte b0 = 0;
        BlockPosition blockposition1 = this.getChunkCoordinates();
        int i = (int) vec3d.y - blockposition1.getY();

        if (i > 2) {
            b0 = 4;
        } else if (i < -2) {
            b0 = -4;
        }

        int j = 6;
        int k = 8;
        int l = blockposition1.k(blockposition);

        if (l < 15) {
            j = l / 2;
            k = l / 2;
        }

        Vec3D vec3d1 = AirRandomPos.a(this, j, k, b0, vec3d, 0.3141592741012573D);

        if (vec3d1 != null) {
            this.navigation.a(0.5F);
            this.navigation.a(vec3d1.x, vec3d1.y, vec3d1.z, 1.0D);
        }
    }

    @Nullable
    public BlockPosition getFlowerPos() {
        return this.savedFlowerPos;
    }

    public boolean hasFlowerPos() {
        return this.savedFlowerPos != null;
    }

    public void setFlowerPos(BlockPosition blockposition) {
        this.savedFlowerPos = blockposition;
    }

    @VisibleForDebug
    public int fw() {
        return Math.max(this.goToHiveGoal.travellingTicks, this.goToKnownFlowerGoal.travellingTicks);
    }

    @VisibleForDebug
    public List<BlockPosition> fx() {
        return this.goToHiveGoal.blacklistedTargets;
    }

    private boolean canPollinate() {
        return this.ticksWithoutNectarSinceExitingHive > 3600;
    }

    boolean fN() {
        if (this.stayOutOfHiveCountdown <= 0 && !this.beePollinateGoal.k() && !this.hasStung() && this.getGoalTarget() == null) {
            boolean flag = this.canPollinate() || this.level.isRaining() || this.level.isNight() || this.hasNectar();

            return flag && !this.fP();
        } else {
            return false;
        }
    }

    public void setCannotEnterHiveTicks(int i) {
        this.stayOutOfHiveCountdown = i;
    }

    public float z(float f) {
        return MathHelper.h(f, this.rollAmountO, this.rollAmount);
    }

    private void fO() {
        this.rollAmountO = this.rollAmount;
        if (this.fU()) {
            this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
        } else {
            this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
        }

    }

    @Override
    protected void mobTick() {
        boolean flag = this.hasStung();

        if (this.aO()) {
            ++this.underWaterTicks;
        } else {
            this.underWaterTicks = 0;
        }

        if (this.underWaterTicks > 20) {
            this.damageEntity(DamageSource.DROWN, 1.0F);
        }

        if (flag) {
            ++this.timeSinceSting;
            if (this.timeSinceSting % 5 == 0 && this.random.nextInt(MathHelper.clamp(1200 - this.timeSinceSting, 1, 1200)) == 0) {
                this.damageEntity(DamageSource.GENERIC, this.getHealth());
            }
        }

        if (!this.hasNectar()) {
            ++this.ticksWithoutNectarSinceExitingHive;
        }

        if (!this.level.isClientSide) {
            this.a((WorldServer) this.level, false);
        }

    }

    public void fy() {
        this.ticksWithoutNectarSinceExitingHive = 0;
    }

    private boolean fP() {
        if (this.hivePos == null) {
            return false;
        } else {
            TileEntity tileentity = this.level.getTileEntity(this.hivePos);

            return tileentity instanceof TileEntityBeehive && ((TileEntityBeehive) tileentity).d();
        }
    }

    @Override
    public int getAnger() {
        return (Integer) this.entityData.get(EntityBee.DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setAnger(int i) {
        this.entityData.set(EntityBee.DATA_REMAINING_ANGER_TIME, i);
    }

    @Override
    public UUID getAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    @Override
    public void anger() {
        this.setAnger(EntityBee.PERSISTENT_ANGER_TIME.a(this.random));
    }

    private boolean i(BlockPosition blockposition) {
        TileEntity tileentity = this.level.getTileEntity(blockposition);

        return tileentity instanceof TileEntityBeehive ? !((TileEntityBeehive) tileentity).isFull() : false;
    }

    @VisibleForDebug
    public boolean hasHivePos() {
        return this.hivePos != null;
    }

    @Nullable
    @VisibleForDebug
    public BlockPosition getHivePos() {
        return this.hivePos;
    }

    @VisibleForDebug
    public PathfinderGoalSelector fG() {
        return this.goalSelector;
    }

    @Override
    protected void R() {
        super.R();
        PacketDebug.a(this);
    }

    int getNumCropsGrownSincePollination() {
        return this.numCropsGrownSincePollination;
    }

    private void fR() {
        this.numCropsGrownSincePollination = 0;
    }

    void fS() {
        ++this.numCropsGrownSincePollination;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (!this.level.isClientSide) {
            if (this.stayOutOfHiveCountdown > 0) {
                --this.stayOutOfHiveCountdown;
            }

            if (this.remainingCooldownBeforeLocatingNewHive > 0) {
                --this.remainingCooldownBeforeLocatingNewHive;
            }

            if (this.remainingCooldownBeforeLocatingNewFlower > 0) {
                --this.remainingCooldownBeforeLocatingNewFlower;
            }

            boolean flag = this.isAngry() && !this.hasStung() && this.getGoalTarget() != null && this.getGoalTarget().f((Entity) this) < 4.0D;

            this.x(flag);
            if (this.tickCount % 20 == 0 && !this.fT()) {
                this.hivePos = null;
            }
        }

    }

    boolean fT() {
        if (!this.hasHivePos()) {
            return false;
        } else {
            TileEntity tileentity = this.level.getTileEntity(this.hivePos);

            return tileentity != null && tileentity.getTileType() == TileEntityTypes.BEEHIVE;
        }
    }

    public boolean hasNectar() {
        return this.v(8);
    }

    public void setHasNectar(boolean flag) {
        if (flag) {
            this.fy();
        }

        this.d(8, flag);
    }

    public boolean hasStung() {
        return this.v(4);
    }

    public void setHasStung(boolean flag) {
        this.d(4, flag);
    }

    private boolean fU() {
        return this.v(2);
    }

    private void x(boolean flag) {
        this.d(2, flag);
    }

    boolean j(BlockPosition blockposition) {
        return !this.b(blockposition, 32);
    }

    private void d(int i, boolean flag) {
        if (flag) {
            this.entityData.set(EntityBee.DATA_FLAGS_ID, (byte) ((Byte) this.entityData.get(EntityBee.DATA_FLAGS_ID) | i));
        } else {
            this.entityData.set(EntityBee.DATA_FLAGS_ID, (byte) ((Byte) this.entityData.get(EntityBee.DATA_FLAGS_ID) & ~i));
        }

    }

    private boolean v(int i) {
        return ((Byte) this.entityData.get(EntityBee.DATA_FLAGS_ID) & i) != 0;
    }

    public static AttributeProvider.Builder fJ() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 10.0D).a(GenericAttributes.FLYING_SPEED, 0.6000000238418579D).a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.ATTACK_DAMAGE, 2.0D).a(GenericAttributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected NavigationAbstract a(World world) {
        NavigationFlying navigationflying = new NavigationFlying(this, world) {
            @Override
            public boolean a(BlockPosition blockposition) {
                return !this.level.getType(blockposition.down()).isAir();
            }

            @Override
            public void c() {
                if (!EntityBee.this.beePollinateGoal.k()) {
                    super.c();
                }
            }
        };

        navigationflying.a(false);
        navigationflying.d(false);
        navigationflying.b(true);
        return navigationflying;
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return itemstack.a((Tag) TagsItem.FLOWERS);
    }

    boolean k(BlockPosition blockposition) {
        return this.level.o(blockposition) && this.level.getType(blockposition).a((Tag) TagsBlock.FLOWERS);
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {}

    @Override
    protected SoundEffect getSoundAmbient() {
        return null;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.BEE_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.BEE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public EntityBee createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityBee) EntityTypes.BEE.a((World) worldserver);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? entitysize.height * 0.5F : entitysize.height * 0.5F;
    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    @Override
    public boolean aF() {
        return this.fK() && this.tickCount % EntityBee.TICKS_PER_FLAP == 0;
    }

    @Override
    public boolean fK() {
        return !this.onGround;
    }

    public void fL() {
        this.setHasNectar(false);
        this.fR();
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if (!this.level.isClientSide) {
                this.beePollinateGoal.l();
            }

            return super.damageEntity(damagesource, f);
        }
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ARTHROPOD;
    }

    @Override
    protected void c(Tag<FluidType> tag) {
        this.setMot(this.getMot().add(0.0D, 0.01D, 0.0D));
    }

    @Override
    public Vec3D cu() {
        return new Vec3D(0.0D, (double) (0.5F * this.getHeadHeight()), (double) (this.getWidth() * 0.2F));
    }

    boolean b(BlockPosition blockposition, int i) {
        return blockposition.a((BaseBlockPosition) this.getChunkCoordinates(), (double) i);
    }

    private class k extends EntityBee.a {

        private static final int MIN_POLLINATION_TICKS = 400;
        private static final int MIN_FIND_FLOWER_RETRY_COOLDOWN = 20;
        private static final int MAX_FIND_FLOWER_RETRY_COOLDOWN = 60;
        private final Predicate<IBlockData> VALID_POLLINATION_BLOCKS = (iblockdata) -> {
            return iblockdata.a((Tag) TagsBlock.FLOWERS) ? (iblockdata.a(Blocks.SUNFLOWER) ? iblockdata.get(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.UPPER : true) : false;
        };
        private static final double ARRIVAL_THRESHOLD = 0.1D;
        private static final int POSITION_CHANGE_CHANCE = 25;
        private static final float SPEED_MODIFIER = 0.35F;
        private static final float HOVER_HEIGHT_WITHIN_FLOWER = 0.6F;
        private static final float HOVER_POS_OFFSET = 0.33333334F;
        private int successfulPollinatingTicks;
        private int lastSoundPlayedTick;
        private boolean pollinating;
        private Vec3D hoverPos;
        private int pollinatingTicks;
        private static final int MAX_POLLINATING_TICKS = 600;

        k() {
            super();
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean g() {
            if (EntityBee.this.remainingCooldownBeforeLocatingNewFlower > 0) {
                return false;
            } else if (EntityBee.this.hasNectar()) {
                return false;
            } else if (EntityBee.this.level.isRaining()) {
                return false;
            } else {
                Optional<BlockPosition> optional = this.o();

                if (optional.isPresent()) {
                    EntityBee.this.savedFlowerPos = (BlockPosition) optional.get();
                    EntityBee.this.navigation.a((double) EntityBee.this.savedFlowerPos.getX() + 0.5D, (double) EntityBee.this.savedFlowerPos.getY() + 0.5D, (double) EntityBee.this.savedFlowerPos.getZ() + 0.5D, 1.2000000476837158D);
                    return true;
                } else {
                    EntityBee.this.remainingCooldownBeforeLocatingNewFlower = MathHelper.nextInt(EntityBee.this.random, 20, 60);
                    return false;
                }
            }
        }

        @Override
        public boolean h() {
            if (!this.pollinating) {
                return false;
            } else if (!EntityBee.this.hasFlowerPos()) {
                return false;
            } else if (EntityBee.this.level.isRaining()) {
                return false;
            } else if (this.j()) {
                return EntityBee.this.random.nextFloat() < 0.2F;
            } else if (EntityBee.this.tickCount % 20 == 0 && !EntityBee.this.k(EntityBee.this.savedFlowerPos)) {
                EntityBee.this.savedFlowerPos = null;
                return false;
            } else {
                return true;
            }
        }

        private boolean j() {
            return this.successfulPollinatingTicks > 400;
        }

        boolean k() {
            return this.pollinating;
        }

        void l() {
            this.pollinating = false;
        }

        @Override
        public void c() {
            this.successfulPollinatingTicks = 0;
            this.pollinatingTicks = 0;
            this.lastSoundPlayedTick = 0;
            this.pollinating = true;
            EntityBee.this.fy();
        }

        @Override
        public void d() {
            if (this.j()) {
                EntityBee.this.setHasNectar(true);
            }

            this.pollinating = false;
            EntityBee.this.navigation.o();
            EntityBee.this.remainingCooldownBeforeLocatingNewFlower = 200;
        }

        @Override
        public void e() {
            ++this.pollinatingTicks;
            if (this.pollinatingTicks > 600) {
                EntityBee.this.savedFlowerPos = null;
            } else {
                Vec3D vec3d = Vec3D.c((BaseBlockPosition) EntityBee.this.savedFlowerPos).add(0.0D, 0.6000000238418579D, 0.0D);

                if (vec3d.f(EntityBee.this.getPositionVector()) > 1.0D) {
                    this.hoverPos = vec3d;
                    this.m();
                } else {
                    if (this.hoverPos == null) {
                        this.hoverPos = vec3d;
                    }

                    boolean flag = EntityBee.this.getPositionVector().f(this.hoverPos) <= 0.1D;
                    boolean flag1 = true;

                    if (!flag && this.pollinatingTicks > 600) {
                        EntityBee.this.savedFlowerPos = null;
                    } else {
                        if (flag) {
                            boolean flag2 = EntityBee.this.random.nextInt(25) == 0;

                            if (flag2) {
                                this.hoverPos = new Vec3D(vec3d.getX() + (double) this.n(), vec3d.getY(), vec3d.getZ() + (double) this.n());
                                EntityBee.this.navigation.o();
                            } else {
                                flag1 = false;
                            }

                            EntityBee.this.getControllerLook().a(vec3d.getX(), vec3d.getY(), vec3d.getZ());
                        }

                        if (flag1) {
                            this.m();
                        }

                        ++this.successfulPollinatingTicks;
                        if (EntityBee.this.random.nextFloat() < 0.05F && this.successfulPollinatingTicks > this.lastSoundPlayedTick + 60) {
                            this.lastSoundPlayedTick = this.successfulPollinatingTicks;
                            EntityBee.this.playSound(SoundEffects.BEE_POLLINATE, 1.0F, 1.0F);
                        }

                    }
                }
            }
        }

        private void m() {
            EntityBee.this.getControllerMove().a(this.hoverPos.getX(), this.hoverPos.getY(), this.hoverPos.getZ(), 0.3499999940395355D);
        }

        private float n() {
            return (EntityBee.this.random.nextFloat() * 2.0F - 1.0F) * 0.33333334F;
        }

        private Optional<BlockPosition> o() {
            return this.a(this.VALID_POLLINATION_BLOCKS, 5.0D);
        }

        private Optional<BlockPosition> a(Predicate<IBlockData> predicate, double d0) {
            BlockPosition blockposition = EntityBee.this.getChunkCoordinates();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int i = 0; (double) i <= d0; i = i > 0 ? -i : 1 - i) {
                for (int j = 0; (double) j < d0; ++j) {
                    for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                        for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, k, i - 1, l);
                            if (blockposition.a((BaseBlockPosition) blockposition_mutableblockposition, d0) && predicate.test(EntityBee.this.level.getType(blockposition_mutableblockposition))) {
                                return Optional.of(blockposition_mutableblockposition);
                            }
                        }
                    }
                }
            }

            return Optional.empty();
        }
    }

    private class j extends ControllerLook {

        j(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        public void a() {
            if (!EntityBee.this.isAngry()) {
                super.a();
            }
        }

        @Override
        protected boolean c() {
            return !EntityBee.this.beePollinateGoal.k();
        }
    }

    private class b extends PathfinderGoalMeleeAttack {

        b(EntityCreature entitycreature, double d0, boolean flag) {
            super(entitycreature, d0, flag);
        }

        @Override
        public boolean a() {
            return super.a() && EntityBee.this.isAngry() && !EntityBee.this.hasStung();
        }

        @Override
        public boolean b() {
            return super.b() && EntityBee.this.isAngry() && !EntityBee.this.hasStung();
        }
    }

    private class d extends EntityBee.a {

        d() {
            super();
        }

        @Override
        public boolean g() {
            if (EntityBee.this.hasHivePos() && EntityBee.this.fN() && EntityBee.this.hivePos.a((IPosition) EntityBee.this.getPositionVector(), 2.0D)) {
                TileEntity tileentity = EntityBee.this.level.getTileEntity(EntityBee.this.hivePos);

                if (tileentity instanceof TileEntityBeehive) {
                    TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

                    if (!tileentitybeehive.isFull()) {
                        return true;
                    }

                    EntityBee.this.hivePos = null;
                }
            }

            return false;
        }

        @Override
        public boolean h() {
            return false;
        }

        @Override
        public void c() {
            TileEntity tileentity = EntityBee.this.level.getTileEntity(EntityBee.this.hivePos);

            if (tileentity instanceof TileEntityBeehive) {
                TileEntityBeehive tileentitybeehive = (TileEntityBeehive) tileentity;

                tileentitybeehive.addBee(EntityBee.this, EntityBee.this.hasNectar());
            }

        }
    }

    private class i extends EntityBee.a {

        i() {
            super();
        }

        @Override
        public boolean g() {
            return EntityBee.this.remainingCooldownBeforeLocatingNewHive == 0 && !EntityBee.this.hasHivePos() && EntityBee.this.fN();
        }

        @Override
        public boolean h() {
            return false;
        }

        @Override
        public void c() {
            EntityBee.this.remainingCooldownBeforeLocatingNewHive = 200;
            List<BlockPosition> list = this.j();

            if (!list.isEmpty()) {
                Iterator iterator = list.iterator();

                BlockPosition blockposition;

                do {
                    if (!iterator.hasNext()) {
                        EntityBee.this.goToHiveGoal.j();
                        EntityBee.this.hivePos = (BlockPosition) list.get(0);
                        return;
                    }

                    blockposition = (BlockPosition) iterator.next();
                } while (EntityBee.this.goToHiveGoal.b(blockposition));

                EntityBee.this.hivePos = blockposition;
            }
        }

        private List<BlockPosition> j() {
            BlockPosition blockposition = EntityBee.this.getChunkCoordinates();
            VillagePlace villageplace = ((WorldServer) EntityBee.this.level).A();
            Stream<VillagePlaceRecord> stream = villageplace.c((villageplacetype) -> {
                return villageplacetype == VillagePlaceType.BEEHIVE || villageplacetype == VillagePlaceType.BEE_NEST;
            }, blockposition, 20, VillagePlace.Occupancy.ANY);

            return (List) stream.map(VillagePlaceRecord::f).filter(EntityBee.this::i).sorted(Comparator.comparingDouble((blockposition1) -> {
                return blockposition1.j(blockposition);
            })).collect(Collectors.toList());
        }
    }

    @VisibleForDebug
    public class e extends EntityBee.a {

        public static final int MAX_TRAVELLING_TICKS = 600;
        int travellingTicks;
        private static final int MAX_BLACKLISTED_TARGETS = 3;
        final List<BlockPosition> blacklistedTargets;
        @Nullable
        private PathEntity lastPath;
        private static final int TICKS_BEFORE_HIVE_DROP = 60;
        private int ticksStuck;

        e() {
            super();
            this.travellingTicks = EntityBee.this.level.random.nextInt(10);
            this.blacklistedTargets = Lists.newArrayList();
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean g() {
            return EntityBee.this.hivePos != null && !EntityBee.this.fl() && EntityBee.this.fN() && !this.d(EntityBee.this.hivePos) && EntityBee.this.level.getType(EntityBee.this.hivePos).a((Tag) TagsBlock.BEEHIVES);
        }

        @Override
        public boolean h() {
            return this.g();
        }

        @Override
        public void c() {
            this.travellingTicks = 0;
            this.ticksStuck = 0;
            super.c();
        }

        @Override
        public void d() {
            this.travellingTicks = 0;
            this.ticksStuck = 0;
            EntityBee.this.navigation.o();
            EntityBee.this.navigation.g();
        }

        @Override
        public void e() {
            if (EntityBee.this.hivePos != null) {
                ++this.travellingTicks;
                if (this.travellingTicks > 600) {
                    this.k();
                } else if (!EntityBee.this.navigation.n()) {
                    if (!EntityBee.this.b(EntityBee.this.hivePos, 16)) {
                        if (EntityBee.this.j(EntityBee.this.hivePos)) {
                            this.l();
                        } else {
                            EntityBee.this.h(EntityBee.this.hivePos);
                        }
                    } else {
                        boolean flag = this.a(EntityBee.this.hivePos);

                        if (!flag) {
                            this.k();
                        } else if (this.lastPath != null && EntityBee.this.navigation.k().a(this.lastPath)) {
                            ++this.ticksStuck;
                            if (this.ticksStuck > 60) {
                                this.l();
                                this.ticksStuck = 0;
                            }
                        } else {
                            this.lastPath = EntityBee.this.navigation.k();
                        }

                    }
                }
            }
        }

        private boolean a(BlockPosition blockposition) {
            EntityBee.this.navigation.a(10.0F);
            EntityBee.this.navigation.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
            return EntityBee.this.navigation.k() != null && EntityBee.this.navigation.k().j();
        }

        boolean b(BlockPosition blockposition) {
            return this.blacklistedTargets.contains(blockposition);
        }

        private void c(BlockPosition blockposition) {
            this.blacklistedTargets.add(blockposition);

            while (this.blacklistedTargets.size() > 3) {
                this.blacklistedTargets.remove(0);
            }

        }

        void j() {
            this.blacklistedTargets.clear();
        }

        private void k() {
            if (EntityBee.this.hivePos != null) {
                this.c(EntityBee.this.hivePos);
            }

            this.l();
        }

        private void l() {
            EntityBee.this.hivePos = null;
            EntityBee.this.remainingCooldownBeforeLocatingNewHive = 200;
        }

        private boolean d(BlockPosition blockposition) {
            if (EntityBee.this.b(blockposition, 2)) {
                return true;
            } else {
                PathEntity pathentity = EntityBee.this.navigation.k();

                return pathentity != null && pathentity.m().equals(blockposition) && pathentity.j() && pathentity.c();
            }
        }
    }

    public class f extends EntityBee.a {

        private static final int MAX_TRAVELLING_TICKS = 600;
        int travellingTicks;

        f() {
            super();
            this.travellingTicks = EntityBee.this.level.random.nextInt(10);
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean g() {
            return EntityBee.this.savedFlowerPos != null && !EntityBee.this.fl() && this.j() && EntityBee.this.k(EntityBee.this.savedFlowerPos) && !EntityBee.this.b(EntityBee.this.savedFlowerPos, 2);
        }

        @Override
        public boolean h() {
            return this.g();
        }

        @Override
        public void c() {
            this.travellingTicks = 0;
            super.c();
        }

        @Override
        public void d() {
            this.travellingTicks = 0;
            EntityBee.this.navigation.o();
            EntityBee.this.navigation.g();
        }

        @Override
        public void e() {
            if (EntityBee.this.savedFlowerPos != null) {
                ++this.travellingTicks;
                if (this.travellingTicks > 600) {
                    EntityBee.this.savedFlowerPos = null;
                } else if (!EntityBee.this.navigation.n()) {
                    if (EntityBee.this.j(EntityBee.this.savedFlowerPos)) {
                        EntityBee.this.savedFlowerPos = null;
                    } else {
                        EntityBee.this.h(EntityBee.this.savedFlowerPos);
                    }
                }
            }
        }

        private boolean j() {
            return EntityBee.this.ticksWithoutNectarSinceExitingHive > 2400;
        }
    }

    private class g extends EntityBee.a {

        static final int GROW_CHANCE = 30;

        g() {
            super();
        }

        @Override
        public boolean g() {
            return EntityBee.this.getNumCropsGrownSincePollination() >= 10 ? false : (EntityBee.this.random.nextFloat() < 0.3F ? false : EntityBee.this.hasNectar() && EntityBee.this.fT());
        }

        @Override
        public boolean h() {
            return this.g();
        }

        @Override
        public void e() {
            if (EntityBee.this.random.nextInt(30) == 0) {
                for (int i = 1; i <= 2; ++i) {
                    BlockPosition blockposition = EntityBee.this.getChunkCoordinates().down(i);
                    IBlockData iblockdata = EntityBee.this.level.getType(blockposition);
                    Block block = iblockdata.getBlock();
                    boolean flag = false;
                    BlockStateInteger blockstateinteger = null;

                    if (iblockdata.a((Tag) TagsBlock.BEE_GROWABLES)) {
                        if (block instanceof BlockCrops) {
                            BlockCrops blockcrops = (BlockCrops) block;

                            if (!blockcrops.isRipe(iblockdata)) {
                                flag = true;
                                blockstateinteger = blockcrops.c();
                            }
                        } else {
                            int j;

                            if (block instanceof BlockStem) {
                                j = (Integer) iblockdata.get(BlockStem.AGE);
                                if (j < 7) {
                                    flag = true;
                                    blockstateinteger = BlockStem.AGE;
                                }
                            } else if (iblockdata.a(Blocks.SWEET_BERRY_BUSH)) {
                                j = (Integer) iblockdata.get(BlockSweetBerryBush.AGE);
                                if (j < 3) {
                                    flag = true;
                                    blockstateinteger = BlockSweetBerryBush.AGE;
                                }
                            } else if (iblockdata.a(Blocks.CAVE_VINES) || iblockdata.a(Blocks.CAVE_VINES_PLANT)) {
                                ((IBlockFragilePlantElement) iblockdata.getBlock()).a((WorldServer) EntityBee.this.level, EntityBee.this.random, blockposition, iblockdata);
                            }
                        }

                        if (flag) {
                            EntityBee.this.level.triggerEffect(2005, blockposition, 0);
                            EntityBee.this.level.setTypeUpdate(blockposition, (IBlockData) iblockdata.set(blockstateinteger, (Integer) iblockdata.get(blockstateinteger) + 1));
                            EntityBee.this.fS();
                        }
                    }
                }

            }
        }
    }

    private class l extends PathfinderGoal {

        private static final int WANDER_THRESHOLD = 22;

        l() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            return EntityBee.this.navigation.m() && EntityBee.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean b() {
            return EntityBee.this.navigation.n();
        }

        @Override
        public void c() {
            Vec3D vec3d = this.g();

            if (vec3d != null) {
                EntityBee.this.navigation.a(EntityBee.this.navigation.a(new BlockPosition(vec3d), 1), 1.0D);
            }

        }

        @Nullable
        private Vec3D g() {
            Vec3D vec3d;

            if (EntityBee.this.fT() && !EntityBee.this.b(EntityBee.this.hivePos, 22)) {
                Vec3D vec3d1 = Vec3D.a((BaseBlockPosition) EntityBee.this.hivePos);

                vec3d = vec3d1.d(EntityBee.this.getPositionVector()).d();
            } else {
                vec3d = EntityBee.this.e(0.0F);
            }

            boolean flag = true;
            Vec3D vec3d2 = HoverRandomPos.a(EntityBee.this, 8, 7, vec3d.x, vec3d.z, 1.5707964F, 3, 1);

            return vec3d2 != null ? vec3d2 : AirAndWaterRandomPos.a(EntityBee.this, 8, 4, -2, vec3d.x, vec3d.z, 1.5707963705062866D);
        }
    }

    private class h extends PathfinderGoalHurtByTarget {

        h(EntityBee entitybee) {
            super(entitybee);
        }

        @Override
        public boolean b() {
            return EntityBee.this.isAngry() && super.b();
        }

        @Override
        protected void a(EntityInsentient entityinsentient, EntityLiving entityliving) {
            if (entityinsentient instanceof EntityBee && this.mob.hasLineOfSight(entityliving)) {
                entityinsentient.setGoalTarget(entityliving);
            }

        }
    }

    private static class c extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        c(EntityBee entitybee) {
            Objects.requireNonNull(entitybee);
            super(entitybee, EntityHuman.class, 10, true, false, entitybee::a_);
        }

        @Override
        public boolean a() {
            return this.h() && super.a();
        }

        @Override
        public boolean b() {
            boolean flag = this.h();

            if (flag && this.mob.getGoalTarget() != null) {
                return super.b();
            } else {
                this.targetMob = null;
                return false;
            }
        }

        private boolean h() {
            EntityBee entitybee = (EntityBee) this.mob;

            return entitybee.isAngry() && !entitybee.hasStung();
        }
    }

    private abstract class a extends PathfinderGoal {

        a() {}

        public abstract boolean g();

        public abstract boolean h();

        @Override
        public boolean a() {
            return this.g() && !EntityBee.this.isAngry();
        }

        @Override
        public boolean b() {
            return this.h() && !EntityBee.this.isAngry();
        }
    }
}
