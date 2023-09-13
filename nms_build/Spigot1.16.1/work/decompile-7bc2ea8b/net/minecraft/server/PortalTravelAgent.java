package net.minecraft.server;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class PortalTravelAgent {

    private final WorldServer world;
    private final Random b;

    public PortalTravelAgent(WorldServer worldserver) {
        this.world = worldserver;
        this.b = new Random(worldserver.getSeed());
    }

    public boolean findAndTeleport(Entity entity, float f) {
        Vec3D vec3d = entity.getPortalOffset();
        EnumDirection enumdirection = entity.getPortalDirection();
        ShapeDetector.Shape shapedetector_shape = this.a(entity.getChunkCoordinates(), entity.getMot(), enumdirection, vec3d.x, vec3d.y, entity instanceof EntityHuman);

        if (shapedetector_shape == null) {
            return false;
        } else {
            Vec3D vec3d1 = shapedetector_shape.position;
            Vec3D vec3d2 = shapedetector_shape.velocity;

            entity.setMot(vec3d2);
            entity.yaw = f + (float) shapedetector_shape.yaw;
            entity.teleportAndSync(vec3d1.x, vec3d1.y, vec3d1.z);
            return true;
        }
    }

    @Nullable
    public ShapeDetector.Shape a(BlockPosition blockposition, Vec3D vec3d, EnumDirection enumdirection, double d0, double d1, boolean flag) {
        VillagePlace villageplace = this.world.x();

        villageplace.a(this.world, blockposition, 128);
        List<VillagePlaceRecord> list = (List) villageplace.b((villageplacetype) -> {
            return villageplacetype == VillagePlaceType.v;
        }, blockposition, 128, VillagePlace.Occupancy.ANY).collect(Collectors.toList());
        Optional<VillagePlaceRecord> optional = list.stream().min(Comparator.comparingDouble((villageplacerecord) -> {
            return villageplacerecord.f().j(blockposition);
        }).thenComparingInt((villageplacerecord) -> {
            return villageplacerecord.f().getY();
        }));

        return (ShapeDetector.Shape) optional.map((villageplacerecord) -> {
            BlockPosition blockposition1 = villageplacerecord.f();

            this.world.getChunkProvider().addTicket(TicketType.PORTAL, new ChunkCoordIntPair(blockposition1), 3, blockposition1);
            ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = BlockPortal.c((GeneratorAccess) this.world, blockposition1);

            return shapedetector_shapedetectorcollection.a(enumdirection, blockposition1, d1, vec3d, d0);
        }).orElse((Object) null);
    }

    public boolean createPortal(Entity entity) {
        boolean flag = true;
        double d0 = -1.0D;
        int i = MathHelper.floor(entity.locX());
        int j = MathHelper.floor(entity.locY());
        int k = MathHelper.floor(entity.locZ());
        int l = i;
        int i1 = j;
        int j1 = k;
        int k1 = 0;
        int l1 = this.b.nextInt(4);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        double d1;
        int i2;
        double d2;
        int j2;
        int k2;
        int l2;
        int i3;
        int j3;
        int k3;
        int l3;
        int i4;
        int j4;
        int k4;
        double d3;
        double d4;

        for (i2 = i - 16; i2 <= i + 16; ++i2) {
            d1 = (double) i2 + 0.5D - entity.locX();

            for (j2 = k - 16; j2 <= k + 16; ++j2) {
                d2 = (double) j2 + 0.5D - entity.locZ();

                label257:
                for (k2 = this.world.getHeight() - 1; k2 >= 0; --k2) {
                    if (this.world.isEmpty(blockposition_mutableblockposition.d(i2, k2, j2))) {
                        while (k2 > 0 && this.world.isEmpty(blockposition_mutableblockposition.d(i2, k2 - 1, j2))) {
                            --k2;
                        }

                        for (i3 = l1; i3 < l1 + 4; ++i3) {
                            l2 = i3 % 2;
                            j3 = 1 - l2;
                            if (i3 % 4 >= 2) {
                                l2 = -l2;
                                j3 = -j3;
                            }

                            for (l3 = 0; l3 < 3; ++l3) {
                                for (i4 = 0; i4 < 4; ++i4) {
                                    for (k4 = -1; k4 < 4; ++k4) {
                                        k3 = i2 + (i4 - 1) * l2 + l3 * j3;
                                        j4 = k2 + k4;
                                        int l4 = j2 + (i4 - 1) * j3 - l3 * l2;

                                        blockposition_mutableblockposition.d(k3, j4, l4);
                                        if (k4 < 0 && !this.world.getType(blockposition_mutableblockposition).getMaterial().isBuildable() || k4 >= 0 && !this.world.isEmpty(blockposition_mutableblockposition)) {
                                            continue label257;
                                        }
                                    }
                                }
                            }

                            d3 = (double) k2 + 0.5D - entity.locY();
                            d4 = d1 * d1 + d3 * d3 + d2 * d2;
                            if (d0 < 0.0D || d4 < d0) {
                                d0 = d4;
                                l = i2;
                                i1 = k2;
                                j1 = j2;
                                k1 = i3 % 4;
                            }
                        }
                    }
                }
            }
        }

        if (d0 < 0.0D) {
            for (i2 = i - 16; i2 <= i + 16; ++i2) {
                d1 = (double) i2 + 0.5D - entity.locX();

                for (j2 = k - 16; j2 <= k + 16; ++j2) {
                    d2 = (double) j2 + 0.5D - entity.locZ();

                    label205:
                    for (k2 = this.world.getHeight() - 1; k2 >= 0; --k2) {
                        if (this.world.isEmpty(blockposition_mutableblockposition.d(i2, k2, j2))) {
                            while (k2 > 0 && this.world.isEmpty(blockposition_mutableblockposition.d(i2, k2 - 1, j2))) {
                                --k2;
                            }

                            for (i3 = l1; i3 < l1 + 2; ++i3) {
                                l2 = i3 % 2;
                                j3 = 1 - l2;

                                for (l3 = 0; l3 < 4; ++l3) {
                                    for (i4 = -1; i4 < 4; ++i4) {
                                        k4 = i2 + (l3 - 1) * l2;
                                        k3 = k2 + i4;
                                        j4 = j2 + (l3 - 1) * j3;
                                        blockposition_mutableblockposition.d(k4, k3, j4);
                                        if (i4 < 0 && !this.world.getType(blockposition_mutableblockposition).getMaterial().isBuildable() || i4 >= 0 && !this.world.isEmpty(blockposition_mutableblockposition)) {
                                            continue label205;
                                        }
                                    }
                                }

                                d3 = (double) k2 + 0.5D - entity.locY();
                                d4 = d1 * d1 + d3 * d3 + d2 * d2;
                                if (d0 < 0.0D || d4 < d0) {
                                    d0 = d4;
                                    l = i2;
                                    i1 = k2;
                                    j1 = j2;
                                    k1 = i3 % 2;
                                }
                            }
                        }
                    }
                }
            }
        }

        int i5 = l;
        int j5 = i1;

        j2 = j1;
        int k5 = k1 % 2;
        int l5 = 1 - k5;

        if (k1 % 4 >= 2) {
            k5 = -k5;
            l5 = -l5;
        }

        if (d0 < 0.0D) {
            i1 = MathHelper.clamp(i1, 70, this.world.getHeight() - 10);
            j5 = i1;

            for (k2 = -1; k2 <= 1; ++k2) {
                for (i3 = 1; i3 < 3; ++i3) {
                    for (l2 = -1; l2 < 3; ++l2) {
                        j3 = i5 + (i3 - 1) * k5 + k2 * l5;
                        l3 = j5 + l2;
                        i4 = j2 + (i3 - 1) * l5 - k2 * k5;
                        boolean flag1 = l2 < 0;

                        blockposition_mutableblockposition.d(j3, l3, i4);
                        this.world.setTypeUpdate(blockposition_mutableblockposition, flag1 ? Blocks.OBSIDIAN.getBlockData() : Blocks.AIR.getBlockData());
                    }
                }
            }
        }

        for (k2 = -1; k2 < 3; ++k2) {
            for (i3 = -1; i3 < 4; ++i3) {
                if (k2 == -1 || k2 == 2 || i3 == -1 || i3 == 3) {
                    blockposition_mutableblockposition.d(i5 + k2 * k5, j5 + i3, j2 + k2 * l5);
                    this.world.setTypeAndData(blockposition_mutableblockposition, Blocks.OBSIDIAN.getBlockData(), 3);
                }
            }
        }

        IBlockData iblockdata = (IBlockData) Blocks.NETHER_PORTAL.getBlockData().set(BlockPortal.AXIS, k5 == 0 ? EnumDirection.EnumAxis.Z : EnumDirection.EnumAxis.X);

        for (i3 = 0; i3 < 2; ++i3) {
            for (l2 = 0; l2 < 3; ++l2) {
                blockposition_mutableblockposition.d(i5 + i3 * k5, j5 + l2, j2 + i3 * l5);
                this.world.setTypeAndData(blockposition_mutableblockposition, iblockdata, 18);
            }
        }

        return true;
    }
}
