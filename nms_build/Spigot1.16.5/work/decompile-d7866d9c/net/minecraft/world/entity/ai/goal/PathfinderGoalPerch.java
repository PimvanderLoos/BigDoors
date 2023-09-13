package net.minecraft.world.entity.ai.goal;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.animal.EntityPerchable;

public class PathfinderGoalPerch extends PathfinderGoal {

    private final EntityPerchable a;
    private EntityPlayer b;
    private boolean c;

    public PathfinderGoalPerch(EntityPerchable entityperchable) {
        this.a = entityperchable;
    }

    @Override
    public boolean a() {
        EntityPlayer entityplayer = (EntityPlayer) this.a.getOwner();
        boolean flag = entityplayer != null && !entityplayer.isSpectator() && !entityplayer.abilities.isFlying && !entityplayer.isInWater();

        return !this.a.isWillSit() && flag && this.a.eY();
    }

    @Override
    public boolean C_() {
        return !this.c;
    }

    @Override
    public void c() {
        this.b = (EntityPlayer) this.a.getOwner();
        this.c = false;
    }

    @Override
    public void e() {
        if (!this.c && !this.a.isSitting() && !this.a.isLeashed()) {
            if (this.a.getBoundingBox().c(this.b.getBoundingBox())) {
                this.c = this.a.d(this.b);
            }

        }
    }
}
