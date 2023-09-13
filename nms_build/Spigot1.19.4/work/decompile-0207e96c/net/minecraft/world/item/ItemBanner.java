package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockBannerAbstract;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import org.apache.commons.lang3.Validate;

public class ItemBanner extends ItemBlockWallable {

    private static final String PATTERN_PREFIX = "block.minecraft.banner.";

    public ItemBanner(Block block, Block block1, Item.Info item_info) {
        super(block, block1, item_info, EnumDirection.DOWN);
        Validate.isInstanceOf(BlockBannerAbstract.class, block);
        Validate.isInstanceOf(BlockBannerAbstract.class, block1);
    }

    public static void appendHoverTextFromBannerBlockEntityTag(ItemStack itemstack, List<IChatBaseComponent> list) {
        NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

        if (nbttagcompound != null && nbttagcompound.contains("Patterns")) {
            NBTTagList nbttaglist = nbttagcompound.getList("Patterns", 10);

            for (int i = 0; i < nbttaglist.size() && i < 6; ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                EnumColor enumcolor = EnumColor.byId(nbttagcompound1.getInt("Color"));
                Holder<EnumBannerPatternType> holder = EnumBannerPatternType.byHash(nbttagcompound1.getString("Pattern"));

                if (holder != null) {
                    holder.unwrapKey().map((resourcekey) -> {
                        return resourcekey.location().toShortLanguageKey();
                    }).ifPresent((s) -> {
                        list.add(IChatBaseComponent.translatable("block.minecraft.banner." + s + "." + enumcolor.getName()).withStyle(EnumChatFormat.GRAY));
                    });
                }
            }

        }
    }

    public EnumColor getColor() {
        return ((BlockBannerAbstract) this.getBlock()).getColor();
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        appendHoverTextFromBannerBlockEntityTag(itemstack, list);
    }
}
