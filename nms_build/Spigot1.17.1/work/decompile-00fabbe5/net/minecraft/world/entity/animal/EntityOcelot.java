package net.minecraft.world.entity.animal;

import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLeapAtTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalOcelotAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class EntityOcelot extends EntityAnimal {

    public static final double CROUCH_SPEED_MOD = 0.6D;
    public static final double WALK_SPEED_MOD = 0.8D;
    public static final double SPRINT_SPEED_MOD = 1.33D;
    private static final RecipeItemStack TEMPT_INGREDIENT = RecipeItemStack.a(Items.COD, Items.SALMON);
    private static final DataWatcherObject<Boolean> DATA_TRUSTING = DataWatcher.a(EntityOcelot.class, DataWatcherRegistry.BOOLEAN);
    private EntityOcelot.a<EntityHuman> ocelotAvoidPlayersGoal;
    private EntityOcelot.b temptGoal;

    public EntityOcelot(EntityTypes<? extends EntityOcelot> entitytypes, World world) {
        super(entitytypes, world);
        this.t();
    }

    public boolean isTrusting() {
        return (Boolean) this.entityData.get(EntityOcelot.DATA_TRUSTING);
    }

    public void setTrusting(boolean flag) {
        this.entityData.set(EntityOcelot.DATA_TRUSTING, flag);
        this.t();
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setBoolean("Trusting", this.isTrusting());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setTrusting(nbttagcompound.getBoolean("Trusting"));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityOcelot.DATA_TRUSTING, false);
    }

    @Override
    protected void initPathfinder() {
        this.temptGoal = new EntityOcelot.b(this, 0.6D, EntityOcelot.TEMPT_INGREDIENT, true);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(3, this.temptGoal);
        this.goalSelector.a(7, new PathfinderGoalLeapAtTarget(this, 0.3F));
        this.goalSelector.a(8, new PathfinderGoalOcelotAttack(this));
        this.goalSelector.a(9, new PathfinderGoalBreed(this, 0.8D));
        this.goalSelector.a(10, new PathfinderGoalRandomStrollLand(this, 0.8D, 1.0000001E-5F));
        this.goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityChicken.class, false));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, false, false, EntityTurtle.BABY_ON_LAND_SELECTOR));
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

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isTrusting() && this.tickCount > 2400;
    }

    public static AttributeProvider.Builder p() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 10.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.OCELOT_AMBIENT;
    }

    @Override
    public int J() {
        return 900;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.OCELOT_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.OCELOT_DEATH;
    }

    private float fx() {
        return (float) this.b(GenericAttributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean attackEntity(Entity entity) {
        return entity.damageEntity(DamageSource.mobAttack(this), this.fx());
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if ((this.temptGoal == null || this.temptGoal.h()) && !this.isTrusting() && this.isBreedItem(itemstack) && entityhuman.f((Entity) this) < 9.0D) {
            this.a(entityhuman, enumhand, itemstack);
            if (!this.level.isClientSide) {
                if (this.random.nextInt(3) == 0) {
                    this.setTrusting(true);
                    this.w(true);
                    this.level.broadcastEntityEffect(this, (byte) 41);
                } else {
                    this.w(false);
                    this.level.broadcastEntityEffect(this, (byte) 40);
                }
            }

            return EnumInteractionResult.a(this.level.isClientSide);
        } else {
            return super.b(entityhuman, enumhand);
        }
    }

    @Override
    public void a(byte b0) {
        if (b0 == 41) {
            this.w(true);
        } else if (b0 == 40) {
            this.w(false);
        } else {
            super.a(b0);
        }

    }

    private void w(boolean flag) {
        ParticleType particletype = Particles.HEART;

        if (!flag) {
            particletype = Particles.SMOKE;
        }

        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;

            this.level.addParticle(particletype, this.d(1.0D), this.da() + 0.5D, this.g(1.0D), d0, d1, d2);
        }

    }

    protected void t() {
        if (this.ocelotAvoidPlayersGoal == null) {
            this.ocelotAvoidPlayersGoal = new EntityOcelot.a<>(this, EntityHuman.class, 16.0F, 0.8D, 1.33D);
        }

        this.goalSelector.a((PathfinderGoal) this.ocelotAvoidPlayersGoal);
        if (!this.isTrusting()) {
            this.goalSelector.a(4, this.ocelotAvoidPlayersGoal);
        }

    }

    @Override
    public EntityOcelot createChild(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityOcelot) EntityTypes.OCELOT.a((World) worldserver);
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        return EntityOcelot.TEMPT_INGREDIENT.test(itemstack);
    }

    public static boolean c(EntityTypes<EntityOcelot> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return random.nextInt(3) != 0;
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        if (iworldreader.f((Entity) this) && !iworldreader.containsLiquid(this.getBoundingBox())) {
            BlockPosition blockposition = this.getChunkCoordinates();

            if (blockposition.getY() < iworldreader.getSeaLevel()) {
                return false;
            }

            IBlockData iblockdata = iworldreader.getType(blockposition.down());

            if (iblockdata.a(Blocks.GRASS_BLOCK) || iblockdata.a((Tag) TagsBlock.LEAVES)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity == null) {
            groupdataentity = new EntityAgeable.a(1.0F);
        }

        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    @Override
    public Vec3D cu() {
        return new Vec3D(0.0D, (double) (0.5F * this.getHeadHeight()), (double) (this.getWidth() * 0.4F));
    }

    @Override
    public boolean bE() {
        return this.getPose() == EntityPose.CROUCHING || super.bE();
    }

    private static class b extends PathfinderGoalTempt {

        private final EntityOcelot ocelot;

        public b(EntityOcelot entityocelot, double d0, RecipeItemStack recipeitemstack, boolean flag) {
            super(entityocelot, d0, recipeitemstack, flag);
            this.ocelot = entityocelot;
        }

        @Override
        protected boolean g() {
            return super.g() && !this.ocelot.isTrusting();
        }
    }

    private static class a<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityOcelot ocelot;

        public a(EntityOcelot entityocelot, Class<T> oclass, float f, double d0, double d1) {
            Predicate predicate = IEntitySelector.NO_CREATIVE_OR_SPECTATOR;

            Objects.requireNonNull(predicate);
            super(entityocelot, oclass, f, d0, d1, predicate::test);
            this.ocelot = entityocelot;
        }

        @Override
        public boolean a() {
            return !this.ocelot.isTrusting() && super.a();
        }

        @Override
        public boolean b() {
            return !this.ocelot.isTrusting() && super.b();
        }
    }
}
