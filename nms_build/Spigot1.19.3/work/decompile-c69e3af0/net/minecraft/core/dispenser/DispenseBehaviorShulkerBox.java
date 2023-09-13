package net.minecraft.core.dispenser;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.ISourceBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContextDirectional;
import net.minecraft.world.level.block.BlockDispenser;
import org.slf4j.Logger;

public class DispenseBehaviorShulkerBox extends DispenseBehaviorMaybe {

    private static final Logger LOGGER = LogUtils.getLogger();

    public DispenseBehaviorShulkerBox() {}

    @Override
    protected ItemStack execute(ISourceBlock isourceblock, ItemStack itemstack) {
        this.setSuccess(false);
        Item item = itemstack.getItem();

        if (item instanceof ItemBlock) {
            EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockState().getValue(BlockDispenser.FACING);
            BlockPosition blockposition = isourceblock.getPos().relative(enumdirection);
            EnumDirection enumdirection1 = isourceblock.getLevel().isEmptyBlock(blockposition.below()) ? enumdirection : EnumDirection.UP;

            try {
                this.setSuccess(((ItemBlock) item).place(new BlockActionContextDirectional(isourceblock.getLevel(), blockposition, enumdirection, itemstack, enumdirection1)).consumesAction());
            } catch (Exception exception) {
                DispenseBehaviorShulkerBox.LOGGER.error("Error trying to place shulker box at {}", blockposition, exception);
            }
        }

        return itemstack;
    }
}
