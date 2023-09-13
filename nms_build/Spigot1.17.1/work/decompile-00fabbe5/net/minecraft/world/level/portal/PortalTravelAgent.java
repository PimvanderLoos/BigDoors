package net.minecraft.world.level.portal;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BaseBlockPosition;
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

    public Optional<BlockUtil.Rectangle> findPortal(BlockPosition blockposition, boolean flag) {
        VillagePlace villageplace = this.level.A();
        int i = flag ? 16 : 128;

        villageplace.a(this.level, blockposition, i);
        Optional<VillagePlaceRecord> optional = villageplace.b((villageplacetype) -> {
            return villageplacetype == VillagePlaceType.NETHER_PORTAL;
        }, blockposition, i, VillagePlace.Occupancy.ANY).sorted(Comparator.comparingDouble((villageplacerecord) -> {
            return villageplacerecord.f().j(blockposition);
        }).thenComparingInt((villageplacerecord) -> {
            return villageplacerecord.f().getY();
        })).filter((villageplacerecord) -> {
            return this.level.getType(villageplacerecord.f()).b(BlockProperties.HORIZONTAL_AXIS);
        }).findFirst();

        return optional.map((villageplacerecord) -> {
            BlockPosition blockposition1 = villageplacerecord.f();

            this.level.getChunkProvider().addTicket(TicketType.PORTAL, new ChunkCoordIntPair(blockposition1), 3, blockposition1);
            IBlockData iblockdata = this.level.getType(blockposition1);

            return BlockUtil.a(blockposition1, (EnumDirection.EnumAxis) iblockdata.get(BlockProperties.HORIZONTAL_AXIS), 21, EnumDirection.EnumAxis.Y, 21, (blockposition2) -> {
                return this.level.getType(blockposition2) == iblockdata;
            });
        });
    }

    public Optional<BlockUtil.Rectangle> createPortal(BlockPosition blockposition, EnumDirection.EnumAxis enumdirection_enumaxis) {
        EnumDirection enumdirection = EnumDirection.a(EnumDirection.EnumAxisDirection.POSITIVE, enumdirection_enumaxis);
        double d0 = -1.0D;
        BlockPosition blockposition1 = null;
        double d1 = -1.0D;
        BlockPosition blockposition2 = null;
        WorldBorder worldborder = this.level.getWorldBorder();
        int i = Math.min(this.level.getMaxBuildHeight(), this.level.getMinBuildHeight() + this.level.getLogicalHeight()) - 1;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        Iterator iterator = BlockPosition.a(blockposition, 16, EnumDirection.EAST, EnumDirection.SOUTH).iterator();

        int j;
        int k;
        int l;

        while (iterator.hasNext()) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = (BlockPosition.MutableBlockPosition) iterator.next();

            j = Math.min(i, this.level.a(HeightMap.Type.MOTION_BLOCKING, blockposition_mutableblockposition1.getX(), blockposition_mutableblockposition1.getZ()));
            boolean flag = true;

            if (worldborder.a((BlockPosition) blockposition_mutableblockposition1) && worldborder.a((BlockPosition) blockposition_mutableblockposition1.c(enumdirection, 1))) {
                blockposition_mutableblockposition1.c(enumdirection.opposite(), 1);

                for (k = j; k >= this.level.getMinBuildHeight(); --k) {
                    blockposition_mutableblockposition1.t(k);
                    if (this.level.isEmpty(blockposition_mutableblockposition1)) {
                        for (l = k; k > this.level.getMinBuildHeight() && this.level.isEmpty(blockposition_mutableblockposition1.c(EnumDirection.DOWN)); --k) {
                            ;
                        }

                        if (k + 4 <= i) {
                            int i1 = l - k;

                            if (i1 <= 0 || i1 >= 3) {
                                blockposition_mutableblockposition1.t(k);
                                if (this.a(blockposition_mutableblockposition1, blockposition_mutableblockposition, enumdirection, 0)) {
                                    double d2 = blockposition.j(blockposition_mutableblockposition1);

                                    if (this.a(blockposition_mutableblockposition1, blockposition_mutableblockposition, enumdirection, -1) && this.a(blockposition_mutableblockposition1, blockposition_mutableblockposition, enumdirection, 1) && (d0 == -1.0D || d0 > d2)) {
                                        d0 = d2;
                                        blockposition1 = blockposition_mutableblockposition1.immutableCopy();
                                    }

                                    if (d0 == -1.0D && (d1 == -1.0D || d1 > d2)) {
                                        d1 = d2;
                                        blockposition2 = blockposition_mutableblockposition1.immutableCopy();
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

            blockposition1 = (new BlockPosition(blockposition.getX(), MathHelper.clamp(blockposition.getY(), j1, k1), blockposition.getZ())).immutableCopy();
            EnumDirection enumdirection1 = enumdirection.g();

            if (!worldborder.a(blockposition1)) {
                return Optional.empty();
            }

            for (int l1 = -1; l1 < 2; ++l1) {
                for (k = 0; k < 2; ++k) {
                    for (l = -1; l < 3; ++l) {
                        IBlockData iblockdata = l < 0 ? Blocks.OBSIDIAN.getBlockData() : Blocks.AIR.getBlockData();

                        blockposition_mutableblockposition.a((BaseBlockPosition) blockposition1, k * enumdirection.getAdjacentX() + l1 * enumdirection1.getAdjacentX(), l, k * enumdirection.getAdjacentZ() + l1 * enumdirection1.getAdjacentZ());
                        this.level.setTypeUpdate(blockposition_mutableblockposition, iblockdata);
                    }
                }
            }
        }

        for (j1 = -1; j1 < 3; ++j1) {
            for (k1 = -1; k1 < 4; ++k1) {
                if (j1 == -1 || j1 == 2 || k1 == -1 || k1 == 3) {
                    blockposition_mutableblockposition.a((BaseBlockPosition) blockposition1, j1 * enumdirection.getAdjacentX(), k1, j1 * enumdirection.getAdjacentZ());
                    this.level.setTypeAndData(blockposition_mutableblockposition, Blocks.OBSIDIAN.getBlockData(), 3);
                }
            }
        }

        IBlockData iblockdata1 = (IBlockData) Blocks.NETHER_PORTAL.getBlockData().set(BlockPortal.AXIS, enumdirection_enumaxis);

        for (k1 = 0; k1 < 2; ++k1) {
            for (j = 0; j < 3; ++j) {
                blockposition_mutableblockposition.a((BaseBlockPosition) blockposition1, k1 * enumdirection.getAdjacentX(), j, k1 * enumdirection.getAdjacentZ());
                this.level.setTypeAndData(blockposition_mutableblockposition, iblockdata1, 18);
            }
        }

        return Optional.of(new BlockUtil.Rectangle(blockposition1.immutableCopy(), 2, 3));
    }

    private boolean a(BlockPosition blockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, EnumDirection enumdirection, int i) {
        EnumDirection enumdirection1 = enumdirection.g();

        for (int j = -1; j < 3; ++j) {
            for (int k = -1; k < 4; ++k) {
                blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection.getAdjacentX() * j + enumdirection1.getAdjacentX() * i, k, enumdirection.getAdjacentZ() * j + enumdirection1.getAdjacentZ() * i);
                if (k < 0 && !this.level.getType(blockposition_mutableblockposition).getMaterial().isBuildable()) {
                    return false;
                }

                if (k >= 0 && !this.level.isEmpty(blockposition_mutableblockposition)) {
                    return false;
                }
            }
        }

        return true;
    }
}
