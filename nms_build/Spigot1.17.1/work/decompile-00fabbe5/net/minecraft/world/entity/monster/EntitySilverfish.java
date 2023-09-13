package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockMonsterEggs;
import net.minecraft.world.level.block.state.IBlockData;

public class EntitySilverfish extends EntityMonster {

    private EntitySilverfish.PathfinderGoalSilverfishWakeOthers friendsGoal;

    public EntitySilverfish(EntityTypes<? extends EntitySilverfish> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        this.friendsGoal = new EntitySilverfish.PathfinderGoalSilverfishWakeOthers(this);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(3, this.friendsGoal);
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(5, new EntitySilverfish.PathfinderGoalSilverfishHideInBlock(this));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a());
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }

    @Override
    public double bk() {
        return 0.1D;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.13F;
    }

    public static AttributeProvider.Builder n() {
        return EntityMonster.fB().a(GenericAttributes.MAX_HEALTH, 8.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.25D).a(GenericAttributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected Entity.MovementEmission aI() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.SILVERFISH_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.SILVERFISH_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.SILVERFISH_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.SILVERFISH_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if ((damagesource instanceof EntityDamageSource || damagesource == DamageSource.MAGIC) && this.friendsGoal != null) {
                this.friendsGoal.g();
            }

            return super.damageEntity(damagesource, f);
        }
    }

    @Override
    public void tick() {
        this.yBodyRot = this.getYRot();
        super.tick();
    }

    @Override
    public void m(float f) {
        this.setYRot(f);
        super.m(f);
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return BlockMonsterEggs.h(iworldreader.getType(blockposition.down())) ? 10.0F : super.a(blockposition, iworldreader);
    }

    public static boolean b(EntityTypes<EntitySilverfish> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        if (c(entitytypes, generatoraccess, enummobspawn, blockposition, random)) {
            EntityHuman entityhuman = generatoraccess.a((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, 5.0D, true);

            return entityhuman == null;
        } else {
            return false;
        }
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ARTHROPOD;
    }

    private static class PathfinderGoalSilverfishWakeOthers extends PathfinderGoal {

        private final EntitySilverfish silverfish;
        private int lookForFriends;

        public PathfinderGoalSilverfishWakeOthers(EntitySilverfish entitysilverfish) {
            this.silverfish = entitysilverfish;
        }

        public void g() {
            if (this.lookForFriends == 0) {
                this.lookForFriends = 20;
            }

        }

        @Override
        public boolean a() {
            return this.lookForFriends > 0;
        }

        @Override
        public void e() {
            --this.lookForFriends;
            if (this.lookForFriends <= 0) {
                World world = this.silverfish.level;
                Random random = this.silverfish.getRandom();
                BlockPosition blockposition = this.silverfish.getChunkCoordinates();

                for (int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i) {
                    for (int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j) {
                        for (int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k) {
                            BlockPosition blockposition1 = blockposition.c(j, i, k);
                            IBlockData iblockdata = world.getType(blockposition1);
                            Block block = iblockdata.getBlock();

                            if (block instanceof BlockMonsterEggs) {
                                if (world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                                    world.a(blockposition1, true, this.silverfish);
                                } else {
                                    world.setTypeAndData(blockposition1, ((BlockMonsterEggs) block).o(world.getType(blockposition1)), 3);
                                }

                                if (random.nextBoolean()) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private static class PathfinderGoalSilverfishHideInBlock extends PathfinderGoalRandomStroll {

        private EnumDirection selectedDirection;
        private boolean doMerge;

        public PathfinderGoalSilverfishHideInBlock(EntitySilverfish entitysilverfish) {
            super(entitysilverfish, 1.0D, 10);
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            if (this.mob.getGoalTarget() != null) {
                return false;
            } else if (!this.mob.getNavigation().m()) {
                return false;
            } else {
                Random random = this.mob.getRandom();

                if (this.mob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && random.nextInt(10) == 0) {
                    this.selectedDirection = EnumDirection.a(random);
                    BlockPosition blockposition = (new BlockPosition(this.mob.locX(), this.mob.locY() + 0.5D, this.mob.locZ())).shift(this.selectedDirection);
                    IBlockData iblockdata = this.mob.level.getType(blockposition);

                    if (BlockMonsterEggs.h(iblockdata)) {
                        this.doMerge = true;
                        return true;
                    }
                }

                this.doMerge = false;
                return super.a();
            }
        }

        @Override
        public boolean b() {
            return this.doMerge ? false : super.b();
        }

        @Override
        public void c() {
            if (!this.doMerge) {
                super.c();
            } else {
                World world = this.mob.level;
                BlockPosition blockposition = (new BlockPosition(this.mob.locX(), this.mob.locY() + 0.5D, this.mob.locZ())).shift(this.selectedDirection);
                IBlockData iblockdata = world.getType(blockposition);

                if (BlockMonsterEggs.h(iblockdata)) {
                    world.setTypeAndData(blockposition, BlockMonsterEggs.n(iblockdata), 3);
                    this.mob.doSpawnEffect();
                    this.mob.die();
                }

            }
        }
    }
}
