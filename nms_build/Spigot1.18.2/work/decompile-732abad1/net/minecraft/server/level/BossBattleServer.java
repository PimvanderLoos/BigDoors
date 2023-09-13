package net.minecraft.server.level;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutBoss;
import net.minecraft.util.MathHelper;
import net.minecraft.world.BossBattle;

public class BossBattleServer extends BossBattle {

    private final Set<EntityPlayer> players = Sets.newHashSet();
    private final Set<EntityPlayer> unmodifiablePlayers;
    public boolean visible;

    public BossBattleServer(IChatBaseComponent ichatbasecomponent, BossBattle.BarColor bossbattle_barcolor, BossBattle.BarStyle bossbattle_barstyle) {
        super(MathHelper.createInsecureUUID(), ichatbasecomponent, bossbattle_barcolor, bossbattle_barstyle);
        this.unmodifiablePlayers = Collections.unmodifiableSet(this.players);
        this.visible = true;
    }

    @Override
    public void setProgress(float f) {
        if (f != this.progress) {
            super.setProgress(f);
            this.broadcast(PacketPlayOutBoss::createUpdateProgressPacket);
        }

    }

    @Override
    public void setColor(BossBattle.BarColor bossbattle_barcolor) {
        if (bossbattle_barcolor != this.color) {
            super.setColor(bossbattle_barcolor);
            this.broadcast(PacketPlayOutBoss::createUpdateStylePacket);
        }

    }

    @Override
    public void setOverlay(BossBattle.BarStyle bossbattle_barstyle) {
        if (bossbattle_barstyle != this.overlay) {
            super.setOverlay(bossbattle_barstyle);
            this.broadcast(PacketPlayOutBoss::createUpdateStylePacket);
        }

    }

    @Override
    public BossBattle setDarkenScreen(boolean flag) {
        if (flag != this.darkenScreen) {
            super.setDarkenScreen(flag);
            this.broadcast(PacketPlayOutBoss::createUpdatePropertiesPacket);
        }

        return this;
    }

    @Override
    public BossBattle setPlayBossMusic(boolean flag) {
        if (flag != this.playBossMusic) {
            super.setPlayBossMusic(flag);
            this.broadcast(PacketPlayOutBoss::createUpdatePropertiesPacket);
        }

        return this;
    }

    @Override
    public BossBattle setCreateWorldFog(boolean flag) {
        if (flag != this.createWorldFog) {
            super.setCreateWorldFog(flag);
            this.broadcast(PacketPlayOutBoss::createUpdatePropertiesPacket);
        }

        return this;
    }

    @Override
    public void setName(IChatBaseComponent ichatbasecomponent) {
        if (!Objects.equal(ichatbasecomponent, this.name)) {
            super.setName(ichatbasecomponent);
            this.broadcast(PacketPlayOutBoss::createUpdateNamePacket);
        }

    }

    public void broadcast(Function<BossBattle, PacketPlayOutBoss> function) {
        if (this.visible) {
            PacketPlayOutBoss packetplayoutboss = (PacketPlayOutBoss) function.apply(this);
            Iterator iterator = this.players.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.connection.send(packetplayoutboss);
            }
        }

    }

    public void addPlayer(EntityPlayer entityplayer) {
        if (this.players.add(entityplayer) && this.visible) {
            entityplayer.connection.send(PacketPlayOutBoss.createAddPacket(this));
        }

    }

    public void removePlayer(EntityPlayer entityplayer) {
        if (this.players.remove(entityplayer) && this.visible) {
            entityplayer.connection.send(PacketPlayOutBoss.createRemovePacket(this.getId()));
        }

    }

    public void removeAllPlayers() {
        if (!this.players.isEmpty()) {
            Iterator iterator = Lists.newArrayList(this.players).iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                this.removePlayer(entityplayer);
            }
        }

    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean flag) {
        if (flag != this.visible) {
            this.visible = flag;
            Iterator iterator = this.players.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.connection.send(flag ? PacketPlayOutBoss.createAddPacket(this) : PacketPlayOutBoss.createRemovePacket(this.getId()));
            }
        }

    }

    public Collection<EntityPlayer> getPlayers() {
        return this.unmodifiablePlayers;
    }
}
