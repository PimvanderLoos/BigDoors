package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemHoe extends ItemTool {

    protected static final Map<Block, Pair<Predicate<ItemActionContext>, Consumer<ItemActionContext>>> TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Pair.of(ItemHoe::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.DIRT_PATH, Pair.of(ItemHoe::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.DIRT, Pair.of(ItemHoe::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.COARSE_DIRT, Pair.of(ItemHoe::onlyIfAirAbove, changeIntoState(Blocks.DIRT.defaultBlockState())), Blocks.ROOTED_DIRT, Pair.of((itemactioncontext) -> {
        return true;
    }, changeIntoStateAndDropItem(Blocks.DIRT.defaultBlockState(), Items.HANGING_ROOTS))));

    protected ItemHoe(ToolMaterial toolmaterial, int i, float f, Item.Info item_info) {
        super((float) i, f, toolmaterial, TagsBlock.MINEABLE_WITH_HOE, item_info);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        Pair<Predicate<ItemActionContext>, Consumer<ItemActionContext>> pair = (Pair) ItemHoe.TILLABLES.get(world.getBlockState(blockposition).getBlock());

        if (pair == null) {
            return EnumInteractionResult.PASS;
        } else {
            Predicate<ItemActionContext> predicate = (Predicate) pair.getFirst();
            Consumer<ItemActionContext> consumer = (Consumer) pair.getSecond();

            if (predicate.test(itemactioncontext)) {
                EntityHuman entityhuman = itemactioncontext.getPlayer();

                world.playSound(entityhuman, blockposition, SoundEffects.HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isClientSide) {
                    consumer.accept(itemactioncontext);
                    if (entityhuman != null) {
                        itemactioncontext.getItemInHand().hurtAndBreak(1, entityhuman, (entityhuman1) -> {
                            entityhuman1.broadcastBreakEvent(itemactioncontext.getHand());
                        });
                    }
                }

                return EnumInteractionResult.sidedSuccess(world.isClientSide);
            } else {
                return EnumInteractionResult.PASS;
            }
        }
    }

    public static Consumer<ItemActionContext> changeIntoState(IBlockData iblockdata) {
        return (itemactioncontext) -> {
            itemactioncontext.getLevel().setBlock(itemactioncontext.getClickedPos(), iblockdata, 11);
            itemactioncontext.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, itemactioncontext.getClickedPos(), GameEvent.a.of(itemactioncontext.getPlayer(), iblockdata));
        };
    }

    public static Consumer<ItemActionContext> changeIntoStateAndDropItem(IBlockData iblockdata, IMaterial imaterial) {
        return (itemactioncontext) -> {
            itemactioncontext.getLevel().setBlock(itemactioncontext.getClickedPos(), iblockdata, 11);
            itemactioncontext.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, itemactioncontext.getClickedPos(), GameEvent.a.of(itemactioncontext.getPlayer(), iblockdata));
            Block.popResourceFromFace(itemactioncontext.getLevel(), itemactioncontext.getClickedPos(), itemactioncontext.getClickedFace(), new ItemStack(imaterial));
        };
    }

    public static boolean onlyIfAirAbove(ItemActionContext itemactioncontext) {
        return itemactioncontext.getClickedFace() != EnumDirection.DOWN && itemactioncontext.getLevel().getBlockState(itemactioncontext.getClickedPos().above()).isAir();
    }
}
