package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyAttachPosition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockButtonAbstract extends BlockAttachable {

    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    private static final int PRESSED_DEPTH = 1;
    private static final int UNPRESSED_DEPTH = 2;
    protected static final int HALF_AABB_HEIGHT = 2;
    protected static final int HALF_AABB_WIDTH = 3;
    protected static final VoxelShape CEILING_AABB_X = Block.box(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
    protected static final VoxelShape CEILING_AABB_Z = Block.box(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
    protected static final VoxelShape FLOOR_AABB_X = Block.box(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
    protected static final VoxelShape FLOOR_AABB_Z = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
    protected static final VoxelShape WEST_AABB = Block.box(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
    protected static final VoxelShape PRESSED_CEILING_AABB_X = Block.box(6.0D, 15.0D, 5.0D, 10.0D, 16.0D, 11.0D);
    protected static final VoxelShape PRESSED_CEILING_AABB_Z = Block.box(5.0D, 15.0D, 6.0D, 11.0D, 16.0D, 10.0D);
    protected static final VoxelShape PRESSED_FLOOR_AABB_X = Block.box(6.0D, 0.0D, 5.0D, 10.0D, 1.0D, 11.0D);
    protected static final VoxelShape PRESSED_FLOOR_AABB_Z = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 1.0D, 10.0D);
    protected static final VoxelShape PRESSED_NORTH_AABB = Block.box(5.0D, 6.0D, 15.0D, 11.0D, 10.0D, 16.0D);
    protected static final VoxelShape PRESSED_SOUTH_AABB = Block.box(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 1.0D);
    protected static final VoxelShape PRESSED_WEST_AABB = Block.box(15.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
    protected static final VoxelShape PRESSED_EAST_AABB = Block.box(0.0D, 6.0D, 5.0D, 1.0D, 10.0D, 11.0D);
    private final BlockSetType type;
    private final int ticksToStayPressed;
    private final boolean arrowsCanPress;

    protected BlockButtonAbstract(BlockBase.Info blockbase_info, BlockSetType blocksettype, int i, boolean flag) {
        super(blockbase_info.sound(blocksettype.soundType()));
        this.type = blocksettype;
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockButtonAbstract.FACING, EnumDirection.NORTH)).setValue(BlockButtonAbstract.POWERED, false)).setValue(BlockButtonAbstract.FACE, BlockPropertyAttachPosition.WALL));
        this.ticksToStayPressed = i;
        this.arrowsCanPress = flag;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockButtonAbstract.FACING);
        boolean flag = (Boolean) iblockdata.getValue(BlockButtonAbstract.POWERED);

        switch ((BlockPropertyAttachPosition) iblockdata.getValue(BlockButtonAbstract.FACE)) {
            case FLOOR:
                if (enumdirection.getAxis() == EnumDirection.EnumAxis.X) {
                    return flag ? BlockButtonAbstract.PRESSED_FLOOR_AABB_X : BlockButtonAbstract.FLOOR_AABB_X;
                }

                return flag ? BlockButtonAbstract.PRESSED_FLOOR_AABB_Z : BlockButtonAbstract.FLOOR_AABB_Z;
            case WALL:
                VoxelShape voxelshape;

                switch (enumdirection) {
                    case EAST:
                        voxelshape = flag ? BlockButtonAbstract.PRESSED_EAST_AABB : BlockButtonAbstract.EAST_AABB;
                        break;
                    case WEST:
                        voxelshape = flag ? BlockButtonAbstract.PRESSED_WEST_AABB : BlockButtonAbstract.WEST_AABB;
                        break;
                    case SOUTH:
                        voxelshape = flag ? BlockButtonAbstract.PRESSED_SOUTH_AABB : BlockButtonAbstract.SOUTH_AABB;
                        break;
                    case NORTH:
                    case UP:
                    case DOWN:
                        voxelshape = flag ? BlockButtonAbstract.PRESSED_NORTH_AABB : BlockButtonAbstract.NORTH_AABB;
                        break;
                    default:
                        throw new IncompatibleClassChangeError();
                }

                return voxelshape;
            case CEILING:
            default:
                return enumdirection.getAxis() == EnumDirection.EnumAxis.X ? (flag ? BlockButtonAbstract.PRESSED_CEILING_AABB_X : BlockButtonAbstract.CEILING_AABB_X) : (flag ? BlockButtonAbstract.PRESSED_CEILING_AABB_Z : BlockButtonAbstract.CEILING_AABB_Z);
        }
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if ((Boolean) iblockdata.getValue(BlockButtonAbstract.POWERED)) {
            return EnumInteractionResult.CONSUME;
        } else {
            this.press(iblockdata, world, blockposition);
            this.playSound(entityhuman, world, blockposition, true);
            world.gameEvent((Entity) entityhuman, GameEvent.BLOCK_ACTIVATE, blockposition);
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        }
    }

    public void press(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockButtonAbstract.POWERED, true), 3);
        this.updateNeighbours(iblockdata, world, blockposition);
        world.scheduleTick(blockposition, (Block) this, this.ticksToStayPressed);
    }

    protected void playSound(@Nullable EntityHuman entityhuman, GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag) {
        generatoraccess.playSound(flag ? entityhuman : null, blockposition, this.getSound(flag), SoundCategory.BLOCKS);
    }

    protected SoundEffect getSound(boolean flag) {
        return flag ? this.type.buttonClickOn() : this.type.buttonClickOff();
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && !iblockdata.is(iblockdata1.getBlock())) {
            if ((Boolean) iblockdata.getValue(BlockButtonAbstract.POWERED)) {
                this.updateNeighbours(iblockdata, world, blockposition);
            }

            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.getValue(BlockButtonAbstract.POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.getValue(BlockButtonAbstract.POWERED) && getConnectedDirection(iblockdata) == enumdirection ? 15 : 0;
    }

    @Override
    public boolean isSignalSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if ((Boolean) iblockdata.getValue(BlockButtonAbstract.POWERED)) {
            this.checkPressed(iblockdata, worldserver, blockposition);
        }
    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide && this.arrowsCanPress && !(Boolean) iblockdata.getValue(BlockButtonAbstract.POWERED)) {
            this.checkPressed(iblockdata, world, blockposition);
        }
    }

    protected void checkPressed(IBlockData iblockdata, World world, BlockPosition blockposition) {
        EntityArrow entityarrow = this.arrowsCanPress ? (EntityArrow) world.getEntitiesOfClass(EntityArrow.class, iblockdata.getShape(world, blockposition).bounds().move(blockposition)).stream().findFirst().orElse((Object) null) : null;
        boolean flag = entityarrow != null;
        boolean flag1 = (Boolean) iblockdata.getValue(BlockButtonAbstract.POWERED);

        if (flag != flag1) {
            world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockButtonAbstract.POWERED, flag), 3);
            this.updateNeighbours(iblockdata, world, blockposition);
            this.playSound((EntityHuman) null, world, blockposition, flag);
            world.gameEvent((Entity) entityarrow, flag ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, blockposition);
        }

        if (flag) {
            world.scheduleTick(new BlockPosition(blockposition), (Block) this, this.ticksToStayPressed);
        }

    }

    private void updateNeighbours(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.updateNeighborsAt(blockposition, this);
        world.updateNeighborsAt(blockposition.relative(getConnectedDirection(iblockdata).getOpposite()), this);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockButtonAbstract.FACING, BlockButtonAbstract.POWERED, BlockButtonAbstract.FACE);
    }
}
