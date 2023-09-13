package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;

public class NavigationSpider extends Navigation {

    private BlockPosition pathToPosition;

    public NavigationSpider(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    @Override
    public PathEntity a(BlockPosition blockposition, int i) {
        this.pathToPosition = blockposition;
        return super.a(blockposition, i);
    }

    @Override
    public PathEntity a(Entity entity, int i) {
        this.pathToPosition = entity.getChunkCoordinates();
        return super.a(entity, i);
    }

    @Override
    public boolean a(Entity entity, double d0) {
        PathEntity pathentity = this.a(entity, 0);

        if (pathentity != null) {
            return this.a(pathentity, d0);
        } else {
            this.pathToPosition = entity.getChunkCoordinates();
            this.speedModifier = d0;
            return true;
        }
    }

    @Override
    public void c() {
        if (!this.m()) {
            super.c();
        } else {
            if (this.pathToPosition != null) {
                if (!this.pathToPosition.a((IPosition) this.mob.getPositionVector(), (double) this.mob.getWidth()) && (this.mob.locY() <= (double) this.pathToPosition.getY() || !(new BlockPosition((double) this.pathToPosition.getX(), this.mob.locY(), (double) this.pathToPosition.getZ())).a((IPosition) this.mob.getPositionVector(), (double) this.mob.getWidth()))) {
                    this.mob.getControllerMove().a((double) this.pathToPosition.getX(), (double) this.pathToPosition.getY(), (double) this.pathToPosition.getZ(), this.speedModifier);
                } else {
                    this.pathToPosition = null;
                }
            }

        }
    }
}
