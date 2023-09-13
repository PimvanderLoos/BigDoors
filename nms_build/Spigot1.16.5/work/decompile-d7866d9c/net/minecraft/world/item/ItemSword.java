package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Material;

public class ItemSword extends ItemToolMaterial implements ItemVanishable {

    private final float a;
    private final Multimap<AttributeBase, AttributeModifier> b;

    public ItemSword(ToolMaterial toolmaterial, int i, float f, Item.Info item_info) {
        super(toolmaterial, item_info);
        this.a = (float) i + toolmaterial.c();
        Builder<AttributeBase, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(GenericAttributes.ATTACK_DAMAGE, new AttributeModifier(ItemSword.f, "Weapon modifier", (double) this.a, AttributeModifier.Operation.ADDITION));
        builder.put(GenericAttributes.ATTACK_SPEED, new AttributeModifier(ItemSword.g, "Weapon modifier", (double) f, AttributeModifier.Operation.ADDITION));
        this.b = builder.build();
    }

    public float f() {
        return this.a;
    }

    @Override
    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        return !entityhuman.isCreative();
    }

    @Override
    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        if (iblockdata.a(Blocks.COBWEB)) {
            return 15.0F;
        } else {
            Material material = iblockdata.getMaterial();

            return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && material != Material.CORAL && !iblockdata.a((Tag) TagsBlock.LEAVES) && material != Material.PUMPKIN ? 1.0F : 1.5F;
        }
    }

    @Override
    public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        itemstack.damage(1, entityliving1, (entityliving2) -> {
            entityliving2.broadcastItemBreak(EnumItemSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean a(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        if (iblockdata.h(world, blockposition) != 0.0F) {
            itemstack.damage(2, entityliving, (entityliving1) -> {
                entityliving1.broadcastItemBreak(EnumItemSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        return iblockdata.a(Blocks.COBWEB);
    }

    @Override
    public Multimap<AttributeBase, AttributeModifier> a(EnumItemSlot enumitemslot) {
        return enumitemslot == EnumItemSlot.MAINHAND ? this.b : super.a(enumitemslot);
    }
}
