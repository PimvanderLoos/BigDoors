package net.minecraft.world.scores;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.EnumChatFormat;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.world.level.saveddata.PersistentBase;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

public class PersistentScoreboard extends PersistentBase {

    public static final String FILE_ID = "scoreboard";
    private final Scoreboard scoreboard;

    public PersistentScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public PersistentScoreboard load(NBTTagCompound nbttagcompound) {
        this.loadObjectives(nbttagcompound.getList("Objectives", 10));
        this.scoreboard.loadPlayerScores(nbttagcompound.getList("PlayerScores", 10));
        if (nbttagcompound.contains("DisplaySlots", 10)) {
            this.loadDisplaySlots(nbttagcompound.getCompound("DisplaySlots"));
        }

        if (nbttagcompound.contains("Teams", 9)) {
            this.loadTeams(nbttagcompound.getList("Teams", 10));
        }

        return this;
    }

    private void loadTeams(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            String s = nbttagcompound.getString("Name");
            ScoreboardTeam scoreboardteam = this.scoreboard.addPlayerTeam(s);
            IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("DisplayName"));

            if (ichatmutablecomponent != null) {
                scoreboardteam.setDisplayName(ichatmutablecomponent);
            }

            if (nbttagcompound.contains("TeamColor", 8)) {
                scoreboardteam.setColor(EnumChatFormat.getByName(nbttagcompound.getString("TeamColor")));
            }

            if (nbttagcompound.contains("AllowFriendlyFire", 99)) {
                scoreboardteam.setAllowFriendlyFire(nbttagcompound.getBoolean("AllowFriendlyFire"));
            }

            if (nbttagcompound.contains("SeeFriendlyInvisibles", 99)) {
                scoreboardteam.setSeeFriendlyInvisibles(nbttagcompound.getBoolean("SeeFriendlyInvisibles"));
            }

            IChatMutableComponent ichatmutablecomponent1;

