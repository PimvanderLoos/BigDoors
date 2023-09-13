package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockDragonEgg extends BlockFalling {

    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public BlockDragonEgg(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockDragonEgg.SHAPE;
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        this.teleport(iblockdata, world, blockposition);
        return EnumInteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.teleport(iblockdata, world, blockposition);
    }

    private void teleport(IBlockData iblockdata, World world, BlockPosition blockposition) {
        WorldBorder worldborder = world.getWorldBorder();

        for (int i = 0; i < 1000; ++i) {
            BlockPosition blockposition1 = blockposition.offset(world.random.nextInt(16) - world.random.nextInt(16), world.random.nextInt(8) - world.random.nextInt(8), world.random.nextInt(16) - world.random.nextInt(16));

            if (world.getBlockState(blockposition1).isAir() && worldborder.isWithinBounds(blockposition1)) {
                if (world.isClientSide) {
                    for (int j = 0; j < 128; ++j) {
                        double d0 = world.random.nextDouble();
                        float f = (world.random.nextFloat() - 0.5F) * 0.2F;
                        float f1 = (world.random.nextFloat() - 0.5F) * 0.2F;
                        float f2 = (world.random.nextFloat() - 0.5F) * 0.2F;
                        double d1 = MathHelper.lerp(d0, (double) blockposition1.getX(), (double) blockposition.getX()) + (world.random.nextDouble() - 0.5D) + 0.5D;
                        double d2 = MathHelper.lerp(d0, (double) blockposition1.getY(), (double) blockposition.getY()) + world.random.nextDouble() - 0.5D;
                        double d3 = MathHelper.lerp(d0, (double) blockposition1.getZ(), (double) blockposition.getZ()) + (world.random.nextDouble() - 0.5D) + 0.5D;

                        world.addParticle(Particles.PORTAL, d1, d2, d3, (double) f, (double) f1, (double) f2);
                    }
                } else {
                    world.setBlock(blockposition1, iblockdata, 2);
                    world.removeBlock(blockposition, false);
                }

                return;
            }
        }

    }

    @Override
    protected int getDelayAfterPlace() {
        return 5;
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
