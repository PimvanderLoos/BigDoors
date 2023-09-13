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
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDispenser;

public class ItemElytra extends Item implements ItemWearable {

    public ItemElytra(Item.Info item_info) {
        super(item_info);
        BlockDispenser.a((IMaterial) this, ItemArmor.DISPENSE_ITEM_BEHAVIOR);
    }

    public static boolean d(ItemStack itemstack) {
        return itemstack.getDamage() < itemstack.i() - 1;
    }

    @Override
    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack1.a(Items.PHANTOM_MEMBRANE);
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = entityhuman.getEquipment(enumitemslot);

        if (itemstack1.isEmpty()) {
            entityhuman.setSlot(enumitemslot, itemstack.cloneItemStack());
            if (!world.isClientSide()) {
                entityhuman.b(StatisticList.ITEM_USED.b(this));
            }

            itemstack.setCount(0);
            return InteractionResultWrapper.a(itemstack, world.isClientSide());
        } else {
            return InteractionResultWrapper.fail(itemstack);
        }
    }

    @Nullable
    @Override
    public SoundEffect g() {
        return SoundEffects.ARMOR_EQUIP_ELYTRA;
    }
}
