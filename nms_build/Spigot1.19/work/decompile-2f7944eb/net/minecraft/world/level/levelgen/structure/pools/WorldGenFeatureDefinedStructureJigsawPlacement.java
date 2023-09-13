package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.data.worldgen.WorldGenFeaturePieces;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.BlockJigsaw;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.WorldGenFeaturePillagerOutpostPoolPiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class WorldGenFeatureDefinedStructureJigsawPlacement {

    static final Logger LOGGER = LogUtils.getLogger();

    public WorldGenFeatureDefinedStructureJigsawPlacement() {}

    public static Optional<Structure.b> addPieces(Structure.a structure_a, Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder, Optional<MinecraftKey> optional, int i, BlockPosition blockposition, boolean flag, Optional<HeightMap.Type> optional1, int j) {
        IRegistryCustom iregistrycustom = structure_a.registryAccess();
        ChunkGenerator chunkgenerator = structure_a.chunkGenerator();
        StructureTemplateManager structuretemplatemanager = structure_a.structureTemplateManager();
        LevelHeightAccessor levelheightaccessor = structure_a.heightAccessor();
        SeededRandom seededrandom = structure_a.random();
        IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> iregistry = iregistrycustom.registryOrThrow(IRegistry.TEMPLATE_POOL_REGISTRY);
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(seededrandom);
        WorldGenFeatureDefinedStructurePoolTemplate worldgenfeaturedefinedstructurepooltemplate = (WorldGenFeatureDefinedStructurePoolTemplate) holder.value();
        WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = worldgenfeaturedefinedstructurepooltemplate.getRandomTemplate(seededrandom);

        if (worldgenfeaturedefinedstructurepoolstructure == WorldGenFeatureDefinedStructurePoolEmpty.INSTANCE) {
            return Optional.empty();
        } else {
            BlockPosition blockposition1;

            if (optional.isPresent()) {
                MinecraftKey minecraftkey = (MinecraftKey) optional.get();
                Optional<BlockPosition> optional2 = getRandomNamedJigsaw(worldgenfeaturedefinedstructurepoolstructure, minecraftkey, blockposition, enumblockrotation, structuretemplatemanager, seededrandom);

                if (optional2.isEmpty()) {
                    WorldGenFeatureDefinedStructureJigsawPlacement.LOGGER.error("No starting jigsaw {} found in start pool {}", minecraftkey, ((ResourceKey) holder.unwrapKey().get()).location());
                    return Optional.empty();
                }

                blockposition1 = (BlockPosition) optional2.get();
            } else {
                blockposition1 = blockposition;
            }

            BlockPosition blockposition2 = blockposition1.subtract(blockposition);
            BlockPosition blockposition3 = blockposition.subtract(blockposition2);
            WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece = new WorldGenFeaturePillagerOutpostPoolPiece(structuretemplatemanager, worldgenfeaturedefinedstructurepoolstructure, blockposition3, worldgenfeaturedefinedstructurepoolstructure.getGroundLevelDelta(), enumblockrotation, worldgenfeaturedefinedstructurepoolstructure.getBoundingBox(structuretemplatemanager, blockposition3, enumblockrotation));
            StructureBoundingBox structureboundingbox = worldgenfeaturepillageroutpostpoolpiece.getBoundingBox();
            int k = (structureboundingbox.maxX() + structureboundingbox.minX()) / 2;
            int l = (structureboundingbox.maxZ() + structureboundingbox.minZ()) / 2;
            int i1;

            if (optional1.isPresent()) {
                i1 = blockposition.getY() + chunkgenerator.getFirstFreeHeight(k, l, (HeightMap.Type) optional1.get(), levelheightaccessor, structure_a.randomState());
            } else {
                i1 = blockposition3.getY();
            }

            int j1 = structureboundingbox.minY() + worldgenfeaturepillageroutpostpoolpiece.getGroundLevelDelta();

            worldgenfeaturepillageroutpostpoolpiece.move(0, i1 - j1, 0);
            int k1 = i1 + blockposition2.getY();

            return Optional.of(new Structure.b(new BlockPosition(k, k1, l), (structurepiecesbuilder) -> {
                List<WorldGenFeaturePillagerOutpostPoolPiece> list = Lists.newArrayList();

                list.add(worldgenfeaturepillageroutpostpoolpiece);
                if (i > 0) {
                    AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) (k - j), (double) (k1 - j), (double) (l - j), (double) (k + j + 1), (double) (k1 + j + 1), (double) (l + j + 1));
                    VoxelShape voxelshape = VoxelShapes.join(VoxelShapes.create(axisalignedbb), VoxelShapes.create(AxisAlignedBB.of(structureboundingbox)), OperatorBoolean.ONLY_FIRST);

                    addPieces(structure_a.randomState(), i, flag, chunkgenerator, structuretemplatemanager, levelheightaccessor, seededrandom, iregistry, worldgenfeaturepillageroutpostpoolpiece, list, voxelshape);
                    Objects.requireNonNull(structurepiecesbuilder);
                    list.forEach(structurepiecesbuilder::addPiece);
                }
            }));
        }
    }

    private static Optional<BlockPosition> getRandomNamedJigsaw(WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation, StructureTemplateManager structuretemplatemanager, SeededRandom seededrandom) {
        List<DefinedStructure.BlockInfo> list = worldgenfeaturedefinedstructurepoolstructure.getShuffledJigsawBlocks(structuretemplatemanager, blockposition, enumblockrotation, seededrandom);
        Optional<BlockPosition> optional = Optional.empty();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();
            MinecraftKey minecraftkey1 = MinecraftKey.tryParse(definedstructure_blockinfo.nbt.getString("name"));

            if (minecraftkey.equals(minecraftkey1)) {
                optional = Optional.of(definedstructure_blockinfo.pos);
                break;
            }
        }

        return optional;
    }

    private static void addPieces(RandomState randomstate, int i, boolean flag, ChunkGenerator chunkgenerator, StructureTemplateManager structuretemplatemanager, LevelHeightAccessor levelheightaccessor, RandomSource randomsource, IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> iregistry, WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, List<WorldGenFeaturePillagerOutpostPoolPiece> list, VoxelShape voxelshape) {
        WorldGenFeatureDefinedStructureJigsawPlacement.b worldgenfeaturedefinedstructurejigsawplacement_b = new WorldGenFeatureDefinedStructureJigsawPlacement.b(iregistry, i, chunkgenerator, structuretemplatemanager, list, randomsource);

        worldgenfeaturedefinedstructurejigsawplacement_b.placing.addLast(new WorldGenFeatureDefinedStructureJigsawPlacement.a(worldgenfeaturepillageroutpostpoolpiece, new MutableObject(voxelshape), 0));

        while (!worldgenfeaturedefinedstructurejigsawplacement_b.placing.isEmpty()) {
            WorldGenFeatureDefinedStructureJigsawPlacement.a worldgenfeaturedefinedstructurejigsawplacement_a = (WorldGenFeatureDefinedStructureJigsawPlacement.a) worldgenfeaturedefinedstructurejigsawplacement_b.placing.removeFirst();

            worldgenfeaturedefinedstructurejigsawplacement_b.tryPlacingChildren(worldgenfeaturedefinedstructurejigsawplacement_a.piece, worldgenfeaturedefinedstructurejigsawplacement_a.free, worldgenfeaturedefinedstructurejigsawplacement_a.depth, flag, levelheightaccessor, randomstate);
        }

    }

    public static boolean generateJigsaw(WorldServer worldserver, Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder, MinecraftKey minecraftkey, int i, BlockPosition blockposition, boolean flag) {
        ChunkGenerator chunkgenerator = worldserver.getChunkSource().getGenerator();
        StructureTemplateManager structuretemplatemanager = worldserver.getStructureManager();
        StructureManager structuremanager = worldserver.structureManager();
        RandomSource randomsource = worldserver.getRandom();
        Structure.a structure_a = new Structure.a(worldserver.registryAccess(), chunkgenerator, chunkgenerator.getBiomeSource(), worldserver.getChunkSource().randomState(), structuretemplatemanager, worldserver.getSeed(), new ChunkCoordIntPair(blockposition), worldserver, (holder1) -> {
            return true;
        });
        Optional<Structure.b> optional = addPieces(structure_a, holder, Optional.of(minecraftkey), i, blockposition, false, Optional.empty(), 128);

        if (optional.isPresent()) {
            StructurePiecesBuilder structurepiecesbuilder = ((Structure.b) optional.get()).getPiecesBuilder();
            Iterator iterator = structurepiecesbuilder.build().pieces().iterator();

            while (iterator.hasNext()) {
                StructurePiece structurepiece = (StructurePiece) iterator.next();

                if (structurepiece instanceof WorldGenFeaturePillagerOutpostPoolPiece) {
                    WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece = (WorldGenFeaturePillagerOutpostPoolPiece) structurepiece;

                    worldgenfeaturepillageroutpostpoolpiece.place(worldserver, structuremanager, chunkgenerator, randomsource, StructureBoundingBox.infinite(), blockposition, flag);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private static final class b {

        private final IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> pools;
        private final int maxDepth;
        private final ChunkGenerator chunkGenerator;
        private final StructureTemplateManager structureTemplateManager;
        private final List<? super WorldGenFeaturePillagerOutpostPoolPiece> pieces;
        private final RandomSource random;
        final Deque<WorldGenFeatureDefinedStructureJigsawPlacement.a> placing = Queues.newArrayDeque();

        b(IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> iregistry, int i, ChunkGenerator chunkgenerator, StructureTemplateManager structuretemplatemanager, List<? super WorldGenFeaturePillagerOutpostPoolPiece> list, RandomSource randomsource) {
            this.pools = iregistry;
            this.maxDepth = i;
            this.chunkGenerator = chunkgenerator;
            this.structureTemplateManager = structuretemplatemanager;
            this.pieces = list;
            this.random = randomsource;
        }

        void tryPlacingChildren(WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, MutableObject<VoxelShape> mutableobject, int i, boolean flag, LevelHeightAccessor levelheightaccessor, RandomState randomstate) {
            WorldGenFeatureDefinedStructurePoolStructure worldgenfeaturedefinedstructurepoolstructure = worldgenfeaturepillageroutpostpoolpiece.getElement();
            BlockPosition blockposition = worldgenfeaturepillageroutpostpoolpiece.getPosition();
            EnumBlockRotation enumblockrotation = worldgenfeaturepillageroutpostpoolpiece.getRotation();
            WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching = worldgenfeaturedefinedstructurepoolstructure.getProjection();
            boolean flag1 = worldgenfeaturedefinedstructurepooltemplate_matching == WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID;
            MutableObject<VoxelShape> mutableobject1 = new MutableObject();
            StructureBoundingBox structureboundingbox = worldgenfeaturepillageroutpostpoolpiece.getBoundingBox();
            int j = structureboundingbox.minY();
            Iterator iterator = worldgenfeaturedefinedstructurepoolstructure.getShuffledJigsawBlocks(this.structureTemplateManager, blockposition, enumblockrotation, this.random).iterator();

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
                                List<DefinedStructure.BlockInfo> list1 = worldgenfeaturedefinedstructurepoolstructure1.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPosition.ZERO, enumblockrotation1, this.random);
                                StructureBoundingBox structureboundingbox1 = worldgenfeaturedefinedstructurepoolstructure1.getBoundingBox(this.structureTemplateManager, BlockPosition.ZERO, enumblockrotation1);
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
                                                return worldgenfeaturedefinedstructurepooltemplate.getMaxSize(this.structureTemplateManager);
                                            }).orElse(0);
                                            int k1 = (Integer) optional3.map((worldgenfeaturedefinedstructurepooltemplate) -> {
                                                return worldgenfeaturedefinedstructurepooltemplate.getMaxSize(this.structureTemplateManager);
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
                                        StructureBoundingBox structureboundingbox2 = worldgenfeaturedefinedstructurepoolstructure1.getBoundingBox(this.structureTemplateManager, blockposition4, enumblockrotation1);
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
                                                l = this.chunkGenerator.getFirstFreeHeight(blockposition1.getX(), blockposition1.getZ(), HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor, randomstate);
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

                                            WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece1 = new WorldGenFeaturePillagerOutpostPoolPiece(this.structureTemplateManager, worldgenfeaturedefinedstructurepoolstructure1, blockposition5, l2, enumblockrotation1, structureboundingbox3);
                                            int i3;

                                            if (flag1) {
                                                i3 = j + k;
                                            } else if (flag3) {
                                                i3 = i2 + k1;
                                            } else {
                                                if (l == -1) {
                                                    l = this.chunkGenerator.getFirstFreeHeight(blockposition1.getX(), blockposition1.getZ(), HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor, randomstate);
                                                }

                                                i3 = l + l1 / 2;
                                            }

                                            worldgenfeaturepillageroutpostpoolpiece.addJunction(new WorldGenFeatureDefinedStructureJigsawJunction(blockposition2.getX(), i3 - k + k2, blockposition2.getZ(), l1, worldgenfeaturedefinedstructurepooltemplate_matching1));
                                            worldgenfeaturepillageroutpostpoolpiece1.addJunction(new WorldGenFeatureDefinedStructureJigsawJunction(blockposition1.getX(), i3 - k1 + l2, blockposition1.getZ(), -l1, worldgenfeaturedefinedstructurepooltemplate_matching));
                                            this.pieces.add(worldgenfeaturepillageroutpostpoolpiece1);
                                            if (i + 1 <= this.maxDepth) {
                                                this.placing.addLast(new WorldGenFeatureDefinedStructureJigsawPlacement.a(worldgenfeaturepillageroutpostpoolpiece1, mutableobject2, i + 1));
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

    private static final class a {

        final WorldGenFeaturePillagerOutpostPoolPiece piece;
        final MutableObject<VoxelShape> free;
        final int depth;

        a(WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece, MutableObject<VoxelShape> mutableobject, int i) {
            this.piece = worldgenfeaturepillageroutpostpoolpiece;
            this.free = mutableobject;
            this.depth = i;
        }
    }
}
