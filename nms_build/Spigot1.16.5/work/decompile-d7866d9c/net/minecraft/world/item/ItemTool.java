package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemTool extends ItemToolMaterial implements ItemVanishable {

    private final Set<Block> a;
    protected final float b;
    private final float c;
    private final Multimap<AttributeBase, AttributeModifier> d;

    protected ItemTool(float f, float f1, ToolMaterial toolmaterial, Set<Block> set, Item.Info item_info) {
        super(toolmaterial, item_info);
        this.a = set;
        this.b = toolmaterial.b();
        this.c = f + toolmaterial.c();
        Builder<AttributeBase, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(GenericAttributes.ATTACK_DAMAGE, new AttributeModifier(ItemTool.f, "Tool modifier", (double) this.c, AttributeModifier.Operation.ADDITION));
        builder.put(GenericAttributes.ATTACK_SPEED, new AttributeModifier(ItemTool.g, "Tool modifier", (double) f1, AttributeModifier.Operation.ADDITION));
        this.d = builder.build();
    }

    @Override
    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        return this.a.contains(iblockdata.getBlock()) ? this.b : 1.0F;
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
        return enumitemslot == EnumItemSlot.MAINHAND ? this.d : super.a(enumitemslot);
    }

    public float d() {
        return this.c;
    }
}
