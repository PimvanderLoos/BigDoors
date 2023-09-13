package net.minecraft.server;

public class PathfinderGoalJumpOnBlock extends PathfinderGoalGotoTarget {

    private final EntityCat g;

    public PathfinderGoalJumpOnBlock(EntityCat entitycat, double d0) {
        super(entitycat, d0, 8);
        this.g = entitycat;
    }

    @Override
    public boolean a() {
        return this.g.isTamed() && !this.g.isWillSit() && super.a();
    }

    @Override
    public void c() {
        super.c();
        this.g.setSitting(false);
    }

    @Override
    public void d() {
        super.d();
        this.g.setSitting(false);
    }

    @Override
    public void e() {
        super.e();
        this.g.setSitting(this.l());
    }

    @Override
    protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        if (!iworldreader.isEmpty(blockposition.up())) {
            return false;
        } else {
            IBlockData iblockdata = iworldreader.getType(blockposition);

            return iblockdata.a(Blocks.CHEST) ? TileEntityChest.a((IBlockAccess) iworldreader, blockposition) < 1 : (iblockdata.a(Blocks.FURNACE) && (Boolean) iblockdata.get(BlockFurnaceFurace.LIT) ? true : iblockdata.a((Tag) TagsBlock.BEDS, (blockbase_blockdata) -> {
                return (Boolean) blockbase_blockdata.d(BlockBed.PART).map((blockpropertybedpart) -> {
                    return blockpropertybedpart != BlockPropertyBedPart.HEAD;
                }).orElse(true);
            }));
        }
    }
}
