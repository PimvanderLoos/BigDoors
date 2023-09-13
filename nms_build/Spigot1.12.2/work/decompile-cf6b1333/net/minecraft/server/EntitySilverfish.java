package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class EntitySilverfish extends EntityMonster {

    private EntitySilverfish.PathfinderGoalSilverfishWakeOthers a;

    public EntitySilverfish(World world) {
        super(world);
        this.setSize(0.4F, 0.3F);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntitySilverfish.class);
    }

    protected void r() {
        this.a = new EntitySilverfish.PathfinderGoalSilverfishWakeOthers(this);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(3, this.a);
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(5, new EntitySilverfish.PathfinderGoalSilverfishHideInBlock(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    }

    public double aF() {
        return 0.1D;
    }

    public float getHeadHeight() {
        return 0.1F;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(1.0D);
    }

    protected boolean playStepSound() {
        return false;
    }

    protected SoundEffect F() {
        return SoundEffects.gM;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.gO;
    }

    protected SoundEffect cf() {
        return SoundEffects.gN;
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.a(SoundEffects.gP, 0.15F, 1.0F);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if ((damagesource instanceof EntityDamageSource || damagesource == DamageSource.MAGIC) && this.a != null) {
                this.a.f();
            }

            return super.damageEntity(damagesource, f);
        }
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.v;
    }

    public void B_() {
        this.aN = this.yaw;
        super.B_();
    }

    public void h(float f) {
        this.yaw = f;
        super.h(f);
    }

    public float a(BlockPosition blockposition) {
        return this.world.getType(blockposition.down()).getBlock() == Blocks.STONE ? 10.0F : super.a(blockposition);
    }

    protected boolean s_() {
        return true;
    }

    public boolean P() {
        if (super.P()) {
            EntityHuman entityhuman = this.world.b(this, 5.0D);

            return entityhuman == null;
        } else {
            return false;
        }
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ARTHROPOD;
    }

    static class PathfinderGoalSilverfishHideInBlock extends PathfinderGoalRandomStroll {

        private EnumDirection h;
        private boolean i;

        public PathfinderGoalSilverfishHideInBlock(EntitySilverfish entitysilverfish) {
            super(entitysilverfish, 1.0D, 10);
            this.a(1);
        }

        public boolean a() {
            if (this.a.getGoalTarget() != null) {
                return false;
            } else if (!this.a.getNavigation().o()) {
                return false;
            } else {
                Random random = this.a.getRandom();

                if (this.a.world.getGameRules().getBoolean("mobGriefing") && random.nextInt(10) == 0) {
                    this.h = EnumDirection.a(random);
                    BlockPosition blockposition = (new BlockPosition(this.a.locX, this.a.locY + 0.5D, this.a.locZ)).shift(this.h);
                    IBlockData iblockdata = this.a.world.getType(blockposition);

                    if (BlockMonsterEggs.x(iblockdata)) {
                        this.i = true;
                        return true;
                    }
                }

                this.i = false;
                return super.a();
            }
        }

        public boolean b() {
            return this.i ? false : super.b();
        }

        public void c() {
            if (!this.i) {
                super.c();
            } else {
                World world = this.a.world;
                BlockPosition blockposition = (new BlockPosition(this.a.locX, this.a.locY + 0.5D, this.a.locZ)).shift(this.h);
                IBlockData iblockdata = world.getType(blockposition);

                if (BlockMonsterEggs.x(iblockdata)) {
                    world.setTypeAndData(blockposition, Blocks.MONSTER_EGG.getBlockData().set(BlockMonsterEggs.VARIANT, BlockMonsterEggs.EnumMonsterEggVarient.a(iblockdata)), 3);
                    this.a.doSpawnEffect();
                    this.a.die();
                }

            }
        }
    }

    static class PathfinderGoalSilverfishWakeOthers extends PathfinderGoal {

        private final EntitySilverfish silverfish;
        private int b;

        public PathfinderGoalSilverfishWakeOthers(EntitySilverfish entitysilverfish) {
            this.silverfish = entitysilverfish;
        }

        public void f() {
            if (this.b == 0) {
                this.b = 20;
            }

        }

        public boolean a() {
            return this.b > 0;
        }

        public void e() {
            --this.b;
            if (this.b <= 0) {
                World world = this.silverfish.world;
                Random random = this.silverfish.getRandom();
                BlockPosition blockposition = new BlockPosition(this.silverfish);

                for (int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i) {
                    for (int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j) {
                        for (int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k) {
                            BlockPosition blockposition1 = blockposition.a(j, i, k);
                            IBlockData iblockdata = world.getType(blockposition1);

                            if (iblockdata.getBlock() == Blocks.MONSTER_EGG) {
                                if (world.getGameRules().getBoolean("mobGriefing")) {
                                    world.setAir(blockposition1, true);
                                } else {
                                    world.setTypeAndData(blockposition1, ((BlockMonsterEggs.EnumMonsterEggVarient) iblockdata.get(BlockMonsterEggs.VARIANT)).d(), 3);
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
}
