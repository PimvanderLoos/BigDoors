package net.minecraft.world.level.levelgen;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.monster.EntityMonsterPatrolling;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.MobSpawner;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;

public class MobSpawnerPatrol implements MobSpawner {

    private int nextTick;

    public MobSpawnerPatrol() {}

    @Override
    public int tick(WorldServer worldserver, boolean flag, boolean flag1) {
        if (!flag) {
            return 0;
        } else if (!worldserver.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
            return 0;
        } else {
            Random random = worldserver.random;

            --this.nextTick;
            if (this.nextTick > 0) {
                return 0;
            } else {
                this.nextTick += 12000 + random.nextInt(1200);
                long i = worldserver.getDayTime() / 24000L;

                if (i >= 5L && worldserver.isDay()) {
                    if (random.nextInt(5) != 0) {
                        return 0;
                    } else {
                        int j = worldserver.players().size();

                        if (j < 1) {
                            return 0;
                        } else {
                            EntityHuman entityhuman = (EntityHuman) worldserver.players().get(random.nextInt(j));

                            if (entityhuman.isSpectator()) {
                                return 0;
                            } else if (worldserver.isCloseToVillage(entityhuman.blockPosition(), 2)) {
                                return 0;
                            } else {
                                int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                                int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = entityhuman.blockPosition().mutable().move(k, 0, l);
                                boolean flag2 = true;

                                if (!worldserver.hasChunksAt(blockposition_mutableblockposition.getX() - 10, blockposition_mutableblockposition.getZ() - 10, blockposition_mutableblockposition.getX() + 10, blockposition_mutableblockposition.getZ() + 10)) {
                                    return 0;
                                } else {
                                    BiomeBase biomebase = worldserver.getBiome(blockposition_mutableblockposition);
                                    BiomeBase.Geography biomebase_geography = biomebase.getBiomeCategory();

                                    if (biomebase_geography == BiomeBase.Geography.MUSHROOM) {
                                        return 0;
                                    } else {
                                        int i1 = 0;
                                        int j1 = (int) Math.ceil((double) worldserver.getCurrentDifficultyAt(blockposition_mutableblockposition).getEffectiveDifficulty()) + 1;

                                        for (int k1 = 0; k1 < j1; ++k1) {
                                            ++i1;
                                            blockposition_mutableblockposition.setY(worldserver.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition_mutableblockposition).getY());
                                            if (k1 == 0) {
                                                if (!this.spawnPatrolMember(worldserver, blockposition_mutableblockposition, random, true)) {
                                                    break;
                                                }
                                            } else {
                                                this.spawnPatrolMember(worldserver, blockposition_mutableblockposition, random, false);
                                            }

                                            blockposition_mutableblockposition.setX(blockposition_mutableblockposition.getX() + random.nextInt(5) - random.nextInt(5));
                                            blockposition_mutableblockposition.setZ(blockposition_mutableblockposition.getZ() + random.nextInt(5) - random.nextInt(5));
                                        }

                                        return i1;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean spawnPatrolMember(WorldServer worldserver, BlockPosition blockposition, Random random, boolean flag) {
        IBlockData iblockdata = worldserver.getBlockState(blockposition);

        if (!SpawnerCreature.isValidEmptySpawnBlock(worldserver, blockposition, iblockdata, iblockdata.getFluidState(), EntityTypes.PILLAGER)) {
            return false;
        } else if (!EntityMonsterPatrolling.checkPatrollingMonsterSpawnRules(EntityTypes.PILLAGER, worldserver, EnumMobSpawn.PATROL, blockposition, random)) {
            return false;
        } else {
            EntityMonsterPatrolling entitymonsterpatrolling = (EntityMonsterPatrolling) EntityTypes.PILLAGER.create(worldserver);

            if (entitymonsterpatrolling != null) {
                if (flag) {
                    entitymonsterpatrolling.setPatrolLeader(true);
                    entitymonsterpatrolling.findPatrolTarget();
                }

                entitymonsterpatrolling.setPos((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
                entitymonsterpatrolling.finalizeSpawn(worldserver, worldserver.getCurrentDifficultyAt(blockposition), EnumMobSpawn.PATROL, (GroupDataEntity) null, (NBTTagCompound) null);
                worldserver.addFreshEntityWithPassengers(entitymonsterpatrolling);
                return true;
            } else {
                return false;
            }
        }
    }
}
