package net.minecraft.server;

public class ItemString extends ItemBlock {

    public ItemString(Item.Info item_info) {
        super(Blocks.TRIPWIRE, item_info);
    }

    public String getName() {
        return this.m();
    }
}
