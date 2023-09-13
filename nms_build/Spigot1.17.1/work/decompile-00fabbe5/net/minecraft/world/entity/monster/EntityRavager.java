package net.minecraft.world.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockLeaves;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderNormal;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityRavager extends EntityRaider {

    private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = (entity) -> {
        return entity.isAlive() && !(entity instanceof EntityRavager);
    };
    private static final double BASE_MOVEMENT_SPEED = 0.3D;
    private static final double ATTACK_MOVEMENT_SPEED = 0.35D;
    private static final int STUNNED_COLOR = 8356754;
    private static final double STUNNED_COLOR_BLUE = 0.5725490196078431D;
    private static final double STUNNED_COLOR_GREEN = 0.5137254901960784D;
    private static final double STUNNED_COLOR_RED = 0.4980392156862745D;
    private static final int ATTACK_DURATION = 10;
    public static final int STUN_DURATION = 40;
    private int attackTick;
    private int stunnedTick;
    private int roarTick;

    public EntityRavager(EntityTypes<? extends EntityRavager> entitytypes, World world) {
        super(entitytypes, world);
        this.maxUpStep = 1.0F;
        this.xpReward = 20;
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(4, new EntityRavager.a());
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 0.4D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(2, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).a());
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, true));
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    @Override
    protected void M() {
        boolean flag = !(this.getRidingPassenger() instanceof EntityInsentient) || this.getRidingPassenger().getEntityType().a((Tag) TagsEntity.RAIDERS);
        boolean flag1 = !(this.getVehicle() instanceof EntityBoat);

        this.goalSelector.a(PathfinderGoal.Type.MOVE, flag);
        this.goalSelector.a(PathfinderGoal.Type.JUMP, flag && flag1);
        this.goalSelector.a(PathfinderGoal.Type.LOOK, flag);
        this.goalSelector.a(PathfinderGoal.Type.TARGET, flag);
    }

    public static AttributeProvider.Builder n() {
        return EntityMonster.fB().a(GenericAttributes.MAX_HEALTH, 100.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.3D).a(GenericAttributes.KNOCKBACK_RESISTANCE, 0.75D).a(GenericAttributes.ATTACK_DAMAGE, 12.0D).a(GenericAttributes.ATTACK_KNOCKBACK, 1.5D).a(GenericAttributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("AttackTick", this.attackTick);
        nbttagcompound.setInt("StunTick", this.stunnedTick);
        nbttagcompound.setInt("RoarTick", this.roarTick);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.attackTick = nbttagcompound.getInt("AttackTick");
        this.stunnedTick = nbttagcompound.getInt("StunTick");
        this.roarTick = nbttagcompound.getInt("RoarTick");
    }

    @Override
    public SoundEffect t() {
        return SoundEffects.RAVAGER_CELEBRATE;
    }

    @Override
    protected NavigationAbstract a(World world) {
        return new EntityRavager.b(this, world);
    }

    @Override
    public int fa() {
        return 45;
    }

    @Override
    public double bl() {
        return 2.1D;
    }

    @Override
    public boolean fd() {
        return !this.isNoAI() && this.getRidingPassenger() instanceof EntityLiving;
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        return this.cB();
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.isAlive()) {
            if (this.isFrozen()) {
                this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0D);
            } else {
                double d0 = this.getGoalTarget() != null ? 0.35D : 0.3D;
                double d1 = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getBaseValue();

                this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(MathHelper.d(0.1D, d1, d0));
            }

            if (this.horizontalCollision && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                boolean flag = false;
                AxisAlignedBB axisalignedbb = this.getBoundingBox().g(0.2D);
                Iterator iterator = BlockPosition.b(MathHelper.floor(axisalignedbb.minX), MathHelper.floor(axisalignedbb.minY), MathHelper.floor(axisalignedbb.minZ), MathHelper.floor(axisalignedbb.maxX), MathHelper.floor(axisalignedbb.maxY), MathHelper.floor(axisalignedbb.maxZ)).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition = (BlockPosition) iterator.next();
                    IBlockData iblockdata = this.level.getType(blockposition);
                    Block block = iblockdata.getBlock();

                    if (block instanceof BlockLeaves) {
                        flag = this.level.a(blockposition, true, this) || flag;
                    }
                }

                if (!flag && this.onGround) {
                    this.jump();
                }
            }

            if (this.roarTick > 0) {
                --this.roarTick;
                if (this.roarTick == 10) {
                    this.fI();
                }
            }

            if (this.attackTick > 0) {
                --this.attackTick;
            }

            if (this.stunnedTick > 0) {
                --this.stunnedTick;
                this.fH();
                if (this.stunnedTick == 0) {
                    this.playSound(SoundEffects.RAVAGER_ROAR, 1.0F, 1.0F);
                    this.roarTick = 20;
                }
            }

        }
    }

    private void fH() {
        if (this.random.nextInt(6) == 0) {
            double d0 = this.locX() - (double) this.getWidth() * Math.sin((double) (this.yBodyRot * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);
            double d1 = this.locY() + (double) this.getHeight() - 0.3D;
            double d2 = this.locZ() + (double) this.getWidth() * Math.cos((double) (this.yBodyRot * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);

            this.level.addParticle(Particles.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
        }

    }

    @Override
    protected boolean isFrozen() {
        return super.isFrozen() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return this.stunnedTick <= 0 && this.roarTick <= 0 ? super.hasLineOfSight(entity) : false;
    }

    @Override
    protected void e(EntityLiving entityliving) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5D) {
                this.stunnedTick = 40;
                this.playSound(SoundEffects.RAVAGER_STUNNED, 1.0F, 1.0F);
                this.level.broadcastEntityEffect(this, (byte) 39);
                entityliving.collide(this);
            } else {
                this.a((Entity) entityliving);
            }

            entityliving.hurtMarked = true;
        }

    }

    private void fI() {
        if (this.isAlive()) {
            List<? extends EntityLiving> list = this.level.a(EntityLiving.class, this.getBoundingBox().g(4.0D), EntityRavager.NO_RAVAGER_AND_ALIVE);

            EntityLiving entityliving;

            for (Iterator iterator = list.iterator(); iterator.hasNext(); this.a((Entity) entityliving)) {
                entityliving = (EntityLiving) iterator.next();
                if (!(entityliving instanceof EntityIllagerAbstract)) {
                    entityliving.damageEntity(DamageSource.mobAttack(this), 6.0F);
                }
            }

            Vec3D vec3d = this.getBoundingBox().f();

            for (int i = 0; i < 40; ++i) {
                double d0 = this.random.nextGaussian() * 0.2D;
                double d1 = this.random.nextGaussian() * 0.2D;
                double d2 = this.random.nextGaussian() * 0.2D;

                this.level.addParticle(Particles.POOF, vec3d.x, vec3d.y, vec3d.z, d0, d1, d2);
            }

            this.level.a((Entity) this, GameEvent.RAVAGER_ROAR, this.cT());
        }

    }

    private void a(Entity entity) {
        double d0 = entity.locX() - this.locX();
        double d1 = entity.locZ() - this.locZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);

        entity.i(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
    }

    @Override
    public void a(byte b0) {
        if (b0 == 4) {
            this.attackTick = 10;
            this.playSound(SoundEffects.RAVAGER_ATTACK, 1.0F, 1.0F);
        } else if (b0 == 39) {
            this.stunnedTick = 40;
        }

        super.a(b0);
    }

    public int p() {
        return this.attackTick;
    }

    public int fw() {
        return this.stunnedTick;
    }

    public int fG() {
        return this.roarTick;
    }

    @Override
    public boolean attackEntity(Entity entity) {
        this.attackTick = 10;
        this.level.broadcastEntityEffect(this, (byte) 4);
        this.playSound(SoundEffects.RAVAGER_ATTACK, 1.0F, 1.0F);
        return super.attackEntity(entity);
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.RAVAGER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.RAVAGER_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.RAVAGER_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return !iworldreader.containsLiquid(this.getBoundingBox());
    }

    @Override
    public void a(int i, boolean flag) {}

    @Override
    public boolean fx() {
        return false;
    }

    private class a extends PathfinderGoalMeleeAttack {

        public a() {
            super(EntityRavager.this, 1.0D, true);
        }

        @Override
        protected double a(EntityLiving entityliving) {
            float f = EntityRavager.this.getWidth() - 0.1F;

            return (double) (f * 2.0F * f * 2.0F + entityliving.getWidth());
        }
    }

    private static class b extends Navigation {

        public b(EntityInsentient entityinsentient, World world) {
            super(entityinsentient, world);
        }

        @Override
        protected Pathfinder a(int i) {
            this.nodeEvaluator = new EntityRavager.c();
            return new Pathfinder(this.nodeEvaluator, i);
        }
    }

    private static class c extends PathfinderNormal {

        c() {}

        @Override
        protected PathType a(IBlockAccess iblockaccess, boolean flag, boolean flag1, BlockPosition blockposition, PathType pathtype) {
            return pathtype == PathType.LEAVES ? PathType.OPEN : super.a(iblockaccess, flag, flag1, blockposition, pathtype);
        }
    }
}
