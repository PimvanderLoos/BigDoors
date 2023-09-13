package net.minecraft.world.level.block.piston;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyPistonType;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class TileEntityPiston extends TileEntity {

    private static final int TICKS_TO_EXTEND = 2;
    private static final double PUSH_OFFSET = 0.01D;
    public static final double TICK_MOVEMENT = 0.51D;
    private IBlockData movedState;
    private EnumDirection direction;
    private boolean extending;
    private boolean isSourcePiston;
    private static final ThreadLocal<EnumDirection> NOCLIP = ThreadLocal.withInitial(() -> {
        return null;
    });
    private float progress;
    private float progressO;
    private long lastTicked;
    private int deathTicks;

    public TileEntityPiston(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.PISTON, blockposition, iblockdata);
        this.movedState = Blocks.AIR.defaultBlockState();
    }

    public TileEntityPiston(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection, boolean flag, boolean flag1) {
        this(blockposition, iblockdata);
        this.movedState = iblockdata1;
        this.direction = enumdirection;
        this.extending = flag;
        this.isSourcePiston = flag1;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public boolean isExtending() {
        return this.extending;
    }

    public EnumDirection getDirection() {
        return this.direction;
    }

    public boolean isSourcePiston() {
        return this.isSourcePiston;
    }

    public float getProgress(float f) {
        if (f > 1.0F) {
            f = 1.0F;
        }

        return MathHelper.lerp(f, this.progressO, this.progress);
    }

    public float getXOff(float f) {
        return (float) this.direction.getStepX() * this.getExtendedProgress(this.getProgress(f));
    }

    public float getYOff(float f) {
        return (float) this.direction.getStepY() * this.getExtendedProgress(this.getProgress(f));
    }

    public float getZOff(float f) {
        return (float) this.direction.getStepZ() * this.getExtendedProgress(this.getProgress(f));
    }

    private float getExtendedProgress(float f) {
        return this.extending ? f - 1.0F : 1.0F - f;
    }

    private IBlockData getCollisionRelatedBlockState() {
        return !this.isExtending() && this.isSourcePiston() && this.movedState.getBlock() instanceof BlockPiston ? (IBlockData) ((IBlockData) ((IBlockData) Blocks.PISTON_HEAD.defaultBlockState().setValue(BlockPistonExtension.SHORT, this.progress > 0.25F)).setValue(BlockPistonExtension.TYPE, this.movedState.is(Blocks.STICKY_PISTON) ? BlockPropertyPistonType.STICKY : BlockPropertyPistonType.DEFAULT)).setValue(BlockPistonExtension.FACING, (EnumDirection) this.movedState.getValue(BlockPiston.FACING)) : this.movedState;
    }

    private static void moveCollidedEntities(World world, BlockPosition blockposition, float f, TileEntityPiston tileentitypiston) {
        EnumDirection enumdirection = tileentitypiston.getMovementDirection();
        double d0 = (double) (f - tileentitypiston.progress);
        VoxelShape voxelshape = tileentitypiston.getCollisionRelatedBlockState().getCollisionShape(world, blockposition);

        if (!voxelshape.isEmpty()) {
            AxisAlignedBB axisalignedbb = moveByPositionAndProgress(blockposition, voxelshape.bounds(), tileentitypiston);
            List<Entity> list = world.getEntities((Entity) null, PistonUtil.getMovementArea(axisalignedbb, enumdirection, d0).minmax(axisalignedbb));

            if (!list.isEmpty()) {
                List<AxisAlignedBB> list1 = voxelshape.toAabbs();
                boolean flag = tileentitypiston.movedState.is(Blocks.SLIME_BLOCK);
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    if (entity.getPistonPushReaction() != EnumPistonReaction.IGNORE) {
                        if (flag) {
                            if (entity instanceof EntityPlayer) {
                                continue;
                            }

                            Vec3D vec3d = entity.getDeltaMovement();
                            double d1 = vec3d.x;
                            double d2 = vec3d.y;
                            double d3 = vec3d.z;

                            switch (enumdirection.getAxis()) {
                                case X:
                                    d1 = (double) enumdirection.getStepX();
                                    break;
                                case Y:
                                    d2 = (double) enumdirection.getStepY();
                                    break;
                                case Z:
                                    d3 = (double) enumdirection.getStepZ();
                            }

                            entity.setDeltaMovement(d1, d2, d3);
                        }

                        double d4 = 0.0D;
                        Iterator iterator1 = list1.iterator();

                        while (iterator1.hasNext()) {
                            AxisAlignedBB axisalignedbb1 = (AxisAlignedBB) iterator1.next();
                            AxisAlignedBB axisalignedbb2 = PistonUtil.getMovementArea(moveByPositionAndProgress(blockposition, axisalignedbb1, tileentitypiston), enumdirection, d0);
                            AxisAlignedBB axisalignedbb3 = entity.getBoundingBox();

                            if (axisalignedbb2.intersects(axisalignedbb3)) {
                                d4 = Math.max(d4, getMovement(axisalignedbb2, enumdirection, axisalignedbb3));
                                if (d4 >= d0) {
                                    break;
                                }
                            }
                        }

                        if (d4 > 0.0D) {
                            d4 = Math.min(d4, d0) + 0.01D;
                            moveEntityByPiston(enumdirection, entity, d4, enumdirection);
                            if (!tileentitypiston.extending && tileentitypiston.isSourcePiston) {
                                fixEntityWithinPistonBase(blockposition, entity, enumdirection, d0);
                            }
                        }
                    }
                }

            }
        }
    }

    private static void moveEntityByPiston(EnumDirection enumdirection, Entity entity, double d0, EnumDirection enumdirection1) {
        TileEntityPiston.NOCLIP.set(enumdirection);
        entity.move(EnumMoveType.PISTON, new Vec3D(d0 * (double) enumdirection1.getStepX(), d0 * (double) enumdirection1.getStepY(), d0 * (double) enumdirection1.getStepZ()));
        TileEntityPiston.NOCLIP.set((Object) null);
    }

    private static void moveStuckEntities(World world, BlockPosition blockposition, float f, TileEntityPiston tileentitypiston) {
        if (tileentitypiston.isStickyForEntities()) {
            EnumDirection enumdirection = tileentitypiston.getMovementDirection();

            if (enumdirection.getAxis().isHorizontal()) {
                double d0 = tileentitypiston.movedState.getCollisionShape(world, blockposition).max(EnumDirection.EnumAxis.Y);
                AxisAlignedBB axisalignedbb = moveByPositionAndProgress(blockposition, new AxisAlignedBB(0.0D, d0, 0.0D, 1.0D, 1.5000000999999998D, 1.0D), tileentitypiston);
                double d1 = (double) (f - tileentitypiston.progress);
                List<Entity> list = world.getEntities((Entity) null, axisalignedbb, (entity) -> {
                    return matchesStickyCritera(axisalignedbb, entity);
                });
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    moveEntityByPiston(enumdirection, entity, d1, enumdirection);
                }

            }
        }
    }

    private static boolean matchesStickyCritera(AxisAlignedBB axisalignedbb, Entity entity) {
        return entity.getPistonPushReaction() == EnumPistonReaction.NORMAL && entity.isOnGround() && entity.getX() >= axisalignedbb.minX && entity.getX() <= axisalignedbb.maxX && entity.getZ() >= axisalignedbb.minZ && entity.getZ() <= axisalignedbb.maxZ;
    }

    private boolean isStickyForEntities() {
        return this.movedState.is(Blocks.HONEY_BLOCK);
    }

    public EnumDirection getMovementDirection() {
        return this.extending ? this.direction : this.direction.getOpposite();
    }

    private static double getMovement(AxisAlignedBB axisalignedbb, EnumDirection enumdirection, AxisAlignedBB axisalignedbb1) {
        switch (enumdirection) {
            case EAST:
                return axisalignedbb.maxX - axisalignedbb1.minX;
            case WEST:
                return axisalignedbb1.maxX - axisalignedbb.minX;
            case UP:
            default:
                return axisalignedbb.maxY - axisalignedbb1.minY;
            case DOWN:
                return axisalignedbb1.maxY - axisalignedbb.minY;
            case SOUTH:
                return axisalignedbb.maxZ - axisalignedbb1.minZ;
            case NORTH:
                return axisalignedbb1.maxZ - axisalignedbb.minZ;
        }
    }

    private static AxisAlignedBB moveByPositionAndProgress(BlockPosition blockposition, AxisAlignedBB axisalignedbb, TileEntityPiston tileentitypiston) {
        double d0 = (double) tileentitypiston.getExtendedProgress(tileentitypiston.progress);

        return axisalignedbb.move((double) blockposition.getX() + d0 * (double) tileentitypiston.direction.getStepX(), (double) blockposition.getY() + d0 * (double) tileentitypiston.direction.getStepY(), (double) blockposition.getZ() + d0 * (double) tileentitypiston.direction.getStepZ());
    }

    private static void fixEntityWithinPistonBase(BlockPosition blockposition, Entity entity, EnumDirection enumdirection, double d0) {
        AxisAlignedBB axisalignedbb = entity.getBoundingBox();
        AxisAlignedBB axisalignedbb1 = VoxelShapes.block().bounds().move(blockposition);

        if (axisalignedbb.intersects(axisalignedbb1)) {
            EnumDirection enumdirection1 = enumdirection.getOpposite();
            double d1 = getMovement(axisalignedbb1, enumdirection1, axisalignedbb) + 0.01D;
            double d2 = getMovement(axisalignedbb1, enumdirection1, axisalignedbb.intersect(axisalignedbb1)) + 0.01D;

            if (Math.abs(d1 - d2) < 0.01D) {
                d1 = Math.min(d1, d0) + 0.01D;
                moveEntityByPiston(enumdirection, entity, d1, enumdirection1);
            }
        }

    }

    public IBlockData getMovedState() {
        return this.movedState;
    }

    public void finalTick() {
        if (this.level != null && (this.progressO < 1.0F || this.level.isClientSide)) {
            this.progress = 1.0F;
            this.progressO = this.progress;
            this.level.removeBlockEntity(this.worldPosition);
            this.setRemoved();
            if (this.level.getBlockState(this.worldPosition).is(Blocks.MOVING_PISTON)) {
                IBlockData iblockdata;

                if (this.isSourcePiston) {
                    iblockdata = Blocks.AIR.defaultBlockState();
                } else {
                    iblockdata = Block.updateFromNeighbourShapes(this.movedState, this.level, this.worldPosition);
                }

                this.level.setBlock(this.worldPosition, iblockdata, 3);
                this.level.neighborChanged(this.worldPosition, iblockdata.getBlock(), this.worldPosition);
            }
        }

    }

    public static void tick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityPiston tileentitypiston) {
        tileentitypiston.lastTicked = world.getGameTime();
        tileentitypiston.progressO = tileentitypiston.progress;
        if (tileentitypiston.progressO >= 1.0F) {
            if (world.isClientSide && tileentitypiston.deathTicks < 5) {
                ++tileentitypiston.deathTicks;
            } else {
                world.removeBlockEntity(blockposition);
                tileentitypiston.setRemoved();
                if (world.getBlockState(blockposition).is(Blocks.MOVING_PISTON)) {
                    IBlockData iblockdata1 = Block.updateFromNeighbourShapes(tileentitypiston.movedState, world, blockposition);

                    if (iblockdata1.isAir()) {
                        world.setBlock(blockposition, tileentitypiston.movedState, 84);
                        Block.updateOrDestroy(tileentitypiston.movedState, iblockdata1, world, blockposition, 3);
                    } else {
                        if (iblockdata1.hasProperty(BlockProperties.WATERLOGGED) && (Boolean) iblockdata1.getValue(BlockProperties.WATERLOGGED)) {
                            iblockdata1 = (IBlockData) iblockdata1.setValue(BlockProperties.WATERLOGGED, false);
                        }

                        world.setBlock(blockposition, iblockdata1, 67);
                        world.neighborChanged(blockposition, iblockdata1.getBlock(), blockposition);
                    }
                }

            }
        } else {
            float f = tileentitypiston.progress + 0.5F;

            moveCollidedEntities(world, blockposition, f, tileentitypiston);
            moveStuckEntities(world, blockposition, f, tileentitypiston);
            tileentitypiston.progress = f;
            if (tileentitypiston.progress >= 1.0F) {
                tileentitypiston.progress = 1.0F;
            }

        }
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.movedState = GameProfileSerializer.readBlockState(nbttagcompound.getCompound("blockState"));
        this.direction = EnumDirection.from3DDataValue(nbttagcompound.getInt("facing"));
        this.progress = nbttagcompound.getFloat("progress");
        this.progressO = this.progress;
        this.extending = nbttagcompound.getBoolean("extending");
        this.isSourcePiston = nbttagcompound.getBoolean("source");
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.put("blockState", GameProfileSerializer.writeBlockState(this.movedState));
        nbttagcompound.putInt("facing", this.direction.get3DDataValue());
        nbttagcompound.putFloat("progress", this.progressO);
        nbttagcompound.putBoolean("extending", this.extending);
        nbttagcompound.putBoolean("source", this.isSourcePiston);
    }

    public VoxelShape getCollisionShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        VoxelShape voxelshape;

        if (!this.extending && this.isSourcePiston && this.movedState.getBlock() instanceof BlockPiston) {
            voxelshape = ((IBlockData) this.movedState.setValue(BlockPiston.EXTENDED, true)).getCollisionShape(iblockaccess, blockposition);
        } else {
            voxelshape = VoxelShapes.empty();
        }

        EnumDirection enumdirection = (EnumDirection) TileEntityPiston.NOCLIP.get();

        if ((double) this.progress < 1.0D && enumdirection == this.getMovementDirection()) {
            return voxelshape;
        } else {
            IBlockData iblockdata;

            if (this.isSourcePiston()) {
                iblockdata = (IBlockData) ((IBlockData) Blocks.PISTON_HEAD.defaultBlockState().setValue(BlockPistonExtension.FACING, this.direction)).setValue(BlockPistonExtension.SHORT, this.extending != 1.0F - this.progress < 0.25F);
            } else {
                iblockdata = this.movedState;
            }

            float f = this.getExtendedProgress(this.progress);
            double d0 = (double) ((float) this.direction.getStepX() * f);
            double d1 = (double) ((float) this.direction.getStepY() * f);
            double d2 = (double) ((float) this.direction.getStepZ() * f);

            return VoxelShapes.or(voxelshape, iblockdata.getCollisionShape(iblockaccess, blockposition).move(d0, d1, d2));
        }
    }

    public long getLastTicked() {
        return this.lastTicked;
    }
}
