package net.minecraft.world.entity.npc;

import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityPositionTypes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.MobSpawner;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.phys.AxisAlignedBB;

public class MobSpawnerCat implements MobSpawner {

    private static final int TICK_DELAY = 1200;
    private int nextTick;

    public MobSpawnerCat() {}

    @Override
    public int tick(WorldServer worldserver, boolean flag, boolean flag1) {
        if (flag1 && worldserver.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            --this.nextTick;
            if (this.nextTick > 0) {
                return 0;
            } else {
                this.nextTick = 1200;
                EntityPlayer entityplayer = worldserver.getRandomPlayer();

                if (entityplayer == null) {
                    return 0;
                } else {
                    RandomSource randomsource = worldserver.random;
                    int i = (8 + randomsource.nextInt(24)) * (randomsource.nextBoolean() ? -1 : 1);
                    int j = (8 + randomsource.nextInt(24)) * (randomsource.nextBoolean() ? -1 : 1);
                    BlockPosition blockposition = entityplayer.blockPosition().offset(i, 0, j);
                    boolean flag2 = true;

                    if (!worldserver.hasChunksAt(blockposition.getX() - 10, blockposition.getZ() - 10, blockposition.getX() + 10, blockposition.getZ() + 10)) {
                        return 0;
                    } else {
                        if (SpawnerCreature.isSpawnPositionOk(EntityPositionTypes.Surface.ON_GROUND, worldserver, blockposition, EntityTypes.CAT)) {
                            if (worldserver.isCloseToVillage(blockposition, 2)) {
                                return this.spawnInVillage(worldserver, blockposition);
                            }

                            if (worldserver.structureManager().getStructureWithPieceAt(blockposition, StructureTags.CATS_SPAWN_IN).isValid()) {
                                return this.spawnInHut(worldserver, blockposition);
                            }
                        }

                        return 0;
                    }
                }
            }
        } else {
            return 0;
        }
    }

    private int spawnInVillage(WorldServer worldserver, BlockPosition blockposition) {
        boolean flag = true;

        if (worldserver.getPoiManager().getCountInRange((holder) -> {
            return holder.is(PoiTypes.HOME);
        }, blockposition, 48, VillagePlace.Occupancy.IS_OCCUPIED) > 4L) {
            List<EntityCat> list = worldserver.getEntitiesOfClass(EntityCat.class, (new AxisAlignedBB(blockposition)).inflate(48.0D, 8.0D, 48.0D));

            if (list.size() < 5) {
                return this.spawnCat(blockposition, worldserver);
            }
        }

        return 0;
    }

    private int spawnInHut(WorldServer worldserver, BlockPosition blockposition) {
        boolean flag = true;
        List<EntityCat> list = worldserver.getEntitiesOfClass(EntityCat.class, (new AxisAlignedBB(blockposition)).inflate(16.0D, 8.0D, 16.0D));

        return list.size() < 1 ? this.spawnCat(blockposition, worldserver) : 0;
    }

    private int spawnCat(BlockPosition blockposition, WorldServer worldserver) {
        EntityCat entitycat = (EntityCat) EntityTypes.CAT.create(worldserver);

        if (entitycat == null) {
            return 0;
        } else {
            entitycat.finalizeSpawn(worldserver, worldserver.getCurrentDifficultyAt(blockposition), EnumMobSpawn.NATURAL, (GroupDataEntity) null, (NBTTagCompound) null);
            entitycat.moveTo(blockposition, 0.0F, 0.0F);
            worldserver.addFreshEntityWithPassengers(entitycat);
            return 1;
        }
    }
}
