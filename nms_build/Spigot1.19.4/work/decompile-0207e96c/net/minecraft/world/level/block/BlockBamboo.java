package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemSword;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyBambooSize;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockBamboo extends Block implements IBlockFragilePlantElement {

    protected static final float SMALL_LEAVES_AABB_OFFSET = 3.0F;
    protected static final float LARGE_LEAVES_AABB_OFFSET = 5.0F;
    protected static final float COLLISION_AABB_OFFSET = 1.5F;
    protected static final VoxelShape SMALL_SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    protected static final VoxelShape LARGE_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    protected static final VoxelShape COLLISION_SHAPE = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
    public static final BlockStateInteger AGE = BlockProperties.AGE_1;
    public static final BlockStateEnum<BlockPropertyBambooSize> LEAVES = BlockProperties.BAMBOO_LEAVES;
    public static final BlockStateInteger STAGE = BlockProperties.STAGE;
    public static final int MAX_HEIGHT = 16;
    public static final int STAGE_GROWING = 0;
    public static final int STAGE_DONE_GROWING = 1;
    public static final int AGE_THIN_BAMBOO = 0;
    public static final int AGE_THICK_BAMBOO = 1;

    public BlockBamboo(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockBamboo.AGE, 0)).setValue(BlockBamboo.LEAVES, BlockPropertyBambooSize.NONE)).setValue(BlockBamboo.STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockBamboo.AGE, BlockBamboo.LEAVES, BlockBamboo.STAGE);
    }

    @Override
    public boolean propagatesSkylightDown(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        VoxelShape voxelshape = iblockdata.getValue(BlockBamboo.LEAVES) == BlockPropertyBambooSize.LARGE ? BlockBamboo.LARGE_SHAPE : BlockBamboo.SMALL_SHAPE;
        Vec3D vec3d = iblockdata.getOffset(iblockaccess, blockposition);

        return voxelshape.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        Vec3D vec3d = iblockdata.getOffset(iblockaccess, blockposition);

        return BlockBamboo.COLLISION_SHAPE.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public boolean isCollisionShapeFullBlock(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());

        if (!fluid.isEmpty()) {
            return null;
        } else {
            IBlockData iblockdata = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos().below());

            if (iblockdata.is(TagsBlock.BAMBOO_PLANTABLE_ON)) {
                if (iblockdata.is(Blocks.BAMBOO_SAPLING)) {
                    return (IBlockData) this.defaultBlockState().setValue(BlockBamboo.AGE, 0);
                } else if (iblockdata.is(Blocks.BAMBOO)) {
                    int i = (Integer) iblockdata.getValue(BlockBamboo.AGE) > 0 ? 1 : 0;

                    return (IBlockData) this.defaultBlockState().setValue(BlockBamboo.AGE, i);
                } else {
                    IBlockData iblockdata1 = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos().above());

                    return iblockdata1.is(Blocks.BAMBOO) ? (IBlockData) this.defaultBlockState().setValue(BlockBamboo.AGE, (Integer) iblockdata1.getValue(BlockBamboo.AGE)) : Blocks.BAMBOO_SAPLING.defaultBlockState();
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (!iblockdata.canSurvive(worldserver, blockposition)) {
            worldserver.destroyBlock(blockposition, true);
        }

    }

    @Override
    public boolean isRandomlyTicking(IBlockData iblockdata) {
        return (Integer) iblockdata.getValue(BlockBamboo.STAGE) == 0;
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if ((Integer) iblockdata.getValue(BlockBamboo.STAGE) == 0) {
            if (randomsource.nextInt(3) == 0 && worldserver.isEmptyBlock(blockposition.above()) && worldserver.getRawBrightness(blockposition.above(), 0) >= 9) {
                int i = this.getHeightBelowUpToMax(worldserver, blockposition) + 1;

                if (i < 16) {
                    this.growBamboo(iblockdata, worldserver, blockposition, randomsource, i);
                }
            }

        }
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getBlockState(blockposition.below()).is(TagsBlock.BAMBOO_PLANTABLE_ON);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canSurvive(generatoraccess, blockposition)) {
            generatoraccess.scheduleTick(blockposition, (Block) this, 1);
        }

        if (enumdirection == EnumDirection.UP && iblockdata1.is(Blocks.BAMBOO) && (Integer) iblockdata1.getValue(BlockBamboo.AGE) > (Integer) iblockdata.getValue(BlockBamboo.AGE)) {
            generatoraccess.setBlock(blockposition, (IBlockData) iblockdata.cycle(BlockBamboo.AGE), 2);
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean isValidBonemealTarget(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        int i = this.getHeightAboveUpToMax(iworldreader, blockposition);
        int j = this.getHeightBelowUpToMax(iworldreader, blockposition);

        return i + j + 1 < 16 && (Integer) iworldreader.getBlockState(blockposition.above(i)).getValue(BlockBamboo.STAGE) != 1;
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.getHeightAboveUpToMax(worldserver, blockposition);
        int j = this.getHeightBelowUpToMax(worldserver, blockposition);
        int k = i + j + 1;
        int l = 1 + randomsource.nextInt(2);

        for (int i1 = 0; i1 < l; ++i1) {
            BlockPosition blockposition1 = blockposition.above(i);
            IBlockData iblockdata1 = worldserver.getBlockState(blockposition1);

            if (k >= 16 || (Integer) iblockdata1.getValue(BlockBamboo.STAGE) == 1 || !worldserver.isEmptyBlock(blockposition1.above())) {
                return;
            }

            this.growBamboo(iblockdata1, worldserver, blockposition1, randomsource, k);
            ++i;
            ++k;
        }

    }

    @Override
    public float getDestroyProgress(IBlockData iblockdata, EntityHuman entityhuman, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return entityhuman.getMainHandItem().getItem() instanceof ItemSword ? 1.0F : super.getDestroyProgress(iblockdata, entityhuman, iblockaccess, blockposition);
    }

    protected void growBamboo(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource, int i) {
        IBlockData iblockdata1 = world.getBlockState(blockposition.below());
        BlockPosition blockposition1 = blockposition.below(2);
        IBlockData iblockdata2 = world.getBlockState(blockposition1);
        BlockPropertyBambooSize blockpropertybamboosize = BlockPropertyBambooSize.NONE;

        if (i >= 1) {
            if (iblockdata1.is(Blocks.BAMBOO) && iblockdata1.getValue(BlockBamboo.LEAVES) != BlockPropertyBambooSize.NONE) {
                if (iblockdata1.is(Blocks.BAMBOO) && iblockdata1.getValue(BlockBamboo.LEAVES) != BlockPropertyBambooSize.NONE) {
                    blockpropertybamboosize = BlockPropertyBambooSize.LARGE;
                    if (iblockdata2.is(Blocks.BAMBOO)) {
                        world.setBlock(blockposition.below(), (IBlockData) iblockdata1.setValue(BlockBamboo.LEAVES, BlockPropertyBambooSize.SMALL), 3);
                        world.setBlock(blockposition1, (IBlockData) iblockdata2.setValue(BlockBamboo.LEAVES, BlockPropertyBambooSize.NONE), 3);
                    }
                }
            } else {
                blockpropertybamboosize = BlockPropertyBambooSize.SMALL;
            }
        }

        int j = (Integer) iblockdata.getValue(BlockBamboo.AGE) != 1 && !iblockdata2.is(Blocks.BAMBOO) ? 0 : 1;
        int k = (i < 11 || randomsource.nextFloat() >= 0.25F) && i != 15 ? 0 : 1;

        world.setBlock(blockposition.above(), (IBlockData) ((IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockBamboo.AGE, j)).setValue(BlockBamboo.LEAVES, blockpropertybamboosize)).setValue(BlockBamboo.STAGE, k), 3);
    }

    protected int getHeightAboveUpToMax(IBlockAccess iblockaccess, BlockPosition blockposition) {
        int i;

        for (i = 0; i < 16 && iblockaccess.getBlockState(blockposition.above(i + 1)).is(Blocks.BAMBOO); ++i) {
            ;
        }

        return i;
    }

    protected int getHeightBelowUpToMax(IBlockAccess iblockaccess, BlockPosition blockposition) {
        int i;

        for (i = 0; i < 16 && iblockaccess.getBlockState(blockposition.below(i + 1)).is(Blocks.BAMBOO); ++i) {
            ;
        }

        return i;
    }
}
