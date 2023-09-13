package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class CommandScoreboard extends CommandAbstract {

    public CommandScoreboard() {}

    public String getCommand() {
        return "scoreboard";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.scoreboard.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (!this.b(minecraftserver, icommandlistener, astring)) {
            if (astring.length < 1) {
                throw new ExceptionUsage("commands.scoreboard.usage", new Object[0]);
            } else {
                if ("objectives".equalsIgnoreCase(astring[0])) {
                    if (astring.length == 1) {
                        throw new ExceptionUsage("commands.scoreboard.objectives.usage", new Object[0]);
                    }

                    if ("list".equalsIgnoreCase(astring[1])) {
                        this.a(icommandlistener, minecraftserver);
                    } else if ("add".equalsIgnoreCase(astring[1])) {
                        if (astring.length < 4) {
                            throw new ExceptionUsage("commands.scoreboard.objectives.add.usage", new Object[0]);
                        }

                        this.a(icommandlistener, astring, 2, minecraftserver);
                    } else if ("remove".equalsIgnoreCase(astring[1])) {
                        if (astring.length != 3) {
                            throw new ExceptionUsage("commands.scoreboard.objectives.remove.usage", new Object[0]);
                        }

                        this.a(icommandlistener, astring[2], minecraftserver);
                    } else {
                        if (!"setdisplay".equalsIgnoreCase(astring[1])) {
                            throw new ExceptionUsage("commands.scoreboard.objectives.usage", new Object[0]);
                        }

                        if (astring.length != 3 && astring.length != 4) {
                            throw new ExceptionUsage("commands.scoreboard.objectives.setdisplay.usage", new Object[0]);
                        }

                        this.i(icommandlistener, astring, 2, minecraftserver);
                    }
                } else if ("players".equalsIgnoreCase(astring[0])) {
                    if (astring.length == 1) {
                        throw new ExceptionUsage("commands.scoreboard.players.usage", new Object[0]);
                    }

                    if ("list".equalsIgnoreCase(astring[1])) {
                        if (astring.length > 3) {
                            throw new ExceptionUsage("commands.scoreboard.players.list.usage", new Object[0]);
                        }

                        this.j(icommandlistener, astring, 2, minecraftserver);
                    } else if ("add".equalsIgnoreCase(astring[1])) {
                        if (astring.length < 5) {
                            throw new ExceptionUsage("commands.scoreboard.players.add.usage", new Object[0]);
                        }

                        this.k(icommandlistener, astring, 2, minecraftserver);
                    } else if ("remove".equalsIgnoreCase(astring[1])) {
                        if (astring.length < 5) {
                            throw new ExceptionUsage("commands.scoreboard.players.remove.usage", new Object[0]);
                        }

                        this.k(icommandlistener, astring, 2, minecraftserver);
                    } else if ("set".equalsIgnoreCase(astring[1])) {
                        if (astring.length < 5) {
                            throw new ExceptionUsage("commands.scoreboard.players.set.usage", new Object[0]);
                        }

                        this.k(icommandlistener, astring, 2, minecraftserver);
                    } else if ("reset".equalsIgnoreCase(astring[1])) {
                        if (astring.length != 3 && astring.length != 4) {
                            throw new ExceptionUsage("commands.scoreboard.players.reset.usage", new Object[0]);
                        }

                        this.l(icommandlistener, astring, 2, minecraftserver);
                    } else if ("enable".equalsIgnoreCase(astring[1])) {
                        if (astring.length != 4) {
                            throw new ExceptionUsage("commands.scoreboard.players.enable.usage", new Object[0]);
                        }

                        this.m(icommandlistener, astring, 2, minecraftserver);
                    } else if ("test".equalsIgnoreCase(astring[1])) {
                        if (astring.length != 5 && astring.length != 6) {
                            throw new ExceptionUsage("commands.scoreboard.players.test.usage", new Object[0]);
                        }

                        this.n(icommandlistener, astring, 2, minecraftserver);
                    } else if ("operation".equalsIgnoreCase(astring[1])) {
                        if (astring.length != 7) {
                            throw new ExceptionUsage("commands.scoreboard.players.operation.usage", new Object[0]);
                        }

                        this.o(icommandlistener, astring, 2, minecraftserver);
                    } else {
                        if (!"tag".equalsIgnoreCase(astring[1])) {
                            throw new ExceptionUsage("commands.scoreboard.players.usage", new Object[0]);
                        }

                        if (astring.length < 4) {
                            throw new ExceptionUsage("commands.scoreboard.players.tag.usage", new Object[0]);
                        }

                        this.a(minecraftserver, icommandlistener, astring, 2);
                    }
                } else {
                    if (!"teams".equalsIgnoreCase(astring[0])) {
                        throw new ExceptionUsage("commands.scoreboard.usage", new Object[0]);
                    }

                    if (astring.length == 1) {
                        throw new ExceptionUsage("commands.scoreboard.teams.usage", new Object[0]);
                    }

                    if ("list".equalsIgnoreCase(astring[1])) {
                        if (astring.length > 3) {
                            throw new ExceptionUsage("commands.scoreboard.teams.list.usage", new Object[0]);
                        }

                        this.e(icommandlistener, astring, 2, minecraftserver);
                    } else if ("add".equalsIgnoreCase(astring[1])) {
                        if (astring.length < 3) {
                            throw new ExceptionUsage("commands.scoreboard.teams.add.usage", new Object[0]);
                        }

                        this.b(icommandlistener, astring, 2, minecraftserver);
                    } else if ("remove".equalsIgnoreCase(astring[1])) {
                        if (astring.length != 3) {
                            throw new ExceptionUsage("commands.scoreboard.teams.remove.usage", new Object[0]);
                        }

                        this.d(icommandlistener, astring, 2, minecraftserver);
                    } else if ("empty".equalsIgnoreCase(astring[1])) {
                        if (astring.length != 3) {
                            throw new ExceptionUsage("commands.scoreboard.teams.empty.usage", new Object[0]);
                        }

                        this.h(icommandlistener, astring, 2, minecraftserver);
                    } else if ("join".equalsIgnoreCase(astring[1])) {
                        if (astring.length < 4 && (astring.length != 3 || !(icommandlistener instanceof EntityHuman))) {
                            throw new ExceptionUsage("commands.scoreboard.teams.join.usage", new Object[0]);
                        }

                        this.f(icommandlistener, astring, 2, minecraftserver);
                    } else if ("leave".equalsIgnoreCase(astring[1])) {
                        if (astring.length < 3 && !(icommandlistener instanceof EntityHuman)) {
                            throw new ExceptionUsage("commands.scoreboard.teams.leave.usage", new Object[0]);
                        }

                        this.g(icommandlistener, astring, 2, minecraftserver);
                    } else {
                        if (!"option".equalsIgnoreCase(astring[1])) {
                            throw new ExceptionUsage("commands.scoreboard.teams.usage", new Object[0]);
                        }

                        if (astring.length != 4 && astring.length != 5) {
                            throw new ExceptionUsage("commands.scoreboard.teams.option.usage", new Object[0]);
                        }

                        this.c(icommandlistener, astring, 2, minecraftserver);
                    }
                }

            }
        }
    }

    private boolean b(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        int i = -1;

        for (int j = 0; j < astring.length; ++j) {
            if (this.isListStart(astring, j) && "*".equals(astring[j])) {
                if (i >= 0) {
                    throw new CommandException("commands.scoreboard.noMultiWildcard", new Object[0]);
                }

                i = j;
            }
        }

        if (i < 0) {
            return false;
        } else {
            ArrayList arraylist = Lists.newArrayList(this.a(minecraftserver).getPlayers());
            String s = astring[i];
            ArrayList arraylist1 = Lists.newArrayList();
            Iterator iterator = arraylist.iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();

                astring[i] = s1;

                try {
                    this.execute(minecraftserver, icommandlistener, astring);
                    arraylist1.add(s1);
                } catch (CommandException commandexception) {
                    ChatMessage chatmessage = new ChatMessage(commandexception.getMessage(), commandexception.getArgs());

                    chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
                    icommandlistener.sendMessage(chatmessage);
                }
            }

            astring[i] = s;
            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, arraylist1.size());
            if (arraylist1.isEmpty()) {
                throw new ExceptionUsage("commands.scoreboard.allMatchesFailed", new Object[0]);
            } else {
                return true;
            }
        }
    }

    protected Scoreboard a(MinecraftServer minecraftserver) {
        return minecraftserver.getWorldServer(0).getScoreboard();
    }

    protected ScoreboardObjective a(String s, boolean flag, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        ScoreboardObjective scoreboardobjective = scoreboard.getObjective(s);

        if (scoreboardobjective == null) {
            throw new CommandException("commands.scoreboard.objectiveNotFound", new Object[] { s});
        } else if (flag && scoreboardobjective.getCriteria().isReadOnly()) {
            throw new CommandException("commands.scoreboard.objectiveReadOnly", new Object[] { s});
        } else {
            return scoreboardobjective;
        }
    }

    protected ScoreboardTeam a(String s, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        ScoreboardTeam scoreboardteam = scoreboard.getTeam(s);

        if (scoreboardteam == null) {
            throw new CommandException("commands.scoreboard.teamNotFound", new Object[] { s});
        } else {
            return scoreboardteam;
        }
    }

    protected void a(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        String s = astring[i++];
        String s1 = astring[i++];
        Scoreboard scoreboard = this.a(minecraftserver);
        IScoreboardCriteria iscoreboardcriteria = (IScoreboardCriteria) IScoreboardCriteria.criteria.get(s1);

        if (iscoreboardcriteria == null) {
            throw new ExceptionUsage("commands.scoreboard.objectives.add.wrongType", new Object[] { s1});
        } else if (scoreboard.getObjective(s) != null) {
            throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", new Object[] { s});
        } else if (s.length() > 16) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.objectives.add.tooLong", new Object[] { s, Integer.valueOf(16)});
        } else if (s.isEmpty()) {
            throw new ExceptionUsage("commands.scoreboard.objectives.add.usage", new Object[0]);
        } else {
            if (astring.length > i) {
                String s2 = a(icommandlistener, astring, i).toPlainText();

                if (s2.length() > 32) {
                    throw new ExceptionInvalidSyntax("commands.scoreboard.objectives.add.displayTooLong", new Object[] { s2, Integer.valueOf(32)});
                }

                if (s2.isEmpty()) {
                    scoreboard.registerObjective(s, iscoreboardcriteria);
                } else {
                    scoreboard.registerObjective(s, iscoreboardcriteria).setDisplayName(s2);
                }
            } else {
                scoreboard.registerObjective(s, iscoreboardcriteria);
            }

            a(icommandlistener, (ICommand) this, "commands.scoreboard.objectives.add.success", new Object[] { s});
        }
    }

    protected void b(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        String s = astring[i++];
        Scoreboard scoreboard = this.a(minecraftserver);

        if (scoreboard.getTeam(s) != null) {
            throw new CommandException("commands.scoreboard.teams.add.alreadyExists", new Object[] { s});
        } else if (s.length() > 16) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.teams.add.tooLong", new Object[] { s, Integer.valueOf(16)});
        } else if (s.isEmpty()) {
            throw new ExceptionUsage("commands.scoreboard.teams.add.usage", new Object[0]);
        } else {
            if (astring.length > i) {
                String s1 = a(icommandlistener, astring, i).toPlainText();

                if (s1.length() > 32) {
                    throw new ExceptionInvalidSyntax("commands.scoreboard.teams.add.displayTooLong", new Object[] { s1, Integer.valueOf(32)});
                }

                if (s1.isEmpty()) {
                    scoreboard.createTeam(s);
                } else {
                    scoreboard.createTeam(s).setDisplayName(s1);
                }
            } else {
                scoreboard.createTeam(s);
            }

            a(icommandlistener, (ICommand) this, "commands.scoreboard.teams.add.success", new Object[] { s});
        }
    }

    protected void c(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        ScoreboardTeam scoreboardteam = this.a(astring[i++], minecraftserver);

        if (scoreboardteam != null) {
            String s = astring[i++].toLowerCase(Locale.ROOT);

            if (!"color".equalsIgnoreCase(s) && !"friendlyfire".equalsIgnoreCase(s) && !"seeFriendlyInvisibles".equalsIgnoreCase(s) && !"nametagVisibility".equalsIgnoreCase(s) && !"deathMessageVisibility".equalsIgnoreCase(s) && !"collisionRule".equalsIgnoreCase(s)) {
                throw new ExceptionUsage("commands.scoreboard.teams.option.usage", new Object[0]);
            } else if (astring.length == 4) {
                if ("color".equalsIgnoreCase(s)) {
                    throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a(EnumChatFormat.a(true, false))});
                } else if (!"friendlyfire".equalsIgnoreCase(s) && !"seeFriendlyInvisibles".equalsIgnoreCase(s)) {
                    if (!"nametagVisibility".equalsIgnoreCase(s) && !"deathMessageVisibility".equalsIgnoreCase(s)) {
                        if ("collisionRule".equalsIgnoreCase(s)) {
                            throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Object[]) ScoreboardTeamBase.EnumTeamPush.a())});
                        } else {
                            throw new ExceptionUsage("commands.scoreboard.teams.option.usage", new Object[0]);
                        }
                    } else {
                        throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Object[]) ScoreboardTeamBase.EnumNameTagVisibility.a())});
                    }
                } else {
                    throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Collection) Arrays.asList(new String[] { "true", "false"}))});
                }
            } else {
                String s1 = astring[i];

                if ("color".equalsIgnoreCase(s)) {
                    EnumChatFormat enumchatformat = EnumChatFormat.b(s1);

                    if (enumchatformat == null || enumchatformat.isFormat()) {
                        throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a(EnumChatFormat.a(true, false))});
                    }

                    scoreboardteam.setColor(enumchatformat);
                    scoreboardteam.setPrefix(enumchatformat.toString());
                    scoreboardteam.setSuffix(EnumChatFormat.RESET.toString());
                } else if ("friendlyfire".equalsIgnoreCase(s)) {
                    if (!"true".equalsIgnoreCase(s1) && !"false".equalsIgnoreCase(s1)) {
                        throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Collection) Arrays.asList(new String[] { "true", "false"}))});
                    }

                    scoreboardteam.setAllowFriendlyFire("true".equalsIgnoreCase(s1));
                } else if ("seeFriendlyInvisibles".equalsIgnoreCase(s)) {
                    if (!"true".equalsIgnoreCase(s1) && !"false".equalsIgnoreCase(s1)) {
                        throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Collection) Arrays.asList(new String[] { "true", "false"}))});
                    }

                    scoreboardteam.setCanSeeFriendlyInvisibles("true".equalsIgnoreCase(s1));
                } else {
                    ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility;

                    if ("nametagVisibility".equalsIgnoreCase(s)) {
                        scoreboardteambase_enumnametagvisibility = ScoreboardTeamBase.EnumNameTagVisibility.a(s1);
                        if (scoreboardteambase_enumnametagvisibility == null) {
                            throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Object[]) ScoreboardTeamBase.EnumNameTagVisibility.a())});
                        }

                        scoreboardteam.setNameTagVisibility(scoreboardteambase_enumnametagvisibility);
                    } else if ("deathMessageVisibility".equalsIgnoreCase(s)) {
                        scoreboardteambase_enumnametagvisibility = ScoreboardTeamBase.EnumNameTagVisibility.a(s1);
                        if (scoreboardteambase_enumnametagvisibility == null) {
                            throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Object[]) ScoreboardTeamBase.EnumNameTagVisibility.a())});
                        }

                        scoreboardteam.setDeathMessageVisibility(scoreboardteambase_enumnametagvisibility);
                    } else if ("collisionRule".equalsIgnoreCase(s)) {
                        ScoreboardTeamBase.EnumTeamPush scoreboardteambase_enumteampush = ScoreboardTeamBase.EnumTeamPush.a(s1);

                        if (scoreboardteambase_enumteampush == null) {
                            throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Object[]) ScoreboardTeamBase.EnumTeamPush.a())});
                        }

                        scoreboardteam.setCollisionRule(scoreboardteambase_enumteampush);
                    }
                }

                a(icommandlistener, (ICommand) this, "commands.scoreboard.teams.option.success", new Object[] { s, scoreboardteam.getName(), s1});
            }
        }
    }

    protected void d(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        ScoreboardTeam scoreboardteam = this.a(astring[i], minecraftserver);

        if (scoreboardteam != null) {
            scoreboard.removeTeam(scoreboardteam);
            a(icommandlistener, (ICommand) this, "commands.scoreboard.teams.remove.success", new Object[] { scoreboardteam.getName()});
        }
    }

    protected void e(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);

        if (astring.length > i) {
            ScoreboardTeam scoreboardteam = this.a(astring[i], minecraftserver);

            if (scoreboardteam == null) {
                return;
            }

            Collection collection = scoreboardteam.getPlayerNameSet();

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, collection.size());
            if (collection.isEmpty()) {
                throw new CommandException("commands.scoreboard.teams.list.player.empty", new Object[] { scoreboardteam.getName()});
            }

            ChatMessage chatmessage = new ChatMessage("commands.scoreboard.teams.list.player.count", new Object[] { Integer.valueOf(collection.size()), scoreboardteam.getName()});

            chatmessage.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
            icommandlistener.sendMessage(chatmessage);
            icommandlistener.sendMessage(new ChatComponentText(a(collection.toArray())));
        } else {
            Collection collection1 = scoreboard.getTeams();

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, collection1.size());
            if (collection1.isEmpty()) {
                throw new CommandException("commands.scoreboard.teams.list.empty", new Object[0]);
            }

            ChatMessage chatmessage1 = new ChatMessage("commands.scoreboard.teams.list.count", new Object[] { Integer.valueOf(collection1.size())});

            chatmessage1.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
            icommandlistener.sendMessage(chatmessage1);
            Iterator iterator = collection1.iterator();

            while (iterator.hasNext()) {
                ScoreboardTeam scoreboardteam1 = (ScoreboardTeam) iterator.next();

                icommandlistener.sendMessage(new ChatMessage("commands.scoreboard.teams.list.entry", new Object[] { scoreboardteam1.getName(), scoreboardteam1.getDisplayName(), Integer.valueOf(scoreboardteam1.getPlayerNameSet().size())}));
            }
        }

    }

    protected void f(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        String s = astring[i++];
        HashSet hashset = Sets.newHashSet();
        HashSet hashset1 = Sets.newHashSet();
        String s1;

        if (icommandlistener instanceof EntityHuman && i == astring.length) {
            s1 = a(icommandlistener).getName();
            if (scoreboard.addPlayerToTeam(s1, s)) {
                hashset.add(s1);
            } else {
                hashset1.add(s1);
            }
        } else {
            while (i < astring.length) {
                s1 = astring[i++];
                if (PlayerSelector.isPattern(s1)) {
                    List list = d(minecraftserver, icommandlistener, s1);
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        Entity entity = (Entity) iterator.next();
                        String s2 = f(minecraftserver, icommandlistener, entity.bn());

                        if (scoreboard.addPlayerToTeam(s2, s)) {
                            hashset.add(s2);
                        } else {
                            hashset1.add(s2);
                        }
                    }
                } else {
                    String s3 = f(minecraftserver, icommandlistener, s1);

                    if (scoreboard.addPlayerToTeam(s3, s)) {
                        hashset.add(s3);
                    } else {
                        hashset1.add(s3);
                    }
                }
            }
        }

        if (!hashset.isEmpty()) {
            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, hashset.size());
            a(icommandlistener, (ICommand) this, "commands.scoreboard.teams.join.success", new Object[] { Integer.valueOf(hashset.size()), s, a(hashset.toArray(new String[hashset.size()]))});
        }

        if (!hashset1.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.join.failure", new Object[] { Integer.valueOf(hashset1.size()), s, a(hashset1.toArray(new String[hashset1.size()]))});
        }
    }

    protected void g(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        HashSet hashset = Sets.newHashSet();
        HashSet hashset1 = Sets.newHashSet();
        String s;

        if (icommandlistener instanceof EntityHuman && i == astring.length) {
            s = a(icommandlistener).getName();
            if (scoreboard.removePlayerFromTeam(s)) {
                hashset.add(s);
            } else {
                hashset1.add(s);
            }
        } else {
            while (i < astring.length) {
                s = astring[i++];
                if (PlayerSelector.isPattern(s)) {
                    List list = d(minecraftserver, icommandlistener, s);
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        Entity entity = (Entity) iterator.next();
                        String s1 = f(minecraftserver, icommandlistener, entity.bn());

                        if (scoreboard.removePlayerFromTeam(s1)) {
                            hashset.add(s1);
                        } else {
                            hashset1.add(s1);
                        }
                    }
                } else {
                    String s2 = f(minecraftserver, icommandlistener, s);

                    if (scoreboard.removePlayerFromTeam(s2)) {
                        hashset.add(s2);
                    } else {
                        hashset1.add(s2);
                    }
                }
            }
        }

        if (!hashset.isEmpty()) {
            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, hashset.size());
            a(icommandlistener, (ICommand) this, "commands.scoreboard.teams.leave.success", new Object[] { Integer.valueOf(hashset.size()), a(hashset.toArray(new String[hashset.size()]))});
        }

        if (!hashset1.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.leave.failure", new Object[] { Integer.valueOf(hashset1.size()), a(hashset1.toArray(new String[hashset1.size()]))});
        }
    }

    protected void h(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        ScoreboardTeam scoreboardteam = this.a(astring[i], minecraftserver);

        if (scoreboardteam != null) {
            ArrayList arraylist = Lists.newArrayList(scoreboardteam.getPlayerNameSet());

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, arraylist.size());
            if (arraylist.isEmpty()) {
                throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", new Object[] { scoreboardteam.getName()});
            } else {
                Iterator iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    scoreboard.removePlayerFromTeam(s, scoreboardteam);
                }

                a(icommandlistener, (ICommand) this, "commands.scoreboard.teams.empty.success", new Object[] { Integer.valueOf(arraylist.size()), scoreboardteam.getName()});
            }
        }
    }

    protected void a(ICommandListener icommandlistener, String s, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        ScoreboardObjective scoreboardobjective = this.a(s, false, minecraftserver);

        scoreboard.unregisterObjective(scoreboardobjective);
        a(icommandlistener, (ICommand) this, "commands.scoreboard.objectives.remove.success", new Object[] { s});
    }

    protected void a(ICommandListener icommandlistener, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        Collection collection = scoreboard.getObjectives();

        if (collection.isEmpty()) {
            throw new CommandException("commands.scoreboard.objectives.list.empty", new Object[0]);
        } else {
            ChatMessage chatmessage = new ChatMessage("commands.scoreboard.objectives.list.count", new Object[] { Integer.valueOf(collection.size())});

            chatmessage.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
            icommandlistener.sendMessage(chatmessage);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

                icommandlistener.sendMessage(new ChatMessage("commands.scoreboard.objectives.list.entry", new Object[] { scoreboardobjective.getName(), scoreboardobjective.getDisplayName(), scoreboardobjective.getCriteria().getName()}));
            }

        }
    }

    protected void i(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        String s = astring[i++];
        int j = Scoreboard.getSlotForName(s);
        ScoreboardObjective scoreboardobjective = null;

        if (astring.length == 4) {
            scoreboardobjective = this.a(astring[i], false, minecraftserver);
        }

        if (j < 0) {
            throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", new Object[] { s});
        } else {
            scoreboard.setDisplaySlot(j, scoreboardobjective);
            if (scoreboardobjective != null) {
                a(icommandlistener, (ICommand) this, "commands.scoreboard.objectives.setdisplay.successSet", new Object[] { Scoreboard.getSlotName(j), scoreboardobjective.getName()});
            } else {
                a(icommandlistener, (ICommand) this, "commands.scoreboard.objectives.setdisplay.successCleared", new Object[] { Scoreboard.getSlotName(j)});
            }

        }
    }

    protected void j(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);

        if (astring.length > i) {
            String s = f(minecraftserver, icommandlistener, astring[i]);
            Map map = scoreboard.getPlayerObjectives(s);

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, map.size());
            if (map.isEmpty()) {
                throw new CommandException("commands.scoreboard.players.list.player.empty", new Object[] { s});
            }

            ChatMessage chatmessage = new ChatMessage("commands.scoreboard.players.list.player.count", new Object[] { Integer.valueOf(map.size()), s});

            chatmessage.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
            icommandlistener.sendMessage(chatmessage);
            Iterator iterator = map.values().iterator();

            while (iterator.hasNext()) {
                ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next();

                icommandlistener.sendMessage(new ChatMessage("commands.scoreboard.players.list.player.entry", new Object[] { Integer.valueOf(scoreboardscore.getScore()), scoreboardscore.getObjective().getDisplayName(), scoreboardscore.getObjective().getName()}));
            }
        } else {
            Collection collection = scoreboard.getPlayers();

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, collection.size());
            if (collection.isEmpty()) {
                throw new CommandException("commands.scoreboard.players.list.empty", new Object[0]);
            }

            ChatMessage chatmessage1 = new ChatMessage("commands.scoreboard.players.list.count", new Object[] { Integer.valueOf(collection.size())});

            chatmessage1.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
            icommandlistener.sendMessage(chatmessage1);
            icommandlistener.sendMessage(new ChatComponentText(a(collection.toArray())));
        }

    }

    protected void k(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        String s = astring[i - 1];
        int j = i;
        String s1 = f(minecraftserver, icommandlistener, astring[i++]);

        if (s1.length() > 40) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", new Object[] { s1, Integer.valueOf(40)});
        } else {
            ScoreboardObjective scoreboardobjective = this.a(astring[i++], true, minecraftserver);
            int k = "set".equalsIgnoreCase(s) ? a(astring[i++]) : a(astring[i++], 0);

            if (astring.length > i) {
                Entity entity = c(minecraftserver, icommandlistener, astring[j]);

                try {
                    NBTTagCompound nbttagcompound = MojangsonParser.parse(a(astring, i));
                    NBTTagCompound nbttagcompound1 = a(entity);

                    if (!GameProfileSerializer.a(nbttagcompound, nbttagcompound1, true)) {
                        throw new CommandException("commands.scoreboard.players.set.tagMismatch", new Object[] { s1});
                    }
                } catch (MojangsonParseException mojangsonparseexception) {
                    throw new CommandException("commands.scoreboard.players.set.tagError", new Object[] { mojangsonparseexception.getMessage()});
                }
            }

            Scoreboard scoreboard = this.a(minecraftserver);
            ScoreboardScore scoreboardscore = scoreboard.getPlayerScoreForObjective(s1, scoreboardobjective);

            if ("set".equalsIgnoreCase(s)) {
                scoreboardscore.setScore(k);
            } else if ("add".equalsIgnoreCase(s)) {
                scoreboardscore.addScore(k);
            } else {
                scoreboardscore.removeScore(k);
            }

            a(icommandlistener, (ICommand) this, "commands.scoreboard.players.set.success", new Object[] { scoreboardobjective.getName(), s1, Integer.valueOf(scoreboardscore.getScore())});
        }
    }

    protected void l(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        String s = f(minecraftserver, icommandlistener, astring[i++]);

        if (astring.length > i) {
            ScoreboardObjective scoreboardobjective = this.a(astring[i++], false, minecraftserver);

            scoreboard.resetPlayerScores(s, scoreboardobjective);
            a(icommandlistener, (ICommand) this, "commands.scoreboard.players.resetscore.success", new Object[] { scoreboardobjective.getName(), s});
        } else {
            scoreboard.resetPlayerScores(s, (ScoreboardObjective) null);
            a(icommandlistener, (ICommand) this, "commands.scoreboard.players.reset.success", new Object[] { s});
        }

    }

    protected void m(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        String s = e(minecraftserver, icommandlistener, astring[i++]);

        if (s.length() > 40) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", new Object[] { s, Integer.valueOf(40)});
        } else {
            ScoreboardObjective scoreboardobjective = this.a(astring[i], false, minecraftserver);

            if (scoreboardobjective.getCriteria() != IScoreboardCriteria.c) {
                throw new CommandException("commands.scoreboard.players.enable.noTrigger", new Object[] { scoreboardobjective.getName()});
            } else {
                ScoreboardScore scoreboardscore = scoreboard.getPlayerScoreForObjective(s, scoreboardobjective);

                scoreboardscore.a(false);
                a(icommandlistener, (ICommand) this, "commands.scoreboard.players.enable.success", new Object[] { scoreboardobjective.getName(), s});
            }
        }
    }

    protected void n(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        String s = f(minecraftserver, icommandlistener, astring[i++]);

        if (s.length() > 40) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", new Object[] { s, Integer.valueOf(40)});
        } else {
            ScoreboardObjective scoreboardobjective = this.a(astring[i++], false, minecraftserver);

            if (!scoreboard.b(s, scoreboardobjective)) {
                throw new CommandException("commands.scoreboard.players.test.notFound", new Object[] { scoreboardobjective.getName(), s});
            } else {
                int j = astring[i].equals("*") ? Integer.MIN_VALUE : a(astring[i]);

                ++i;
                int k = i < astring.length && !astring[i].equals("*") ? a(astring[i], j) : Integer.MAX_VALUE;
                ScoreboardScore scoreboardscore = scoreboard.getPlayerScoreForObjective(s, scoreboardobjective);

                if (scoreboardscore.getScore() >= j && scoreboardscore.getScore() <= k) {
                    a(icommandlistener, (ICommand) this, "commands.scoreboard.players.test.success", new Object[] { Integer.valueOf(scoreboardscore.getScore()), Integer.valueOf(j), Integer.valueOf(k)});
                } else {
                    throw new CommandException("commands.scoreboard.players.test.failed", new Object[] { Integer.valueOf(scoreboardscore.getScore()), Integer.valueOf(j), Integer.valueOf(k)});
                }
            }
        }
    }

    protected void o(ICommandListener icommandlistener, String[] astring, int i, MinecraftServer minecraftserver) throws CommandException {
        Scoreboard scoreboard = this.a(minecraftserver);
        String s = f(minecraftserver, icommandlistener, astring[i++]);
        ScoreboardObjective scoreboardobjective = this.a(astring[i++], true, minecraftserver);
        String s1 = astring[i++];
        String s2 = f(minecraftserver, icommandlistener, astring[i++]);
        ScoreboardObjective scoreboardobjective1 = this.a(astring[i], false, minecraftserver);

        if (s.length() > 40) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", new Object[] { s, Integer.valueOf(40)});
        } else if (s2.length() > 40) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", new Object[] { s2, Integer.valueOf(40)});
        } else {
            ScoreboardScore scoreboardscore = scoreboard.getPlayerScoreForObjective(s, scoreboardobjective);

            if (!scoreboard.b(s2, scoreboardobjective1)) {
                throw new CommandException("commands.scoreboard.players.operation.notFound", new Object[] { scoreboardobjective1.getName(), s2});
            } else {
                ScoreboardScore scoreboardscore1 = scoreboard.getPlayerScoreForObjective(s2, scoreboardobjective1);

                if ("+=".equals(s1)) {
                    scoreboardscore.setScore(scoreboardscore.getScore() + scoreboardscore1.getScore());
                } else if ("-=".equals(s1)) {
                    scoreboardscore.setScore(scoreboardscore.getScore() - scoreboardscore1.getScore());
                } else if ("*=".equals(s1)) {
                    scoreboardscore.setScore(scoreboardscore.getScore() * scoreboardscore1.getScore());
                } else if ("/=".equals(s1)) {
                    if (scoreboardscore1.getScore() != 0) {
                        scoreboardscore.setScore(scoreboardscore.getScore() / scoreboardscore1.getScore());
                    }
                } else if ("%=".equals(s1)) {
                    if (scoreboardscore1.getScore() != 0) {
                        scoreboardscore.setScore(scoreboardscore.getScore() % scoreboardscore1.getScore());
                    }
                } else if ("=".equals(s1)) {
                    scoreboardscore.setScore(scoreboardscore1.getScore());
                } else if ("<".equals(s1)) {
                    scoreboardscore.setScore(Math.min(scoreboardscore.getScore(), scoreboardscore1.getScore()));
                } else if (">".equals(s1)) {
                    scoreboardscore.setScore(Math.max(scoreboardscore.getScore(), scoreboardscore1.getScore()));
                } else {
                    if (!"><".equals(s1)) {
                        throw new CommandException("commands.scoreboard.players.operation.invalidOperation", new Object[] { s1});
                    }

                    int j = scoreboardscore.getScore();

                    scoreboardscore.setScore(scoreboardscore1.getScore());
                    scoreboardscore1.setScore(j);
                }

                a(icommandlistener, (ICommand) this, "commands.scoreboard.players.operation.success", new Object[0]);
            }
        }
    }

    protected void a(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        String s = f(minecraftserver, icommandlistener, astring[i]);
        Entity entity = c(minecraftserver, icommandlistener, astring[i++]);
        String s1 = astring[i++];
        Set set = entity.getScoreboardTags();

        if ("list".equals(s1)) {
            if (!set.isEmpty()) {
                ChatMessage chatmessage = new ChatMessage("commands.scoreboard.players.tag.list", new Object[] { s});

                chatmessage.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
                icommandlistener.sendMessage(chatmessage);
                icommandlistener.sendMessage(new ChatComponentText(a(set.toArray())));
            }

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, set.size());
        } else if (astring.length < 5) {
            throw new ExceptionUsage("commands.scoreboard.players.tag.usage", new Object[0]);
        } else {
            String s2 = astring[i++];

            if (astring.length > i) {
                try {
                    NBTTagCompound nbttagcompound = MojangsonParser.parse(a(astring, i));
                    NBTTagCompound nbttagcompound1 = a(entity);

                    if (!GameProfileSerializer.a(nbttagcompound, nbttagcompound1, true)) {
                        throw new CommandException("commands.scoreboard.players.tag.tagMismatch", new Object[] { s});
                    }
                } catch (MojangsonParseException mojangsonparseexception) {
                    throw new CommandException("commands.scoreboard.players.tag.tagError", new Object[] { mojangsonparseexception.getMessage()});
                }
            }

            if ("add".equals(s1)) {
                if (!entity.addScoreboardTag(s2)) {
                    throw new CommandException("commands.scoreboard.players.tag.tooMany", new Object[] { Integer.valueOf(1024)});
                }

                a(icommandlistener, (ICommand) this, "commands.scoreboard.players.tag.success.add", new Object[] { s2});
            } else {
                if (!"remove".equals(s1)) {
                    throw new ExceptionUsage("commands.scoreboard.players.tag.usage", new Object[0]);
                }

                if (!entity.removeScoreboardTag(s2)) {
                    throw new CommandException("commands.scoreboard.players.tag.notFound", new Object[] { s2});
                }

                a(icommandlistener, (ICommand) this, "commands.scoreboard.players.tag.success.remove", new Object[] { s2});
            }

        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        if (astring.length == 1) {
            return a(astring, new String[] { "objectives", "players", "teams"});
        } else {
            if ("objectives".equalsIgnoreCase(astring[0])) {
                if (astring.length == 2) {
                    return a(astring, new String[] { "list", "add", "remove", "setdisplay"});
                }

                if ("add".equalsIgnoreCase(astring[1])) {
                    if (astring.length == 4) {
                        Set set = IScoreboardCriteria.criteria.keySet();

                        return a(astring, (Collection) set);
                    }
                } else if ("remove".equalsIgnoreCase(astring[1])) {
                    if (astring.length == 3) {
                        return a(astring, (Collection) this.a(false, minecraftserver));
                    }
                } else if ("setdisplay".equalsIgnoreCase(astring[1])) {
                    if (astring.length == 3) {
                        return a(astring, Scoreboard.h());
                    }

                    if (astring.length == 4) {
                        return a(astring, (Collection) this.a(false, minecraftserver));
                    }
                }
            } else if ("players".equalsIgnoreCase(astring[0])) {
                if (astring.length == 2) {
                    return a(astring, new String[] { "set", "add", "remove", "reset", "list", "enable", "test", "operation", "tag"});
                }

                if (!"set".equalsIgnoreCase(astring[1]) && !"add".equalsIgnoreCase(astring[1]) && !"remove".equalsIgnoreCase(astring[1]) && !"reset".equalsIgnoreCase(astring[1])) {
                    if ("enable".equalsIgnoreCase(astring[1])) {
                        if (astring.length == 3) {
                            return a(astring, minecraftserver.getPlayers());
                        }

                        if (astring.length == 4) {
                            return a(astring, (Collection) this.b(minecraftserver));
                        }
                    } else if (!"list".equalsIgnoreCase(astring[1]) && !"test".equalsIgnoreCase(astring[1])) {
                        if ("operation".equalsIgnoreCase(astring[1])) {
                            if (astring.length == 3) {
                                return a(astring, this.a(minecraftserver).getPlayers());
                            }

                            if (astring.length == 4) {
                                return a(astring, (Collection) this.a(true, minecraftserver));
                            }

                            if (astring.length == 5) {
                                return a(astring, new String[] { "+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><"});
                            }

                            if (astring.length == 6) {
                                return a(astring, minecraftserver.getPlayers());
                            }

                            if (astring.length == 7) {
                                return a(astring, (Collection) this.a(false, minecraftserver));
                            }
                        } else if ("tag".equalsIgnoreCase(astring[1])) {
                            if (astring.length == 3) {
                                return a(astring, this.a(minecraftserver).getPlayers());
                            }

                            if (astring.length == 4) {
                                return a(astring, new String[] { "add", "remove", "list"});
                            }
                        }
                    } else {
                        if (astring.length == 3) {
                            return a(astring, this.a(minecraftserver).getPlayers());
                        }

                        if (astring.length == 4 && "test".equalsIgnoreCase(astring[1])) {
                            return a(astring, (Collection) this.a(false, minecraftserver));
                        }
                    }
                } else {
                    if (astring.length == 3) {
                        return a(astring, minecraftserver.getPlayers());
                    }

                    if (astring.length == 4) {
                        return a(astring, (Collection) this.a(true, minecraftserver));
                    }
                }
            } else if ("teams".equalsIgnoreCase(astring[0])) {
                if (astring.length == 2) {
                    return a(astring, new String[] { "add", "remove", "join", "leave", "empty", "list", "option"});
                }

                if ("join".equalsIgnoreCase(astring[1])) {
                    if (astring.length == 3) {
                        return a(astring, this.a(minecraftserver).getTeamNames());
                    }

                    if (astring.length >= 4) {
                        return a(astring, minecraftserver.getPlayers());
                    }
                } else {
                    if ("leave".equalsIgnoreCase(astring[1])) {
                        return a(astring, minecraftserver.getPlayers());
                    }

                    if (!"empty".equalsIgnoreCase(astring[1]) && !"list".equalsIgnoreCase(astring[1]) && !"remove".equalsIgnoreCase(astring[1])) {
                        if ("option".equalsIgnoreCase(astring[1])) {
                            if (astring.length == 3) {
                                return a(astring, this.a(minecraftserver).getTeamNames());
                            }

                            if (astring.length == 4) {
                                return a(astring, new String[] { "color", "friendlyfire", "seeFriendlyInvisibles", "nametagVisibility", "deathMessageVisibility", "collisionRule"});
                            }

                            if (astring.length == 5) {
                                if ("color".equalsIgnoreCase(astring[3])) {
                                    return a(astring, EnumChatFormat.a(true, false));
                                }

                                if ("nametagVisibility".equalsIgnoreCase(astring[3]) || "deathMessageVisibility".equalsIgnoreCase(astring[3])) {
                                    return a(astring, ScoreboardTeamBase.EnumNameTagVisibility.a());
                                }

                                if ("collisionRule".equalsIgnoreCase(astring[3])) {
                                    return a(astring, ScoreboardTeamBase.EnumTeamPush.a());
                                }

                                if ("friendlyfire".equalsIgnoreCase(astring[3]) || "seeFriendlyInvisibles".equalsIgnoreCase(astring[3])) {
                                    return a(astring, new String[] { "true", "false"});
                                }
                            }
                        }
                    } else if (astring.length == 3) {
                        return a(astring, this.a(minecraftserver).getTeamNames());
                    }
                }
            }

            return Collections.emptyList();
        }
    }

    protected List<String> a(boolean flag, MinecraftServer minecraftserver) {
        Collection collection = this.a(minecraftserver).getObjectives();
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

            if (!flag || !scoreboardobjective.getCriteria().isReadOnly()) {
                arraylist.add(scoreboardobjective.getName());
            }
        }

        return arraylist;
    }

    protected List<String> b(MinecraftServer minecraftserver) {
        Collection collection = this.a(minecraftserver).getObjectives();
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

            if (scoreboardobjective.getCriteria() == IScoreboardCriteria.c) {
                arraylist.add(scoreboardobjective.getName());
            }
        }

        return arraylist;
    }

    public boolean isListStart(String[] astring, int i) {
        return !"players".equalsIgnoreCase(astring[0]) ? ("teams".equalsIgnoreCase(astring[0]) ? i == 2 : false) : (astring.length > 1 && "operation".equalsIgnoreCase(astring[1]) ? i == 2 || i == 5 : i == 2);
    }
}
