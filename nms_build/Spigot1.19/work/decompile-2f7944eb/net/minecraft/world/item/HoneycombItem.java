package net.minecraft.world.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;

public class HoneycombItem extends Item {

    public static final Supplier<BiMap<Block, Block>> WAXABLES = Suppliers.memoize(() -> {
        return ImmutableBiMap.builder().put(Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK).put(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER).put(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER).put(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER).put(Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER).put(Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER).put(Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER).put(Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER).put(Blocks.CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB).put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB).put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB).put(Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB).put(Blocks.CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS).put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS).put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS).put(Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS).build();
    });
    public static final Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> {
        return ((BiMap) HoneycombItem.WAXABLES.get()).inverse();
    });

    public HoneycombItem(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        IBlockData iblockdata = world.getBlockState(blockposition);

        return (EnumInteractionResult) getWaxed(iblockdata).map((iblockdata1) -> {
            EntityHuman entityhuman = itemactioncontext.getPlayer();
            ItemStack itemstack = itemactioncontext.getItemInHand();

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.ITEM_USED_ON_BLOCK.trigger((EntityPlayer) entityhuman, blockposition, itemstack);
            }

            itemstack.shrink(1);
            world.setBlock(blockposition, iblockdata1, 11);
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(entityhuman, iblockdata1));
            world.levelEvent(entityhuman, 3003, blockposition, 0);
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        }).orElse(EnumInteractionResult.PASS);
    }

    public static Optional<IBlockData> getWaxed(IBlockData iblockdata) {
        return Optional.ofNullable((Block) ((BiMap) HoneycombItem.WAXABLES.get()).get(iblockdata.getBlock())).map((block) -> {
            return block.withPropertiesOf(iblockdata);
        });
    }
}
