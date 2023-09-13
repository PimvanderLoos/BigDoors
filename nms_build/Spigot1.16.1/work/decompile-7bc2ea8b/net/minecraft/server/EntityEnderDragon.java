package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityEnderDragon extends EntityInsentient implements IMonster {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final DataWatcherObject<Integer> PHASE = DataWatcher.a(EntityEnderDragon.class, DataWatcherRegistry.b);
    private static final PathfinderTargetCondition bD = (new PathfinderTargetCondition()).a(64.0D);
    public final double[][] c = new double[64][3];
    public int d = -1;
    public final EntityComplexPart[] children;
    public final EntityComplexPart bv = new EntityComplexPart(this, "head", 1.0F, 1.0F);
    private final EntityComplexPart bF = new EntityComplexPart(this, "neck", 3.0F, 3.0F);
    private final EntityComplexPart bG = new EntityComplexPart(this, "body", 5.0F, 3.0F);
    private final EntityComplexPart bH = new EntityComplexPart(this, "tail", 2.0F, 2.0F);
    private final EntityComplexPart bI = new EntityComplexPart(this, "tail", 2.0F, 2.0F);
    private final EntityComplexPart bJ = new EntityComplexPart(this, "tail", 2.0F, 2.0F);
    private final EntityComplexPart bK = new EntityComplexPart(this, "wing", 4.0F, 2.0F);
    private final EntityComplexPart bL = new EntityComplexPart(this, "wing", 4.0F, 2.0F);
    public float bw;
    public float bx;
    public boolean by;
    public int deathAnimationTicks;
    public float bA;
    @Nullable
    public EntityEnderCrystal currentEnderCrystal;
    @Nullable
    private final EnderDragonBattle bM;
    private final DragonControllerManager bN;
    private int bO = 100;
    private int bP;
    private final PathPoint[] bQ = new PathPoint[24];
    private final int[] bR = new int[24];
    private final Path bS = new Path();

    public EntityEnderDragon(EntityTypes<? extends EntityEnderDragon> entitytypes, World world) {
        super(EntityTypes.ENDER_DRAGON, world);
        this.children = new EntityComplexPart[]{this.bv, this.bF, this.bG, this.bH, this.bI, this.bJ, this.bK, this.bL};
        this.setHealth(this.getMaxHealth());
        this.noclip = true;
        this.ac = true;
        if (world instanceof WorldServer) {
            this.bM = ((WorldServer) world).getDragonBattle();
        } else {
            this.bM = null;
        }

        this.bN = new DragonControllerManager(this);
    }

    public static AttributeProvider.Builder m() {
        return EntityInsentient.p().a(GenericAttributes.MAX_HEALTH, 200.0D);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register(EntityEnderDragon.PHASE, DragonControllerPhase.HOVER.b());
    }

    public double[] a(int i, float f) {
        if (this.dk()) {
            f = 0.0F;
        }

        f = 1.0F - f;
        int j = this.d - i & 63;
        int k = this.d - i - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.c[j][0];
        double d1 = MathHelper.g(this.c[k][0] - d0);

        adouble[0] = d0 + d1 * (double) f;
        d0 = this.c[j][1];
        d1 = this.c[k][1] - d0;
        adouble[1] = d0 + d1 * (double) f;
        adouble[2] = MathHelper.d((double) f, this.c[j][2], this.c[k][2]);
        return adouble;
    }

    @Override
    public void movementTick() {
        float f;
        float f1;

        if (this.world.isClientSide) {
            this.setHealth(this.getHealth());
            if (!this.isSilent()) {
                f = MathHelper.cos(this.bx * 6.2831855F);
                f1 = MathHelper.cos(this.bw * 6.2831855F);
                if (f1 <= -0.3F && f >= -0.3F) {
                    this.world.a(this.locX(), this.locY(), this.locZ(), SoundEffects.ENTITY_ENDER_DRAGON_FLAP, this.getSoundCategory(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
                }

                if (!this.bN.a().a() && --this.bO < 0) {
                    this.world.a(this.locX(), this.locY(), this.locZ(), SoundEffects.ENTITY_ENDER_DRAGON_GROWL, this.getSoundCategory(), 2.5F, 0.8F + this.random.nextFloat() * 0.3F, false);
                    this.bO = 200 + this.random.nextInt(200);
                }
            }
        }

        this.bw = this.bx;
        if (this.dk()) {
            f = (this.random.nextFloat() - 0.5F) * 8.0F;
            f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;

            this.world.addParticle(Particles.EXPLOSION, this.locX() + (double) f, this.locY() + 2.0D + (double) f1, this.locZ() + (double) f2, 0.0D, 0.0D, 0.0D);
        } else {
            this.eO();
            Vec3D vec3d = this.getMot();

            f1 = 0.2F / (MathHelper.sqrt(b(vec3d)) * 10.0F + 1.0F);
            f1 *= (float) Math.pow(2.0D, vec3d.y);
            if (this.bN.a().a()) {
                this.bx += 0.1F;
            } else if (this.by) {
                this.bx += f1 * 0.5F;
            } else {
                this.bx += f1;
            }

            this.yaw = MathHelper.g(this.yaw);
            if (this.isNoAI()) {
                this.bx = 0.5F;
            } else {
                if (this.d < 0) {
                    for (int i = 0; i < this.c.length; ++i) {
                        this.c[i][0] = (double) this.yaw;
                        this.c[i][1] = this.locY();
                    }
                }

                if (++this.d == this.c.length) {
                    this.d = 0;
                }

                this.c[this.d][0] = (double) this.yaw;
                this.c[this.d][1] = this.locY();
                double d0;
                double d1;
                double d2;
                float f3;
                float f4;

                if (this.world.isClientSide) {
                    if (this.bb > 0) {
                        double d3 = this.locX() + (this.bc - this.locX()) / (double) this.bb;

                        d0 = this.locY() + (this.bd - this.locY()) / (double) this.bb;
                        d1 = this.locZ() + (this.be - this.locZ()) / (double) this.bb;
                        d2 = MathHelper.g(this.bf - (double) this.yaw);
                        this.yaw = (float) ((double) this.yaw + d2 / (double) this.bb);
                        this.pitch = (float) ((double) this.pitch + (this.bg - (double) this.pitch) / (double) this.bb);
                        --this.bb;
                        this.setPosition(d3, d0, d1);
                        this.setYawPitch(this.yaw, this.pitch);
                    }

                    this.bN.a().b();
                } else {
                    IDragonController idragoncontroller = this.bN.a();

                    idragoncontroller.c();
                    if (this.bN.a() != idragoncontroller) {
                        idragoncontroller = this.bN.a();
                        idragoncontroller.c();
                    }

                    Vec3D vec3d1 = idragoncontroller.g();

                    if (vec3d1 != null) {
                        d0 = vec3d1.x - this.locX();
                        d1 = vec3d1.y - this.locY();
                        d2 = vec3d1.z - this.locZ();
                        double d4 = d0 * d0 + d1 * d1 + d2 * d2;
                        float f5 = idragoncontroller.f();
                        double d5 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

                        if (d5 > 0.0D) {
                            d1 = MathHelper.a(d1 / d5, (double) (-f5), (double) f5);
                        }

                        this.setMot(this.getMot().add(0.0D, d1 * 0.01D, 0.0D));
                        this.yaw = MathHelper.g(this.yaw);
                        double d6 = MathHelper.a(MathHelper.g(180.0D - MathHelper.d(d0, d2) * 57.2957763671875D - (double) this.yaw), -50.0D, 50.0D);
                        Vec3D vec3d2 = vec3d1.a(this.locX(), this.locY(), this.locZ()).d();
                        Vec3D vec3d3 = (new Vec3D((double) MathHelper.sin(this.yaw * 0.017453292F), this.getMot().y, (double) (-MathHelper.cos(this.yaw * 0.017453292F)))).d();

                        f3 = Math.max(((float) vec3d3.b(vec3d2) + 0.5F) / 1.5F, 0.0F);
                        this.bA *= 0.8F;
                        this.bA = (float) ((double) this.bA + d6 * (double) idragoncontroller.h());
                        this.yaw += this.bA * 0.1F;
                        f4 = (float) (2.0D / (d4 + 1.0D));
                        float f6 = 0.06F;

                        this.a(0.06F * (f3 * f4 + (1.0F - f4)), new Vec3D(0.0D, 0.0D, -1.0D));
                        if (this.by) {
                            this.move(EnumMoveType.SELF, this.getMot().a(0.800000011920929D));
                        } else {
                            this.move(EnumMoveType.SELF, this.getMot());
                        }

                        Vec3D vec3d4 = this.getMot().d();
                        double d7 = 0.8D + 0.15D * (vec3d4.b(vec3d3) + 1.0D) / 2.0D;

                        this.setMot(this.getMot().d(d7, 0.9100000262260437D, d7));
                    }
                }

                this.aH = this.yaw;
                Vec3D[] avec3d = new Vec3D[this.children.length];

                for (int j = 0; j < this.children.length; ++j) {
                    avec3d[j] = new Vec3D(this.children[j].locX(), this.children[j].locY(), this.children[j].locZ());
                }

                float f7 = (float) (this.a(5, 1.0F)[1] - this.a(10, 1.0F)[1]) * 10.0F * 0.017453292F;
                float f8 = MathHelper.cos(f7);
                float f9 = MathHelper.sin(f7);
                float f10 = this.yaw * 0.017453292F;
                float f11 = MathHelper.sin(f10);
                float f12 = MathHelper.cos(f10);

                this.a(this.bG, (double) (f11 * 0.5F), 0.0D, (double) (-f12 * 0.5F));
                this.a(this.bK, (double) (f12 * 4.5F), 2.0D, (double) (f11 * 4.5F));
                this.a(this.bL, (double) (f12 * -4.5F), 2.0D, (double) (f11 * -4.5F));
                if (!this.world.isClientSide && this.hurtTicks == 0) {
                    this.a(this.world.getEntities(this, this.bK.getBoundingBox().grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D), IEntitySelector.e));
                    this.a(this.world.getEntities(this, this.bL.getBoundingBox().grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D), IEntitySelector.e));
                    this.b(this.world.getEntities(this, this.bv.getBoundingBox().g(1.0D), IEntitySelector.e));
                    this.b(this.world.getEntities(this, this.bF.getBoundingBox().g(1.0D), IEntitySelector.e));
                }

                float f13 = MathHelper.sin(this.yaw * 0.017453292F - this.bA * 0.01F);
                float f14 = MathHelper.cos(this.yaw * 0.017453292F - this.bA * 0.01F);
                float f15 = this.eN();

                this.a(this.bv, (double) (f13 * 6.5F * f8), (double) (f15 + f9 * 6.5F), (double) (-f14 * 6.5F * f8));
                this.a(this.bF, (double) (f13 * 5.5F * f8), (double) (f15 + f9 * 5.5F), (double) (-f14 * 5.5F * f8));
                double[] adouble = this.a(5, 1.0F);

                int k;

                for (k = 0; k < 3; ++k) {
                    EntityComplexPart entitycomplexpart = null;

                    if (k == 0) {
                        entitycomplexpart = this.bH;
                    }

                    if (k == 1) {
                        entitycomplexpart = this.bI;
                    }

                    if (k == 2) {
                        entitycomplexpart = this.bJ;
                    }

                    double[] adouble1 = this.a(12 + k * 2, 1.0F);
                    float f16 = this.yaw * 0.017453292F + this.i(adouble1[0] - adouble[0]) * 0.017453292F;
                    float f17 = MathHelper.sin(f16);
                    float f18 = MathHelper.cos(f16);

                    f3 = 1.5F;
                    f4 = (float) (k + 1) * 2.0F;
                    this.a(entitycomplexpart, (double) (-(f11 * 1.5F + f17 * f4) * f8), adouble1[1] - adouble[1] - (double) ((f4 + 1.5F) * f9) + 1.5D, (double) ((f12 * 1.5F + f18 * f4) * f8));
                }

                if (!this.world.isClientSide) {
                    this.by = this.b(this.bv.getBoundingBox()) | this.b(this.bF.getBoundingBox()) | this.b(this.bG.getBoundingBox());
                    if (this.bM != null) {
                        this.bM.b(this);
                    }
                }

                for (k = 0; k < this.children.length; ++k) {
                    this.children[k].lastX = avec3d[k].x;
                    this.children[k].lastY = avec3d[k].y;
                    this.children[k].lastZ = avec3d[k].z;
                    this.children[k].D = avec3d[k].x;
                    this.children[k].E = avec3d[k].y;
                    this.children[k].F = avec3d[k].z;
                }

            }
        }
    }

    private void a(EntityComplexPart entitycomplexpart, double d0, double d1, double d2) {
        entitycomplexpart.setPosition(this.locX() + d0, this.locY() + d1, this.locZ() + d2);
    }

    private float eN() {
        if (this.bN.a().a()) {
            return -1.0F;
        } else {
            double[] adouble = this.a(5, 1.0F);
            double[] adouble1 = this.a(0, 1.0F);

            return (float) (adouble[1] - adouble1[1]);
        }
    }

    private void eO() {
        if (this.currentEnderCrystal != null) {
            if (this.currentEnderCrystal.dead) {
                this.currentEnderCrystal = null;
            } else if (this.ticksLived % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 1.0F);
            }
        }

        if (this.random.nextInt(10) == 0) {
            List<EntityEnderCrystal> list = this.world.a(EntityEnderCrystal.class, this.getBoundingBox().g(32.0D));
            EntityEnderCrystal entityendercrystal = null;
            double d0 = Double.MAX_VALUE;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityEnderCrystal entityendercrystal1 = (EntityEnderCrystal) iterator.next();
                double d1 = entityendercrystal1.h(this);

                if (d1 < d0) {
                    d0 = d1;
                    entityendercrystal = entityendercrystal1;
                }
            }

            this.currentEnderCrystal = entityendercrystal;
        }

    }

    private void a(List<Entity> list) {
        double d0 = (this.bG.getBoundingBox().minX + this.bG.getBoundingBox().maxX) / 2.0D;
        double d1 = (this.bG.getBoundingBox().minZ + this.bG.getBoundingBox().maxZ) / 2.0D;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityLiving) {
                double d2 = entity.locX() - d0;
                double d3 = entity.locZ() - d1;
                double d4 = d2 * d2 + d3 * d3;

                entity.h(d2 / d4 * 4.0D, 0.20000000298023224D, d3 / d4 * 4.0D);
                if (!this.bN.a().a() && ((EntityLiving) entity).cZ() < entity.ticksLived - 2) {
                    entity.damageEntity(DamageSource.mobAttack(this), 5.0F);
                    this.a((EntityLiving) this, entity);
                }
            }
        }

    }

    private void b(List<Entity> list) {
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityLiving) {
                entity.damageEntity(DamageSource.mobAttack(this), 10.0F);
                this.a((EntityLiving) this, entity);
            }
        }

    }

    private float i(double d0) {
        return (float) MathHelper.g(d0);
    }

    private boolean b(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.floor(axisalignedbb.minY);
        int k = MathHelper.floor(axisalignedbb.minZ);
        int l = MathHelper.floor(axisalignedbb.maxX);
        int i1 = MathHelper.floor(axisalignedbb.maxY);
        int j1 = MathHelper.floor(axisalignedbb.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for (int k1 = i; k1 <= l; ++k1) {
            for (int l1 = j; l1 <= i1; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    BlockPosition blockposition = new BlockPosition(k1, l1, i2);
                    IBlockData iblockdata = this.world.getType(blockposition);
                    Block block = iblockdata.getBlock();

                    if (!iblockdata.isAir() && iblockdata.getMaterial() != Material.FIRE) {
                        if (this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) && !TagsBlock.DRAGON_IMMUNE.isTagged(block)) {
                            flag1 = this.world.a(blockposition, false) || flag1;
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            BlockPosition blockposition1 = new BlockPosition(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(i1 - j + 1), k + this.random.nextInt(j1 - k + 1));

            this.world.triggerEffect(2008, blockposition1, 0);
        }

        return flag;
    }

    public boolean a(EntityComplexPart entitycomplexpart, DamageSource damagesource, float f) {
        if (this.bN.a().getControllerPhase() == DragonControllerPhase.DYING) {
            return false;
        } else {
            f = this.bN.a().a(damagesource, f);
            if (entitycomplexpart != this.bv) {
                f = f / 4.0F + Math.min(f, 1.0F);
            }

            if (f < 0.01F) {
                return false;
            } else {
                if (damagesource.getEntity() instanceof EntityHuman || damagesource.isExplosion()) {
                    float f1 = this.getHealth();

                    this.dealDamage(damagesource, f);
                    if (this.dk() && !this.bN.a().a()) {
                        this.setHealth(1.0F);
                        this.bN.setControllerPhase(DragonControllerPhase.DYING);
                    }

                    if (this.bN.a().a()) {
                        this.bP = (int) ((float) this.bP + (f1 - this.getHealth()));
                        if ((float) this.bP > 0.25F * this.getMaxHealth()) {
                            this.bP = 0;
                            this.bN.setControllerPhase(DragonControllerPhase.TAKEOFF);
                        }
                    }
                }

                return true;
            }
        }
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (damagesource instanceof EntityDamageSource && ((EntityDamageSource) damagesource).y()) {
            this.a(this.bG, damagesource, f);
        }

        return false;
    }

    protected boolean dealDamage(DamageSource damagesource, float f) {
        return super.damageEntity(damagesource, f);
    }

    @Override
    public void killEntity() {
        this.die();
        if (this.bM != null) {
            this.bM.b(this);
            this.bM.a(this);
        }

    }

    @Override
    protected void cT() {
        if (this.bM != null) {
            this.bM.b(this);
        }

        ++this.deathAnimationTicks;
        if (this.deathAnimationTicks >= 180 && this.deathAnimationTicks <= 200) {
            float f = (this.random.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;

            this.world.addParticle(Particles.EXPLOSION_EMITTER, this.locX() + (double) f, this.locY() + 2.0D + (double) f1, this.locZ() + (double) f2, 0.0D, 0.0D, 0.0D);
        }

        boolean flag = this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT);
        short short0 = 500;

        if (this.bM != null && !this.bM.isPreviouslyKilled()) {
            short0 = 12000;
        }

        if (!this.world.isClientSide) {
            if (this.deathAnimationTicks > 150 && this.deathAnimationTicks % 5 == 0 && flag) {
                this.a(MathHelper.d((float) short0 * 0.08F));
            }

            if (this.deathAnimationTicks == 1 && !this.isSilent()) {
                this.world.b(1028, this.getChunkCoordinates(), 0);
            }
        }

        this.move(EnumMoveType.SELF, new Vec3D(0.0D, 0.10000000149011612D, 0.0D));
        this.yaw += 20.0F;
        this.aH = this.yaw;
        if (this.deathAnimationTicks == 200 && !this.world.isClientSide) {
            if (flag) {
                this.a(MathHelper.d((float) short0 * 0.2F));
            }

            if (this.bM != null) {
                this.bM.a(this);
            }

            this.die();
        }

    }

    private void a(int i) {
        while (i > 0) {
            int j = EntityExperienceOrb.getOrbValue(i);

            i -= j;
            this.world.addEntity(new EntityExperienceOrb(this.world, this.locX(), this.locY(), this.locZ(), j));
        }

    }

    public int eJ() {
        if (this.bQ[0] == null) {
            for (int i = 0; i < 24; ++i) {
                int j = 5;
                int k;
                int l;

                if (i < 12) {
                    k = MathHelper.d(60.0F * MathHelper.cos(2.0F * (-3.1415927F + 0.2617994F * (float) i)));
                    l = MathHelper.d(60.0F * MathHelper.sin(2.0F * (-3.1415927F + 0.2617994F * (float) i)));
                } else {
                    int i1;

                    if (i < 20) {
                        i1 = i - 12;
                        k = MathHelper.d(40.0F * MathHelper.cos(2.0F * (-3.1415927F + 0.3926991F * (float) i1)));
                        l = MathHelper.d(40.0F * MathHelper.sin(2.0F * (-3.1415927F + 0.3926991F * (float) i1)));
                        j += 10;
                    } else {
                        i1 = i - 20;
                        k = MathHelper.d(20.0F * MathHelper.cos(2.0F * (-3.1415927F + 0.7853982F * (float) i1)));
                        l = MathHelper.d(20.0F * MathHelper.sin(2.0F * (-3.1415927F + 0.7853982F * (float) i1)));
                    }
                }

                int j1 = Math.max(this.world.getSeaLevel() + 10, this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPosition(k, 0, l)).getY() + j);

                this.bQ[i] = new PathPoint(k, j1, l);
            }

            this.bR[0] = 6146;
            this.bR[1] = 8197;
            this.bR[2] = 8202;
            this.bR[3] = 16404;
            this.bR[4] = 32808;
            this.bR[5] = 32848;
            this.bR[6] = 65696;
            this.bR[7] = 131392;
            this.bR[8] = 131712;
            this.bR[9] = 263424;
            this.bR[10] = 526848;
            this.bR[11] = 525313;
            this.bR[12] = 1581057;
            this.bR[13] = 3166214;
            this.bR[14] = 2138120;
            this.bR[15] = 6373424;
            this.bR[16] = 4358208;
            this.bR[17] = 12910976;
            this.bR[18] = 9044480;
            this.bR[19] = 9706496;
            this.bR[20] = 15216640;
            this.bR[21] = 13688832;
            this.bR[22] = 11763712;
            this.bR[23] = 8257536;
        }

        return this.o(this.locX(), this.locY(), this.locZ());
    }

    public int o(double d0, double d1, double d2) {
        float f = 10000.0F;
        int i = 0;
        PathPoint pathpoint = new PathPoint(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        byte b0 = 0;

        if (this.bM == null || this.bM.c() == 0) {
            b0 = 12;
        }

        for (int j = b0; j < 24; ++j) {
            if (this.bQ[j] != null) {
                float f1 = this.bQ[j].b(pathpoint);

                if (f1 < f) {
                    f = f1;
                    i = j;
                }
            }
        }

        return i;
    }

    @Nullable
    public PathEntity a(int i, int j, @Nullable PathPoint pathpoint) {
        PathPoint pathpoint1;

        for (int k = 0; k < 24; ++k) {
            pathpoint1 = this.bQ[k];
            pathpoint1.i = false;
            pathpoint1.g = 0.0F;
            pathpoint1.e = 0.0F;
            pathpoint1.f = 0.0F;
            pathpoint1.h = null;
            pathpoint1.d = -1;
        }

        PathPoint pathpoint2 = this.bQ[i];

        pathpoint1 = this.bQ[j];
        pathpoint2.e = 0.0F;
        pathpoint2.f = pathpoint2.a(pathpoint1);
        pathpoint2.g = pathpoint2.f;
        this.bS.a();
        this.bS.a(pathpoint2);
        PathPoint pathpoint3 = pathpoint2;
        byte b0 = 0;

        if (this.bM == null || this.bM.c() == 0) {
            b0 = 12;
        }

        label70:
        while (!this.bS.e()) {
            PathPoint pathpoint4 = this.bS.c();

            if (pathpoint4.equals(pathpoint1)) {
                if (pathpoint != null) {
                    pathpoint.h = pathpoint1;
                    pathpoint1 = pathpoint;
                }

                return this.a(pathpoint2, pathpoint1);
            }

            if (pathpoint4.a(pathpoint1) < pathpoint3.a(pathpoint1)) {
                pathpoint3 = pathpoint4;
            }

            pathpoint4.i = true;
            int l = 0;
            int i1 = 0;

            while (true) {
                if (i1 < 24) {
                    if (this.bQ[i1] != pathpoint4) {
                        ++i1;
                        continue;
                    }

                    l = i1;
                }

                i1 = b0;

                while (true) {
                    if (i1 >= 24) {
                        continue label70;
                    }

                    if ((this.bR[l] & 1 << i1) > 0) {
                        PathPoint pathpoint5 = this.bQ[i1];

                        if (!pathpoint5.i) {
                            float f = pathpoint4.e + pathpoint4.a(pathpoint5);

                            if (!pathpoint5.c() || f < pathpoint5.e) {
                                pathpoint5.h = pathpoint4;
                                pathpoint5.e = f;
                                pathpoint5.f = pathpoint5.a(pathpoint1);
                                if (pathpoint5.c()) {
                                    this.bS.a(pathpoint5, pathpoint5.e + pathpoint5.f);
                                } else {
                                    pathpoint5.g = pathpoint5.e + pathpoint5.f;
                                    this.bS.a(pathpoint5);
                                }
                            }
                        }
                    }

                    ++i1;
                }
            }
        }

        if (pathpoint3 == pathpoint2) {
            return null;
        } else {
            EntityEnderDragon.LOGGER.debug("Failed to find path from {} to {}", i, j);
            if (pathpoint != null) {
                pathpoint.h = pathpoint3;
                pathpoint3 = pathpoint;
            }

            return this.a(pathpoint2, pathpoint3);
        }
    }

    private PathEntity a(PathPoint pathpoint, PathPoint pathpoint1) {
        List<PathPoint> list = Lists.newArrayList();
        PathPoint pathpoint2 = pathpoint1;

        list.add(0, pathpoint1);

        while (pathpoint2.h != null) {
            pathpoint2 = pathpoint2.h;
            list.add(0, pathpoint2);
        }

        return new PathEntity(list, new BlockPosition(pathpoint1.a, pathpoint1.b, pathpoint1.c), true);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("DragonPhase", this.bN.a().getControllerPhase().b());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKey("DragonPhase")) {
            this.bN.setControllerPhase(DragonControllerPhase.getById(nbttagcompound.getInt("DragonPhase")));
        }

    }

    @Override
    public void checkDespawn() {}

    public EntityComplexPart[] eK() {
        return this.children;
    }

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ENDER_DRAGON_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0F;
    }

    public Vec3D t(float f) {
        IDragonController idragoncontroller = this.bN.a();
        DragonControllerPhase<? extends IDragonController> dragoncontrollerphase = idragoncontroller.getControllerPhase();
        float f1;
        Vec3D vec3d;

        if (dragoncontrollerphase != DragonControllerPhase.LANDING && dragoncontrollerphase != DragonControllerPhase.TAKEOFF) {
            if (idragoncontroller.a()) {
                float f2 = this.pitch;

                f1 = 1.5F;
                this.pitch = -45.0F;
                vec3d = this.f(f);
                this.pitch = f2;
            } else {
                vec3d = this.f(f);
            }
        } else {
            BlockPosition blockposition = this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.a);

            f1 = Math.max(MathHelper.sqrt(blockposition.a(this.getPositionVector(), true)) / 4.0F, 1.0F);
            float f3 = 6.0F / f1;
            float f4 = this.pitch;
            float f5 = 1.5F;

            this.pitch = -f3 * 1.5F * 5.0F;
            vec3d = this.f(f);
            this.pitch = f4;
        }

        return vec3d;
    }

    public void a(EntityEnderCrystal entityendercrystal, BlockPosition blockposition, DamageSource damagesource) {
        EntityHuman entityhuman;

        if (damagesource.getEntity() instanceof EntityHuman) {
            entityhuman = (EntityHuman) damagesource.getEntity();
        } else {
            entityhuman = this.world.a(EntityEnderDragon.bD, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
        }

        if (entityendercrystal == this.currentEnderCrystal) {
            this.a(this.bv, DamageSource.d(entityhuman), 10.0F);
        }

        this.bN.a().a(entityendercrystal, blockposition, damagesource, entityhuman);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityEnderDragon.PHASE.equals(datawatcherobject) && this.world.isClientSide) {
            this.bN.setControllerPhase(DragonControllerPhase.getById((Integer) this.getDataWatcher().get(EntityEnderDragon.PHASE)));
        }

        super.a(datawatcherobject);
    }

    public DragonControllerManager getDragonControllerManager() {
        return this.bN;
    }

    @Nullable
    public EnderDragonBattle getEnderDragonBattle() {
        return this.bM;
    }

    @Override
    public boolean addEffect(MobEffect mobeffect) {
        return false;
    }

    @Override
    protected boolean n(Entity entity) {
        return false;
    }

    @Override
    public boolean canPortal() {
        return false;
    }
}
