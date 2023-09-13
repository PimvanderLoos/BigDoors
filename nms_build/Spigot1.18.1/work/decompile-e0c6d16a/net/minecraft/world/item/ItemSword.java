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

    private final float attackDamage;
    private final Multimap<AttributeBase, AttributeModifier> defaultModifiers;

    public ItemSword(ToolMaterial toolmaterial, int i, float f, Item.Info item_info) {
        super(toolmaterial, item_info);
        this.attackDamage = (float) i + toolmaterial.getAttackDamageBonus();
        Builder<AttributeBase, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(GenericAttributes.ATTACK_DAMAGE, new AttributeModifier(ItemSword.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double) this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(GenericAttributes.ATTACK_SPEED, new AttributeModifier(ItemSword.BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double) f, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public float getDamage() {
        return this.attackDamage;
    }

    @Override
    public boolean canAttackBlock(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        return !entityhuman.isCreative();
    }

    @Override
    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        if (iblockdata.is(Blocks.COBWEB)) {
            return 15.0F;
        } else {
            Material material = iblockdata.getMaterial();

            return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && !iblockdata.is((Tag) TagsBlock.LEAVES) && material != Material.VEGETABLE ? 1.0F : 1.5F;
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        itemstack.hurtAndBreak(1, entityliving1, (entityliving2) -> {
            entityliving2.broadcastBreakEvent(EnumItemSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        if (iblockdata.getDestroySpeed(world, blockposition) != 0.0F) {
            itemstack.hurtAndBreak(2, entityliving, (entityliving1) -> {
                entityliving1.broadcastBreakEvent(EnumItemSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(IBlockData iblockdata) {
        return iblockdata.is(Blocks.COBWEB);
    }

    @Override
    public Multimap<AttributeBase, AttributeModifier> getDefaultAttributeModifiers(EnumItemSlot enumitemslot) {
        return enumitemslot == EnumItemSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(enumitemslot);
    }
}
