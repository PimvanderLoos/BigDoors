package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.ItemStack;

@Immutable
public class ChestLock {

    public static final ChestLock NO_LOCK = new ChestLock("");
    public static final String TAG_LOCK = "Lock";
    public final String key;

    public ChestLock(String s) {
        this.key = s;
    }

    public boolean unlocksWith(ItemStack itemstack) {
        return this.key.isEmpty() || !itemstack.isEmpty() && itemstack.hasCustomHoverName() && this.key.equals(itemstack.getHoverName().getString());
    }

    public void addToTag(NBTTagCompound nbttagcompound) {
        if (!this.key.isEmpty()) {
            nbttagcompound.putString("Lock", this.key);
        }

    }

    public static ChestLock fromTag(NBTTagCompound nbttagcompound) {
        return nbttagcompound.contains("Lock", 8) ? new ChestLock(nbttagcompound.getString("Lock")) : ChestLock.NO_LOCK;
    }
}
