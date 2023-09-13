package net.minecraft.world.effect;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.EntityHuman;

public class MobEffectList {

    private final Map<AttributeBase, AttributeModifier> attributeModifiers = Maps.newHashMap();
    private final MobEffectInfo category;
    private final int color;
    @Nullable
    private String descriptionId;

    @Nullable
    public static MobEffectList byId(int i) {
        return (MobEffectList) IRegistry.MOB_EFFECT.byId(i);
    }

    public static int getId(MobEffectList mobeffectlist) {
        return IRegistry.MOB_EFFECT.getId(mobeffectlist);
    }

    protected MobEffectList(MobEffectInfo mobeffectinfo, int i) {
        this.category = mobeffectinfo;
        this.color = i;
    }

    public void applyEffectTick(EntityLiving entityliving, int i) {
        if (this == MobEffects.REGENERATION) {
            if (entityliving.getHealth() < entityliving.getMaxHealth()) {
                entityliving.heal(1.0F);
            }
        } else if (this == MobEffects.POISON) {
            if (entityliving.getHealth() > 1.0F) {
                entityliving.hurt(DamageSource.MAGIC, 1.0F);
            }
        } else if (this == MobEffects.WITHER) {
            entityliving.hurt(DamageSource.WITHER, 1.0F);
        } else if (this == MobEffects.HUNGER && entityliving instanceof EntityHuman) {
            ((EntityHuman) entityliving).causeFoodExhaustion(0.005F * (float) (i + 1));
        } else if (this == MobEffects.SATURATION && entityliving instanceof EntityHuman) {
            if (!entityliving.level.isClientSide) {
                ((EntityHuman) entityliving).getFoodData().eat(i + 1, 1.0F);
            }
        } else if ((this != MobEffects.HEAL || entityliving.isInvertedHealAndHarm()) && (this != MobEffects.HARM || !entityliving.isInvertedHealAndHarm())) {
            if (this == MobEffects.HARM && !entityliving.isInvertedHealAndHarm() || this == MobEffects.HEAL && entityliving.isInvertedHealAndHarm()) {
                entityliving.hurt(DamageSource.MAGIC, (float) (6 << i));
            }
        } else {
            entityliving.heal((float) Math.max(4 << i, 0));
        }

    }

    public void applyInstantenousEffect(@Nullable Entity entity, @Nullable Entity entity1, EntityLiving entityliving, int i, double d0) {
        int j;

        if ((this != MobEffects.HEAL || entityliving.isInvertedHealAndHarm()) && (this != MobEffects.HARM || !entityliving.isInvertedHealAndHarm())) {
            if ((this != MobEffects.HARM || entityliving.isInvertedHealAndHarm()) && (this != MobEffects.HEAL || !entityliving.isInvertedHealAndHarm())) {
                this.applyEffectTick(entityliving, i);
            } else {
                j = (int) (d0 * (double) (6 << i) + 0.5D);
                if (entity == null) {
                    entityliving.hurt(DamageSource.MAGIC, (float) j);
                } else {
                    entityliving.hurt(DamageSource.indirectMagic(entity, entity1), (float) j);
                }
            }
        } else {
            j = (int) (d0 * (double) (4 << i) + 0.5D);
            entityliving.heal((float) j);
        }

    }

    public boolean isDurationEffectTick(int i, int j) {
        int k;

        if (this == MobEffects.REGENERATION) {
            k = 50 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this == MobEffects.POISON) {
            k = 25 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this == MobEffects.WITHER) {
            k = 40 >> j;
            return k > 0 ? i % k == 0 : true;
        } else {
            return this == MobEffects.HUNGER;
        }
    }

    public boolean isInstantenous() {
        return false;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = SystemUtils.makeDescriptionId("effect", IRegistry.MOB_EFFECT.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public IChatBaseComponent getDisplayName() {
        return new ChatMessage(this.getDescriptionId());
    }

    public MobEffectInfo getCategory() {
        return this.category;
    }

    public int getColor() {
        return this.color;
    }

    public MobEffectList addAttributeModifier(AttributeBase attributebase, String s, double d0, AttributeModifier.Operation attributemodifier_operation) {
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(s), this::getDescriptionId, d0, attributemodifier_operation);

        this.attributeModifiers.put(attributebase, attributemodifier);
        return this;
    }

    public Map<AttributeBase, AttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }

    public void removeAttributeModifiers(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        Iterator iterator = this.attributeModifiers.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<AttributeBase, AttributeModifier> entry = (Entry) iterator.next();
            AttributeModifiable attributemodifiable = attributemapbase.getInstance((AttributeBase) entry.getKey());

            if (attributemodifiable != null) {
                attributemodifiable.removeModifier((AttributeModifier) entry.getValue());
            }
        }

    }

    public void addAttributeModifiers(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        Iterator iterator = this.attributeModifiers.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<AttributeBase, AttributeModifier> entry = (Entry) iterator.next();
            AttributeModifiable attributemodifiable = attributemapbase.getInstance((AttributeBase) entry.getKey());

            if (attributemodifiable != null) {
                AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();

                attributemodifiable.removeModifier(attributemodifier);
                attributemodifiable.addPermanentModifier(new AttributeModifier(attributemodifier.getId(), this.getDescriptionId() + " " + i, this.getAttributeModifierValue(i, attributemodifier), attributemodifier.getOperation()));
            }
        }

    }

    public double getAttributeModifierValue(int i, AttributeModifier attributemodifier) {
        return attributemodifier.getAmount() * (double) (i + 1);
    }

    public boolean isBeneficial() {
        return this.category == MobEffectInfo.BENEFICIAL;
    }
}
