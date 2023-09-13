package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockSweetBerryBush extends BlockPlant implements IBlockFragilePlantElement {

    private static final float HURT_SPEED_THRESHOLD = 0.003F;
    public static final int MAX_AGE = 3;
    public static final BlockStateInteger AGE = BlockProperties.AGE_3;
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public BlockSweetBerryBush(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockSweetBerryBush.AGE, 0));
    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.SWEET_BERRIES);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (Integer) iblockdata.getValue(BlockSweetBerryBush.AGE) == 0 ? BlockSweetBerryBush.SAPLING_SHAPE : ((Integer) iblockdata.getValue(BlockSweetBerryBush.AGE) < 3 ? BlockSweetBerryBush.MID_GROWTH_SHAPE : super.getShape(iblockdata, iblockaccess, blockposition, voxelshapecollision));
    }

    @Override
    public boolean isRandomlyTicking(IBlockData iblockdata) {
        return (Integer) iblockdata.getValue(BlockSweetBerryBush.AGE) < 3;
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        int i = (Integer) iblockdata.getValue(BlockSweetBerryBush.AGE);

        if (i < 3 && randomsource.nextInt(5) == 0 && worldserver.getRawBrightness(blockposition.above(), 0) >= 9) {
            IBlockData iblockdata1 = (IBlockData) iblockdata.setValue(BlockSweetBerryBush.AGE, i + 1);

            worldserver.setBlock(blockposition, iblockdata1, 2);
            worldserver.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(iblockdata1));
        }

    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (entity instanceof EntityLiving && entity.getType() != EntityTypes.FOX && entity.getType() != EntityTypes.BEE) {
            entity.makeStuckInBlock(iblockdata, new Vec3D(0.800000011920929D, 0.75D, 0.800000011920929D));
            if (!world.isClientSide && (Integer) iblockdata.getValue(BlockSweetBerryBush.AGE) > 0 && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
                double d0 = Math.abs(entity.getX() - entity.xOld);
                double d1 = Math.abs(entity.getZ() - entity.zOld);

                if (d0 >= 0.003000000026077032D || d1 >= 0.003000000026077032D) {
                    entity.hurt(world.damageSources().sweetBerryBush(), 1.0F);
                }
            }

        }
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        int i = (Integer) iblockdata.getValue(BlockSweetBerryBush.AGE);
        boolean flag = i == 3;

        if (!flag && entityhuman.getItemInHand(enumhand).is(Items.BONE_MEAL)) {
            return EnumInteractionResult.PASS;
        } else if (i > 1) {
            int j = 1 + world.random.nextInt(2);

            popResource(world, blockposition, new ItemStack(Items.SWEET_BERRIES, j + (flag ? 1 : 0)));
            world.playSound((EntityHuman) null, blockposition, SoundEffects.SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            IBlockData iblockdata1 = (IBlockData) iblockdata.setValue(BlockSweetBerryBush.AGE, 1);

            world.setBlock(blockposition, iblockdata1, 2);
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(entityhuman, iblockdata1));
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return super.use(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        }
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockSweetBerryBush.AGE);
    }

    @Override
    public boolean isValidBonemealTarget(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return (Integer) iblockdata.getValue(BlockSweetBerryBush.AGE) < 3;
    }

    @Override
    public boolean isBonemealSuccess(World world, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void performBonemeal(WorldServer worldserver, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        int i = Math.min(3, (Integer) iblockdata.getValue(BlockSweetBerryBush.AGE) + 1);

        worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockSweetBerryBush.AGE, i), 2);
    }
}
