package net.minecraft.server;

public class PathfinderGoalRestrictOpenDoor extends PathfinderGoal {

    private final EntityCreature a;
    private VillageDoor b;

    public PathfinderGoalRestrictOpenDoor(EntityCreature entitycreature) {
        this.a = entitycreature;
        if (!(entitycreature.getNavigation() instanceof Navigation)) {
            throw new IllegalArgumentException("Unsupported mob type for RestrictOpenDoorGoal");
        }
    }

    public boolean a() {
        if (this.a.world.L()) {
            return false;
        } else {
            BlockPosition blockposition = new BlockPosition(this.a);
            Village village = this.a.world.af().getClosestVillage(blockposition, 16);

            if (village == null) {
                return false;
            } else {
                this.b = village.b(blockposition);
                return this.b == null ? false : (double) this.b.b(blockposition) < 2.25D;
            }
        }
    }

    public boolean b() {
        return this.a.world.L() ? false : !this.b.i() && this.b.c(new BlockPosition(this.a));
    }

    public void c() {
        ((Navigation) this.a.getNavigation()).a(false);
        ((Navigation) this.a.getNavigation()).b(false);
    }

    public void d() {
        ((Navigation) this.a.getNavigation()).a(true);
        ((Navigation) this.a.getNavigation()).b(true);
        this.b = null;
    }

    public void e() {
        this.b.b();
    }
}
