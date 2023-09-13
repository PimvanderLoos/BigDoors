package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockLectern;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemBookAndQuill extends Item {

    public ItemBookAndQuill(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        IBlockData iblockdata = world.getBlockState(blockposition);

        return iblockdata.is(Blocks.LECTERN) ? (BlockLectern.tryPlaceBook(itemactioncontext.getPlayer(), world, blockposition, iblockdata, itemactioncontext.getItemInHand()) ? EnumInteractionResult.sidedSuccess(world.isClientSide) : EnumInteractionResult.PASS) : EnumInteractionResult.PASS;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        entityhuman.openItemGui(itemstack, enumhand);
        entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
        return InteractionResultWrapper.sidedSuccess(itemstack, world.isClientSide());
    }

    public static boolean makeSureTagIsValid(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            return false;
        } else if (!nbttagcompound.contains("pages", 9)) {
            return false;
        } else {
            NBTTagList nbttaglist = nbttagcompound.getList("pages", 8);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                String s = nbttaglist.getString(i);

                if (s.length() > 32767) {
                    return false;
                }
            }

            return true;
        }
    }
}
