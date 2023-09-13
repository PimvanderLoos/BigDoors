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
        super(MathHelper.a(), ichatbasecomponent, bossbattle_barcolor, bossbattle_barstyle);
        this.unmodifiablePlayers = Collections.unmodifiableSet(this.players);
        this.visible = true;
    }

    @Override
    public void setProgress(float f) {
        if (f != this.progress) {
            super.setProgress(f);
            this.sendUpdate(PacketPlayOutBoss::createUpdateProgressPacket);
        }

    }

    @Override
    public void a(BossBattle.BarColor bossbattle_barcolor) {
        if (bossbattle_barcolor != this.color) {
            super.a(bossbattle_barcolor);
            this.sendUpdate(PacketPlayOutBoss::createUpdateStylePacket);
        }

    }

    @Override
    public void a(BossBattle.BarStyle bossbattle_barstyle) {
        if (bossbattle_barstyle != this.overlay) {
            super.a(bossbattle_barstyle);
            this.sendUpdate(PacketPlayOutBoss::createUpdateStylePacket);
        }

    }

    @Override
    public BossBattle setDarkenSky(boolean flag) {
        if (flag != this.darkenScreen) {
            super.setDarkenSky(flag);
            this.sendUpdate(PacketPlayOutBoss::createUpdatePropertiesPacket);
        }

        return this;
    }

    @Override
    public BossBattle setPlayMusic(boolean flag) {
        if (flag != this.playBossMusic) {
            super.setPlayMusic(flag);
            this.sendUpdate(PacketPlayOutBoss::createUpdatePropertiesPacket);
        }

        return this;
    }

    @Override
    public BossBattle setCreateFog(boolean flag) {
        if (flag != this.createWorldFog) {
            super.setCreateFog(flag);
            this.sendUpdate(PacketPlayOutBoss::createUpdatePropertiesPacket);
        }

        return this;
    }

    @Override
    public void a(IChatBaseComponent ichatbasecomponent) {
        if (!Objects.equal(ichatbasecomponent, this.name)) {
            super.a(ichatbasecomponent);
            this.sendUpdate(PacketPlayOutBoss::createUpdateNamePacket);
        }

    }

    public void sendUpdate(Function<BossBattle, PacketPlayOutBoss> function) {
        if (this.visible) {
            PacketPlayOutBoss packetplayoutboss = (PacketPlayOutBoss) function.apply(this);
            Iterator iterator = this.players.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.connection.sendPacket(packetplayoutboss);
            }
        }

    }

    public void addPlayer(EntityPlayer entityplayer) {
        if (this.players.add(entityplayer) && this.visible) {
            entityplayer.connection.sendPacket(PacketPlayOutBoss.createAddPacket(this));
        }

    }

    public void removePlayer(EntityPlayer entityplayer) {
        if (this.players.remove(entityplayer) && this.visible) {
            entityplayer.connection.sendPacket(PacketPlayOutBoss.createRemovePacket(this.i()));
        }

    }

    public void b() {
        if (!this.players.isEmpty()) {
            Iterator iterator = Lists.newArrayList(this.players).iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                this.removePlayer(entityplayer);
            }
        }

    }

    public boolean g() {
        return this.visible;
    }

    public void setVisible(boolean flag) {
        if (flag != this.visible) {
            this.visible = flag;
            Iterator iterator = this.players.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.connection.sendPacket(flag ? PacketPlayOutBoss.createAddPacket(this) : PacketPlayOutBoss.createRemovePacket(this.i()));
            }
        }

    }

    public Collection<EntityPlayer> getPlayers() {
        return this.unmodifiablePlayers;
    }
}
