package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.scores.PersistentScoreboard;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardScore;
import net.minecraft.world.scores.ScoreboardTeam;

public class ScoreboardServer extends Scoreboard {

    private final MinecraftServer server;
    private final Set<ScoreboardObjective> trackedObjectives = Sets.newHashSet();
    private final List<Runnable> dirtyListeners = Lists.newArrayList();

    public ScoreboardServer(MinecraftServer minecraftserver) {
        this.server = minecraftserver;
    }

    @Override
    public void handleScoreChanged(ScoreboardScore scoreboardscore) {
        super.handleScoreChanged(scoreboardscore);
        if (this.trackedObjectives.contains(scoreboardscore.getObjective())) {
            this.server.getPlayerList().sendAll(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, scoreboardscore.getObjective().getName(), scoreboardscore.getPlayerName(), scoreboardscore.getScore()));
        }

        this.a();
    }

    @Override
    public void handlePlayerRemoved(String s) {
        super.handlePlayerRemoved(s);
        this.server.getPlayerList().sendAll(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.REMOVE, (String) null, s, 0));
        this.a();
    }

    @Override
    public void a(String s, ScoreboardObjective scoreboardobjective) {
        super.a(s, scoreboardobjective);
        if (this.trackedObjectives.contains(scoreboardobjective)) {
            this.server.getPlayerList().sendAll(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.REMOVE, scoreboardobjective.getName(), s, 0));
        }

        this.a();
    }

    @Override
    public void setDisplaySlot(int i, @Nullable ScoreboardObjective scoreboardobjective) {
        ScoreboardObjective scoreboardobjective1 = this.getObjectiveForSlot(i);

        super.setDisplaySlot(i, scoreboardobjective);
        if (scoreboardobjective1 != scoreboardobjective && scoreboardobjective1 != null) {
            if (this.h(scoreboardobjective1) > 0) {
                this.server.getPlayerList().sendAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            } else {
                this.g(scoreboardobjective1);
            }
        }

        if (scoreboardobjective != null) {
            if (this.trackedObjectives.contains(scoreboardobjective)) {
                this.server.getPlayerList().sendAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            } else {
                this.e(scoreboardobjective);
            }
        }

        this.a();
    }

    @Override
    public boolean addPlayerToTeam(String s, ScoreboardTeam scoreboardteam) {
        if (super.addPlayerToTeam(s, scoreboardteam)) {
            this.server.getPlayerList().sendAll(PacketPlayOutScoreboardTeam.a(scoreboardteam, s, PacketPlayOutScoreboardTeam.a.ADD));
            this.a();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removePlayerFromTeam(String s, ScoreboardTeam scoreboardteam) {
        super.removePlayerFromTeam(s, scoreboardteam);
        this.server.getPlayerList().sendAll(PacketPlayOutScoreboardTeam.a(scoreboardteam, s, PacketPlayOutScoreboardTeam.a.REMOVE));
        this.a();
    }

    @Override
    public void handleObjectiveAdded(ScoreboardObjective scoreboardobjective) {
        super.handleObjectiveAdded(scoreboardobjective);
        this.a();
    }

    @Override
    public void handleObjectiveChanged(ScoreboardObjective scoreboardobjective) {
        super.handleObjectiveChanged(scoreboardobjective);
        if (this.trackedObjectives.contains(scoreboardobjective)) {
            this.server.getPlayerList().sendAll(new PacketPlayOutScoreboardObjective(scoreboardobjective, 2));
        }

        this.a();
    }

    @Override
    public void handleObjectiveRemoved(ScoreboardObjective scoreboardobjective) {
        super.handleObjectiveRemoved(scoreboardobjective);
        if (this.trackedObjectives.contains(scoreboardobjective)) {
            this.g(scoreboardobjective);
        }

        this.a();
    }

    @Override
    public void handleTeamAdded(ScoreboardTeam scoreboardteam) {
        super.handleTeamAdded(scoreboardteam);
        this.server.getPlayerList().sendAll(PacketPlayOutScoreboardTeam.a(scoreboardteam, true));
        this.a();
    }

    @Override
    public void handleTeamChanged(ScoreboardTeam scoreboardteam) {
        super.handleTeamChanged(scoreboardteam);
        this.server.getPlayerList().sendAll(PacketPlayOutScoreboardTeam.a(scoreboardteam, false));
        this.a();
    }

    @Override
    public void handleTeamRemoved(ScoreboardTeam scoreboardteam) {
        super.handleTeamRemoved(scoreboardteam);
        this.server.getPlayerList().sendAll(PacketPlayOutScoreboardTeam.a(scoreboardteam));
        this.a();
    }

    public void a(Runnable runnable) {
        this.dirtyListeners.add(runnable);
    }

    protected void a() {
        Iterator iterator = this.dirtyListeners.iterator();

        while (iterator.hasNext()) {
            Runnable runnable = (Runnable) iterator.next();

            runnable.run();
        }

    }

    public List<Packet<?>> getScoreboardScorePacketsForObjective(ScoreboardObjective scoreboardobjective) {
        List<Packet<?>> list = Lists.newArrayList();

        list.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 0));

        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) == scoreboardobjective) {
                list.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            }
        }

        Iterator iterator = this.getScoresForObjective(scoreboardobjective).iterator();

        while (iterator.hasNext()) {
            ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next();

            list.add(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, scoreboardscore.getObjective().getName(), scoreboardscore.getPlayerName(), scoreboardscore.getScore()));
        }

        return list;
    }

    public void e(ScoreboardObjective scoreboardobjective) {
        List<Packet<?>> list = this.getScoreboardScorePacketsForObjective(scoreboardobjective);
        Iterator iterator = this.server.getPlayerList().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet<?> packet = (Packet) iterator1.next();

                entityplayer.connection.sendPacket(packet);
            }
        }

        this.trackedObjectives.add(scoreboardobjective);
    }

    public List<Packet<?>> f(ScoreboardObjective scoreboardobjective) {
        List<Packet<?>> list = Lists.newArrayList();

        list.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 1));

        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) == scoreboardobjective) {
                list.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            }
        }

        return list;
    }

    public void g(ScoreboardObjective scoreboardobjective) {
        List<Packet<?>> list = this.f(scoreboardobjective);
        Iterator iterator = this.server.getPlayerList().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet<?> packet = (Packet) iterator1.next();

                entityplayer.connection.sendPacket(packet);
            }
        }

        this.trackedObjectives.remove(scoreboardobjective);
    }

    public int h(ScoreboardObjective scoreboardobjective) {
        int i = 0;

        for (int j = 0; j < 19; ++j) {
            if (this.getObjectiveForSlot(j) == scoreboardobjective) {
                ++i;
            }
        }

        return i;
    }

    public PersistentScoreboard b() {
        PersistentScoreboard persistentscoreboard = new PersistentScoreboard(this);

        Objects.requireNonNull(persistentscoreboard);
        this.a(persistentscoreboard::b);
        return persistentscoreboard;
    }

    public PersistentScoreboard a(NBTTagCompound nbttagcompound) {
        return this.b().b(nbttagcompound);
    }

    public static enum Action {

        CHANGE, REMOVE;

        private Action() {}
    }
}
