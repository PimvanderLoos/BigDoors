package net.minecraft.server.players;

import java.util.Iterator;
import java.util.List;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.player.EntityHuman;

public class SleepStatus {

    private int activePlayers;
    private int sleepingPlayers;

    public SleepStatus() {}

    public boolean a(int i) {
        return this.sleepingPlayers >= this.b(i);
    }

    public boolean a(int i, List<EntityPlayer> list) {
        int j = (int) list.stream().filter(EntityHuman::isDeeplySleeping).count();

        return j >= this.b(i);
    }

    public int b(int i) {
        return Math.max(1, MathHelper.f((float) (this.activePlayers * i) / 100.0F));
    }

    public void a() {
        this.sleepingPlayers = 0;
    }

    public int b() {
        return this.sleepingPlayers;
    }

    public boolean a(List<EntityPlayer> list) {
        int i = this.activePlayers;
        int j = this.sleepingPlayers;

        this.activePlayers = 0;
        this.sleepingPlayers = 0;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (!entityplayer.isSpectator()) {
                ++this.activePlayers;
                if (entityplayer.isSleeping()) {
                    ++this.sleepingPlayers;
                }
            }
        }

        return (j > 0 || this.sleepingPlayers > 0) && (i != this.activePlayers || j != this.sleepingPlayers);
    }
}
