package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCampfire;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemSpade extends ItemTool {

    protected static final Map<Block, IBlockData> FLATTENABLES = Maps.newHashMap((new Builder()).put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.getBlockData()).put(Blocks.DIRT, Blocks.DIRT_PATH.getBlockData()).put(Blocks.PODZOL, Blocks.DIRT_PATH.getBlockData()).put(Blocks.COARSE_DIRT, Blocks.DIRT_PATH.getBlockData()).put(Blocks.MYCELIUM, Blocks.DIRT_PATH.getBlockData()).put(Blocks.ROOTED_DIRT, Blocks.DIRT_PATH.getBlockData()).build());

    public ItemSpade(ToolMaterial toolmaterial, float f, float f1, Item.Info item_info) {
        super(f, f1, toolmaterial, TagsBlock.MINEABLE_WITH_SHOVEL, item_info);
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
            IBlockData iblockdata1 = (IBlockData) ItemSpade.FLATTENABLES.get(iblockdata.getBlock());
            IBlockData iblockdata2 = null;

            if (iblockdata1 != null && world.getType(blockposition.up()).isAir()) {
                world.playSound(entityhuman, blockposition, SoundEffects.SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                iblockdata2 = iblockdata1;
            } else if (iblockdata.getBlock() instanceof BlockCampfire && (Boolean) iblockdata.get(BlockCampfire.LIT)) {
                if (!world.isClientSide()) {
                    world.a((EntityHuman) null, 1009, blockposition, 0);
                }

                BlockCampfire.a((Entity) itemactioncontext.getEntity(), (GeneratorAccess) world, blockposition, iblockdata);
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
