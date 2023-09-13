package net.minecraft.world.level;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPositionTypes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.biome.GenLayerZoomerBiome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.Vec3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SpawnerCreature {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MIN_SPAWN_DISTANCE = 24;
    public static final int SPAWN_DISTANCE_CHUNK = 8;
    public static final int SPAWN_DISTANCE_BLOCK = 128;
    static final int MAGIC_NUMBER = (int) Math.pow(17.0D, 2.0D);
    private static final EnumCreatureType[] SPAWNING_CATEGORIES = (EnumCreatureType[]) Stream.of(EnumCreatureType.values()).filter((enumcreaturetype) -> {
        return enumcreaturetype != EnumCreatureType.MISC;
    }).toArray((i) -> {
        return new EnumCreatureType[i];
    });

    private SpawnerCreature() {}

    public static SpawnerCreature.d a(int i, Iterable<Entity> iterable, SpawnerCreature.b spawnercreature_b) {
        SpawnerCreatureProbabilities spawnercreatureprobabilities = new SpawnerCreatureProbabilities();
        Object2IntOpenHashMap<EnumCreatureType> object2intopenhashmap = new Object2IntOpenHashMap();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityInsentient) {
                EntityInsentient entityinsentient = (EntityInsentient) entity;

                if (entityinsentient.isPersistent() || entityinsentient.isSpecialPersistence()) {
                    continue;
                }
            }

            EnumCreatureType enumcreaturetype = entity.getEntityType().f();

            if (enumcreaturetype != EnumCreatureType.MISC) {
                BlockPosition blockposition = entity.getChunkCoordinates();
                long j = ChunkCoordIntPair.pair(SectionPosition.a(blockposition.getX()), SectionPosition.a(blockposition.getZ()));

                spawnercreature_b.query(j, (chunk) -> {
                    BiomeSettingsMobs.b biomesettingsmobs_b = a(blockposition, (IChunkAccess) chunk).b().a(entity.getEntityType());

                    if (biomesettingsmobs_b != null) {
                        spawnercreatureprobabilities.a(entity.getChunkCoordinates(), biomesettingsmobs_b.b());
                    }

                    object2intopenhashmap.addTo(enumcreaturetype, 1);
                });
            }
        }

        return new SpawnerCreature.d(i, object2intopenhashmap, spawnercreatureprobabilities);
    }

    static BiomeBase a(BlockPosition blockposition, IChunkAccess ichunkaccess) {
        return GenLayerZoomerBiome.INSTANCE.a(0L, blockposition.getX(), blockposition.getY(), blockposition.getZ(), ichunkaccess.getBiomeIndex());
    }

    public static void a(WorldServer worldserver, Chunk chunk, SpawnerCreature.d spawnercreature_d, boolean flag, boolean flag1, boolean flag2) {
        worldserver.getMethodProfiler().enter("spawner");
        EnumCreatureType[] aenumcreaturetype = SpawnerCreature.SPAWNING_CATEGORIES;
        int i = aenumcreaturetype.length;

        for (int j = 0; j < i; ++j) {
            EnumCreatureType enumcreaturetype = aenumcreaturetype[j];

            if ((flag || !enumcreaturetype.d()) && (flag1 || enumcreaturetype.d()) && (flag2 || !enumcreaturetype.e()) && spawnercreature_d.a(enumcreaturetype)) {
                Objects.requireNonNull(spawnercreature_d);
                SpawnerCreature.c spawnercreature_c = spawnercreature_d::a;

                Objects.requireNonNull(spawnercreature_d);
                a(enumcreaturetype, worldserver, chunk, spawnercreature_c, spawnercreature_d::a);
            }
        }

        worldserver.getMethodProfiler().exit();
    }

    public static void a(EnumCreatureType enumcreaturetype, WorldServer worldserver, Chunk chunk, SpawnerCreature.c spawnercreature_c, SpawnerCreature.a spawnercreature_a) {
        BlockPosition blockposition = getRandomPosition(worldserver, chunk);

        if (blockposition.getY() >= worldserver.getMinBuildHeight() + 1) {
            a(enumcreaturetype, worldserver, (IChunkAccess) chunk, blockposition, spawnercreature_c, spawnercreature_a);
        }
    }

    @VisibleForDebug
    public static void a(EnumCreatureType enumcreaturetype, WorldServer worldserver, BlockPosition blockposition) {
        a(enumcreaturetype, worldserver, worldserver.A(blockposition), blockposition, (entitytypes, blockposition1, ichunkaccess) -> {
            return true;
        }, (entityinsentient, ichunkaccess) -> {
        });
    }

    public static void a(EnumCreatureType enumcreaturetype, WorldServer worldserver, IChunkAccess ichunkaccess, BlockPosition blockposition, SpawnerCreature.c spawnercreature_c, SpawnerCreature.a spawnercreature_a) {
        StructureManager structuremanager = worldserver.getStructureManager();
        ChunkGenerator chunkgenerator = worldserver.getChunkProvider().getChunkGenerator();
        int i = blockposition.getY();
        IBlockData iblockdata = ichunkaccess.getType(blockposition);

        if (!iblockdata.isOccluding(ichunkaccess, blockposition)) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            int j = 0;
            int k = 0;

            while (k < 3) {
                int l = blockposition.getX();
                int i1 = blockposition.getZ();
                boolean flag = true;
                BiomeSettingsMobs.c biomesettingsmobs_c = null;
                GroupDataEntity groupdataentity = null;
                int j1 = MathHelper.f(worldserver.random.nextFloat() * 4.0F);
                int k1 = 0;
                int l1 = 0;

                while (true) {
                    if (l1 < j1) {
                        label53:
                        {
                            l += worldserver.random.nextInt(6) - worldserver.random.nextInt(6);
                            i1 += worldserver.random.nextInt(6) - worldserver.random.nextInt(6);
                            blockposition_mutableblockposition.d(l, i, i1);
                            double d0 = (double) l + 0.5D;
                            double d1 = (double) i1 + 0.5D;
                            EntityHuman entityhuman = worldserver.a(d0, (double) i, d1, -1.0D, false);

                            if (entityhuman != null) {
                                double d2 = entityhuman.h(d0, (double) i, d1);

                                if (a(worldserver, ichunkaccess, blockposition_mutableblockposition, d2)) {
                                    if (biomesettingsmobs_c == null) {
                                        Optional<BiomeSettingsMobs.c> optional = a(worldserver, structuremanager, chunkgenerator, enumcreaturetype, worldserver.random, (BlockPosition) blockposition_mutableblockposition);

                                        if (!optional.isPresent()) {
                                            break label53;
                                        }

                                        biomesettingsmobs_c = (BiomeSettingsMobs.c) optional.get();
                                        j1 = biomesettingsmobs_c.minCount + worldserver.random.nextInt(1 + biomesettingsmobs_c.maxCount - biomesettingsmobs_c.minCount);
                                    }

                                    if (a(worldserver, enumcreaturetype, structuremanager, chunkgenerator, biomesettingsmobs_c, blockposition_mutableblockposition, d2) && spawnercreature_c.test(biomesettingsmobs_c.type, blockposition_mutableblockposition, ichunkaccess)) {
                                        EntityInsentient entityinsentient = a(worldserver, biomesettingsmobs_c.type);

                                        if (entityinsentient == null) {
                                            return;
                                        }

                                        entityinsentient.setPositionRotation(d0, (double) i, d1, worldserver.random.nextFloat() * 360.0F, 0.0F);
                                        if (a(worldserver, entityinsentient, d2)) {
                                            groupdataentity = entityinsentient.prepare(worldserver, worldserver.getDamageScaler(entityinsentient.getChunkCoordinates()), EnumMobSpawn.NATURAL, groupdataentity, (NBTTagCompound) null);
                                            ++j;
                                            ++k1;
                                            worldserver.addAllEntities(entityinsentient);
                                            spawnercreature_a.run(entityinsentient, ichunkaccess);
                                            if (j >= entityinsentient.getMaxSpawnGroup()) {
                                                return;
                                            }

                                            if (entityinsentient.c(k1)) {
                                                break label53;
                                            }
                                        }
                                    }
                                }
                            }

                            ++l1;
                            continue;
                        }
                    }

                    ++k;
                    break;
                }
            }

        }
    }

    private static boolean a(WorldServer worldserver, IChunkAccess ichunkaccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, double d0) {
        return d0 <= 576.0D ? false : (worldserver.getSpawn().a((IPosition) (new Vec3D((double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + 0.5D)), 24.0D) ? false : Objects.equals(new ChunkCoordIntPair(blockposition_mutableblockposition), ichunkaccess.getPos()) || worldserver.f((BlockPosition) blockposition_mutableblockposition));
    }

    private static boolean a(WorldServer worldserver, EnumCreatureType enumcreaturetype, StructureManager structuremanager, ChunkGenerator chunkgenerator, BiomeSettingsMobs.c biomesettingsmobs_c, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, double d0) {
        EntityTypes<?> entitytypes = biomesettingsmobs_c.type;

        if (entitytypes.f() == EnumCreatureType.MISC) {
            return false;
        } else if (!entitytypes.e() && d0 > (double) (entitytypes.f().f() * entitytypes.f().f())) {
            return false;
        } else if (entitytypes.c() && a(worldserver, structuremanager, chunkgenerator, enumcreaturetype, biomesettingsmobs_c, (BlockPosition) blockposition_mutableblockposition)) {
            EntityPositionTypes.Surface entitypositiontypes_surface = EntityPositionTypes.a(entitytypes);

            return !a(entitypositiontypes_surface, (IWorldReader) worldserver, (BlockPosition) blockposition_mutableblockposition, entitytypes) ? false : (!EntityPositionTypes.a(entitytypes, worldserver, EnumMobSpawn.NATURAL, blockposition_mutableblockposition, worldserver.random) ? false : worldserver.b(entitytypes.a((double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + 0.5D)));
        } else {
            return false;
        }
    }

    @Nullable
    private static EntityInsentient a(WorldServer worldserver, EntityTypes<?> entitytypes) {
        try {
            Entity entity = entitytypes.a((World) worldserver);

            if (!(entity instanceof EntityInsentient)) {
                throw new IllegalStateException("Trying to spawn a non-mob: " + IRegistry.ENTITY_TYPE.getKey(entitytypes));
            } else {
                EntityInsentient entityinsentient = (EntityInsentient) entity;

                return entityinsentient;
            }
        } catch (Exception exception) {
            SpawnerCreature.LOGGER.warn("Failed to create mob", exception);
            return null;
        }
    }

    private static boolean a(WorldServer worldserver, EntityInsentient entityinsentient, double d0) {
        return d0 > (double) (entityinsentient.getEntityType().f().f() * entityinsentient.getEntityType().f().f()) && entityinsentient.isTypeNotPersistent(d0) ? false : entityinsentient.a((GeneratorAccess) worldserver, EnumMobSpawn.NATURAL) && entityinsentient.a((IWorldReader) worldserver);
    }

    private static Optional<BiomeSettingsMobs.c> a(WorldServer worldserver, StructureManager structuremanager, ChunkGenerator chunkgenerator, EnumCreatureType enumcreaturetype, Random random, BlockPosition blockposition) {
        BiomeBase biomebase = worldserver.getBiome(blockposition);

        return enumcreaturetype == EnumCreatureType.WATER_AMBIENT && biomebase.t() == BiomeBase.Geography.RIVER && random.nextFloat() < 0.98F ? Optional.empty() : a(worldserver, structuremanager, chunkgenerator, enumcreaturetype, blockposition, biomebase).b(random);
    }

    private static boolean a(WorldServer worldserver, StructureManager structuremanager, ChunkGenerator chunkgenerator, EnumCreatureType enumcreaturetype, BiomeSettingsMobs.c biomesettingsmobs_c, BlockPosition blockposition) {
        return a(worldserver, structuremanager, chunkgenerator, enumcreaturetype, blockposition, (BiomeBase) null).d().contains(biomesettingsmobs_c);
    }

    private static WeightedRandomList<BiomeSettingsMobs.c> a(WorldServer worldserver, StructureManager structuremanager, ChunkGenerator chunkgenerator, EnumCreatureType enumcreaturetype, BlockPosition blockposition, @Nullable BiomeBase biomebase) {
        return enumcreaturetype == EnumCreatureType.MONSTER && worldserver.getType(blockposition.down()).a(Blocks.NETHER_BRICKS) && structuremanager.a(blockposition, false, StructureGenerator.NETHER_BRIDGE).e() ? StructureGenerator.NETHER_BRIDGE.c() : chunkgenerator.getMobsFor(biomebase != null ? biomebase : worldserver.getBiome(blockposition), structuremanager, enumcreaturetype, blockposition);
    }

    private static BlockPosition getRandomPosition(World world, Chunk chunk) {
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
        int i = chunkcoordintpair.d() + world.random.nextInt(16);
        int j = chunkcoordintpair.e() + world.random.nextInt(16);
        int k = chunk.getHighestBlock(HeightMap.Type.WORLD_SURFACE, i, j) + 1;
        int l = MathHelper.b(world.random, world.getMinBuildHeight(), k);

        return new BlockPosition(i, l, j);
    }

    public static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid, EntityTypes<?> entitytypes) {
        return iblockdata.r(iblockaccess, blockposition) ? false : (iblockdata.isPowerSource() ? false : (!fluid.isEmpty() ? false : (iblockdata.a((Tag) TagsBlock.PREVENT_MOB_SPAWNING_INSIDE) ? false : !entitytypes.a(iblockdata))));
    }

    public static boolean a(EntityPositionTypes.Surface entitypositiontypes_surface, IWorldReader iworldreader, BlockPosition blockposition, @Nullable EntityTypes<?> entitytypes) {
        if (entitypositiontypes_surface == EntityPositionTypes.Surface.NO_RESTRICTIONS) {
            return true;
        } else if (entitytypes != null && iworldreader.getWorldBorder().a(blockposition)) {
            IBlockData iblockdata = iworldreader.getType(blockposition);
            Fluid fluid = iworldreader.getFluid(blockposition);
            BlockPosition blockposition1 = blockposition.up();
            BlockPosition blockposition2 = blockposition.down();

            switch (entitypositiontypes_surface) {
                case IN_WATER:
                    return fluid.a((Tag) TagsFluid.WATER) && iworldreader.getFluid(blockposition2).a((Tag) TagsFluid.WATER) && !iworldreader.getType(blockposition1).isOccluding(iworldreader, blockposition1);
                case IN_LAVA:
                    return fluid.a((Tag) TagsFluid.LAVA);
                case ON_GROUND:
                default:
                    IBlockData iblockdata1 = iworldreader.getType(blockposition2);

                    return !iblockdata1.a((IBlockAccess) iworldreader, blockposition2, entitytypes) ? false : a((IBlockAccess) iworldreader, blockposition, iblockdata, fluid, entitytypes) && a((IBlockAccess) iworldreader, blockposition1, iworldreader.getType(blockposition1), iworldreader.getFluid(blockposition1), entitytypes);
            }
        } else {
            return false;
        }
    }

    public static void a(WorldAccess worldaccess, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, Random random) {
        BiomeSettingsMobs biomesettingsmobs = biomebase.b();
        WeightedRandomList<BiomeSettingsMobs.c> weightedrandomlist = biomesettingsmobs.a(EnumCreatureType.CREATURE);

        if (!weightedrandomlist.c()) {
            int i = chunkcoordintpair.d();
            int j = chunkcoordintpair.e();

            while (random.nextFloat() < biomesettingsmobs.a()) {
                Optional<BiomeSettingsMobs.c> optional = weightedrandomlist.b(random);

                if (optional.isPresent()) {
                    BiomeSettingsMobs.c biomesettingsmobs_c = (BiomeSettingsMobs.c) optional.get();
                    int k = biomesettingsmobs_c.minCount + random.nextInt(1 + biomesettingsmobs_c.maxCount - biomesettingsmobs_c.minCount);
                    GroupDataEntity groupdataentity = null;
                    int l = i + random.nextInt(16);
                    int i1 = j + random.nextInt(16);
                    int j1 = l;
                    int k1 = i1;

                    for (int l1 = 0; l1 < k; ++l1) {
                        boolean flag = false;

                        for (int i2 = 0; !flag && i2 < 4; ++i2) {
                            BlockPosition blockposition = a(worldaccess, biomesettingsmobs_c.type, l, i1);

                            if (biomesettingsmobs_c.type.c() && a(EntityPositionTypes.a(biomesettingsmobs_c.type), (IWorldReader) worldaccess, blockposition, biomesettingsmobs_c.type)) {
                                float f = biomesettingsmobs_c.type.k();
                                double d0 = MathHelper.a((double) l, (double) i + (double) f, (double) i + 16.0D - (double) f);
                                double d1 = MathHelper.a((double) i1, (double) j + (double) f, (double) j + 16.0D - (double) f);

                                if (!worldaccess.b(biomesettingsmobs_c.type.a(d0, (double) blockposition.getY(), d1)) || !EntityPositionTypes.a(biomesettingsmobs_c.type, worldaccess, EnumMobSpawn.CHUNK_GENERATION, new BlockPosition(d0, (double) blockposition.getY(), d1), worldaccess.getRandom())) {
                                    continue;
                                }

                                Entity entity;

                                try {
                                    entity = biomesettingsmobs_c.type.a((World) worldaccess.getLevel());
                                } catch (Exception exception) {
                                    SpawnerCreature.LOGGER.warn("Failed to create mob", exception);
                                    continue;
                                }

                                entity.setPositionRotation(d0, (double) blockposition.getY(), d1, random.nextFloat() * 360.0F, 0.0F);
                                if (entity instanceof EntityInsentient) {
                                    EntityInsentient entityinsentient = (EntityInsentient) entity;

                                    if (entityinsentient.a((GeneratorAccess) worldaccess, EnumMobSpawn.CHUNK_GENERATION) && entityinsentient.a((IWorldReader) worldaccess)) {
                                        groupdataentity = entityinsentient.prepare(worldaccess, worldaccess.getDamageScaler(entityinsentient.getChunkCoordinates()), EnumMobSpawn.CHUNK_GENERATION, groupdataentity, (NBTTagCompound) null);
                                        worldaccess.addAllEntities(entityinsentient);
                                        flag = true;
                                    }
                                }
                            }

                            l += random.nextInt(5) - random.nextInt(5);

                            for (i1 += random.nextInt(5) - random.nextInt(5); l < i || l >= i + 16 || i1 < j || i1 >= j + 16; i1 = k1 + random.nextInt(5) - random.nextInt(5)) {
                                l = j1 + random.nextInt(5) - random.nextInt(5);
                            }
                        }
                    }
                }
            }

        }
    }

    private static BlockPosition a(IWorldReader iworldreader, EntityTypes<?> entitytypes, int i, int j) {
        int k = iworldreader.a(EntityPositionTypes.b(entitytypes), i, j);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, k, j);

        if (iworldreader.getDimensionManager().hasCeiling()) {
            do {
                blockposition_mutableblockposition.c(EnumDirection.DOWN);
            } while (!iworldreader.getType(blockposition_mutableblockposition).isAir());

            do {
                blockposition_mutableblockposition.c(EnumDirection.DOWN);
            } while (iworldreader.getType(blockposition_mutableblockposition).isAir() && blockposition_mutableblockposition.getY() > iworldreader.getMinBuildHeight());
        }

        if (EntityPositionTypes.a(entitytypes) == EntityPositionTypes.Surface.ON_GROUND) {
            BlockPosition blockposition = blockposition_mutableblockposition.down();

            if (iworldreader.getType(blockposition).a((IBlockAccess) iworldreader, blockposition, PathMode.LAND)) {
                return blockposition;
            }
        }

        return blockposition_mutableblockposition.immutableCopy();
    }

    @FunctionalInterface
    public interface b {

        void query(long i, Consumer<Chunk> consumer);
    }

    public static class d {

        private final int spawnableChunkCount;
        private final Object2IntOpenHashMap<EnumCreatureType> mobCategoryCounts;
        private final SpawnerCreatureProbabilities spawnPotential;
        private final Object2IntMap<EnumCreatureType> unmodifiableMobCategoryCounts;
        @Nullable
        private BlockPosition lastCheckedPos;
        @Nullable
        private EntityTypes<?> lastCheckedType;
        private double lastCharge;

        d(int i, Object2IntOpenHashMap<EnumCreatureType> object2intopenhashmap, SpawnerCreatureProbabilities spawnercreatureprobabilities) {
            this.spawnableChunkCount = i;
            this.mobCategoryCounts = object2intopenhashmap;
            this.spawnPotential = spawnercreatureprobabilities;
            this.unmodifiableMobCategoryCounts = Object2IntMaps.unmodifiable(object2intopenhashmap);
        }

        private boolean a(EntityTypes<?> entitytypes, BlockPosition blockposition, IChunkAccess ichunkaccess) {
            this.lastCheckedPos = blockposition;
            this.lastCheckedType = entitytypes;
            BiomeSettingsMobs.b biomesettingsmobs_b = SpawnerCreature.a(blockposition, ichunkaccess).b().a(entitytypes);

            if (biomesettingsmobs_b == null) {
                this.lastCharge = 0.0D;
                return true;
            } else {
                double d0 = biomesettingsmobs_b.b();

                this.lastCharge = d0;
                double d1 = this.spawnPotential.b(blockposition, d0);

                return d1 <= biomesettingsmobs_b.a();
            }
        }

        private void a(EntityInsentient entityinsentient, IChunkAccess ichunkaccess) {
            EntityTypes<?> entitytypes = entityinsentient.getEntityType();
            BlockPosition blockposition = entityinsentient.getChunkCoordinates();
            double d0;

            if (blockposition.equals(this.lastCheckedPos) && entitytypes == this.lastCheckedType) {
                d0 = this.lastCharge;
            } else {
                BiomeSettingsMobs.b biomesettingsmobs_b = SpawnerCreature.a(blockposition, ichunkaccess).b().a(entitytypes);

                if (biomesettingsmobs_b != null) {
                    d0 = biomesettingsmobs_b.b();
                } else {
                    d0 = 0.0D;
                }
            }

            this.spawnPotential.a(blockposition, d0);
            this.mobCategoryCounts.addTo(entitytypes.f(), 1);
        }

        public int a() {
            return this.spawnableChunkCount;
        }

        public Object2IntMap<EnumCreatureType> b() {
            return this.unmodifiableMobCategoryCounts;
        }

        boolean a(EnumCreatureType enumcreaturetype) {
            int i = enumcreaturetype.b() * this.spawnableChunkCount / SpawnerCreature.MAGIC_NUMBER;

            return this.mobCategoryCounts.getInt(enumcreaturetype) < i;
        }
    }

    @FunctionalInterface
    public interface c {

        boolean test(EntityTypes<?> entitytypes, BlockPosition blockposition, IChunkAccess ichunkaccess);
    }

    @FunctionalInterface
    public interface a {

        void run(EntityInsentient entityinsentient, IChunkAccess ichunkaccess);
    }
}
