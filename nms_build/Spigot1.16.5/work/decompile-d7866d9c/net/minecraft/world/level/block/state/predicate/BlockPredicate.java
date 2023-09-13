package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockPredicate implements Predicate<IBlockData> {

    private final Block a;

    public BlockPredicate(Block block) {
        this.a = block;
    }

    public static BlockPredicate a(Block block) {
        return new BlockPredicate(block);
    }

    public boolean test(@Nullable IBlockData iblockdata) {
        return iblockdata != null && iblockdata.a(this.a);
    }
}
