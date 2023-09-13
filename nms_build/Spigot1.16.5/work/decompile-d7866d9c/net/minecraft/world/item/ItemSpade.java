package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCampfire;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemSpade extends ItemTool {

    private static final Set<Block> c = Sets.newHashSet(new Block[]{Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.SOUL_SOIL});
    protected static final Map<Block, IBlockData> a = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH.getBlockData()));

    public ItemSpade(ToolMaterial toolmaterial, float f, float f1, Item.Info item_info) {
        super(f, f1, toolmaterial, ItemSpade.c, item_info);
    }

    @Override
    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        return iblockdata.a(Blocks.SNOW) || iblockdata.a(Blocks.SNOW_BLOCK);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        if (itemactioncontext.getClickedFace() == EnumDirection.DOWN) {
            return EnumInteractionResult.PASS;
        } else {
            EntityHuman entityhuman = itemactioncontext.getEntity();
            IBlockData iblockdata1 = (IBlockData) ItemSpade.a.get(iblockdata.getBlock());
            IBlockData iblockdata2 = null;

            if (iblockdata1 != null && world.getType(blockposition.up()).isAir()) {
                world.playSound(entityhuman, blockposition, SoundEffects.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                iblockdata2 = iblockdata1;
            } else if (iblockdata.getBlock() instanceof BlockCampfire && (Boolean) iblockdata.get(BlockCampfire.LIT)) {
                if (!world.s_()) {
                    world.a((EntityHuman) null, 1009, blockposition, 0);
                }

                BlockCampfire.c((GeneratorAccess) world, blockposition, iblockdata);
                iblockdata2 = (IBlockData) iblockdata.set(BlockCampfire.LIT, false);
            }

            if (iblockdata2 != null) {
                if (!world.isClientSide) {
                    world.setTypeAndData(blockposition, iblockdata2, 11);
                    if (entityhuman != null) {
                        itemactioncontext.getItemStack().damage(1, entityhuman, (entityhuman1) -> {
                            entityhuman1.broadcastItemBreak(itemactioncontext.getHand());
                        });
                    }
                }

                return EnumInteractionResult.a(world.isClientSide);
            } else {
                return EnumInteractionResult.PASS;
            }
        }
    }
}
