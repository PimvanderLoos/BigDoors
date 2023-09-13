package net.minecraft.server;

public class ItemCoal extends Item {

    public ItemCoal() {
        this.a(true);
        this.setMaxDurability(0);
        this.b(CreativeModeTab.l);
    }

    public String a(ItemStack itemstack) {
        return itemstack.getData() == 1 ? "item.charcoal" : "item.coal";
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            nonnulllist.add(new ItemStack(this, 1, 0));
            nonnulllist.add(new ItemStack(this, 1, 1));
        }

    }
}
