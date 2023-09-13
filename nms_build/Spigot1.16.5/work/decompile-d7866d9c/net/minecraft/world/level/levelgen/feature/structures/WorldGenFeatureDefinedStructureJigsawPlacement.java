package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.data.worldgen.WorldGenFeaturePieces;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.block.BlockJigsaw;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.WorldGenFeaturePillagerOutpostPoolPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFeatureDefinedStructureJigsawPlacement {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void a(IRegistryCustom iregistrycustom, WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration, WorldGenFeatureDefinedStructureJigsawPlacement.a worldgenfeaturedefinedstructurejigsawplacement_a, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, BlockPosition blockposition, List<? super WorldGenFeaturePillagerOutpostPoolPiece> list, Random random, boolean flag, boolean flag1) {
        StructureGenerator.g();
        IRegistryWritable<WorldGenFeatureDefinedStructurePoolTemplate> iregistrywritable = iregistrycustom.b(IRegistry.ax);
        EnumBlockRotation enumblockrotation = EnumBlockRotation.a(random);
        WorldGenFeatureDefinedStructurePoolTemplate worldgenfeaturedefinedstructurepooltemplate = (WorldGenFeatureDefinedStructurePoolTemplate) worldgenfeaturevillageconfiguration.c().get();
        WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = worldgenfeaturedefinedstructurepooltemplate.a(random);
        WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece = worldgenfeaturedefinedstructurejigsawplacement_a.create(definedstructuremanager, worldgenfeaturedefinedstructurepoolstructure, blockposition, worldgenfeaturedefinedstructurepoolstructure.f(), enumblockrotation, worldgenfeaturedefinedstructurepoolstructure.a(definedstructuremanager, blockposition, enumblockrotation));
        StructureBoundingBox structureboundingbox = worldgenfeaturepillageroutpostpoolpiece.g();
        int i = (structureboundingbox.d + structureboundingbox.a) / 2;
        int j = (structureboundingbox.f + structureboundingbox.c) / 2;
        int k;

        if (flag1) {
            k = blockposition.getY() + chunkgenerator.b(i, j, HeightMap.Type.WORLD_SURFACE_WG);
        } else {
            k = blockposition.getY();
        }

        int l = structureboundingbox.b + worldgenfeaturepillageroutpostpoolpiece.d();

        worldgenfeaturepillageroutpostpoolpiece.a(0, k - l, 0);
        list.add(worldgenfeaturepillageroutpostpoolpiece);
        if (worldgenfeaturevillageconfiguration.b() > 0) {
            boolean flag2 = true;
            AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) (i - 80), (double) (k - 80), (double) (j - 80), (double) (i + 80 + 1), (double) (k + 80 + 1), (double) (j + 80 + 1));
            WorldGenFeatureDefinedStructureJigsawPlacement.c worldgenfeaturedefinedstructurejigsawplacement_c = new WorldGenFeatureDefinedStructureJigsawPlacement.c(iregistrywritable, worldgenfeaturevillageconfiguration.b(), worldgenfeaturedefinedstructurejigsawplacement_a, chunkgenerator, definedstructuremanager, list, random);

            worldgenfeaturedefinedstructurejigsawplacement_c.h.addLast(new WorldGenFeatureDefinedStructureJigsawPlacement.b(worldgenfeaturepillageroutpostpoolpiece, new MutableObject(VoxelShapes.a(VoxelShapes.a(axisalignedbb), VoxelShapes.a(AxisAlignedBB.a(structureboundingbox)), OperatorBoolean.ONLY_FIRST)), k + 80, 0));

            while (!worldgenfeaturedefinedstructurejigsawplacement_c.h.isEmpty()) {
                WorldGenFeatureDefinedStructureJigsawPlacement.b worldgenfeaturedefinedstructurejigsawplacement_b = (WorldGenFeatureDefinedStructureJigsawPlacement.b) worldgenfeaturedefinedstructurejigsawplacement_c.h.removeFirst();

                worldgenfeaturedefinedstructurejigsawplacement_c.a(worldgenfeaturedefinedstructurejigsawplacement_b.a, worldgenfeaturedefinedstructurejigsawplacement_b.b, worldgenfeaturedefinedstructurejigsawplacement_b.c, worldgenfeaturedefinedstructurejigsawplacement_b.d, flag);
            }

        }
    }

    public static void a(IRegistryCustom iregistrycustom, WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, int i, WorldGenFeatureDefinedStructureJigsawPlacement.a worldgenfeaturedefinedstructurejigsawplacement_a, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, List<? super WorldGenFeaturePillagerOutpostPoolPiece> list, Random random) {
        IRegistryWritable<WorldGenFeatureDefinedStructurePoolTemplate> iregistrywritable = iregistrycustom.b(IRegistry.ax);
        WorldGenFeatureDefinedStructureJigsawPlacement.c worldgenfeaturedefinedstructurejigsawplacement_c = new WorldGenFeatureDefinedStructureJigsawPlacement.c(iregistrywritable, i, worldgenfeaturedefinedstructurejigsawplacement_a, chunkgenerator, definedstructuremanager, list, random);

        worldgenfeaturedefinedstructurejigsawplacement_c.h.addLast(new WorldGenFeatureDefinedStructureJigsawPlacement.b(worldgenfeaturepillageroutpostpoolpiece, new MutableObject(VoxelShapes.a), 0, 0));

        while (!worldgenfeaturedefinedstructurejigsawplacement_c.h.isEmpty()) {
            WorldGenFeatureDefinedStructureJigsawPlacement.b worldgenfeaturedefinedstructurejigsawplacement_b = (WorldGenFeatureDefinedStructureJigsawPlacement.b) worldgenfeaturedefinedstructurejigsawplacement_c.h.removeFirst();

            worldgenfeaturedefinedstructurejigsawplacement_c.a(worldgenfeaturedefinedstructurejigsawplacement_b.a, worldgenfeaturedefinedstructurejigsawplacement_b.b, worldgenfeaturedefinedstructurejigsawplacement_b.c, worldgenfeaturedefinedstructurejigsawplacement_b.d, false);
        }

    }

    public interface a {

        WorldGenFeaturePillagerOutpostPoolPiece create(DefinedStructureManager definedstructuremanager, WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure, BlockPosition blockposition, int i, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox);
    }

    static final class c {

        private final IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> a;
        private final int b;
        private final WorldGenFeatureDefinedStructureJigsawPlacement.a c;
        private final ChunkGenerator d;
        private final DefinedStructureManager e;
        private final List<? super WorldGenFeaturePillagerOutpostPoolPiece> f;
        private final Random g;
        private final Deque<WorldGenFeatureDefinedStructureJigsawPlacement.b> h;

        private c(IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> iregistry, int i, WorldGenFeatureDefinedStructureJigsawPlacement.a worldgenfeaturedefinedstructurejigsawplacement_a, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, List<? super WorldGenFeaturePillagerOutpostPoolPiece> list, Random random) {
            this.h = Queues.newArrayDeque();
            this.a = iregistry;
            this.b = i;
            this.c = worldgenfeaturedefinedstructurejigsawplacement_a;
            this.d = chunkgenerator;
            this.e = definedstructuremanager;
            this.f = list;
            this.g = random;
        }

        private void a(WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, MutableObject<VoxelShape> mutableobject, int i, int j, boolean flag) {
            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = worldgenfeaturepillageroutpostpoolpiece.b();
            BlockPosition blockposition = worldgenfeaturepillageroutpostpoolpiece.c();
            EnumBlockRotation enumblockrotation = worldgenfeaturepillageroutpostpoolpiece.ap_();
            WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching = worldgenfeaturedefinedstructurepoolstructure.e();
            boolean flag1 = worldgenfeaturedefinedstructurepooltemplate_matching == WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID;
            MutableObject<VoxelShape> mutableobject1 = new MutableObject();
            StructureBoundingBox structureboundingbox = worldgenfeaturepillageroutpostpoolpiece.g();
            int k = structureboundingbox.b;
            Iterator iterator = worldgenfeaturedefinedstructurepoolstructure.a(this.e, blockposition, enumblockrotation, this.g).iterator();

            label132:
            while (iterator.hasNext()) {
                DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();
                EnumDirection enumdirection = BlockJigsaw.h(definedstructure_blockinfo.b);
                BlockPosition blockposition1 = definedstructure_blockinfo.a;
                BlockPosition blockposition2 = blockposition1.shift(enumdirection);
                int l = blockposition1.getY() - k;
                int i1 = -1;
                MinecraftKey minecraftkey = new MinecraftKey(definedstructure_blockinfo.c.getString("pool"));
                Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional = this.a.getOptional(minecraftkey);

                if (optional.isPresent() && (((WorldGenFeatureDefinedStructurePoolTemplate) optional.get()).c() != 0 || Objects.equals(minecraftkey, WorldGenFeaturePieces.a.a()))) {
                    MinecraftKey minecraftkey1 = ((WorldGenFeatureDefinedStructurePoolTemplate) optional.get()).a();
                    Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional1 = this.a.getOptional(minecraftkey1);

                    if (optional1.isPresent() && (((WorldGenFeatureDefinedStructurePoolTemplate) optional1.get()).c() != 0 || Objects.equals(minecraftkey1, WorldGenFeaturePieces.a.a()))) {
                        boolean flag2 = structureboundingbox.b((BaseBlockPosition) blockposition2);
                        MutableObject mutableobject2;
                        int j1;

                        if (flag2) {
                            mutableobject2 = mutableobject1;
                            j1 = k;
                            if (mutableobject1.getValue() == null) {
                                mutableobject1.setValue(VoxelShapes.a(AxisAlignedBB.a(structureboundingbox)));
                            }
                        } else {
                            mutableobject2 = mutableobject;
                            j1 = i;
                        }

                        List<WorldGenFeatureDefinedStructurePoolStructure> list = Lists.newArrayList();

                        if (j != this.b) {
                            list.addAll(((WorldGenFeatureDefinedStructurePoolTemplate) optional.get()).b(this.g));
                        }

                        list.addAll(((WorldGenFeatureDefinedStructurePoolTemplate) optional1.get()).b(this.g));
                        Iterator iterator1 = list.iterator();

                        while (iterator1.hasNext()) {
                            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure1 = (WorldGenFeatureDefinedStructurePoolStructure) iterator1.next();

                            if (worldgenfeaturedefinedstructurepoolstructure1 == WorldGenFeatureDefinedStructurePoolEmpty.b) {
                                break;
                            }

                            Iterator iterator2 = EnumBlockRotation.b(this.g).iterator();

                            while (iterator2.hasNext()) {
                                EnumBlockRotation enumblockrotation1 = (EnumBlockRotation) iterator2.next();
                                List<DefinedStructure.BlockInfo> list1 = worldgenfeaturedefinedstructurepoolstructure1.a(this.e, BlockPosition.ZERO, enumblockrotation1, this.g);
                                StructureBoundingBox structureboundingbox1 = worldgenfeaturedefinedstructurepoolstructure1.a(this.e, BlockPosition.ZERO, enumblockrotation1);
                                int k1;

                                if (flag && structureboundingbox1.e() <= 16) {
                                    k1 = list1.stream().mapToInt((definedstructure_blockinfo1) -> {
                                        if (!structureboundingbox1.b((BaseBlockPosition) definedstructure_blockinfo1.a.shift(BlockJigsaw.h(definedstructure_blockinfo1.b)))) {
                                            return 0;
                                        } else {
                                            MinecraftKey minecraftkey2 = new MinecraftKey(definedstructure_blockinfo1.c.getString("pool"));
                                            Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional2 = this.a.getOptional(minecraftkey2);
                                            Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional3 = optional2.flatMap((worldgenfeaturedefinedstructurepooltemplate) -> {
                                                return this.a.getOptional(worldgenfeaturedefinedstructurepooltemplate.a());
                                            });
                                            int l1 = (Integer) optional2.map((worldgenfeaturedefinedstructurepooltemplate) -> {
                                                return worldgenfeaturedefinedstructurepooltemplate.a(this.e);
                                            }).orElse(0);
                                            int i2 = (Integer) optional3.map((worldgenfeaturedefinedstructurepooltemplate) -> {
                                                return worldgenfeaturedefinedstructurepooltemplate.a(this.e);
                                            }).orElse(0);

                                            return Math.max(l1, i2);
                                        }
                                    }).max().orElse(0);
                                } else {
                                    k1 = 0;
                                }

                                Iterator iterator3 = list1.iterator();

                                while (iterator3.hasNext()) {
                                    DefinedStructure.BlockInfo definedstructure_blockinfo1 = (DefinedStructure.BlockInfo) iterator3.next();

                                    if (BlockJigsaw.a(definedstructure_blockinfo, definedstructure_blockinfo1)) {
                                        BlockPosition blockposition3 = definedstructure_blockinfo1.a;
                                        BlockPosition blockposition4 = new BlockPosition(blockposition2.getX() - blockposition3.getX(), blockposition2.getY() - blockposition3.getY(), blockposition2.getZ() - blockposition3.getZ());
                                        StructureBoundingBox structureboundingbox2 = worldgenfeaturedefinedstructurepoolstructure1.a(this.e, blockposition4, enumblockrotation1);
                                        int l1 = structureboundingbox2.b;
                                        WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching1 = worldgenfeaturedefinedstructurepoolstructure1.e();
                                        boolean flag3 = worldgenfeaturedefinedstructurepooltemplate_matching1 == WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID;
                                        int i2 = blockposition3.getY();
                                        int j2 = l - i2 + BlockJigsaw.h(definedstructure_blockinfo.b).getAdjacentY();
                                        int k2;

                                        if (flag1 && flag3) {
                                            k2 = k + j2;
                                        } else {
                                            if (i1 == -1) {
                                                i1 = this.d.b(blockposition1.getX(), blockposition1.getZ(), HeightMap.Type.WORLD_SURFACE_WG);
                                            }

                                            k2 = i1 - i2;
                                        }

                                        int l2 = k2 - l1;
                                        StructureBoundingBox structureboundingbox3 = structureboundingbox2.b(0, l2, 0);
                                        BlockPosition blockposition5 = blockposition4.b(0, l2, 0);
                                        int i3;

                                        if (k1 > 0) {
                                            i3 = Math.max(k1 + 1, structureboundingbox3.e - structureboundingbox3.b);
                                            structureboundingbox3.e = structureboundingbox3.b + i3;
                                        }

                                        if (!VoxelShapes.c((VoxelShape) mutableobject2.getValue(), VoxelShapes.a(AxisAlignedBB.a(structureboundingbox3).shrink(0.25D)), OperatorBoolean.ONLY_SECOND)) {
                                            mutableobject2.setValue(VoxelShapes.b((VoxelShape) mutableobject2.getValue(), VoxelShapes.a(AxisAlignedBB.a(structureboundingbox3)), OperatorBoolean.ONLY_FIRST));
                                            i3 = worldgenfeaturepillageroutpostpoolpiece.d();
                                            int j3;

                                            if (flag3) {
                                                j3 = i3 - j2;
                                            } else {
                                                j3 = worldgenfeaturedefinedstructurepoolstructure1.f();
                                            }

                                            WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece1 = this.c.create(this.e, worldgenfeaturedefinedstructurepoolstructure1, blockposition5, j3, enumblockrotation1, structureboundingbox3);
                                            int k3;

                                            if (flag1) {
                                                k3 = k + l;
                                            } else if (flag3) {
                                                k3 = k2 + i2;
                                            } else {
                                                if (i1 == -1) {
                                                    i1 = this.d.b(blockposition1.getX(), blockposition1.getZ(), HeightMap.Type.WORLD_SURFACE_WG);
                                                }

                                                k3 = i1 + j2 / 2;
                                            }

                                            worldgenfeaturepillageroutpostpoolpiece.a(new WorldGenFeatureDefinedStructureJigsawJunction(blockposition2.getX(), k3 - l + i3, blockposition2.getZ(), j2, worldgenfeaturedefinedstructurepooltemplate_matching1));
                                            worldgenfeaturepillageroutpostpoolpiece1.a(new WorldGenFeatureDefinedStructureJigsawJunction(blockposition1.getX(), k3 - i2 + j3, blockposition1.getZ(), -j2, worldgenfeaturedefinedstructurepooltemplate_matching));
                                            this.f.add(worldgenfeaturepillageroutpostpoolpiece1);
                                            if (j + 1 <= this.b) {
                                                this.h.addLast(new WorldGenFeatureDefinedStructureJigsawPlacement.b(worldgenfeaturepillageroutpostpoolpiece1, mutableobject2, j1, j + 1));
                                            }
                                            continue label132;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        WorldGenFeatureDefinedStructureJigsawPlacement.LOGGER.warn("Empty or none existent fallback pool: {}", minecraftkey1);
                    }
                } else {
                    WorldGenFeatureDefinedStructureJigsawPlacement.LOGGER.warn("Empty or none existent pool: {}", minecraftkey);
                }
            }

        }
    }

    static final class b {

        private final WorldGenFeaturePillagerOutpostPoolPiece a;
        private final MutableObject<VoxelShape> b;
        private final int c;
        private final int d;

        private b(WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, MutableObject<VoxelShape> mutableobject, int i, int j) {
            this.a = worldgenfeaturepillageroutpostpoolpiece;
            this.b = mutableobject;
            this.c = i;
            this.d = j;
        }
    }
}
