package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3D;

public class DamageSource {

    private final Holder<DamageType> type;
    @Nullable
    private final Entity causingEntity;
    @Nullable
    private final Entity directEntity;
    @Nullable
    private final Vec3D damageSourcePosition;

    public String toString() {
        return "DamageSource (" + this.type().msgId() + ")";
    }

    public float getFoodExhaustion() {
        return this.type().exhaustion();
    }

    public boolean isIndirect() {
        return this.causingEntity != this.directEntity;
    }

    private DamageSource(Holder<DamageType> holder, @Nullable Entity entity, @Nullable Entity entity1, @Nullable Vec3D vec3d) {
        this.type = holder;
        this.causingEntity = entity1;
        this.directEntity = entity;
        this.damageSourcePosition = vec3d;
    }

    public DamageSource(Holder<DamageType> holder, @Nullable Entity entity, @Nullable Entity entity1) {
        this(holder, entity, entity1, (Vec3D) null);
    }

    public DamageSource(Holder<DamageType> holder, Vec3D vec3d) {
        this(holder, (Entity) null, (Entity) null, vec3d);
    }

    public DamageSource(Holder<DamageType> holder, @Nullable Entity entity) {
        this(holder, entity, entity);
    }

    public DamageSource(Holder<DamageType> holder) {
        this(holder, (Entity) null, (Entity) null, (Vec3D) null);
    }

    @Nullable
    public Entity getDirectEntity() {
        return this.directEntity;
    }

    @Nullable
    public Entity getEntity() {
        return this.causingEntity;
    }

    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        String s = "death.attack." + this.type().msgId();

        if (this.causingEntity == null && this.directEntity == null) {
            EntityLiving entityliving1 = entityliving.getKillCredit();
            String s1 = s + ".player";

            return entityliving1 != null ? IChatBaseComponent.translatable(s1, entityliving.getDisplayName(), entityliving1.getDisplayName()) : IChatBaseComponent.translatable(s, entityliving.getDisplayName());
        } else {
            IChatBaseComponent ichatbasecomponent = this.causingEntity == null ? this.directEntity.getDisplayName() : this.causingEntity.getDisplayName();
            Entity entity = this.causingEntity;
            ItemStack itemstack;

            if (entity instanceof EntityLiving) {
                EntityLiving entityliving2 = (EntityLiving) entity;

                itemstack = entityliving2.getMainHandItem();
            } else {
                itemstack = ItemStack.EMPTY;
            }

            ItemStack itemstack1 = itemstack;

            return !itemstack1.isEmpty() && itemstack1.hasCustomHoverName() ? IChatBaseComponent.translatable(s + ".item", entityliving.getDisplayName(), ichatbasecomponent, itemstack1.getDisplayName()) : IChatBaseComponent.translatable(s, entityliving.getDisplayName(), ichatbasecomponent);
        }
    }

    public String getMsgId() {
        return this.type().msgId();
    }

    public boolean scalesWithDifficulty() {
        boolean flag;

        switch (this.type().scaling()) {
            case NEVER:
                flag = false;
                break;
            case WHEN_CAUSED_BY_LIVING_NON_PLAYER:
                flag = this.causingEntity instanceof EntityLiving && !(this.causingEntity instanceof EntityHuman);
                break;
            case ALWAYS:
                flag = true;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return flag;
    }

    public boolean isCreativePlayer() {
        Entity entity = this.getEntity();
        boolean flag;

        if (entity instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman) entity;

            if (entityhuman.getAbilities().instabuild) {
                flag = true;
                return flag;
            }
        }

        flag = false;
        return flag;
    }

    @Nullable
    public Vec3D getSourcePosition() {
        return this.damageSourcePosition != null ? this.damageSourcePosition : (this.causingEntity != null ? this.causingEntity.position() : null);
    }

    @Nullable
    public Vec3D sourcePositionRaw() {
        return this.damageSourcePosition;
    }

    public boolean is(TagKey<DamageType> tagkey) {
        return this.type.is(tagkey);
    }

    public boolean is(ResourceKey<DamageType> resourcekey) {
        return this.type.is(resourcekey);
    }

    public DamageType type() {
        return (DamageType) this.type.value();
    }

    public Holder<DamageType> typeHolder() {
        return this.type;
    }
}
