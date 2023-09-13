package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class CommandAdvancement extends CommandAbstract {

    public CommandAdvancement() {}

    public String getCommand() {
        return "advancement";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.advancement.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 1) {
            throw new ExceptionUsage("commands.advancement.usage", new Object[0]);
        } else {
            CommandAdvancement.Action commandadvancement_action = CommandAdvancement.Action.a(astring[0]);

            if (commandadvancement_action != null) {
                if (astring.length < 3) {
                    throw commandadvancement_action.a();
                }

                EntityPlayer entityplayer = b(minecraftserver, icommandlistener, astring[1]);
                CommandAdvancement.Filter commandadvancement_filter = CommandAdvancement.Filter.a(astring[2]);

                if (commandadvancement_filter == null) {
                    throw commandadvancement_action.a();
                }

                this.a(minecraftserver, icommandlistener, astring, entityplayer, commandadvancement_action, commandadvancement_filter);
            } else {
                if (!"test".equals(astring[0])) {
                    throw new ExceptionUsage("commands.advancement.usage", new Object[0]);
                }

                if (astring.length == 3) {
                    this.a(icommandlistener, b(minecraftserver, icommandlistener, astring[1]), a(minecraftserver, astring[2]));
                } else {
                    if (astring.length != 4) {
                        throw new ExceptionUsage("commands.advancement.test.usage", new Object[0]);
                    }

                    this.a(icommandlistener, b(minecraftserver, icommandlistener, astring[1]), a(minecraftserver, astring[2]), astring[3]);
                }
            }

        }
    }

    private void a(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, EntityPlayer entityplayer, CommandAdvancement.Action commandadvancement_action, CommandAdvancement.Filter commandadvancement_filter) throws CommandException {
        if (commandadvancement_filter == CommandAdvancement.Filter.EVERYTHING) {
            if (astring.length == 3) {
                int i = commandadvancement_action.a(entityplayer, minecraftserver.getAdvancementData().c());

                if (i == 0) {
                    throw commandadvancement_filter.a(commandadvancement_action, new Object[] { entityplayer.getName()});
                } else {
                    commandadvancement_filter.a(icommandlistener, this, commandadvancement_action, new Object[] { entityplayer.getName(), Integer.valueOf(i)});
                }
            } else {
                throw commandadvancement_filter.a(commandadvancement_action);
            }
        } else if (astring.length < 4) {
            throw commandadvancement_filter.a(commandadvancement_action);
        } else {
            Advancement advancement = a(minecraftserver, astring[3]);

            if (commandadvancement_filter == CommandAdvancement.Filter.ONLY && astring.length == 5) {
                String s = astring[4];

                if (!advancement.getCriteria().keySet().contains(s)) {
                    throw new CommandException("commands.advancement.criterionNotFound", new Object[] { advancement.getName(), astring[4]});
                }

                if (!commandadvancement_action.a(entityplayer, advancement, s)) {
                    throw new CommandException(commandadvancement_action.d + ".criterion.failed", new Object[] { advancement.getName(), entityplayer.getName(), s});
                }

                a(icommandlistener, (ICommand) this, commandadvancement_action.d + ".criterion.success", new Object[] { advancement.getName(), entityplayer.getName(), s});
            } else {
                if (astring.length != 4) {
                    throw commandadvancement_filter.a(commandadvancement_action);
                }

                List list = this.a(advancement, commandadvancement_filter);
                int j = commandadvancement_action.a(entityplayer, (Iterable) list);

                if (j == 0) {
                    throw commandadvancement_filter.a(commandadvancement_action, new Object[] { advancement.getName(), entityplayer.getName()});
                }

                commandadvancement_filter.a(icommandlistener, this, commandadvancement_action, new Object[] { advancement.getName(), entityplayer.getName(), Integer.valueOf(j)});
            }

        }
    }

    private void a(Advancement advancement, List<Advancement> list) {
        Iterator iterator = advancement.e().iterator();

        while (iterator.hasNext()) {
            Advancement advancement1 = (Advancement) iterator.next();

            list.add(advancement1);
            this.a(advancement1, list);
        }

    }

    private List<Advancement> a(Advancement advancement, CommandAdvancement.Filter commandadvancement_filter) {
        ArrayList arraylist = Lists.newArrayList();

        if (commandadvancement_filter.h) {
            for (Advancement advancement1 = advancement.b(); advancement1 != null; advancement1 = advancement1.b()) {
                arraylist.add(advancement1);
            }
        }

        arraylist.add(advancement);
        if (commandadvancement_filter.i) {
            this.a(advancement, (List) arraylist);
        }

        return arraylist;
    }

    private void a(ICommandListener icommandlistener, EntityPlayer entityplayer, Advancement advancement, String s) throws CommandException {
        AdvancementDataPlayer advancementdataplayer = entityplayer.getAdvancementData();
        CriterionProgress criterionprogress = advancementdataplayer.getProgress(advancement).getCriterionProgress(s);

        if (criterionprogress == null) {
            throw new CommandException("commands.advancement.criterionNotFound", new Object[] { advancement.getName(), s});
        } else if (!criterionprogress.a()) {
            throw new CommandException("commands.advancement.test.criterion.notDone", new Object[] { entityplayer.getName(), advancement.getName(), s});
        } else {
            a(icommandlistener, (ICommand) this, "commands.advancement.test.criterion.success", new Object[] { entityplayer.getName(), advancement.getName(), s});
        }
    }

    private void a(ICommandListener icommandlistener, EntityPlayer entityplayer, Advancement advancement) throws CommandException {
        AdvancementProgress advancementprogress = entityplayer.getAdvancementData().getProgress(advancement);

        if (!advancementprogress.isDone()) {
            throw new CommandException("commands.advancement.test.advancement.notDone", new Object[] { entityplayer.getName(), advancement.getName()});
        } else {
            a(icommandlistener, (ICommand) this, "commands.advancement.test.advancement.success", new Object[] { entityplayer.getName(), advancement.getName()});
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        if (astring.length == 1) {
            return a(astring, new String[] { "grant", "revoke", "test"});
        } else {
            CommandAdvancement.Action commandadvancement_action = CommandAdvancement.Action.a(astring[0]);

            if (commandadvancement_action != null) {
                if (astring.length == 2) {
                    return a(astring, minecraftserver.getPlayers());
                }

                if (astring.length == 3) {
                    return a(astring, CommandAdvancement.Filter.f);
                }

                CommandAdvancement.Filter commandadvancement_filter = CommandAdvancement.Filter.a(astring[2]);

                if (commandadvancement_filter != null && commandadvancement_filter != CommandAdvancement.Filter.EVERYTHING) {
                    if (astring.length == 4) {
                        return a(astring, (Collection) this.a(minecraftserver));
                    }

                    if (astring.length == 5 && commandadvancement_filter == CommandAdvancement.Filter.ONLY) {
                        Advancement advancement = minecraftserver.getAdvancementData().a(new MinecraftKey(astring[3]));

                        if (advancement != null) {
                            return a(astring, (Collection) advancement.getCriteria().keySet());
                        }
                    }
                }
            }

            if ("test".equals(astring[0])) {
                if (astring.length == 2) {
                    return a(astring, minecraftserver.getPlayers());
                }

                if (astring.length == 3) {
                    return a(astring, (Collection) this.a(minecraftserver));
                }

                if (astring.length == 4) {
                    Advancement advancement1 = minecraftserver.getAdvancementData().a(new MinecraftKey(astring[2]));

                    if (advancement1 != null) {
                        return a(astring, (Collection) advancement1.getCriteria().keySet());
                    }
                }
            }

            return Collections.emptyList();
        }
    }

    private List<MinecraftKey> a(MinecraftServer minecraftserver) {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = minecraftserver.getAdvancementData().c().iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            arraylist.add(advancement.getName());
        }

        return arraylist;
    }

    public boolean isListStart(String[] astring, int i) {
        return astring.length > 1 && ("grant".equals(astring[0]) || "revoke".equals(astring[0]) || "test".equals(astring[0])) && i == 1;
    }

    public static Advancement a(MinecraftServer minecraftserver, String s) throws CommandException {
        Advancement advancement = minecraftserver.getAdvancementData().a(new MinecraftKey(s));

        if (advancement == null) {
            throw new CommandException("commands.advancement.advancementNotFound", new Object[] { s});
        } else {
            return advancement;
        }
    }

    static enum Filter {

        ONLY("only", false, false), THROUGH("through", true, true), FROM("from", false, true), UNTIL("until", true, false), EVERYTHING("everything", true, true);

        static final String[] f = new String[values().length];
        final String g;
        final boolean h;
        final boolean i;

        private Filter(String s, boolean flag, boolean flag1) {
            this.g = s;
            this.h = flag;
            this.i = flag1;
        }

        CommandException a(CommandAdvancement.Action commandadvancement_action, Object... aobject) {
            return new CommandException(commandadvancement_action.d + "." + this.g + ".failed", aobject);
        }

        CommandException a(CommandAdvancement.Action commandadvancement_action) {
            return new CommandException(commandadvancement_action.d + "." + this.g + ".usage", new Object[0]);
        }

        void a(ICommandListener icommandlistener, CommandAdvancement commandadvancement, CommandAdvancement.Action commandadvancement_action, Object... aobject) {
            CommandAbstract.a(icommandlistener, (ICommand) commandadvancement, commandadvancement_action.d + "." + this.g + ".success", aobject);
        }

        @Nullable
        static CommandAdvancement.Filter a(String s) {
            CommandAdvancement.Filter[] acommandadvancement_filter = values();
            int i = acommandadvancement_filter.length;

            for (int j = 0; j < i; ++j) {
                CommandAdvancement.Filter commandadvancement_filter = acommandadvancement_filter[j];

                if (commandadvancement_filter.g.equals(s)) {
                    return commandadvancement_filter;
                }
            }

            return null;
        }

        static {
            for (int i = 0; i < values().length; ++i) {
                CommandAdvancement.Filter.f[i] = values()[i].g;
            }

        }
    }

    static enum Action {

        GRANT("grant") {;
            protected boolean a(EntityPlayer entityplayer, Advancement advancement) {
                AdvancementProgress advancementprogress = entityplayer.getAdvancementData().getProgress(advancement);

                if (advancementprogress.isDone()) {
                    return false;
                } else {
                    Iterator iterator = advancementprogress.getRemainingCriteria().iterator();

                    while (iterator.hasNext()) {
                        String s = (String) iterator.next();

                        entityplayer.getAdvancementData().grantCriteria(advancement, s);
                    }

                    return true;
                }
            }

            protected boolean a(EntityPlayer entityplayer, Advancement advancement, String s) {
                return entityplayer.getAdvancementData().grantCriteria(advancement, s);
            }
        }, REVOKE("revoke") {;
    protected boolean a(EntityPlayer entityplayer, Advancement advancement) {
        AdvancementProgress advancementprogress = entityplayer.getAdvancementData().getProgress(advancement);

        if (!advancementprogress.b()) {
            return false;
        } else {
            Iterator iterator = advancementprogress.getAwardedCriteria().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                entityplayer.getAdvancementData().revokeCritera(advancement, s);
            }

            return true;
        }
    }

    protected boolean a(EntityPlayer entityplayer, Advancement advancement, String s) {
        return entityplayer.getAdvancementData().revokeCritera(advancement, s);
    }
};

        final String c;
        final String d;

        private Action(String s) {
            this.c = s;
            this.d = "commands.advancement." + s;
        }

        @Nullable
        static CommandAdvancement.Action a(String s) {
            CommandAdvancement.Action[] acommandadvancement_action = values();
            int i = acommandadvancement_action.length;

            for (int j = 0; j < i; ++j) {
                CommandAdvancement.Action commandadvancement_action = acommandadvancement_action[j];

                if (commandadvancement_action.c.equals(s)) {
                    return commandadvancement_action;
                }
            }

            return null;
        }

        CommandException a() {
            return new CommandException(this.d + ".usage", new Object[0]);
        }

        public int a(EntityPlayer entityplayer, Iterable<Advancement> iterable) {
            int i = 0;
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Advancement advancement = (Advancement) iterator.next();

                if (this.a(entityplayer, advancement)) {
                    ++i;
                }
            }

            return i;
        }

        protected abstract boolean a(EntityPlayer entityplayer, Advancement advancement);

        protected abstract boolean a(EntityPlayer entityplayer, Advancement advancement, String s);

        Action(String s, Object object) {
            this(s);
        }
    }
}
