package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.Vec3D;
import org.slf4j.Logger;

public final class SpawnerCreature {

    private static final Logger LOGGER = LogUtils.getLogger();
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

    public static SpawnerCreature.d createState(int i, Iterable<Entity> iterable, SpawnerCreature.b spawnercreature_b, LocalMobCapCalculator localmobcapcalculator) {
        SpawnerCreatureProbabilities spawnercreatureprobabilities = new SpawnerCreatureProbabilities();
        Object2IntOpenHashMap<EnumCreatureType> object2intopenhashmap = new Object2IntOpenHashMap();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityInsentient) {
                EntityInsentient entityinsentient = (EntityInsentient) entity;

                if (entityinsentient.isPersistenceRequired() || entityinsentient.requiresCustomPersistence()) {
                    continue;
                }
            }

            EnumCreatureType enumcreaturetype = entity.getType().getCategory();

            if (enumcreaturetype != EnumCreatureType.MISC) {
                BlockPosition blockposition = entity.blockPosition();

                spawnercreature_b.query(ChunkCoordIntPair.asLong(blockposition), (chunk) -> {
                    BiomeSettingsMobs.b biomesettingsmobs_b = getRoughBiome(blockposition, chunk).getMobSettings().getMobSpawnCost(entity.getType());

                    if (biomesettingsmobs_b != null) {
                        spawnercreatureprobabilities.addCharge(entity.blockPosition(), biomesettingsmobs_b.charge());
                    }

                    if (entity instanceof EntityInsentient) {
                        localmobcapcalculator.addMob(chunk.getPos(), enumcreaturetype);
                    }

                    object2intopenhashmap.addTo(enumcreaturetype, 1);
                });
            }
        }

        return new SpawnerCreature.d(i, object2intopenhashmap, spawnercreatureprobabilities, localmobcapcalculator);
    }

    static BiomeBase getRoughBiome(BlockPosition blockposition, IChunkAccess ichunkaccess) {
        return (BiomeBase) ichunkaccess.getNoiseBiome(QuartPos.fromBlock(blockposition.getX()), QuartPos.fromBlock(blockposition.getY()), QuartPos.fromBlock(blockposition.getZ())).value();
    }

    public static void spawnForChunk(WorldServer worldserver, Chunk chunk, SpawnerCreature.d spawnercreature_d, boolean flag, boolean flag1, boolean flag2) {
        worldserver.getProfiler().push("spawner");
        EnumCreatureType[] aenumcreaturetype = SpawnerCreature.SPAWNING_CATEGORIES;
        int i = aenumcreaturetype.length;

        for (int j = 0; j < i; ++j) {
            EnumCreatureType enumcreaturetype = aenumcreaturetype[j];

            if ((flag || !enumcreaturetype.isFriendly()) && (flag1 || enumcreaturetype.isFriendly()) && (flag2 || !enumcreaturetype.isPersistent()) && spawnercreature_d.canSpawnForCategory(enumcreaturetype, chunk.getPos())) {
                Objects.requireNonNull(spawnercreature_d);
                SpawnerCreature.c spawnercreature_c = spawnercreature_d::canSpawn;

                Objects.requireNonNull(spawnercreature_d);
                spawnCategoryForChunk(enumcreaturetype, worldserver, chunk, spawnercreature_c, spawnercreature_d::afterSpawn);
            }
        }

        worldserver.getProfiler().pop();
    }

    public static void spawnCategoryForChunk(EnumCreatureType enumcreaturetype, WorldServer worldserver, Chunk chunk, SpawnerCreature.c spawnercreature_c, SpawnerCreature.a spawnercreature_a) {
        BlockPosition blockposition = getRandomPosWithin(worldserver, chunk);

        if (blockposition.getY() >= worldserver.getMinBuildHeight() + 1) {
            spawnCategoryForPosition(enumcreaturetype, worldserver, chunk, blockposition, spawnercreature_c, spawnercreature_a);
        }
    }

    @VisibleForDebug
    public static void spawnCategoryForPosition(EnumCreatureType enumcreaturetype, WorldServer worldserver, BlockPosition blockposition) {
        spawnCategoryForPosition(enumcreaturetype, worldserver, worldserver.getChunk(blockposition), blockposition, (entitytypes, blockposition1, ichunkaccess) -> {
            return true;
        }, (entityinsentient, ichunkaccess) -> {
        });
    }

    public static void spawnCategoryForPosition(EnumCreatureType enumcreaturetype, WorldServer worldserver, IChunkAccess ichunkaccess, BlockPosition blockposition, SpawnerCreature.c spawnercreature_c, SpawnerCreature.a spawnercreature_a) {
        StructureManager structuremanager = worldserver.structureManager();
        ChunkGenerator chunkgenerator = worldserver.getChunkSource().getGenerator();
        int i = blockposition.getY();
        IBlockData iblockdata = ichunkaccess.getBlockState(blockposition);

        if (!iblockdata.isRedstoneConductor(ichunkaccess, blockposition)) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            int j = 0;
            int k = 0;

            while (k < 3) {
                int l = blockposition.getX();
                int i1 = blockposition.getZ();
                boolean flag = true;
                BiomeSettingsMobs.c biomesettingsmobs_c = null;
                GroupDataEntity groupdataentity = null;
                int j1 = MathHelper.ceil(worldserver.random.nextFloat() * 4.0F);
                int k1 = 0;
                int l1 = 0;

                while (true) {
                    if (l1 < j1) {
                        label53:
                        {
                            l += worldserver.random.nextInt(6) - worldserver.random.nextInt(6);
                            i1 += worldserver.random.nextInt(6) - worldserver.random.nextInt(6);
                            blockposition_mutableblockposition.set(l, i, i1);
                            double d0 = (double) l + 0.5D;
                            double d1 = (double) i1 + 0.5D;
                            EntityHuman entityhuman = worldserver.getNearestPlayer(d0, (double) i, d1, -1.0D, false);

                            if (entityhuman != null) {
                                double d2 = entityhuman.distanceToSqr(d0, (double) i, d1);

                                if (isRightDistanceToPlayerAndSpawnPoint(worldserver, ichunkaccess, blockposition_mutableblockposition, d2)) {
                                    if (biomesettingsmobs_c == null) {
                                        Optional<BiomeSettingsMobs.c> optional = getRandomSpawnMobAt(worldserver, structuremanager, chunkgenerator, enumcreaturetype, worldserver.random, blockposition_mutableblockposition);

                                        if (optional.isEmpty()) {
                                            break label53;
                                        }

                                        biomesettingsmobs_c = (BiomeSettingsMobs.c) optional.get();
                                        j1 = biomesettingsmobs_c.minCount + worldserver.random.nextInt(1 + biomesettingsmobs_c.maxCount - biomesettingsmobs_c.minCount);
                                    }

                                    if (isValidSpawnPostitionForType(worldserver, enumcreaturetype, structuremanager, chunkgenerator, biomesettingsmobs_c, blockposition_mutableblockposition, d2) && spawnercreature_c.test(biomesettingsmobs_c.type, blockposition_mutableblockposition, ichunkaccess)) {
                                        EntityInsentient entityinsentient = getMobForSpawn(worldserver, biomesettingsmobs_c.type);

                                        if (entityinsentient == null) {
                                            return;
                                        }

                                        entityinsentient.moveTo(d0, (double) i, d1, worldserver.random.nextFloat() * 360.0F, 0.0F);
                                        if (isValidPositionForMob(worldserver, entityinsentient, d2)) {
                                            groupdataentity = entityinsentient.finalizeSpawn(worldserver, worldserver.getCurrentDifficultyAt(entityinsentient.blockPosition()), EnumMobSpawn.NATURAL, groupdataentity, (NBTTagCompound) null);
                                            ++j;
                                            ++k1;
                                            worldserver.addFreshEntityWithPassengers(entityinsentient);
                                            spawnercreature_a.run(entityinsentient, ichunkaccess);
                                            if (j >= entityinsentient.getMaxSpawnClusterSize()) {
                                                return;
                                            }

                                            if (entityinsentient.isMaxGroupSizeReached(k1)) {
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

    private static boolean isRightDistanceToPlayerAndSpawnPoint(WorldServer worldserver, IChunkAccess ichunkaccess, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, double d0) {
        return d0 <= 576.0D ? false : (worldserver.getSharedSpawnPos().closerToCenterThan(new Vec3D((double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + 0.5D), 24.0D) ? false : Objects.equals(new ChunkCoordIntPair(blockposition_mutableblockposition), ichunkaccess.getPos()) || worldserver.isNaturalSpawningAllowed((BlockPosition) blockposition_mutableblockposition));
    }

    private static boolean isValidSpawnPostitionForType(WorldServer worldserver, EnumCreatureType enumcreaturetype, StructureManager structuremanager, ChunkGenerator chunkgenerator, BiomeSettingsMobs.c biomesettingsmobs_c, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, double d0) {
        EntityTypes<?> entitytypes = biomesettingsmobs_c.type;

        if (entitytypes.getCategory() == EnumCreatureType.MISC) {
            return false;
        } else if (!entitytypes.canSpawnFarFromPlayer() && d0 > (double) (entitytypes.getCategory().getDespawnDistance() * entitytypes.getCategory().getDespawnDistance())) {
            return false;
        } else if (entitytypes.canSummon() && canSpawnMobAt(worldserver, structuremanager, chunkgenerator, enumcreaturetype, biomesettingsmobs_c, blockposition_mutableblockposition)) {
            EntityPositionTypes.Surface entitypositiontypes_surface = EntityPositionTypes.getPlacementType(entitytypes);

            return !isSpawnPositionOk(entitypositiontypes_surface, worldserver, blockposition_mutableblockposition, entitytypes) ? false : (!EntityPositionTypes.checkSpawnRules(entitytypes, worldserver, EnumMobSpawn.NATURAL, blockposition_mutableblockposition, worldserver.random) ? false : worldserver.noCollision(entitytypes.getAABB((double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + 0.5D)));
        } else {
            return false;
        }
    }

    @Nullable
    private static EntityInsentient getMobForSpawn(WorldServer worldserver, EntityTypes<?> entitytypes) {
        try {
            Entity entity = entitytypes.create(worldserver);

            if (entity instanceof EntityInsentient) {
                EntityInsentient entityinsentient = (EntityInsentient) entity;

                return entityinsentient;
            }

            SpawnerCreature.LOGGER.warn("Can't spawn entity of type: {}", BuiltInRegistries.ENTITY_TYPE.getKey(entitytypes));
        } catch (Exception exception) {
            SpawnerCreature.LOGGER.warn("Failed to create mob", exception);
        }

        return null;
    }

    private static boolean isValidPositionForMob(WorldServer worldserver, EntityInsentient entityinsentient, double d0) {
        return d0 > (double) (entityinsentient.getType().getCategory().getDespawnDistance() * entityinsentient.getType().getCategory().getDespawnDistance()) && entityinsentient.removeWhenFarAway(d0) ? false : entityinsentient.checkSpawnRules(worldserver, EnumMobSpawn.NATURAL) && entityinsentient.checkSpawnObstruction(worldserver);
    }

    private static Optional<BiomeSettingsMobs.c> getRandomSpawnMobAt(WorldServer worldserver, StructureManager structuremanager, ChunkGenerator chunkgenerator, EnumCreatureType enumcreaturetype, RandomSource randomsource, BlockPosition blockposition) {
        Holder<BiomeBase> holder = worldserver.getBiome(blockposition);

        return enumcreaturetype == EnumCreatureType.WATER_AMBIENT && holder.is(BiomeTags.REDUCED_WATER_AMBIENT_SPAWNS) && randomsource.nextFloat() < 0.98F ? Optional.empty() : mobsAt(worldserver, structuremanager, chunkgenerator, enumcreaturetype, blockposition, holder).getRandom(randomsource);
    }

    private static boolean canSpawnMobAt(WorldServer worldserver, StructureManager structuremanager, ChunkGenerator chunkgenerator, EnumCreatureType enumcreaturetype, BiomeSettingsMobs.c biomesettingsmobs_c, BlockPosition blockposition) {
        return mobsAt(worldserver, structuremanager, chunkgenerator, enumcreaturetype, blockposition, (Holder) null).unwrap().contains(biomesettingsmobs_c);
    }

    private static WeightedRandomList<BiomeSettingsMobs.c> mobsAt(WorldServer worldserver, StructureManager structuremanager, ChunkGenerator chunkgenerator, EnumCreatureType enumcreaturetype, BlockPosition blockposition, @Nullable Holder<BiomeBase> holder) {
        return isInNetherFortressBounds(blockposition, worldserver, enumcreaturetype, structuremanager) ? NetherFortressStructure.FORTRESS_ENEMIES : chunkgenerator.getMobsAt(holder != null ? holder : worldserver.getBiome(blockposition), structuremanager, enumcreaturetype, blockposition);
    }

    public static boolean isInNetherFortressBounds(BlockPosition blockposition, WorldServer worldserver, EnumCreatureType enumcreaturetype, StructureManager structuremanager) {
        if (enumcreaturetype == EnumCreatureType.MONSTER && worldserver.getBlockState(blockposition.below()).is(Blocks.NETHER_BRICKS)) {
            Structure structure = (Structure) structuremanager.registryAccess().registryOrThrow(Registries.STRUCTURE).get(BuiltinStructures.FORTRESS);

            return structure == null ? false : structuremanager.getStructureAt(blockposition, structure).isValid();
        } else {
            return false;
        }
    }

    private static BlockPosition getRandomPosWithin(World world, Chunk chunk) {
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
        int i = chunkcoordintpair.getMinBlockX() + world.random.nextInt(16);
        int j = chunkcoordintpair.getMinBlockZ() + world.random.nextInt(16);
        int k = chunk.getHeight(HeightMap.Type.WORLD_SURFACE, i, j) + 1;
        int l = MathHelper.randomBetweenInclusive(world.random, world.getMinBuildHeight(), k);

        return new BlockPosition(i, l, j);
    }

    public static boolean isValidEmptySpawnBlock(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid, EntityTypes<?> entitytypes) {
        return iblockdata.isCollisionShapeFullBlock(iblockaccess, blockposition) ? false : (iblockdata.isSignalSource() ? false : (!fluid.isEmpty() ? false : (iblockdata.is(TagsBlock.PREVENT_MOB_SPAWNING_INSIDE) ? false : !entitytypes.isBlockDangerous(iblockdata))));
    }

    public static boolean isSpawnPositionOk(EntityPositionTypes.Surface entitypositiontypes_surface, IWorldReader iworldreader, BlockPosition blockposition, @Nullable EntityTypes<?> entitytypes) {
        if (entitypositiontypes_surface == EntityPositionTypes.Surface.NO_RESTRICTIONS) {
            return true;
        } else if (entitytypes != null && iworldreader.getWorldBorder().isWithinBounds(blockposition)) {
            IBlockData iblockdata = iworldreader.getBlockState(blockposition);
            Fluid fluid = iworldreader.getFluidState(blockposition);
            BlockPosition blockposition1 = blockposition.above();
            BlockPosition blockposition2 = blockposition.below();

            switch (entitypositiontypes_surface) {
                case IN_WATER:
                    return fluid.is(TagsFluid.WATER) && !iworldreader.getBlockState(blockposition1).isRedstoneConductor(iworldreader, blockposition1);
                case IN_LAVA:
                    return fluid.is(TagsFluid.LAVA);
                case ON_GROUND:
                default:
                    IBlockData iblockdata1 = iworldreader.getBlockState(blockposition2);

                    return !iblockdata1.isValidSpawn(iworldreader, blockposition2, entitytypes) ? false : isValidEmptySpawnBlock(iworldreader, blockposition, iblockdata, fluid, entitytypes) && isValidEmptySpawnBlock(iworldreader, blockposition1, iworldreader.getBlockState(blockposition1), iworldreader.getFluidState(blockposition1), entitytypes);
            }
        } else {
            return false;
        }
    }

    public static void spawnMobsForChunkGeneration(WorldAccess worldaccess, Holder<BiomeBase> holder, ChunkCoordIntPair chunkcoordintpair, RandomSource randomsource) {
        BiomeSettingsMobs biomesettingsmobs = ((BiomeBase) holder.value()).getMobSettings();
        WeightedRandomList<BiomeSettingsMobs.c> weightedrandomlist = biomesettingsmobs.getMobs(EnumCreatureType.CREATURE);

        if (!weightedrandomlist.isEmpty()) {
            int i = chunkcoordintpair.getMinBlockX();
            int j = chunkcoordintpair.getMinBlockZ();

            while (randomsource.nextFloat() < biomesettingsmobs.getCreatureProbability()) {
                Optional<BiomeSettingsMobs.c> optional = weightedrandomlist.getRandom(randomsource);

                if (optional.isPresent()) {
                    BiomeSettingsMobs.c biomesettingsmobs_c = (BiomeSettingsMobs.c) optional.get();
                    int k = biomesettingsmobs_c.minCount + randomsource.nextInt(1 + biomesettingsmobs_c.maxCount - biomesettingsmobs_c.minCount);
                    GroupDataEntity groupdataentity = null;
                    int l = i + randomsource.nextInt(16);
                    int i1 = j + randomsource.nextInt(16);
                    int j1 = l;
                    int k1 = i1;

                    for (int l1 = 0; l1 < k; ++l1) {
                        boolean flag = false;

                        for (int i2 = 0; !flag && i2 < 4; ++i2) {
                            BlockPosition blockposition = getTopNonCollidingPos(worldaccess, biomesettingsmobs_c.type, l, i1);

                            if (biomesettingsmobs_c.type.canSummon() && isSpawnPositionOk(EntityPositionTypes.getPlacementType(biomesettingsmobs_c.type), worldaccess, blockposition, biomesettingsmobs_c.type)) {
                                float f = biomesettingsmobs_c.type.getWidth();
                                double d0 = MathHelper.clamp((double) l, (double) i + (double) f, (double) i + 16.0D - (double) f);
                                double d1 = MathHelper.clamp((double) i1, (double) j + (double) f, (double) j + 16.0D - (double) f);

                                if (!worldaccess.noCollision(biomesettingsmobs_c.type.getAABB(d0, (double) blockposition.getY(), d1)) || !EntityPositionTypes.checkSpawnRules(biomesettingsmobs_c.type, worldaccess, EnumMobSpawn.CHUNK_GENERATION, BlockPosition.containing(d0, (double) blockposition.getY(), d1), worldaccess.getRandom())) {
                                    continue;
                                }

                                Entity entity;

                                try {
                                    entity = biomesettingsmobs_c.type.create(worldaccess.getLevel());
                                } catch (Exception exception) {
                                    SpawnerCreature.LOGGER.warn("Failed to create mob", exception);
                                    continue;
                                }

                                if (entity == null) {
                                    continue;
                                }

                                entity.moveTo(d0, (double) blockposition.getY(), d1, randomsource.nextFloat() * 360.0F, 0.0F);
                                if (entity instanceof EntityInsentient) {
                                    EntityInsentient entityinsentient = (EntityInsentient) entity;

                                    if (entityinsentient.checkSpawnRules(worldaccess, EnumMobSpawn.CHUNK_GENERATION) && entityinsentient.checkSpawnObstruction(worldaccess)) {
                                        groupdataentity = entityinsentient.finalizeSpawn(worldaccess, worldaccess.getCurrentDifficultyAt(entityinsentient.blockPosition()), EnumMobSpawn.CHUNK_GENERATION, groupdataentity, (NBTTagCompound) null);
                                        worldaccess.addFreshEntityWithPassengers(entityinsentient);
                                        flag = true;
                                    }
                                }
                            }

                            l += randomsource.nextInt(5) - randomsource.nextInt(5);

                            for (i1 += randomsource.nextInt(5) - randomsource.nextInt(5); l < i || l >= i + 16 || i1 < j || i1 >= j + 16; i1 = k1 + randomsource.nextInt(5) - randomsource.nextInt(5)) {
                                l = j1 + randomsource.nextInt(5) - randomsource.nextInt(5);
                            }
                        }
                    }
                }
            }

        }
    }

    private static BlockPosition getTopNonCollidingPos(IWorldReader iworldreader, EntityTypes<?> entitytypes, int i, int j) {
        int k = iworldreader.getHeight(EntityPositionTypes.getHeightmapType(entitytypes), i, j);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, k, j);

        if (iworldreader.dimensionType().hasCeiling()) {
            do {
                blockposition_mutableblockposition.move(EnumDirection.DOWN);
            } while (!iworldreader.getBlockState(blockposition_mutableblockposition).isAir());

            do {
                blockposition_mutableblockposition.move(EnumDirection.DOWN);
            } while (iworldreader.getBlockState(blockposition_mutableblockposition).isAir() && blockposition_mutableblockposition.getY() > iworldreader.getMinBuildHeight());
        }

        if (EntityPositionTypes.getPlacementType(entitytypes) == EntityPositionTypes.Surface.ON_GROUND) {
            BlockPosition blockposition = blockposition_mutableblockposition.below();

            if (iworldreader.getBlockState(blockposition).isPathfindable(iworldreader, blockposition, PathMode.LAND)) {
                return blockposition;
            }
        }

        return blockposition_mutableblockposition.immutable();
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
        private final LocalMobCapCalculator localMobCapCalculator;
        @Nullable
        private BlockPosition lastCheckedPos;
        @Nullable
        private EntityTypes<?> lastCheckedType;
        private double lastCharge;

        d(int i, Object2IntOpenHashMap<EnumCreatureType> object2intopenhashmap, SpawnerCreatureProbabilities spawnercreatureprobabilities, LocalMobCapCalculator localmobcapcalculator) {
            this.spawnableChunkCount = i;
            this.mobCategoryCounts = object2intopenhashmap;
            this.spawnPotential = spawnercreatureprobabilities;
            this.localMobCapCalculator = localmobcapcalculator;
            this.unmodifiableMobCategoryCounts = Object2IntMaps.unmodifiable(object2intopenhashmap);
        }

        private boolean canSpawn(EntityTypes<?> entitytypes, BlockPosition blockposition, IChunkAccess ichunkaccess) {
            this.lastCheckedPos = blockposition;
            this.lastCheckedType = entitytypes;
            BiomeSettingsMobs.b biomesettingsmobs_b = SpawnerCreature.getRoughBiome(blockposition, ichunkaccess).getMobSettings().getMobSpawnCost(entitytypes);

            if (biomesettingsmobs_b == null) {
                this.lastCharge = 0.0D;
                return true;
            } else {
                double d0 = biomesettingsmobs_b.charge();

                this.lastCharge = d0;
                double d1 = this.spawnPotential.getPotentialEnergyChange(blockposition, d0);

                return d1 <= biomesettingsmobs_b.energyBudget();
            }
        }

        private void afterSpawn(EntityInsentient entityinsentient, IChunkAccess ichunkaccess) {
            EntityTypes<?> entitytypes = entityinsentient.getType();
            BlockPosition blockposition = entityinsentient.blockPosition();
            double d0;

            if (blockposition.equals(this.lastCheckedPos) && entitytypes == this.lastCheckedType) {
                d0 = this.lastCharge;
            } else {
                BiomeSettingsMobs.b biomesettingsmobs_b = SpawnerCreature.getRoughBiome(blockposition, ichunkaccess).getMobSettings().getMobSpawnCost(entitytypes);

                if (biomesettingsmobs_b != null) {
                    d0 = biomesettingsmobs_b.charge();
                } else {
                    d0 = 0.0D;
                }
            }

            this.spawnPotential.addCharge(blockposition, d0);
            EnumCreatureType enumcreaturetype = entitytypes.getCategory();

            this.mobCategoryCounts.addTo(enumcreaturetype, 1);
            this.localMobCapCalculator.addMob(new ChunkCoordIntPair(blockposition), enumcreaturetype);
        }

        public int getSpawnableChunkCount() {
            return this.spawnableChunkCount;
        }

        public Object2IntMap<EnumCreatureType> getMobCategoryCounts() {
            return this.unmodifiableMobCategoryCounts;
        }

        boolean canSpawnForCategory(EnumCreatureType enumcreaturetype, ChunkCoordIntPair chunkcoordintpair) {
            int i = enumcreaturetype.getMaxInstancesPerChunk() * this.spawnableChunkCount / SpawnerCreature.MAGIC_NUMBER;

            return this.mobCategoryCounts.getInt(enumcreaturetype) >= i ? false : this.localMobCapCalculator.canSpawn(enumcreaturetype, chunkcoordintpair);
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
