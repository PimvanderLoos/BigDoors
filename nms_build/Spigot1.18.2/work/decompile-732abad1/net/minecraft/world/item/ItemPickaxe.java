package net.minecraft.world.item;

import net.minecraft.tags.TagsBlock;

public class ItemPickaxe extends ItemTool {

    protected ItemPickaxe(ToolMaterial toolmaterial, int i, float f, Item.Info item_info) {
        super((float) i, f, toolmaterial, TagsBlock.MINEABLE_WITH_PICKAXE, item_info);
    }
}
