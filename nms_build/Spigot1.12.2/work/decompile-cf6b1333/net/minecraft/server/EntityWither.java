package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nullable;

public class EntityWither extends EntityMonster implements IRangedEntity {

    private static final DataWatcherObject<Integer> a = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> c = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer>[] bx = new DataWatcherObject[] { EntityWither.a, EntityWither.b, EntityWither.c};
    private static final DataWatcherObject<Integer> by = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private final float[] bz = new float[2];
    private final float[] bA = new float[2];
    private final float[] bB = new float[2];
    private final float[] bC = new float[2];
    private final int[] bD = new int[2];
    private final int[] bE = new int[2];
    private int bF;
    private final BossBattleServer bG;
    private static final Predicate<Entity> bH = new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() != EnumMonsterType.UNDEAD && ((EntityLiving) entity).cS();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    };

    public EntityWither(World world) {
        super(world);
        this.bG = (BossBattleServer) (new BossBattleServer(this.getScoreboardDisplayName(), BossBattle.BarColor.PURPLE, BossBattle.BarStyle.PROGRESS)).setDarkenSky(true);
        this.setHealth(this.getMaxHealth());
        this.setSize(0.9F, 3.5F);
        this.fireProof = true;
        ((Navigation) this.getNavigation()).c(true);
        this.b_ = 50;
    }

    protected void r() {
        this.goalSelector.a(0, new EntityWither.a());
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 40, 20.0F));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 0, false, false, EntityWither.bH));
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityWither.a, Integer.valueOf(0));
        this.datawatcher.register(EntityWither.b, Integer.valueOf(0));
        this.datawatcher.register(EntityWither.c, Integer.valueOf(0));
        this.datawatcher.register(EntityWither.by, Integer.valueOf(0));
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityWither.class);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Invul", this.dm());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.g(nbttagcompound.getInt("Invul"));
        if (this.hasCustomName()) {
            this.bG.a(this.getScoreboardDisplayName());
        }

    }

    public void setCustomName(String s) {
        super.setCustomName(s);
        this.bG.a(this.getScoreboardDisplayName());
    }

    protected SoundEffect F() {
        return SoundEffects.iC;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.iF;
    }

    protected SoundEffect cf() {
        return SoundEffects.iE;
    }

    public void n() {
        this.motY *= 0.6000000238418579D;
        double d0;
        double d1;
        double d2;

        if (!this.world.isClientSide && this.m(0) > 0) {
            Entity entity = this.world.getEntity(this.m(0));

            if (entity != null) {
                if (this.locY < entity.locY || !this.dn() && this.locY < entity.locY + 5.0D) {
                    if (this.motY < 0.0D) {
                        this.motY = 0.0D;
                    }

                    this.motY += (0.5D - this.motY) * 0.6000000238418579D;
                }

                double d3 = entity.locX - this.locX;

                d0 = entity.locZ - this.locZ;
                d1 = d3 * d3 + d0 * d0;
                if (d1 > 9.0D) {
                    d2 = (double) MathHelper.sqrt(d1);
                    this.motX += (d3 / d2 * 0.5D - this.motX) * 0.6000000238418579D;
                    this.motZ += (d0 / d2 * 0.5D - this.motZ) * 0.6000000238418579D;
                }
            }
        }

        if (this.motX * this.motX + this.motZ * this.motZ > 0.05000000074505806D) {
            this.yaw = (float) MathHelper.c(this.motZ, this.motX) * 57.295776F - 90.0F;
        }

        super.n();

        int i;

        for (i = 0; i < 2; ++i) {
            this.bC[i] = this.bA[i];
            this.bB[i] = this.bz[i];
        }

        int j;

        for (i = 0; i < 2; ++i) {
            j = this.m(i + 1);
            Entity entity1 = null;

            if (j > 0) {
                entity1 = this.world.getEntity(j);
            }

            if (entity1 != null) {
                d0 = this.n(i + 1);
                d1 = this.o(i + 1);
                d2 = this.p(i + 1);
                double d4 = entity1.locX - d0;
                double d5 = entity1.locY + (double) entity1.getHeadHeight() - d1;
                double d6 = entity1.locZ - d2;
                double d7 = (double) MathHelper.sqrt(d4 * d4 + d6 * d6);
                float f = (float) (MathHelper.c(d6, d4) * 57.2957763671875D) - 90.0F;
                float f1 = (float) (-(MathHelper.c(d5, d7) * 57.2957763671875D));

                this.bz[i] = this.b(this.bz[i], f1, 40.0F);
                this.bA[i] = this.b(this.bA[i], f, 10.0F);
            } else {
                this.bA[i] = this.b(this.bA[i], this.aN, 10.0F);
            }
        }

        boolean flag = this.dn();

        for (j = 0; j < 3; ++j) {
            double d8 = this.n(j);
            double d9 = this.o(j);
            double d10 = this.p(j);

            this.world.addParticle(EnumParticle.SMOKE_NORMAL, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D, new int[0]);
            if (flag && this.world.random.nextInt(4) == 0) {
                this.world.addParticle(EnumParticle.SPELL_MOB, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D, new int[0]);
            }
        }

        if (this.dm() > 0) {
            for (j = 0; j < 3; ++j) {
                this.world.addParticle(EnumParticle.SPELL_MOB, this.locX + this.random.nextGaussian(), this.locY + (double) (this.random.nextFloat() * 3.3F), this.locZ + this.random.nextGaussian(), 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D, new int[0]);
            }
        }

    }

    protected void M() {
        int i;

        if (this.dm() > 0) {
            i = this.dm() - 1;
            if (i <= 0) {
                this.world.createExplosion(this, this.locX, this.locY + (double) this.getHeadHeight(), this.locZ, 7.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
                this.world.a(1023, new BlockPosition(this), 0);
            }

            this.g(i);
            if (this.ticksLived % 10 == 0) {
                this.heal(10.0F);
            }

        } else {
            super.M();

            int j;

            for (i = 1; i < 3; ++i) {
                if (this.ticksLived >= this.bD[i - 1]) {
                    this.bD[i - 1] = this.ticksLived + 10 + this.random.nextInt(10);
                    if (this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) {
                        int k = i - 1;
                        int l = this.bE[i - 1];

                        this.bE[k] = this.bE[i - 1] + 1;
                        if (l > 15) {
                            float f = 10.0F;
                            float f1 = 5.0F;
                            double d0 = MathHelper.a(this.random, this.locX - 10.0D, this.locX + 10.0D);
                            double d1 = MathHelper.a(this.random, this.locY - 5.0D, this.locY + 5.0D);
                            double d2 = MathHelper.a(this.random, this.locZ - 10.0D, this.locZ + 10.0D);

                            this.a(i + 1, d0, d1, d2, true);
                            this.bE[i - 1] = 0;
                        }
                    }

                    j = this.m(i);
                    if (j > 0) {
                        Entity entity = this.world.getEntity(j);

                        if (entity != null && entity.isAlive() && this.h(entity) <= 900.0D && this.hasLineOfSight(entity)) {
                            if (entity instanceof EntityHuman && ((EntityHuman) entity).abilities.isInvulnerable) {
                                this.a(i, 0);
                            } else {
                                this.a(i + 1, (EntityLiving) entity);
                                this.bD[i - 1] = this.ticksLived + 40 + this.random.nextInt(20);
                                this.bE[i - 1] = 0;
                            }
                        } else {
                            this.a(i, 0);
                        }
                    } else {
                        List list = this.world.a(EntityLiving.class, this.getBoundingBox().grow(20.0D, 8.0D, 20.0D), Predicates.and(EntityWither.bH, IEntitySelector.e));

                        for (int i1 = 0; i1 < 10 && !list.isEmpty(); ++i1) {
                            EntityLiving entityliving = (EntityLiving) list.get(this.random.nextInt(list.size()));

                            if (entityliving != this && entityliving.isAlive() && this.hasLineOfSight(entityliving)) {
                                if (entityliving instanceof EntityHuman) {
                                    if (!((EntityHuman) entityliving).abilities.isInvulnerable) {
                                        this.a(i, entityliving.getId());
                                    }
                                } else {
                                    this.a(i, entityliving.getId());
                                }
                                break;
                            }

                            list.remove(entityliving);
                        }
                    }
                }
            }

            if (this.getGoalTarget() != null) {
                this.a(0, this.getGoalTarget().getId());
            } else {
                this.a(0, 0);
            }

            if (this.bF > 0) {
                --this.bF;
                if (this.bF == 0 && this.world.getGameRules().getBoolean("mobGriefing")) {
                    i = MathHelper.floor(this.locY);
                    j = MathHelper.floor(this.locX);
                    int j1 = MathHelper.floor(this.locZ);
                    boolean flag = false;

                    for (int k1 = -1; k1 <= 1; ++k1) {
                        for (int l1 = -1; l1 <= 1; ++l1) {
                            for (int i2 = 0; i2 <= 3; ++i2) {
                                int j2 = j + k1;
                                int k2 = i + i2;
                                int l2 = j1 + l1;
                                BlockPosition blockposition = new BlockPosition(j2, k2, l2);
                                IBlockData iblockdata = this.world.getType(blockposition);
                                Block block = iblockdata.getBlock();

                                if (iblockdata.getMaterial() != Material.AIR && a(block)) {
                                    flag = this.world.setAir(blockposition, true) || flag;
                                }
                            }
                        }
                    }

                    if (flag) {
                        this.world.a((EntityHuman) null, 1022, new BlockPosition(this), 0);
                    }
                }
            }

            if (this.ticksLived % 20 == 0) {
                this.heal(1.0F);
            }

            this.bG.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    public static boolean a(Block block) {
        return block != Blocks.BEDROCK && block != Blocks.END_PORTAL && block != Blocks.END_PORTAL_FRAME && block != Blocks.COMMAND_BLOCK && block != Blocks.dc && block != Blocks.dd && block != Blocks.BARRIER && block != Blocks.STRUCTURE_BLOCK && block != Blocks.dj && block != Blocks.PISTON_EXTENSION && block != Blocks.END_GATEWAY;
    }

    public void p() {
        this.g(220);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    public void ba() {}

    public void b(EntityPlayer entityplayer) {
        super.b(entityplayer);
        this.bG.addPlayer(entityplayer);
    }

    public void c(EntityPlayer entityplayer) {
        super.c(entityplayer);
        this.bG.removePlayer(entityplayer);
    }

    private double n(int i) {
        if (i <= 0) {
            return this.locX;
        } else {
            float f = (this.aN + (float) (180 * (i - 1))) * 0.017453292F;
            float f1 = MathHelper.cos(f);

            return this.locX + (double) f1 * 1.3D;
        }
    }

    private double o(int i) {
        return i <= 0 ? this.locY + 3.0D : this.locY + 2.2D;
    }

    private double p(int i) {
        if (i <= 0) {
            return this.locZ;
        } else {
            float f = (this.aN + (float) (180 * (i - 1))) * 0.017453292F;
            float f1 = MathHelper.sin(f);

            return this.locZ + (double) f1 * 1.3D;
        }
    }

    private float b(float f, float f1, float f2) {
        float f3 = MathHelper.g(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }

    private void a(int i, EntityLiving entityliving) {
        this.a(i, entityliving.locX, entityliving.locY + (double) entityliving.getHeadHeight() * 0.5D, entityliving.locZ, i == 0 && this.random.nextFloat() < 0.001F);
    }

    private void a(int i, double d0, double d1, double d2, boolean flag) {
        this.world.a((EntityHuman) null, 1024, new BlockPosition(this), 0);
        double d3 = this.n(i);
        double d4 = this.o(i);
        double d5 = this.p(i);
        double d6 = d0 - d3;
        double d7 = d1 - d4;
        double d8 = d2 - d5;
        EntityWitherSkull entitywitherskull = new EntityWitherSkull(this.world, this, d6, d7, d8);

        if (flag) {
            entitywitherskull.setCharged(true);
        }

        entitywitherskull.locY = d4;
        entitywitherskull.locX = d3;
        entitywitherskull.locZ = d5;
        this.world.addEntity(entitywitherskull);
    }

    public void a(EntityLiving entityliving, float f) {
        this.a(0, entityliving);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource != DamageSource.DROWN && !(damagesource.getEntity() instanceof EntityWither)) {
            if (this.dm() > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                Entity entity;

                if (this.dn()) {
                    entity = damagesource.i();
                    if (entity instanceof EntityArrow) {
                        return false;
                    }
                }

                entity = damagesource.getEntity();
                if (entity != null && !(entity instanceof EntityHuman) && entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == this.getMonsterType()) {
                    return false;
                } else {
                    if (this.bF <= 0) {
                        this.bF = 20;
                    }

                    for (int i = 0; i < this.bE.length; ++i) {
                        this.bE[i] += 3;
                    }

                    return super.damageEntity(damagesource, f);
                }
            }
        } else {
            return false;
        }
    }

    protected void dropDeathLoot(boolean flag, int i) {
        EntityItem entityitem = this.a(Items.NETHER_STAR, 1);

        if (entityitem != null) {
            entityitem.v();
        }

    }

    protected void L() {
        this.ticksFarFromPlayer = 0;
    }

    public void e(float f, float f1) {}

    public void addEffect(MobEffect mobeffect) {}

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(300.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.6000000238418579D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(40.0D);
        this.getAttributeInstance(GenericAttributes.h).setValue(4.0D);
    }

    public int dm() {
        return ((Integer) this.datawatcher.get(EntityWither.by)).intValue();
    }

    public void g(int i) {
        this.datawatcher.set(EntityWither.by, Integer.valueOf(i));
    }

    public int m(int i) {
        return ((Integer) this.datawatcher.get(EntityWither.bx[i])).intValue();
    }

    public void a(int i, int j) {
        this.datawatcher.set(EntityWither.bx[i], Integer.valueOf(j));
    }

    public boolean dn() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    protected boolean n(Entity entity) {
        return false;
    }

    public boolean bf() {
        return false;
    }

    public void p(boolean flag) {}

    class a extends PathfinderGoal {

        public a() {
            this.a(7);
        }

        public boolean a() {
            return EntityWither.this.dm() > 0;
        }
    }
}
