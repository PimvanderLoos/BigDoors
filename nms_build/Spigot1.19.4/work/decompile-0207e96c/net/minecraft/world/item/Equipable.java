package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;

public interface Equipable extends ItemVanishable {

    EnumItemSlot getEquipmentSlot();

    default SoundEffect getEquipSound() {
        return SoundEffects.ARMOR_EQUIP_GENERIC;
    }

    default InteractionResultWrapper<ItemStack> swapWithEquipmentSlot(Item item, World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = entityhuman.getItemBySlot(enumitemslot);

        if (!EnchantmentManager.hasBindingCurse(itemstack1) && !ItemStack.matches(itemstack, itemstack1)) {
            entityhuman.setItemSlot(enumitemslot, itemstack.copy());
            if (!world.isClientSide()) {
                entityhuman.awardStat(StatisticList.ITEM_USED.get(item));
            }

            if (itemstack1.isEmpty()) {
                itemstack.setCount(0);
            } else {
                entityhuman.setItemInHand(enumhand, itemstack1.copy());
            }

            return InteractionResultWrapper.sidedSuccess(itemstack, world.isClientSide());
        } else {
            return InteractionResultWrapper.fail(itemstack);
        }
    }

    @Nullable
    static Equipable get(ItemStack itemstack) {
        Item item = itemstack.getItem();

        if (item instanceof Equipable) {
            Equipable equipable = (Equipable) item;

            return equipable;
        } else {
            Item item1 = itemstack.getItem();

            if (item1 instanceof ItemBlock) {
                ItemBlock itemblock = (ItemBlock) item1;
                Block block = itemblock.getBlock();

                if (block instanceof Equipable) {
                    Equipable equipable1 = (Equipable) block;

                    return equipable1;
                }
            }

            return null;
        }
    }
}
