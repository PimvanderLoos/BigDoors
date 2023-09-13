package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyAttachPosition;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockLever extends BlockAttachable {

    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    protected static final int DEPTH = 6;
    protected static final int WIDTH = 6;
    protected static final int HEIGHT = 8;
    protected static final VoxelShape NORTH_AABB = Block.a(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.a(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
    protected static final VoxelShape WEST_AABB = Block.a(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
    protected static final VoxelShape EAST_AABB = Block.a(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
    protected static final VoxelShape UP_AABB_Z = Block.a(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
    protected static final VoxelShape UP_AABB_X = Block.a(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
    protected static final VoxelShape DOWN_AABB_Z = Block.a(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
    protected static final VoxelShape DOWN_AABB_X = Block.a(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);

    protected BlockLever(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockLever.FACING, EnumDirection.NORTH)).set(BlockLever.POWERED, false)).set(BlockLever.FACE, BlockPropertyAttachPosition.WALL));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((BlockPropertyAttachPosition) iblockdata.get(BlockLever.FACE)) {
            case FLOOR:
                switch (((EnumDirection) iblockdata.get(BlockLever.FACING)).n()) {
                    case X:
                        return BlockLever.UP_AABB_X;
                    case Z:
                    default:
                        return BlockLever.UP_AABB_Z;
                }
            case WALL:
                switch ((EnumDirection) iblockdata.get(BlockLever.FACING)) {
                    case EAST:
                        return BlockLever.EAST_AABB;
                    case WEST:
                        return BlockLever.WEST_AABB;
                    case SOUTH:
                        return BlockLever.SOUTH_AABB;
                    case NORTH:
                    default:
                        return BlockLever.NORTH_AABB;
                }
            case CEILING:
            default:
                switch (((EnumDirection) iblockdata.get(BlockLever.FACING)).n()) {
                    case X:
                        return BlockLever.DOWN_AABB_X;
                    case Z:
                    default:
                        return BlockLever.DOWN_AABB_Z;
                }
        }
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        IBlockData iblockdata1;

        if (world.isClientSide) {
            iblockdata1 = (IBlockData) iblockdata.a((IBlockState) BlockLever.POWERED);
            if ((Boolean) iblockdata1.get(BlockLever.POWERED)) {
                a(iblockdata1, world, blockposition, 1.0F);
            }

            return EnumInteractionResult.SUCCESS;
        } else {
            iblockdata1 = this.d(iblockdata, world, blockposition);
            float f = (Boolean) iblockdata1.get(BlockLever.POWERED) ? 0.6F : 0.5F;

            world.playSound((EntityHuman) null, blockposition, SoundEffects.LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
            world.a((Entity) entityhuman, (Boolean) iblockdata1.get(BlockLever.POWERED) ? GameEvent.BLOCK_SWITCH : GameEvent.BLOCK_UNSWITCH, blockposition);
            return EnumInteractionResult.CONSUME;
        }
    }

    public IBlockData d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        iblockdata = (IBlockData) iblockdata.a((IBlockState) BlockLever.POWERED);
        world.setTypeAndData(blockposition, iblockdata, 3);
        this.e(iblockdata, world, blockposition);
        return iblockdata;
    }

    private static void a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, float f) {
        EnumDirection enumdirection = ((EnumDirection) iblockdata.get(BlockLever.FACING)).opposite();
        EnumDirection enumdirection1 = h(iblockdata).opposite();
        double d0 = (double) blockposition.getX() + 0.5D + 0.1D * (double) enumdirection.getAdjacentX() + 0.2D * (double) enumdirection1.getAdjacentX();
        double d1 = (double) blockposition.getY() + 0.5D + 0.1D * (double) enumdirection.getAdjacentY() + 0.2D * (double) enumdirection1.getAdjacentY();
        double d2 = (double) blockposition.getZ() + 0.5D + 0.1D * (double) enumdirection.getAdjacentZ() + 0.2D * (double) enumdirection1.getAdjacentZ();

        generatoraccess.addParticle(new ParticleParamRedstone(ParticleParamRedstone.REDSTONE_PARTICLE_COLOR, f), d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(BlockLever.POWERED) && random.nextFloat() < 0.25F) {
            a(iblockdata, world, blockposition, 0.5F);
        }

    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && !iblockdata.a(iblockdata1.getBlock())) {
            if ((Boolean) iblockdata.get(BlockLever.POWERED)) {
                this.e(iblockdata, world, blockposition);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockLever.POWERED) ? 15 : 0;
    }

    @Override
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockLever.POWERED) && h(iblockdata) == enumdirection ? 15 : 0;
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    private void e(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.applyPhysics(blockposition, this);
        world.applyPhysics(blockposition.shift(h(iblockdata).opposite()), this);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockLever.FACE, BlockLever.FACING, BlockLever.POWERED);
    }
}
