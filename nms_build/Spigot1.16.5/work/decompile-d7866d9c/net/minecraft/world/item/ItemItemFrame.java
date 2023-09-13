package net.minecraft.world.item;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class ItemItemFrame extends ItemHanging {

    public ItemItemFrame(Item.Info item_info) {
        super(EntityTypes.ITEM_FRAME, item_info);
    }

    @Override
    protected boolean a(EntityHuman entityhuman, EnumDirection enumdirection, ItemStack itemstack, BlockPosition blockposition) {
        return !World.isOutsideWorld(blockposition) && entityhuman.a(blockposition, enumdirection, itemstack);
    }
}
