package net.minecraft.world.scores;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;
import org.slf4j.Logger;

public class Scoreboard {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int DISPLAY_SLOT_LIST = 0;
    public static final int DISPLAY_SLOT_SIDEBAR = 1;
    public static final int DISPLAY_SLOT_BELOW_NAME = 2;
    public static final int DISPLAY_SLOT_TEAMS_SIDEBAR_START = 3;
    public static final int DISPLAY_SLOT_TEAMS_SIDEBAR_END = 18;
    public static final int DISPLAY_SLOTS = 19;
    private final Map<String, ScoreboardObjective> objectivesByName = Maps.newHashMap();
    private final Map<IScoreboardCriteria, List<ScoreboardObjective>> objectivesByCriteria = Maps.newHashMap();
    private final Map<String, Map<ScoreboardObjective, ScoreboardScore>> playerScores = Maps.newHashMap();
    private final ScoreboardObjective[] displayObjectives = new ScoreboardObjective[19];
    private final Map<String, ScoreboardTeam> teamsByName = Maps.newHashMap();
    private final Map<String, ScoreboardTeam> teamsByPlayer = Maps.newHashMap();
    @Nullable
    private static String[] displaySlotNames;

    public Scoreboard() {}

    public boolean hasObjective(String s) {
        return this.objectivesByName.containsKey(s);
    }

    public ScoreboardObjective getOrCreateObjective(String s) {
        return (ScoreboardObjective) this.objectivesByName.get(s);
    }

    @Nullable
    public ScoreboardObjective getObjective(@Nullable String s) {
        return (ScoreboardObjective) this.objectivesByName.get(s);
    }

    public ScoreboardObjective addObjective(String s, IScoreboardCriteria iscoreboardcriteria, IChatBaseComponent ichatbasecomponent, IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        if (this.objectivesByName.containsKey(s)) {
            throw new IllegalArgumentException("An objective with the name '" + s + "' already exists!");
        } else {
            ScoreboardObjective scoreboardobjective = new ScoreboardObjective(this, s, iscoreboardcriteria, ichatbasecomponent, iscoreboardcriteria_enumscoreboardhealthdisplay);

            ((List) this.objectivesByCriteria.computeIfAbsent(iscoreboardcriteria, (iscoreboardcriteria1) -> {
                return Lists.newArrayList();
            })).add(scoreboardobjective);
            this.objectivesByName.put(s, scoreboardobjective);
            this.onObjectiveAdded(scoreboardobjective);
            return scoreboardobjective;
        }
    }

    public final void forAllObjectives(IScoreboardCriteria iscoreboardcriteria, String s, Consumer<ScoreboardScore> consumer) {
        ((List) this.objectivesByCriteria.getOrDefault(iscoreboardcriteria, Collections.emptyList())).forEach((scoreboardobjective) -> {
            consumer.accept(this.getOrCreatePlayerScore(s, scoreboardobjective));
        });
    }

    public boolean hasPlayerScore(String s, ScoreboardObjective scoreboardobjective) {
        Map<ScoreboardObjective, ScoreboardScore> map = (Map) this.playerScores.get(s);

        if (map == null) {
            return false;
        } else {
            ScoreboardScore scoreboardscore = (ScoreboardScore) map.get(scoreboardobjective);

            return scoreboardscore != null;
        }
    }

    public ScoreboardScore getOrCreatePlayerScore(String s, ScoreboardObjective scoreboardobjective) {
        Map<ScoreboardObjective, ScoreboardScore> map = (Map) this.playerScores.computeIfAbsent(s, (s1) -> {
            return Maps.newHashMap();
        });

        return (ScoreboardScore) map.computeIfAbsent(scoreboardobjective, (scoreboardobjective1) -> {
            ScoreboardScore scoreboardscore = new ScoreboardScore(this, scoreboardobjective1, s);

            scoreboardscore.setScore(0);
            return scoreboardscore;
        });
    }

    public Collection<ScoreboardScore> getPlayerScores(ScoreboardObjective scoreboardobjective) {
        List<ScoreboardScore> list = Lists.newArrayList();
        Iterator iterator = this.playerScores.values().iterator();

        while (iterator.hasNext()) {
            Map<ScoreboardObjective, ScoreboardScore> map = (Map) iterator.next();
            ScoreboardScore scoreboardscore = (ScoreboardScore) map.get(scoreboardobjective);

            if (scoreboardscore != null) {
                list.add(scoreboardscore);
            }
        }

        list.sort(ScoreboardScore.SCORE_COMPARATOR);
        return list;
    }

