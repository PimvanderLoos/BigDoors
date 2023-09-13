package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class NavigationListener implements IWorldAccess {

    private final List<NavigationAbstract> a = Lists.newArrayList();

    public NavigationListener() {}

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, int i) {
        if (this.a(world, blockposition, iblockdata, iblockdata1)) {
            int j = 0;

            for (int k = this.a.size(); j < k; ++j) {
                NavigationAbstract navigationabstract = (NavigationAbstract) this.a.get(j);

                if (navigationabstract != null && !navigationabstract.j()) {
                    PathEntity pathentity = navigationabstract.l();

                    if (pathentity != null && !pathentity.b() && pathentity.d() != 0) {
                        PathPoint pathpoint = navigationabstract.c.c();
                        double d0 = blockposition.distanceSquared(((double) pathpoint.a + navigationabstract.a.locX) / 2.0D, ((double) pathpoint.b + navigationabstract.a.locY) / 2.0D, ((double) pathpoint.c + navigationabstract.a.locZ) / 2.0D);
                        int l = (pathentity.d() - pathentity.e()) * (pathentity.d() - pathentity.e());

                        if (d0 < (double) l) {
                            navigationabstract.k();
                        }
                    }
                }
            }

        }
    }

    protected boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        AxisAlignedBB axisalignedbb = iblockdata.d(world, blockposition);
        AxisAlignedBB axisalignedbb1 = iblockdata1.d(world, blockposition);

        return axisalignedbb != axisalignedbb1 && (axisalignedbb == null || !axisalignedbb.equals(axisalignedbb1));
    }

    public void a(BlockPosition blockposition) {}

    public void a(int i, int j, int k, int l, int i1, int j1) {}

    public void a(@Nullable EntityHuman entityhuman, SoundEffect soundeffect, SoundCategory soundcategory, double d0, double d1, double d2, float f, float f1) {}

    public void a(int i, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {}

    public void a(int i, boolean flag, boolean flag1, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {}

    public void a(Entity entity) {
        if (entity instanceof EntityInsentient) {
            this.a.add(((EntityInsentient) entity).getNavigation());
        }

    }

    public void b(Entity entity) {
        if (entity instanceof EntityInsentient) {
            this.a.remove(((EntityInsentient) entity).getNavigation());
        }

    }

    public void a(SoundEffect soundeffect, BlockPosition blockposition) {}

    public void a(int i, BlockPosition blockposition, int j) {}

    public void a(EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {}

    public void b(int i, BlockPosition blockposition, int j) {}
}
