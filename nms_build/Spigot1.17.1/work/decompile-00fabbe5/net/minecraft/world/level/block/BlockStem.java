package net.minecraft.world.level.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockStem extends BlockPlant implements IBlockFragilePlantElement {

    public static final int MAX_AGE = 7;
    public static final BlockStateInteger AGE = BlockProperties.AGE_7;
    protected static final float AABB_OFFSET = 1.0F;
    protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.a(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
    private final BlockStemmed fruit;
    private final Supplier<Item> seedSupplier;

    protected BlockStem(BlockStemmed blockstemmed, Supplier<Item> supplier, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.fruit = blockstemmed;
        this.seedSupplier = supplier;
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockStem.AGE, 0));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockStem.SHAPE_BY_AGE[(Integer) iblockdata.get(BlockStem.AGE)];
    }

    @Override
    protected boolean d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.a(Blocks.FARMLAND);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (worldserver.getLightLevel(blockposition, 0) >= 9) {
            float f = BlockCrops.a((Block) this, (IBlockAccess) worldserver, blockposition);

            if (random.nextInt((int) (25.0F / f) + 1) == 0) {
                int i = (Integer) iblockdata.get(BlockStem.AGE);

                if (i < 7) {
                    iblockdata = (IBlockData) iblockdata.set(BlockStem.AGE, i + 1);
                    worldserver.setTypeAndData(blockposition, iblockdata, 2);
                } else {
                    EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
                    BlockPosition blockposition1 = blockposition.shift(enumdirection);
                    IBlockData iblockdata1 = worldserver.getType(blockposition1.down());

                    if (worldserver.getType(blockposition1).isAir() && (iblockdata1.a(Blocks.FARMLAND) || iblockdata1.a((Tag) TagsBlock.DIRT))) {
                        worldserver.setTypeUpdate(blockposition1, this.fruit.getBlockData());
                        worldserver.setTypeUpdate(blockposition, (IBlockData) this.fruit.d().getBlockData().set(BlockFacingHorizontal.FACING, enumdirection));
                    }
                }
            }

        }
    }

    @Override
    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack((IMaterial) this.seedSupplier.get());
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return (Integer) iblockdata.get(BlockStem.AGE) != 7;
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        int i = Math.min(7, (Integer) iblockdata.get(BlockStem.AGE) + MathHelper.nextInt(worldserver.random, 2, 5));
        IBlockData iblockdata1 = (IBlockData) iblockdata.set(BlockStem.AGE, i);

        worldserver.setTypeAndData(blockposition, iblockdata1, 2);
        if (i == 7) {
            iblockdata1.b(worldserver, blockposition, worldserver.random);
        }

    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockStem.AGE);
    }

    public BlockStemmed c() {
        return this.fruit;
    }
}
