package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityDamageSource extends DamageSource {

    @Nullable
    protected Entity w;
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
    public Entity getEntity() {
        return this.w;
    }

    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        ItemStack itemstack = this.w instanceof EntityLiving ? ((EntityLiving) this.w).getItemInMainHand() : ItemStack.a;
        String s = "death.attack." + this.translationIndex;

        return !itemstack.isEmpty() && itemstack.hasName() ? new ChatMessage(s + ".item", new Object[] { entityliving.getScoreboardDisplayName(), this.w.getScoreboardDisplayName(), itemstack.A()}) : new ChatMessage(s, new Object[] { entityliving.getScoreboardDisplayName(), this.w.getScoreboardDisplayName()});
    }

    public boolean s() {
        return this.w != null && this.w instanceof EntityLiving && !(this.w instanceof EntityHuman);
    }

    @Nullable
    public Vec3D w() {
        return new Vec3D(this.w.locX, this.w.locY, this.w.locZ);
    }
}
