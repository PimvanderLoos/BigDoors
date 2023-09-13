package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;

public interface VirtualLevelReadable {

    boolean a(BlockPosition blockposition, Predicate<IBlockData> predicate);

    BlockPosition getHighestBlockYAt(HeightMap.Type heightmap_type, BlockPosition blockposition);
}
