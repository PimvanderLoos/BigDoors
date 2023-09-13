package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;

public class ItemHoe extends ItemTool {

    private static final Set<Block> c = ImmutableSet.of(Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.HAY_BLOCK, Blocks.DRIED_KELP_BLOCK, Blocks.TARGET, Blocks.SHROOMLIGHT, new Block[]{Blocks.SPONGE, Blocks.WET_SPONGE, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES});
    protected static final Map<Block, IBlockData> a = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getBlockData(), Blocks.GRASS_PATH, Blocks.FARMLAND.getBlockData(), Blocks.DIRT, Blocks.FARMLAND.getBlockData(), Blocks.COARSE_DIRT, Blocks.DIRT.getBlockData()));

    protected ItemHoe(ToolMaterial toolmaterial, int i, float f, Item.Info item_info) {
        super((float) i, f, toolmaterial, ItemHoe.c, item_info);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();

        if (itemactioncontext.getClickedFace() != EnumDirection.DOWN && world.getType(blockposition.up()).isAir()) {
            IBlockData iblockdata = (IBlockData) ItemHoe.a.get(world.getType(blockposition).getBlock());

            if (iblockdata != null) {
                EntityHuman entityhuman = itemactioncontext.getEntity();

                world.playSound(entityhuman, blockposition, SoundEffects.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isClientSide) {
                    world.setTypeAndData(blockposition, iblockdata, 11);
                    if (entityhuman != null) {
                        itemactioncontext.getItemStack().damage(1, entityhuman, (entityhuman1) -> {
                            entityhuman1.broadcastItemBreak(itemactioncontext.getHand());
                        });
                    }
                }

                return EnumInteractionResult.a(world.isClientSide);
            }
        }

        return EnumInteractionResult.PASS;
    }
}
