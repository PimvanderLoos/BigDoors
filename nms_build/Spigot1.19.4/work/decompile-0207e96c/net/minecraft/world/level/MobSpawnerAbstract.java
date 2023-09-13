package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPositionTypes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AxisAlignedBB;
import org.slf4j.Logger;

public abstract class MobSpawnerAbstract {

    public static final String SPAWN_DATA_TAG = "SpawnData";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int EVENT_SPAWN = 1;
    public int spawnDelay = 20;
    public SimpleWeightedRandomList<MobSpawnerData> spawnPotentials = SimpleWeightedRandomList.empty();
    @Nullable
    public MobSpawnerData nextSpawnData;
    private double spin;
    private double oSpin;
    public int minSpawnDelay = 200;
    public int maxSpawnDelay = 800;
    public int spawnCount = 4;
    @Nullable
    private Entity displayEntity;
    public int maxNearbyEntities = 6;
    public int requiredPlayerRange = 16;
    public int spawnRange = 4;

    public MobSpawnerAbstract() {}

    public void setEntityId(EntityTypes<?> entitytypes, @Nullable World world, RandomSource randomsource, BlockPosition blockposition) {
        this.getOrCreateNextSpawnData(world, randomsource, blockposition).getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entitytypes).toString());
    }

    private boolean isNearPlayer(World world, BlockPosition blockposition) {
        return world.hasNearbyAlivePlayer((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, (double) this.requiredPlayerRange);
    }

    public void clientTick(World world, BlockPosition blockposition) {
        if (!this.isNearPlayer(world, blockposition)) {
            this.oSpin = this.spin;
        } else if (this.displayEntity != null) {
            RandomSource randomsource = world.getRandom();
            double d0 = (double) blockposition.getX() + randomsource.nextDouble();
            double d1 = (double) blockposition.getY() + randomsource.nextDouble();
            double d2 = (double) blockposition.getZ() + randomsource.nextDouble();

            world.addParticle(Particles.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            world.addParticle(Particles.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }

            this.oSpin = this.spin;
            this.spin = (this.spin + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
        }

    }

    public void serverTick(WorldServer worldserver, BlockPosition blockposition) {
        if (this.isNearPlayer(worldserver, blockposition)) {
            if (this.spawnDelay == -1) {
                this.delay(worldserver, blockposition);
            }

            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            } else {
                boolean flag = false;
                RandomSource randomsource = worldserver.getRandom();
                MobSpawnerData mobspawnerdata = this.getOrCreateNextSpawnData(worldserver, randomsource, blockposition);

                for (int i = 0; i < this.spawnCount; ++i) {
                    NBTTagCompound nbttagcompound = mobspawnerdata.getEntityToSpawn();
                    Optional<EntityTypes<?>> optional = EntityTypes.by(nbttagcompound);

                    if (optional.isEmpty()) {
                        this.delay(worldserver, blockposition);
                        return;
                    }

                    NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);
                    int j = nbttaglist.size();
                    double d0 = j >= 1 ? nbttaglist.getDouble(0) : (double) blockposition.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) this.spawnRange + 0.5D;
                    double d1 = j >= 2 ? nbttaglist.getDouble(1) : (double) (blockposition.getY() + randomsource.nextInt(3) - 1);
                    double d2 = j >= 3 ? nbttaglist.getDouble(2) : (double) blockposition.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) this.spawnRange + 0.5D;

                    if (worldserver.noCollision(((EntityTypes) optional.get()).getAABB(d0, d1, d2))) {
                        BlockPosition blockposition1 = BlockPosition.containing(d0, d1, d2);

                        if (mobspawnerdata.getCustomSpawnRules().isPresent()) {
                            if (!((EntityTypes) optional.get()).getCategory().isFriendly() && worldserver.getDifficulty() == EnumDifficulty.PEACEFUL) {
                                continue;
                            }

                            MobSpawnerData.a mobspawnerdata_a = (MobSpawnerData.a) mobspawnerdata.getCustomSpawnRules().get();

                            if (!mobspawnerdata_a.blockLightLimit().isValueInRange(worldserver.getBrightness(EnumSkyBlock.BLOCK, blockposition1)) || !mobspawnerdata_a.skyLightLimit().isValueInRange(worldserver.getBrightness(EnumSkyBlock.SKY, blockposition1))) {
                                continue;
                            }
                        } else if (!EntityPositionTypes.checkSpawnRules((EntityTypes) optional.get(), worldserver, EnumMobSpawn.SPAWNER, blockposition1, worldserver.getRandom())) {
                            continue;
                        }

                        Entity entity = EntityTypes.loadEntityRecursive(nbttagcompound, worldserver, (entity1) -> {
                            entity1.moveTo(d0, d1, d2, entity1.getYRot(), entity1.getXRot());
                            return entity1;
                        });

                        if (entity == null) {
                            this.delay(worldserver, blockposition);
                            return;
                        }

                        int k = worldserver.getEntitiesOfClass(entity.getClass(), (new AxisAlignedBB((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 1), (double) (blockposition.getZ() + 1))).inflate((double) this.spawnRange)).size();

                        if (k >= this.maxNearbyEntities) {
                            this.delay(worldserver, blockposition);
                            return;
                        }

                        entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), randomsource.nextFloat() * 360.0F, 0.0F);
                        if (entity instanceof EntityInsentient) {
                            EntityInsentient entityinsentient = (EntityInsentient) entity;

                            if (mobspawnerdata.getCustomSpawnRules().isEmpty() && !entityinsentient.checkSpawnRules(worldserver, EnumMobSpawn.SPAWNER) || !entityinsentient.checkSpawnObstruction(worldserver)) {
                                continue;
                            }

                            if (mobspawnerdata.getEntityToSpawn().size() == 1 && mobspawnerdata.getEntityToSpawn().contains("id", 8)) {
                                ((EntityInsentient) entity).finalizeSpawn(worldserver, worldserver.getCurrentDifficultyAt(entity.blockPosition()), EnumMobSpawn.SPAWNER, (GroupDataEntity) null, (NBTTagCompound) null);
                            }
                        }

                        if (!worldserver.tryAddFreshEntityWithPassengers(entity)) {
                            this.delay(worldserver, blockposition);
                            return;
                        }

                        worldserver.levelEvent(2004, blockposition, 0);
                        worldserver.gameEvent(entity, GameEvent.ENTITY_PLACE, blockposition1);
                        if (entity instanceof EntityInsentient) {
                            ((EntityInsentient) entity).spawnAnim();
                        }

                        flag = true;
                    }
                }

                if (flag) {
                    this.delay(worldserver, blockposition);
                }

            }
        }
    }

    private void delay(World world, BlockPosition blockposition) {
        RandomSource randomsource = world.random;

        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            this.spawnDelay = this.minSpawnDelay + randomsource.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        }

        this.spawnPotentials.getRandom(randomsource).ifPresent((weightedentry_b) -> {
            this.setNextSpawnData(world, blockposition, (MobSpawnerData) weightedentry_b.getData());
        });
        this.broadcastEvent(world, blockposition, 1);
    }

    public void load(@Nullable World world, BlockPosition blockposition, NBTTagCompound nbttagcompound) {
        this.spawnDelay = nbttagcompound.getShort("Delay");
        boolean flag = nbttagcompound.contains("SpawnData", 10);

        if (flag) {
            MobSpawnerData mobspawnerdata = (MobSpawnerData) MobSpawnerData.CODEC.parse(DynamicOpsNBT.INSTANCE, nbttagcompound.getCompound("SpawnData")).resultOrPartial((s) -> {
                MobSpawnerAbstract.LOGGER.warn("Invalid SpawnData: {}", s);
            }).orElseGet(MobSpawnerData::new);

            this.setNextSpawnData(world, blockposition, mobspawnerdata);
        }

        boolean flag1 = nbttagcompound.contains("SpawnPotentials", 9);

        if (flag1) {
            NBTTagList nbttaglist = nbttagcompound.getList("SpawnPotentials", 10);

            this.spawnPotentials = (SimpleWeightedRandomList) MobSpawnerData.LIST_CODEC.parse(DynamicOpsNBT.INSTANCE, nbttaglist).resultOrPartial((s) -> {
                MobSpawnerAbstract.LOGGER.warn("Invalid SpawnPotentials list: {}", s);
            }).orElseGet(SimpleWeightedRandomList::empty);
        } else {
            this.spawnPotentials = SimpleWeightedRandomList.single(this.nextSpawnData != null ? this.nextSpawnData : new MobSpawnerData());
        }

        if (nbttagcompound.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = nbttagcompound.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbttagcompound.getShort("MaxSpawnDelay");
            this.spawnCount = nbttagcompound.getShort("SpawnCount");
        }

        if (nbttagcompound.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = nbttagcompound.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = nbttagcompound.getShort("RequiredPlayerRange");
        }

        if (nbttagcompound.contains("SpawnRange", 99)) {
            this.spawnRange = nbttagcompound.getShort("SpawnRange");
        }

        this.displayEntity = null;
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        nbttagcompound.putShort("Delay", (short) this.spawnDelay);
        nbttagcompound.putShort("MinSpawnDelay", (short) this.minSpawnDelay);
        nbttagcompound.putShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
        nbttagcompound.putShort("SpawnCount", (short) this.spawnCount);
        nbttagcompound.putShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
        nbttagcompound.putShort("RequiredPlayerRange", (short) this.requiredPlayerRange);
        nbttagcompound.putShort("SpawnRange", (short) this.spawnRange);
        if (this.nextSpawnData != null) {
            nbttagcompound.put("SpawnData", (NBTBase) MobSpawnerData.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.nextSpawnData).result().orElseThrow(() -> {
                return new IllegalStateException("Invalid SpawnData");
            }));
        }

        nbttagcompound.put("SpawnPotentials", (NBTBase) MobSpawnerData.LIST_CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.spawnPotentials).result().orElseThrow());
        return nbttagcompound;
    }

    @Nullable
    public Entity getOrCreateDisplayEntity(World world, RandomSource randomsource, BlockPosition blockposition) {
        if (this.displayEntity == null) {
            NBTTagCompound nbttagcompound = this.getOrCreateNextSpawnData(world, randomsource, blockposition).getEntityToSpawn();

            if (!nbttagcompound.contains("id", 8)) {
                return null;
            }

            this.displayEntity = EntityTypes.loadEntityRecursive(nbttagcompound, world, Function.identity());
            if (nbttagcompound.size() == 1 && this.displayEntity instanceof EntityInsentient) {
                ;
            }
        }

        return this.displayEntity;
    }

    public boolean onEventTriggered(World world, int i) {
        if (i == 1) {
            if (world.isClientSide) {
                this.spawnDelay = this.minSpawnDelay;
            }

            return true;
        } else {
            return false;
        }
    }

    protected void setNextSpawnData(@Nullable World world, BlockPosition blockposition, MobSpawnerData mobspawnerdata) {
        this.nextSpawnData = mobspawnerdata;
    }

    private MobSpawnerData getOrCreateNextSpawnData(@Nullable World world, RandomSource randomsource, BlockPosition blockposition) {
        if (this.nextSpawnData != null) {
            return this.nextSpawnData;
        } else {
            this.setNextSpawnData(world, blockposition, (MobSpawnerData) this.spawnPotentials.getRandom(randomsource).map(WeightedEntry.b::getData).orElseGet(MobSpawnerData::new));
            return this.nextSpawnData;
        }
    }

    public abstract void broadcastEvent(World world, BlockPosition blockposition, int i);

    public double getSpin() {
        return this.spin;
    }

    public double getoSpin() {
        return this.oSpin;
    }
}
