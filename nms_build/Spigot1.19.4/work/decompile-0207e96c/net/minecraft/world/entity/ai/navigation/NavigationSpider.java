package net.minecraft.world.entity.ai.navigation;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;

public class NavigationSpider extends Navigation {

    @Nullable
    private BlockPosition pathToPosition;

    public NavigationSpider(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    @Override
    public PathEntity createPath(BlockPosition blockposition, int i) {
        this.pathToPosition = blockposition;
        return super.createPath(blockposition, i);
    }

    @Override
    public PathEntity createPath(Entity entity, int i) {
        this.pathToPosition = entity.blockPosition();
        return super.createPath(entity, i);
    }

    @Override
    public boolean moveTo(Entity entity, double d0) {
        PathEntity pathentity = this.createPath(entity, 0);

        if (pathentity != null) {
            return this.moveTo(pathentity, d0);
        } else {
            this.pathToPosition = entity.blockPosition();
            this.speedModifier = d0;
            return true;
        }
    }

    @Override
    public void tick() {
        if (!this.isDone()) {
            super.tick();
        } else {
            if (this.pathToPosition != null) {
                if (!this.pathToPosition.closerToCenterThan(this.mob.position(), (double) this.mob.getBbWidth()) && (this.mob.getY() <= (double) this.pathToPosition.getY() || !BlockPosition.containing((double) this.pathToPosition.getX(), this.mob.getY(), (double) this.pathToPosition.getZ()).closerToCenterThan(this.mob.position(), (double) this.mob.getBbWidth()))) {
                    this.mob.getMoveControl().setWantedPosition((double) this.pathToPosition.getX(), (double) this.pathToPosition.getY(), (double) this.pathToPosition.getZ(), this.speedModifier);
                } else {
                    this.pathToPosition = null;
                }
            }

        }
    }
}
