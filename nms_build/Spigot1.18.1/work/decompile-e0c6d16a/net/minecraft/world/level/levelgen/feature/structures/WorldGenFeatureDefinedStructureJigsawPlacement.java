package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.QuartPos;
import net.minecraft.data.worldgen.WorldGenFeaturePieces;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.BlockJigsaw;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.WorldGenFeaturePillagerOutpostPoolPiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
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

    public static Optional<PieceGenerator<WorldGenFeatureVillageConfiguration>> addPieces(PieceGeneratorSupplier.a<WorldGenFeatureVillageConfiguration> piecegeneratorsupplier_a, WorldGenFeatureDefinedStructureJigsawPlacement.a worldgenfeaturedefinedstructurejigsawplacement_a, BlockPosition blockposition, boolean flag, boolean flag1) {
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureSeed(piecegeneratorsupplier_a.seed(), piecegeneratorsupplier_a.chunkPos().x, piecegeneratorsupplier_a.chunkPos().z);
        IRegistryCustom iregistrycustom = piecegeneratorsupplier_a.registryAccess();
        WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration = (WorldGenFeatureVillageConfiguration) piecegeneratorsupplier_a.config();
        ChunkGenerator chunkgenerator = piecegeneratorsupplier_a.chunkGenerator();
        DefinedStructureManager definedstructuremanager = piecegeneratorsupplier_a.structureManager();
        LevelHeightAccessor levelheightaccessor = piecegeneratorsupplier_a.heightAccessor();
        Predicate<BiomeBase> predicate = piecegeneratorsupplier_a.validBiome();

        StructureGenerator.bootstrap();
        IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> iregistry = iregistrycustom.registryOrThrow(IRegistry.TEMPLATE_POOL_REGISTRY);
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(seededrandom);
        WorldGenFeatureDefinedStructurePoolTemplate worldgenfeaturedefinedstructurepooltemplate = (WorldGenFeatureDefinedStructurePoolTemplate) worldgenfeaturevillageconfiguration.startPool().get();
        WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = worldgenfeaturedefinedstructurepooltemplate.getRandomTemplate(seededrandom);

        if (worldgenfeaturedefinedstructurepoolstructure == WorldGenFeatureDefinedStructurePoolEmpty.INSTANCE) {
            return Optional.empty();
        } else {
            WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece = worldgenfeaturedefinedstructurejigsawplacement_a.create(definedstructuremanager, worldgenfeaturedefinedstructurepoolstructure, blockposition, worldgenfeaturedefinedstructurepoolstructure.getGroundLevelDelta(), enumblockrotation, worldgenfeaturedefinedstructurepoolstructure.getBoundingBox(definedstructuremanager, blockposition, enumblockrotation));
            StructureBoundingBox structureboundingbox = worldgenfeaturepillageroutpostpoolpiece.getBoundingBox();
            int i = (structureboundingbox.maxX() + structureboundingbox.minX()) / 2;
            int j = (structureboundingbox.maxZ() + structureboundingbox.minZ()) / 2;
            int k;

            if (flag1) {
                k = blockposition.getY() + chunkgenerator.getFirstFreeHeight(i, j, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
            } else {
                k = blockposition.getY();
            }

            if (!predicate.test(chunkgenerator.getNoiseBiome(QuartPos.fromBlock(i), QuartPos.fromBlock(k), QuartPos.fromBlock(j)))) {
                return Optional.empty();
            } else {
                int l = structureboundingbox.minY() + worldgenfeaturepillageroutpostpoolpiece.getGroundLevelDelta();

                worldgenfeaturepillageroutpostpoolpiece.move(0, k - l, 0);
                return Optional.of((structurepiecesbuilder, piecegenerator_a) -> {
                    List<WorldGenFeaturePillagerOutpostPoolPiece> list = Lists.newArrayList();

                    list.add(worldgenfeaturepillageroutpostpoolpiece);
                    if (worldgenfeaturevillageconfiguration.maxDepth() > 0) {
                        boolean flag2 = true;
                        AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) (i - 80), (double) (k - 80), (double) (j - 80), (double) (i + 80 + 1), (double) (k + 80 + 1), (double) (j + 80 + 1));
                        WorldGenFeatureDefinedStructureJigsawPlacement.c worldgenfeaturedefinedstructurejigsawplacement_c = new WorldGenFeatureDefinedStructureJigsawPlacement.c(iregistry, worldgenfeaturevillageconfiguration.maxDepth(), worldgenfeaturedefinedstructurejigsawplacement_a, chunkgenerator, definedstructuremanager, list, seededrandom);

                        worldgenfeaturedefinedstructurejigsawplacement_c.placing.addLast(new WorldGenFeatureDefinedStructureJigsawPlacement.b(worldgenfeaturepillageroutpostpoolpiece, new MutableObject(VoxelShapes.join(VoxelShapes.create(axisalignedbb), VoxelShapes.create(AxisAlignedBB.of(structureboundingbox)), OperatorBoolean.ONLY_FIRST)), 0));

                        while (!worldgenfeaturedefinedstructurejigsawplacement_c.placing.isEmpty()) {
                            WorldGenFeatureDefinedStructureJigsawPlacement.b worldgenfeaturedefinedstructurejigsawplacement_b = (WorldGenFeatureDefinedStructureJigsawPlacement.b) worldgenfeaturedefinedstructurejigsawplacement_c.placing.removeFirst();

                            worldgenfeaturedefinedstructurejigsawplacement_c.tryPlacingChildren(worldgenfeaturedefinedstructurejigsawplacement_b.piece, worldgenfeaturedefinedstructurejigsawplacement_b.free, worldgenfeaturedefinedstructurejigsawplacement_b.depth, flag, levelheightaccessor);
                        }

                        Objects.requireNonNull(structurepiecesbuilder);
                        list.forEach(structurepiecesbuilder::addPiece);
                    }
                });
            }
        }
    }

    public static void addPieces(IRegistryCustom iregistrycustom, WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, int i, WorldGenFeatureDefinedStructureJigsawPlacement.a worldgenfeaturedefinedstructurejigsawplacement_a, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, List<? super WorldGenFeaturePillagerOutpostPoolPiece> list, Random random, LevelHeightAccessor levelheightaccessor) {
        IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> iregistry = iregistrycustom.registryOrThrow(IRegistry.TEMPLATE_POOL_REGISTRY);
        WorldGenFeatureDefinedStructureJigsawPlacement.c worldgenfeaturedefinedstructurejigsawplacement_c = new WorldGenFeatureDefinedStructureJigsawPlacement.c(iregistry, i, worldgenfeaturedefinedstructurejigsawplacement_a, chunkgenerator, definedstructuremanager, list, random);

        worldgenfeaturedefinedstructurejigsawplacement_c.placing.addLast(new WorldGenFeatureDefinedStructureJigsawPlacement.b(worldgenfeaturepillageroutpostpoolpiece, new MutableObject(VoxelShapes.INFINITY), 0));

        while (!worldgenfeaturedefinedstructurejigsawplacement_c.placing.isEmpty()) {
            WorldGenFeatureDefinedStructureJigsawPlacement.b worldgenfeaturedefinedstructurejigsawplacement_b = (WorldGenFeatureDefinedStructureJigsawPlacement.b) worldgenfeaturedefinedstructurejigsawplacement_c.placing.removeFirst();

            worldgenfeaturedefinedstructurejigsawplacement_c.tryPlacingChildren(worldgenfeaturedefinedstructurejigsawplacement_b.piece, worldgenfeaturedefinedstructurejigsawplacement_b.free, worldgenfeaturedefinedstructurejigsawplacement_b.depth, false, levelheightaccessor);
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

        void tryPlacingChildren(WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, MutableObject<VoxelShape> mutableobject, int i, boolean flag, LevelHeightAccessor levelheightaccessor) {
            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = worldgenfeaturepillageroutpostpoolpiece.getElement();
            BlockPosition blockposition = worldgenfeaturepillageroutpostpoolpiece.getPosition();
            EnumBlockRotation enumblockrotation = worldgenfeaturepillageroutpostpoolpiece.getRotation();
            WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching = worldgenfeaturedefinedstructurepoolstructure.getProjection();
            boolean flag1 = worldgenfeaturedefinedstructurepooltemplate_matching == WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID;
            MutableObject<VoxelShape> mutableobject1 = new MutableObject();
            StructureBoundingBox structureboundingbox = worldgenfeaturepillageroutpostpoolpiece.getBoundingBox();
            int j = structureboundingbox.minY();
            Iterator iterator = worldgenfeaturedefinedstructurepoolstructure.getShuffledJigsawBlocks(this.structureManager, blockposition, enumblockrotation, this.random).iterator();

            label132:
            while (iterator.hasNext()) {
                DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();
                EnumDirection enumdirection = BlockJigsaw.getFrontFacing(definedstructure_blockinfo.state);
                BlockPosition blockposition1 = definedstructure_blockinfo.pos;
                BlockPosition blockposition2 = blockposition1.relative(enumdirection);
                int k = blockposition1.getY() - j;
                int l = -1;
                MinecraftKey minecraftkey = new MinecraftKey(definedstructure_blockinfo.nbt.getString("pool"));
                Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional = this.pools.getOptional(minecraftkey);

                if (optional.isPresent() && (((WorldGenFeatureDefinedStructurePoolTemplate) optional.get()).size() != 0 || Objects.equals(minecraftkey, WorldGenFeaturePieces.EMPTY.location()))) {
                    MinecraftKey minecraftkey1 = ((WorldGenFeatureDefinedStructurePoolTemplate) optional.get()).getFallback();
                    Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional1 = this.pools.getOptional(minecraftkey1);

                    if (optional1.isPresent() && (((WorldGenFeatureDefinedStructurePoolTemplate) optional1.get()).size() != 0 || Objects.equals(minecraftkey1, WorldGenFeaturePieces.EMPTY.location()))) {
                        boolean flag2 = structureboundingbox.isInside(blockposition2);
                        MutableObject mutableobject2;

                        if (flag2) {
                            mutableobject2 = mutableobject1;
                            if (mutableobject1.getValue() == null) {
                                mutableobject1.setValue(VoxelShapes.create(AxisAlignedBB.of(structureboundingbox)));
                            }
                        } else {
                            mutableobject2 = mutableobject;
                        }

                        List<WorldGenFeatureDefinedStructurePoolStructure> list = Lists.newArrayList();

                        if (i != this.maxDepth) {
                            list.addAll(((WorldGenFeatureDefinedStructurePoolTemplate) optional.get()).getShuffledTemplates(this.random));
                        }

                        list.addAll(((WorldGenFeatureDefinedStructurePoolTemplate) optional1.get()).getShuffledTemplates(this.random));
                        Iterator iterator1 = list.iterator();

                        while (iterator1.hasNext()) {
                            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure1 = (WorldGenFeatureDefinedStructurePoolStructure) iterator1.next();

                            if (worldgenfeaturedefinedstructurepoolstructure1 == WorldGenFeatureDefinedStructurePoolEmpty.INSTANCE) {
                                break;
                            }

                            Iterator iterator2 = EnumBlockRotation.getShuffled(this.random).iterator();

                            while (iterator2.hasNext()) {
                                EnumBlockRotation enumblockrotation1 = (EnumBlockRotation) iterator2.next();
                                List<DefinedStructure.BlockInfo> list1 = worldgenfeaturedefinedstructurepoolstructure1.getShuffledJigsawBlocks(this.structureManager, BlockPosition.ZERO, enumblockrotation1, this.random);
                                StructureBoundingBox structureboundingbox1 = worldgenfeaturedefinedstructurepoolstructure1.getBoundingBox(this.structureManager, BlockPosition.ZERO, enumblockrotation1);
                                int i1;

                                if (flag && structureboundingbox1.getYSpan() <= 16) {
                                    i1 = list1.stream().mapToInt((definedstructure_blockinfo1) -> {
                                        if (!structureboundingbox1.isInside(definedstructure_blockinfo1.pos.relative(BlockJigsaw.getFrontFacing(definedstructure_blockinfo1.state)))) {
                                            return 0;
                                        } else {
                                            MinecraftKey minecraftkey2 = new MinecraftKey(definedstructure_blockinfo1.nbt.getString("pool"));
                                            Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional2 = this.pools.getOptional(minecraftkey2);
                                            Optional<WorldGenFeatureDefinedStructurePoolTemplate> optional3 = optional2.flatMap((worldgenfeaturedefinedstructurepooltemplate) -> {
                                                return this.pools.getOptional(worldgenfeaturedefinedstructurepooltemplate.getFallback());
                                            });
                                            int j1 = (Integer) optional2.map((worldgenfeaturedefinedstructurepooltemplate) -> {
                                                return worldgenfeaturedefinedstructurepooltemplate.getMaxSize(this.structureManager);
                                            }).orElse(0);
                                            int k1 = (Integer) optional3.map((worldgenfeaturedefinedstructurepooltemplate) -> {
                                                return worldgenfeaturedefinedstructurepooltemplate.getMaxSize(this.structureManager);
                                            }).orElse(0);

                                            return Math.max(j1, k1);
                                        }
                                    }).max().orElse(0);
                                } else {
                                    i1 = 0;
                                }

                                Iterator iterator3 = list1.iterator();

                                while (iterator3.hasNext()) {
                                    DefinedStructure.BlockInfo definedstructure_blockinfo1 = (DefinedStructure.BlockInfo) iterator3.next();

                                    if (BlockJigsaw.canAttach(definedstructure_blockinfo, definedstructure_blockinfo1)) {
                                        BlockPosition blockposition3 = definedstructure_blockinfo1.pos;
                                        BlockPosition blockposition4 = blockposition2.subtract(blockposition3);
                                        StructureBoundingBox structureboundingbox2 = worldgenfeaturedefinedstructurepoolstructure1.getBoundingBox(this.structureManager, blockposition4, enumblockrotation1);
                                        int j1 = structureboundingbox2.minY();
                                        WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching1 = worldgenfeaturedefinedstructurepoolstructure1.getProjection();
                                        boolean flag3 = worldgenfeaturedefinedstructurepooltemplate_matching1 == WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID;
                                        int k1 = blockposition3.getY();
                                        int l1 = k - k1 + BlockJigsaw.getFrontFacing(definedstructure_blockinfo.state).getStepY();
                                        int i2;

                                        if (flag1 && flag3) {
                                            i2 = j + l1;
                                        } else {
                                            if (l == -1) {
                                                l = this.chunkGenerator.getFirstFreeHeight(blockposition1.getX(), blockposition1.getZ(), HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
                                            }

                                            i2 = l - k1;
                                        }

                                        int j2 = i2 - j1;
                                        StructureBoundingBox structureboundingbox3 = structureboundingbox2.moved(0, j2, 0);
                                        BlockPosition blockposition5 = blockposition4.offset(0, j2, 0);
                                        int k2;

                                        if (i1 > 0) {
                                            k2 = Math.max(i1 + 1, structureboundingbox3.maxY() - structureboundingbox3.minY());
                                            structureboundingbox3.encapsulate(new BlockPosition(structureboundingbox3.minX(), structureboundingbox3.minY() + k2, structureboundingbox3.minZ()));
                                        }

                                        if (!VoxelShapes.joinIsNotEmpty((VoxelShape) mutableobject2.getValue(), VoxelShapes.create(AxisAlignedBB.of(structureboundingbox3).deflate(0.25D)), OperatorBoolean.ONLY_SECOND)) {
                                            mutableobject2.setValue(VoxelShapes.joinUnoptimized((VoxelShape) mutableobject2.getValue(), VoxelShapes.create(AxisAlignedBB.of(structureboundingbox3)), OperatorBoolean.ONLY_FIRST));
                                            k2 = worldgenfeaturepillageroutpostpoolpiece.getGroundLevelDelta();
                                            int l2;

                                            if (flag3) {
                                                l2 = k2 - l1;
                                            } else {
                                                l2 = worldgenfeaturedefinedstructurepoolstructure1.getGroundLevelDelta();
                                            }

                                            WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece1 = this.factory.create(this.structureManager, worldgenfeaturedefinedstructurepoolstructure1, blockposition5, l2, enumblockrotation1, structureboundingbox3);
                                            int i3;

                                            if (flag1) {
                                                i3 = j + k;
                                            } else if (flag3) {
                                                i3 = i2 + k1;
                                            } else {
                                                if (l == -1) {
                                                    l = this.chunkGenerator.getFirstFreeHeight(blockposition1.getX(), blockposition1.getZ(), HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
                                                }

                                                i3 = l + l1 / 2;
                                            }

                                            worldgenfeaturepillageroutpostpoolpiece.addJunction(new WorldGenFeatureDefinedStructureJigsawJunction(blockposition2.getX(), i3 - k + k2, blockposition2.getZ(), l1, worldgenfeaturedefinedstructurepooltemplate_matching1));
                                            worldgenfeaturepillageroutpostpoolpiece1.addJunction(new WorldGenFeatureDefinedStructureJigsawJunction(blockposition1.getX(), i3 - k1 + l2, blockposition1.getZ(), -l1, worldgenfeaturedefinedstructurepooltemplate_matching));
                                            this.pieces.add(worldgenfeaturepillageroutpostpoolpiece1);
                                            if (i + 1 <= this.maxDepth) {
                                                this.placing.addLast(new WorldGenFeatureDefinedStructureJigsawPlacement.b(worldgenfeaturepillageroutpostpoolpiece1, mutableobject2, i + 1));
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
        final int depth;

        b(WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, MutableObject<VoxelShape> mutableobject, int i) {
            this.piece = worldgenfeaturepillageroutpostpoolpiece;
            this.free = mutableobject;
            this.depth = i;
        }
    }
}
