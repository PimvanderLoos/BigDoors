package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalGotoTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTurtleEgg;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.phys.Vec3D;

public class EntityTurtle extends EntityAnimal {

    private static final DataWatcherObject<BlockPosition> HOME_POS = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.BLOCK_POS);
    private static final DataWatcherObject<Boolean> HAS_EGG = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> LAYING_EGG = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<BlockPosition> TRAVEL_POS = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.BLOCK_POS);
    private static final DataWatcherObject<Boolean> GOING_HOME = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> TRAVELLING = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.BOOLEAN);
    public static final RecipeItemStack FOOD_ITEMS = RecipeItemStack.a(Blocks.SEAGRASS.getItem());
    int layEggCounter;
    public static final Predicate<EntityLiving> BABY_ON_LAND_SELECTOR = (entityliving) -> {
        return entityliving.isBaby() && !entityliving.isInWater();
    };

    public EntityTurtle(EntityTypes<? extends EntityTurtle> entitytypes, World world) {
        super(entitytypes, world);
        this.a(PathType.WATER, 0.0F);
        this.a(PathType.DOOR_IRON_CLOSED, -1.0F);
        this.a(PathType.DOOR_WOOD_CLOSED, -1.0F);
        this.a(PathType.DOOR_OPEN, -1.0F);
        this.moveControl = new EntityTurtle.e(this);
        this.maxUpStep = 1.0F;
    }

    public void setHomePos(BlockPosition blockposition) {
        this.entityData.set(EntityTurtle.HOME_POS, blockposition);
    }

    BlockPosition getHomePos() {
        return (BlockPosition) this.entityData.get(EntityTurtle.HOME_POS);
    }

    void setTravelPos(BlockPosition blockposition) {
        this.entityData.set(EntityTurtle.TRAVEL_POS, blockposition);
    }

    BlockPosition getTravelPos() {
        return (BlockPosition) this.entityData.get(EntityTurtle.TRAVEL_POS);
    }

    public boolean hasEgg() {
        return (Boolean) this.entityData.get(EntityTurtle.HAS_EGG);
    }

    void setHasEgg(boolean flag) {
        this.entityData.set(EntityTurtle.HAS_EGG, flag);
    }

    public boolean t() {
        return (Boolean) this.entityData.get(EntityTurtle.LAYING_EGG);
    }

    void w(boolean flag) {
        this.layEggCounter = flag ? 1 : 0;
        this.entityData.set(EntityTurtle.LAYING_EGG, flag);
    }

    boolean fE() {
        return (Boolean) this.entityData.get(EntityTurtle.GOING_HOME);
    }

    void x(boolean flag) {
        this.entityData.set(EntityTurtle.GOING_HOME, flag);
    }

    boolean fF() {
        return (Boolean) this.entityData.get(EntityTurtle.TRAVELLING);
    }

    void y(boolean flag) {
        this.entityData.set(EntityTurtle.TRAVELLING, flag);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityTurtle.HOME_POS, BlockPosition.ZERO);
        this.entityData.register(EntityTurtle.HAS_EGG, false);
        this.entityData.register(EntityTurtle.TRAVEL_POS, BlockPosition.ZERO);
        this.entityData.register(EntityTurtle.GOING_HOME, false);
        this.entityData.register(EntityTurtle.TRAVELLING, false);
        this.entityData.register(EntityTurtle.LAYING_EGG, false);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("HomePosX", this.getHomePos().getX());
        nbttagcompound.setInt("HomePosY", this.getHomePos().getY());
        nbttagcompound.setInt("HomePosZ", this.getHomePos().getZ());
        nbttagcompound.setBoolean("HasEgg", this.hasEgg());
        nbttagcompound.setInt("TravelPosX", this.getTravelPos().getX());
        nbttagcompound.setInt("TravelPosY", this.getTravelPos().getY());
        nbttagcompound.setInt("TravelPosZ", this.getTravelPos().getZ());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("HomePosX");
        int j = nbttagcompound.getInt("HomePosY");
        int k = nbttagcompound.getInt("HomePosZ");

        this.setHomePos(new BlockPosition(i, j, k));
        super.loadData(nbttagcompound);
        this.setHasEgg(nbttagcompound.getBoolean("HasEgg"));
        int l = nbttagcompound.getInt("TravelPosX");
        int i1 = nbttagcompound.getInt("TravelPosY");
        int j1 = nbttagcompound.getInt("TravelPosZ");

        this.setTravelPos(new BlockPosition(l, i1, j1));
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setHomePos(this.getChunkCoordinates());
        this.setTravelPos(BlockPosition.ZERO);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    public static boolean c(EntityTypes<EntityTurtle> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return blockposition.getY() < generatoraccess.getSeaLevel() + 4 && BlockTurtleEgg.a((IBlockAccess) generatoraccess, blockposition) && generatoraccess.getLightLevel(blockposition, 0) > 8;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new EntityTurtle.f(this, 1.2D));
        this.goalSelector.a(1, new EntityTurtle.a(this, 1.0D));
        this.goalSelector.a(1, new EntityTurtle.d(this, 1.0D));
        this.goalSelector.a(2, new PathfinderGoalTempt(this, 1.1D, EntityTurtle.FOOD_ITEMS, false));
        this.goalSelector.a(3, new EntityTurtle.c(this, 1.0D));
        this.goalSelector.a(4, new EntityTurtle.b(this, 1.0D));
        this.goalSelector.a(7, new EntityTurtle.i(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(9, new EntityTurtle.h(this, 1.0D, 100));
    }

    public static AttributeProvider.Builder fw() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 30.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public boolean ck() {
        return false;
    }

    @Override
    public boolean dr() {
        return true;
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.WATER;
    }

    @Override
    public int J() {
        return 200;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return !this.isInWater() && this.onGround && !this.isBaby() ? SoundEffects.TURTLE_AMBIENT_LAND : super.getSoundAmbient();
    }

    @Override
    protected void d(float f) {
        super.d(f * 1.5F);
    }

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.TURTLE_SWIM;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.isBaby() ? SoundEffects.TURTLE_HURT_BABY : SoundEffects.TURTLE_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return this.isBaby() ? SoundEffects.TURTLE_DEATH_BABY : SoundEffects.TURTLE_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        SoundEffect soundeffect = this.isBaby() ? SoundEffects.TURTLE_SHAMBLE_BABY : SoundEffects.TURTLE_SHAMBLE;

        this.playSound(soundeffect, 0.15F, 1.0F);
    }

    @Override
    public boolean fz() {
        return super.fz() && !this.hasEgg();
    }

    @Override
    protected float az() {
        return this.moveDist + 0.15F;
    }

    @Override
    public float dz() {
        return this.isBaby() ? 0.3F : 1.0F;
    }

    @Override
    protected NavigationAbstract a(World world) {
        return new EntityTurtle.g(this, world);
    }

    @Nullable
    @Override
    public EntityAgeable createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.TURTLE.a((World) worldserver);
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return itemstack.a(Blocks.SEAGRASS.getItem());
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return !this.fE() && iworldreader.getFluid(blockposition).a((Tag) TagsFluid.WATER) ? 10.0F : (BlockTurtleEgg.a((IBlockAccess) iworldreader, blockposition) ? 10.0F : iworldreader.z(blockposition) - 0.5F);
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.isAlive() && this.t() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0) {
            BlockPosition blockposition = this.getChunkCoordinates();

            if (BlockTurtleEgg.a((IBlockAccess) this.level, blockposition)) {
                this.level.triggerEffect(2001, blockposition, Block.getCombinedId(this.level.getType(blockposition.down())));
            }
        }

    }

    @Override
    protected void n() {
        super.n();
        if (!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.a((IMaterial) Items.SCUTE, 1);
        }

    }

    @Override
    public void g(Vec3D vec3d) {
        if (this.doAITick() && this.isInWater()) {
            this.a(0.1F, vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.9D));
            if (this.getGoalTarget() == null && (!this.fE() || !this.getHomePos().a((IPosition) this.getPositionVector(), 20.0D))) {
                this.setMot(this.getMot().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.g(vec3d);
        }

    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return false;
    }

    @Override
    public void onLightningStrike(WorldServer worldserver, EntityLightning entitylightning) {
        this.damageEntity(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
    }

    private static class e extends ControllerMove {

        private final EntityTurtle turtle;

        e(EntityTurtle entityturtle) {
            super(entityturtle);
            this.turtle = entityturtle;
        }

        private void g() {
            if (this.turtle.isInWater()) {
                this.turtle.setMot(this.turtle.getMot().add(0.0D, 0.005D, 0.0D));
                if (!this.turtle.getHomePos().a((IPosition) this.turtle.getPositionVector(), 16.0D)) {
                    this.turtle.r(Math.max(this.turtle.ew() / 2.0F, 0.08F));
                }

                if (this.turtle.isBaby()) {
                    this.turtle.r(Math.max(this.turtle.ew() / 3.0F, 0.06F));
                }
            } else if (this.turtle.onGround) {
                this.turtle.r(Math.max(this.turtle.ew() / 2.0F, 0.06F));
            }

        }

        @Override
        public void a() {
            this.g();
            if (this.operation == ControllerMove.Operation.MOVE_TO && !this.turtle.getNavigation().m()) {
                double d0 = this.wantedX - this.turtle.locX();
                double d1 = this.wantedY - this.turtle.locY();
                double d2 = this.wantedZ - this.turtle.locZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                d1 /= d3;
                float f = (float) (MathHelper.d(d2, d0) * 57.2957763671875D) - 90.0F;

                this.turtle.setYRot(this.a(this.turtle.getYRot(), f, 90.0F));
                this.turtle.yBodyRot = this.turtle.getYRot();
                float f1 = (float) (this.speedModifier * this.turtle.b(GenericAttributes.MOVEMENT_SPEED));

                this.turtle.r(MathHelper.h(0.125F, this.turtle.ew(), f1));
                this.turtle.setMot(this.turtle.getMot().add(0.0D, (double) this.turtle.ew() * d1 * 0.1D, 0.0D));
            } else {
                this.turtle.r(0.0F);
            }
        }
    }

    private static class f extends PathfinderGoalPanic {

        f(EntityTurtle entityturtle, double d0) {
            super(entityturtle, d0);
        }

        @Override
        public boolean a() {
            if (this.mob.getLastDamager() == null && !this.mob.isBurning()) {
                return false;
            } else {
                BlockPosition blockposition = this.a(this.mob.level, this.mob, 7, 4);

                if (blockposition != null) {
                    this.posX = (double) blockposition.getX();
                    this.posY = (double) blockposition.getY();
                    this.posZ = (double) blockposition.getZ();
                    return true;
                } else {
                    return this.g();
                }
            }
        }
    }

    private static class a extends PathfinderGoalBreed {

        private final EntityTurtle turtle;

        a(EntityTurtle entityturtle, double d0) {
            super(entityturtle, d0);
            this.turtle = entityturtle;
        }

        @Override
        public boolean a() {
            return super.a() && !this.turtle.hasEgg();
        }

        @Override
        protected void g() {
            EntityPlayer entityplayer = this.animal.getBreedCause();

            if (entityplayer == null && this.partner.getBreedCause() != null) {
                entityplayer = this.partner.getBreedCause();
            }

            if (entityplayer != null) {
                entityplayer.a(StatisticList.ANIMALS_BRED);
                CriterionTriggers.BRED_ANIMALS.a(entityplayer, this.animal, this.partner, (EntityAgeable) null);
            }

            this.turtle.setHasEgg(true);
            this.animal.resetLove();
            this.partner.resetLove();
            Random random = this.animal.getRandom();

            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addEntity(new EntityExperienceOrb(this.level, this.animal.locX(), this.animal.locY(), this.animal.locZ(), random.nextInt(7) + 1));
            }

        }
    }

    private static class d extends PathfinderGoalGotoTarget {

        private final EntityTurtle turtle;

        d(EntityTurtle entityturtle, double d0) {
            super(entityturtle, d0, 16);
            this.turtle = entityturtle;
        }

        @Override
        public boolean a() {
            return this.turtle.hasEgg() && this.turtle.getHomePos().a((IPosition) this.turtle.getPositionVector(), 9.0D) ? super.a() : false;
        }

        @Override
        public boolean b() {
            return super.b() && this.turtle.hasEgg() && this.turtle.getHomePos().a((IPosition) this.turtle.getPositionVector(), 9.0D);
        }

        @Override
        public void e() {
            super.e();
            BlockPosition blockposition = this.turtle.getChunkCoordinates();

            if (!this.turtle.isInWater() && this.l()) {
                if (this.turtle.layEggCounter < 1) {
                    this.turtle.w(true);
                } else if (this.turtle.layEggCounter > 200) {
                    World world = this.turtle.level;

                    world.playSound((EntityHuman) null, blockposition, SoundEffects.TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
                    world.setTypeAndData(this.blockPos.up(), (IBlockData) Blocks.TURTLE_EGG.getBlockData().set(BlockTurtleEgg.EGGS, this.turtle.random.nextInt(4) + 1), 3);
                    this.turtle.setHasEgg(false);
                    this.turtle.w(false);
                    this.turtle.setLoveTicks(600);
                }

                if (this.turtle.t()) {
                    ++this.turtle.layEggCounter;
                }
            }

        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            return !iworldreader.isEmpty(blockposition.up()) ? false : BlockTurtleEgg.b((IBlockAccess) iworldreader, blockposition);
        }
    }

    private static class c extends PathfinderGoalGotoTarget {

        private static final int GIVE_UP_TICKS = 1200;
        private final EntityTurtle turtle;

        c(EntityTurtle entityturtle, double d0) {
            super(entityturtle, entityturtle.isBaby() ? 2.0D : d0, 24);
            this.turtle = entityturtle;
            this.verticalSearchStart = -1;
        }

        @Override
        public boolean b() {
            return !this.turtle.isInWater() && this.tryTicks <= 1200 && this.a(this.turtle.level, this.blockPos);
        }

        @Override
        public boolean a() {
            return this.turtle.isBaby() && !this.turtle.isInWater() ? super.a() : (!this.turtle.fE() && !this.turtle.isInWater() && !this.turtle.hasEgg() ? super.a() : false);
        }

        @Override
        public boolean k() {
            return this.tryTicks % 160 == 0;
        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            return iworldreader.getType(blockposition).a(Blocks.WATER);
        }
    }

    private static class b extends PathfinderGoal {

        private final EntityTurtle turtle;
        private final double speedModifier;
        private boolean stuck;
        private int closeToHomeTryTicks;
        private static final int GIVE_UP_TICKS = 600;

        b(EntityTurtle entityturtle, double d0) {
            this.turtle = entityturtle;
            this.speedModifier = d0;
        }

        @Override
        public boolean a() {
            return this.turtle.isBaby() ? false : (this.turtle.hasEgg() ? true : (this.turtle.getRandom().nextInt(700) != 0 ? false : !this.turtle.getHomePos().a((IPosition) this.turtle.getPositionVector(), 64.0D)));
        }

        @Override
        public void c() {
            this.turtle.x(true);
            this.stuck = false;
            this.closeToHomeTryTicks = 0;
        }

        @Override
        public void d() {
            this.turtle.x(false);
        }

        @Override
        public boolean b() {
            return !this.turtle.getHomePos().a((IPosition) this.turtle.getPositionVector(), 7.0D) && !this.stuck && this.closeToHomeTryTicks <= 600;
        }

        @Override
        public void e() {
            BlockPosition blockposition = this.turtle.getHomePos();
            boolean flag = blockposition.a((IPosition) this.turtle.getPositionVector(), 16.0D);

            if (flag) {
                ++this.closeToHomeTryTicks;
            }

            if (this.turtle.getNavigation().m()) {
                Vec3D vec3d = Vec3D.c((BaseBlockPosition) blockposition);
                Vec3D vec3d1 = DefaultRandomPos.a(this.turtle, 16, 3, vec3d, 0.3141592741012573D);

                if (vec3d1 == null) {
                    vec3d1 = DefaultRandomPos.a(this.turtle, 8, 7, vec3d, 1.5707963705062866D);
                }

                if (vec3d1 != null && !flag && !this.turtle.level.getType(new BlockPosition(vec3d1)).a(Blocks.WATER)) {
                    vec3d1 = DefaultRandomPos.a(this.turtle, 16, 5, vec3d, 1.5707963705062866D);
                }

                if (vec3d1 == null) {
                    this.stuck = true;
                    return;
                }

                this.turtle.getNavigation().a(vec3d1.x, vec3d1.y, vec3d1.z, this.speedModifier);
            }

        }
    }

    private static class i extends PathfinderGoal {

        private final EntityTurtle turtle;
        private final double speedModifier;
        private boolean stuck;

        i(EntityTurtle entityturtle, double d0) {
            this.turtle = entityturtle;
            this.speedModifier = d0;
        }

        @Override
        public boolean a() {
            return !this.turtle.fE() && !this.turtle.hasEgg() && this.turtle.isInWater();
        }

        @Override
        public void c() {
            boolean flag = true;
            boolean flag1 = true;
            Random random = this.turtle.random;
            int i = random.nextInt(1025) - 512;
            int j = random.nextInt(9) - 4;
            int k = random.nextInt(1025) - 512;

            if ((double) j + this.turtle.locY() > (double) (this.turtle.level.getSeaLevel() - 1)) {
                j = 0;
            }

            BlockPosition blockposition = new BlockPosition((double) i + this.turtle.locX(), (double) j + this.turtle.locY(), (double) k + this.turtle.locZ());

            this.turtle.setTravelPos(blockposition);
            this.turtle.y(true);
            this.stuck = false;
        }

        @Override
        public void e() {
            if (this.turtle.getNavigation().m()) {
                Vec3D vec3d = Vec3D.c((BaseBlockPosition) this.turtle.getTravelPos());
                Vec3D vec3d1 = DefaultRandomPos.a(this.turtle, 16, 3, vec3d, 0.3141592741012573D);

                if (vec3d1 == null) {
                    vec3d1 = DefaultRandomPos.a(this.turtle, 8, 7, vec3d, 1.5707963705062866D);
                }

                if (vec3d1 != null) {
                    int i = MathHelper.floor(vec3d1.x);
                    int j = MathHelper.floor(vec3d1.z);
                    boolean flag = true;

                    if (!this.turtle.level.b(i - 34, j - 34, i + 34, j + 34)) {
                        vec3d1 = null;
                    }
                }

                if (vec3d1 == null) {
                    this.stuck = true;
                    return;
                }

                this.turtle.getNavigation().a(vec3d1.x, vec3d1.y, vec3d1.z, this.speedModifier);
            }

        }

        @Override
        public boolean b() {
            return !this.turtle.getNavigation().m() && !this.stuck && !this.turtle.fE() && !this.turtle.isInLove() && !this.turtle.hasEgg();
        }

        @Override
        public void d() {
            this.turtle.y(false);
            super.d();
        }
    }

    private static class h extends PathfinderGoalRandomStroll {

        private final EntityTurtle turtle;

        h(EntityTurtle entityturtle, double d0, int i) {
            super(entityturtle, d0, i);
            this.turtle = entityturtle;
        }

        @Override
        public boolean a() {
            return !this.mob.isInWater() && !this.turtle.fE() && !this.turtle.hasEgg() ? super.a() : false;
        }
    }

    private static class g extends NavigationGuardian {

        g(EntityTurtle entityturtle, World world) {
            super(entityturtle, world);
        }

        @Override
        protected boolean a() {
            return true;
        }

        @Override
        protected Pathfinder a(int i) {
            this.nodeEvaluator = new AmphibiousNodeEvaluator(true);
            this.nodeEvaluator.b(false);
            this.nodeEvaluator.a(false);
            return new Pathfinder(this.nodeEvaluator, i);
        }

        @Override
        public boolean a(BlockPosition blockposition) {
            if (this.mob instanceof EntityTurtle) {
                EntityTurtle entityturtle = (EntityTurtle) this.mob;

                if (entityturtle.fF()) {
                    return this.level.getType(blockposition).a(Blocks.WATER);
                }
            }

            return !this.level.getType(blockposition.down()).isAir();
        }
    }
}
