package net.minecraft.world.level.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.level.block.state.IBlockData;

public interface WeatheringCopper extends ChangeOverTimeBlock<WeatheringCopper.a> {

    Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> {
        return ImmutableBiMap.builder().put(Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER).put(Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER).put(Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER).put(Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER).put(Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER).put(Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER).put(Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB).put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB).put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB).put(Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS).put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS).put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS).build();
    });
    Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> {
        return ((BiMap) WeatheringCopper.NEXT_BY_BLOCK.get()).inverse();
    });

    static Optional<Block> a(Block block) {
        return Optional.ofNullable((Block) ((BiMap) WeatheringCopper.PREVIOUS_BY_BLOCK.get()).get(block));
    }

    static Block b(Block block) {
        Block block1 = block;

        for (Block block2 = (Block) ((BiMap) WeatheringCopper.PREVIOUS_BY_BLOCK.get()).get(block); block2 != null; block2 = (Block) ((BiMap) WeatheringCopper.PREVIOUS_BY_BLOCK.get()).get(block2)) {
            block1 = block2;
        }

        return block1;
    }

    static Optional<IBlockData> b(IBlockData iblockdata) {
        return a(iblockdata.getBlock()).map((block) -> {
            return block.l(iblockdata);
        });
    }

    static Optional<Block> c(Block block) {
        return Optional.ofNullable((Block) ((BiMap) WeatheringCopper.NEXT_BY_BLOCK.get()).get(block));
    }

    static IBlockData c(IBlockData iblockdata) {
        return b(iblockdata.getBlock()).l(iblockdata);
    }

    @Override
    default Optional<IBlockData> a(IBlockData iblockdata) {
        return c(iblockdata.getBlock()).map((block) -> {
            return block.l(iblockdata);
        });
    }

    @Override
    default float a() {
        return this.b() == WeatheringCopper.a.UNAFFECTED ? 0.75F : 1.0F;
    }

    public static enum a {

        UNAFFECTED, EXPOSED, WEATHERED, OXIDIZED;

        private a() {}
    }
}
