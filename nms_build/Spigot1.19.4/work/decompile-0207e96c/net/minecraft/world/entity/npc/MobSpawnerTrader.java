package net.minecraft.world.entity.npc;

import java.util.Iterator;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityPositionTypes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.animal.horse.EntityLlamaTrader;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.MobSpawner;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.storage.IWorldDataServer;

public class MobSpawnerTrader implements MobSpawner {

    private static final int DEFAULT_TICK_DELAY = 1200;
    public static final int DEFAULT_SPAWN_DELAY = 24000;
    private static final int MIN_SPAWN_CHANCE = 25;
    private static final int MAX_SPAWN_CHANCE = 75;
    private static final int SPAWN_CHANCE_INCREASE = 25;
    private static final int SPAWN_ONE_IN_X_CHANCE = 10;
    private static final int NUMBER_OF_SPAWN_ATTEMPTS = 10;
    private final RandomSource random = RandomSource.create();
    private final IWorldDataServer serverLevelData;
    private int tickDelay;
    private int spawnDelay;
    private int spawnChance;

    public MobSpawnerTrader(IWorldDataServer iworlddataserver) {
        this.serverLevelData = iworlddataserver;
        this.tickDelay = 1200;
        this.spawnDelay = iworlddataserver.getWanderingTraderSpawnDelay();
        this.spawnChance = iworlddataserver.getWanderingTraderSpawnChance();
        if (this.spawnDelay == 0 && this.spawnChance == 0) {
            this.spawnDelay = 24000;
            iworlddataserver.setWanderingTraderSpawnDelay(this.spawnDelay);
            this.spawnChance = 25;
            iworlddataserver.setWanderingTraderSpawnChance(this.spawnChance);
        }

    }

    @Override
    public int tick(WorldServer worldserver, boolean flag, boolean flag1) {
        if (!worldserver.getGameRules().getBoolean(GameRules.RULE_DO_TRADER_SPAWNING)) {
            return 0;
        } else if (--this.tickDelay > 0) {
            return 0;
        } else {
            this.tickDelay = 1200;
            this.spawnDelay -= 1200;
            this.serverLevelData.setWanderingTraderSpawnDelay(this.spawnDelay);
            if (this.spawnDelay > 0) {
                return 0;
            } else {
                this.spawnDelay = 24000;
                if (!worldserver.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                    return 0;
                } else {
                    int i = this.spawnChance;

                    this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 75);
                    this.serverLevelData.setWanderingTraderSpawnChance(this.spawnChance);
                    if (this.random.nextInt(100) > i) {
                        return 0;
                    } else if (this.spawn(worldserver)) {
                        this.spawnChance = 25;
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }

    private boolean spawn(WorldServer worldserver) {
        EntityPlayer entityplayer = worldserver.getRandomPlayer();

        if (entityplayer == null) {
            return true;
        } else if (this.random.nextInt(10) != 0) {
            return false;
        } else {
            BlockPosition blockposition = entityplayer.blockPosition();
            boolean flag = true;
            VillagePlace villageplace = worldserver.getPoiManager();
            Optional<BlockPosition> optional = villageplace.find((holder) -> {
                return holder.is(PoiTypes.MEETING);
            }, (blockposition1) -> {
                return true;
            }, blockposition, 48, VillagePlace.Occupancy.ANY);
            BlockPosition blockposition1 = (BlockPosition) optional.orElse(blockposition);
            BlockPosition blockposition2 = this.findSpawnPositionNear(worldserver, blockposition1, 48);

            if (blockposition2 != null && this.hasEnoughSpace(worldserver, blockposition2)) {
                if (worldserver.getBiome(blockposition2).is(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
                    return false;
                }

                EntityVillagerTrader entityvillagertrader = (EntityVillagerTrader) EntityTypes.WANDERING_TRADER.spawn(worldserver, blockposition2, EnumMobSpawn.EVENT);

                if (entityvillagertrader != null) {
                    for (int i = 0; i < 2; ++i) {
                        this.tryToSpawnLlamaFor(worldserver, entityvillagertrader, 4);
                    }

                    this.serverLevelData.setWanderingTraderId(entityvillagertrader.getUUID());
                    entityvillagertrader.setDespawnDelay(48000);
                    entityvillagertrader.setWanderTarget(blockposition1);
                    entityvillagertrader.restrictTo(blockposition1, 16);
                    return true;
                }
            }

            return false;
        }
    }

    private void tryToSpawnLlamaFor(WorldServer worldserver, EntityVillagerTrader entityvillagertrader, int i) {
        BlockPosition blockposition = this.findSpawnPositionNear(worldserver, entityvillagertrader.blockPosition(), i);

        if (blockposition != null) {
            EntityLlamaTrader entityllamatrader = (EntityLlamaTrader) EntityTypes.TRADER_LLAMA.spawn(worldserver, blockposition, EnumMobSpawn.EVENT);

            if (entityllamatrader != null) {
                entityllamatrader.setLeashedTo(entityvillagertrader, true);
            }
        }
    }

    @Nullable
    private BlockPosition findSpawnPositionNear(IWorldReader iworldreader, BlockPosition blockposition, int i) {
        BlockPosition blockposition1 = null;

        for (int j = 0; j < 10; ++j) {
            int k = blockposition.getX() + this.random.nextInt(i * 2) - i;
            int l = blockposition.getZ() + this.random.nextInt(i * 2) - i;
            int i1 = iworldreader.getHeight(HeightMap.Type.WORLD_SURFACE, k, l);
            BlockPosition blockposition2 = new BlockPosition(k, i1, l);

            if (SpawnerCreature.isSpawnPositionOk(EntityPositionTypes.Surface.ON_GROUND, iworldreader, blockposition2, EntityTypes.WANDERING_TRADER)) {
                blockposition1 = blockposition2;
                break;
            }
        }

        return blockposition1;
    }

    private boolean hasEnoughSpace(IBlockAccess iblockaccess, BlockPosition blockposition) {
        Iterator iterator = BlockPosition.betweenClosed(blockposition, blockposition.offset(1, 2, 1)).iterator();

        BlockPosition blockposition1;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            blockposition1 = (BlockPosition) iterator.next();
        } while (iblockaccess.getBlockState(blockposition1).getCollisionShape(iblockaccess, blockposition1).isEmpty());

        return false;
    }
}
