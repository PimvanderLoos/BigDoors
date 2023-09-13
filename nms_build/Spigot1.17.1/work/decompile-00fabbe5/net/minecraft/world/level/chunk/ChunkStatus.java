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
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.lighting.LightEngine;

public class ChunkStatus {

    private static final EnumSet<HeightMap.Type> PRE_FEATURES = EnumSet.of(HeightMap.Type.OCEAN_FLOOR_WG, HeightMap.Type.WORLD_SURFACE_WG);
    private static final EnumSet<HeightMap.Type> POST_FEATURES = EnumSet.of(HeightMap.Type.OCEAN_FLOOR, HeightMap.Type.WORLD_SURFACE, HeightMap.Type.MOTION_BLOCKING, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES);
    private static final ChunkStatus.c PASSTHROUGH_LOAD_TASK = (chunkstatus, worldserver, definedstructuremanager, lightenginethreaded, function, ichunkaccess) -> {
        if (ichunkaccess instanceof ProtoChunk && !ichunkaccess.getChunkStatus().b(chunkstatus)) {
            ((ProtoChunk) ichunkaccess).a(chunkstatus);
        }

        return CompletableFuture.completedFuture(Either.left(ichunkaccess));
    };
    public static final ChunkStatus EMPTY = a("empty", (ChunkStatus) null, -1, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
    });
    public static final ChunkStatus STRUCTURE_STARTS = a("structure_starts", ChunkStatus.EMPTY, 0, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess) -> {
        if (!ichunkaccess.getChunkStatus().b(chunkstatus)) {
            if (worldserver.getMinecraftServer().getSaveData().getGeneratorSettings().shouldGenerateMapFeatures()) {
                chunkgenerator.createStructures(worldserver.t(), worldserver.getStructureManager(), ichunkaccess, definedstructuremanager, worldserver.getSeed());
            }

            if (ichunkaccess instanceof ProtoChunk) {
                ((ProtoChunk) ichunkaccess).a(chunkstatus);
            }
        }

        return CompletableFuture.completedFuture(Either.left(ichunkaccess));
    });
    public static final ChunkStatus STRUCTURE_REFERENCES = a("structure_references", ChunkStatus.STRUCTURE_STARTS, 8, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
        RegionLimitedWorldAccess regionlimitedworldaccess = new RegionLimitedWorldAccess(worldserver, list, chunkstatus, -1);

        chunkgenerator.storeStructures(regionlimitedworldaccess, worldserver.getStructureManager().a(regionlimitedworldaccess), ichunkaccess);
    });
    public static final ChunkStatus BIOMES = a("biomes", ChunkStatus.STRUCTURE_REFERENCES, 0, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
        chunkgenerator.createBiomes(worldserver.t().d(IRegistry.BIOME_REGISTRY), ichunkaccess);
    });
    public static final ChunkStatus NOISE = a("noise", ChunkStatus.BIOMES, 8, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess) -> {
        if (!ichunkaccess.getChunkStatus().b(chunkstatus)) {
            RegionLimitedWorldAccess regionlimitedworldaccess = new RegionLimitedWorldAccess(worldserver, list, chunkstatus, 0);

            return chunkgenerator.buildNoise(executor, worldserver.getStructureManager().a(regionlimitedworldaccess), ichunkaccess).thenApply((ichunkaccess1) -> {
                if (ichunkaccess1 instanceof ProtoChunk) {
                    ((ProtoChunk) ichunkaccess1).a(chunkstatus);
                }

                return Either.left(ichunkaccess1);
            });
        } else {
            return CompletableFuture.completedFuture(Either.left(ichunkaccess));
        }
    });
    public static final ChunkStatus SURFACE = a("surface", ChunkStatus.NOISE, 0, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
        chunkgenerator.buildBase(new RegionLimitedWorldAccess(worldserver, list, chunkstatus, 0), ichunkaccess);
    });
    public static final ChunkStatus CARVERS = a("carvers", ChunkStatus.SURFACE, 0, ChunkStatus.PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
        chunkgenerator.doCarving(worldserver.getSeed(), worldserver.r_(), ichunkaccess, WorldGenStage.Features.AIR);
    });
    public static final ChunkStatus LIQUID_CARVERS = a("liquid_carvers", ChunkStatus.CARVERS, 0, ChunkStatus.POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
        chunkgenerator.doCarving(worldserver.getSeed(), worldserver.r_(), ichunkaccess, WorldGenStage.Features.LIQUID);
    });
    public static final ChunkStatus FEATURES = a("features", ChunkStatus.LIQUID_CARVERS, 8, ChunkStatus.POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess) -> {
        ProtoChunk protochunk = (ProtoChunk) ichunkaccess;

        protochunk.a((LightEngine) lightenginethreaded);
        if (!ichunkaccess.getChunkStatus().b(chunkstatus)) {
            HeightMap.a(ichunkaccess, EnumSet.of(HeightMap.Type.MOTION_BLOCKING, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, HeightMap.Type.OCEAN_FLOOR, HeightMap.Type.WORLD_SURFACE));
            RegionLimitedWorldAccess regionlimitedworldaccess = new RegionLimitedWorldAccess(worldserver, list, chunkstatus, 1);

            chunkgenerator.addDecorations(regionlimitedworldaccess, worldserver.getStructureManager().a(regionlimitedworldaccess));
            protochunk.a(chunkstatus);
        }

        return CompletableFuture.completedFuture(Either.left(ichunkaccess));
    });
    public static final ChunkStatus LIGHT = a("light", ChunkStatus.FEATURES, 1, ChunkStatus.POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess) -> {
        return a(chunkstatus, lightenginethreaded, ichunkaccess);
    }, (chunkstatus, worldserver, definedstructuremanager, lightenginethreaded, function, ichunkaccess) -> {
        return a(chunkstatus, lightenginethreaded, ichunkaccess);
    });
    public static final ChunkStatus SPAWN = a("spawn", ChunkStatus.LIGHT, 0, ChunkStatus.POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
        chunkgenerator.addMobs(new RegionLimitedWorldAccess(worldserver, list, chunkstatus, -1));
    });
    public static final ChunkStatus HEIGHTMAPS = a("heightmaps", ChunkStatus.SPAWN, 0, ChunkStatus.POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (chunkstatus, worldserver, chunkgenerator, list, ichunkaccess) -> {
    });
    public static final ChunkStatus FULL = a("full", ChunkStatus.HEIGHTMAPS, 0, ChunkStatus.POST_FEATURES, ChunkStatus.Type.LEVELCHUNK, (chunkstatus, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, ichunkaccess) -> {
        return (CompletableFuture) function.apply(ichunkaccess);
    }, (chunkstatus, worldserver, definedstructuremanager, lightenginethreaded, function, ichunkaccess) -> {
        return (CompletableFuture) function.apply(ichunkaccess);
    });
    private static final List<ChunkStatus> STATUS_BY_RANGE = ImmutableList.of(ChunkStatus.FULL, ChunkStatus.FEATURES, ChunkStatus.LIQUID_CARVERS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS, ChunkStatus.STRUCTURE_STARTS);
    private static final IntList RANGE_BY_STATUS = (IntList) SystemUtils.a((Object) (new IntArrayList(a().size())), (intarraylist) -> {
        int i = 0;

        for (int j = a().size() - 1; j >= 0; --j) {
            while (i + 1 < ChunkStatus.STATUS_BY_RANGE.size() && j <= ((ChunkStatus) ChunkStatus.STATUS_BY_RANGE.get(i + 1)).c()) {
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

    private static CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> a(ChunkStatus chunkstatus, LightEngineThreaded lightenginethreaded, IChunkAccess ichunkaccess) {
        boolean flag = a(chunkstatus, ichunkaccess);

        if (!ichunkaccess.getChunkStatus().b(chunkstatus)) {
            ((ProtoChunk) ichunkaccess).a(chunkstatus);
        }

        return lightenginethreaded.a(ichunkaccess, flag).thenApply(Either::left);
    }

    private static ChunkStatus a(String s, @Nullable ChunkStatus chunkstatus, int i, EnumSet<HeightMap.Type> enumset, ChunkStatus.Type chunkstatus_type, ChunkStatus.d chunkstatus_d) {
        return a(s, chunkstatus, i, enumset, chunkstatus_type, (ChunkStatus.b) chunkstatus_d);
    }

    private static ChunkStatus a(String s, @Nullable ChunkStatus chunkstatus, int i, EnumSet<HeightMap.Type> enumset, ChunkStatus.Type chunkstatus_type, ChunkStatus.b chunkstatus_b) {
        return a(s, chunkstatus, i, enumset, chunkstatus_type, chunkstatus_b, ChunkStatus.PASSTHROUGH_LOAD_TASK);
    }

    private static ChunkStatus a(String s, @Nullable ChunkStatus chunkstatus, int i, EnumSet<HeightMap.Type> enumset, ChunkStatus.Type chunkstatus_type, ChunkStatus.b chunkstatus_b, ChunkStatus.c chunkstatus_c) {
        return (ChunkStatus) IRegistry.a((IRegistry) IRegistry.CHUNK_STATUS, s, (Object) (new ChunkStatus(s, chunkstatus, i, enumset, chunkstatus_type, chunkstatus_b, chunkstatus_c)));
    }

    public static List<ChunkStatus> a() {
        List<ChunkStatus> list = Lists.newArrayList();

        ChunkStatus chunkstatus;

        for (chunkstatus = ChunkStatus.FULL; chunkstatus.e() != chunkstatus; chunkstatus = chunkstatus.e()) {
            list.add(chunkstatus);
        }

        list.add(chunkstatus);
        Collections.reverse(list);
        return list;
    }

    private static boolean a(ChunkStatus chunkstatus, IChunkAccess ichunkaccess) {
        return ichunkaccess.getChunkStatus().b(chunkstatus) && ichunkaccess.s();
    }

    public static ChunkStatus a(int i) {
        return i >= ChunkStatus.STATUS_BY_RANGE.size() ? ChunkStatus.EMPTY : (i < 0 ? ChunkStatus.FULL : (ChunkStatus) ChunkStatus.STATUS_BY_RANGE.get(i));
    }

    public static int b() {
        return ChunkStatus.STATUS_BY_RANGE.size();
    }

    public static int a(ChunkStatus chunkstatus) {
        return ChunkStatus.RANGE_BY_STATUS.getInt(chunkstatus.c());
    }

    ChunkStatus(String s, @Nullable ChunkStatus chunkstatus, int i, EnumSet<HeightMap.Type> enumset, ChunkStatus.Type chunkstatus_type, ChunkStatus.b chunkstatus_b, ChunkStatus.c chunkstatus_c) {
        this.name = s;
        this.parent = chunkstatus == null ? this : chunkstatus;
        this.generationTask = chunkstatus_b;
        this.loadingTask = chunkstatus_c;
        this.range = i;
        this.chunkType = chunkstatus_type;
        this.heightmapsAfter = enumset;
        this.index = chunkstatus == null ? 0 : chunkstatus.c() + 1;
    }

    public int c() {
        return this.index;
    }

    public String d() {
        return this.name;
    }

    public ChunkStatus e() {
        return this.parent;
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> a(Executor executor, WorldServer worldserver, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, LightEngineThreaded lightenginethreaded, Function<IChunkAccess, CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> function, List<IChunkAccess> list) {
        return this.generationTask.doWork(this, executor, worldserver, chunkgenerator, definedstructuremanager, lightenginethreaded, function, list, (IChunkAccess) list.get(list.size() / 2));
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> a(WorldServer worldserver, DefinedStructureManager definedstructuremanager, LightEngineThreaded lightenginethreaded, Function<IChunkAccess, CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> function, IChunkAccess ichunkaccess) {
        return this.loadingTask.doWork(this, worldserver, definedstructuremanager, lightenginethreaded, function, ichunkaccess);
    }

    public int f() {
        return this.range;
    }

    public ChunkStatus.Type getType() {
        return this.chunkType;
    }

    public static ChunkStatus a(String s) {
        return (ChunkStatus) IRegistry.CHUNK_STATUS.get(MinecraftKey.a(s));
    }

    public EnumSet<HeightMap.Type> h() {
        return this.heightmapsAfter;
    }

    public boolean b(ChunkStatus chunkstatus) {
        return this.c() >= chunkstatus.c();
    }

    public String toString() {
        return IRegistry.CHUNK_STATUS.getKey(this).toString();
    }

    public static enum Type {

        PROTOCHUNK, LEVELCHUNK;

        private Type() {}
    }

    private interface b {

        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> doWork(ChunkStatus chunkstatus, Executor executor, WorldServer worldserver, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, LightEngineThreaded lightenginethreaded, Function<IChunkAccess, CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> function, List<IChunkAccess> list, IChunkAccess ichunkaccess);
    }

    private interface c {

        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> doWork(ChunkStatus chunkstatus, WorldServer worldserver, DefinedStructureManager definedstructuremanager, LightEngineThreaded lightenginethreaded, Function<IChunkAccess, CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> function, IChunkAccess ichunkaccess);
    }

    private interface d extends ChunkStatus.b {

        @Override
        default CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> doWork(ChunkStatus chunkstatus, Executor executor, WorldServer worldserver, ChunkGenerator chunkgenerator, DefinedStructureManager definedstructuremanager, LightEngineThreaded lightenginethreaded, Function<IChunkAccess, CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> function, List<IChunkAccess> list, IChunkAccess ichunkaccess) {
            if (!ichunkaccess.getChunkStatus().b(chunkstatus)) {
                this.doWork(chunkstatus, worldserver, chunkgenerator, list, ichunkaccess);
                if (ichunkaccess instanceof ProtoChunk) {
                    ((ProtoChunk) ichunkaccess).a(chunkstatus);
                }
            }

            return CompletableFuture.completedFuture(Either.left(ichunkaccess));
        }

        void doWork(ChunkStatus chunkstatus, WorldServer worldserver, ChunkGenerator chunkgenerator, List<IChunkAccess> list, IChunkAccess ichunkaccess);
    }
}