    public Collection<ScoreboardObjective> getObjectives() {
        return this.objectivesByName.values();
    }

    public Collection<String> getObjectiveNames() {
        return this.objectivesByName.keySet();
    }

    public Collection<String> getTrackedPlayers() {
        return Lists.newArrayList(this.playerScores.keySet());
    }

    public void resetPlayerScore(String s, @Nullable ScoreboardObjective scoreboardobjective) {
        Map map;

        if (scoreboardobjective == null) {
            map = (Map) this.playerScores.remove(s);
            if (map != null) {
                this.onPlayerRemoved(s);
            }
        } else {
            map = (Map) this.playerScores.get(s);
            if (map != null) {
                ScoreboardScore scoreboardscore = (ScoreboardScore) map.remove(scoreboardobjective);

                if (map.size() < 1) {
                    Map<ScoreboardObjective, ScoreboardScore> map1 = (Map) this.playerScores.remove(s);

                    if (map1 != null) {
                        this.onPlayerRemoved(s);
                    }
                } else if (scoreboardscore != null) {
                    this.onPlayerScoreRemoved(s, scoreboardobjective);
                }
            }
        }

    }

    public Map<ScoreboardObjective, ScoreboardScore> getPlayerScores(String s) {
        Map<ScoreboardObjective, ScoreboardScore> map = (Map) this.playerScores.get(s);

        if (map == null) {
            map = Maps.newHashMap();
        }

        return (Map) map;
    }

    public void removeObjective(ScoreboardObjective scoreboardobjective) {
        this.objectivesByName.remove(scoreboardobjective.getName());

        for (int i = 0; i < 19; ++i) {
            if (this.getDisplayObjective(i) == scoreboardobjective) {
                this.setDisplayObjective(i, (ScoreboardObjective) null);
            }
        }

        List<ScoreboardObjective> list = (List) this.objectivesByCriteria.get(scoreboardobjective.getCriteria());

        if (list != null) {
            list.remove(scoreboardobjective);
        }

        Iterator iterator = this.playerScores.values().iterator();

        while (iterator.hasNext()) {
            Map<ScoreboardObjective, ScoreboardScore> map = (Map) iterator.next();

            map.remove(scoreboardobjective);
        }

        this.onObjectiveRemoved(scoreboardobjective);
    }

    public void setDisplayObjective(int i, @Nullable ScoreboardObjective scoreboardobjective) {
        this.displayObjectives[i] = scoreboardobjective;
    }

    @Nullable
    public ScoreboardObjective getDisplayObjective(int i) {
        return this.displayObjectives[i];
    }

    @Nullable
    public ScoreboardTeam getPlayerTeam(String s) {
        return (ScoreboardTeam) this.teamsByName.get(s);
    }

    public ScoreboardTeam addPlayerTeam(String s) {
        ScoreboardTeam scoreboardteam = this.getPlayerTeam(s);

        if (scoreboardteam != null) {
            Scoreboard.LOGGER.warn("Requested creation of existing team '{}'", s);
            return scoreboardteam;
        } else {
            scoreboardteam = new ScoreboardTeam(this, s);
            this.teamsByName.put(s, scoreboardteam);
            this.onTeamAdded(scoreboardteam);
            return scoreboardteam;
        }
    }

    public void removePlayerTeam(ScoreboardTeam scoreboardteam) {
        this.teamsByName.remove(scoreboardteam.getName());
        Iterator iterator = scoreboardteam.getPlayers().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            this.teamsByPlayer.remove(s);
        }