            if (nbttagcompound.contains("MemberNamePrefix", 8)) {
                ichatmutablecomponent1 = IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("MemberNamePrefix"));
                if (ichatmutablecomponent1 != null) {
                    scoreboardteam.setPlayerPrefix(ichatmutablecomponent1);
                }
            }

            if (nbttagcompound.contains("MemberNameSuffix", 8)) {
                ichatmutablecomponent1 = IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("MemberNameSuffix"));
                if (ichatmutablecomponent1 != null) {
                    scoreboardteam.setPlayerSuffix(ichatmutablecomponent1);
                }
            }

            ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility;

            if (nbttagcompound.contains("NameTagVisibility", 8)) {
                scoreboardteambase_enumnametagvisibility = ScoreboardTeamBase.EnumNameTagVisibility.byName(nbttagcompound.getString("NameTagVisibility"));
                if (scoreboardteambase_enumnametagvisibility != null) {
                    scoreboardteam.setNameTagVisibility(scoreboardteambase_enumnametagvisibility);
                }
            }

            if (nbttagcompound.contains("DeathMessageVisibility", 8)) {
                scoreboardteambase_enumnametagvisibility = ScoreboardTeamBase.EnumNameTagVisibility.byName(nbttagcompound.getString("DeathMessageVisibility"));
                if (scoreboardteambase_enumnametagvisibility != null) {
                    scoreboardteam.setDeathMessageVisibility(scoreboardteambase_enumnametagvisibility);
                }
            }

            if (nbttagcompound.contains("CollisionRule", 8)) {
                ScoreboardTeamBase.EnumTeamPush scoreboardteambase_enumteampush = ScoreboardTeamBase.EnumTeamPush.byName(nbttagcompound.getString("CollisionRule"));

                if (scoreboardteambase_enumteampush != null) {
                    scoreboardteam.setCollisionRule(scoreboardteambase_enumteampush);
                }
            }

            this.loadTeamPlayers(scoreboardteam, nbttagcompound.getList("Players", 8));
        }

    }

    private void loadTeamPlayers(ScoreboardTeam scoreboardteam, NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            this.scoreboard.addPlayerToTeam(nbttaglist.getString(i), scoreboardteam);
        }

    }

    private void loadDisplaySlots(NBTTagCompound nbttagcompound) {
        for (int i = 0; i < 19; ++i) {
            if (nbttagcompound.contains("slot_" + i, 8)) {
                String s = nbttagcompound.getString("slot_" + i);
                ScoreboardObjective scoreboardobjective = this.scoreboard.getObjective(s);

                this.scoreboard.setDisplayObjective(i, scoreboardobjective);
            }
        }

    }

    private void loadObjectives(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);

            IScoreboardCriteria.byName(nbttagcompound.getString("CriteriaName")).ifPresent((iscoreboardcriteria) -> {
                String s = nbttagcompound.getString("Name");
                IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("DisplayName"));
                IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay = IScoreboardCriteria.EnumScoreboardHealthDisplay.byId(nbttagcompound.getString("RenderType"));

                this.scoreboard.addObjective(s, iscoreboardcriteria, ichatmutablecomponent, iscoreboardcriteria_enumscoreboardhealthdisplay);
            });
        }

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        nbttagcompound.put("Objectives", this.saveObjectives());
        nbttagcompound.put("PlayerScores", this.scoreboard.savePlayerScores());
        nbttagcompound.put("Teams", this.saveTeams());
        this.saveDisplaySlots(nbttagcompound);
        return nbttagcompound;
    }

    private NBTTagList saveTeams() {
        NBTTagList nbttaglist = new NBTTagList();
        Collection<ScoreboardTeam> collection = this.scoreboard.getPlayerTeams();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreboardTeam scoreboardteam = (ScoreboardTeam) iterator.next();
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.putString("Name", scoreboardteam.getName());
            nbttagcompound.putString("DisplayName", IChatBaseComponent.ChatSerializer.toJson(scoreboardteam.getDisplayName()));
            if (scoreboardteam.getColor().getId() >= 0) {
                nbttagcompound.putString("TeamColor", scoreboardteam.getColor().getName());
            }

            nbttagcompound.putBoolean("AllowFriendlyFire", scoreboardteam.isAllowFriendlyFire());
            nbttagcompound.putBoolean("SeeFriendlyInvisibles", scoreboardteam.canSeeFriendlyInvisibles());
            nbttagcompound.putString("MemberNamePrefix", IChatBaseComponent.ChatSerializer.toJson(scoreboardteam.getPlayerPrefix()));
            nbttagcompound.putString("MemberNameSuffix", IChatBaseComponent.ChatSerializer.toJson(scoreboardteam.getPlayerSuffix()));
            nbttagcompound.putString("NameTagVisibility", scoreboardteam.getNameTagVisibility().name);
            nbttagcompound.putString("DeathMessageVisibility", scoreboardteam.getDeathMessageVisibility().name);
            nbttagcompound.putString("CollisionRule", scoreboardteam.getCollisionRule().name);
            NBTTagList nbttaglist1 = new NBTTagList();
            Iterator iterator1 = scoreboardteam.getPlayers().iterator();

            while (iterator1.hasNext()) {
                String s = (String) iterator1.next();

                nbttaglist1.add(NBTTagString.valueOf(s));
            }

            nbttagcompound.put("Players", nbttaglist1);
            nbttaglist.add(nbttagcompound);
        }

        return nbttaglist;
    }

    private void saveDisplaySlots(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        boolean flag = false;

        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective scoreboardobjective = this.scoreboard.getDisplayObjective(i);

            if (scoreboardobjective != null) {
                nbttagcompound1.putString("slot_" + i, scoreboardobjective.getName());
                flag = true;
            }
        }

        if (flag) {
            nbttagcompound.put("DisplaySlots", nbttagcompound1);
        }

    }

    private NBTTagList saveObjectives() {
        NBTTagList nbttaglist = new NBTTagList();
        Collection<ScoreboardObjective> collection = this.scoreboard.getObjectives();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

            if (scoreboardobjective.getCriteria() != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.putString("Name", scoreboardobjective.getName());
                nbttagcompound.putString("CriteriaName", scoreboardobjective.getCriteria().getName());
                nbttagcompound.putString("DisplayName", IChatBaseComponent.ChatSerializer.toJson(scoreboardobjective.getDisplayName()));
                nbttagcompound.putString("RenderType", scoreboardobjective.getRenderType().getId());
                nbttaglist.add(nbttagcompound);
            }
        }

        return nbttaglist;
    }
}
