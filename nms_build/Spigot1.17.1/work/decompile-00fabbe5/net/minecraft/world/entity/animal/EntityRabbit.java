package net.minecraft.world.entity.animal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerJump;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalGotoTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCarrots;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;

public class EntityRabbit extends EntityAnimal {

    public static final double STROLL_SPEED_MOD = 0.6D;
    public static final double BREED_SPEED_MOD = 0.8D;
    public static final double FOLLOW_SPEED_MOD = 1.0D;
    public static final double FLEE_SPEED_MOD = 2.2D;
    public static final double ATTACK_SPEED_MOD = 1.4D;
    private static final DataWatcherObject<Integer> DATA_TYPE_ID = DataWatcher.a(EntityRabbit.class, DataWatcherRegistry.INT);
    public static final int TYPE_BROWN = 0;
    public static final int TYPE_WHITE = 1;
    public static final int TYPE_BLACK = 2;
    public static final int TYPE_WHITE_SPLOTCHED = 3;
    public static final int TYPE_GOLD = 4;
    public static final int TYPE_SALT = 5;
    public static final int TYPE_EVIL = 99;
    private static final MinecraftKey KILLER_BUNNY = new MinecraftKey("killer_bunny");
    public static final int EVIL_ATTACK_POWER = 8;
    public static final int EVIL_ARMOR_VALUE = 8;
    private static final int MORE_CARROTS_DELAY = 40;
    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int jumpDelayTicks;
    int moreCarrotTicks;

    public EntityRabbit(EntityTypes<? extends EntityRabbit> entitytypes, World world) {
        super(entitytypes, world);
        this.jumpControl = new EntityRabbit.ControllerJumpRabbit(this);
        this.moveControl = new EntityRabbit.ControllerMoveRabbit(this);
        this.i(0.0D);
    }

