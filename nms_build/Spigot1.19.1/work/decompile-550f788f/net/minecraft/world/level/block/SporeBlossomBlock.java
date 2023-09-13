package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class SporeBlossomBlock extends Block {

    private static final VoxelShape SHAPE = Block.box(2.0D, 13.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final int ADD_PARTICLE_ATTEMPTS = 14;
    private static final int PARTICLE_XZ_RADIUS = 10;
    private static final int PARTICLE_Y_MAX = 10;

    public SporeBlossomBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return Block.canSupportCenter(iworldreader, blockposition.above(), EnumDirection.DOWN) && !iworldreader.isWaterAt(blockposition);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.UP && !this.canSurvive(iblockdata, generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        double d0 = (double) i + randomsource.nextDouble();
        double d1 = (double) j + 0.7D;
        double d2 = (double) k + randomsource.nextDouble();

        world.addParticle(Particles.FALLING_SPORE_BLOSSOM, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l = 0; l < 14; ++l) {
            blockposition_mutableblockposition.set(i + MathHelper.nextInt(randomsource, -10, 10), j - randomsource.nextInt(10), k + MathHelper.nextInt(randomsource, -10, 10));
            IBlockData iblockdata1 = world.getBlockState(blockposition_mutableblockposition);

            if (!iblockdata1.isCollisionShapeFullBlock(world, blockposition_mutableblockposition)) {
                world.addParticle(Particles.SPORE_BLOSSOM_AIR, (double) blockposition_mutableblockposition.getX() + randomsource.nextDouble(), (double) blockposition_mutableblockposition.getY() + randomsource.nextDouble(), (double) blockposition_mutableblockposition.getZ() + randomsource.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return SporeBlossomBlock.SHAPE;
    }
}
