package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockBannerAbstract;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import org.apache.commons.lang3.Validate;

public class ItemBanner extends ItemBlockWallable {

    private static final String PATTERN_PREFIX = "block.minecraft.banner.";

    public ItemBanner(Block block, Block block1, Item.Info item_info) {
        super(block, block1, item_info);
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
                EnumBannerPatternType enumbannerpatterntype = EnumBannerPatternType.byHash(nbttagcompound1.getString("Pattern"));

                if (enumbannerpatterntype != null) {
                    String s = enumbannerpatterntype.getFilename();

                    list.add((new ChatMessage("block.minecraft.banner." + s + "." + enumcolor.getName())).withStyle(EnumChatFormat.GRAY));
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
