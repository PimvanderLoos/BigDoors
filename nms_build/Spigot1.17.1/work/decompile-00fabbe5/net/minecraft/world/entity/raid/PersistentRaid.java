package net.minecraft.world.entity.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceRecord;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.saveddata.PersistentBase;
import net.minecraft.world.phys.Vec3D;

public class PersistentRaid extends PersistentBase {

    private static final String RAID_FILE_ID = "raids";
    public final Map<Integer, Raid> raidMap = Maps.newHashMap();
    private final WorldServer level;
    private int nextAvailableID;
    private int tick;

    public PersistentRaid(WorldServer worldserver) {
        this.level = worldserver;
        this.nextAvailableID = 1;
        this.b();
    }

    public Raid a(int i) {
        return (Raid) this.raidMap.get(i);
    }

    public void a() {
        ++this.tick;
        Iterator iterator = this.raidMap.values().iterator();

        while (iterator.hasNext()) {
            Raid raid = (Raid) iterator.next();

            if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                raid.stop();
            }

            if (raid.isStopped()) {
                iterator.remove();
                this.b();
            } else {
                raid.o();
            }
        }

        if (this.tick % 200 == 0) {
            this.b();
        }

        PacketDebug.a(this.level, this.raidMap.values());
    }

    public static boolean a(EntityRaider entityraider, Raid raid) {
        return entityraider != null && raid != null && raid.getWorld() != null ? entityraider.isAlive() && entityraider.isCanJoinRaid() && entityraider.dK() <= 2400 && entityraider.level.getDimensionManager() == raid.getWorld().getDimensionManager() : false;
    }

    @Nullable
    public Raid a(EntityPlayer entityplayer) {
        if (entityplayer.isSpectator()) {
            return null;
        } else if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            return null;
        } else {
            DimensionManager dimensionmanager = entityplayer.level.getDimensionManager();

            if (!dimensionmanager.hasRaids()) {
                return null;
            } else {
                BlockPosition blockposition = entityplayer.getChunkCoordinates();
                List<VillagePlaceRecord> list = (List) this.level.A().c(VillagePlaceType.ALL, blockposition, 64, VillagePlace.Occupancy.IS_OCCUPIED).collect(Collectors.toList());
                int i = 0;
                Vec3D vec3d = Vec3D.ZERO;

                for (Iterator iterator = list.iterator(); iterator.hasNext(); ++i) {
                    VillagePlaceRecord villageplacerecord = (VillagePlaceRecord) iterator.next();
                    BlockPosition blockposition1 = villageplacerecord.f();

                    vec3d = vec3d.add((double) blockposition1.getX(), (double) blockposition1.getY(), (double) blockposition1.getZ());
                }

                BlockPosition blockposition2;

                if (i > 0) {
                    vec3d = vec3d.a(1.0D / (double) i);
                    blockposition2 = new BlockPosition(vec3d);
                } else {
                    blockposition2 = blockposition;
                }

                Raid raid = this.a(entityplayer.getWorldServer(), blockposition2);
                boolean flag = false;

                if (!raid.isStarted()) {
                    if (!this.raidMap.containsKey(raid.getId())) {
                        this.raidMap.put(raid.getId(), raid);
                    }

                    flag = true;
                } else if (raid.getBadOmenLevel() < raid.getMaxBadOmenLevel()) {
                    flag = true;
                } else {
                    entityplayer.removeEffect(MobEffects.BAD_OMEN);
                    entityplayer.connection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, (byte) 43));
                }

                if (flag) {
                    raid.a((EntityHuman) entityplayer);
                    entityplayer.connection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, (byte) 43));
                    if (!raid.c()) {
                        entityplayer.a(StatisticList.RAID_TRIGGER);
                        CriterionTriggers.BAD_OMEN.a(entityplayer);
                    }
                }

                this.b();
                return raid;
            }
        }
    }

    private Raid a(WorldServer worldserver, BlockPosition blockposition) {
        Raid raid = worldserver.c(blockposition);

        return raid != null ? raid : new Raid(this.d(), worldserver, blockposition);
    }

    public static PersistentRaid a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
        PersistentRaid persistentraid = new PersistentRaid(worldserver);

        persistentraid.nextAvailableID = nbttagcompound.getInt("NextAvailableID");
        persistentraid.tick = nbttagcompound.getInt("Tick");
        NBTTagList nbttaglist = nbttagcompound.getList("Raids", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
            Raid raid = new Raid(worldserver, nbttagcompound1);

            persistentraid.raidMap.put(raid.getId(), raid);
        }

        return persistentraid;
    }

    @Override
    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("NextAvailableID", this.nextAvailableID);
        nbttagcompound.setInt("Tick", this.tick);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.raidMap.values().iterator();

        while (iterator.hasNext()) {
            Raid raid = (Raid) iterator.next();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            raid.a(nbttagcompound1);
            nbttaglist.add(nbttagcompound1);
        }

        nbttagcompound.set("Raids", nbttaglist);
        return nbttagcompound;
    }

    public static String a(DimensionManager dimensionmanager) {
        return "raids" + dimensionmanager.getSuffix();
    }

    private int d() {
        return ++this.nextAvailableID;
    }

    @Nullable
    public Raid getNearbyRaid(BlockPosition blockposition, int i) {
        Raid raid = null;
        double d0 = (double) i;
        Iterator iterator = this.raidMap.values().iterator();

        while (iterator.hasNext()) {
            Raid raid1 = (Raid) iterator.next();
            double d1 = raid1.getCenter().j(blockposition);

            if (raid1.v() && d1 < d0) {
                raid = raid1;
                d0 = d1;
            }
        }

        return raid;
    }
}
