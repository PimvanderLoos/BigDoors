package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;

public class ItemAxe extends ItemTool {

    private static final Set<Block> e = Sets.newHashSet(new Block[] { Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE});
    private static final float[] f = new float[] { 6.0F, 8.0F, 8.0F, 8.0F, 6.0F};
    private static final float[] n = new float[] { -3.2F, -3.2F, -3.1F, -3.0F, -3.0F};

    protected ItemAxe(Item.EnumToolMaterial item_enumtoolmaterial) {
        super(item_enumtoolmaterial, ItemAxe.e);
        this.b = ItemAxe.f[item_enumtoolmaterial.ordinal()];
        this.c = ItemAxe.n[item_enumtoolmaterial.ordinal()];
    }

    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        Material material = iblockdata.getMaterial();

        return material != Material.WOOD && material != Material.PLANT && material != Material.REPLACEABLE_PLANT ? super.getDestroySpeed(itemstack, iblockdata) : this.a;
    }
}
