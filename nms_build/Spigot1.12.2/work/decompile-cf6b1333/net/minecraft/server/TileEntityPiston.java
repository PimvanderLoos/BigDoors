package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class TileEntityPiston extends TileEntity implements ITickable {

    private IBlockData a;
    private EnumDirection f;
    private boolean g;
    private boolean h;
    private static final ThreadLocal<EnumDirection> i = new ThreadLocal() {
        protected EnumDirection a() {
            return null;
        }

        protected Object initialValue() {
            return this.a();
        }
    };
    private float j;
    private float k;

    public TileEntityPiston() {}

    public TileEntityPiston(IBlockData iblockdata, EnumDirection enumdirection, boolean flag, boolean flag1) {
        this.a = iblockdata;
        this.f = enumdirection;
        this.g = flag;
        this.h = flag1;
    }

    public IBlockData a() {
        return this.a;
    }

    public NBTTagCompound d() {
        return this.save(new NBTTagCompound());
    }

    public int v() {
        return 0;
    }

    public boolean f() {
        return this.g;
    }

    public EnumDirection h() {
        return this.f;
    }

    public boolean i() {
        return this.h;
    }

    private float e(float f) {
        return this.g ? f - 1.0F : 1.0F - f;
    }

    public AxisAlignedBB a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.a(iblockaccess, blockposition, this.j).b(this.a(iblockaccess, blockposition, this.k));
    }

    public AxisAlignedBB a(IBlockAccess iblockaccess, BlockPosition blockposition, float f) {
        f = this.e(f);
        IBlockData iblockdata = this.k();

        return iblockdata.e(iblockaccess, blockposition).d((double) (f * (float) this.f.getAdjacentX()), (double) (f * (float) this.f.getAdjacentY()), (double) (f * (float) this.f.getAdjacentZ()));
    }

    private IBlockData k() {
        return !this.f() && this.i() ? Blocks.PISTON_HEAD.getBlockData().set(BlockPistonExtension.TYPE, this.a.getBlock() == Blocks.STICKY_PISTON ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT).set(BlockPistonExtension.FACING, this.a.get(BlockPiston.FACING)) : this.a;
    }

    private void f(float f) {
        EnumDirection enumdirection = this.g ? this.f : this.f.opposite();
        double d0 = (double) (f - this.j);
        ArrayList arraylist = Lists.newArrayList();

        this.k().a(this.world, BlockPosition.ZERO, new AxisAlignedBB(BlockPosition.ZERO), arraylist, (Entity) null, true);
        if (!arraylist.isEmpty()) {
            AxisAlignedBB axisalignedbb = this.a(this.a((List) arraylist));
            List list = this.world.getEntities((Entity) null, this.a(axisalignedbb, enumdirection, d0).b(axisalignedbb));

            if (!list.isEmpty()) {
                boolean flag = this.a.getBlock() == Blocks.SLIME;

                for (int i = 0; i < list.size(); ++i) {
                    Entity entity = (Entity) list.get(i);

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

                        for (int j = 0; j < arraylist.size(); ++j) {
                            AxisAlignedBB axisalignedbb1 = this.a(this.a((AxisAlignedBB) arraylist.get(j)), enumdirection, d0);
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
                            TileEntityPiston.i.set(enumdirection);
                            entity.move(EnumMoveType.PISTON, d1 * (double) enumdirection.getAdjacentX(), d1 * (double) enumdirection.getAdjacentY(), d1 * (double) enumdirection.getAdjacentZ());
                            TileEntityPiston.i.set((Object) null);
                            if (!this.g && this.h) {
                                this.a(entity, enumdirection, d0);
                            }
                        }
                    }
                }

            }
        }
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
        double d0 = (double) this.e(this.j);

        return axisalignedbb.d((double) this.position.getX() + d0 * (double) this.f.getAdjacentX(), (double) this.position.getY() + d0 * (double) this.f.getAdjacentY(), (double) this.position.getZ() + d0 * (double) this.f.getAdjacentZ());
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
        AxisAlignedBB axisalignedbb1 = Block.j.a(this.position);

        if (axisalignedbb.c(axisalignedbb1)) {
            EnumDirection enumdirection1 = enumdirection.opposite();
            double d1 = this.a(axisalignedbb1, enumdirection1, axisalignedbb) + 0.01D;
            double d2 = this.a(axisalignedbb1, enumdirection1, axisalignedbb.a(axisalignedbb1)) + 0.01D;

            if (Math.abs(d1 - d2) < 0.01D) {
                d1 = Math.min(d1, d0) + 0.01D;
                TileEntityPiston.i.set(enumdirection);
                entity.move(EnumMoveType.PISTON, d1 * (double) enumdirection1.getAdjacentX(), d1 * (double) enumdirection1.getAdjacentY(), d1 * (double) enumdirection1.getAdjacentZ());
                TileEntityPiston.i.set((Object) null);
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

    public void j() {
        if (this.k < 1.0F && this.world != null) {
            this.j = 1.0F;
            this.k = this.j;
            this.world.s(this.position);
            this.z();
            if (this.world.getType(this.position).getBlock() == Blocks.PISTON_EXTENSION) {
                this.world.setTypeAndData(this.position, this.a, 3);
                this.world.a(this.position, this.a.getBlock(), this.position);
            }
        }

    }

    public void e() {
        this.k = this.j;
        if (this.k >= 1.0F) {
            this.world.s(this.position);
            this.z();
            if (this.world.getType(this.position).getBlock() == Blocks.PISTON_EXTENSION) {
                this.world.setTypeAndData(this.position, this.a, 3);
                this.world.a(this.position, this.a.getBlock(), this.position);
            }

        } else {
            float f = this.j + 0.5F;

            this.f(f);
            this.j = f;
            if (this.j >= 1.0F) {
                this.j = 1.0F;
            }

        }
    }

    public static void a(DataConverterManager dataconvertermanager) {}

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.a = Block.getById(nbttagcompound.getInt("blockId")).fromLegacyData(nbttagcompound.getInt("blockData"));
        this.f = EnumDirection.fromType1(nbttagcompound.getInt("facing"));
        this.j = nbttagcompound.getFloat("progress");
        this.k = this.j;
        this.g = nbttagcompound.getBoolean("extending");
        this.h = nbttagcompound.getBoolean("source");
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setInt("blockId", Block.getId(this.a.getBlock()));
        nbttagcompound.setInt("blockData", this.a.getBlock().toLegacyData(this.a));
        nbttagcompound.setInt("facing", this.f.a());
        nbttagcompound.setFloat("progress", this.k);
        nbttagcompound.setBoolean("extending", this.g);
        nbttagcompound.setBoolean("source", this.h);
        return nbttagcompound;
    }

    public void a(World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity) {
        if (!this.g && this.h) {
            this.a.set(BlockPiston.EXTENDED, Boolean.valueOf(true)).a(world, blockposition, axisalignedbb, list, entity, false);
        }

        EnumDirection enumdirection = (EnumDirection) TileEntityPiston.i.get();

        if ((double) this.j >= 1.0D || enumdirection != (this.g ? this.f : this.f.opposite())) {
            int i = list.size();
            IBlockData iblockdata;

            if (this.i()) {
                iblockdata = Blocks.PISTON_HEAD.getBlockData().set(BlockPistonExtension.FACING, this.f).set(BlockPistonExtension.SHORT, Boolean.valueOf(this.g != 1.0F - this.j < 0.25F));
            } else {
                iblockdata = this.a;
            }

            float f = this.e(this.j);
            double d0 = (double) ((float) this.f.getAdjacentX() * f);
            double d1 = (double) ((float) this.f.getAdjacentY() * f);
            double d2 = (double) ((float) this.f.getAdjacentZ() * f);

            iblockdata.a(world, blockposition, axisalignedbb.d(-d0, -d1, -d2), list, entity, true);

            for (int j = i; j < list.size(); ++j) {
                list.set(j, ((AxisAlignedBB) list.get(j)).d(d0, d1, d2));
            }

        }
    }
}
