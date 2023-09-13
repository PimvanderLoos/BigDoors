package net.minecraft.world.item;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityHanging;
import net.minecraft.world.entity.player.EntityHuman;

public class ItemItemFrame extends ItemHanging {

    public ItemItemFrame(EntityTypes<? extends EntityHanging> entitytypes, Item.Info item_info) {
        super(entitytypes, item_info);
    }

    @Override
    protected boolean a(EntityHuman entityhuman, EnumDirection enumdirection, ItemStack itemstack, BlockPosition blockposition) {
        return !entityhuman.level.isOutsideWorld(blockposition) && entityhuman.a(blockposition, enumdirection, itemstack);
    }
}
