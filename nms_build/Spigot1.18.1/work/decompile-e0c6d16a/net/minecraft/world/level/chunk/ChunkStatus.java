package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.LightEngineThreaded;
import net.minecraft.server.level.PlayerChunk;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class ChunkStatus {

    public static final int MAX_STRUCTURE_DISTANCE = 8;
    private static final EnumSet<HeightMap.Type> PRE_FEATURES = EnumSet.of(HeightMap.Type.OCEAN_FLOOR_WG, HeightMap.Type.WORLD_SURFACE_WG);
    public static final EnumSet<HeightMap.Type> POST_FEATURES = EnumSet.of(HeightMap.Type.OCEAN_FLOOR, HeightMap.Type.WORLD_SURFACE, HeightMap.Type.MOTION_BLOCKING, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES);
    private static final ChunkStatus.c PASSTHROUGH_LOAD_TASK = (chunkstatus, worldserver, definedstructuremanager, lightenginethreaded, function, ichunkaccess) -> {
        if (ichunkaccess instanceof ProtoChunk) {
            ProtoChunk protochunk = (ProtoChunk) ichunkaccess;

            if (!ichunkaccess.getStatus().isOrAfter(chunkstatus)) {
                protochunk.setStatus(chunkstatus);
            }
        }

        return CompletableFuture.completedFuture(Either.left(ichunkaccess));
    };
    public static final ChunkStatus EMPTY = registerSimple("empty", (ChunkStatus) null, -1, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
    });
    public static final ChunkStatus STRUCTURE_STARTS = register("structure_starts", ChunkStatus.EMPTY, 0, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess, flag) -> {
        if (!ichunkaccess.getStatus().isOrAfter(chunkstatus)) {
            if (worldserver.getServer().getWorldData().worldGenSettings().generateFeatures()) {
                chunkgenerator.createStructures(worldserver.registryAccess(), worldserver.structureFeatureManager(), ichunkaccess, definedstructuremanager, worldserver.getSeed());
            }

            if (ichunkaccess instanceof ProtoChunk) {
                ProtoChunk protochunk = (ProtoChunk) ichunkaccess;

                protochunk.setStatus(chunkstatus);
            }

            worldserver.onStructureStartsAvailable(ichunkaccess);
        }

        return CompletableFuture.completedFuture(Either.left(ichunkaccess));
    }, (chunkstatus, worldserver, definedstructuremanager, lightenginethreaded, function, ichunkaccess) -> {
        if (!ichunkaccess.getStatus().isOrAfter(chunkstatus)) {
            if (ichunkaccess instanceof ProtoChunk) {
                ProtoChunk protochunk = (ProtoChunk) ichunkaccess;

                protochunk.setStatus(chunkstatus);
            }

            worldserver.onStructureStartsAvailable(ichunkaccess);
        }

        return CompletableFuture.completedFuture(Either.left(ichunkaccess));
    });
    public static final ChunkStatus STRUCTURE_REFERENCES = registerSimple("structure_references", ChunkStatus.STRUCTURE_STARTS, 8, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
        RegionLimitedWorldAccess regionlimitedworldaccess = new RegionLimitedWorldAccess(worldserver, list, chunkstatus, -1);

        chunkgenerator.createReferences(regionlimitedworldaccess, worldserver.structureFeatureManager().forWorldGenRegion(regionlimitedworldaccess), ichunkaccess);
    });
    public static final ChunkStatus BIOMES = register("biomes", ChunkStatus.STRUCTURE_REFERENCES, 8, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess, flag) -> {
        if (!flag && ichunkaccess.getStatus().isOrAfter(chunkstatus)) {
            return CompletableFuture.completedFuture(Either.left(ichunkaccess));
        } else {
            RegionLimitedWorldAccess regionlimitedworldaccess = new RegionLimitedWorldAccess(worldserver, list, chunkstatus, -1);

            return chunkgenerator.createBiomes(worldserver.registryAccess().registryOrThrow(IRegistry.BIOME_REGISTRY), executor, Blender.of(regionlimitedworldaccess), worldserver.structureFeatureManager().forWorldGenRegion(regionlimitedworldaccess), ichunkaccess).thenApply((ichunkaccess1) -> {
                if (ichunkaccess1 instanceof ProtoChunk) {
                    ((ProtoChunk) ichunkaccess1).setStatus(chunkstatus);
                }

                return Either.left(ichunkaccess1);
            });
        }
    });
    public static final ChunkStatus NOISE = register("noise", ChunkStatus.BIOMES, 8, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess, flag) -> {
        if (!flag && ichunkaccess.getStatus().isOrAfter(chunkstatus)) {
            return CompletableFuture.completedFuture(Either.left(ichunkaccess));
        } else {
            RegionLimitedWorldAccess regionlimitedworldaccess = new RegionLimitedWorldAccess(worldserver, list, chunkstatus, 0);

            return chunkgenerator.fillFromNoise(executor, Blender.of(regionlimitedworldaccess), worldserver.structureFeatureManager().forWorldGenRegion(regionlimitedworldaccess), ichunkaccess).thenApply((ichunkaccess1) -> {
                if (ichunkaccess1 instanceof ProtoChunk) {
                    ProtoChunk protochunk = (ProtoChunk) ichunkaccess1;
                    BelowZeroRetrogen belowzeroretrogen = protochunk.getBelowZeroRetrogen();

                    if (belowzeroretrogen != null) {
                        BelowZeroRetrogen.replaceOldBedrock(protochunk);
                        if (belowzeroretrogen.hasBedrockHoles()) {
                            belowzeroretrogen.applyBedrockMask(protochunk);
                        }
                    }

                    protochunk.setStatus(chunkstatus);
                }

                return Either.left(ichunkaccess1);
            });
        }
    });
    public static final ChunkStatus SURFACE = registerSimple("surface", ChunkStatus.NOISE, 8, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
        RegionLimitedWorldAccess regionlimitedworldaccess = new RegionLimitedWorldAccess(worldserver, list, chunkstatus, 0);

        chunkgenerator.buildSurface(regionlimitedworldaccess, worldserver.structureFeatureManager().forWorldGenRegion(regionlimitedworldaccess), ichunkaccess);
    });
    public static final ChunkStatus CARVERS = registerSimple("carvers", ChunkStatus.SURFACE, 8, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
        RegionLimitedWorldAccess regionlimitedworldaccess = new RegionLimitedWorldAccess(worldserver, list, chunkstatus, 0);

        if (ichunkaccess instanceof ProtoChunk) {
            ProtoChunk protochunk = (ProtoChunk) ichunkaccess;

            Blender.addAroundOldChunksCarvingMaskFilter(regionlimitedworldaccess, protochunk);
        }

        chunkgenerator.applyCarvers(regionlimitedworldaccess, worldserver.getSeed(), worldserver.getBiomeManager(), worldserver.structureFeatureManager().forWorldGenRegion(regionlimitedworldaccess), ichunkaccess, WorldGenStage.Features.AIR);
    });
    public static final ChunkStatus LIQUID_CARVERS = registerSimple("liquid_carvers", ChunkStatus.CARVERS, 8, ChunkStatus.POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
    });
    public static final ChunkStatus FEATURES = register("features", ChunkStatus.LIQUID_CARVERS, 8, ChunkStatus.POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess, flag) -> {
        ProtoChunk protochunk = (ProtoChunk) ichunkaccess;

        protochunk.setLightEngine(lightenginethreaded);
        if (flag || !ichunkaccess.getStatus().isOrAfter(chunkstatus)) {
            HeightMap.primeHeightmaps(ichunkaccess, EnumSet.of(HeightMap.Type.MOTION_BLOCKING, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, HeightMap.Type.OCEAN_FLOOR, HeightMap.Type.WORLD_SURFACE));
            RegionLimitedWorldAccess regionlimitedworldaccess = new RegionLimitedWorldAccess(worldserver, list, chunkstatus, 1);

            chunkgenerator.applyBiomeDecoration(regionlimitedworldaccess, ichunkaccess, worldserver.structureFeatureManager().forWorldGenRegion(regionlimitedworldaccess));
            Blender.generateBorderTicks(regionlimitedworldaccess, ichunkaccess);
            protochunk.setStatus(chunkstatus);
        }

        return CompletableFuture.completedFuture(Either.left(ichunkaccess));
    });
    public static final ChunkStatus LIGHT = register("light", ChunkStatus.FEATURES, 1, ChunkStatus.POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess, flag) -> {
        return lightChunk(chunkstatus, lightenginethreaded, ichunkaccess);
    }, (chunkstatus, worldserver, definedstructuremanager, lightenginethreaded, function, ichunkaccess) -> {
        return lightChunk(chunkstatus, lightenginethreaded, ichunkaccess);
    });
    public static final ChunkStatus SPAWN = registerSimple("spawn", ChunkStatus.LIGHT, 0, ChunkStatus.POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
        if (!ichunkaccess.isUpgrading()) {
            chunkgenerator.spawnOriginalMobs(new RegionLimitedWorldAccess(worldserver, list, chunkstatus, -1));
        }

    });
    public static final ChunkStatus HEIGHTMAPS = registerSimple("heightmaps", ChunkStatus.SPAWN, 0, ChunkStatus.POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
    });
    public static final ChunkStatus FULL = register("full", ChunkStatus.HEIGHTMAPS, 0, ChunkStatus.POST_FEATURES, ChunkStatus.Type.LEVELCHUNK, (chunkstatus, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess, flag) -> {
        return (CompletableFuture) function.apply(ichunkaccess);
    }, (chunkstatus, worldserver, definedstructuremanager, lightenginethreaded, function, ichunkaccess) -> {
        return (CompletableFuture) function.apply(ichunkaccess);
    });
    private static final List<ChunkStatus> STATUS_BY_RANGE = ImmutableList.of(ChunkStatus.FULL, ChunkStatus.FEATURES, ChunkStatus.LIQUID_CARVERS, ChunkStatus.BIOMES, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, new ChunkStatus[0]);
    private static final IntList RANGE_BY_STATUS = (IntList) SystemUtils.make(new IntArrayList(getStatusList().size()), (intarraylist) -> {
        int i = 0;

        for (int j = getStatusList().size() - 1; j >= 0; --j) {
            while (i + 1 < ChunkStatus.STATUS_BY_RANGE.size() && j <= ((ChunkStatus) ChunkStatus.STATUS_BY_RANGE.get(i + 1)).getIndex()) {
                ++i;
            }

            intarraylist.add(0, i);
        }

    });
    private final String name;
    private final int index;
    private final ChunkStatus parent;
    private final ChunkStatus.b generationTask;
    private final ChunkStatus.c loadingTask;
    private final int range;
    private final ChunkStatus.Type chunkType;
    private final EnumSet<HeightMap.Type> heightmapsAfter;

    private static CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> lightChunk(ChunkStatus chunkstatus, LightEngineThreaded lightenginethreaded, IChunkAccess ichunkaccess) {
        boolean flag = isLighted(chunkstatus, ichunkaccess);

        if (!ichunkaccess.getStatus().isOrAfter(chunkstatus)) {
            ((ProtoChunk) ichunkaccess).setStatus(chunkstatus);
        }

        return lightenginethreaded.lightChunk(ichunkaccess, flag).thenApply(Either::left);
    }

    private static ChunkStatus registerSimple(String s, @Nullable ChunkStatus chunkstatus, int i, EnumSet<HeightMap.Type> enumset, ChunkStatus.Type chunkstatus_type, ChunkStatus.d chunkstatus_d) {
        return register(s, chunkstatus, i, enumset, chunkstatus_type, chunkstatus_d);
    }

    private static ChunkStatus register(String s, @Nullable ChunkStatus chunkstatus, int i, EnumSet<HeightMap.Type> enumset, ChunkStatus.Type chunkstatus_type, ChunkStatus.b chunkstatus_b) {
        return register(s, chunkstatus, i, enumset, chunkstatus_type, chunkstatus_b, ChunkStatus.PASSTHROUGH_LOAD_TASK);
    }

    private static ChunkStatus register(String s, @Nullable ChunkStatus chunkstatus, int i, EnumSet<HeightMap.Type> enumset, ChunkStatus.Type chunkstatus_type, ChunkStatus.b chunkstatus_b, ChunkStatus.c chunkstatus_c) {
        return (ChunkStatus) IRegistry.register(IRegistry.CHUNK_STATUS, s, new ChunkStatus(s, chunkstatus, i, enumset, chunkstatus_type, chunkstatus_b, chunkstatus_c));
    }

    public static List<ChunkStatus> getStatusList() {
        List<ChunkStatus> list = Lists.newArrayList();

        ChunkStatus chunkstatus;

        for (chunkstatus = ChunkStatus.FULL; chunkstatus.getParent() != chunkstatus; chunkstatus = chunkstatus.getParent()) {
            list.add(chunkstatus);
        }

        list.add(chunkstatus);
        Collections.reverse(list);
        return list;
    }

    private static boolean isLighted(ChunkStatus chunkstatus, IChunkAccess ichunkaccess) {
        return ichunkaccess.getStatus().isOrAfter(chunkstatus) && ichunkaccess.isLightCorrect();
    }

    public static ChunkStatus getStatusAroundFullChunk(int i) {
        return i >= ChunkStatus.STATUS_BY_RANGE.size() ? ChunkStatus.EMPTY : (i < 0 ? ChunkStatus.FULL : (ChunkStatus) ChunkStatus.STATUS_BY_RANGE.get(i));
    }

    public static int maxDistance() {
        return ChunkStatus.STATUS_BY_RANGE.size();
    }

    public static int getDistance(ChunkStatus chunkstatus) {
        return ChunkStatus.RANGE_BY_STATUS.getInt(chunkstatus.getIndex());
    }

    ChunkStatus(String s, @Nullable ChunkStatus chunkstatus, int i, EnumSet<HeightMap.Type> enumset, ChunkStatus.Type chunkstatus_type, ChunkStatus.b chunkstatus_b, ChunkStatus.c chunkstatus_c) {
        this.name = s;
        this.parent = chunkstatus == null ? this : chunkstatus;
        this.generationTask = chunkstatus_b;
        this.loadingTask = chunkstatus_c;
        this.range = i;
        this.chunkType = chunkstatus_type;
        this.heightmapsAfter = enumset;
        this.index = chunkstatus == null ? 0 : chunkstatus.getIndex() + 1;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public ChunkStatus getParent() {
        return this.parent;
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> generate(Executor executor, WorldServer worldserver, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, LightEngineThreaded lightenginethreaded, Function<IChunkAccess, CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> function, List<IChunkAccess> list, boolean flag) {
        IChunkAccess ichunkaccess = (IChunkAccess) list.get(list.size() / 2);
        ProfiledDuration profiledduration = JvmProfiler.INSTANCE.onChunkGenerate(ichunkaccess.getPos(), worldserver.dimension(), this.name);
        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = this.generationTask.doWork(this, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess, flag);

        return profiledduration != null ? completablefuture.thenApply((either) -> {
            profiledduration.finish();
            return either;
        }) : completablefuture;
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> load(WorldServer worldserver, DefinedStructureManager definedstructuremanager, LightEngineThreaded lightenginethreaded, Function<IChunkAccess, CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> function, IChunkAccess ichunkaccess) {
        return this.loadingTask.doWork(this, worldserver, definedstructuremanager, lightenginethreaded, function, ichunkaccess);
    }

    public int getRange() {
        return this.range;
    }

    public ChunkStatus.Type getChunkType() {
        return this.chunkType;
    }

    public static ChunkStatus byName(String s) {
        return (ChunkStatus) IRegistry.CHUNK_STATUS.get(MinecraftKey.tryParse(s));
    }

    public EnumSet<HeightMap.Type> heightmapsAfter() {
        return this.heightmapsAfter;
    }

    public boolean isOrAfter(ChunkStatus chunkstatus) {
        return this.getIndex() >= chunkstatus.getIndex();
    }

    public String toString() {
        return IRegistry.CHUNK_STATUS.getKey(this).toString();
    }

    public static enum Type {

        PROTOCHUNK, LEVELCHUNK;

        private Type() {}
    }

    private interface b {

        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> doWork(ChunkStatus chunkstatus, Executor executor, WorldServer worldserver, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, LightEngineThreaded lightenginethreaded, Function<IChunkAccess, CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> function, List<IChunkAccess> list, IChunkAccess ichunkaccess, boolean flag);
    }

    private interface c {

        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> doWork(ChunkStatus chunkstatus, WorldServer worldserver, DefinedStructureManager definedstructuremanager, LightEngineThreaded lightenginethreaded, Function<IChunkAccess, CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> function, IChunkAccess ichunkaccess);
    }

    private interface d extends ChunkStatus.b {

        @Override
        default CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> doWork(ChunkStatus chunkstatus, Executor executor, WorldServer worldserver, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, LightEngineThreaded lightenginethreaded, Function<IChunkAccess, CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> function, List<IChunkAccess> list, IChunkAccess ichunkaccess, boolean flag) {
            if (flag || !ichunkaccess.getStatus().isOrAfter(chunkstatus)) {
                this.doWork(chunkstatus, worldserver, chunkgenerator, list, ichunkaccess);
                if (ichunkaccess instanceof ProtoChunk) {
                    ((ProtoChunk) ichunkaccess).setStatus(chunkstatus);
                }
            }

            return CompletableFuture.completedFuture(Either.left(ichunkaccess));
        }

        void doWork(ChunkStatus chunkstatus, WorldServer worldserver, ChunkGenerator chunkgenerator, List<IChunkAccess> list, IChunkAccess ichunkaccess);
    }
}
