package net.minecraft.world.item;

import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemShears extends Item {

    public ItemShears(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public boolean a(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        if (!world.isClientSide && !iblockdata.getBlock().a((Tag) TagsBlock.FIRE)) {
            itemstack.damage(1, entityliving, (entityliving1) -> {
                entityliving1.broadcastItemBreak(EnumItemSlot.MAINHAND);
            });
        }

        return !iblockdata.a((Tag) TagsBlock.LEAVES) && !iblockdata.a(Blocks.COBWEB) && !iblockdata.a(Blocks.GRASS) && !iblockdata.a(Blocks.FERN) && !iblockdata.a(Blocks.DEAD_BUSH) && !iblockdata.a(Blocks.VINE) && !iblockdata.a(Blocks.TRIPWIRE) && !iblockdata.a((Tag) TagsBlock.WOOL) ? super.a(itemstack, world, iblockdata, blockposition, entityliving) : true;
    }

    @Override
    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        return iblockdata.a(Blocks.COBWEB) || iblockdata.a(Blocks.REDSTONE_WIRE) || iblockdata.a(Blocks.TRIPWIRE);
    }

    @Override
    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        return !iblockdata.a(Blocks.COBWEB) && !iblockdata.a((Tag) TagsBlock.LEAVES) ? (iblockdata.a((Tag) TagsBlock.WOOL) ? 5.0F : super.getDestroySpeed(itemstack, iblockdata)) : 15.0F;
    }
}
