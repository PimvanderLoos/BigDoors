package net.minecraft.world.entity.animal;

import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomSwim;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityFish extends EntityWaterAnimal implements Bucketable {

    private static final DataWatcherObject<Boolean> FROM_BUCKET = DataWatcher.a(EntityFish.class, DataWatcherRegistry.BOOLEAN);

    public EntityFish(EntityTypes<? extends EntityFish> entitytypes, World world) {
        super(entitytypes, world);
        this.moveControl = new EntityFish.a(this);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.65F;
    }

    public static AttributeProvider.Builder n() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 3.0D);
    }

    @Override
    public boolean isSpecialPersistence() {
        return super.isSpecialPersistence() || this.isFromBucket();
    }

    public static boolean b(EntityTypes<? extends EntityFish> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getType(blockposition).a(Blocks.WATER) && generatoraccess.getType(blockposition.up()).a(Blocks.WATER);
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.isFromBucket() && !this.hasCustomName();
    }

    @Override
    public int getMaxSpawnGroup() {
        return 8;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityFish.FROM_BUCKET, false);
    }

    @Override
    public boolean isFromBucket() {
        return (Boolean) this.entityData.get(EntityFish.FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean flag) {
        this.entityData.set(EntityFish.FROM_BUCKET, flag);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setBoolean("FromBucket", this.isFromBucket());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setFromBucket(nbttagcompound.getBoolean("FromBucket"));
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(0, new PathfinderGoalPanic(this, 1.25D));
        PathfinderGoalSelector pathfindergoalselector = this.goalSelector;
        Predicate predicate = IEntitySelector.NO_SPECTATORS;

        Objects.requireNonNull(predicate);
        pathfindergoalselector.a(2, new PathfinderGoalAvoidTarget<>(this, EntityHuman.class, 8.0F, 1.6D, 1.4D, predicate::test));
        this.goalSelector.a(4, new EntityFish.b(this));
    }

    @Override
    protected NavigationAbstract a(World world) {
        return new NavigationGuardian(this, world);
    }

    @Override
    public void g(Vec3D vec3d) {
        if (this.doAITick() && this.isInWater()) {
            this.a(0.01F, vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.9D));
            if (this.getGoalTarget() == null) {
                this.setMot(this.getMot().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.g(vec3d);
        }

    }

    @Override
    public void movementTick() {
        if (!this.isInWater() && this.onGround && this.verticalCollision) {
            this.setMot(this.getMot().add((double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), 0.4000000059604645D, (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
            this.onGround = false;
            this.hasImpulse = true;
            this.playSound(this.getSoundFlop(), this.getSoundVolume(), this.ep());
        }

        super.movementTick();
    }

    @Override
    protected EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        return (EnumInteractionResult) Bucketable.a(entityhuman, enumhand, this).orElse(super.b(entityhuman, enumhand));
    }

    @Override
    public void setBucketName(ItemStack itemstack) {
        Bucketable.a(this, itemstack);
    }

    @Override
    public void c(NBTTagCompound nbttagcompound) {
        Bucketable.a(this, nbttagcompound);
    }

    @Override
    public SoundEffect t() {
        return SoundEffects.BUCKET_FILL_FISH;
    }

    protected boolean fw() {
        return true;
    }

    protected abstract SoundEffect getSoundFlop();

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.FISH_SWIM;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {}

    private static class a extends ControllerMove {

        private final EntityFish fish;

        a(EntityFish entityfish) {
            super(entityfish);
            this.fish = entityfish;
        }

        @Override
        public void a() {
            if (this.fish.a((Tag) TagsFluid.WATER)) {
                this.fish.setMot(this.fish.getMot().add(0.0D, 0.005D, 0.0D));
            }

            if (this.operation == ControllerMove.Operation.MOVE_TO && !this.fish.getNavigation().m()) {
                float f = (float) (this.speedModifier * this.fish.b(GenericAttributes.MOVEMENT_SPEED));

                this.fish.r(MathHelper.h(0.125F, this.fish.ew(), f));
                double d0 = this.wantedX - this.fish.locX();
                double d1 = this.wantedY - this.fish.locY();
                double d2 = this.wantedZ - this.fish.locZ();

                if (d1 != 0.0D) {
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                    this.fish.setMot(this.fish.getMot().add(0.0D, (double) this.fish.ew() * (d1 / d3) * 0.1D, 0.0D));
                }

                if (d0 != 0.0D || d2 != 0.0D) {
                    float f1 = (float) (MathHelper.d(d2, d0) * 57.2957763671875D) - 90.0F;

                    this.fish.setYRot(this.a(this.fish.getYRot(), f1, 90.0F));
                    this.fish.yBodyRot = this.fish.getYRot();
                }

            } else {
                this.fish.r(0.0F);
            }
        }
    }

    private static class b extends PathfinderGoalRandomSwim {

        private final EntityFish fish;

        public b(EntityFish entityfish) {
            super(entityfish, 1.0D, 40);
            this.fish = entityfish;
        }

        @Override
        public boolean a() {
            return this.fish.fw() && super.a();
        }
    }
}
