package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityDamageSource extends DamageSource {

    @Nullable
    protected Entity v;
    private boolean w;

    public EntityDamageSource(String s, @Nullable Entity entity) {
        super(s);
        this.v = entity;
    }

    public EntityDamageSource w() {
        this.w = true;
        return this;
    }

    public boolean x() {
        return this.w;
    }

    @Nullable
    public Entity getEntity() {
        return this.v;
    }

    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        ItemStack itemstack = this.v instanceof EntityLiving ? ((EntityLiving) this.v).getItemInMainHand() : ItemStack.a;
        String s = "death.attack." + this.translationIndex;
        String s1 = s + ".item";

        return !itemstack.isEmpty() && itemstack.hasName() && LocaleI18n.c(s1) ? new ChatMessage(s1, new Object[] { entityliving.getScoreboardDisplayName(), this.v.getScoreboardDisplayName(), itemstack.C()}) : new ChatMessage(s, new Object[] { entityliving.getScoreboardDisplayName(), this.v.getScoreboardDisplayName()});
    }

    public boolean r() {
        return this.v != null && this.v instanceof EntityLiving && !(this.v instanceof EntityHuman);
    }

    @Nullable
    public Vec3D v() {
        return new Vec3D(this.v.locX, this.v.locY, this.v.locZ);
    }
}
