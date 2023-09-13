package net.minecraft.server;

public class PathfinderGoalFollowOwnerParrot extends PathfinderGoalFollowOwner {

    public PathfinderGoalFollowOwnerParrot(EntityTameableAnimal entitytameableanimal, double d0, float f, float f1) {
        super(entitytameableanimal, d0, f, f1);
    }

    protected boolean a(int i, int j, int k, int l, int i1) {
        IBlockData iblockdata = this.a.getType(new BlockPosition(i + l, k - 1, j + i1));

        return (iblockdata.q() || iblockdata.a(TagsBlock.LEAVES)) && this.a.isEmpty(new BlockPosition(i + l, k, j + i1)) && this.a.isEmpty(new BlockPosition(i + l, k + 1, j + i1));
    }
}
