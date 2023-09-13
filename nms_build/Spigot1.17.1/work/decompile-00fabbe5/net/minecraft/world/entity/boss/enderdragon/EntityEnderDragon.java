package net.minecraft.world.entity.boss.enderdragon;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.boss.EntityComplexPart;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerManager;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonControllerPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.IDragonController;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.dimension.end.EnderDragonBattle;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenEndTrophy;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityEnderDragon extends EntityInsentient implements IMonster {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final DataWatcherObject<Integer> DATA_PHASE = DataWatcher.a(EntityEnderDragon.class, DataWatcherRegistry.INT);
    private static final PathfinderTargetCondition CRYSTAL_DESTROY_TARGETING = PathfinderTargetCondition.a().a(64.0D);
    private static final int GROWL_INTERVAL_MIN = 200;
    private static final int GROWL_INTERVAL_MAX = 400;
    private static final float SITTING_ALLOWED_DAMAGE_PERCENTAGE = 0.25F;
    private static final String DRAGON_DEATH_TIME_KEY = "DragonDeathTime";
    private static final String DRAGON_PHASE_KEY = "DragonPhase";
    public final double[][] positions = new double[64][3];
    public int posPointer = -1;
    public final EntityComplexPart[] subEntities;
    public final EntityComplexPart head = new EntityComplexPart(this, "head", 1.0F, 1.0F);
    private final EntityComplexPart neck = new EntityComplexPart(this, "neck", 3.0F, 3.0F);
    private final EntityComplexPart body = new EntityComplexPart(this, "body", 5.0F, 3.0F);
    private final EntityComplexPart tail1 = new EntityComplexPart(this, "tail", 2.0F, 2.0F);
    private final EntityComplexPart tail2 = new EntityComplexPart(this, "tail", 2.0F, 2.0F);
    private final EntityComplexPart tail3 = new EntityComplexPart(this, "tail", 2.0F, 2.0F);
    private final EntityComplexPart wing1 = new EntityComplexPart(this, "wing", 4.0F, 2.0F);
    private final EntityComplexPart wing2 = new EntityComplexPart(this, "wing", 4.0F, 2.0F);
    public float oFlapTime;
    public float flapTime;
    public boolean inWall;
    public int dragonDeathTime;
    public float yRotA;
    @Nullable
    public EntityEnderCrystal nearestCrystal;
    @Nullable
    private final EnderDragonBattle dragonFight;
    private final DragonControllerManager phaseManager;
    private int growlTime = 100;
    private int sittingDamageReceived;
    private final PathPoint[] nodes = new PathPoint[24];
    private final int[] nodeAdjacency = new int[24];
    private final Path openSet = new Path();

    public EntityEnderDragon(EntityTypes<? extends EntityEnderDragon> entitytypes, World world) {
        super(EntityTypes.ENDER_DRAGON, world);
        this.subEntities = new EntityComplexPart[]{this.head, this.neck, this.body, this.tail1, this.tail2, this.tail3, this.wing1, this.wing2};
        this.setHealth(this.getMaxHealth());
        this.noPhysics = true;
        this.noCulling = true;
        if (world instanceof WorldServer) {
            this.dragonFight = ((WorldServer) world).getDragonBattle();
        } else {
            this.dragonFight = null;
        }

        this.phaseManager = new DragonControllerManager(this);
    }

    public static AttributeProvider.Builder n() {
        return EntityInsentient.w().a(GenericAttributes.MAX_HEALTH, 200.0D);
    }

    @Override
    public boolean aF() {
        float f = MathHelper.cos(this.flapTime * 6.2831855F);
        float f1 = MathHelper.cos(this.oFlapTime * 6.2831855F);

        return f1 <= -0.3F && f >= -0.3F;
    }

    @Override
    public void aE() {
        if (this.level.isClientSide && !this.isSilent()) {
            this.level.a(this.locX(), this.locY(), this.locZ(), SoundEffects.ENDER_DRAGON_FLAP, this.getSoundCategory(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
        }

    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register(EntityEnderDragon.DATA_PHASE, DragonControllerPhase.HOVERING.b());
    }

    public double[] a(int i, float f) {
        if (this.dV()) {
            f = 0.0F;
        }

        f = 1.0F - f;
        int j = this.posPointer - i & 63;
        int k = this.posPointer - i - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.positions[j][0];
        double d1 = MathHelper.f(this.positions[k][0] - d0);

        adouble[0] = d0 + d1 * (double) f;
        d0 = this.positions[j][1];
        d1 = this.positions[k][1] - d0;
        adouble[1] = d0 + d1 * (double) f;
        adouble[2] = MathHelper.d((double) f, this.positions[j][2], this.positions[k][2]);
        return adouble;
    }

    @Override
    public void movementTick() {
        this.au();
        if (this.level.isClientSide) {
            this.setHealth(this.getHealth());
            if (!this.isSilent() && !this.phaseManager.a().a() && --this.growlTime < 0) {
                this.level.a(this.locX(), this.locY(), this.locZ(), SoundEffects.ENDER_DRAGON_GROWL, this.getSoundCategory(), 2.5F, 0.8F + this.random.nextFloat() * 0.3F, false);
                this.growlTime = 200 + this.random.nextInt(200);
            }
        }

        this.oFlapTime = this.flapTime;
        float f;

        if (this.dV()) {
            float f1 = (this.random.nextFloat() - 0.5F) * 8.0F;

            f = (this.random.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;

            this.level.addParticle(Particles.EXPLOSION, this.locX() + (double) f1, this.locY() + 2.0D + (double) f, this.locZ() + (double) f2, 0.0D, 0.0D, 0.0D);
        } else {
            this.fx();
            Vec3D vec3d = this.getMot();

            f = 0.2F / ((float) vec3d.h() * 10.0F + 1.0F);
            f *= (float) Math.pow(2.0D, vec3d.y);
            if (this.phaseManager.a().a()) {
                this.flapTime += 0.1F;
            } else if (this.inWall) {
                this.flapTime += f * 0.5F;
            } else {
                this.flapTime += f;
            }

            this.setYRot(MathHelper.g(this.getYRot()));
            if (this.isNoAI()) {
                this.flapTime = 0.5F;
            } else {
                if (this.posPointer < 0) {
                    for (int i = 0; i < this.positions.length; ++i) {
                        this.positions[i][0] = (double) this.getYRot();
                        this.positions[i][1] = this.locY();
                    }
                }

                if (++this.posPointer == this.positions.length) {
                    this.posPointer = 0;
                }

                this.positions[this.posPointer][0] = (double) this.getYRot();
                this.positions[this.posPointer][1] = this.locY();
                double d0;
                double d1;
                double d2;
                float f3;
                float f4;
                float f5;

                if (this.level.isClientSide) {
                    if (this.lerpSteps > 0) {
                        double d3 = this.locX() + (this.lerpX - this.locX()) / (double) this.lerpSteps;

                        d0 = this.locY() + (this.lerpY - this.locY()) / (double) this.lerpSteps;
                        d1 = this.locZ() + (this.lerpZ - this.locZ()) / (double) this.lerpSteps;
                        d2 = MathHelper.f(this.lerpYRot - (double) this.getYRot());
                        this.setYRot(this.getYRot() + (float) d2 / (float) this.lerpSteps);
                        this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
                        --this.lerpSteps;
                        this.setPosition(d3, d0, d1);
                        this.setYawPitch(this.getYRot(), this.getXRot());
                    }

                    this.phaseManager.a().b();
                } else {
                    IDragonController idragoncontroller = this.phaseManager.a();

                    idragoncontroller.c();
                    if (this.phaseManager.a() != idragoncontroller) {
                        idragoncontroller = this.phaseManager.a();
                        idragoncontroller.c();
                    }

                    Vec3D vec3d1 = idragoncontroller.g();

                    if (vec3d1 != null) {
                        d0 = vec3d1.x - this.locX();
                        d1 = vec3d1.y - this.locY();
                        d2 = vec3d1.z - this.locZ();
                        double d4 = d0 * d0 + d1 * d1 + d2 * d2;
                        float f6 = idragoncontroller.f();
                        double d5 = Math.sqrt(d0 * d0 + d2 * d2);

                        if (d5 > 0.0D) {
                            d1 = MathHelper.a(d1 / d5, (double) (-f6), (double) f6);
                        }

                        this.setMot(this.getMot().add(0.0D, d1 * 0.01D, 0.0D));
                        this.setYRot(MathHelper.g(this.getYRot()));
                        Vec3D vec3d2 = vec3d1.a(this.locX(), this.locY(), this.locZ()).d();
                        Vec3D vec3d3 = (new Vec3D((double) MathHelper.sin(this.getYRot() * 0.017453292F), this.getMot().y, (double) (-MathHelper.cos(this.getYRot() * 0.017453292F)))).d();

                        f3 = Math.max(((float) vec3d3.b(vec3d2) + 0.5F) / 1.5F, 0.0F);
                        if (Math.abs(d0) > 9.999999747378752E-6D || Math.abs(d2) > 9.999999747378752E-6D) {
                            double d6 = MathHelper.a(MathHelper.f(180.0D - MathHelper.d(d0, d2) * 57.2957763671875D - (double) this.getYRot()), -50.0D, 50.0D);

                            this.yRotA *= 0.8F;
                            this.yRotA = (float) ((double) this.yRotA + d6 * (double) idragoncontroller.h());
                            this.setYRot(this.getYRot() + this.yRotA * 0.1F);
                        }

                        f4 = (float) (2.0D / (d4 + 1.0D));
                        f5 = 0.06F;
                        this.a(0.06F * (f3 * f4 + (1.0F - f4)), new Vec3D(0.0D, 0.0D, -1.0D));
                        if (this.inWall) {
                            this.move(EnumMoveType.SELF, this.getMot().a(0.800000011920929D));
                        } else {
                            this.move(EnumMoveType.SELF, this.getMot());
                        }

                        Vec3D vec3d4 = this.getMot().d();
                        double d7 = 0.8D + 0.15D * (vec3d4.b(vec3d3) + 1.0D) / 2.0D;

                        this.setMot(this.getMot().d(d7, 0.9100000262260437D, d7));
                    }
                }

                this.yBodyRot = this.getYRot();
                Vec3D[] avec3d = new Vec3D[this.subEntities.length];

                for (int j = 0; j < this.subEntities.length; ++j) {
                    avec3d[j] = new Vec3D(this.subEntities[j].locX(), this.subEntities[j].locY(), this.subEntities[j].locZ());
                }

                float f7 = (float) (this.a(5, 1.0F)[1] - this.a(10, 1.0F)[1]) * 10.0F * 0.017453292F;
                float f8 = MathHelper.cos(f7);
                float f9 = MathHelper.sin(f7);
                float f10 = this.getYRot() * 0.017453292F;
                float f11 = MathHelper.sin(f10);
                float f12 = MathHelper.cos(f10);

                this.a(this.body, (double) (f11 * 0.5F), 0.0D, (double) (-f12 * 0.5F));
                this.a(this.wing1, (double) (f12 * 4.5F), 2.0D, (double) (f11 * 4.5F));
                this.a(this.wing2, (double) (f12 * -4.5F), 2.0D, (double) (f11 * -4.5F));
                if (!this.level.isClientSide && this.hurtTime == 0) {
                    this.a(this.level.getEntities(this, this.wing1.getBoundingBox().grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D), IEntitySelector.NO_CREATIVE_OR_SPECTATOR));
                    this.a(this.level.getEntities(this, this.wing2.getBoundingBox().grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D), IEntitySelector.NO_CREATIVE_OR_SPECTATOR));
                    this.b(this.level.getEntities(this, this.head.getBoundingBox().g(1.0D), IEntitySelector.NO_CREATIVE_OR_SPECTATOR));
                    this.b(this.level.getEntities(this, this.neck.getBoundingBox().g(1.0D), IEntitySelector.NO_CREATIVE_OR_SPECTATOR));
                }

                float f13 = MathHelper.sin(this.getYRot() * 0.017453292F - this.yRotA * 0.01F);
                float f14 = MathHelper.cos(this.getYRot() * 0.017453292F - this.yRotA * 0.01F);
                float f15 = this.fw();

                this.a(this.head, (double) (f13 * 6.5F * f8), (double) (f15 + f9 * 6.5F), (double) (-f14 * 6.5F * f8));
                this.a(this.neck, (double) (f13 * 5.5F * f8), (double) (f15 + f9 * 5.5F), (double) (-f14 * 5.5F * f8));
                double[] adouble = this.a(5, 1.0F);

                int k;

                for (k = 0; k < 3; ++k) {
                    EntityComplexPart entitycomplexpart = null;

                    if (k == 0) {
                        entitycomplexpart = this.tail1;
                    }

                    if (k == 1) {
                        entitycomplexpart = this.tail2;
                    }

                    if (k == 2) {
                        entitycomplexpart = this.tail3;
                    }

                    double[] adouble1 = this.a(12 + k * 2, 1.0F);
                    float f16 = this.getYRot() * 0.017453292F + this.i(adouble1[0] - adouble[0]) * 0.017453292F;

                    f3 = MathHelper.sin(f16);
                    f4 = MathHelper.cos(f16);
                    f5 = 1.5F;
                    float f17 = (float) (k + 1) * 2.0F;

                    this.a(entitycomplexpart, (double) (-(f11 * 1.5F + f3 * f17) * f8), adouble1[1] - adouble[1] - (double) ((f17 + 1.5F) * f9) + 1.5D, (double) ((f12 * 1.5F + f4 * f17) * f8));
                }

                if (!this.level.isClientSide) {
                    this.inWall = this.b(this.head.getBoundingBox()) | this.b(this.neck.getBoundingBox()) | this.b(this.body.getBoundingBox());
                    if (this.dragonFight != null) {
                        this.dragonFight.b(this);
                    }
                }

                for (k = 0; k < this.subEntities.length; ++k) {
                    this.subEntities[k].xo = avec3d[k].x;
                    this.subEntities[k].yo = avec3d[k].y;
                    this.subEntities[k].zo = avec3d[k].z;
                    this.subEntities[k].xOld = avec3d[k].x;
                    this.subEntities[k].yOld = avec3d[k].y;
                    this.subEntities[k].zOld = avec3d[k].z;
                }

            }
        }
    }

    private void a(EntityComplexPart entitycomplexpart, double d0, double d1, double d2) {
        entitycomplexpart.setPosition(this.locX() + d0, this.locY() + d1, this.locZ() + d2);
    }

    private float fw() {
        if (this.phaseManager.a().a()) {
            return -1.0F;
        } else {
            double[] adouble = this.a(5, 1.0F);
            double[] adouble1 = this.a(0, 1.0F);

            return (float) (adouble[1] - adouble1[1]);
        }
    }

    private void fx() {
        if (this.nearestCrystal != null) {
            if (this.nearestCrystal.isRemoved()) {
                this.nearestCrystal = null;
            } else if (this.tickCount % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 1.0F);
            }
        }

        if (this.random.nextInt(10) == 0) {
            List<EntityEnderCrystal> list = this.level.a(EntityEnderCrystal.class, this.getBoundingBox().g(32.0D));
            EntityEnderCrystal entityendercrystal = null;
            double d0 = Double.MAX_VALUE;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityEnderCrystal entityendercrystal1 = (EntityEnderCrystal) iterator.next();
                double d1 = entityendercrystal1.f(this);

                if (d1 < d0) {
                    d0 = d1;
                    entityendercrystal = entityendercrystal1;
                }
            }

            this.nearestCrystal = entityendercrystal;
        }

    }

    private void a(List<Entity> list) {
        double d0 = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0D;
        double d1 = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0D;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityLiving) {
                double d2 = entity.locX() - d0;
                double d3 = entity.locZ() - d1;
                double d4 = Math.max(d2 * d2 + d3 * d3, 0.1D);

                entity.i(d2 / d4 * 4.0D, 0.20000000298023224D, d3 / d4 * 4.0D);
                if (!this.phaseManager.a().a() && ((EntityLiving) entity).dH() < entity.tickCount - 2) {
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
        return (float) MathHelper.f(d0);
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
                    IBlockData iblockdata = this.level.getType(blockposition);

                    if (!iblockdata.isAir() && iblockdata.getMaterial() != Material.FIRE) {
                        if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && !iblockdata.a((Tag) TagsBlock.DRAGON_IMMUNE)) {
                            flag1 = this.level.a(blockposition, false) || flag1;
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            BlockPosition blockposition1 = new BlockPosition(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(i1 - j + 1), k + this.random.nextInt(j1 - k + 1));

            this.level.triggerEffect(2008, blockposition1, 0);
        }

        return flag;
    }

    public boolean a(EntityComplexPart entitycomplexpart, DamageSource damagesource, float f) {
        if (this.phaseManager.a().getControllerPhase() == DragonControllerPhase.DYING) {
            return false;
        } else {
            f = this.phaseManager.a().a(damagesource, f);
            if (entitycomplexpart != this.head) {
                f = f / 4.0F + Math.min(f, 1.0F);
            }

            if (f < 0.01F) {
                return false;
            } else {
                if (damagesource.getEntity() instanceof EntityHuman || damagesource.isExplosion()) {
                    float f1 = this.getHealth();

                    this.dealDamage(damagesource, f);
                    if (this.dV() && !this.phaseManager.a().a()) {
                        this.setHealth(1.0F);
                        this.phaseManager.setControllerPhase(DragonControllerPhase.DYING);
                    }

                    if (this.phaseManager.a().a()) {
                        this.sittingDamageReceived = (int) ((float) this.sittingDamageReceived + (f1 - this.getHealth()));
                        if ((float) this.sittingDamageReceived > 0.25F * this.getMaxHealth()) {
                            this.sittingDamageReceived = 0;
                            this.phaseManager.setControllerPhase(DragonControllerPhase.TAKEOFF);
                        }
                    }
                }

                return true;
            }
        }
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (damagesource instanceof EntityDamageSource && ((EntityDamageSource) damagesource).E()) {
            this.a(this.body, damagesource, f);
        }

        return false;
    }

    protected boolean dealDamage(DamageSource damagesource, float f) {
        return super.damageEntity(damagesource, f);
    }

    @Override
    public void killEntity() {
        this.a(Entity.RemovalReason.KILLED);
        if (this.dragonFight != null) {
            this.dragonFight.b(this);
            this.dragonFight.a(this);
        }

    }

    @Override
    protected void dB() {
        if (this.dragonFight != null) {
            this.dragonFight.b(this);
        }

        ++this.dragonDeathTime;
        if (this.dragonDeathTime >= 180 && this.dragonDeathTime <= 200) {
            float f = (this.random.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;

            this.level.addParticle(Particles.EXPLOSION_EMITTER, this.locX() + (double) f, this.locY() + 2.0D + (double) f1, this.locZ() + (double) f2, 0.0D, 0.0D, 0.0D);
        }

        boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        short short0 = 500;

        if (this.dragonFight != null && !this.dragonFight.isPreviouslyKilled()) {
            short0 = 12000;
        }

        if (this.level instanceof WorldServer) {
            if (this.dragonDeathTime > 150 && this.dragonDeathTime % 5 == 0 && flag) {
                EntityExperienceOrb.a((WorldServer) this.level, this.getPositionVector(), MathHelper.d((float) short0 * 0.08F));
            }

            if (this.dragonDeathTime == 1 && !this.isSilent()) {
                this.level.b(1028, this.getChunkCoordinates(), 0);
            }
        }

        this.move(EnumMoveType.SELF, new Vec3D(0.0D, 0.10000000149011612D, 0.0D));
        this.setYRot(this.getYRot() + 20.0F);
        this.yBodyRot = this.getYRot();
        if (this.dragonDeathTime == 200 && this.level instanceof WorldServer) {
            if (flag) {
                EntityExperienceOrb.a((WorldServer) this.level, this.getPositionVector(), MathHelper.d((float) short0 * 0.2F));
            }

            if (this.dragonFight != null) {
                this.dragonFight.a(this);
            }

            this.a(Entity.RemovalReason.KILLED);
        }

    }

    public int p() {
        if (this.nodes[0] == null) {
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

                int j1 = Math.max(this.level.getSeaLevel() + 10, this.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPosition(k, 0, l)).getY() + j);

                this.nodes[i] = new PathPoint(k, j1, l);
            }

            this.nodeAdjacency[0] = 6146;
            this.nodeAdjacency[1] = 8197;
            this.nodeAdjacency[2] = 8202;
            this.nodeAdjacency[3] = 16404;
            this.nodeAdjacency[4] = 32808;
            this.nodeAdjacency[5] = 32848;
            this.nodeAdjacency[6] = 65696;
            this.nodeAdjacency[7] = 131392;
            this.nodeAdjacency[8] = 131712;
            this.nodeAdjacency[9] = 263424;
            this.nodeAdjacency[10] = 526848;
            this.nodeAdjacency[11] = 525313;
            this.nodeAdjacency[12] = 1581057;
            this.nodeAdjacency[13] = 3166214;
            this.nodeAdjacency[14] = 2138120;
            this.nodeAdjacency[15] = 6373424;
            this.nodeAdjacency[16] = 4358208;
            this.nodeAdjacency[17] = 12910976;
            this.nodeAdjacency[18] = 9044480;
            this.nodeAdjacency[19] = 9706496;
            this.nodeAdjacency[20] = 15216640;
            this.nodeAdjacency[21] = 13688832;
            this.nodeAdjacency[22] = 11763712;
            this.nodeAdjacency[23] = 8257536;
        }

        return this.q(this.locX(), this.locY(), this.locZ());
    }

    public int q(double d0, double d1, double d2) {
        float f = 10000.0F;
        int i = 0;
        PathPoint pathpoint = new PathPoint(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        byte b0 = 0;

        if (this.dragonFight == null || this.dragonFight.c() == 0) {
            b0 = 12;
        }

        for (int j = b0; j < 24; ++j) {
            if (this.nodes[j] != null) {
                float f1 = this.nodes[j].b(pathpoint);

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
            pathpoint1 = this.nodes[k];
            pathpoint1.closed = false;
            pathpoint1.f = 0.0F;
            pathpoint1.g = 0.0F;
            pathpoint1.h = 0.0F;
            pathpoint1.cameFrom = null;
            pathpoint1.heapIdx = -1;
        }

        PathPoint pathpoint2 = this.nodes[i];

        pathpoint1 = this.nodes[j];
        pathpoint2.g = 0.0F;
        pathpoint2.h = pathpoint2.a(pathpoint1);
        pathpoint2.f = pathpoint2.h;
        this.openSet.a();
        this.openSet.a(pathpoint2);
        PathPoint pathpoint3 = pathpoint2;
        byte b0 = 0;

        if (this.dragonFight == null || this.dragonFight.c() == 0) {
            b0 = 12;
        }

        label70:
        while (!this.openSet.e()) {
            PathPoint pathpoint4 = this.openSet.c();

            if (pathpoint4.equals(pathpoint1)) {
                if (pathpoint != null) {
                    pathpoint.cameFrom = pathpoint1;
                    pathpoint1 = pathpoint;
                }

                return this.a(pathpoint2, pathpoint1);
            }

            if (pathpoint4.a(pathpoint1) < pathpoint3.a(pathpoint1)) {
                pathpoint3 = pathpoint4;
            }

            pathpoint4.closed = true;
            int l = 0;
            int i1 = 0;

            while (true) {
                if (i1 < 24) {
                    if (this.nodes[i1] != pathpoint4) {
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

                    if ((this.nodeAdjacency[l] & 1 << i1) > 0) {
                        PathPoint pathpoint5 = this.nodes[i1];

                        if (!pathpoint5.closed) {
                            float f = pathpoint4.g + pathpoint4.a(pathpoint5);

                            if (!pathpoint5.c() || f < pathpoint5.g) {
                                pathpoint5.cameFrom = pathpoint4;
                                pathpoint5.g = f;
                                pathpoint5.h = pathpoint5.a(pathpoint1);
                                if (pathpoint5.c()) {
                                    this.openSet.a(pathpoint5, pathpoint5.g + pathpoint5.h);
                                } else {
                                    pathpoint5.f = pathpoint5.g + pathpoint5.h;
                                    this.openSet.a(pathpoint5);
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
                pathpoint.cameFrom = pathpoint3;
                pathpoint3 = pathpoint;
            }

            return this.a(pathpoint2, pathpoint3);
        }
    }

    private PathEntity a(PathPoint pathpoint, PathPoint pathpoint1) {
        List<PathPoint> list = Lists.newArrayList();
        PathPoint pathpoint2 = pathpoint1;

        list.add(0, pathpoint1);

        while (pathpoint2.cameFrom != null) {
            pathpoint2 = pathpoint2.cameFrom;
            list.add(0, pathpoint2);
        }

        return new PathEntity(list, new BlockPosition(pathpoint1.x, pathpoint1.y, pathpoint1.z), true);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("DragonPhase", this.phaseManager.a().getControllerPhase().b());
        nbttagcompound.setInt("DragonDeathTime", this.dragonDeathTime);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKey("DragonPhase")) {
            this.phaseManager.setControllerPhase(DragonControllerPhase.getById(nbttagcompound.getInt("DragonPhase")));
        }

        if (nbttagcompound.hasKey("DragonDeathTime")) {
            this.dragonDeathTime = nbttagcompound.getInt("DragonDeathTime");
        }

    }

    @Override
    public void checkDespawn() {}

    public EntityComplexPart[] t() {
        return this.subEntities;
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
        return SoundEffects.ENDER_DRAGON_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENDER_DRAGON_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 5.0F;
    }

    public float a(int i, double[] adouble, double[] adouble1) {
        IDragonController idragoncontroller = this.phaseManager.a();
        DragonControllerPhase<? extends IDragonController> dragoncontrollerphase = idragoncontroller.getControllerPhase();
        double d0;

        if (dragoncontrollerphase != DragonControllerPhase.LANDING && dragoncontrollerphase != DragonControllerPhase.TAKEOFF) {
            if (idragoncontroller.a()) {
                d0 = (double) i;
            } else if (i == 6) {
                d0 = 0.0D;
            } else {
                d0 = adouble1[1] - adouble[1];
            }
        } else {
            BlockPosition blockposition = this.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.END_PODIUM_LOCATION);
            double d1 = Math.max(Math.sqrt(blockposition.a((IPosition) this.getPositionVector(), true)) / 4.0D, 1.0D);

            d0 = (double) i / d1;
        }

        return (float) d0;
    }

    public Vec3D y(float f) {
        IDragonController idragoncontroller = this.phaseManager.a();
        DragonControllerPhase<? extends IDragonController> dragoncontrollerphase = idragoncontroller.getControllerPhase();
        float f1;
        Vec3D vec3d;

        if (dragoncontrollerphase != DragonControllerPhase.LANDING && dragoncontrollerphase != DragonControllerPhase.TAKEOFF) {
            if (idragoncontroller.a()) {
                float f2 = this.getXRot();

                f1 = 1.5F;
                this.setXRot(-45.0F);
                vec3d = this.e(f);
                this.setXRot(f2);
            } else {
                vec3d = this.e(f);
            }
        } else {
            BlockPosition blockposition = this.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.END_PODIUM_LOCATION);

            f1 = Math.max((float) Math.sqrt(blockposition.a((IPosition) this.getPositionVector(), true)) / 4.0F, 1.0F);
            float f3 = 6.0F / f1;
            float f4 = this.getXRot();
            float f5 = 1.5F;

            this.setXRot(-f3 * 1.5F * 5.0F);
            vec3d = this.e(f);
            this.setXRot(f4);
        }

        return vec3d;
    }

    public void a(EntityEnderCrystal entityendercrystal, BlockPosition blockposition, DamageSource damagesource) {
        EntityHuman entityhuman;

        if (damagesource.getEntity() instanceof EntityHuman) {
            entityhuman = (EntityHuman) damagesource.getEntity();
        } else {
            entityhuman = this.level.a(EntityEnderDragon.CRYSTAL_DESTROY_TARGETING, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
        }

        if (entityendercrystal == this.nearestCrystal) {
            this.a(this.head, DamageSource.d(entityhuman), 10.0F);
        }

        this.phaseManager.a().a(entityendercrystal, blockposition, damagesource, entityhuman);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityEnderDragon.DATA_PHASE.equals(datawatcherobject) && this.level.isClientSide) {
            this.phaseManager.setControllerPhase(DragonControllerPhase.getById((Integer) this.getDataWatcher().get(EntityEnderDragon.DATA_PHASE)));
        }

        super.a(datawatcherobject);
    }

    public DragonControllerManager getDragonControllerManager() {
        return this.phaseManager;
    }

    @Nullable
    public EnderDragonBattle getEnderDragonBattle() {
        return this.dragonFight;
    }

    @Override
    public boolean addEffect(MobEffect mobeffect, @Nullable Entity entity) {
        return false;
    }

    @Override
    protected boolean l(Entity entity) {
        return false;
    }

    @Override
    public boolean canPortal() {
        return false;
    }

    @Override
    public void a(PacketPlayOutSpawnEntityLiving packetplayoutspawnentityliving) {
        super.a(packetplayoutspawnentityliving);
        EntityComplexPart[] aentitycomplexpart = this.t();

        for (int i = 0; i < aentitycomplexpart.length; ++i) {
            aentitycomplexpart[i].e(i + packetplayoutspawnentityliving.b());
        }

    }

    @Override
    public boolean c(EntityLiving entityliving) {
        return entityliving.dN();
    }
}
