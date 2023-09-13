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
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemTool extends ItemToolMaterial implements ItemVanishable {

    private final Tag<Block> blocks;
    protected final float speed;
    private final float attackDamageBaseline;
    private final Multimap<AttributeBase, AttributeModifier> defaultModifiers;

    protected ItemTool(float f, float f1, ToolMaterial toolmaterial, Tag<Block> tag, Item.Info item_info) {
        super(toolmaterial, item_info);
        this.blocks = tag;
        this.speed = toolmaterial.b();
        this.attackDamageBaseline = f + toolmaterial.c();
        Builder<AttributeBase, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(GenericAttributes.ATTACK_DAMAGE, new AttributeModifier(ItemTool.BASE_ATTACK_DAMAGE_UUID, "Tool modifier", (double) this.attackDamageBaseline, AttributeModifier.Operation.ADDITION));
        builder.put(GenericAttributes.ATTACK_SPEED, new AttributeModifier(ItemTool.BASE_ATTACK_SPEED_UUID, "Tool modifier", (double) f1, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        return this.blocks.isTagged(iblockdata.getBlock()) ? this.speed : 1.0F;
    }

    @Override
    public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        itemstack.damage(2, entityliving1, (entityliving2) -> {
            entityliving2.broadcastItemBreak(EnumItemSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean a(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        if (!world.isClientSide && iblockdata.h(world, blockposition) != 0.0F) {
            itemstack.damage(1, entityliving, (entityliving1) -> {
                entityliving1.broadcastItemBreak(EnumItemSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public Multimap<AttributeBase, AttributeModifier> a(EnumItemSlot enumitemslot) {
        return enumitemslot == EnumItemSlot.MAINHAND ? this.defaultModifiers : super.a(enumitemslot);
    }

    public float d() {
        return this.attackDamageBaseline;
    }

    @Override
    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        int i = this.j().d();

        return i < 3 && iblockdata.a((Tag) TagsBlock.NEEDS_DIAMOND_TOOL) ? false : (i < 2 && iblockdata.a((Tag) TagsBlock.NEEDS_IRON_TOOL) ? false : (i < 1 && iblockdata.a((Tag) TagsBlock.NEEDS_STONE_TOOL) ? false : iblockdata.a(this.blocks)));
    }
}
