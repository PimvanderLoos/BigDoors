package net.minecraft.world.item;

import net.minecraft.world.level.block.Block;

public class ItemNamedBlock extends ItemBlock {

    public ItemNamedBlock(Block block, Item.Info item_info) {
        super(block, item_info);
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }
}
