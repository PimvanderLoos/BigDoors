package net.minecraft.world.level.portal;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceRecord;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.BlockPortal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.HeightMap;

public class PortalTravelAgent {

    private static final int TICKET_RADIUS = 3;
    private static final int SEARCH_RADIUS = 128;
    private static final int CREATE_RADIUS = 16;
    private static final int FRAME_HEIGHT = 5;
    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_BOX = 3;
    private static final int FRAME_HEIGHT_START = -1;
    private static final int FRAME_HEIGHT_END = 4;
    private static final int FRAME_WIDTH_START = -1;
    private static final int FRAME_WIDTH_END = 3;
    private static final int FRAME_BOX_START = -1;
    private static final int FRAME_BOX_END = 2;
    private static final int NOTHING_FOUND = -1;
    private final WorldServer level;

    public PortalTravelAgent(WorldServer worldserver) {
        this.level = worldserver;
    }

    public Optional<BlockUtil.Rectangle> findPortalAround(BlockPosition blockposition, boolean flag, WorldBorder worldborder) {
        VillagePlace villageplace = this.level.getPoiManager();
        int i = flag ? 16 : 128;

        villageplace.ensureLoadedAndValid(this.level, blockposition, i);
        Optional<VillagePlaceRecord> optional = villageplace.getInSquare((villageplacetype) -> {
            return villageplacetype == VillagePlaceType.NETHER_PORTAL;
        }, blockposition, i, VillagePlace.Occupancy.ANY).filter((villageplacerecord) -> {
            return worldborder.isWithinBounds(villageplacerecord.getPos());
        }).sorted(Comparator.comparingDouble((villageplacerecord) -> {
            return villageplacerecord.getPos().distSqr(blockposition);
        }).thenComparingInt((villageplacerecord) -> {
            return villageplacerecord.getPos().getY();
        })).filter((villageplacerecord) -> {
            return this.level.getBlockState(villageplacerecord.getPos()).hasProperty(BlockProperties.HORIZONTAL_AXIS);
        }).findFirst();

        return optional.map((villageplacerecord) -> {
            BlockPosition blockposition1 = villageplacerecord.getPos();

            this.level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkCoordIntPair(blockposition1), 3, blockposition1);
            IBlockData iblockdata = this.level.getBlockState(blockposition1);

            return BlockUtil.getLargestRectangleAround(blockposition1, (EnumDirection.EnumAxis) iblockdata.getValue(BlockProperties.HORIZONTAL_AXIS), 21, EnumDirection.EnumAxis.Y, 21, (blockposition2) -> {
                return this.level.getBlockState(blockposition2) == iblockdata;
            });
        });
    }

    public Optional<BlockUtil.Rectangle> createPortal(BlockPosition blockposition, EnumDirection.EnumAxis enumdirection_enumaxis) {
        EnumDirection enumdirection = EnumDirection.get(EnumDirection.EnumAxisDirection.POSITIVE, enumdirection_enumaxis);
        double d0 = -1.0D;
        BlockPosition blockposition1 = null;
        double d1 = -1.0D;
        BlockPosition blockposition2 = null;
        WorldBorder worldborder = this.level.getWorldBorder();
        int i = Math.min(this.level.getMaxBuildHeight(), this.level.getMinBuildHeight() + this.level.getLogicalHeight()) - 1;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
        Iterator iterator = BlockPosition.spiralAround(blockposition, 16, EnumDirection.EAST, EnumDirection.SOUTH).iterator();

        int j;
        int k;
        int l;

        while (iterator.hasNext()) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = (BlockPosition.MutableBlockPosition) iterator.next();

            j = Math.min(i, this.level.getHeight(HeightMap.Type.MOTION_BLOCKING, blockposition_mutableblockposition1.getX(), blockposition_mutableblockposition1.getZ()));
            boolean flag = true;

            if (worldborder.isWithinBounds((BlockPosition) blockposition_mutableblockposition1) && worldborder.isWithinBounds((BlockPosition) blockposition_mutableblockposition1.move(enumdirection, 1))) {
                blockposition_mutableblockposition1.move(enumdirection.getOpposite(), 1);

                for (k = j; k >= this.level.getMinBuildHeight(); --k) {
                    blockposition_mutableblockposition1.setY(k);
                    if (this.level.isEmptyBlock(blockposition_mutableblockposition1)) {
                        for (l = k; k > this.level.getMinBuildHeight() && this.level.isEmptyBlock(blockposition_mutableblockposition1.move(EnumDirection.DOWN)); --k) {
                            ;
                        }

                        if (k + 4 <= i) {
                            int i1 = l - k;

                            if (i1 <= 0 || i1 >= 3) {
                                blockposition_mutableblockposition1.setY(k);
                                if (this.canHostFrame(blockposition_mutableblockposition1, blockposition_mutableblockposition, enumdirection, 0)) {
                                    double d2 = blockposition.distSqr(blockposition_mutableblockposition1);

                                    if (this.canHostFrame(blockposition_mutableblockposition1, blockposition_mutableblockposition, enumdirection, -1) && this.canHostFrame(blockposition_mutableblockposition1, blockposition_mutableblockposition, enumdirection, 1) && (d0 == -1.0D || d0 > d2)) {
                                        d0 = d2;
                                        blockposition1 = blockposition_mutableblockposition1.immutable();
                                    }

                                    if (d0 == -1.0D && (d1 == -1.0D || d1 > d2)) {
                                        d1 = d2;
                                        blockposition2 = blockposition_mutableblockposition1.immutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (d0 == -1.0D && d1 != -1.0D) {
            blockposition1 = blockposition2;
            d0 = d1;
        }

        int j1;
        int k1;

        if (d0 == -1.0D) {
            j1 = Math.max(this.level.getMinBuildHeight() - -1, 70);
            k1 = i - 9;
            if (k1 < j1) {
                return Optional.empty();
            }

            blockposition1 = (new BlockPosition(blockposition.getX(), MathHelper.clamp(blockposition.getY(), j1, k1), blockposition.getZ())).immutable();
            EnumDirection enumdirection1 = enumdirection.getClockWise();

            if (!worldborder.isWithinBounds(blockposition1)) {
                return Optional.empty();
            }

            for (int l1 = -1; l1 < 2; ++l1) {
                for (k = 0; k < 2; ++k) {
                    for (l = -1; l < 3; ++l) {
                        IBlockData iblockdata = l < 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();

                        blockposition_mutableblockposition.setWithOffset(blockposition1, k * enumdirection.getStepX() + l1 * enumdirection1.getStepX(), l, k * enumdirection.getStepZ() + l1 * enumdirection1.getStepZ());
                        this.level.setBlockAndUpdate(blockposition_mutableblockposition, iblockdata);
                    }
                }
            }
        }

        for (j1 = -1; j1 < 3; ++j1) {
            for (k1 = -1; k1 < 4; ++k1) {
                if (j1 == -1 || j1 == 2 || k1 == -1 || k1 == 3) {
                    blockposition_mutableblockposition.setWithOffset(blockposition1, j1 * enumdirection.getStepX(), k1, j1 * enumdirection.getStepZ());
                    this.level.setBlock(blockposition_mutableblockposition, Blocks.OBSIDIAN.defaultBlockState(), 3);
                }
            }
        }

        IBlockData iblockdata1 = (IBlockData) Blocks.NETHER_PORTAL.defaultBlockState().setValue(BlockPortal.AXIS, enumdirection_enumaxis);

        for (k1 = 0; k1 < 2; ++k1) {
            for (j = 0; j < 3; ++j) {
                blockposition_mutableblockposition.setWithOffset(blockposition1, k1 * enumdirection.getStepX(), j, k1 * enumdirection.getStepZ());
                this.level.setBlock(blockposition_mutableblockposition, iblockdata1, 18);
            }
        }

        return Optional.of(new BlockUtil.Rectangle(blockposition1.immutable(), 2, 3));
    }

    private boolean canHostFrame(BlockPosition blockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, EnumDirection enumdirection, int i) {
        EnumDirection enumdirection1 = enumdirection.getClockWise();

        for (int j = -1; j < 3; ++j) {
            for (int k = -1; k < 4; ++k) {
                blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection.getStepX() * j + enumdirection1.getStepX() * i, k, enumdirection.getStepZ() * j + enumdirection1.getStepZ() * i);
                if (k < 0 && !this.level.getBlockState(blockposition_mutableblockposition).getMaterial().isSolid()) {
                    return false;
                }

                if (k >= 0 && !this.level.isEmptyBlock(blockposition_mutableblockposition)) {
                    return false;
                }
            }
        }

        return true;
    }
}
