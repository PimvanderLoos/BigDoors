package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class EntityAreaEffectCloud extends Entity {

    private static final DataWatcherObject<Float> a = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> c = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Integer> d = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> e = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> f = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.b);
    private PotionRegistry potionRegistry;
    public List<MobEffect> effects;
    private final Map<Entity, Integer> at;
    private int au;
    public int waitTime;
    public int reapplicationDelay;
    private boolean hasColor;
    public int durationOnUse;
    public float radiusOnUse;
    public float radiusPerTick;
    private EntityLiving aB;
    private UUID aC;

    public EntityAreaEffectCloud(World world) {
        super(world);
        this.potionRegistry = Potions.EMPTY;
        this.effects = Lists.newArrayList();
        this.at = Maps.newHashMap();
        this.au = 600;
        this.waitTime = 20;
        this.reapplicationDelay = 20;
        this.noclip = true;
        this.fireProof = true;
        this.setRadius(3.0F);
    }

    public EntityAreaEffectCloud(World world, double d0, double d1, double d2) {
        this(world);
        this.setPosition(d0, d1, d2);
    }

    protected void i() {
        this.getDataWatcher().register(EntityAreaEffectCloud.b, Integer.valueOf(0));
        this.getDataWatcher().register(EntityAreaEffectCloud.a, Float.valueOf(0.5F));
        this.getDataWatcher().register(EntityAreaEffectCloud.c, Boolean.valueOf(false));
        this.getDataWatcher().register(EntityAreaEffectCloud.d, Integer.valueOf(EnumParticle.SPELL_MOB.c()));
        this.getDataWatcher().register(EntityAreaEffectCloud.e, Integer.valueOf(0));
        this.getDataWatcher().register(EntityAreaEffectCloud.f, Integer.valueOf(0));
    }

    public void setRadius(float f) {
        double d0 = this.locX;
        double d1 = this.locY;
        double d2 = this.locZ;

        this.setSize(f * 2.0F, 0.5F);
        this.setPosition(d0, d1, d2);
        if (!this.world.isClientSide) {
            this.getDataWatcher().set(EntityAreaEffectCloud.a, Float.valueOf(f));
        }

    }

    public float getRadius() {
        return ((Float) this.getDataWatcher().get(EntityAreaEffectCloud.a)).floatValue();
    }

    public void a(PotionRegistry potionregistry) {
        this.potionRegistry = potionregistry;
        if (!this.hasColor) {
            this.C();
        }

    }

    private void C() {
        if (this.potionRegistry == Potions.EMPTY && this.effects.isEmpty()) {
            this.getDataWatcher().set(EntityAreaEffectCloud.b, Integer.valueOf(0));
        } else {
            this.getDataWatcher().set(EntityAreaEffectCloud.b, Integer.valueOf(PotionUtil.a((Collection) PotionUtil.a(this.potionRegistry, (Collection) this.effects))));
        }

    }

    public void a(MobEffect mobeffect) {
        this.effects.add(mobeffect);
        if (!this.hasColor) {
            this.C();
        }

    }

    public int getColor() {
        return ((Integer) this.getDataWatcher().get(EntityAreaEffectCloud.b)).intValue();
    }

    public void setColor(int i) {
        this.hasColor = true;
        this.getDataWatcher().set(EntityAreaEffectCloud.b, Integer.valueOf(i));
    }

    public EnumParticle getParticle() {
        return EnumParticle.a(((Integer) this.getDataWatcher().get(EntityAreaEffectCloud.d)).intValue());
    }

    public void setParticle(EnumParticle enumparticle) {
        this.getDataWatcher().set(EntityAreaEffectCloud.d, Integer.valueOf(enumparticle.c()));
    }

    public int n() {
        return ((Integer) this.getDataWatcher().get(EntityAreaEffectCloud.e)).intValue();
    }

    public void c(int i) {
        this.getDataWatcher().set(EntityAreaEffectCloud.e, Integer.valueOf(i));
    }

    public int p() {
        return ((Integer) this.getDataWatcher().get(EntityAreaEffectCloud.f)).intValue();
    }

    public void d(int i) {
        this.getDataWatcher().set(EntityAreaEffectCloud.f, Integer.valueOf(i));
    }

    protected void a(boolean flag) {
        this.getDataWatcher().set(EntityAreaEffectCloud.c, Boolean.valueOf(flag));
    }

    public boolean q() {
        return ((Boolean) this.getDataWatcher().get(EntityAreaEffectCloud.c)).booleanValue();
    }

    public int getDuration() {
        return this.au;
    }

    public void setDuration(int i) {
        this.au = i;
    }

    public void B_() {
        super.B_();
        boolean flag = this.q();
        float f = this.getRadius();

        if (this.world.isClientSide) {
            EnumParticle enumparticle = this.getParticle();
            int[] aint = new int[enumparticle.d()];

            if (aint.length > 0) {
                aint[0] = this.n();
            }

            if (aint.length > 1) {
                aint[1] = this.p();
            }

            float f1;
            float f2;
            float f3;
            int i;
            int j;
            int k;

            if (flag) {
                if (this.random.nextBoolean()) {
                    for (int l = 0; l < 2; ++l) {
                        float f4 = this.random.nextFloat() * 6.2831855F;

                        f1 = MathHelper.c(this.random.nextFloat()) * 0.2F;
                        f2 = MathHelper.cos(f4) * f1;
                        f3 = MathHelper.sin(f4) * f1;
                        if (enumparticle == EnumParticle.SPELL_MOB) {
                            int i1 = this.random.nextBoolean() ? 16777215 : this.getColor();

                            i = i1 >> 16 & 255;
                            j = i1 >> 8 & 255;
                            k = i1 & 255;
                            this.world.a(EnumParticle.SPELL_MOB.c(), this.locX + (double) f2, this.locY, this.locZ + (double) f3, (double) ((float) i / 255.0F), (double) ((float) j / 255.0F), (double) ((float) k / 255.0F), new int[0]);
                        } else {
                            this.world.a(enumparticle.c(), this.locX + (double) f2, this.locY, this.locZ + (double) f3, 0.0D, 0.0D, 0.0D, aint);
                        }
                    }
                }
            } else {
                float f5 = 3.1415927F * f * f;

                for (int j1 = 0; (float) j1 < f5; ++j1) {
                    f1 = this.random.nextFloat() * 6.2831855F;
                    f2 = MathHelper.c(this.random.nextFloat()) * f;
                    f3 = MathHelper.cos(f1) * f2;
                    float f6 = MathHelper.sin(f1) * f2;

                    if (enumparticle == EnumParticle.SPELL_MOB) {
                        i = this.getColor();
                        j = i >> 16 & 255;
                        k = i >> 8 & 255;
                        int k1 = i & 255;

                        this.world.a(EnumParticle.SPELL_MOB.c(), this.locX + (double) f3, this.locY, this.locZ + (double) f6, (double) ((float) j / 255.0F), (double) ((float) k / 255.0F), (double) ((float) k1 / 255.0F), new int[0]);
                    } else {
                        this.world.a(enumparticle.c(), this.locX + (double) f3, this.locY, this.locZ + (double) f6, (0.5D - this.random.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.random.nextDouble()) * 0.15D, aint);
                    }
                }
            }
        } else {
            if (this.ticksLived >= this.waitTime + this.au) {
                this.die();
                return;
            }

            boolean flag1 = this.ticksLived < this.waitTime;

            if (flag != flag1) {
                this.a(flag1);
            }

            if (flag1) {
                return;
            }

            if (this.radiusPerTick != 0.0F) {
                f += this.radiusPerTick;
                if (f < 0.5F) {
                    this.die();
                    return;
                }

                this.setRadius(f);
            }

            if (this.ticksLived % 5 == 0) {
                Iterator iterator = this.at.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();

                    if (this.ticksLived >= ((Integer) entry.getValue()).intValue()) {
                        iterator.remove();
                    }
                }

                ArrayList arraylist = Lists.newArrayList();
                Iterator iterator1 = this.potionRegistry.a().iterator();

                while (iterator1.hasNext()) {
                    MobEffect mobeffect = (MobEffect) iterator1.next();

                    arraylist.add(new MobEffect(mobeffect.getMobEffect(), mobeffect.getDuration() / 4, mobeffect.getAmplifier(), mobeffect.isAmbient(), mobeffect.isShowParticles()));
                }

                arraylist.addAll(this.effects);
                if (arraylist.isEmpty()) {
                    this.at.clear();
                } else {
                    List list = this.world.a(EntityLiving.class, this.getBoundingBox());

                    if (!list.isEmpty()) {
                        Iterator iterator2 = list.iterator();

                        while (iterator2.hasNext()) {
                            EntityLiving entityliving = (EntityLiving) iterator2.next();

                            if (!this.at.containsKey(entityliving) && entityliving.cR()) {
                                double d0 = entityliving.locX - this.locX;
                                double d1 = entityliving.locZ - this.locZ;
                                double d2 = d0 * d0 + d1 * d1;

                                if (d2 <= (double) (f * f)) {
                                    this.at.put(entityliving, Integer.valueOf(this.ticksLived + this.reapplicationDelay));
                                    Iterator iterator3 = arraylist.iterator();

                                    while (iterator3.hasNext()) {
                                        MobEffect mobeffect1 = (MobEffect) iterator3.next();

                                        if (mobeffect1.getMobEffect().isInstant()) {
                                            mobeffect1.getMobEffect().applyInstantEffect(this, this.getSource(), entityliving, mobeffect1.getAmplifier(), 0.5D);
                                        } else {
                                            entityliving.addEffect(new MobEffect(mobeffect1));
                                        }
                                    }

                                    if (this.radiusOnUse != 0.0F) {
                                        f += this.radiusOnUse;
                                        if (f < 0.5F) {
                                            this.die();
                                            return;
                                        }

                                        this.setRadius(f);
                                    }

                                    if (this.durationOnUse != 0) {
                                        this.au += this.durationOnUse;
                                        if (this.au <= 0) {
                                            this.die();
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

    }

    public void setRadiusOnUse(float f) {
        this.radiusOnUse = f;
    }

    public void setRadiusPerTick(float f) {
        this.radiusPerTick = f;
    }

    public void setWaitTime(int i) {
        this.waitTime = i;
    }

    public void setSource(@Nullable EntityLiving entityliving) {
        this.aB = entityliving;
        this.aC = entityliving == null ? null : entityliving.getUniqueID();
    }

    @Nullable
    public EntityLiving getSource() {
        if (this.aB == null && this.aC != null && this.world instanceof WorldServer) {
            Entity entity = ((WorldServer) this.world).getEntity(this.aC);

            if (entity instanceof EntityLiving) {
                this.aB = (EntityLiving) entity;
            }
        }

        return this.aB;
    }

    protected void a(NBTTagCompound nbttagcompound) {
        this.ticksLived = nbttagcompound.getInt("Age");
        this.au = nbttagcompound.getInt("Duration");
        this.waitTime = nbttagcompound.getInt("WaitTime");
        this.reapplicationDelay = nbttagcompound.getInt("ReapplicationDelay");
        this.durationOnUse = nbttagcompound.getInt("DurationOnUse");
        this.radiusOnUse = nbttagcompound.getFloat("RadiusOnUse");
        this.radiusPerTick = nbttagcompound.getFloat("RadiusPerTick");
        this.setRadius(nbttagcompound.getFloat("Radius"));
        this.aC = nbttagcompound.a("OwnerUUID");
        if (nbttagcompound.hasKeyOfType("Particle", 8)) {
            EnumParticle enumparticle = EnumParticle.a(nbttagcompound.getString("Particle"));

            if (enumparticle != null) {
                this.setParticle(enumparticle);
                this.c(nbttagcompound.getInt("ParticleParam1"));
                this.d(nbttagcompound.getInt("ParticleParam2"));
            }
        }

        if (nbttagcompound.hasKeyOfType("Color", 99)) {
            this.setColor(nbttagcompound.getInt("Color"));
        }

        if (nbttagcompound.hasKeyOfType("Potion", 8)) {
            this.a(PotionUtil.c(nbttagcompound));
        }

        if (nbttagcompound.hasKeyOfType("Effects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Effects", 10);

            this.effects.clear();

            for (int i = 0; i < nbttaglist.size(); ++i) {
                MobEffect mobeffect = MobEffect.b(nbttaglist.get(i));

                if (mobeffect != null) {
                    this.a(mobeffect);
                }
            }
        }

    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Age", this.ticksLived);
        nbttagcompound.setInt("Duration", this.au);
        nbttagcompound.setInt("WaitTime", this.waitTime);
        nbttagcompound.setInt("ReapplicationDelay", this.reapplicationDelay);
        nbttagcompound.setInt("DurationOnUse", this.durationOnUse);
        nbttagcompound.setFloat("RadiusOnUse", this.radiusOnUse);
        nbttagcompound.setFloat("RadiusPerTick", this.radiusPerTick);
        nbttagcompound.setFloat("Radius", this.getRadius());
        nbttagcompound.setString("Particle", this.getParticle().b());
        nbttagcompound.setInt("ParticleParam1", this.n());
        nbttagcompound.setInt("ParticleParam2", this.p());
        if (this.aC != null) {
            nbttagcompound.a("OwnerUUID", this.aC);
        }

        if (this.hasColor) {
            nbttagcompound.setInt("Color", this.getColor());
        }

        if (this.potionRegistry != Potions.EMPTY && this.potionRegistry != null) {
            nbttagcompound.setString("Potion", ((MinecraftKey) PotionRegistry.a.b(this.potionRegistry)).toString());
        }

        if (!this.effects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.effects.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add(mobeffect.a(new NBTTagCompound()));
            }

            nbttagcompound.set("Effects", nbttaglist);
        }

    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityAreaEffectCloud.a.equals(datawatcherobject)) {
            this.setRadius(this.getRadius());
        }

        super.a(datawatcherobject);
    }

    public EnumPistonReaction getPushReaction() {
        return EnumPistonReaction.IGNORE;
    }
}
