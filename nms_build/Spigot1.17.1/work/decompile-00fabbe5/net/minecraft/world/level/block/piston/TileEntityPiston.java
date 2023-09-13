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
import net.minecraft.world.level.GeneratorAccess;
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
    }

    public TileEntityPiston(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection, boolean flag, boolean flag1) {
        this(blockposition, iblockdata);
        this.movedState = iblockdata1;
        this.direction = enumdirection;
        this.extending = flag;
        this.isSourcePiston = flag1;
    }

    @Override
    public NBTTagCompound Z_() {
        return this.save(new NBTTagCompound());
    }

    public boolean d() {
        return this.extending;
    }

    public EnumDirection f() {
        return this.direction;
    }

    public boolean g() {
        return this.isSourcePiston;
    }

    public float a(float f) {
        if (f > 1.0F) {
            f = 1.0F;
        }

        return MathHelper.h(f, this.progressO, this.progress);
    }

    public float b(float f) {
        return (float) this.direction.getAdjacentX() * this.e(this.a(f));
    }

    public float c(float f) {
        return (float) this.direction.getAdjacentY() * this.e(this.a(f));
    }

    public float d(float f) {
        return (float) this.direction.getAdjacentZ() * this.e(this.a(f));
    }

    private float e(float f) {
        return this.extending ? f - 1.0F : 1.0F - f;
    }

    private IBlockData t() {
        return !this.d() && this.g() && this.movedState.getBlock() instanceof BlockPiston ? (IBlockData) ((IBlockData) ((IBlockData) Blocks.PISTON_HEAD.getBlockData().set(BlockPistonExtension.SHORT, this.progress > 0.25F)).set(BlockPistonExtension.TYPE, this.movedState.a(Blocks.STICKY_PISTON) ? BlockPropertyPistonType.STICKY : BlockPropertyPistonType.DEFAULT)).set(BlockPistonExtension.FACING, (EnumDirection) this.movedState.get(BlockPiston.FACING)) : this.movedState;
    }

    private static void a(World world, BlockPosition blockposition, float f, TileEntityPiston tileentitypiston) {
        EnumDirection enumdirection = tileentitypiston.h();
        double d0 = (double) (f - tileentitypiston.progress);
        VoxelShape voxelshape = tileentitypiston.t().getCollisionShape(world, blockposition);

        if (!voxelshape.isEmpty()) {
            AxisAlignedBB axisalignedbb = a(blockposition, voxelshape.getBoundingBox(), tileentitypiston);
            List<Entity> list = world.getEntities((Entity) null, PistonUtil.a(axisalignedbb, enumdirection, d0).b(axisalignedbb));

            if (!list.isEmpty()) {
                List<AxisAlignedBB> list1 = voxelshape.toList();
                boolean flag = tileentitypiston.movedState.a(Blocks.SLIME_BLOCK);
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    if (entity.getPushReaction() != EnumPistonReaction.IGNORE) {
                        if (flag) {
                            if (entity instanceof EntityPlayer) {
                                continue;
                            }

                            Vec3D vec3d = entity.getMot();
                            double d1 = vec3d.x;
                            double d2 = vec3d.y;
                            double d3 = vec3d.z;

                            switch (enumdirection.n()) {
                                case X:
                                    d1 = (double) enumdirection.getAdjacentX();
                                    break;
                                case Y:
                                    d2 = (double) enumdirection.getAdjacentY();
                                    break;
                                case Z:
                                    d3 = (double) enumdirection.getAdjacentZ();
                            }

                            entity.setMot(d1, d2, d3);
                        }

                        double d4 = 0.0D;
                        Iterator iterator1 = list1.iterator();

                        while (iterator1.hasNext()) {
                            AxisAlignedBB axisalignedbb1 = (AxisAlignedBB) iterator1.next();
                            AxisAlignedBB axisalignedbb2 = PistonUtil.a(a(blockposition, axisalignedbb1, tileentitypiston), enumdirection, d0);
                            AxisAlignedBB axisalignedbb3 = entity.getBoundingBox();

                            if (axisalignedbb2.c(axisalignedbb3)) {
                                d4 = Math.max(d4, a(axisalignedbb2, enumdirection, axisalignedbb3));
                                if (d4 >= d0) {
                                    break;
                                }
                            }
                        }

                        if (d4 > 0.0D) {
                            d4 = Math.min(d4, d0) + 0.01D;
                            a(enumdirection, entity, d4, enumdirection);
                            if (!tileentitypiston.extending && tileentitypiston.isSourcePiston) {
                                a(blockposition, entity, enumdirection, d0);
                            }
                        }
                    }
                }

            }
        }
    }

    private static void a(EnumDirection enumdirection, Entity entity, double d0, EnumDirection enumdirection1) {
        TileEntityPiston.NOCLIP.set(enumdirection);
        entity.move(EnumMoveType.PISTON, new Vec3D(d0 * (double) enumdirection1.getAdjacentX(), d0 * (double) enumdirection1.getAdjacentY(), d0 * (double) enumdirection1.getAdjacentZ()));
        TileEntityPiston.NOCLIP.set((Object) null);
    }

    private static void b(World world, BlockPosition blockposition, float f, TileEntityPiston tileentitypiston) {
        if (tileentitypiston.u()) {
            EnumDirection enumdirection = tileentitypiston.h();

            if (enumdirection.n().d()) {
                double d0 = tileentitypiston.movedState.getCollisionShape(world, blockposition).c(EnumDirection.EnumAxis.Y);
                AxisAlignedBB axisalignedbb = a(blockposition, new AxisAlignedBB(0.0D, d0, 0.0D, 1.0D, 1.5000000999999998D, 1.0D), tileentitypiston);
                double d1 = (double) (f - tileentitypiston.progress);
                List<Entity> list = world.getEntities((Entity) null, axisalignedbb, (entity) -> {
                    return a(axisalignedbb, entity);
                });
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    a(enumdirection, entity, d1, enumdirection);
                }

            }
        }
    }

    private static boolean a(AxisAlignedBB axisalignedbb, Entity entity) {
        return entity.getPushReaction() == EnumPistonReaction.NORMAL && entity.isOnGround() && entity.locX() >= axisalignedbb.minX && entity.locX() <= axisalignedbb.maxX && entity.locZ() >= axisalignedbb.minZ && entity.locZ() <= axisalignedbb.maxZ;
    }

    private boolean u() {
        return this.movedState.a(Blocks.HONEY_BLOCK);
    }

    public EnumDirection h() {
        return this.extending ? this.direction : this.direction.opposite();
    }

    private static double a(AxisAlignedBB axisalignedbb, EnumDirection enumdirection, AxisAlignedBB axisalignedbb1) {
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

    private static AxisAlignedBB a(BlockPosition blockposition, AxisAlignedBB axisalignedbb, TileEntityPiston tileentitypiston) {
        double d0 = (double) tileentitypiston.e(tileentitypiston.progress);

        return axisalignedbb.d((double) blockposition.getX() + d0 * (double) tileentitypiston.direction.getAdjacentX(), (double) blockposition.getY() + d0 * (double) tileentitypiston.direction.getAdjacentY(), (double) blockposition.getZ() + d0 * (double) tileentitypiston.direction.getAdjacentZ());
    }

    private static void a(BlockPosition blockposition, Entity entity, EnumDirection enumdirection, double d0) {
        AxisAlignedBB axisalignedbb = entity.getBoundingBox();
        AxisAlignedBB axisalignedbb1 = VoxelShapes.b().getBoundingBox().a(blockposition);

        if (axisalignedbb.c(axisalignedbb1)) {
            EnumDirection enumdirection1 = enumdirection.opposite();
            double d1 = a(axisalignedbb1, enumdirection1, axisalignedbb) + 0.01D;
            double d2 = a(axisalignedbb1, enumdirection1, axisalignedbb.a(axisalignedbb1)) + 0.01D;

            if (Math.abs(d1 - d2) < 0.01D) {
                d1 = Math.min(d1, d0) + 0.01D;
                a(enumdirection, entity, d1, enumdirection1);
            }
        }

    }

    public IBlockData i() {
        return this.movedState;
    }

    public void j() {
        if (this.level != null && (this.progressO < 1.0F || this.level.isClientSide)) {
            this.progress = 1.0F;
            this.progressO = this.progress;
            this.level.removeTileEntity(this.worldPosition);
            this.aa_();
            if (this.level.getType(this.worldPosition).a(Blocks.MOVING_PISTON)) {
                IBlockData iblockdata;

                if (this.isSourcePiston) {
                    iblockdata = Blocks.AIR.getBlockData();
                } else {
                    iblockdata = Block.b(this.movedState, (GeneratorAccess) this.level, this.worldPosition);
                }

                this.level.setTypeAndData(this.worldPosition, iblockdata, 3);
                this.level.a(this.worldPosition, iblockdata.getBlock(), this.worldPosition);
            }
        }

    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityPiston tileentitypiston) {
        tileentitypiston.lastTicked = world.getTime();
        tileentitypiston.progressO = tileentitypiston.progress;
        if (tileentitypiston.progressO >= 1.0F) {
            if (world.isClientSide && tileentitypiston.deathTicks < 5) {
                ++tileentitypiston.deathTicks;
            } else {
                world.removeTileEntity(blockposition);
                tileentitypiston.aa_();
                if (tileentitypiston.movedState != null && world.getType(blockposition).a(Blocks.MOVING_PISTON)) {
                    IBlockData iblockdata1 = Block.b(tileentitypiston.movedState, (GeneratorAccess) world, blockposition);

                    if (iblockdata1.isAir()) {
                        world.setTypeAndData(blockposition, tileentitypiston.movedState, 84);
                        Block.a(tileentitypiston.movedState, iblockdata1, world, blockposition, 3);
                    } else {
                        if (iblockdata1.b(BlockProperties.WATERLOGGED) && (Boolean) iblockdata1.get(BlockProperties.WATERLOGGED)) {
                            iblockdata1 = (IBlockData) iblockdata1.set(BlockProperties.WATERLOGGED, false);
                        }

                        world.setTypeAndData(blockposition, iblockdata1, 67);
                        world.a(blockposition, iblockdata1.getBlock(), blockposition);
                    }
                }

            }
        } else {
            float f = tileentitypiston.progress + 0.5F;

            a(world, blockposition, f, tileentitypiston);
            b(world, blockposition, f, tileentitypiston);
            tileentitypiston.progress = f;
            if (tileentitypiston.progress >= 1.0F) {
                tileentitypiston.progress = 1.0F;
            }

        }
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.movedState = GameProfileSerializer.c(nbttagcompound.getCompound("blockState"));
        this.direction = EnumDirection.fromType1(nbttagcompound.getInt("facing"));
        this.progress = nbttagcompound.getFloat("progress");
        this.progressO = this.progress;
        this.extending = nbttagcompound.getBoolean("extending");
        this.isSourcePiston = nbttagcompound.getBoolean("source");
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.set("blockState", GameProfileSerializer.a(this.movedState));
        nbttagcompound.setInt("facing", this.direction.b());
        nbttagcompound.setFloat("progress", this.progressO);
        nbttagcompound.setBoolean("extending", this.extending);
        nbttagcompound.setBoolean("source", this.isSourcePiston);
        return nbttagcompound;
    }

    public VoxelShape a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        VoxelShape voxelshape;

        if (!this.extending && this.isSourcePiston) {
            voxelshape = ((IBlockData) this.movedState.set(BlockPiston.EXTENDED, true)).getCollisionShape(iblockaccess, blockposition);
        } else {
            voxelshape = VoxelShapes.a();
        }

        EnumDirection enumdirection = (EnumDirection) TileEntityPiston.NOCLIP.get();

        if ((double) this.progress < 1.0D && enumdirection == this.h()) {
            return voxelshape;
        } else {
            IBlockData iblockdata;

            if (this.g()) {
                iblockdata = (IBlockData) ((IBlockData) Blocks.PISTON_HEAD.getBlockData().set(BlockPistonExtension.FACING, this.direction)).set(BlockPistonExtension.SHORT, this.extending != 1.0F - this.progress < 0.25F);
            } else {
                iblockdata = this.movedState;
            }

            float f = this.e(this.progress);
            double d0 = (double) ((float) this.direction.getAdjacentX() * f);
            double d1 = (double) ((float) this.direction.getAdjacentY() * f);
            double d2 = (double) ((float) this.direction.getAdjacentZ() * f);

            return VoxelShapes.a(voxelshape, iblockdata.getCollisionShape(iblockaccess, blockposition).a(d0, d1, d2));
        }
    }

    public long s() {
        return this.lastTicked;
    }
}
