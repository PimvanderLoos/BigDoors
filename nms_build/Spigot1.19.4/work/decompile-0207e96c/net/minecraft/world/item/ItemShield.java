package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDispenser;

public class ItemShield extends Item implements Equipable {

    public static final int EFFECTIVE_BLOCK_DELAY = 5;
    public static final float MINIMUM_DURABILITY_DAMAGE = 3.0F;
    public static final String TAG_BASE_COLOR = "Base";

    public ItemShield(Item.Info item_info) {
        super(item_info);
        BlockDispenser.registerBehavior(this, ItemArmor.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public String getDescriptionId(ItemStack itemstack) {
        if (ItemBlock.getBlockEntityData(itemstack) != null) {
            String s = this.getDescriptionId();

            return s + "." + getColor(itemstack).getName();
        } else {
            return super.getDescriptionId(itemstack);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        ItemBanner.appendHoverTextFromBannerBlockEntityTag(itemstack, list);
    }

    @Override
    public EnumAnimation getUseAnimation(ItemStack itemstack) {
        return EnumAnimation.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 72000;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        entityhuman.startUsingItem(enumhand);
        return InteractionResultWrapper.consume(itemstack);
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack1.is(TagsItem.PLANKS) || super.isValidRepairItem(itemstack, itemstack1);
    }

    public static EnumColor getColor(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

        return nbttagcompound != null ? EnumColor.byId(nbttagcompound.getInt("Base")) : EnumColor.WHITE;
    }

    @Override
    public EnumItemSlot getEquipmentSlot() {
        return EnumItemSlot.OFFHAND;
    }
}
