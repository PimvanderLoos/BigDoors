package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;

public class ItemPickaxe extends ItemTool {

    private static final Set<Block> e = Sets.newHashSet(new Block[] { Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE});

    protected ItemPickaxe(Item.EnumToolMaterial item_enumtoolmaterial) {
        super(1.0F, -2.8F, item_enumtoolmaterial, ItemPickaxe.e);
    }

    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        Block block = iblockdata.getBlock();

        if (block == Blocks.OBSIDIAN) {
            return this.d.d() == 3;
        } else if (block != Blocks.DIAMOND_BLOCK && block != Blocks.DIAMOND_ORE) {
            if (block != Blocks.EMERALD_ORE && block != Blocks.EMERALD_BLOCK) {
                if (block != Blocks.GOLD_BLOCK && block != Blocks.GOLD_ORE) {
                    if (block != Blocks.IRON_BLOCK && block != Blocks.IRON_ORE) {
                        if (block != Blocks.LAPIS_BLOCK && block != Blocks.LAPIS_ORE) {
                            if (block != Blocks.REDSTONE_ORE && block != Blocks.LIT_REDSTONE_ORE) {
                                Material material = iblockdata.getMaterial();

                                return material == Material.STONE ? true : (material == Material.ORE ? true : material == Material.HEAVY);
                            } else {
                                return this.d.d() >= 2;
                            }
                        } else {
                            return this.d.d() >= 1;
                        }
                    } else {
                        return this.d.d() >= 1;
                    }
                } else {
                    return this.d.d() >= 2;
                }
            } else {
                return this.d.d() >= 2;
            }
        } else {
            return this.d.d() >= 2;
        }
    }

    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        Material material = iblockdata.getMaterial();

        return material != Material.ORE && material != Material.HEAVY && material != Material.STONE ? super.getDestroySpeed(itemstack, iblockdata) : this.a;
    }
}
