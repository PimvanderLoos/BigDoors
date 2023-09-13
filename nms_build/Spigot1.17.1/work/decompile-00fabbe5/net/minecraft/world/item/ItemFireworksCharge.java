package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.world.level.World;

public class ItemFireworksCharge extends Item {

    public ItemFireworksCharge(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public void a(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        NBTTagCompound nbttagcompound = itemstack.b("Explosion");

        if (nbttagcompound != null) {
            a(nbttagcompound, list);
        }

    }

    public static void a(NBTTagCompound nbttagcompound, List<IChatBaseComponent> list) {
        ItemFireworks.EffectType itemfireworks_effecttype = ItemFireworks.EffectType.a(nbttagcompound.getByte("Type"));

        list.add((new ChatMessage("item.minecraft.firework_star.shape." + itemfireworks_effecttype.b())).a(EnumChatFormat.GRAY));
        int[] aint = nbttagcompound.getIntArray("Colors");

        if (aint.length > 0) {
            list.add(a((new ChatComponentText("")).a(EnumChatFormat.GRAY), aint));
        }

        int[] aint1 = nbttagcompound.getIntArray("FadeColors");

        if (aint1.length > 0) {
            list.add(a((new ChatMessage("item.minecraft.firework_star.fade_to")).c(" ").a(EnumChatFormat.GRAY), aint1));
        }

        if (nbttagcompound.getBoolean("Trail")) {
            list.add((new ChatMessage("item.minecraft.firework_star.trail")).a(EnumChatFormat.GRAY));
        }

        if (nbttagcompound.getBoolean("Flicker")) {
            list.add((new ChatMessage("item.minecraft.firework_star.flicker")).a(EnumChatFormat.GRAY));
        }

    }

    private static IChatBaseComponent a(IChatMutableComponent ichatmutablecomponent, int[] aint) {
        for (int i = 0; i < aint.length; ++i) {
            if (i > 0) {
                ichatmutablecomponent.c(", ");
            }

            ichatmutablecomponent.addSibling(a(aint[i]));
        }

        return ichatmutablecomponent;
    }

    private static IChatBaseComponent a(int i) {
        EnumColor enumcolor = EnumColor.b(i);

        return enumcolor == null ? new ChatMessage("item.minecraft.firework_star.custom_color") : new ChatMessage("item.minecraft.firework_star." + enumcolor.b());
    }
}
