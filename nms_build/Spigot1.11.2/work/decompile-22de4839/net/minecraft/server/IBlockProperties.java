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

    MaterialMapColor g();

    IBlockData a(EnumBlockRotation enumblockrotation);

    IBlockData a(EnumBlockMirror enumblockmirror);

    boolean h();

    EnumRenderType j();

    boolean l();

    boolean m();

    boolean n();

    int a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection);

    boolean o();

    int a(World world, BlockPosition blockposition);

    float b(World world, BlockPosition blockposition);

    float a(EntityHuman entityhuman, World world, BlockPosition blockposition);

    int b(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection);

    EnumPistonReaction p();

    IBlockData b(IBlockAccess iblockaccess, BlockPosition blockposition);

    boolean q();

    @Nullable
    AxisAlignedBB c(IBlockAccess iblockaccess, BlockPosition blockposition);

    void a(World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag);

    AxisAlignedBB d(IBlockAccess iblockaccess, BlockPosition blockposition);

    MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1);

    boolean r();

    Vec3D e(IBlockAccess iblockaccess, BlockPosition blockposition);

    boolean s();
}
