package net.minecraft.server;

public class ItemBookAndQuill extends Item {

    public ItemBookAndQuill() {
        this.d(1);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        entityhuman.a(itemstack, enumhand);
        entityhuman.b(StatisticList.b((Item) this));
        return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
    }

    public static boolean b(NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            return false;
        } else if (!nbttagcompound.hasKeyOfType("pages", 9)) {
            return false;
        } else {
            NBTTagList nbttaglist = nbttagcompound.getList("pages", 8);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                String s = nbttaglist.getString(i);

                if (s.length() > 32767) {
                    return false;
                }
            }

            return true;
        }
    }
}
