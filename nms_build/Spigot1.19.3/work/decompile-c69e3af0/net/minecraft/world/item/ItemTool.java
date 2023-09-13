package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemTool extends ItemToolMaterial implements ItemVanishable {

    private final TagKey<Block> blocks;
    protected final float speed;
    private final float attackDamageBaseline;
    private final Multimap<AttributeBase, AttributeModifier> defaultModifiers;

    protected ItemTool(float f, float f1, ToolMaterial toolmaterial, TagKey<Block> tagkey, Item.Info item_info) {
        super(toolmaterial, item_info);
        this.blocks = tagkey;
        this.speed = toolmaterial.getSpeed();
        this.attackDamageBaseline = f + toolmaterial.getAttackDamageBonus();
        Builder<AttributeBase, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(GenericAttributes.ATTACK_DAMAGE, new AttributeModifier(ItemTool.BASE_ATTACK_DAMAGE_UUID, "Tool modifier", (double) this.attackDamageBaseline, AttributeModifier.Operation.ADDITION));
        builder.put(GenericAttributes.ATTACK_SPEED, new AttributeModifier(ItemTool.BASE_ATTACK_SPEED_UUID, "Tool modifier", (double) f1, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        return iblockdata.is(this.blocks) ? this.speed : 1.0F;
    }

    @Override
    public boolean hurtEnemy(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        itemstack.hurtAndBreak(2, entityliving1, (entityliving2) -> {
            entityliving2.broadcastBreakEvent(EnumItemSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        if (!world.isClientSide && iblockdata.getDestroySpeed(world, blockposition) != 0.0F) {
            itemstack.hurtAndBreak(1, entityliving, (entityliving1) -> {
                entityliving1.broadcastBreakEvent(EnumItemSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public Multimap<AttributeBase, AttributeModifier> getDefaultAttributeModifiers(EnumItemSlot enumitemslot) {
        return enumitemslot == EnumItemSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(enumitemslot);
    }

    public float getAttackDamage() {
        return this.attackDamageBaseline;
    }

    @Override
    public boolean isCorrectToolForDrops(IBlockData iblockdata) {
        int i = this.getTier().getLevel();

        return i < 3 && iblockdata.is(TagsBlock.NEEDS_DIAMOND_TOOL) ? false : (i < 2 && iblockdata.is(TagsBlock.NEEDS_IRON_TOOL) ? false : (i < 1 && iblockdata.is(TagsBlock.NEEDS_STONE_TOOL) ? false : iblockdata.is(this.blocks)));
    }
}
