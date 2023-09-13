package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockTorchWall;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenEndTrophy extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public static final int PODIUM_RADIUS = 4;
    public static final int PODIUM_PILLAR_HEIGHT = 4;
    public static final int RIM_RADIUS = 1;
    public static final float CORNER_ROUNDING = 0.5F;
    public static final BlockPosition END_PODIUM_LOCATION = BlockPosition.ZERO;
    private final boolean active;

    public WorldGenEndTrophy(boolean flag) {
        super(WorldGenFeatureEmptyConfiguration.CODEC);
        this.active = flag;
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        BlockPosition blockposition = featureplacecontext.origin();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        Iterator iterator = BlockPosition.betweenClosed(new BlockPosition(blockposition.getX() - 4, blockposition.getY() - 1, blockposition.getZ() - 4), new BlockPosition(blockposition.getX() + 4, blockposition.getY() + 32, blockposition.getZ() + 4)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            boolean flag = blockposition1.closerThan((BaseBlockPosition) blockposition, 2.5D);

            if (flag || blockposition1.closerThan((BaseBlockPosition) blockposition, 3.5D)) {
                if (blockposition1.getY() < blockposition.getY()) {
                    if (flag) {
                        this.setBlock(generatoraccessseed, blockposition1, Blocks.BEDROCK.defaultBlockState());
                    } else if (blockposition1.getY() < blockposition.getY()) {
                        this.setBlock(generatoraccessseed, blockposition1, Blocks.END_STONE.defaultBlockState());
                    }
                } else if (blockposition1.getY() > blockposition.getY()) {
                    this.setBlock(generatoraccessseed, blockposition1, Blocks.AIR.defaultBlockState());
                } else if (!flag) {
                    this.setBlock(generatoraccessseed, blockposition1, Blocks.BEDROCK.defaultBlockState());
                } else if (this.active) {
                    this.setBlock(generatoraccessseed, new BlockPosition(blockposition1), Blocks.END_PORTAL.defaultBlockState());
                } else {
                    this.setBlock(generatoraccessseed, new BlockPosition(blockposition1), Blocks.AIR.defaultBlockState());
                }
            }
        }

        for (int i = 0; i < 4; ++i) {
            this.setBlock(generatoraccessseed, blockposition.above(i), Blocks.BEDROCK.defaultBlockState());
        }

        BlockPosition blockposition2 = blockposition.above(2);
        Iterator iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator1.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator1.next();

            this.setBlock(generatoraccessseed, blockposition2.relative(enumdirection), (IBlockData) Blocks.WALL_TORCH.defaultBlockState().setValue(BlockTorchWall.FACING, enumdirection));
        }

        return true;
    }
}
