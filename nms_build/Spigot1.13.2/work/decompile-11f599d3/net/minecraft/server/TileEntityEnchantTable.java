package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class TileEntityEnchantTable extends TileEntity implements ITileEntityContainer, ITickable {

    public int a;
    public float e;
    public float f;
    public float g;
    public float h;
    public float i;
    public float j;
    public float k;
    public float l;
    public float m;
    private static final Random n = new Random();
    private IChatBaseComponent o;

    public TileEntityEnchantTable() {
        super(TileEntityTypes.ENCHANTING_TABLE);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (this.hasCustomName()) {
            nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(this.o));
        }

        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.o = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName"));
        }

    }

    public void tick() {
        this.j = this.i;
        this.l = this.k;
        EntityHuman entityhuman = this.world.a((double) ((float) this.position.getX() + 0.5F), (double) ((float) this.position.getY() + 0.5F), (double) ((float) this.position.getZ() + 0.5F), 3.0D, false);

        if (entityhuman != null) {
            double d0 = entityhuman.locX - (double) ((float) this.position.getX() + 0.5F);
            double d1 = entityhuman.locZ - (double) ((float) this.position.getZ() + 0.5F);

            this.m = (float) MathHelper.c(d1, d0);
            this.i += 0.1F;
            if (this.i < 0.5F || TileEntityEnchantTable.n.nextInt(40) == 0) {
                float f = this.g;

                do {
                    this.g += (float) (TileEntityEnchantTable.n.nextInt(4) - TileEntityEnchantTable.n.nextInt(4));
                } while (f == this.g);
            }
        } else {
            this.m += 0.02F;
            this.i -= 0.1F;
        }

        while (this.k >= 3.1415927F) {
            this.k -= 6.2831855F;
        }

        while (this.k < -3.1415927F) {
            this.k += 6.2831855F;
        }

        while (this.m >= 3.1415927F) {
            this.m -= 6.2831855F;
        }

        while (this.m < -3.1415927F) {
            this.m += 6.2831855F;
        }

        float f1;

        for (f1 = this.m - this.k; f1 >= 3.1415927F; f1 -= 6.2831855F) {
            ;
        }

        while (f1 < -3.1415927F) {
            f1 += 6.2831855F;
        }

        this.k += f1 * 0.4F;
        this.i = MathHelper.a(this.i, 0.0F, 1.0F);
        ++this.a;
        this.f = this.e;
        float f2 = (this.g - this.e) * 0.4F;
        float f3 = 0.2F;

        f2 = MathHelper.a(f2, -0.2F, 0.2F);
        this.h += (f2 - this.h) * 0.9F;
        this.e += this.h;
    }

    public IChatBaseComponent getDisplayName() {
        return (IChatBaseComponent) (this.o != null ? this.o : new ChatMessage("container.enchant", new Object[0]));
    }

    public boolean hasCustomName() {
        return this.o != null;
    }

    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.o = ichatbasecomponent;
    }

    @Nullable
    public IChatBaseComponent getCustomName() {
        return this.o;
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerEnchantTable(playerinventory, this.world, this.position);
    }

    public String getContainerName() {
        return "minecraft:enchanting_table";
    }
}
