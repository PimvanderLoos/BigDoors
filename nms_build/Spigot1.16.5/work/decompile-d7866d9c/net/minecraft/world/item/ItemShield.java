package net.minecraft.world.item;

import net.minecraft.tags.TagsItem;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDispenser;

public class ItemShield extends Item {

    public ItemShield(Item.Info item_info) {
        super(item_info);
        BlockDispenser.a((IMaterial) this, ItemArmor.a);
    }

    @Override
    public String f(ItemStack itemstack) {
        return itemstack.b("BlockEntityTag") != null ? this.getName() + '.' + d(itemstack).c() : super.f(itemstack);
    }

    @Override
    public EnumAnimation d_(ItemStack itemstack) {
        return EnumAnimation.BLOCK;
    }

    @Override
    public int e_(ItemStack itemstack) {
        return 72000;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        entityhuman.c(enumhand);
        return InteractionResultWrapper.consume(itemstack);
    }

    @Override
    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return TagsItem.PLANKS.isTagged(itemstack1.getItem()) || super.a(itemstack, itemstack1);
    }

    public static EnumColor d(ItemStack itemstack) {
        return EnumColor.fromColorIndex(itemstack.a("BlockEntityTag").getInt("Base"));
    }
}
