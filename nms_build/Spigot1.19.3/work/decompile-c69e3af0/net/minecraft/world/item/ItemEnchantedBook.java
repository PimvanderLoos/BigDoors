package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.WeightedRandomEnchant;
import net.minecraft.world.level.World;

public class ItemEnchantedBook extends Item {

    public static final String TAG_STORED_ENCHANTMENTS = "StoredEnchantments";

    public ItemEnchantedBook(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack itemstack) {
        return false;
    }

    public static NBTTagList getEnchantments(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null ? nbttagcompound.getList("StoredEnchantments", 10) : new NBTTagList();
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        super.appendHoverText(itemstack, world, list, tooltipflag);
        ItemStack.appendEnchantmentNames(list, getEnchantments(itemstack));
    }

    public static void addEnchantment(ItemStack itemstack, WeightedRandomEnchant weightedrandomenchant) {
        NBTTagList nbttaglist = getEnchantments(itemstack);
        boolean flag = true;
        MinecraftKey minecraftkey = EnchantmentManager.getEnchantmentId(weightedrandomenchant.enchantment);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            MinecraftKey minecraftkey1 = EnchantmentManager.getEnchantmentId(nbttagcompound);

            if (minecraftkey1 != null && minecraftkey1.equals(minecraftkey)) {
                if (EnchantmentManager.getEnchantmentLevel(nbttagcompound) < weightedrandomenchant.level) {
                    EnchantmentManager.setEnchantmentLevel(nbttagcompound, weightedrandomenchant.level);
                }

                flag = false;
                break;
            }
        }

        if (flag) {
            nbttaglist.add(EnchantmentManager.storeEnchantment(minecraftkey, weightedrandomenchant.level));
        }

        itemstack.getOrCreateTag().put("StoredEnchantments", nbttaglist);
    }

    public static ItemStack createForEnchantment(WeightedRandomEnchant weightedrandomenchant) {
        ItemStack itemstack = new ItemStack(Items.ENCHANTED_BOOK);

        addEnchantment(itemstack, weightedrandomenchant);
        return itemstack;
    }
}
