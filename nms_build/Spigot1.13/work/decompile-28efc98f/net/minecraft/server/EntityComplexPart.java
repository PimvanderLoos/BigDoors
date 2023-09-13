package net.minecraft.server;

public class EntityComplexPart extends Entity {

    public final IComplex owner;
    public final String b;

    public EntityComplexPart(IComplex icomplex, String s, float f, float f1) {
        super(icomplex.P(), icomplex.J_());
        this.setSize(f, f1);
        this.owner = icomplex;
        this.b = s;
    }

    protected void x_() {}

    protected void a(NBTTagCompound nbttagcompound) {}

    protected void b(NBTTagCompound nbttagcompound) {}

    public boolean isInteractable() {
        return true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        return this.isInvulnerable(damagesource) ? false : this.owner.a(this, damagesource, f);
    }

    public boolean s(Entity entity) {
        return this == entity || this.owner == entity;
    }
}
