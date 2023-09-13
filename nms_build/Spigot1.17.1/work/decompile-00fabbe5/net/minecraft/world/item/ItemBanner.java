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

    public static void a(ItemStack itemstack, List<IChatBaseComponent> list) {
        NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");

        if (nbttagcompound != null && nbttagcompound.hasKey("Patterns")) {
            NBTTagList nbttaglist = nbttagcompound.getList("Patterns", 10);

            for (int i = 0; i < nbttaglist.size() && i < 6; ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                EnumColor enumcolor = EnumColor.fromColorIndex(nbttagcompound1.getInt("Color"));
                EnumBannerPatternType enumbannerpatterntype = EnumBannerPatternType.a(nbttagcompound1.getString("Pattern"));

                if (enumbannerpatterntype != null) {
                    String s = enumbannerpatterntype.a();

                    list.add((new ChatMessage("block.minecraft.banner." + s + "." + enumcolor.b())).a(EnumChatFormat.GRAY));
                }
            }

        }
    }

    public EnumColor b() {
        return ((BlockBannerAbstract) this.getBlock()).getColor();
    }

    @Override
    public void a(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        a(itemstack, list);
    }
}
