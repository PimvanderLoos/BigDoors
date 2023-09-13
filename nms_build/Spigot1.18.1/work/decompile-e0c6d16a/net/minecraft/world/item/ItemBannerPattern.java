package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;

public class ItemBannerPattern extends Item {

    private final EnumBannerPatternType bannerPattern;

    public ItemBannerPattern(EnumBannerPatternType enumbannerpatterntype, Item.Info item_info) {
        super(item_info);
        this.bannerPattern = enumbannerpatterntype;
    }

    public EnumBannerPatternType getBannerPattern() {
        return this.bannerPattern;
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        list.add(this.getDisplayName().withStyle(EnumChatFormat.GRAY));
    }

    public IChatMutableComponent getDisplayName() {
        return new ChatMessage(this.getDescriptionId() + ".desc");
    }
}
