package net.minecraft.server;

import java.util.List;
import javax.annotation.Nullable;

public interface IBlockProperties {

    Material getMaterial();

    boolean b();

    boolean a(Entity entity);

    int c();

    int d();

    boolean f();

    MaterialMapColor a(IBlockAccess iblockaccess, BlockPosition blockposition);

    IBlockData a(EnumBlockRotation enumblockrotation);

    IBlockData a(EnumBlockMirror enumblockmirror);

    boolean g();

    EnumRenderType i();

    boolean k();

    boolean l();

    boolean m();

    int a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection);

    boolean n();

    int a(World world, BlockPosition blockposition);

    float b(World world, BlockPosition blockposition);

    float a(EntityHuman entityhuman, World world, BlockPosition blockposition);

    int b(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection);

    EnumPistonReaction o();

    IBlockData c(IBlockAccess iblockaccess, BlockPosition blockposition);

    boolean p();

    @Nullable
    AxisAlignedBB d(IBlockAccess iblockaccess, BlockPosition blockposition);

    void a(World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag);

    AxisAlignedBB e(IBlockAccess iblockaccess, BlockPosition blockposition);

    MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1);

    boolean q();

    Vec3D f(IBlockAccess iblockaccess, BlockPosition blockposition);

    boolean r();

    EnumBlockFaceShape d(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection);
}
