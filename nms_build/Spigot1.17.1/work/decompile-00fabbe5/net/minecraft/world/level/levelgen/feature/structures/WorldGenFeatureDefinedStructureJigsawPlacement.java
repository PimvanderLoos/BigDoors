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
import net.minecraft.data.worldgen.WorldGenFeaturePieces;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.BlockJigsaw;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
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

    static final Logger LOGGER = LogManager.getLogger();

    public WorldGenFeatureDefinedStructureJigsawPlacement() {}

    public static void a(IRegistryCustom iregistrycustom, WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration, WorldGenFeatureDefinedStructureJigsawPlacement.a worldgenfeaturedefinedstructurejigsawplacement_a, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, BlockPosition blockposition, StructurePieceAccessor structurepieceaccessor, Random random, boolean flag, boolean flag1, LevelHeightAccessor levelheightaccessor) {
        StructureGenerator.e();
        List<WorldGenFeaturePillagerOutpostPoolPiece> list = Lists.newArrayList();
        IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> iregistry = iregistrycustom.d(IRegistry.TEMPLATE_POOL_REGISTRY);
        EnumBlockRotation enumblockrotation = EnumBlockRotation.a(random);
        WorldGenFeatureDefinedStructurePoolTemplate worldgenfeaturedefinedstructurepooltemplate = (WorldGenFeatureDefinedStructurePoolTemplate) worldgenfeaturevillageconfiguration.c().get();
        WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = worldgenfeaturedefinedstructurepooltemplate.a(random);

        if (worldgenfeaturedefinedstructurepoolstructure != WorldGenFeatureDefinedStructurePoolEmpty.INSTANCE) {
            WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece = worldgenfeaturedefinedstructurejigsawplacement_a.create(definedstructuremanager, worldgenfeaturedefinedstructurepoolstructure, blockposition, worldgenfeaturedefinedstructurepoolstructure.f(), enumblockrotation, worldgenfeaturedefinedstructurepoolstructure.a(definedstructuremanager, blockposition, enumblockrotation));
            StructureBoundingBox structureboundingbox = worldgenfeaturepillageroutpostpoolpiece.f();
            int i = (structureboundingbox.j() + structureboundingbox.g()) / 2;
            int j = (structureboundingbox.l() + structureboundingbox.i()) / 2;
            int k;

            if (flag1) {
                k = blockposition.getY() + chunkgenerator.b(i, j, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
            } else {
                k = blockposition.getY();
            }

            int l = structureboundingbox.h() + worldgenfeaturepillageroutpostpoolpiece.d();

            worldgenfeaturepillageroutpostpoolpiece.a(0, k - l, 0);
            list.add(worldgenfeaturepillageroutpostpoolpiece);
            if (worldgenfeaturevillageconfiguration.b() > 0) {
                boolean flag2 = true;
                AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) (i - 80), (double) (k - 80), (double) (j - 80), (double) (i + 80 + 1), (double) (k + 80 + 1), (double) (j + 80 + 1));
                WorldGenFeatureDefinedStructureJigsawPlacement.c worldgenfeaturedefinedstructurejigsawplacement_c = new WorldGenFeatureDefinedStructureJigsawPlacement.c(iregistry, worldgenfeaturevillageconfiguration.b(), worldgenfeaturedefinedstructurejigsawplacement_a, chunkgenerator, definedstructuremanager, list, random);

                worldgenfeaturedefinedstructurejigsawplacement_c.placing.addLast(new WorldGenFeatureDefinedStructureJigsawPlacement.b(worldgenfeaturepillageroutpostpoolpiece, new MutableObject(VoxelShapes.a(VoxelShapes.a(axisalignedbb), VoxelShapes.a(AxisAlignedBB.a(structureboundingbox)), OperatorBoolean.ONLY_FIRST)), k + 80, 0));

                while (!worldgenfeaturedefinedstructurejigsawplacement_c.placing.isEmpty()) {
                    WorldGenFeatureDefinedStructureJigsawPlacement.b worldgenfeaturedefinedstructurejigsawplacement_b = (WorldGenFeatureDefinedStructureJigsawPlacement.b) worldgenfeaturedefinedstructurejigsawplacement_c.placing.removeFirst();

                    worldgenfeaturedefinedstructurejigsawplacement_c.a(worldgenfeaturedefinedstructurejigsawplacement_b.piece, worldgenfeaturedefinedstructurejigsawplacement_b.free, worldgenfeaturedefinedstructurejigsawplacement_b.boundsTop, worldgenfeaturedefinedstructurejigsawplacement_b.depth, flag, levelheightaccessor);
                }

                Objects.requireNonNull(structurepieceaccessor);
                list.forEach(structurepieceaccessor::a);
            }
        }
    }

    public static void a(IRegistryCustom iregistrycustom, WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, int i, WorldGenFeatureDefinedStructureJigsawPlacement.a worldgenfeaturedefinedstructurejigsawplacement_a, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, List<? super WorldGenFeaturePillagerOutpostPoolPiece> list, Random random, LevelHeightAccessor levelheightaccessor) {
        IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> iregistry = iregistrycustom.d(IRegistry.TEMPLATE_POOL_REGISTRY);
        WorldGenFeatureDefinedStructureJigsawPlacement.c worldgenfeaturedefinedstructurejigsawplacement_c = new WorldGenFeatureDefinedStructureJigsawPlacement.c(iregistry, i, worldgenfeaturedefinedstructurejigsawplacement_a, chunkgenerator, definedstructuremanager, list, random);

        worldgenfeaturedefinedstructurejigsawplacement_c.placing.addLast(new WorldGenFeatureDefinedStructureJigsawPlacement.b(worldgenfeaturepillageroutpostpoolpiece, new MutableObject(VoxelShapes.INFINITY), 0, 0));

        while (!worldgenfeaturedefinedstructurejigsawplacement_c.placing.isEmpty()) {
            WorldGenFeatureDefinedStructureJigsawPlacement.b worldgenfeaturedefinedstructurejigsawplacement_b = (WorldGenFeatureDefinedStructureJigsawPlacement.b) worldgenfeaturedefinedstructurejigsawplacement_c.placing.removeFirst();

            worldgenfeaturedefinedstructurejigsawplacement_c.a(worldgenfeaturedefinedstructurejigsawplacement_b.piece, worldgenfeaturedefinedstructurejigsawplacement_b.free, worldgenfeaturedefinedstructurejigsawplacement_b.boundsTop, worldgenfeaturedefinedstructurejigsawplacement_b.depth, false, levelheightaccessor);
        }

    }

    public interface a {

        WorldGenFeaturePillagerOutpostPoolPiece create(DefinedStructureManager definedstructuremanager, WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure, BlockPosition blockposition, int i, EnumBlockRotation enumblockrotation, StructureBoundingBox structureboundingbox);
    }

    private static final class c {

        private final IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> pools;
        private final int maxDepth;
        private final WorldGenFeatureDefinedStructureJigsawPlacement.a factory;
        private final ChunkGenerator chunkGenerator;
        private final DefinedStructureManager structureManager;
        private final List<? super WorldGenFeaturePillagerOutpostPoolPiece> pieces;
        private final Random random;
        final Deque<WorldGenFeatureDefinedStructureJigsawPlacement.b> placing = Queues.newArrayDeque();

        c(IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> iregistry, int i, WorldGenFeatureDefinedStructureJigsawPlacement.a worldgenfeaturedefinedstructurejigsawplacement_a, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, List<? super WorldGenFeaturePillagerOutpostPoolPiece> list, Random random) {
            this.pools = iregistry;
            this.maxDepth = i;
            this.factory = worldgenfeaturedefinedstructurejigsawplacement_a;
            this.chunkGenerator = chunkgenerator;
            this.structureManager = definedstructuremanager;
            this.pieces = list;
            this.random = random;
        }

        void a(WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, MutableObject<VoxelShape> mutableobject, int i, int j, boolean flag, LevelHeightAccessor levelheightaccessor) {
            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = worldgenfeaturepillageroutpostpoolpiece.b();
            BlockPosition blockposition = worldgenfeaturepillageroutpostpoolpiece.c();
            EnumBlockRotation enumblockrotation = worldgenfeaturepillageroutpostpoolpiece.ac_();
            WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching = worldgenfeaturedefinedstructurepoolstructure.e();
            boolean flag1 = worldgenfeaturedefinedstructurepooltemplate_matching == WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID;
            MutableObject<VoxelShape> mutableobject1 = new MutableObject();
            StructureBoundingBox structureboundingbox = worldgenfeaturepillageroutpostpoolpiece.f();
            int k = structureboundingbox.h();
            Iterator iterator = worldgenfeaturedefinedstructurepoolstructure.a(this.structureManager, blockposition, enumblockrotation, this.random).iterator();

            label132:
            while (iterator.hasNext()) {
                DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();
                EnumDirection enumdirection = BlockJigsaw.h(definedstructure_blockinfo.state);
                BlockPosition blockposition1 = definedstructure_blockinfo.pos;
                BlockPosition blockposition2 = blockposition1.shift(enumdirection);
                int l = blockposition1.getY() - k;
                int i1 = -1;
                MinecraftKey minecraftkey = new MinecraftKey(definedstructure_blockinfo.nbt.getString("pool"));
                Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional = this.pools.getOptional(minecraftkey);

                if (optional.isPresent() && (((WorldGenFeatureDefinedStructurePoolTemplate) optional.get()).c() != 0 || Objects.equals(minecraftkey, WorldGenFeaturePieces.EMPTY.a()))) {
                    MinecraftKey minecraftkey1 = ((WorldGenFeatureDefinedStructurePoolTemplate) optional.get()).a();
                    Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional1 = this.pools.getOptional(minecraftkey1);

                    if (optional1.isPresent() && (((WorldGenFeatureDefinedStructurePoolTemplate) optional1.get()).c() != 0 || Objects.equals(minecraftkey1, WorldGenFeaturePieces.EMPTY.a()))) {
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

                        if (j != this.maxDepth) {
                            list.addAll(((WorldGenFeatureDefinedStructurePoolTemplate) optional.get()).b(this.random));
                        }

                        list.addAll(((WorldGenFeatureDefinedStructurePoolTemplate) optional1.get()).b(this.random));
                        Iterator iterator1 = list.iterator();

                        while (iterator1.hasNext()) {
                            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure1 = (WorldGenFeatureDefinedStructurePoolStructure) iterator1.next();

                            if (worldgenfeaturedefinedstructurepoolstructure1 == WorldGenFeatureDefinedStructurePoolEmpty.INSTANCE) {
                                break;
                            }

                            Iterator iterator2 = EnumBlockRotation.b(this.random).iterator();

                            while (iterator2.hasNext()) {
                                EnumBlockRotation enumblockrotation1 = (EnumBlockRotation) iterator2.next();
                                List<DefinedStructure.BlockInfo> list1 = worldgenfeaturedefinedstructurepoolstructure1.a(this.structureManager, BlockPosition.ZERO, enumblockrotation1, this.random);
                                StructureBoundingBox structureboundingbox1 = worldgenfeaturedefinedstructurepoolstructure1.a(this.structureManager, BlockPosition.ZERO, enumblockrotation1);
                                int k1;

                                if (flag && structureboundingbox1.d() <= 16) {
                                    k1 = list1.stream().mapToInt((definedstructure_blockinfo1) -> {
                                        if (!structureboundingbox1.b((BaseBlockPosition) definedstructure_blockinfo1.pos.shift(BlockJigsaw.h(definedstructure_blockinfo1.state)))) {
                                            return 0;
                                        } else {
                                            MinecraftKey minecraftkey2 = new MinecraftKey(definedstructure_blockinfo1.nbt.getString("pool"));
                                            Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional2 = this.pools.getOptional(minecraftkey2);
                                            Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional3 = optional2.flatMap((worldgenfeaturedefinedstructurepooltemplate) -> {
                                                return this.pools.getOptional(worldgenfeaturedefinedstructurepooltemplate.a());
                                            });
                                            int l1 = (Integer) optional2.map((worldgenfeaturedefinedstructurepooltemplate) -> {
                                                return worldgenfeaturedefinedstructurepooltemplate.a(this.structureManager);
                                            }).orElse(0);
                                            int i2 = (Integer) optional3.map((worldgenfeaturedefinedstructurepooltemplate) -> {
                                                return worldgenfeaturedefinedstructurepooltemplate.a(this.structureManager);
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
                                        BlockPosition blockposition3 = definedstructure_blockinfo1.pos;
                                        BlockPosition blockposition4 = blockposition2.e(blockposition3);
                                        StructureBoundingBox structureboundingbox2 = worldgenfeaturedefinedstructurepoolstructure1.a(this.structureManager, blockposition4, enumblockrotation1);
                                        int l1 = structureboundingbox2.h();
                                        WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching1 = worldgenfeaturedefinedstructurepoolstructure1.e();
                                        boolean flag3 = worldgenfeaturedefinedstructurepooltemplate_matching1 == WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID;
                                        int i2 = blockposition3.getY();
                                        int j2 = l - i2 + BlockJigsaw.h(definedstructure_blockinfo.state).getAdjacentY();
                                        int k2;

                                        if (flag1 && flag3) {
                                            k2 = k + j2;
                                        } else {
                                            if (i1 == -1) {
                                                i1 = this.chunkGenerator.b(blockposition1.getX(), blockposition1.getZ(), HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
                                            }

                                            k2 = i1 - i2;
                                        }

                                        int l2 = k2 - l1;
                                        StructureBoundingBox structureboundingbox3 = structureboundingbox2.b(0, l2, 0);
                                        BlockPosition blockposition5 = blockposition4.c(0, l2, 0);
                                        int i3;

                                        if (k1 > 0) {
                                            i3 = Math.max(k1 + 1, structureboundingbox3.k() - structureboundingbox3.h());
                                            structureboundingbox3.a(new BlockPosition(structureboundingbox3.g(), structureboundingbox3.h() + i3, structureboundingbox3.i()));
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

                                            WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece1 = this.factory.create(this.structureManager, worldgenfeaturedefinedstructurepoolstructure1, blockposition5, j3, enumblockrotation1, structureboundingbox3);
                                            int k3;

                                            if (flag1) {
                                                k3 = k + l;
                                            } else if (flag3) {
                                                k3 = k2 + i2;
                                            } else {
                                                if (i1 == -1) {
                                                    i1 = this.chunkGenerator.b(blockposition1.getX(), blockposition1.getZ(), HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
                                                }

                                                k3 = i1 + j2 / 2;
                                            }

                                            worldgenfeaturepillageroutpostpoolpiece.a(new WorldGenFeatureDefinedStructureJigsawJunction(blockposition2.getX(), k3 - l + i3, blockposition2.getZ(), j2, worldgenfeaturedefinedstructurepooltemplate_matching1));
                                            worldgenfeaturepillageroutpostpoolpiece1.a(new WorldGenFeatureDefinedStructureJigsawJunction(blockposition1.getX(), k3 - i2 + j3, blockposition1.getZ(), -j2, worldgenfeaturedefinedstructurepooltemplate_matching));
                                            this.pieces.add(worldgenfeaturepillageroutpostpoolpiece1);
                                            if (j + 1 <= this.maxDepth) {
                                                this.placing.addLast(new WorldGenFeatureDefinedStructureJigsawPlacement.b(worldgenfeaturepillageroutpostpoolpiece1, mutableobject2, j1, j + 1));
                                            }
                                            continue label132;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        WorldGenFeatureDefinedStructureJigsawPlacement.LOGGER.warn("Empty or non-existent fallback pool: {}", minecraftkey1);
                    }
                } else {
                    WorldGenFeatureDefinedStructureJigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", minecraftkey);
                }
            }

        }
    }

    private static final class b {

        final WorldGenFeaturePillagerOutpostPoolPiece piece;
        final MutableObject<VoxelShape> free;
        final int boundsTop;
        final int depth;

        b(WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, MutableObject<VoxelShape> mutableobject, int i, int j) {
            this.piece = worldgenfeaturepillageroutpostpoolpiece;
            this.free = mutableobject;
            this.boundsTop = i;
            this.depth = j;
        }
    }
}
