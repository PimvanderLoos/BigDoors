package net.minecraft.world.item;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Material;

public class ItemPickaxe extends ItemTool {

    private static final Set<Block> a = ImmutableSet.of(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, new Block[]{Blocks.POWERED_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB, Blocks.SANDSTONE_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB, Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.POLISHED_GRANITE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.PISTON_HEAD});

    protected ItemPickaxe(ToolMaterial toolmaterial, int i, float f, Item.Info item_info) {
        super((float) i, f, toolmaterial, ItemPickaxe.a, item_info);
    }

    @Override
    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        int i = this.g().d();

        if (!iblockdata.a(Blocks.OBSIDIAN) && !iblockdata.a(Blocks.CRYING_OBSIDIAN) && !iblockdata.a(Blocks.NETHERITE_BLOCK) && !iblockdata.a(Blocks.RESPAWN_ANCHOR) && !iblockdata.a(Blocks.ANCIENT_DEBRIS)) {
            if (!iblockdata.a(Blocks.DIAMOND_BLOCK) && !iblockdata.a(Blocks.DIAMOND_ORE) && !iblockdata.a(Blocks.EMERALD_ORE) && !iblockdata.a(Blocks.EMERALD_BLOCK) && !iblockdata.a(Blocks.GOLD_BLOCK) && !iblockdata.a(Blocks.GOLD_ORE) && !iblockdata.a(Blocks.REDSTONE_ORE)) {
                if (!iblockdata.a(Blocks.IRON_BLOCK) && !iblockdata.a(Blocks.IRON_ORE) && !iblockdata.a(Blocks.LAPIS_BLOCK) && !iblockdata.a(Blocks.LAPIS_ORE)) {
                    Material material = iblockdata.getMaterial();

                    return material == Material.STONE || material == Material.ORE || material == Material.HEAVY || iblockdata.a(Blocks.NETHER_GOLD_ORE);
                } else {
                    return i >= 1;
                }
            } else {
                return i >= 2;
            }
        } else {
            return i >= 3;
        }
    }

    @Override
    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        Material material = iblockdata.getMaterial();

        return material != Material.ORE && material != Material.HEAVY && material != Material.STONE ? super.getDestroySpeed(itemstack, iblockdata) : this.b;
    }
}
