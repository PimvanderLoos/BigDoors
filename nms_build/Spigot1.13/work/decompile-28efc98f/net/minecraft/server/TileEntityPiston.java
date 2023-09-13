package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public class TileEntityPiston extends TileEntity implements ITickable {

    private IBlockData a;
    private EnumDirection e;
    private boolean f;
    private boolean g;
    private static final ThreadLocal<EnumDirection> h = new ThreadLocal() {
        protected EnumDirection a() {
            return null;
        }

        protected Object initialValue() {
            return this.a();
        }
    };
    private float i;
    private float j;
    private long k;

    public TileEntityPiston() {
        super(TileEntityTypes.k);
    }

    public TileEntityPiston(IBlockData iblockdata, EnumDirection enumdirection, boolean flag, boolean flag1) {
        this();
        this.a = iblockdata;
        this.e = enumdirection;
        this.f = flag;
        this.g = flag1;
    }

    public NBTTagCompound aa_() {
        return this.save(new NBTTagCompound());
    }

    public boolean c() {
        return this.f;
    }

    public boolean f() {
        return this.g;
    }

    public float a(float f) {
        if (f > 1.0F) {
            f = 1.0F;
        }

        return this.j + (this.i - this.j) * f;
    }

    private float e(float f) {
        return this.f ? f - 1.0F : 1.0F - f;
    }

    private IBlockData l() {
        return !this.c() && this.f() ? (IBlockData) ((IBlockData) Blocks.PISTON_HEAD.getBlockData().set(BlockPistonExtension.TYPE, this.a.getBlock() == Blocks.STICKY_PISTON ? BlockPropertyPistonType.STICKY : BlockPropertyPistonType.DEFAULT)).set(BlockPistonExtension.FACING, this.a.get(BlockPiston.FACING)) : this.a;
    }

    private void f(float f) {
        EnumDirection enumdirection = this.h();
        double d0 = (double) (f - this.i);
        VoxelShape voxelshape = this.l().h(this.world, this.getPosition());

        if (!voxelshape.b()) {
            List list = voxelshape.d();
            AxisAlignedBB axisalignedbb = this.a(this.a(list));
            List list1 = this.world.getEntities((Entity) null, this.a(axisalignedbb, enumdirection, d0).b(axisalignedbb));

            if (!list1.isEmpty()) {
                boolean flag = this.a.getBlock() == Blocks.SLIME_BLOCK;

                for (int i = 0; i < list1.size(); ++i) {
                    Entity entity = (Entity) list1.get(i);

                    if (entity.getPushReaction() != EnumPistonReaction.IGNORE) {
                        if (flag) {
                            switch (enumdirection.k()) {
                            case X:
                                entity.motX = (double) enumdirection.getAdjacentX();
                                break;

                            case Y:
                                entity.motY = (double) enumdirection.getAdjacentY();
                                break;

                            case Z:
                                entity.motZ = (double) enumdirection.getAdjacentZ();
                            }
                        }

                        double d1 = 0.0D;

                        for (int j = 0; j < list.size(); ++j) {
                            AxisAlignedBB axisalignedbb1 = this.a(this.a((AxisAlignedBB) list.get(j)), enumdirection, d0);
                            AxisAlignedBB axisalignedbb2 = entity.getBoundingBox();

                            if (axisalignedbb1.c(axisalignedbb2)) {
                                d1 = Math.max(d1, this.a(axisalignedbb1, enumdirection, axisalignedbb2));
                                if (d1 >= d0) {
                                    break;
                                }
                            }
                        }

                        if (d1 > 0.0D) {
                            d1 = Math.min(d1, d0) + 0.01D;
                            TileEntityPiston.h.set(enumdirection);
                            entity.move(EnumMoveType.PISTON, d1 * (double) enumdirection.getAdjacentX(), d1 * (double) enumdirection.getAdjacentY(), d1 * (double) enumdirection.getAdjacentZ());
                            TileEntityPiston.h.set((Object) null);
                            if (!this.f && this.g) {
                                this.a(entity, enumdirection, d0);
                            }
                        }
                    }
                }

            }
        }
    }

    public EnumDirection h() {
        return this.f ? this.e : this.e.opposite();
    }

    private AxisAlignedBB a(List<AxisAlignedBB> list) {
        double d0 = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 1.0D;
        double d4 = 1.0D;
        double d5 = 1.0D;

        AxisAlignedBB axisalignedbb;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); d5 = Math.max(axisalignedbb.f, d5)) {
            axisalignedbb = (AxisAlignedBB) iterator.next();
            d0 = Math.min(axisalignedbb.a, d0);
            d1 = Math.min(axisalignedbb.b, d1);
            d2 = Math.min(axisalignedbb.c, d2);
            d3 = Math.max(axisalignedbb.d, d3);
            d4 = Math.max(axisalignedbb.e, d4);
        }

        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    private double a(AxisAlignedBB axisalignedbb, EnumDirection enumdirection, AxisAlignedBB axisalignedbb1) {
        switch (enumdirection.k()) {
        case X:
            return b(axisalignedbb, enumdirection, axisalignedbb1);

        case Y:
        default:
            return c(axisalignedbb, enumdirection, axisalignedbb1);

        case Z:
            return d(axisalignedbb, enumdirection, axisalignedbb1);
        }
    }

    private AxisAlignedBB a(AxisAlignedBB axisalignedbb) {
        double d0 = (double) this.e(this.i);

        return axisalignedbb.d((double) this.position.getX() + d0 * (double) this.e.getAdjacentX(), (double) this.position.getY() + d0 * (double) this.e.getAdjacentY(), (double) this.position.getZ() + d0 * (double) this.e.getAdjacentZ());
    }

    private AxisAlignedBB a(AxisAlignedBB axisalignedbb, EnumDirection enumdirection, double d0) {
        double d1 = d0 * (double) enumdirection.c().a();
        double d2 = Math.min(d1, 0.0D);
        double d3 = Math.max(d1, 0.0D);

        switch (enumdirection) {
        case WEST:
            return new AxisAlignedBB(axisalignedbb.a + d2, axisalignedbb.b, axisalignedbb.c, axisalignedbb.a + d3, axisalignedbb.e, axisalignedbb.f);

        case EAST:
            return new AxisAlignedBB(axisalignedbb.d + d2, axisalignedbb.b, axisalignedbb.c, axisalignedbb.d + d3, axisalignedbb.e, axisalignedbb.f);

        case DOWN:
            return new AxisAlignedBB(axisalignedbb.a, axisalignedbb.b + d2, axisalignedbb.c, axisalignedbb.d, axisalignedbb.b + d3, axisalignedbb.f);

        case UP:
        default:
            return new AxisAlignedBB(axisalignedbb.a, axisalignedbb.e + d2, axisalignedbb.c, axisalignedbb.d, axisalignedbb.e + d3, axisalignedbb.f);

        case NORTH:
            return new AxisAlignedBB(axisalignedbb.a, axisalignedbb.b, axisalignedbb.c + d2, axisalignedbb.d, axisalignedbb.e, axisalignedbb.c + d3);

        case SOUTH:
            return new AxisAlignedBB(axisalignedbb.a, axisalignedbb.b, axisalignedbb.f + d2, axisalignedbb.d, axisalignedbb.e, axisalignedbb.f + d3);
        }
    }

    private void a(Entity entity, EnumDirection enumdirection, double d0) {
        AxisAlignedBB axisalignedbb = entity.getBoundingBox();
        AxisAlignedBB axisalignedbb1 = VoxelShapes.b().a().a(this.position);

        if (axisalignedbb.c(axisalignedbb1)) {
            EnumDirection enumdirection1 = enumdirection.opposite();
            double d1 = this.a(axisalignedbb1, enumdirection1, axisalignedbb) + 0.01D;
            double d2 = this.a(axisalignedbb1, enumdirection1, axisalignedbb.a(axisalignedbb1)) + 0.01D;

            if (Math.abs(d1 - d2) < 0.01D) {
                d1 = Math.min(d1, d0) + 0.01D;
                TileEntityPiston.h.set(enumdirection);
                entity.move(EnumMoveType.PISTON, d1 * (double) enumdirection1.getAdjacentX(), d1 * (double) enumdirection1.getAdjacentY(), d1 * (double) enumdirection1.getAdjacentZ());
                TileEntityPiston.h.set((Object) null);
            }
        }

    }

    private static double b(AxisAlignedBB axisalignedbb, EnumDirection enumdirection, AxisAlignedBB axisalignedbb1) {
        return enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE ? axisalignedbb.d - axisalignedbb1.a : axisalignedbb1.d - axisalignedbb.a;
    }

    private static double c(AxisAlignedBB axisalignedbb, EnumDirection enumdirection, AxisAlignedBB axisalignedbb1) {
        return enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE ? axisalignedbb.e - axisalignedbb1.b : axisalignedbb1.e - axisalignedbb.b;
    }

    private static double d(AxisAlignedBB axisalignedbb, EnumDirection enumdirection, AxisAlignedBB axisalignedbb1) {
        return enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE ? axisalignedbb.f - axisalignedbb1.c : axisalignedbb1.f - axisalignedbb.c;
    }

    public IBlockData i() {
        return this.a;
    }

    public void j() {
        if (this.j < 1.0F && this.world != null) {
            this.i = 1.0F;
            this.j = this.i;
            this.world.n(this.position);
            this.y();
            if (this.world.getType(this.position).getBlock() == Blocks.MOVING_PISTON) {
                IBlockData iblockdata;

                if (this.g) {
                    iblockdata = Blocks.AIR.getBlockData();
                } else {
                    iblockdata = Block.b(this.a, this.world, this.position);
                }

                this.world.setTypeAndData(this.position, iblockdata, 3);
                this.world.a(this.position, iblockdata.getBlock(), this.position);
            }
        }

    }

    public void Y_() {
        this.k = this.world.getTime();
        this.j = this.i;
        if (this.j >= 1.0F) {
            this.world.n(this.position);
            this.y();
            if (this.a != null && this.world.getType(this.position).getBlock() == Blocks.MOVING_PISTON) {
                IBlockData iblockdata = Block.b(this.a, this.world, this.position);

                if (iblockdata.isAir()) {
                    this.world.setTypeAndData(this.position, this.a, 20);
                    Block.a(this.a, iblockdata, this.world, this.position, 3);
                } else {
                    if (iblockdata.b(BlockProperties.x) && ((Boolean) iblockdata.get(BlockProperties.x)).booleanValue()) {
                        iblockdata = (IBlockData) iblockdata.set(BlockProperties.x, Boolean.valueOf(false));
                    }

                    this.world.setTypeAndData(this.position, iblockdata, 3);
                    this.world.a(this.position, iblockdata.getBlock(), this.position);
                }
            }

        } else {
            float f = this.i + 0.5F;

            this.f(f);
            this.i = f;
            if (this.i >= 1.0F) {
                this.i = 1.0F;
            }

        }
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.a = GameProfileSerializer.d(nbttagcompound.getCompound("blockState"));
        this.e = EnumDirection.fromType1(nbttagcompound.getInt("facing"));
        this.i = nbttagcompound.getFloat("progress");
        this.j = this.i;
        this.f = nbttagcompound.getBoolean("extending");
        this.g = nbttagcompound.getBoolean("source");
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.set("blockState", GameProfileSerializer.a(this.a));
        nbttagcompound.setInt("facing", this.e.a());
        nbttagcompound.setFloat("progress", this.j);
        nbttagcompound.setBoolean("extending", this.f);
        nbttagcompound.setBoolean("source", this.g);
        return nbttagcompound;
    }

    public VoxelShape a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        VoxelShape voxelshape;

        if (!this.f && this.g) {
            voxelshape = ((IBlockData) this.a.set(BlockPiston.EXTENDED, Boolean.valueOf(true))).h(iblockaccess, blockposition);
        } else {
            voxelshape = VoxelShapes.a();
        }

        EnumDirection enumdirection = (EnumDirection) TileEntityPiston.h.get();

        if ((double) this.i < 1.0D && enumdirection == this.h()) {
            return voxelshape;
        } else {
            IBlockData iblockdata;

            if (this.f()) {
                iblockdata = (IBlockData) ((IBlockData) Blocks.PISTON_HEAD.getBlockData().set(BlockPistonExtension.FACING, this.e)).set(BlockPistonExtension.SHORT, Boolean.valueOf(this.f != 1.0F - this.i < 4.0F));
            } else {
                iblockdata = this.a;
            }

            float f = this.e(this.i);
            double d0 = (double) ((float) this.e.getAdjacentX() * f);
            double d1 = (double) ((float) this.e.getAdjacentY() * f);
            double d2 = (double) ((float) this.e.getAdjacentZ() * f);

            return VoxelShapes.a(voxelshape, iblockdata.h(iblockaccess, blockposition).a(d0, d1, d2));
        }
    }

    public long k() {
        return this.k;
    }
}
