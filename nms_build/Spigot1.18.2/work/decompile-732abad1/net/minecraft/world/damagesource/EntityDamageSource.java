package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3D;

public class EntityDamageSource extends DamageSource {

    protected final Entity entity;
    private boolean isThorns;

    public EntityDamageSource(String s, Entity entity) {
        super(s);
        this.entity = entity;
    }

    public EntityDamageSource setThorns() {
        this.isThorns = true;
        return this;
    }

    public boolean isThorns() {
        return this.isThorns;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        ItemStack itemstack = this.entity instanceof EntityLiving ? ((EntityLiving) this.entity).getMainHandItem() : ItemStack.EMPTY;
        String s = "death.attack." + this.msgId;

        return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? new ChatMessage(s + ".item", new Object[]{entityliving.getDisplayName(), this.entity.getDisplayName(), itemstack.getDisplayName()}) : new ChatMessage(s, new Object[]{entityliving.getDisplayName(), this.entity.getDisplayName()});
    }

    @Override
    public boolean scalesWithDifficulty() {
        return this.entity instanceof EntityLiving && !(this.entity instanceof EntityHuman);
    }

    @Nullable
    @Override
    public Vec3D getSourcePosition() {
        return this.entity.position();
    }

    @Override
    public String toString() {
        return "EntityDamageSource (" + this.entity + ")";
    }
}
