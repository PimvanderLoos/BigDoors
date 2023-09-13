package net.minecraft.world.level;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.world.entity.EnumCreatureType;

public class LocalMobCapCalculator {

    private final Long2ObjectMap<List<EntityPlayer>> playersNearChunk = new Long2ObjectOpenHashMap();
    private final Map<EntityPlayer, LocalMobCapCalculator.a> playerMobCounts = Maps.newHashMap();
    private final PlayerChunkMap chunkMap;

    public LocalMobCapCalculator(PlayerChunkMap playerchunkmap) {
        this.chunkMap = playerchunkmap;
    }

    private List<EntityPlayer> getPlayersNear(ChunkCoordIntPair chunkcoordintpair) {
        return (List) this.playersNearChunk.computeIfAbsent(chunkcoordintpair.toLong(), (i) -> {
            return this.chunkMap.getPlayersCloseForSpawning(chunkcoordintpair);
        });
    }

    public void addMob(ChunkCoordIntPair chunkcoordintpair, EnumCreatureType enumcreaturetype) {
        Iterator iterator = this.getPlayersNear(chunkcoordintpair).iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            ((LocalMobCapCalculator.a) this.playerMobCounts.computeIfAbsent(entityplayer, (entityplayer1) -> {
                return new LocalMobCapCalculator.a();
            })).add(enumcreaturetype);
        }

    }

    public boolean canSpawn(EnumCreatureType enumcreaturetype, ChunkCoordIntPair chunkcoordintpair) {
        Iterator iterator = this.getPlayersNear(chunkcoordintpair).iterator();

        LocalMobCapCalculator.a localmobcapcalculator_a;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            localmobcapcalculator_a = (LocalMobCapCalculator.a) this.playerMobCounts.get(entityplayer);
        } while (localmobcapcalculator_a != null && !localmobcapcalculator_a.canSpawn(enumcreaturetype));

        return true;
    }

    private static class a {

        private final Object2IntMap<EnumCreatureType> counts = new Object2IntOpenHashMap(EnumCreatureType.values().length);

        a() {}

        public void add(EnumCreatureType enumcreaturetype) {
            this.counts.computeInt(enumcreaturetype, (enumcreaturetype1, integer) -> {
                return integer == null ? 1 : integer + 1;
            });
        }

        public boolean canSpawn(EnumCreatureType enumcreaturetype) {
            return this.counts.getOrDefault(enumcreaturetype, 0) < enumcreaturetype.getMaxInstancesPerChunk();
        }
    }
}
