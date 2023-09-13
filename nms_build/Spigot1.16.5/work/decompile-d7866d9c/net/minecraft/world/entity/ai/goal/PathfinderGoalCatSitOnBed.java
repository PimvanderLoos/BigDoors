package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.level.IWorldReader;

public class PathfinderGoalCatSitOnBed extends PathfinderGoalGotoTarget {

    private final EntityCat g;

    public PathfinderGoalCatSitOnBed(EntityCat entitycat, double d0, int i) {
        super(entitycat, d0, i, 6);
        this.g = entitycat;
        this.f = -2;
        this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        return this.g.isTamed() && !this.g.isWillSit() && !this.g.eW() && super.a();
    }

    @Override
    public void c() {
        super.c();
        this.g.setSitting(false);
    }

    @Override
    protected int a(EntityCreature entitycreature) {
        return 40;
    }

    @Override
    public void d() {
        super.d();
        this.g.x(false);
    }

    @Override
    public void e() {
        super.e();
        this.g.setSitting(false);
        if (!this.l()) {
            this.g.x(false);
        } else if (!this.g.eW()) {
            this.g.x(true);
        }

    }

    @Override
    protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.isEmpty(blockposition.up()) && iworldreader.getType(blockposition).getBlock().a((Tag) TagsBlock.BEDS);
    }
}
