package net.minecraft.world.item;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockRotatable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemAxe extends ItemTool {

    protected static final Map<Block, Block> STRIPPABLES = (new Builder()).put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD).put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD).put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG).put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK).build();

    protected ItemAxe(ToolMaterial toolmaterial, float f, float f1, Item.Info item_info) {
        super(f, f1, toolmaterial, TagsBlock.MINEABLE_WITH_AXE, item_info);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        EntityHuman entityhuman = itemactioncontext.getPlayer();
        IBlockData iblockdata = world.getBlockState(blockposition);
        Optional<IBlockData> optional = this.getStripped(iblockdata);
        Optional<IBlockData> optional1 = WeatheringCopper.getPrevious(iblockdata);
        Optional<IBlockData> optional2 = Optional.ofNullable((Block) ((BiMap) HoneycombItem.WAX_OFF_BY_BLOCK.get()).get(iblockdata.getBlock())).map((block) -> {
            return block.withPropertiesOf(iblockdata);
        });
        ItemStack itemstack = itemactioncontext.getItemInHand();
        Optional<IBlockData> optional3 = Optional.empty();

        if (optional.isPresent()) {
            world.playSound(entityhuman, blockposition, SoundEffects.AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            optional3 = optional;
        } else if (optional1.isPresent()) {
            world.playSound(entityhuman, blockposition, SoundEffects.AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.levelEvent(entityhuman, 3005, blockposition, 0);
            optional3 = optional1;
        } else if (optional2.isPresent()) {
            world.playSound(entityhuman, blockposition, SoundEffects.AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.levelEvent(entityhuman, 3004, blockposition, 0);
            optional3 = optional2;
        }

        if (optional3.isPresent()) {
            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.ITEM_USED_ON_BLOCK.trigger((EntityPlayer) entityhuman, blockposition, itemstack);
            }

            world.setBlock(blockposition, (IBlockData) optional3.get(), 11);
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(entityhuman, (IBlockData) optional3.get()));
            if (entityhuman != null) {
                itemstack.hurtAndBreak(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastBreakEvent(itemactioncontext.getHand());
                });
            }

            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    private Optional<IBlockData> getStripped(IBlockData iblockdata) {
        return Optional.ofNullable((Block) ItemAxe.STRIPPABLES.get(iblockdata.getBlock())).map((block) -> {
            return (IBlockData) block.defaultBlockState().setValue(BlockRotatable.AXIS, (EnumDirection.EnumAxis) iblockdata.getValue(BlockRotatable.AXIS));
        });
    }
}
