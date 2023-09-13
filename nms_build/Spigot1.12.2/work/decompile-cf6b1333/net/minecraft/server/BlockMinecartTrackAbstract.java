package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public abstract class BlockMinecartTrackAbstract extends Block {

    protected static final AxisAlignedBB a = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected static final AxisAlignedBB b = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected final boolean c;

    public static boolean b(World world, BlockPosition blockposition) {
        return i(world.getType(blockposition));
    }

    public static boolean i(IBlockData iblockdata) {
        Block block = iblockdata.getBlock();

        return block == Blocks.RAIL || block == Blocks.GOLDEN_RAIL || block == Blocks.DETECTOR_RAIL || block == Blocks.ACTIVATOR_RAIL;
    }

    protected BlockMinecartTrackAbstract(boolean flag) {
        super(Material.ORIENTABLE);
        this.c = flag;
        this.a(CreativeModeTab.e);
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockMinecartTrackAbstract.k;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = iblockdata.getBlock() == this ? (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(this.g()) : null;

        return blockminecarttrackabstract_enumtrackposition != null && blockminecarttrackabstract_enumtrackposition.c() ? BlockMinecartTrackAbstract.b : BlockMinecartTrackAbstract.a;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return world.getType(blockposition.down()).q();
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide) {
            iblockdata = this.a(world, blockposition, iblockdata, true);
            if (this.c) {
                iblockdata.doPhysics(world, blockposition, this, blockposition);
            }
        }

    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(this.g());
            boolean flag = false;

            if (!world.getType(blockposition.down()).q()) {
                flag = true;
            }

            if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST && !world.getType(blockposition.east()).q()) {
                flag = true;
            } else if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST && !world.getType(blockposition.west()).q()) {
                flag = true;
            } else if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH && !world.getType(blockposition.north()).q()) {
                flag = true;
            } else if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH && !world.getType(blockposition.south()).q()) {
                flag = true;
            }

            if (flag && !world.isEmpty(blockposition)) {
                this.b(world, blockposition, iblockdata, 0);
                world.setAir(blockposition);
            } else {
                this.a(iblockdata, world, blockposition, block);
            }

        }
    }

    protected void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {}

    protected IBlockData a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return world.isClientSide ? iblockdata : (new BlockMinecartTrackAbstract.MinecartTrackLogic(world, blockposition, iblockdata)).a(world.isBlockIndirectlyPowered(blockposition), flag).c();
    }

    public EnumPistonReaction h(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        if (((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(this.g())).c()) {
            world.applyPhysics(blockposition.up(), this, false);
        }

        if (this.c) {
            world.applyPhysics(blockposition, this, false);
            world.applyPhysics(blockposition.down(), this, false);
        }

    }

    public abstract IBlockState<BlockMinecartTrackAbstract.EnumTrackPosition> g();

    public static enum EnumTrackPosition implements INamable {

        NORTH_SOUTH(0, "north_south"), EAST_WEST(1, "east_west"), ASCENDING_EAST(2, "ascending_east"), ASCENDING_WEST(3, "ascending_west"), ASCENDING_NORTH(4, "ascending_north"), ASCENDING_SOUTH(5, "ascending_south"), SOUTH_EAST(6, "south_east"), SOUTH_WEST(7, "south_west"), NORTH_WEST(8, "north_west"), NORTH_EAST(9, "north_east");

        private static final BlockMinecartTrackAbstract.EnumTrackPosition[] k = new BlockMinecartTrackAbstract.EnumTrackPosition[values().length];
        private final int l;
        private final String m;

        private EnumTrackPosition(int i, String s) {
            this.l = i;
            this.m = s;
        }

        public int a() {
            return this.l;
        }

        public String toString() {
            return this.m;
        }

        public boolean c() {
            return this == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH || this == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST || this == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH || this == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST;
        }

        public static BlockMinecartTrackAbstract.EnumTrackPosition a(int i) {
            if (i < 0 || i >= BlockMinecartTrackAbstract.EnumTrackPosition.k.length) {
                i = 0;
            }

            return BlockMinecartTrackAbstract.EnumTrackPosition.k[i];
        }

        public String getName() {
            return this.m;
        }

        static {
            BlockMinecartTrackAbstract.EnumTrackPosition[] ablockminecarttrackabstract_enumtrackposition = values();
            int i = ablockminecarttrackabstract_enumtrackposition.length;

            for (int j = 0; j < i; ++j) {
                BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = ablockminecarttrackabstract_enumtrackposition[j];

                BlockMinecartTrackAbstract.EnumTrackPosition.k[blockminecarttrackabstract_enumtrackposition.a()] = blockminecarttrackabstract_enumtrackposition;
            }

        }
    }

    public class MinecartTrackLogic {

        private final World b;
        private final BlockPosition c;
        private final BlockMinecartTrackAbstract d;
        private IBlockData e;
        private final boolean f;
        private final List<BlockPosition> g = Lists.newArrayList();

        public MinecartTrackLogic(World world, BlockPosition blockposition, IBlockData iblockdata) {
            this.b = world;
            this.c = blockposition;
            this.e = iblockdata;
            this.d = (BlockMinecartTrackAbstract) iblockdata.getBlock();
            BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(this.d.g());

            this.f = this.d.c;
            this.a(blockminecarttrackabstract_enumtrackposition);
        }

        public List<BlockPosition> a() {
            return this.g;
        }

        private void a(BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition) {
            this.g.clear();
            switch (blockminecarttrackabstract_enumtrackposition) {
            case NORTH_SOUTH:
                this.g.add(this.c.north());
                this.g.add(this.c.south());
                break;

            case EAST_WEST:
                this.g.add(this.c.west());
                this.g.add(this.c.east());
                break;

            case ASCENDING_EAST:
                this.g.add(this.c.west());
                this.g.add(this.c.east().up());
                break;

            case ASCENDING_WEST:
                this.g.add(this.c.west().up());
                this.g.add(this.c.east());
                break;

            case ASCENDING_NORTH:
                this.g.add(this.c.north().up());
                this.g.add(this.c.south());
                break;

            case ASCENDING_SOUTH:
                this.g.add(this.c.north());
                this.g.add(this.c.south().up());
                break;

            case SOUTH_EAST:
                this.g.add(this.c.east());
                this.g.add(this.c.south());
                break;

            case SOUTH_WEST:
                this.g.add(this.c.west());
                this.g.add(this.c.south());
                break;

            case NORTH_WEST:
                this.g.add(this.c.west());
                this.g.add(this.c.north());
                break;

            case NORTH_EAST:
                this.g.add(this.c.east());
                this.g.add(this.c.north());
            }

        }

        private void d() {
            for (int i = 0; i < this.g.size(); ++i) {
                BlockMinecartTrackAbstract.MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic = this.b((BlockPosition) this.g.get(i));

                if (blockminecarttrackabstract_minecarttracklogic != null && blockminecarttrackabstract_minecarttracklogic.a(this)) {
                    this.g.set(i, blockminecarttrackabstract_minecarttracklogic.c);
                } else {
                    this.g.remove(i--);
                }
            }

        }

        private boolean a(BlockPosition blockposition) {
            return BlockMinecartTrackAbstract.b(this.b, blockposition) || BlockMinecartTrackAbstract.b(this.b, blockposition.up()) || BlockMinecartTrackAbstract.b(this.b, blockposition.down());
        }

        @Nullable
        private BlockMinecartTrackAbstract.MinecartTrackLogic b(BlockPosition blockposition) {
            IBlockData iblockdata = this.b.getType(blockposition);

            if (BlockMinecartTrackAbstract.i(iblockdata)) {
                return BlockMinecartTrackAbstract.this.new MinecartTrackLogic(this.b, blockposition, iblockdata);
            } else {
                BlockPosition blockposition1 = blockposition.up();

                iblockdata = this.b.getType(blockposition1);
                if (BlockMinecartTrackAbstract.i(iblockdata)) {
                    return BlockMinecartTrackAbstract.this.new MinecartTrackLogic(this.b, blockposition1, iblockdata);
                } else {
                    blockposition1 = blockposition.down();
                    iblockdata = this.b.getType(blockposition1);
                    return BlockMinecartTrackAbstract.i(iblockdata) ? BlockMinecartTrackAbstract.this.new MinecartTrackLogic(this.b, blockposition1, iblockdata) : null;
                }
            }
        }

        private boolean a(BlockMinecartTrackAbstract.MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic) {
            return this.c(blockminecarttrackabstract_minecarttracklogic.c);
        }

        private boolean c(BlockPosition blockposition) {
            for (int i = 0; i < this.g.size(); ++i) {
                BlockPosition blockposition1 = (BlockPosition) this.g.get(i);

                if (blockposition1.getX() == blockposition.getX() && blockposition1.getZ() == blockposition.getZ()) {
                    return true;
                }
            }

            return false;
        }

        protected int b() {
            int i = 0;
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                if (this.a(this.c.shift(enumdirection))) {
                    ++i;
                }
            }

            return i;
        }

        private boolean b(BlockMinecartTrackAbstract.MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic) {
            return this.a(blockminecarttrackabstract_minecarttracklogic) || this.g.size() != 2;
        }

        private void c(BlockMinecartTrackAbstract.MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic) {
            this.g.add(blockminecarttrackabstract_minecarttracklogic.c);
            BlockPosition blockposition = this.c.north();
            BlockPosition blockposition1 = this.c.south();
            BlockPosition blockposition2 = this.c.west();
            BlockPosition blockposition3 = this.c.east();
            boolean flag = this.c(blockposition);
            boolean flag1 = this.c(blockposition1);
            boolean flag2 = this.c(blockposition2);
            boolean flag3 = this.c(blockposition3);
            BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = null;

            if (flag || flag1) {
                blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;
            }

            if (flag2 || flag3) {
                blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST;
            }

            if (!this.f) {
                if (flag1 && flag3 && !flag && !flag2) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST;
                }

                if (flag1 && flag2 && !flag && !flag3) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST;
                }

                if (flag && flag2 && !flag1 && !flag3) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST;
                }

                if (flag && flag3 && !flag1 && !flag2) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH) {
                if (BlockMinecartTrackAbstract.b(this.b, blockposition.up())) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH;
                }

                if (BlockMinecartTrackAbstract.b(this.b, blockposition1.up())) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST) {
                if (BlockMinecartTrackAbstract.b(this.b, blockposition3.up())) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST;
                }

                if (BlockMinecartTrackAbstract.b(this.b, blockposition2.up())) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == null) {
                blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;
            }

            this.e = this.e.set(this.d.g(), blockminecarttrackabstract_enumtrackposition);
            this.b.setTypeAndData(this.c, this.e, 3);
        }

        private boolean d(BlockPosition blockposition) {
            BlockMinecartTrackAbstract.MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic = this.b(blockposition);

            if (blockminecarttrackabstract_minecarttracklogic == null) {
                return false;
            } else {
                blockminecarttrackabstract_minecarttracklogic.d();
                return blockminecarttrackabstract_minecarttracklogic.b(this);
            }
        }

        public BlockMinecartTrackAbstract.MinecartTrackLogic a(boolean flag, boolean flag1) {
            BlockPosition blockposition = this.c.north();
            BlockPosition blockposition1 = this.c.south();
            BlockPosition blockposition2 = this.c.west();
            BlockPosition blockposition3 = this.c.east();
            boolean flag2 = this.d(blockposition);
            boolean flag3 = this.d(blockposition1);
            boolean flag4 = this.d(blockposition2);
            boolean flag5 = this.d(blockposition3);
            BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = null;

            if ((flag2 || flag3) && !flag4 && !flag5) {
                blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;
            }

            if ((flag4 || flag5) && !flag2 && !flag3) {
                blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST;
            }

            if (!this.f) {
                if (flag3 && flag5 && !flag2 && !flag4) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST;
                }

                if (flag3 && flag4 && !flag2 && !flag5) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST;
                }

                if (flag2 && flag4 && !flag3 && !flag5) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST;
                }

                if (flag2 && flag5 && !flag3 && !flag4) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == null) {
                if (flag2 || flag3) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;
                }

                if (flag4 || flag5) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST;
                }

                if (!this.f) {
                    if (flag) {
                        if (flag3 && flag5) {
                            blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST;
                        }

                        if (flag4 && flag3) {
                            blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST;
                        }

                        if (flag5 && flag2) {
                            blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST;
                        }

                        if (flag2 && flag4) {
                            blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST;
                        }
                    } else {
                        if (flag2 && flag4) {
                            blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST;
                        }

                        if (flag5 && flag2) {
                            blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST;
                        }

                        if (flag4 && flag3) {
                            blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST;
                        }

                        if (flag3 && flag5) {
                            blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST;
                        }
                    }
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH) {
                if (BlockMinecartTrackAbstract.b(this.b, blockposition.up())) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH;
                }

                if (BlockMinecartTrackAbstract.b(this.b, blockposition1.up())) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST) {
                if (BlockMinecartTrackAbstract.b(this.b, blockposition3.up())) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST;
                }

                if (BlockMinecartTrackAbstract.b(this.b, blockposition2.up())) {
                    blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST;
                }
            }

            if (blockminecarttrackabstract_enumtrackposition == null) {
                blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;
            }

            this.a(blockminecarttrackabstract_enumtrackposition);
            this.e = this.e.set(this.d.g(), blockminecarttrackabstract_enumtrackposition);
            if (flag1 || this.b.getType(this.c) != this.e) {
                this.b.setTypeAndData(this.c, this.e, 3);

                for (int i = 0; i < this.g.size(); ++i) {
                    BlockMinecartTrackAbstract.MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic = this.b((BlockPosition) this.g.get(i));

                    if (blockminecarttrackabstract_minecarttracklogic != null) {
                        blockminecarttrackabstract_minecarttracklogic.d();
                        if (blockminecarttrackabstract_minecarttracklogic.b(this)) {
                            blockminecarttrackabstract_minecarttracklogic.c(this);
                        }
                    }
                }
            }

            return this;
        }

        public IBlockData c() {
            return this.e;
        }
    }
}
