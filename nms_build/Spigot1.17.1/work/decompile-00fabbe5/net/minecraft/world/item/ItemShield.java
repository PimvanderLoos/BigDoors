package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDispenser;

public class ItemShield extends Item {

    public static final int EFFECTIVE_BLOCK_DELAY = 5;
    public static final float MINIMUM_DURABILITY_DAMAGE = 3.0F;
    public static final String TAG_BASE_COLOR = "Base";

    public ItemShield(Item.Info item_info) {
        super(item_info);
        BlockDispenser.a((IMaterial) this, ItemArmor.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public String j(ItemStack itemstack) {
        if (itemstack.b("BlockEntityTag") != null) {
            String s = this.getName();

            return s + "." + d(itemstack).b();
        } else {
            return super.j(itemstack);
        }
    }

    @Override
    public void a(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        ItemBanner.a(itemstack, list);
    }

    @Override
    public EnumAnimation c(ItemStack itemstack) {
        return EnumAnimation.BLOCK;
    }

    @Override
    public int b(ItemStack itemstack) {
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
        return itemstack1.a((Tag) TagsItem.PLANKS) || super.a(itemstack, itemstack1);
    }

    public static EnumColor d(ItemStack itemstack) {
        return EnumColor.fromColorIndex(itemstack.a("BlockEntityTag").getInt("Base"));
    }
}
