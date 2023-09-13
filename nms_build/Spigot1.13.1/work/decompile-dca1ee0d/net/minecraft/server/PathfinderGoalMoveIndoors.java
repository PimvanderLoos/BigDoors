package net.minecraft.server;

public class PathfinderGoalMoveIndoors extends PathfinderGoal {

    private final EntityCreature a;
    private VillageDoor b;
    private int c = -1;
    private int d = -1;

    public PathfinderGoalMoveIndoors(EntityCreature entitycreature) {
        this.a = entitycreature;
        this.a(1);
    }

    public boolean a() {
        BlockPosition blockposition = new BlockPosition(this.a);

        if ((!this.a.world.L() || this.a.world.isRaining() && this.a.world.getBiome(blockposition).c() != BiomeBase.Precipitation.RAIN) && this.a.world.worldProvider.g()) {
            if (this.a.getRandom().nextInt(50) != 0) {
                return false;
            } else if (this.c != -1 && this.a.d((double) this.c, this.a.locY, (double) this.d) < 4.0D) {
                return false;
            } else {
                Village village = this.a.world.af().getClosestVillage(blockposition, 14);

                if (village == null) {
                    return false;
                } else {
                    this.b = village.c(blockposition);
                    return this.b != null;
                }
            }
        } else {
            return false;
        }
    }

    public boolean b() {
        return !this.a.getNavigation().p();
    }

    public void c() {
        this.c = -1;
        BlockPosition blockposition = this.b.e();
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();

        if (this.a.c(blockposition) > 256.0D) {
            Vec3D vec3d = RandomPositionGenerator.a(this.a, 14, 3, new Vec3D((double) i + 0.5D, (double) j, (double) k + 0.5D));

            if (vec3d != null) {
                this.a.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            }
        } else {
            this.a.getNavigation().a((double) i + 0.5D, (double) j, (double) k + 0.5D, 1.0D);
        }

    }

    public void d() {
        this.c = this.b.e().getX();
        this.d = this.b.e().getZ();
        this.b = null;
    }
}
