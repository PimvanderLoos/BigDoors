package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.ISourceBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.item.context.BlockActionContextDirectional;
import net.minecraft.world.level.block.BlockDispenser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DispenseBehaviorShulkerBox extends DispenseBehaviorMaybe {

    private static final Logger LOGGER = LogManager.getLogger();

    public DispenseBehaviorShulkerBox() {}

    @Override
    protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
        this.a(false);
        Item item = itemstack.getItem();

        if (item instanceof ItemBlock) {
            EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING);
            BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
            EnumDirection enumdirection1 = isourceblock.getWorld().isEmpty(blockposition.down()) ? enumdirection : EnumDirection.UP;

            try {
                this.a(((ItemBlock) item).a((BlockActionContext) (new BlockActionContextDirectional(isourceblock.getWorld(), blockposition, enumdirection, itemstack, enumdirection1))).a());
            } catch (Exception exception) {
                DispenseBehaviorShulkerBox.LOGGER.error("Error trying to place shulker box at {}", blockposition, exception);
            }
        }

        return itemstack;
    }
}
