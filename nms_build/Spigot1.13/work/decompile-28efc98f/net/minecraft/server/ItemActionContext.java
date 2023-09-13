package net.minecraft.server;

import javax.annotation.Nullable;

public class ItemActionContext {

    protected final EntityHuman b;
    protected final float c;
    protected final float d;
    protected final float e;
    protected final EnumDirection f;
    protected final World g;
    protected final ItemStack h;
    protected final BlockPosition i;

    public ItemActionContext(EntityHuman entityhuman, ItemStack itemstack, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        this(entityhuman.world, entityhuman, itemstack, blockposition, enumdirection, f, f1, f2);
    }

    protected ItemActionContext(World world, @Nullable EntityHuman entityhuman, ItemStack itemstack, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        this.b = entityhuman;
        this.f = enumdirection;
        this.c = f;
        this.d = f1;
        this.e = f2;
        this.i = blockposition;
        this.h = itemstack;
        this.g = world;
    }

    public BlockPosition getClickPosition() {
        return this.i;
    }

    public ItemStack getItemStack() {
        return this.h;
    }

    @Nullable
    public EntityHuman getEntity() {
        return this.b;
    }

    public World getWorld() {
        return this.g;
    }

    public EnumDirection getClickedFace() {
        return this.f;
    }

    public float m() {
        return this.c;
    }

    public float n() {
        return this.d;
    }

    public float o() {
        return this.e;
    }

    public EnumDirection f() {
        return this.b == null ? EnumDirection.NORTH : this.b.getDirection();
    }

    public boolean isSneaking() {
        return this.b != null && this.b.isSneaking();
    }

    public float h() {
        return this.b == null ? 0.0F : this.b.yaw;
    }
}
