package net.minecraft.world.entity.ai.village;

import com.mojang.logging.LogUtils;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.MobSpawner;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.Vec3D;
import org.slf4j.Logger;

public class VillageSiege implements MobSpawner {

    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean hasSetupSiege;
    private VillageSiege.State siegeState;
    private int zombiesToSpawn;
    private int nextSpawnTime;
    private int spawnX;
    private int spawnY;
    private int spawnZ;

    public VillageSiege() {
        this.siegeState = VillageSiege.State.SIEGE_DONE;
    }

    @Override
    public int tick(WorldServer worldserver, boolean flag, boolean flag1) {
        if (!worldserver.isDay() && flag) {
            float f = worldserver.getTimeOfDay(0.0F);

            if ((double) f == 0.5D) {
                this.siegeState = worldserver.random.nextInt(10) == 0 ? VillageSiege.State.SIEGE_TONIGHT : VillageSiege.State.SIEGE_DONE;
            }

            if (this.siegeState == VillageSiege.State.SIEGE_DONE) {
                return 0;
            } else {
                if (!this.hasSetupSiege) {
                    if (!this.tryToSetupSiege(worldserver)) {
                        return 0;
                    }

                    this.hasSetupSiege = true;
                }

                if (this.nextSpawnTime > 0) {
                    --this.nextSpawnTime;
                    return 0;
                } else {
                    this.nextSpawnTime = 2;
                    if (this.zombiesToSpawn > 0) {
                        this.trySpawn(worldserver);
                        --this.zombiesToSpawn;
                    } else {
                        this.siegeState = VillageSiege.State.SIEGE_DONE;
                    }

                    return 1;
                }
            }
        } else {
            this.siegeState = VillageSiege.State.SIEGE_DONE;
            this.hasSetupSiege = false;
            return 0;
        }
    }

    private boolean tryToSetupSiege(WorldServer worldserver) {
        Iterator iterator = worldserver.players().iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();

            if (!entityhuman.isSpectator()) {
                BlockPosition blockposition = entityhuman.blockPosition();

                if (worldserver.isVillage(blockposition) && !worldserver.getBiome(blockposition).is(BiomeTags.WITHOUT_ZOMBIE_SIEGES)) {
                    for (int i = 0; i < 10; ++i) {
                        float f = worldserver.random.nextFloat() * 6.2831855F;

                        this.spawnX = blockposition.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0F);
                        this.spawnY = blockposition.getY();
                        this.spawnZ = blockposition.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0F);
                        if (this.findRandomSpawnPos(worldserver, new BlockPosition(this.spawnX, this.spawnY, this.spawnZ)) != null) {
                            this.nextSpawnTime = 0;
                            this.zombiesToSpawn = 20;
                            break;
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private void trySpawn(WorldServer worldserver) {
        Vec3D vec3d = this.findRandomSpawnPos(worldserver, new BlockPosition(this.spawnX, this.spawnY, this.spawnZ));

        if (vec3d != null) {
            EntityZombie entityzombie;

            try {
                entityzombie = new EntityZombie(worldserver);
                entityzombie.finalizeSpawn(worldserver, worldserver.getCurrentDifficultyAt(entityzombie.blockPosition()), EnumMobSpawn.EVENT, (GroupDataEntity) null, (NBTTagCompound) null);
            } catch (Exception exception) {
                VillageSiege.LOGGER.warn("Failed to create zombie for village siege at {}", vec3d, exception);
                return;
            }

            entityzombie.moveTo(vec3d.x, vec3d.y, vec3d.z, worldserver.random.nextFloat() * 360.0F, 0.0F);
            worldserver.addFreshEntityWithPassengers(entityzombie);
        }
    }

    @Nullable
    private Vec3D findRandomSpawnPos(WorldServer worldserver, BlockPosition blockposition) {
        for (int i = 0; i < 10; ++i) {
            int j = blockposition.getX() + worldserver.random.nextInt(16) - 8;
            int k = blockposition.getZ() + worldserver.random.nextInt(16) - 8;
            int l = worldserver.getHeight(HeightMap.Type.WORLD_SURFACE, j, k);
            BlockPosition blockposition1 = new BlockPosition(j, l, k);

            if (worldserver.isVillage(blockposition1) && EntityMonster.checkMonsterSpawnRules(EntityTypes.ZOMBIE, worldserver, EnumMobSpawn.EVENT, blockposition1, worldserver.random)) {
                return Vec3D.atBottomCenterOf(blockposition1);
            }
        }

        return null;
    }

    private static enum State {

        SIEGE_CAN_ACTIVATE, SIEGE_TONIGHT, SIEGE_DONE;

        private State() {}
    }
}
