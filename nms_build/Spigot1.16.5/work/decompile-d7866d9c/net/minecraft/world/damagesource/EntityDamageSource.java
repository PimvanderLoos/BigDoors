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

    @Nullable
    protected final Entity w;
    private boolean x;

    public EntityDamageSource(String s, @Nullable Entity entity) {
        super(s);
        this.w = entity;
    }

    public EntityDamageSource x() {
        this.x = true;
        return this;
    }

    public boolean y() {
        return this.x;
    }

    @Nullable
    @Override
    public Entity getEntity() {
        return this.w;
    }

    @Override
    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        ItemStack itemstack = this.w instanceof EntityLiving ? ((EntityLiving) this.w).getItemInMainHand() : ItemStack.b;
        String s = "death.attack." + this.translationIndex;

        return !itemstack.isEmpty() && itemstack.hasName() ? new ChatMessage(s + ".item", new Object[]{entityliving.getScoreboardDisplayName(), this.w.getScoreboardDisplayName(), itemstack.C()}) : new ChatMessage(s, new Object[]{entityliving.getScoreboardDisplayName(), this.w.getScoreboardDisplayName()});
    }

    @Override
    public boolean s() {
        return this.w != null && this.w instanceof EntityLiving && !(this.w instanceof EntityHuman);
    }

    @Nullable
    @Override
    public Vec3D w() {
        return this.w != null ? this.w.getPositionVector() : null;
    }

    @Override
    public String toString() {
        return "EntityDamageSource (" + this.w + ")";
    }
}
