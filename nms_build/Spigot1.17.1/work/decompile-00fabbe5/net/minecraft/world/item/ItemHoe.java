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

public class ItemHoe extends ItemTool {

    protected static final Map<Block, Pair<Predicate<ItemActionContext>, Consumer<ItemActionContext>>> TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Pair.of(ItemHoe::b, b(Blocks.FARMLAND.getBlockData())), Blocks.DIRT_PATH, Pair.of(ItemHoe::b, b(Blocks.FARMLAND.getBlockData())), Blocks.DIRT, Pair.of(ItemHoe::b, b(Blocks.FARMLAND.getBlockData())), Blocks.COARSE_DIRT, Pair.of(ItemHoe::b, b(Blocks.DIRT.getBlockData())), Blocks.ROOTED_DIRT, Pair.of((itemactioncontext) -> {
        return true;
    }, a(Blocks.DIRT.getBlockData(), (IMaterial) Items.HANGING_ROOTS))));

    protected ItemHoe(ToolMaterial toolmaterial, int i, float f, Item.Info item_info) {
        super((float) i, f, toolmaterial, TagsBlock.MINEABLE_WITH_HOE, item_info);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        Pair<Predicate<ItemActionContext>, Consumer<ItemActionContext>> pair = (Pair) ItemHoe.TILLABLES.get(world.getType(blockposition).getBlock());

        if (pair == null) {
            return EnumInteractionResult.PASS;
        } else {
            Predicate<ItemActionContext> predicate = (Predicate) pair.getFirst();
            Consumer<ItemActionContext> consumer = (Consumer) pair.getSecond();

            if (predicate.test(itemactioncontext)) {
                EntityHuman entityhuman = itemactioncontext.getEntity();

                world.playSound(entityhuman, blockposition, SoundEffects.HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isClientSide) {
                    consumer.accept(itemactioncontext);
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

    public static Consumer<ItemActionContext> b(IBlockData iblockdata) {
        return (itemactioncontext) -> {
            itemactioncontext.getWorld().setTypeAndData(itemactioncontext.getClickPosition(), iblockdata, 11);
        };
    }

    public static Consumer<ItemActionContext> a(IBlockData iblockdata, IMaterial imaterial) {
        return (itemactioncontext) -> {
            itemactioncontext.getWorld().setTypeAndData(itemactioncontext.getClickPosition(), iblockdata, 11);
            Block.a(itemactioncontext.getWorld(), itemactioncontext.getClickPosition(), itemactioncontext.getClickedFace(), new ItemStack(imaterial));
        };
    }

    public static boolean b(ItemActionContext itemactioncontext) {
        return itemactioncontext.getClickedFace() != EnumDirection.DOWN && itemactioncontext.getWorld().getType(itemactioncontext.getClickPosition().up()).isAir();
    }
}
