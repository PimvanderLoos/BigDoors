package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.world.level.World;

public class ItemFireworksCharge extends Item {

    public ItemFireworksCharge(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        NBTTagCompound nbttagcompound = itemstack.getTagElement("Explosion");

        if (nbttagcompound != null) {
            appendHoverText(nbttagcompound, list);
        }

    }

    public static void appendHoverText(NBTTagCompound nbttagcompound, List<IChatBaseComponent> list) {
        ItemFireworks.EffectType itemfireworks_effecttype = ItemFireworks.EffectType.byId(nbttagcompound.getByte("Type"));

        list.add(IChatBaseComponent.translatable("item.minecraft.firework_star.shape." + itemfireworks_effecttype.getName()).withStyle(EnumChatFormat.GRAY));
        int[] aint = nbttagcompound.getIntArray("Colors");

        if (aint.length > 0) {
            list.add(appendColors(IChatBaseComponent.empty().withStyle(EnumChatFormat.GRAY), aint));
        }

        int[] aint1 = nbttagcompound.getIntArray("FadeColors");

        if (aint1.length > 0) {
            list.add(appendColors(IChatBaseComponent.translatable("item.minecraft.firework_star.fade_to").append(CommonComponents.SPACE).withStyle(EnumChatFormat.GRAY), aint1));
        }

        if (nbttagcompound.getBoolean("Trail")) {
            list.add(IChatBaseComponent.translatable("item.minecraft.firework_star.trail").withStyle(EnumChatFormat.GRAY));
        }

        if (nbttagcompound.getBoolean("Flicker")) {
            list.add(IChatBaseComponent.translatable("item.minecraft.firework_star.flicker").withStyle(EnumChatFormat.GRAY));
        }

    }

    private static IChatBaseComponent appendColors(IChatMutableComponent ichatmutablecomponent, int[] aint) {
        for (int i = 0; i < aint.length; ++i) {
            if (i > 0) {
                ichatmutablecomponent.append(", ");
            }

            ichatmutablecomponent.append(getColorName(aint[i]));
        }

        return ichatmutablecomponent;
    }

    private static IChatBaseComponent getColorName(int i) {
        EnumColor enumcolor = EnumColor.byFireworkColor(i);

        return enumcolor == null ? IChatBaseComponent.translatable("item.minecraft.firework_star.custom_color") : IChatBaseComponent.translatable("item.minecraft.firework_star." + enumcolor.getName());
    }
}
