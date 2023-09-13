package net.minecraft.server;

public class ItemWrittenBook extends Item {

    public ItemWrittenBook() {
        this.d(1);
    }

    public static boolean b(NBTTagCompound nbttagcompound) {
        if (!ItemBookAndQuill.b(nbttagcompound)) {
            return false;
        } else if (!nbttagcompound.hasKeyOfType("title", 8)) {
            return false;
        } else {
            String s = nbttagcompound.getString("title");

            return s != null && s.length() <= 32 ? nbttagcompound.hasKeyOfType("author", 8) : false;
        }
    }

    public static int h(ItemStack itemstack) {
        return itemstack.getTag().getInt("generation");
    }

    public String b(ItemStack itemstack) {
        if (itemstack.hasTag()) {
            NBTTagCompound nbttagcompound = itemstack.getTag();
            String s = nbttagcompound.getString("title");

            if (!UtilColor.b(s)) {
                return s;
            }
        }

        return super.b(itemstack);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!world.isClientSide) {
            this.a(itemstack, entityhuman);
        }

        entityhuman.a(itemstack, enumhand);
        entityhuman.b(StatisticList.b((Item) this));
        return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
    }

    private void a(ItemStack itemstack, EntityHuman entityhuman) {
        if (itemstack.getTag() != null) {
            NBTTagCompound nbttagcompound = itemstack.getTag();

            if (!nbttagcompound.getBoolean("resolved")) {
                nbttagcompound.setBoolean("resolved", true);
                if (b(nbttagcompound)) {
                    NBTTagList nbttaglist = nbttagcompound.getList("pages", 8);

                    for (int i = 0; i < nbttaglist.size(); ++i) {
                        String s = nbttaglist.getString(i);

                        Object object;

                        try {
                            IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.b(s);

                            object = ChatComponentUtils.filterForDisplay(entityhuman, ichatbasecomponent, entityhuman);
                        } catch (Exception exception) {
                            object = new ChatComponentText(s);
                        }

                        nbttaglist.a(i, new NBTTagString(IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) object)));
                    }

                    nbttagcompound.set("pages", nbttaglist);
                    if (entityhuman instanceof EntityPlayer && entityhuman.getItemInMainHand() == itemstack) {
                        Slot slot = entityhuman.activeContainer.getSlot(entityhuman.inventory, entityhuman.inventory.itemInHandIndex);

                        ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutSetSlot(0, slot.rawSlotIndex, itemstack));
                    }

                }
            }
        }
    }
}
