package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class PathfinderGoalEatTile extends PathfinderGoal {

    private static final Predicate<IBlockData> b = BlockStatePredicate.a((Block) Blocks.TALLGRASS).a(BlockLongGrass.TYPE, Predicates.equalTo(BlockLongGrass.EnumTallGrassType.GRASS));
    private final EntityInsentient c;
    private final World d;
    int a;

    public PathfinderGoalEatTile(EntityInsentient entityinsentient) {
        this.c = entityinsentient;
        this.d = entityinsentient.world;
        this.a(7);
    }

    public boolean a() {
        if (this.c.getRandom().nextInt(this.c.isBaby() ? 50 : 1000) != 0) {
            return false;
        } else {
            BlockPosition blockposition = new BlockPosition(this.c.locX, this.c.locY, this.c.locZ);

            return PathfinderGoalEatTile.b.apply(this.d.getType(blockposition)) ? true : this.d.getType(blockposition.down()).getBlock() == Blocks.GRASS;
        }
    }

    public void c() {
        this.a = 40;
        this.d.broadcastEntityEffect(this.c, (byte) 10);
        this.c.getNavigation().p();
    }

    public void d() {
        this.a = 0;
    }

    public boolean b() {
        return this.a > 0;
    }

    public int f() {
        return this.a;
    }

    public void e() {
        this.a = Math.max(0, this.a - 1);
        if (this.a == 4) {
            BlockPosition blockposition = new BlockPosition(this.c.locX, this.c.locY, this.c.locZ);

            if (PathfinderGoalEatTile.b.apply(this.d.getType(blockposition))) {
                if (this.d.getGameRules().getBoolean("mobGriefing")) {
                    this.d.setAir(blockposition, false);
                }

                this.c.A();
            } else {
                BlockPosition blockposition1 = blockposition.down();

                if (this.d.getType(blockposition1).getBlock() == Blocks.GRASS) {
                    if (this.d.getGameRules().getBoolean("mobGriefing")) {
                        this.d.triggerEffect(2001, blockposition1, Block.getId(Blocks.GRASS));
                        this.d.setTypeAndData(blockposition1, Blocks.DIRT.getBlockData(), 2);
                    }

                    this.c.A();
                }
            }

        }
    }
}