        this.onTeamRemoved(scoreboardteam);
    }

    public boolean addPlayerToTeam(String s, ScoreboardTeam scoreboardteam) {
        if (this.getPlayersTeam(s) != null) {
            this.removePlayerFromTeam(s);
        }

        this.teamsByPlayer.put(s, scoreboardteam);
        return scoreboardteam.getPlayers().add(s);
    }

    public boolean removePlayerFromTeam(String s) {
        ScoreboardTeam scoreboardteam = this.getPlayersTeam(s);

        if (scoreboardteam != null) {
            this.removePlayerFromTeam(s, scoreboardteam);
            return true;
        } else {
            return false;
        }
    }

    public void removePlayerFromTeam(String s, ScoreboardTeam scoreboardteam) {
        if (this.getPlayersTeam(s) != scoreboardteam) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + scoreboardteam.getName() + "'.");
        } else {
            this.teamsByPlayer.remove(s);
            scoreboardteam.getPlayers().remove(s);
        }
    }

    public Collection<String> getTeamNames() {
        return this.teamsByName.keySet();
    }

    public Collection<ScoreboardTeam> getPlayerTeams() {
        return this.teamsByName.values();
    }

    @Nullable
    public ScoreboardTeam getPlayersTeam(String s) {
        return (ScoreboardTeam) this.teamsByPlayer.get(s);
    }

    public void onObjectiveAdded(ScoreboardObjective scoreboardobjective) {}

    public void onObjectiveChanged(ScoreboardObjective scoreboardobjective) {}

    public void onObjectiveRemoved(ScoreboardObjective scoreboardobjective) {}

    public void onScoreChanged(ScoreboardScore scoreboardscore) {}

    public void onPlayerRemoved(String s) {}

    public void onPlayerScoreRemoved(String s, ScoreboardObjective scoreboardobjective) {}

    public void onTeamAdded(ScoreboardTeam scoreboardteam) {}

    public void onTeamChanged(ScoreboardTeam scoreboardteam) {}

    public void onTeamRemoved(ScoreboardTeam scoreboardteam) {}

    public static String getDisplaySlotName(int i) {
        switch (i) {
            case 0:
                return "list";
            case 1:
                return "sidebar";
            case 2:
                return "belowName";
            default:
                if (i >= 3 && i <= 18) {
                    EnumChatFormat enumchatformat = EnumChatFormat.getById(i - 3);

                    if (enumchatformat != null && enumchatformat != EnumChatFormat.RESET) {
                        return "sidebar.team." + enumchatformat.getName();
                    }
                }

                return null;
        }
    }

    public static int getDisplaySlotByName(String s) {
        if ("list".equalsIgnoreCase(s)) {
            return 0;
        } else if ("sidebar".equalsIgnoreCase(s)) {
            return 1;
        } else if ("belowName".equalsIgnoreCase(s)) {
            return 2;
        } else {
            if (s.startsWith("sidebar.team.")) {
                String s1 = s.substring("sidebar.team.".length());
                EnumChatFormat enumchatformat = EnumChatFormat.getByName(s1);

                if (enumchatformat != null && enumchatformat.getId() >= 0) {
                    return enumchatformat.getId() + 3;
                }
            }

            return -1;
        }
    }

    public static String[] getDisplaySlotNames() {
        if (Scoreboard.displaySlotNames == null) {
            Scoreboard.displaySlotNames = new String[19];

            for (int i = 0; i < 19; ++i) {
                Scoreboard.displaySlotNames[i] = getDisplaySlotName(i);
            }
        }

        return Scoreboard.displaySlotNames;
    }

    public void entityRemoved(Entity entity) {
        if (entity != null && !(entity instanceof EntityHuman) && !entity.isAlive()) {
            String s = entity.getStringUUID();

            this.resetPlayerScore(s, (ScoreboardObjective) null);
            this.removePlayerFromTeam(s);
        }
    }

    protected NBTTagList savePlayerScores() {
        NBTTagList nbttaglist = new NBTTagList();

        this.playerScores.values().stream().map(Map::values).forEach((collection) -> {
            collection.stream().filter((scoreboardscore) -> {
                return scoreboardscore.getObjective() != null;
            }).forEach((scoreboardscore) -> {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.putString("Name", scoreboardscore.getOwner());
                nbttagcompound.putString("Objective", scoreboardscore.getObjective().getName());
                nbttagcompound.putInt("Score", scoreboardscore.getScore());
                nbttagcompound.putBoolean("Locked", scoreboardscore.isLocked());
                nbttaglist.add(nbttagcompound);
            });
        });
        return nbttaglist;
    }

    protected void loadPlayerScores(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            ScoreboardObjective scoreboardobjective = this.getOrCreateObjective(nbttagcompound.getString("Objective"));
            String s = nbttagcompound.getString("Name");
            ScoreboardScore scoreboardscore = this.getOrCreatePlayerScore(s, scoreboardobjective);

            scoreboardscore.setScore(nbttagcompound.getInt("Score"));
            if (nbttagcompound.contains("Locked")) {
                scoreboardscore.setLocked(nbttagcompound.getBoolean("Locked"));
            }
        }

    }
}
