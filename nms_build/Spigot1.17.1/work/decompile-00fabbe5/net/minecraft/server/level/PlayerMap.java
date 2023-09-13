package net.minecraft.server.level;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.stream.Stream;

public final class PlayerMap {

    private final Object2BooleanMap<EntityPlayer> players = new Object2BooleanOpenHashMap();

    public PlayerMap() {}

    public Stream<EntityPlayer> a(long i) {
        return this.players.keySet().stream();
    }

    public void a(long i, EntityPlayer entityplayer, boolean flag) {
        this.players.put(entityplayer, flag);
    }

    public void a(long i, EntityPlayer entityplayer) {
        this.players.removeBoolean(entityplayer);
    }

    public void a(EntityPlayer entityplayer) {
        this.players.replace(entityplayer, true);
    }

    public void b(EntityPlayer entityplayer) {
        this.players.replace(entityplayer, false);
    }

    public boolean c(EntityPlayer entityplayer) {
        return this.players.getOrDefault(entityplayer, true);
    }

    public boolean d(EntityPlayer entityplayer) {
        return this.players.getBoolean(entityplayer);
    }

    public void a(long i, long j, EntityPlayer entityplayer) {}
}
