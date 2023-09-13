package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockActionContext extends ItemActionContext {

    private final BlockPosition j;
    protected boolean a;

    public BlockActionContext(ItemActionContext itemactioncontext) {
        this(itemactioncontext.getWorld(), itemactioncontext.getEntity(), itemactioncontext.getItemStack(), itemactioncontext.getClickPosition(), itemactioncontext.getClickedFace(), itemactioncontext.m(), itemactioncontext.n(), itemactioncontext.o());
    }

    protected BlockActionContext(World world, @Nullable EntityHuman entityhuman, ItemStack itemstack, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        super(world, entityhuman, itemstack, blockposition, enumdirection, f, f1, f2);
        this.a = true;
        this.j = this.i.shift(this.f);
        this.a = this.getWorld().getType(this.i).a(this);
    }

    public BlockPosition getClickPosition() {
        return this.a ? this.i : this.j;
    }

    public boolean b() {
        return this.a || this.getWorld().getType(this.getClickPosition()).a(this);
    }

    public boolean c() {
        return this.a;
    }

    public EnumDirection d() {
        return EnumDirection.a((Entity) this.b)[0];
    }

    public EnumDirection[] e() {
        EnumDirection[] aenumdirection = EnumDirection.a((Entity) this.b);

        if (this.a) {
            return aenumdirection;
        } else {
            int i;

            for (i = 0; i < aenumdirection.length && aenumdirection[i] != this.f.opposite(); ++i) {
                ;
            }

            if (i > 0) {
                System.arraycopy(aenumdirection, 0, aenumdirection, 1, i);
                aenumdirection[0] = this.f.opposite();
            }

            return aenumdirection;
        }
    }
}
