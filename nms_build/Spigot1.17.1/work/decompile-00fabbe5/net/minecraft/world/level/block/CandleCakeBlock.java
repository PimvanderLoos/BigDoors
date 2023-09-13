package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class CandleCakeBlock extends AbstractCandleBlock {

    public static final BlockStateBoolean LIT = AbstractCandleBlock.LIT;
    protected static final float AABB_OFFSET = 1.0F;
    protected static final VoxelShape CAKE_SHAPE = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D);
    protected static final VoxelShape CANDLE_SHAPE = Block.a(7.0D, 8.0D, 7.0D, 9.0D, 14.0D, 9.0D);
    protected static final VoxelShape SHAPE = VoxelShapes.a(CandleCakeBlock.CAKE_SHAPE, CandleCakeBlock.CANDLE_SHAPE);
    private static final Map<Block, CandleCakeBlock> BY_CANDLE = Maps.newHashMap();
    private static final Iterable<Vec3D> PARTICLE_OFFSETS = ImmutableList.of(new Vec3D(0.5D, 1.0D, 0.5D));

    protected CandleCakeBlock(Block block, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(CandleCakeBlock.LIT, false));
        CandleCakeBlock.BY_CANDLE.put(block, this);
    }

    @Override
    protected Iterable<Vec3D> a(IBlockData iblockdata) {
        return CandleCakeBlock.PARTICLE_OFFSETS;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return CandleCakeBlock.SHAPE;
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.a(Items.FLINT_AND_STEEL) && !itemstack.a(Items.FIRE_CHARGE)) {
            if (a(movingobjectpositionblock) && entityhuman.b(enumhand).isEmpty() && (Boolean) iblockdata.get(CandleCakeBlock.LIT)) {
                a(entityhuman, iblockdata, (GeneratorAccess) world, blockposition);
                return EnumInteractionResult.a(world.isClientSide);
            } else {
                EnumInteractionResult enuminteractionresult = BlockCake.a((GeneratorAccess) world, blockposition, Blocks.CAKE.getBlockData(), entityhuman);

                if (enuminteractionresult.a()) {
                    c(iblockdata, world, blockposition);
                }

                return enuminteractionresult;
            }
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    private static boolean a(MovingObjectPositionBlock movingobjectpositionblock) {
        return movingobjectpositionblock.getPos().y - (double) movingobjectpositionblock.getBlockPosition().getY() > 0.5D;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(CandleCakeBlock.LIT);
    }

    @Override
    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Blocks.CAKE);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getType(blockposition.down()).getMaterial().isBuildable();
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return BlockCake.FULL_CAKE_SIGNAL;
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    public static IBlockData a(Block block) {
        return ((CandleCakeBlock) CandleCakeBlock.BY_CANDLE.get(block)).getBlockData();
    }

    public static boolean g(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.CANDLE_CAKES, (blockbase_blockdata) -> {
            return blockbase_blockdata.b(CandleCakeBlock.LIT) && !(Boolean) iblockdata.get(CandleCakeBlock.LIT);
        });
    }
}
