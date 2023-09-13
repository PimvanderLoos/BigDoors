package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyTrackPosition;

public class MinecartTrackLogic {

    private final World level;
    private final BlockPosition pos;
    private final BlockMinecartTrackAbstract block;
    private IBlockData state;
    private final boolean isStraight;
    private final List<BlockPosition> connections = Lists.newArrayList();

    public MinecartTrackLogic(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.level = world;
        this.pos = blockposition;
        this.state = iblockdata;
        this.block = (BlockMinecartTrackAbstract) iblockdata.getBlock();
        BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.getValue(this.block.getShapeProperty());

        this.isStraight = this.block.isStraight();
        this.updateConnections(blockpropertytrackposition);
    }

    public List<BlockPosition> getConnections() {
        return this.connections;
    }

    private void updateConnections(BlockPropertyTrackPosition blockpropertytrackposition) {
        this.connections.clear();
        switch (blockpropertytrackposition) {
            case NORTH_SOUTH:
                this.connections.add(this.pos.north());
                this.connections.add(this.pos.south());
                break;
            case EAST_WEST:
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.east());
                break;
            case ASCENDING_EAST:
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.east().above());
                break;
            case ASCENDING_WEST:
                this.connections.add(this.pos.west().above());
                this.connections.add(this.pos.east());
                break;
            case ASCENDING_NORTH:
                this.connections.add(this.pos.north().above());
                this.connections.add(this.pos.south());
                break;
            case ASCENDING_SOUTH:
                this.connections.add(this.pos.north());
                this.connections.add(this.pos.south().above());
                break;
            case SOUTH_EAST:
                this.connections.add(this.pos.east());
                this.connections.add(this.pos.south());
                break;
            case SOUTH_WEST:
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.south());
                break;
            case NORTH_WEST:
                this.connections.add(this.pos.west());
                this.connections.add(this.pos.north());
                break;
            case NORTH_EAST:
                this.connections.add(this.pos.east());
                this.connections.add(this.pos.north());
        }

    }

    private void removeSoftConnections() {
        for (int i = 0; i < this.connections.size(); ++i) {
            MinecartTrackLogic minecarttracklogic = this.getRail((BlockPosition) this.connections.get(i));

            if (minecarttracklogic != null && minecarttracklogic.connectsTo(this)) {
                this.connections.set(i, minecarttracklogic.pos);
            } else {
                this.connections.remove(i--);
            }
        }

    }

    private boolean hasRail(BlockPosition blockposition) {
        return BlockMinecartTrackAbstract.isRail(this.level, blockposition) || BlockMinecartTrackAbstract.isRail(this.level, blockposition.above()) || BlockMinecartTrackAbstract.isRail(this.level, blockposition.below());
    }

    @Nullable
    private MinecartTrackLogic getRail(BlockPosition blockposition) {
        IBlockData iblockdata = this.level.getBlockState(blockposition);

        if (BlockMinecartTrackAbstract.isRail(iblockdata)) {
            return new MinecartTrackLogic(this.level, blockposition, iblockdata);
        } else {
            BlockPosition blockposition1 = blockposition.above();

            iblockdata = this.level.getBlockState(blockposition1);
            if (BlockMinecartTrackAbstract.isRail(iblockdata)) {
                return new MinecartTrackLogic(this.level, blockposition1, iblockdata);
            } else {
                blockposition1 = blockposition.below();
                iblockdata = this.level.getBlockState(blockposition1);
                return BlockMinecartTrackAbstract.isRail(iblockdata) ? new MinecartTrackLogic(this.level, blockposition1, iblockdata) : null;
            }
        }
    }

    private boolean connectsTo(MinecartTrackLogic minecarttracklogic) {
        return this.hasConnection(minecarttracklogic.pos);
    }

    private boolean hasConnection(BlockPosition blockposition) {
        for (int i = 0; i < this.connections.size(); ++i) {
            BlockPosition blockposition1 = (BlockPosition) this.connections.get(i);

            if (blockposition1.getX() == blockposition.getX() && blockposition1.getZ() == blockposition.getZ()) {
                return true;
            }
        }

        return false;
    }

    protected int countPotentialConnections() {
        int i = 0;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            if (this.hasRail(this.pos.relative(enumdirection))) {
                ++i;
            }
        }

        return i;
    }

    private boolean canConnectTo(MinecartTrackLogic minecarttracklogic) {
        return this.connectsTo(minecarttracklogic) || this.connections.size() != 2;
    }

    private void connectTo(MinecartTrackLogic minecarttracklogic) {
        this.connections.add(minecarttracklogic.pos);
        BlockPosition blockposition = this.pos.north();
        BlockPosition blockposition1 = this.pos.south();
        BlockPosition blockposition2 = this.pos.west();
        BlockPosition blockposition3 = this.pos.east();
        boolean flag = this.hasConnection(blockposition);
        boolean flag1 = this.hasConnection(blockposition1);
        boolean flag2 = this.hasConnection(blockposition2);
        boolean flag3 = this.hasConnection(blockposition3);
        BlockPropertyTrackPosition blockpropertytrackposition = null;

        if (flag || flag1) {
            blockpropertytrackposition = BlockPropertyTrackPosition.NORTH_SOUTH;
        }

        if (flag2 || flag3) {
            blockpropertytrackposition = BlockPropertyTrackPosition.EAST_WEST;
        }

        if (!this.isStraight) {
            if (flag1 && flag3 && !flag && !flag2) {
                blockpropertytrackposition = BlockPropertyTrackPosition.SOUTH_EAST;
            }

            if (flag1 && flag2 && !flag && !flag3) {
                blockpropertytrackposition = BlockPropertyTrackPosition.SOUTH_WEST;
            }

            if (flag && flag2 && !flag1 && !flag3) {
                blockpropertytrackposition = BlockPropertyTrackPosition.NORTH_WEST;
            }

            if (flag && flag3 && !flag1 && !flag2) {
                blockpropertytrackposition = BlockPropertyTrackPosition.NORTH_EAST;
            }
        }

        if (blockpropertytrackposition == BlockPropertyTrackPosition.NORTH_SOUTH) {
            if (BlockMinecartTrackAbstract.isRail(this.level, blockposition.above())) {
                blockpropertytrackposition = BlockPropertyTrackPosition.ASCENDING_NORTH;
            }

            if (BlockMinecartTrackAbstract.isRail(this.level, blockposition1.above())) {
                blockpropertytrackposition = BlockPropertyTrackPosition.ASCENDING_SOUTH;
            }
        }

        if (blockpropertytrackposition == BlockPropertyTrackPosition.EAST_WEST) {
            if (BlockMinecartTrackAbstract.isRail(this.level, blockposition3.above())) {
                blockpropertytrackposition = BlockPropertyTrackPosition.ASCENDING_EAST;
            }

            if (BlockMinecartTrackAbstract.isRail(this.level, blockposition2.above())) {
                blockpropertytrackposition = BlockPropertyTrackPosition.ASCENDING_WEST;
            }
        }

        if (blockpropertytrackposition == null) {
            blockpropertytrackposition = BlockPropertyTrackPosition.NORTH_SOUTH;
        }

        this.state = (IBlockData) this.state.setValue(this.block.getShapeProperty(), blockpropertytrackposition);
        this.level.setBlock(this.pos, this.state, 3);
    }

    private boolean hasNeighborRail(BlockPosition blockposition) {
        MinecartTrackLogic minecarttracklogic = this.getRail(blockposition);

        if (minecarttracklogic == null) {
            return false;
        } else {
            minecarttracklogic.removeSoftConnections();
            return minecarttracklogic.canConnectTo(this);
        }
    }

    public MinecartTrackLogic place(boolean flag, boolean flag1, BlockPropertyTrackPosition blockpropertytrackposition) {
        BlockPosition blockposition = this.pos.north();
        BlockPosition blockposition1 = this.pos.south();
        BlockPosition blockposition2 = this.pos.west();
        BlockPosition blockposition3 = this.pos.east();
        boolean flag2 = this.hasNeighborRail(blockposition);
        boolean flag3 = this.hasNeighborRail(blockposition1);
        boolean flag4 = this.hasNeighborRail(blockposition2);
        boolean flag5 = this.hasNeighborRail(blockposition3);
        BlockPropertyTrackPosition blockpropertytrackposition1 = null;
        boolean flag6 = flag2 || flag3;
        boolean flag7 = flag4 || flag5;

        if (flag6 && !flag7) {
            blockpropertytrackposition1 = BlockPropertyTrackPosition.NORTH_SOUTH;
        }

        if (flag7 && !flag6) {
            blockpropertytrackposition1 = BlockPropertyTrackPosition.EAST_WEST;
        }

        boolean flag8 = flag3 && flag5;
        boolean flag9 = flag3 && flag4;
        boolean flag10 = flag2 && flag5;
        boolean flag11 = flag2 && flag4;

        if (!this.isStraight) {
            if (flag8 && !flag2 && !flag4) {
                blockpropertytrackposition1 = BlockPropertyTrackPosition.SOUTH_EAST;
            }

            if (flag9 && !flag2 && !flag5) {
                blockpropertytrackposition1 = BlockPropertyTrackPosition.SOUTH_WEST;
            }

            if (flag11 && !flag3 && !flag5) {
                blockpropertytrackposition1 = BlockPropertyTrackPosition.NORTH_WEST;
            }

            if (flag10 && !flag3 && !flag4) {
                blockpropertytrackposition1 = BlockPropertyTrackPosition.NORTH_EAST;
            }
        }

        if (blockpropertytrackposition1 == null) {
            if (flag6 && flag7) {
                blockpropertytrackposition1 = blockpropertytrackposition;
            } else if (flag6) {
                blockpropertytrackposition1 = BlockPropertyTrackPosition.NORTH_SOUTH;
            } else if (flag7) {
                blockpropertytrackposition1 = BlockPropertyTrackPosition.EAST_WEST;
            }

            if (!this.isStraight) {
                if (flag) {
                    if (flag8) {
                        blockpropertytrackposition1 = BlockPropertyTrackPosition.SOUTH_EAST;
                    }

                    if (flag9) {
                        blockpropertytrackposition1 = BlockPropertyTrackPosition.SOUTH_WEST;
                    }

                    if (flag10) {
                        blockpropertytrackposition1 = BlockPropertyTrackPosition.NORTH_EAST;
                    }

                    if (flag11) {
                        blockpropertytrackposition1 = BlockPropertyTrackPosition.NORTH_WEST;
                    }
                } else {
                    if (flag11) {
                        blockpropertytrackposition1 = BlockPropertyTrackPosition.NORTH_WEST;
                    }

                    if (flag10) {
                        blockpropertytrackposition1 = BlockPropertyTrackPosition.NORTH_EAST;
                    }

                    if (flag9) {
                        blockpropertytrackposition1 = BlockPropertyTrackPosition.SOUTH_WEST;
                    }

                    if (flag8) {
                        blockpropertytrackposition1 = BlockPropertyTrackPosition.SOUTH_EAST;
                    }
                }
            }
        }

        if (blockpropertytrackposition1 == BlockPropertyTrackPosition.NORTH_SOUTH) {
            if (BlockMinecartTrackAbstract.isRail(this.level, blockposition.above())) {
                blockpropertytrackposition1 = BlockPropertyTrackPosition.ASCENDING_NORTH;
            }

            if (BlockMinecartTrackAbstract.isRail(this.level, blockposition1.above())) {
                blockpropertytrackposition1 = BlockPropertyTrackPosition.ASCENDING_SOUTH;
            }
        }

        if (blockpropertytrackposition1 == BlockPropertyTrackPosition.EAST_WEST) {
            if (BlockMinecartTrackAbstract.isRail(this.level, blockposition3.above())) {
                blockpropertytrackposition1 = BlockPropertyTrackPosition.ASCENDING_EAST;
            }

            if (BlockMinecartTrackAbstract.isRail(this.level, blockposition2.above())) {
                blockpropertytrackposition1 = BlockPropertyTrackPosition.ASCENDING_WEST;
            }
        }

        if (blockpropertytrackposition1 == null) {
            blockpropertytrackposition1 = blockpropertytrackposition;
        }

        this.updateConnections(blockpropertytrackposition1);
        this.state = (IBlockData) this.state.setValue(this.block.getShapeProperty(), blockpropertytrackposition1);
        if (flag1 || this.level.getBlockState(this.pos) != this.state) {
            this.level.setBlock(this.pos, this.state, 3);

            for (int i = 0; i < this.connections.size(); ++i) {
                MinecartTrackLogic minecarttracklogic = this.getRail((BlockPosition) this.connections.get(i));

                if (minecarttracklogic != null) {
                    minecarttracklogic.removeSoftConnections();
                    if (minecarttracklogic.canConnectTo(this)) {
                        minecarttracklogic.connectTo(this);
                    }
                }
            }
        }

        return this;
    }

    public IBlockData getState() {
        return this.state;
    }
}
