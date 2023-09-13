package net.minecraft.server.level;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Set;

public final class PlayerMap {

    private final Object2BooleanMap<EntityPlayer> players = new Object2BooleanOpenHashMap();

    public PlayerMap() {}

    public Set<EntityPlayer> getPlayers(long i) {
        return this.players.keySet();
    }

    public void addPlayer(long i, EntityPlayer entityplayer, boolean flag) {
        this.players.put(entityplayer, flag);
    }

    public void removePlayer(long i, EntityPlayer entityplayer) {
        this.players.removeBoolean(entityplayer);
    }

    public void ignorePlayer(EntityPlayer entityplayer) {
        this.players.replace(entityplayer, true);
    }

    public void unIgnorePlayer(EntityPlayer entityplayer) {
        this.players.replace(entityplayer, false);
    }

    public boolean ignoredOrUnknown(EntityPlayer entityplayer) {
        return this.players.getOrDefault(entityplayer, true);
    }

    public boolean ignored(EntityPlayer entityplayer) {
        return this.players.getBoolean(entityplayer);
    }

    public void updatePlayer(long i, long j, EntityPlayer entityplayer) {}
}
