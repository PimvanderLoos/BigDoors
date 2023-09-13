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
    public void onScoreChanged(ScoreboardScore scoreboardscore) {
        super.onScoreChanged(scoreboardscore);
        if (this.trackedObjectives.contains(scoreboardscore.getObjective())) {
            this.server.getPlayerList().broadcastAll(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, scoreboardscore.getObjective().getName(), scoreboardscore.getOwner(), scoreboardscore.getScore()));
        }

        this.setDirty();
    }

    @Override
    public void onPlayerRemoved(String s) {
        super.onPlayerRemoved(s);
        this.server.getPlayerList().broadcastAll(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.REMOVE, (String) null, s, 0));
        this.setDirty();
    }

    @Override
    public void onPlayerScoreRemoved(String s, ScoreboardObjective scoreboardobjective) {
        super.onPlayerScoreRemoved(s, scoreboardobjective);
        if (this.trackedObjectives.contains(scoreboardobjective)) {
            this.server.getPlayerList().broadcastAll(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.REMOVE, scoreboardobjective.getName(), s, 0));
        }

        this.setDirty();
    }

    @Override
    public void setDisplayObjective(int i, @Nullable ScoreboardObjective scoreboardobjective) {
        ScoreboardObjective scoreboardobjective1 = this.getDisplayObjective(i);

        super.setDisplayObjective(i, scoreboardobjective);
        if (scoreboardobjective1 != scoreboardobjective && scoreboardobjective1 != null) {
            if (this.getObjectiveDisplaySlotCount(scoreboardobjective1) > 0) {
                this.server.getPlayerList().broadcastAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            } else {
                this.stopTrackingObjective(scoreboardobjective1);
            }
        }

        if (scoreboardobjective != null) {
            if (this.trackedObjectives.contains(scoreboardobjective)) {
                this.server.getPlayerList().broadcastAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            } else {
                this.startTrackingObjective(scoreboardobjective);
            }
        }

        this.setDirty();
    }

    @Override
    public boolean addPlayerToTeam(String s, ScoreboardTeam scoreboardteam) {
        if (super.addPlayerToTeam(s, scoreboardteam)) {
            this.server.getPlayerList().broadcastAll(PacketPlayOutScoreboardTeam.createPlayerPacket(scoreboardteam, s, PacketPlayOutScoreboardTeam.a.ADD));
            this.setDirty();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removePlayerFromTeam(String s, ScoreboardTeam scoreboardteam) {
        super.removePlayerFromTeam(s, scoreboardteam);
        this.server.getPlayerList().broadcastAll(PacketPlayOutScoreboardTeam.createPlayerPacket(scoreboardteam, s, PacketPlayOutScoreboardTeam.a.REMOVE));
        this.setDirty();
    }

    @Override
    public void onObjectiveAdded(ScoreboardObjective scoreboardobjective) {
        super.onObjectiveAdded(scoreboardobjective);
        this.setDirty();
    }

    @Override
    public void onObjectiveChanged(ScoreboardObjective scoreboardobjective) {
        super.onObjectiveChanged(scoreboardobjective);
        if (this.trackedObjectives.contains(scoreboardobjective)) {
            this.server.getPlayerList().broadcastAll(new PacketPlayOutScoreboardObjective(scoreboardobjective, 2));
        }

        this.setDirty();
    }

    @Override
    public void onObjectiveRemoved(ScoreboardObjective scoreboardobjective) {
        super.onObjectiveRemoved(scoreboardobjective);
        if (this.trackedObjectives.contains(scoreboardobjective)) {
            this.stopTrackingObjective(scoreboardobjective);
        }

        this.setDirty();
    }

    @Override
    public void onTeamAdded(ScoreboardTeam scoreboardteam) {
        super.onTeamAdded(scoreboardteam);
        this.server.getPlayerList().broadcastAll(PacketPlayOutScoreboardTeam.createAddOrModifyPacket(scoreboardteam, true));
        this.setDirty();
    }

    @Override
    public void onTeamChanged(ScoreboardTeam scoreboardteam) {
        super.onTeamChanged(scoreboardteam);
        this.server.getPlayerList().broadcastAll(PacketPlayOutScoreboardTeam.createAddOrModifyPacket(scoreboardteam, false));
        this.setDirty();
    }

    @Override
    public void onTeamRemoved(ScoreboardTeam scoreboardteam) {
        super.onTeamRemoved(scoreboardteam);
        this.server.getPlayerList().broadcastAll(PacketPlayOutScoreboardTeam.createRemovePacket(scoreboardteam));
        this.setDirty();
    }

    public void addDirtyListener(Runnable runnable) {
        this.dirtyListeners.add(runnable);
    }

    protected void setDirty() {
        Iterator iterator = this.dirtyListeners.iterator();

        while (iterator.hasNext()) {
            Runnable runnable = (Runnable) iterator.next();

            runnable.run();
        }

    }

    public List<Packet<?>> getStartTrackingPackets(ScoreboardObjective scoreboardobjective) {
        List<Packet<?>> list = Lists.newArrayList();

        list.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 0));

        for (int i = 0; i < 19; ++i) {
            if (this.getDisplayObjective(i) == scoreboardobjective) {
                list.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            }
        }

        Iterator iterator = this.getPlayerScores(scoreboardobjective).iterator();

        while (iterator.hasNext()) {
            ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next();

            list.add(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, scoreboardscore.getObjective().getName(), scoreboardscore.getOwner(), scoreboardscore.getScore()));
        }

        return list;
    }

    public void startTrackingObjective(ScoreboardObjective scoreboardobjective) {
        List<Packet<?>> list = this.getStartTrackingPackets(scoreboardobjective);
        Iterator iterator = this.server.getPlayerList().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet<?> packet = (Packet) iterator1.next();

                entityplayer.connection.send(packet);
            }
        }

        this.trackedObjectives.add(scoreboardobjective);
    }

    public List<Packet<?>> getStopTrackingPackets(ScoreboardObjective scoreboardobjective) {
        List<Packet<?>> list = Lists.newArrayList();

        list.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 1));

        for (int i = 0; i < 19; ++i) {
            if (this.getDisplayObjective(i) == scoreboardobjective) {
                list.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            }
        }

        return list;
    }

    public void stopTrackingObjective(ScoreboardObjective scoreboardobjective) {
        List<Packet<?>> list = this.getStopTrackingPackets(scoreboardobjective);
        Iterator iterator = this.server.getPlayerList().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet<?> packet = (Packet) iterator1.next();

                entityplayer.connection.send(packet);
            }
        }

        this.trackedObjectives.remove(scoreboardobjective);
    }

    public int getObjectiveDisplaySlotCount(ScoreboardObjective scoreboardobjective) {
        int i = 0;

        for (int j = 0; j < 19; ++j) {
            if (this.getDisplayObjective(j) == scoreboardobjective) {
                ++i;
            }
        }

        return i;
    }

    public PersistentScoreboard createData() {
        PersistentScoreboard persistentscoreboard = new PersistentScoreboard(this);

        Objects.requireNonNull(persistentscoreboard);
        this.addDirtyListener(persistentscoreboard::setDirty);
        return persistentscoreboard;
    }

    public PersistentScoreboard createData(NBTTagCompound nbttagcompound) {
        return this.createData().load(nbttagcompound);
    }

    public static enum Action {

        CHANGE, REMOVE;

        private Action() {}
    }
}
