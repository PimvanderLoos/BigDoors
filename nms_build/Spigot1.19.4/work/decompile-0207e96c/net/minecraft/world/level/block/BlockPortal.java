package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.portal.BlockPortalShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockPortal extends Block {

    public static final BlockStateEnum<EnumDirection.EnumAxis> AXIS = BlockProperties.HORIZONTAL_AXIS;
    protected static final int AABB_OFFSET = 2;
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    public BlockPortal(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockPortal.AXIS, EnumDirection.EnumAxis.X));
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((EnumDirection.EnumAxis) iblockdata.getValue(BlockPortal.AXIS)) {
            case Z:
                return BlockPortal.Z_AXIS_AABB;
            case X:
            default:
                return BlockPortal.X_AXIS_AABB;
        }
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (worldserver.dimensionType().natural() && worldserver.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && randomsource.nextInt(2000) < worldserver.getDifficulty().getId()) {
            while (worldserver.getBlockState(blockposition).is((Block) this)) {
                blockposition = blockposition.below();
            }

            if (worldserver.getBlockState(blockposition).isValidSpawn(worldserver, blockposition, EntityTypes.ZOMBIFIED_PIGLIN)) {
                Entity entity = EntityTypes.ZOMBIFIED_PIGLIN.spawn(worldserver, blockposition.above(), EnumMobSpawn.STRUCTURE);

                if (entity != null) {
                    entity.setPortalCooldown();
                }
            }
        }

    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.getAxis();
        EnumDirection.EnumAxis enumdirection_enumaxis1 = (EnumDirection.EnumAxis) iblockdata.getValue(BlockPortal.AXIS);
        boolean flag = enumdirection_enumaxis1 != enumdirection_enumaxis && enumdirection_enumaxis.isHorizontal();

        return !flag && !iblockdata1.is((Block) this) && !(new BlockPortalShape(generatoraccess, blockposition, enumdirection_enumaxis1)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (entity.canChangeDimensions()) {
            entity.handleInsidePortal(blockposition);
        }

    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        if (randomsource.nextInt(100) == 0) {
            world.playLocalSound((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, randomsource.nextFloat() * 0.4F + 0.8F, false);
        }

        for (int i = 0; i < 4; ++i) {
            double d0 = (double) blockposition.getX() + randomsource.nextDouble();
            double d1 = (double) blockposition.getY() + randomsource.nextDouble();
            double d2 = (double) blockposition.getZ() + randomsource.nextDouble();
            double d3 = ((double) randomsource.nextFloat() - 0.5D) * 0.5D;
            double d4 = ((double) randomsource.nextFloat() - 0.5D) * 0.5D;
            double d5 = ((double) randomsource.nextFloat() - 0.5D) * 0.5D;
            int j = randomsource.nextInt(2) * 2 - 1;

            if (!world.getBlockState(blockposition.west()).is((Block) this) && !world.getBlockState(blockposition.east()).is((Block) this)) {
                d0 = (double) blockposition.getX() + 0.5D + 0.25D * (double) j;
                d3 = (double) (randomsource.nextFloat() * 2.0F * (float) j);
            } else {
                d2 = (double) blockposition.getZ() + 0.5D + 0.25D * (double) j;
                d5 = (double) (randomsource.nextFloat() * 2.0F * (float) j);
            }

            world.addParticle(Particles.PORTAL, d0, d1, d2, d3, d4, d5);
        }

    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.EMPTY;
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch ((EnumDirection.EnumAxis) iblockdata.getValue(BlockPortal.AXIS)) {
                    case Z:
                        return (IBlockData) iblockdata.setValue(BlockPortal.AXIS, EnumDirection.EnumAxis.X);
                    case X:
                        return (IBlockData) iblockdata.setValue(BlockPortal.AXIS, EnumDirection.EnumAxis.Z);
                    default:
                        return iblockdata;
                }
            default:
                return iblockdata;
        }
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockPortal.AXIS);
    }
}
