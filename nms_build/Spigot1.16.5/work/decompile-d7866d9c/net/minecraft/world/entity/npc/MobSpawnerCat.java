package net.minecraft.world.entity.npc;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityPositionTypes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.MobSpawner;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.phys.AxisAlignedBB;

public class MobSpawnerCat implements MobSpawner {

    private int a;

    public MobSpawnerCat() {}

    @Override
    public int a(WorldServer worldserver, boolean flag, boolean flag1) {
        if (flag1 && worldserver.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            --this.a;
            if (this.a > 0) {
                return 0;
            } else {
                this.a = 1200;
                EntityPlayer entityplayer = worldserver.q_();

                if (entityplayer == null) {
                    return 0;
                } else {
                    Random random = worldserver.random;
                    int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                    int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                    BlockPosition blockposition = entityplayer.getChunkCoordinates().b(i, 0, j);

                    if (!worldserver.isAreaLoaded(blockposition.getX() - 10, blockposition.getY() - 10, blockposition.getZ() - 10, blockposition.getX() + 10, blockposition.getY() + 10, blockposition.getZ() + 10)) {
                        return 0;
                    } else {
                        if (SpawnerCreature.a(EntityPositionTypes.Surface.ON_GROUND, (IWorldReader) worldserver, blockposition, EntityTypes.CAT)) {
                            if (worldserver.a(blockposition, 2)) {
                                return this.a(worldserver, blockposition);
                            }

                            if (worldserver.getStructureManager().a(blockposition, true, StructureGenerator.SWAMP_HUT).e()) {
                                return this.b(worldserver, blockposition);
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

    private int a(WorldServer worldserver, BlockPosition blockposition) {
        boolean flag = true;

        if (worldserver.y().a(VillagePlaceType.r.c(), blockposition, 48, VillagePlace.Occupancy.IS_OCCUPIED) > 4L) {
            List<EntityCat> list = worldserver.a(EntityCat.class, (new AxisAlignedBB(blockposition)).grow(48.0D, 8.0D, 48.0D));

            if (list.size() < 5) {
                return this.a(blockposition, worldserver);
            }
        }

        return 0;
    }

    private int b(WorldServer worldserver, BlockPosition blockposition) {
        boolean flag = true;
        List<EntityCat> list = worldserver.a(EntityCat.class, (new AxisAlignedBB(blockposition)).grow(16.0D, 8.0D, 16.0D));

        return list.size() < 1 ? this.a(blockposition, worldserver) : 0;
    }

    private int a(BlockPosition blockposition, WorldServer worldserver) {
        EntityCat entitycat = (EntityCat) EntityTypes.CAT.a((World) worldserver);

        if (entitycat == null) {
            return 0;
        } else {
            entitycat.prepare(worldserver, worldserver.getDamageScaler(blockposition), EnumMobSpawn.NATURAL, (GroupDataEntity) null, (NBTTagCompound) null);
            entitycat.setPositionRotation(blockposition, 0.0F, 0.0F);
            worldserver.addAllEntities(entitycat);
            return 1;
        }
    }
}
