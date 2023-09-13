package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource {

    @Nullable
    private final Entity cause;

    public EntityDamageSourceIndirect(String s, Entity entity, @Nullable Entity entity1) {
        super(s, entity);
        this.cause = entity1;
    }

    @Nullable
    @Override
    public Entity getDirectEntity() {
        return this.entity;
    }

    @Nullable
    @Override
    public Entity getEntity() {
        return this.cause;
    }

    @Override
    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        IChatBaseComponent ichatbasecomponent = this.cause == null ? this.entity.getDisplayName() : this.cause.getDisplayName();
        Entity entity = this.cause;
        ItemStack itemstack;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving1 = (EntityLiving) entity;

            itemstack = entityliving1.getMainHandItem();
        } else {
            itemstack = ItemStack.EMPTY;
        }

        ItemStack itemstack1 = itemstack;
        String s = "death.attack." + this.msgId;

        if (!itemstack1.isEmpty() && itemstack1.hasCustomHoverName()) {
            String s1 = s + ".item";

            return IChatBaseComponent.translatable(s1, entityliving.getDisplayName(), ichatbasecomponent, itemstack1.getDisplayName());
        } else {
            return IChatBaseComponent.translatable(s, entityliving.getDisplayName(), ichatbasecomponent);
        }
    }
}
