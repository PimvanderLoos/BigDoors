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
    private static final DataWatcherObject<Float> DATA_RADIUS = DataWatcher.defineId(EntityAreaEffectCloud.class, DataWatcherRegistry.FLOAT);
    private static final DataWatcherObject<Integer> DATA_COLOR = DataWatcher.defineId(EntityAreaEffectCloud.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Boolean> DATA_WAITING = DataWatcher.defineId(EntityAreaEffectCloud.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<ParticleParam> DATA_PARTICLE = DataWatcher.defineId(EntityAreaEffectCloud.class, DataWatcherRegistry.PARTICLE);
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
        this.setPos(d0, d1, d2);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(EntityAreaEffectCloud.DATA_COLOR, 0);
        this.getEntityData().define(EntityAreaEffectCloud.DATA_RADIUS, 0.5F);
        this.getEntityData().define(EntityAreaEffectCloud.DATA_WAITING, false);
        this.getEntityData().define(EntityAreaEffectCloud.DATA_PARTICLE, Particles.ENTITY_EFFECT);
    }

    public void setRadius(float f) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(EntityAreaEffectCloud.DATA_RADIUS, MathHelper.clamp(f, 0.0F, 32.0F));
        }

    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();

        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public float getRadius() {
        return (Float) this.getEntityData().get(EntityAreaEffectCloud.DATA_RADIUS);
    }

    public void setPotion(PotionRegistry potionregistry) {
        this.potion = potionregistry;
        if (!this.fixedColor) {
            this.updateColor();
        }

    }

    private void updateColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getEntityData().set(EntityAreaEffectCloud.DATA_COLOR, 0);
        } else {
            this.getEntityData().set(EntityAreaEffectCloud.DATA_COLOR, PotionUtil.getColor((Collection) PotionUtil.getAllEffects(this.potion, this.effects)));
        }

    }

    public void addEffect(MobEffect mobeffect) {
        this.effects.add(mobeffect);
        if (!this.fixedColor) {
            this.updateColor();
        }

    }

    public int getColor() {
        return (Integer) this.getEntityData().get(EntityAreaEffectCloud.DATA_COLOR);
    }

    public void setFixedColor(int i) {
        this.fixedColor = true;
        this.getEntityData().set(EntityAreaEffectCloud.DATA_COLOR, i);
    }

    public ParticleParam getParticle() {
        return (ParticleParam) this.getEntityData().get(EntityAreaEffectCloud.DATA_PARTICLE);
    }

    public void setParticle(ParticleParam particleparam) {
        this.getEntityData().set(EntityAreaEffectCloud.DATA_PARTICLE, particleparam);
    }

    protected void setWaiting(boolean flag) {
        this.getEntityData().set(EntityAreaEffectCloud.DATA_WAITING, flag);
    }

    public boolean isWaiting() {
        return (Boolean) this.getEntityData().get(EntityAreaEffectCloud.DATA_WAITING);
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
        boolean flag = this.isWaiting();
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
                i = MathHelper.ceil(3.1415927F * f * f);
                f1 = f;
            }

            for (int j = 0; j < i; ++j) {
                float f2 = this.random.nextFloat() * 6.2831855F;
                float f3 = MathHelper.sqrt(this.random.nextFloat()) * f1;
                double d0 = this.getX() + (double) (MathHelper.cos(f2) * f3);
                double d1 = this.getY();
                double d2 = this.getZ() + (double) (MathHelper.sin(f2) * f3);
                double d3;
                double d4;
                double d5;

                if (particleparam.getType() == Particles.ENTITY_EFFECT) {
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

                this.level.addAlwaysVisibleParticle(particleparam, d0, d1, d2, d3, d4, d5);
            }
        } else {
            if (this.tickCount >= this.waitTime + this.duration) {
                this.discard();
                return;
            }

            boolean flag1 = this.tickCount < this.waitTime;

            if (flag != flag1) {
                this.setWaiting(flag1);
            }

            if (flag1) {
                return;
            }

            if (this.radiusPerTick != 0.0F) {
                f += this.radiusPerTick;
                if (f < 0.5F) {
                    this.discard();
                    return;
                }

                this.setRadius(f);
            }

            if (this.tickCount % 5 == 0) {
                this.victims.entrySet().removeIf((entry) -> {
                    return this.tickCount >= (Integer) entry.getValue();
                });
                List<MobEffect> list = Lists.newArrayList();
                Iterator iterator = this.potion.getEffects().iterator();

                while (iterator.hasNext()) {
                    MobEffect mobeffect = (MobEffect) iterator.next();

                    list.add(new MobEffect(mobeffect.getEffect(), mobeffect.getDuration() / 4, mobeffect.getAmplifier(), mobeffect.isAmbient(), mobeffect.isVisible()));
                }

                list.addAll(this.effects);
                if (list.isEmpty()) {
                    this.victims.clear();
                } else {
                    List<EntityLiving> list1 = this.level.getEntitiesOfClass(EntityLiving.class, this.getBoundingBox());

                    if (!list1.isEmpty()) {
                        Iterator iterator1 = list1.iterator();

                        while (iterator1.hasNext()) {
                            EntityLiving entityliving = (EntityLiving) iterator1.next();

                            if (!this.victims.containsKey(entityliving) && entityliving.isAffectedByPotions()) {
                                double d6 = entityliving.getX() - this.getX();
                                double d7 = entityliving.getZ() - this.getZ();
                                double d8 = d6 * d6 + d7 * d7;

                                if (d8 <= (double) (f * f)) {
                                    this.victims.put(entityliving, this.tickCount + this.reapplicationDelay);
                                    Iterator iterator2 = list.iterator();

                                    while (iterator2.hasNext()) {
                                        MobEffect mobeffect1 = (MobEffect) iterator2.next();

                                        if (mobeffect1.getEffect().isInstantenous()) {
                                            mobeffect1.getEffect().applyInstantenousEffect(this, this.getOwner(), entityliving, mobeffect1.getAmplifier(), 0.5D);
                                        } else {
                                            entityliving.addEffect(new MobEffect(mobeffect1), this);
                                        }
                                    }

                                    if (this.radiusOnUse != 0.0F) {
                                        f += this.radiusOnUse;
                                        if (f < 0.5F) {
                                            this.discard();
                                            return;
                                        }

                                        this.setRadius(f);
                                    }

                                    if (this.durationOnUse != 0) {
                                        this.duration += this.durationOnUse;
                                        if (this.duration <= 0) {
                                            this.discard();
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

    public float getRadiusOnUse() {
        return this.radiusOnUse;
    }

    public void setRadiusOnUse(float f) {
        this.radiusOnUse = f;
    }

    public float getRadiusPerTick() {
        return this.radiusPerTick;
    }

    public void setRadiusPerTick(float f) {
        this.radiusPerTick = f;
    }

    public int getDurationOnUse() {
        return this.durationOnUse;
    }

    public void setDurationOnUse(int i) {
        this.durationOnUse = i;
    }

    public int getWaitTime() {
        return this.waitTime;
    }

    public void setWaitTime(int i) {
        this.waitTime = i;
    }

    public void setOwner(@Nullable EntityLiving entityliving) {
        this.owner = entityliving;
        this.ownerUUID = entityliving == null ? null : entityliving.getUUID();
    }

    @Nullable
    public EntityLiving getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level instanceof WorldServer) {
            Entity entity = ((WorldServer) this.level).getEntity(this.ownerUUID);

            if (entity instanceof EntityLiving) {
                this.owner = (EntityLiving) entity;
            }
        }

        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        this.tickCount = nbttagcompound.getInt("Age");
        this.duration = nbttagcompound.getInt("Duration");
        this.waitTime = nbttagcompound.getInt("WaitTime");
        this.reapplicationDelay = nbttagcompound.getInt("ReapplicationDelay");
        this.durationOnUse = nbttagcompound.getInt("DurationOnUse");
        this.radiusOnUse = nbttagcompound.getFloat("RadiusOnUse");
        this.radiusPerTick = nbttagcompound.getFloat("RadiusPerTick");
        this.setRadius(nbttagcompound.getFloat("Radius"));
        if (nbttagcompound.hasUUID("Owner")) {
            this.ownerUUID = nbttagcompound.getUUID("Owner");
        }

        if (nbttagcompound.contains("Particle", 8)) {
            try {
                this.setParticle(ArgumentParticle.readParticle(new StringReader(nbttagcompound.getString("Particle"))));
            } catch (CommandSyntaxException commandsyntaxexception) {
                EntityAreaEffectCloud.LOGGER.warn("Couldn't load custom particle {}", nbttagcompound.getString("Particle"), commandsyntaxexception);
            }
        }

        if (nbttagcompound.contains("Color", 99)) {
            this.setFixedColor(nbttagcompound.getInt("Color"));
        }

        if (nbttagcompound.contains("Potion", 8)) {
            this.setPotion(PotionUtil.getPotion(nbttagcompound));
        }

        if (nbttagcompound.contains("Effects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Effects", 10);

            this.effects.clear();

            for (int i = 0; i < nbttaglist.size(); ++i) {
                MobEffect mobeffect = MobEffect.load(nbttaglist.getCompound(i));

                if (mobeffect != null) {
                    this.addEffect(mobeffect);
                }
            }
        }

    }

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.putInt("Age", this.tickCount);
        nbttagcompound.putInt("Duration", this.duration);
        nbttagcompound.putInt("WaitTime", this.waitTime);
        nbttagcompound.putInt("ReapplicationDelay", this.reapplicationDelay);
        nbttagcompound.putInt("DurationOnUse", this.durationOnUse);
        nbttagcompound.putFloat("RadiusOnUse", this.radiusOnUse);
        nbttagcompound.putFloat("RadiusPerTick", this.radiusPerTick);
        nbttagcompound.putFloat("Radius", this.getRadius());
        nbttagcompound.putString("Particle", this.getParticle().writeToString());
        if (this.ownerUUID != null) {
            nbttagcompound.putUUID("Owner", this.ownerUUID);
        }

        if (this.fixedColor) {
            nbttagcompound.putInt("Color", this.getColor());
        }

        if (this.potion != Potions.EMPTY) {
            nbttagcompound.putString("Potion", IRegistry.POTION.getKey(this.potion).toString());
        }

        if (!this.effects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.effects.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add(mobeffect.save(new NBTTagCompound()));
            }

            nbttagcompound.put("Effects", nbttaglist);
        }

    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (EntityAreaEffectCloud.DATA_RADIUS.equals(datawatcherobject)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    public PotionRegistry getPotion() {
        return this.potion;
    }

    @Override
    public EnumPistonReaction getPistonPushReaction() {
        return EnumPistonReaction.IGNORE;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new PacketPlayOutSpawnEntity(this);
    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        return EntitySize.scalable(this.getRadius() * 2.0F, 0.5F);
    }
}
