package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.level.IWorldReader;

public class PathfinderGoalCatSitOnBed extends PathfinderGoalGotoTarget {

    private final EntityCat cat;

    public PathfinderGoalCatSitOnBed(EntityCat entitycat, double d0, int i) {
        super(entitycat, d0, i, 6);
        this.cat = entitycat;
        this.verticalSearchStart = -2;
        this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        return this.cat.isTamed() && !this.cat.isWillSit() && !this.cat.fG() && super.a();
    }

    @Override
    public void c() {
        super.c();
        this.cat.setSitting(false);
    }

    @Override
    protected int a(EntityCreature entitycreature) {
        return 40;
    }

    @Override
    public void d() {
        super.d();
        this.cat.z(false);
    }

    @Override
    public void e() {
        super.e();
        this.cat.setSitting(false);
        if (!this.l()) {
            this.cat.z(false);
        } else if (!this.cat.fG()) {
            this.cat.z(true);
        }

    }

    @Override
    protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.isEmpty(blockposition.up()) && iworldreader.getType(blockposition).a((Tag) TagsBlock.BEDS);
    }
}
