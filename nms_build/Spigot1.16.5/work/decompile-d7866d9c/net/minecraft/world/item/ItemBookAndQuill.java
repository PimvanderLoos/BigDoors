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
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        return iblockdata.a(Blocks.LECTERN) ? (BlockLectern.a(world, blockposition, iblockdata, itemactioncontext.getItemStack()) ? EnumInteractionResult.a(world.isClientSide) : EnumInteractionResult.PASS) : EnumInteractionResult.PASS;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        entityhuman.openBook(itemstack, enumhand);
        entityhuman.b(StatisticList.ITEM_USED.b(this));
        return InteractionResultWrapper.a(itemstack, world.s_());
    }

    public static boolean a(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            return false;
        } else if (!nbttagcompound.hasKeyOfType("pages", 9)) {
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
