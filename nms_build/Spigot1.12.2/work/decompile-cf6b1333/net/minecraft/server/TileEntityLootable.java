package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public abstract class TileEntityLootable extends TileEntityContainer implements ILootable {

    protected MinecraftKey m;
    protected long n;
    protected String o;

    public TileEntityLootable() {}

    protected boolean c(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("LootTable", 8)) {
            this.m = new MinecraftKey(nbttagcompound.getString("LootTable"));
            this.n = nbttagcompound.getLong("LootTableSeed");
            return true;
        } else {
            return false;
        }
    }

    protected boolean d(NBTTagCompound nbttagcompound) {
        if (this.m != null) {
            nbttagcompound.setString("LootTable", this.m.toString());
            if (this.n != 0L) {
                nbttagcompound.setLong("LootTableSeed", this.n);
            }

            return true;
        } else {
            return false;
        }
    }

    public void d(@Nullable EntityHuman entityhuman) {
        if (this.m != null) {
            LootTable loottable = this.world.getLootTableRegistry().a(this.m);

            this.m = null;
            Random random;

            if (this.n == 0L) {
                random = new Random();
            } else {
                random = new Random(this.n);
            }

            LootTableInfo.a loottableinfo_a = new LootTableInfo.a((WorldServer) this.world);

            if (entityhuman != null) {
                loottableinfo_a.a(entityhuman.du());
            }

            loottable.a(this, random, loottableinfo_a.a());
        }

    }

    public MinecraftKey b() {
        return this.m;
    }

    public void a(MinecraftKey minecraftkey, long i) {
        this.m = minecraftkey;
        this.n = i;
    }

    public boolean hasCustomName() {
        return this.o != null && !this.o.isEmpty();
    }

    public void setCustomName(String s) {
        this.o = s;
    }

    public ItemStack getItem(int i) {
        this.d((EntityHuman) null);
        return (ItemStack) this.q().get(i);
    }

    public ItemStack splitStack(int i, int j) {
        this.d((EntityHuman) null);
        ItemStack itemstack = ContainerUtil.a(this.q(), i, j);

        if (!itemstack.isEmpty()) {
            this.update();
        }

        return itemstack;
    }

    public ItemStack splitWithoutUpdate(int i) {
        this.d((EntityHuman) null);
        return ContainerUtil.a(this.q(), i);
    }

    public void setItem(int i, @Nullable ItemStack itemstack) {
        this.d((EntityHuman) null);
        this.q().set(i, itemstack);
        if (itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

        this.update();
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.d((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public int getProperty(int i) {
        return 0;
    }

    public void setProperty(int i, int j) {}

    public int h() {
        return 0;
    }

    public void clear() {
        this.d((EntityHuman) null);
        this.q().clear();
    }

    protected abstract NonNullList<ItemStack> q();
}
