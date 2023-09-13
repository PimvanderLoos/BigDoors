package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalGotoTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.PathfinderGoalZombieAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntityTurtle;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityDrowned extends EntityZombie implements IRangedEntity {

    public static final float NAUTILUS_SHELL_CHANCE = 0.03F;
    boolean searchingForLand;
    public final NavigationGuardian waterNavigation;
    public final Navigation groundNavigation;

    public EntityDrowned(EntityTypes<? extends EntityDrowned> entitytypes, World world) {
        super(entitytypes, world);
        this.maxUpStep = 1.0F;
        this.moveControl = new EntityDrowned.d(this);
        this.a(PathType.WATER, 0.0F);
        this.waterNavigation = new NavigationGuardian(this, world);
        this.groundNavigation = new Navigation(this, world);
    }

    @Override
    protected void n() {
        this.goalSelector.a(1, new EntityDrowned.c(this, 1.0D));
        this.goalSelector.a(2, new EntityDrowned.f(this, 1.0D, 40, 10.0F));
        this.goalSelector.a(2, new EntityDrowned.a(this, 1.0D, false));
        this.goalSelector.a(5, new EntityDrowned.b(this, 1.0D));
        this.goalSelector.a(6, new EntityDrowned.e(this, 1.0D, this.level.getSeaLevel()));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityDrowned.class})).a(EntityPigZombie.class));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::j));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, Axolotl.class, true, false));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        if (this.getEquipment(EnumItemSlot.OFFHAND).isEmpty() && this.random.nextFloat() < 0.03F) {
            this.setSlot(EnumItemSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
            this.handDropChances[EnumItemSlot.OFFHAND.b()] = 2.0F;
        }

        return groupdataentity;
    }

    public static boolean a(EntityTypes<EntityDrowned> entitytypes, WorldAccess worldaccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        Optional<ResourceKey<BiomeBase>> optional = worldaccess.j(blockposition);
        boolean flag = worldaccess.getDifficulty() != EnumDifficulty.PEACEFUL && a(worldaccess, blockposition, random) && (enummobspawn == EnumMobSpawn.SPAWNER || worldaccess.getFluid(blockposition).a((Tag) TagsFluid.WATER));

        return !Objects.equals(optional, Optional.of(Biomes.RIVER)) && !Objects.equals(optional, Optional.of(Biomes.FROZEN_RIVER)) ? random.nextInt(40) == 0 && a((GeneratorAccess) worldaccess, blockposition) && flag : random.nextInt(15) == 0 && flag;
    }

    private static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return blockposition.getY() < generatoraccess.getSeaLevel() - 5;
    }

    @Override
    protected boolean p() {
        return false;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isInWater() ? SoundEffects.DROWNED_AMBIENT_WATER : SoundEffects.DROWNED_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.isInWater() ? SoundEffects.DROWNED_HURT_WATER : SoundEffects.DROWNED_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return this.isInWater() ? SoundEffects.DROWNED_DEATH_WATER : SoundEffects.DROWNED_DEATH;
    }

    @Override
    protected SoundEffect getSoundStep() {
        return SoundEffects.DROWNED_STEP;
    }

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.DROWNED_SWIM;
    }

    @Override
    protected ItemStack fw() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        if ((double) this.random.nextFloat() > 0.9D) {
            int i = this.random.nextInt(16);

            if (i < 10) {
                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
            }
        }

    }

    @Override
    protected boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack1.a(Items.NAUTILUS_SHELL) ? false : (itemstack1.a(Items.TRIDENT) ? (itemstack.a(Items.TRIDENT) ? itemstack.getDamage() < itemstack1.getDamage() : false) : (itemstack.a(Items.TRIDENT) ? true : super.a(itemstack, itemstack1)));
    }

    @Override
    protected boolean fx() {
        return false;
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.f((Entity) this);
    }

    public boolean j(@Nullable EntityLiving entityliving) {
        return entityliving != null ? !this.level.isDay() || entityliving.isInWater() : false;
    }

    @Override
    public boolean ck() {
        return !this.isSwimming();
    }

    boolean fG() {
        if (this.searchingForLand) {
            return true;
        } else {
            EntityLiving entityliving = this.getGoalTarget();

            return entityliving != null && entityliving.isInWater();
        }
    }

    @Override
    public void g(Vec3D vec3d) {
        if (this.doAITick() && this.isInWater() && this.fG()) {
            this.a(0.01F, vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.9D));
        } else {
            super.g(vec3d);
        }

    }

    @Override
    public void aQ() {
        if (!this.level.isClientSide) {
            if (this.doAITick() && this.isInWater() && this.fG()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }

    }

    protected boolean fy() {
        PathEntity pathentity = this.getNavigation().k();

        if (pathentity != null) {
            BlockPosition blockposition = pathentity.m();

            if (blockposition != null) {
                double d0 = this.h((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());

                if (d0 < 4.0D) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        EntityThrownTrident entitythrowntrident = new EntityThrownTrident(this.level, this, new ItemStack(Items.TRIDENT));
        double d0 = entityliving.locX() - this.locX();
        double d1 = entityliving.e(0.3333333333333333D) - entitythrowntrident.locY();
        double d2 = entityliving.locZ() - this.locZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        entitythrowntrident.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.level.getDifficulty().a() * 4));
        this.playSound(SoundEffects.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addEntity(entitythrowntrident);
    }

    public void v(boolean flag) {
        this.searchingForLand = flag;
    }

    private static class d extends ControllerMove {

        private final EntityDrowned drowned;

        public d(EntityDrowned entitydrowned) {
            super(entitydrowned);
            this.drowned = entitydrowned;
        }

        @Override
        public void a() {
            EntityLiving entityliving = this.drowned.getGoalTarget();

            if (this.drowned.fG() && this.drowned.isInWater()) {
                if (entityliving != null && entityliving.locY() > this.drowned.locY() || this.drowned.searchingForLand) {
                    this.drowned.setMot(this.drowned.getMot().add(0.0D, 0.002D, 0.0D));
                }

                if (this.operation != ControllerMove.Operation.MOVE_TO || this.drowned.getNavigation().m()) {
                    this.drowned.r(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.drowned.locX();
                double d1 = this.wantedY - this.drowned.locY();
                double d2 = this.wantedZ - this.drowned.locZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                d1 /= d3;
                float f = (float) (MathHelper.d(d2, d0) * 57.2957763671875D) - 90.0F;

                this.drowned.setYRot(this.a(this.drowned.getYRot(), f, 90.0F));
                this.drowned.yBodyRot = this.drowned.getYRot();
                float f1 = (float) (this.speedModifier * this.drowned.b(GenericAttributes.MOVEMENT_SPEED));
                float f2 = MathHelper.h(0.125F, this.drowned.ew(), f1);

                this.drowned.r(f2);
                this.drowned.setMot(this.drowned.getMot().add((double) f2 * d0 * 0.005D, (double) f2 * d1 * 0.1D, (double) f2 * d2 * 0.005D));
            } else {
                if (!this.drowned.onGround) {
                    this.drowned.setMot(this.drowned.getMot().add(0.0D, -0.008D, 0.0D));
                }

                super.a();
            }

        }
    }

    private static class c extends PathfinderGoal {

        private final EntityCreature mob;
        private double wantedX;
        private double wantedY;
        private double wantedZ;
        private final double speedModifier;
        private final World level;

        public c(EntityCreature entitycreature, double d0) {
            this.mob = entitycreature;
            this.speedModifier = d0;
            this.level = entitycreature.level;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            if (!this.level.isDay()) {
                return false;
            } else if (this.mob.isInWater()) {
                return false;
            } else {
                Vec3D vec3d = this.g();

                if (vec3d == null) {
                    return false;
                } else {
                    this.wantedX = vec3d.x;
                    this.wantedY = vec3d.y;
                    this.wantedZ = vec3d.z;
                    return true;
                }
            }
        }

        @Override
        public boolean b() {
            return !this.mob.getNavigation().m();
        }

        @Override
        public void c() {
            this.mob.getNavigation().a(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        @Nullable
        private Vec3D g() {
            Random random = this.mob.getRandom();
            BlockPosition blockposition = this.mob.getChunkCoordinates();

            for (int i = 0; i < 10; ++i) {
                BlockPosition blockposition1 = blockposition.c(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);

                if (this.level.getType(blockposition1).a(Blocks.WATER)) {
                    return Vec3D.c((BaseBlockPosition) blockposition1);
                }
            }

            return null;
        }
    }

    private static class f extends PathfinderGoalArrowAttack {

        private final EntityDrowned drowned;

        public f(IRangedEntity irangedentity, double d0, int i, float f) {
            super(irangedentity, d0, i, f);
            this.drowned = (EntityDrowned) irangedentity;
        }

        @Override
        public boolean a() {
            return super.a() && this.drowned.getItemInMainHand().a(Items.TRIDENT);
        }

        @Override
        public void c() {
            super.c();
            this.drowned.setAggressive(true);
            this.drowned.c(EnumHand.MAIN_HAND);
        }

        @Override
        public void d() {
            super.d();
            this.drowned.clearActiveItem();
            this.drowned.setAggressive(false);
        }
    }

    private static class a extends PathfinderGoalZombieAttack {

        private final EntityDrowned drowned;

        public a(EntityDrowned entitydrowned, double d0, boolean flag) {
            super((EntityZombie) entitydrowned, d0, flag);
            this.drowned = entitydrowned;
        }

        @Override
        public boolean a() {
            return super.a() && this.drowned.j(this.drowned.getGoalTarget());
        }

        @Override
        public boolean b() {
            return super.b() && this.drowned.j(this.drowned.getGoalTarget());
        }
    }

    private static class b extends PathfinderGoalGotoTarget {

        private final EntityDrowned drowned;

        public b(EntityDrowned entitydrowned, double d0) {
            super(entitydrowned, d0, 8, 2);
            this.drowned = entitydrowned;
        }

        @Override
        public boolean a() {
            return super.a() && !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.locY() >= (double) (this.drowned.level.getSeaLevel() - 3);
        }

        @Override
        public boolean b() {
            return super.b();
        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            BlockPosition blockposition1 = blockposition.up();

            return iworldreader.isEmpty(blockposition1) && iworldreader.isEmpty(blockposition1.up()) ? iworldreader.getType(blockposition).a((IBlockAccess) iworldreader, blockposition, (Entity) this.drowned) : false;
        }

        @Override
        public void c() {
            this.drowned.v(false);
            this.drowned.navigation = this.drowned.groundNavigation;
            super.c();
        }

        @Override
        public void d() {
            super.d();
        }
    }

    private static class e extends PathfinderGoal {

        private final EntityDrowned drowned;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;

        public e(EntityDrowned entitydrowned, double d0, int i) {
            this.drowned = entitydrowned;
            this.speedModifier = d0;
            this.seaLevel = i;
        }

        @Override
        public boolean a() {
            return !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.locY() < (double) (this.seaLevel - 2);
        }

        @Override
        public boolean b() {
            return this.a() && !this.stuck;
        }

        @Override
        public void e() {
            if (this.drowned.locY() < (double) (this.seaLevel - 1) && (this.drowned.getNavigation().m() || this.drowned.fy())) {
                Vec3D vec3d = DefaultRandomPos.a(this.drowned, 4, 8, new Vec3D(this.drowned.locX(), (double) (this.seaLevel - 1), this.drowned.locZ()), 1.5707963705062866D);

                if (vec3d == null) {
                    this.stuck = true;
                    return;
                }

                this.drowned.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, this.speedModifier);
            }

        }

        @Override
        public void c() {
            this.drowned.v(true);
            this.stuck = false;
        }

        @Override
        public void d() {
            this.drowned.v(false);
        }
    }
}
