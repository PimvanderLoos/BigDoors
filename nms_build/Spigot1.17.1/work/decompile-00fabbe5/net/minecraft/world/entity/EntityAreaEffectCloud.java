package net.minecraft.world.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.ArgumentParticle;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.EnumPistonReaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAreaEffectCloud extends Entity {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int TIME_BETWEEN_APPLICATIONS = 5;
    private static final DataWatcherObject<Float> DATA_RADIUS = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Integer> DATA_COLOR = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> DATA_WAITING = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<ParticleParam> DATA_PARTICLE = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.PARTICLE);
    private static final float MAX_RADIUS = 32.0F;
    private PotionRegistry potion;
    public List<MobEffect> effects;
    private final Map<Entity, Integer> victims;
    private int duration;
    public int waitTime;
    public int reapplicationDelay;
    private boolean fixedColor;
    public int durationOnUse;
    public float radiusOnUse;
    public float radiusPerTick;
    @Nullable
    private EntityLiving owner;
    @Nullable
    private UUID ownerUUID;

    public EntityAreaEffectCloud(EntityTypes<? extends EntityAreaEffectCloud> entitytypes, World world) {
        super(entitytypes, world);
        this.potion = Potions.EMPTY;
        this.effects = Lists.newArrayList();
        this.victims = Maps.newHashMap();
        this.duration = 600;
        this.waitTime = 20;
        this.reapplicationDelay = 20;
        this.noPhysics = true;
        this.setRadius(3.0F);
    }

    public EntityAreaEffectCloud(World world, double d0, double d1, double d2) {
        this(EntityTypes.AREA_EFFECT_CLOUD, world);
        this.setPosition(d0, d1, d2);
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityAreaEffectCloud.DATA_COLOR, 0);
        this.getDataWatcher().register(EntityAreaEffectCloud.DATA_RADIUS, 0.5F);
        this.getDataWatcher().register(EntityAreaEffectCloud.DATA_WAITING, false);
        this.getDataWatcher().register(EntityAreaEffectCloud.DATA_PARTICLE, Particles.ENTITY_EFFECT);
    }

    public void setRadius(float f) {
        if (!this.level.isClientSide) {
            this.getDataWatcher().set(EntityAreaEffectCloud.DATA_RADIUS, MathHelper.a(f, 0.0F, 32.0F));
        }

    }

    @Override
    public void updateSize() {
        double d0 = this.locX();
        double d1 = this.locY();
        double d2 = this.locZ();

        super.updateSize();
        this.setPosition(d0, d1, d2);
    }

    public float getRadius() {
        return (Float) this.getDataWatcher().get(EntityAreaEffectCloud.DATA_RADIUS);
    }

    public void a(PotionRegistry potionregistry) {
        this.potion = potionregistry;
        if (!this.fixedColor) {
            this.v();
        }

    }

    private void v() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getDataWatcher().set(EntityAreaEffectCloud.DATA_COLOR, 0);
        } else {
            this.getDataWatcher().set(EntityAreaEffectCloud.DATA_COLOR, PotionUtil.a((Collection) PotionUtil.a(this.potion, (Collection) this.effects)));
        }

    }

    public void addEffect(MobEffect mobeffect) {
        this.effects.add(mobeffect);
        if (!this.fixedColor) {
            this.v();
        }

    }

    public int getColor() {
        return (Integer) this.getDataWatcher().get(EntityAreaEffectCloud.DATA_COLOR);
    }

    public void setColor(int i) {
        this.fixedColor = true;
        this.getDataWatcher().set(EntityAreaEffectCloud.DATA_COLOR, i);
    }

    public ParticleParam getParticle() {
        return (ParticleParam) this.getDataWatcher().get(EntityAreaEffectCloud.DATA_PARTICLE);
    }

    public void setParticle(ParticleParam particleparam) {
        this.getDataWatcher().set(EntityAreaEffectCloud.DATA_PARTICLE, particleparam);
    }

    protected void a(boolean flag) {
        this.getDataWatcher().set(EntityAreaEffectCloud.DATA_WAITING, flag);
    }

    public boolean l() {
        return (Boolean) this.getDataWatcher().get(EntityAreaEffectCloud.DATA_WAITING);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int i) {
        this.duration = i;
    }

    @Override
    public void tick() {
        super.tick();
        boolean flag = this.l();
        float f = this.getRadius();

        if (this.level.isClientSide) {
            if (flag && this.random.nextBoolean()) {
                return;
            }

            ParticleParam particleparam = this.getParticle();
            int i;
            float f1;

            if (flag) {
                i = 2;
                f1 = 0.2F;
            } else {
                i = MathHelper.f(3.1415927F * f * f);
                f1 = f;
            }

            for (int j = 0; j < i; ++j) {
                float f2 = this.random.nextFloat() * 6.2831855F;
                float f3 = MathHelper.c(this.random.nextFloat()) * f1;
                double d0 = this.locX() + (double) (MathHelper.cos(f2) * f3);
                double d1 = this.locY();
                double d2 = this.locZ() + (double) (MathHelper.sin(f2) * f3);
                double d3;
                double d4;
                double d5;

                if (particleparam.getParticle() == Particles.ENTITY_EFFECT) {
                    int k = flag && this.random.nextBoolean() ? 16777215 : this.getColor();

                    d3 = (double) ((float) (k >> 16 & 255) / 255.0F);
                    d4 = (double) ((float) (k >> 8 & 255) / 255.0F);
                    d5 = (double) ((float) (k & 255) / 255.0F);
                } else if (flag) {
                    d3 = 0.0D;
                    d4 = 0.0D;
                    d5 = 0.0D;
                } else {
                    d3 = (0.5D - this.random.nextDouble()) * 0.15D;
                    d4 = 0.009999999776482582D;
                    d5 = (0.5D - this.random.nextDouble()) * 0.15D;
                }

                this.level.b(particleparam, d0, d1, d2, d3, d4, d5);
            }
        } else {
            if (this.tickCount >= this.waitTime + this.duration) {
                this.die();
                return;
            }

            boolean flag1 = this.tickCount < this.waitTime;

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

            if (this.tickCount % 5 == 0) {
                this.victims.entrySet().removeIf((entry) -> {
                    return this.tickCount >= (Integer) entry.getValue();
                });
                List<MobEffect> list = Lists.newArrayList();
                Iterator iterator = this.potion.a().iterator();

                while (iterator.hasNext()) {
                    MobEffect mobeffect = (MobEffect) iterator.next();

                    list.add(new MobEffect(mobeffect.getMobEffect(), mobeffect.getDuration() / 4, mobeffect.getAmplifier(), mobeffect.isAmbient(), mobeffect.isShowParticles()));
                }

                list.addAll(this.effects);
                if (list.isEmpty()) {
                    this.victims.clear();
                } else {
                    List<EntityLiving> list1 = this.level.a(EntityLiving.class, this.getBoundingBox());

                    if (!list1.isEmpty()) {
                        Iterator iterator1 = list1.iterator();

                        while (iterator1.hasNext()) {
                            EntityLiving entityliving = (EntityLiving) iterator1.next();

                            if (!this.victims.containsKey(entityliving) && entityliving.eQ()) {
                                double d6 = entityliving.locX() - this.locX();
                                double d7 = entityliving.locZ() - this.locZ();
                                double d8 = d6 * d6 + d7 * d7;

                                if (d8 <= (double) (f * f)) {
                                    this.victims.put(entityliving, this.tickCount + this.reapplicationDelay);
                                    Iterator iterator2 = list.iterator();

                                    while (iterator2.hasNext()) {
                                        MobEffect mobeffect1 = (MobEffect) iterator2.next();

                                        if (mobeffect1.getMobEffect().isInstant()) {
                                            mobeffect1.getMobEffect().applyInstantEffect(this, this.getSource(), entityliving, mobeffect1.getAmplifier(), 0.5D);
                                        } else {
                                            entityliving.addEffect(new MobEffect(mobeffect1), this);
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
                                        this.duration += this.durationOnUse;
                                        if (this.duration <= 0) {
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

    public float o() {
        return this.radiusOnUse;
    }

    public void setRadiusOnUse(float f) {
        this.radiusOnUse = f;
    }

    public float p() {
        return this.radiusPerTick;
    }

    public void setRadiusPerTick(float f) {
        this.radiusPerTick = f;
    }

    public int q() {
        return this.durationOnUse;
    }

    public void c(int i) {
        this.durationOnUse = i;
    }

    public int r() {
        return this.waitTime;
    }

    public void setWaitTime(int i) {
        this.waitTime = i;
    }

    public void setSource(@Nullable EntityLiving entityliving) {
        this.owner = entityliving;
        this.ownerUUID = entityliving == null ? null : entityliving.getUniqueID();
    }

    @Nullable
    public EntityLiving getSource() {
        if (this.owner == null && this.ownerUUID != null && this.level instanceof WorldServer) {
            Entity entity = ((WorldServer) this.level).getEntity(this.ownerUUID);

            if (entity instanceof EntityLiving) {
                this.owner = (EntityLiving) entity;
            }
        }

        return this.owner;
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        this.tickCount = nbttagcompound.getInt("Age");
        this.duration = nbttagcompound.getInt("Duration");
        this.waitTime = nbttagcompound.getInt("WaitTime");
        this.reapplicationDelay = nbttagcompound.getInt("ReapplicationDelay");
        this.durationOnUse = nbttagcompound.getInt("DurationOnUse");
        this.radiusOnUse = nbttagcompound.getFloat("RadiusOnUse");
        this.radiusPerTick = nbttagcompound.getFloat("RadiusPerTick");
        this.setRadius(nbttagcompound.getFloat("Radius"));
        if (nbttagcompound.b("Owner")) {
            this.ownerUUID = nbttagcompound.a("Owner");
        }

        if (nbttagcompound.hasKeyOfType("Particle", 8)) {
            try {
                this.setParticle(ArgumentParticle.b(new StringReader(nbttagcompound.getString("Particle"))));
            } catch (CommandSyntaxException commandsyntaxexception) {
                EntityAreaEffectCloud.LOGGER.warn("Couldn't load custom particle {}", nbttagcompound.getString("Particle"), commandsyntaxexception);
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
                MobEffect mobeffect = MobEffect.b(nbttaglist.getCompound(i));

                if (mobeffect != null) {
                    this.addEffect(mobeffect);
                }
            }
        }

    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Age", this.tickCount);
        nbttagcompound.setInt("Duration", this.duration);
        nbttagcompound.setInt("WaitTime", this.waitTime);
        nbttagcompound.setInt("ReapplicationDelay", this.reapplicationDelay);
        nbttagcompound.setInt("DurationOnUse", this.durationOnUse);
        nbttagcompound.setFloat("RadiusOnUse", this.radiusOnUse);
        nbttagcompound.setFloat("RadiusPerTick", this.radiusPerTick);
        nbttagcompound.setFloat("Radius", this.getRadius());
        nbttagcompound.setString("Particle", this.getParticle().a());
        if (this.ownerUUID != null) {
            nbttagcompound.a("Owner", this.ownerUUID);
        }

        if (this.fixedColor) {
            nbttagcompound.setInt("Color", this.getColor());
        }

        if (this.potion != Potions.EMPTY) {
            nbttagcompound.setString("Potion", IRegistry.POTION.getKey(this.potion).toString());
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

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityAreaEffectCloud.DATA_RADIUS.equals(datawatcherobject)) {
            this.updateSize();
        }

        super.a(datawatcherobject);
    }

    public PotionRegistry t() {
        return this.potion;
    }

    @Override
    public EnumPistonReaction getPushReaction() {
        return EnumPistonReaction.IGNORE;
    }

    @Override
    public Packet<?> getPacket() {
        return new PacketPlayOutSpawnEntity(this);
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return EntitySize.b(this.getRadius() * 2.0F, 0.5F);
    }
}
