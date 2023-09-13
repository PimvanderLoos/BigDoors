package net.minecraft.world.item;

import net.minecraft.world.level.block.Block;

public class ItemAir extends Item {

    private final Block a;

    public ItemAir(Block block, Item.Info item_info) {
        super(item_info);
        this.a = block;
    }

    @Override
    public String getName() {
        return this.a.i();
    }
}