    @Override
    public void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityRabbit.PathfinderGoalRabbitPanic(this, 2.2D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 0.8D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, RecipeItemStack.a(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
        this.goalSelector.a(4, new EntityRabbit.PathfinderGoalRabbitAvoidTarget<>(this, EntityHuman.class, 8.0F, 2.2D, 2.2D));
        this.goalSelector.a(4, new EntityRabbit.PathfinderGoalRabbitAvoidTarget<>(this, EntityWolf.class, 10.0F, 2.2D, 2.2D));
        this.goalSelector.a(4, new EntityRabbit.PathfinderGoalRabbitAvoidTarget<>(this, EntityMonster.class, 4.0F, 2.2D, 2.2D));
        this.goalSelector.a(5, new EntityRabbit.PathfinderGoalEatCarrots(this));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.6D));
        this.goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
    }

    @Override
    protected float er() {
        if (!this.horizontalCollision && (!this.moveControl.b() || this.moveControl.e() <= this.locY() + 0.5D)) {
            PathEntity pathentity = this.navigation.k();

            if (pathentity != null && !pathentity.c()) {
                Vec3D vec3d = pathentity.a((Entity) this);

                if (vec3d.y > this.locY() + 0.5D) {
                    return 0.5F;
                }
            }

            return this.moveControl.c() <= 0.6D ? 0.2F : 0.3F;
        } else {
            return 0.5F;
        }
    }

    @Override
    protected void jump() {
        super.jump();
        double d0 = this.moveControl.c();

        if (d0 > 0.0D) {
            double d1 = this.getMot().i();

            if (d1 < 0.01D) {
                this.a(0.1F, new Vec3D(0.0D, 0.0D, 1.0D));
            }
        }

        if (!this.level.isClientSide) {
            this.level.broadcastEntityEffect(this, (byte) 1);
        }

    }

    public float z(float f) {
        return this.jumpDuration == 0 ? 0.0F : ((float) this.jumpTicks + f) / (float) this.jumpDuration;
    }

    public void i(double d0) {
        this.getNavigation().a(d0);
        this.moveControl.a(this.moveControl.d(), this.moveControl.e(), this.moveControl.f(), d0);
    }

    @Override
    public void setJumping(boolean flag) {
        super.setJumping(flag);
        if (flag) {
            this.playSound(this.getSoundJump(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }

    }

    public void p() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityRabbit.DATA_TYPE_ID, 0);
    }

    @Override
    public void mobTick() {
        if (this.jumpDelayTicks > 0) {
            --this.jumpDelayTicks;
        }

        if (this.moreCarrotTicks > 0) {
            this.moreCarrotTicks -= this.random.nextInt(3);
            if (this.moreCarrotTicks < 0) {
                this.moreCarrotTicks = 0;
            }
        }

        if (this.onGround) {
            if (!this.wasOnGround) {
                this.setJumping(false);
                this.fG();
            }

            if (this.getRabbitType() == 99 && this.jumpDelayTicks == 0) {
                EntityLiving entityliving = this.getGoalTarget();

                if (entityliving != null && this.f((Entity) entityliving) < 16.0D) {
                    this.b(entityliving.locX(), entityliving.locZ());
                    this.moveControl.a(entityliving.locX(), entityliving.locY(), entityliving.locZ(), this.moveControl.c());
                    this.p();
                    this.wasOnGround = true;
                }
            }

            EntityRabbit.ControllerJumpRabbit entityrabbit_controllerjumprabbit = (EntityRabbit.ControllerJumpRabbit) this.jumpControl;

            if (!entityrabbit_controllerjumprabbit.c()) {
                if (this.moveControl.b() && this.jumpDelayTicks == 0) {
                    PathEntity pathentity = this.navigation.k();
                    Vec3D vec3d = new Vec3D(this.moveControl.d(), this.moveControl.e(), this.moveControl.f());

                    if (pathentity != null && !pathentity.c()) {
                        vec3d = pathentity.a((Entity) this);
                    }

                    this.b(vec3d.x, vec3d.z);
                    this.p();
                }
            } else if (!entityrabbit_controllerjumprabbit.d()) {
                this.fy();
            }
        }

        this.wasOnGround = this.onGround;
    }

    @Override
    public boolean aV() {
        return false;
    }

    private void b(double d0, double d1) {
        this.setYRot((float) (MathHelper.d(d1 - this.locZ(), d0 - this.locX()) * 57.2957763671875D) - 90.0F);
    }

    private void fy() {
        ((EntityRabbit.ControllerJumpRabbit) this.jumpControl).a(true);
    }

    private void fE() {
        ((EntityRabbit.ControllerJumpRabbit) this.jumpControl).a(false);
    }

    private void fF() {
        if (this.moveControl.c() < 2.2D) {
            this.jumpDelayTicks = 10;
        } else {
            this.jumpDelayTicks = 1;
        }

    }

    private void fG() {
        this.fF();
        this.fE();
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }

    }

    public static AttributeProvider.Builder t() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 3.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("RabbitType", this.getRabbitType());
        nbttagcompound.setInt("MoreCarrotTicks", this.moreCarrotTicks);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setRabbitType(nbttagcompound.getInt("RabbitType"));
        this.moreCarrotTicks = nbttagcompound.getInt("MoreCarrotTicks");
    }

    protected SoundEffect getSoundJump() {
        return SoundEffects.RABBIT_JUMP;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.RABBIT_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.RABBIT_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.RABBIT_DEATH;
    }

    @Override
    public boolean attackEntity(Entity entity) {
        if (this.getRabbitType() == 99) {
            this.playSound(SoundEffects.RABBIT_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            return entity.damageEntity(DamageSource.mobAttack(this), 8.0F);
        } else {
            return entity.damageEntity(DamageSource.mobAttack(this), 3.0F);
        }
    }

    @Override
    public SoundCategory getSoundCategory() {
        return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
    }

    private static boolean m(ItemStack itemstack) {
        return itemstack.a(Items.CARROT) || itemstack.a(Items.GOLDEN_CARROT) || itemstack.a(Blocks.DANDELION.getItem());
    }

    @Override
    public EntityRabbit createChild(WorldServer worldserver, EntityAgeable entityageable) {
        EntityRabbit entityrabbit = (EntityRabbit) EntityTypes.RABBIT.a((World) worldserver);
        int i = this.a((GeneratorAccess) worldserver);

        if (this.random.nextInt(20) != 0) {
            if (entityageable instanceof EntityRabbit && this.random.nextBoolean()) {
                i = ((EntityRabbit) entityageable).getRabbitType();
            } else {
                i = this.getRabbitType();
            }
        }

        entityrabbit.setRabbitType(i);
        return entityrabbit;
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return m(itemstack);
    }

    public int getRabbitType() {
        return (Integer) this.entityData.get(EntityRabbit.DATA_TYPE_ID);
    }

    public void setRabbitType(int i) {
        if (i == 99) {
            this.getAttributeInstance(GenericAttributes.ARMOR).setValue(8.0D);
            this.goalSelector.a(4, new EntityRabbit.PathfinderGoalKillerRabbitMeleeAttack(this));
            this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a());
            this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
            this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityWolf.class, true));
            if (!this.hasCustomName()) {
                this.setCustomName(new ChatMessage(SystemUtils.a("entity", EntityRabbit.KILLER_BUNNY)));
            }
        }

        this.entityData.set(EntityRabbit.DATA_TYPE_ID, i);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        int i = this.a((GeneratorAccess) worldaccess);

        if (groupdataentity instanceof EntityRabbit.GroupDataRabbit) {
            i = ((EntityRabbit.GroupDataRabbit) groupdataentity).rabbitType;
        } else {
            groupdataentity = new EntityRabbit.GroupDataRabbit(i);
        }

        this.setRabbitType(i);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    private int a(GeneratorAccess generatoraccess) {
        BiomeBase biomebase = generatoraccess.getBiome(this.getChunkCoordinates());
        int i = this.random.nextInt(100);

        return biomebase.c() == BiomeBase.Precipitation.SNOW ? (i < 80 ? 1 : 3) : (biomebase.t() == BiomeBase.Geography.DESERT ? 4 : (i < 50 ? 0 : (i < 90 ? 5 : 2)));
    }

    public static boolean c(EntityTypes<EntityRabbit> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        IBlockData iblockdata = generatoraccess.getType(blockposition.down());

        return (iblockdata.a(Blocks.GRASS_BLOCK) || iblockdata.a(Blocks.SNOW) || iblockdata.a(Blocks.SAND)) && generatoraccess.getLightLevel(blockposition, 0) > 8;
    }

    boolean fH() {
        return this.moreCarrotTicks == 0;
    }

    @Override
    public void a(byte b0) {
        if (b0 == 1) {
            this.aW();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else {
            super.a(b0);
        }

    }

    @Override
    public Vec3D cu() {
        return new Vec3D(0.0D, (double) (0.6F * this.getHeadHeight()), (double) (this.getWidth() * 0.4F));
    }

    public class ControllerJumpRabbit extends ControllerJump {

        private final EntityRabbit rabbit;
        private boolean canJump;

        public ControllerJumpRabbit(EntityRabbit entityrabbit) {
            super(entityrabbit);
            this.rabbit = entityrabbit;
        }

        public boolean c() {
            return this.jump;
        }

        public boolean d() {
            return this.canJump;
        }

        public void a(boolean flag) {
            this.canJump = flag;
        }

        @Override
        public void b() {
            if (this.jump) {
                this.rabbit.p();
                this.jump = false;
            }

        }
    }

    private static class ControllerMoveRabbit extends ControllerMove {

        private final EntityRabbit rabbit;
        private double nextJumpSpeed;

        public ControllerMoveRabbit(EntityRabbit entityrabbit) {
            super(entityrabbit);
            this.rabbit = entityrabbit;
        }

        @Override
        public void a() {
            if (this.rabbit.onGround && !this.rabbit.jumping && !((EntityRabbit.ControllerJumpRabbit) this.rabbit.jumpControl).c()) {
                this.rabbit.i(0.0D);
            } else if (this.b()) {
                this.rabbit.i(this.nextJumpSpeed);
            }

            super.a();
        }

        @Override
        public void a(double d0, double d1, double d2, double d3) {
            if (this.rabbit.isInWater()) {
                d3 = 1.5D;
            }

            super.a(d0, d1, d2, d3);
            if (d3 > 0.0D) {
                this.nextJumpSpeed = d3;
            }

        }
    }

    private static class PathfinderGoalRabbitPanic extends PathfinderGoalPanic {

        private final EntityRabbit rabbit;

        public PathfinderGoalRabbitPanic(EntityRabbit entityrabbit, double d0) {
            super(entityrabbit, d0);
            this.rabbit = entityrabbit;
        }

        @Override
        public void e() {
            super.e();
            this.rabbit.i(this.speedModifier);
        }
    }

    private static class PathfinderGoalRabbitAvoidTarget<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityRabbit rabbit;

        public PathfinderGoalRabbitAvoidTarget(EntityRabbit entityrabbit, Class<T> oclass, float f, double d0, double d1) {
            super(entityrabbit, oclass, f, d0, d1);
            this.rabbit = entityrabbit;
        }

        @Override
        public boolean a() {
            return this.rabbit.getRabbitType() != 99 && super.a();
        }
    }

    private static class PathfinderGoalEatCarrots extends PathfinderGoalGotoTarget {

        private final EntityRabbit rabbit;
        private boolean wantsToRaid;
        private boolean canRaid;

        public PathfinderGoalEatCarrots(EntityRabbit entityrabbit) {
            super(entityrabbit, 0.699999988079071D, 16);
            this.rabbit = entityrabbit;
        }

        @Override
        public boolean a() {
            if (this.nextStartTick <= 0) {
                if (!this.rabbit.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    return false;
                }

                this.canRaid = false;
                this.wantsToRaid = this.rabbit.fH();
                this.wantsToRaid = true;
            }

            return super.a();
        }

        @Override
        public boolean b() {
            return this.canRaid && super.b();
        }

        @Override
        public void e() {
            super.e();
            this.rabbit.getControllerLook().a((double) this.blockPos.getX() + 0.5D, (double) (this.blockPos.getY() + 1), (double) this.blockPos.getZ() + 0.5D, 10.0F, (float) this.rabbit.eZ());
            if (this.l()) {
                World world = this.rabbit.level;
                BlockPosition blockposition = this.blockPos.up();
                IBlockData iblockdata = world.getType(blockposition);
                Block block = iblockdata.getBlock();

                if (this.canRaid && block instanceof BlockCarrots) {
                    int i = (Integer) iblockdata.get(BlockCarrots.AGE);

                    if (i == 0) {
                        world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 2);
                        world.a(blockposition, true, this.rabbit);
                    } else {
                        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCarrots.AGE, i - 1), 2);
                        world.triggerEffect(2001, blockposition, Block.getCombinedId(iblockdata));
                    }

                    this.rabbit.moreCarrotTicks = 40;
                }

                this.canRaid = false;
                this.nextStartTick = 10;
            }

        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            IBlockData iblockdata = iworldreader.getType(blockposition);

            if (iblockdata.a(Blocks.FARMLAND) && this.wantsToRaid && !this.canRaid) {
                iblockdata = iworldreader.getType(blockposition.up());
                if (iblockdata.getBlock() instanceof BlockCarrots && ((BlockCarrots) iblockdata.getBlock()).isRipe(iblockdata)) {
                    this.canRaid = true;
                    return true;
                }
            }

            return false;
        }
    }

    private static class PathfinderGoalKillerRabbitMeleeAttack extends PathfinderGoalMeleeAttack {

        public PathfinderGoalKillerRabbitMeleeAttack(EntityRabbit entityrabbit) {
            super(entityrabbit, 1.4D, true);
        }

        @Override
        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.getWidth());
        }
    }

    public static class GroupDataRabbit extends EntityAgeable.a {

        public final int rabbitType;

        public GroupDataRabbit(int i) {
            super(1.0F);
            this.rabbitType = i;
        }
    }
}
