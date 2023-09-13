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
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDispenser;

public class ItemElytra extends Item implements ItemWearable {

    public ItemElytra(Item.Info item_info) {
        super(item_info);
        BlockDispenser.registerBehavior(this, ItemArmor.DISPENSE_ITEM_BEHAVIOR);
    }

    public static boolean isFlyEnabled(ItemStack itemstack) {
        return itemstack.getDamageValue() < itemstack.getMaxDamage() - 1;
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack1.is(Items.PHANTOM_MEMBRANE);
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = entityhuman.getItemBySlot(enumitemslot);

        if (itemstack1.isEmpty()) {
            entityhuman.setItemSlot(enumitemslot, itemstack.copy());
            if (!world.isClientSide()) {
                entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
            }

            itemstack.setCount(0);
            return InteractionResultWrapper.sidedSuccess(itemstack, world.isClientSide());
        } else {
            return InteractionResultWrapper.fail(itemstack);
        }
    }

    @Nullable
    @Override
    public SoundEffect getEquipSound() {
        return SoundEffects.ARMOR_EQUIP_ELYTRA;
    }
}
