package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EntityRavager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockCrops extends BlockPlant implements IBlockFragilePlantElement {

    public static final int MAX_AGE = 7;
    public static final BlockStateInteger AGE = BlockProperties.AGE_7;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    protected BlockCrops(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(this.getAgeProperty(), 0));
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockCrops.SHAPE_BY_AGE[(Integer) iblockdata.getValue(this.getAgeProperty())];
    }

    @Override
    protected boolean mayPlaceOn(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.is(Blocks.FARMLAND);
    }

    public BlockStateInteger getAgeProperty() {
        return BlockCrops.AGE;
    }

    public int getMaxAge() {
        return 7;
    }

    protected int getAge(IBlockData iblockdata) {
        return (Integer) iblockdata.getValue(this.getAgeProperty());
    }

    public IBlockData getStateForAge(int i) {
        return (IBlockData) this.defaultBlockState().setValue(this.getAgeProperty(), i);
    }

    public boolean isMaxAge(IBlockData iblockdata) {
        return (Integer) iblockdata.getValue(this.getAgeProperty()) >= this.getMaxAge();
    }

    @Override
    public boolean isRandomlyTicking(IBlockData iblockdata) {
        return !this.isMaxAge(iblockdata);
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (worldserver.getRawBrightness(blockposition, 0) >= 9) {
            int i = this.getAge(iblockdata);

            if (i < this.getMaxAge()) {
                float f = getGrowthSpeed(this, worldserver, blockposition);

                if (random.nextInt((int) (25.0F / f) + 1) == 0) {
                    worldserver.setBlock(blockposition, this.getStateForAge(i + 1), 2);
                }
            }
        }

    }

    public void growCrops(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.getAge(iblockdata) + this.getBonemealAgeIncrease(world);
        int j = this.getMaxAge();

        if (i > j) {
            i = j;
        }

        world.setBlock(blockposition, this.getStateForAge(i), 2);
    }

    protected int getBonemealAgeIncrease(World world) {
        return MathHelper.nextInt(world.random, 2, 5);
    }

    protected static float getGrowthSpeed(Block block, IBlockAccess iblockaccess, BlockPosition blockposition) {
        float f = 1.0F;
        BlockPosition blockposition1 = blockposition.below();

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float f1 = 0.0F;
                IBlockData iblockdata = iblockaccess.getBlockState(blockposition1.offset(i, 0, j));

                if (iblockdata.is(Blocks.FARMLAND)) {
                    f1 = 1.0F;
                    if ((Integer) iblockdata.getValue(BlockSoil.MOISTURE) > 0) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPosition blockposition2 = blockposition.north();
        BlockPosition blockposition3 = blockposition.south();
        BlockPosition blockposition4 = blockposition.west();
        BlockPosition blockposition5 = blockposition.east();
        boolean flag = iblockaccess.getBlockState(blockposition4).is(block) || iblockaccess.getBlockState(blockposition5).is(block);
        boolean flag1 = iblockaccess.getBlockState(blockposition2).is(block) || iblockaccess.getBlockState(blockposition3).is(block);

        if (flag && flag1) {
            f /= 2.0F;
        } else {
            boolean flag2 = iblockaccess.getBlockState(blockposition4.north()).is(block) || iblockaccess.getBlockState(blockposition5.north()).is(block) || iblockaccess.getBlockState(blockposition5.south()).is(block) || iblockaccess.getBlockState(blockposition4.south()).is(block);

            if (flag2) {
                f /= 2.0F;
            }
        }

        return f;
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return (iworldreader.getRawBrightness(blockposition, 0) >= 8 || iworldreader.canSeeSky(blockposition)) && super.canSurvive(iblockdata, iworldreader, blockposition);
    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (entity instanceof EntityRavager && world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            world.destroyBlock(blockposition, true, entity);
        }

        super.entityInside(iblockdata, world, blockposition, entity);
    }

    protected IMaterial getBaseSeedId() {
        return Items.WHEAT_SEEDS;
    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(this.getBaseSeedId());
    }

    @Override
    public boolean isValidBonemealTarget(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return !this.isMaxAge(iblockdata);
    }

    @Override
    public boolean isBonemealSuccess(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.growCrops(worldserver, blockposition, iblockdata);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockCrops.AGE);
    }
}
