package net.minecraft.world.item;

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
        BlockDispenser.a((IMaterial) this, ItemArmor.a);
    }

    public static boolean d(ItemStack itemstack) {
        return itemstack.getDamage() < itemstack.h() - 1;
    }

    @Override
    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack1.getItem() == Items.PHANTOM_MEMBRANE;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        EnumItemSlot enumitemslot = EntityInsentient.j(itemstack);
        ItemStack itemstack1 = entityhuman.getEquipment(enumitemslot);

        if (itemstack1.isEmpty()) {
            entityhuman.setSlot(enumitemslot, itemstack.cloneItemStack());
            itemstack.setCount(0);
            return InteractionResultWrapper.a(itemstack, world.s_());
        } else {
            return InteractionResultWrapper.fail(itemstack);
        }
    }
}
