package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.piston.BlockPistonMoving;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockSoil extends Block {

    public static final BlockStateInteger MOISTURE = BlockProperties.MOISTURE;
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    public static final int MAX_MOISTURE = 7;

    protected BlockSoil(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockSoil.MOISTURE, 0));
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == EnumDirection.UP && !iblockdata.canSurvive(generatoraccess, blockposition)) {
            generatoraccess.scheduleTick(blockposition, (Block) this, 1);
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition.above());

        return !iblockdata1.getMaterial().isSolid() || iblockdata1.getBlock() instanceof BlockFenceGate || iblockdata1.getBlock() instanceof BlockPistonMoving;
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return !this.defaultBlockState().canSurvive(blockactioncontext.getLevel(), blockactioncontext.getClickedPos()) ? Blocks.DIRT.defaultBlockState() : super.getStateForPlacement(blockactioncontext);
    }

    @Override
    public boolean useShapeForLightOcclusion(IBlockData iblockdata) {
        return true;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockSoil.SHAPE;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (!iblockdata.canSurvive(worldserver, blockposition)) {
            turnToDirt(iblockdata, worldserver, blockposition);
        }

    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        int i = (Integer) iblockdata.getValue(BlockSoil.MOISTURE);

        if (!isNearWater(worldserver, blockposition) && !worldserver.isRainingAt(blockposition.above())) {
            if (i > 0) {
                worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockSoil.MOISTURE, i - 1), 2);
            } else if (!isUnderCrops(worldserver, blockposition)) {
                turnToDirt(iblockdata, worldserver, blockposition);
            }
        } else if (i < 7) {
            worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockSoil.MOISTURE, 7), 2);
        }

    }

    @Override
    public void fallOn(World world, IBlockData iblockdata, BlockPosition blockposition, Entity entity, float f) {
        if (!world.isClientSide && world.random.nextFloat() < f - 0.5F && entity instanceof EntityLiving && (entity instanceof EntityHuman || world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) && entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F) {
            turnToDirt(iblockdata, world, blockposition);
        }

        super.fallOn(world, iblockdata, blockposition, entity, f);
    }

    public static void turnToDirt(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.setBlockAndUpdate(blockposition, pushEntitiesUp(iblockdata, Blocks.DIRT.defaultBlockState(), world, blockposition));
    }

    private static boolean isUnderCrops(IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block block = iblockaccess.getBlockState(blockposition.above()).getBlock();

        return block instanceof BlockCrops || block instanceof BlockStem || block instanceof BlockStemAttached;
    }

    private static boolean isNearWater(IWorldReader iworldreader, BlockPosition blockposition) {
        Iterator iterator = BlockPosition.betweenClosed(blockposition.offset(-4, 0, -4), blockposition.offset(4, 1, 4)).iterator();

        BlockPosition blockposition1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            blockposition1 = (BlockPosition) iterator.next();
        } while (!iworldreader.getFluidState(blockposition1).is((Tag) TagsFluid.WATER));

        return true;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockSoil.MOISTURE);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
