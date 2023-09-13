package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.selector.ArgumentParserSelector;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.world.entity.Entity;

public class ArgumentScoreholder implements ArgumentType<ArgumentScoreholder.a> {

    public static final SuggestionProvider<CommandListenerWrapper> SUGGEST_SCORE_HOLDERS = (commandcontext, suggestionsbuilder) -> {
        StringReader stringreader = new StringReader(suggestionsbuilder.getInput());

        stringreader.setCursor(suggestionsbuilder.getStart());
        ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(stringreader);

        try {
            argumentparserselector.parse();
        } catch (CommandSyntaxException commandsyntaxexception) {
            ;
        }

        return argumentparserselector.fillSuggestions(suggestionsbuilder, (suggestionsbuilder1) -> {
            ICompletionProvider.suggest((Iterable) ((CommandListenerWrapper) commandcontext.getSource()).getOnlinePlayerNames(), suggestionsbuilder1);
        });
    };
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
    private static final SimpleCommandExceptionType ERROR_NO_RESULTS = new SimpleCommandExceptionType(new ChatMessage("argument.scoreHolder.empty"));
    private static final byte FLAG_MULTIPLE = 1;
    final boolean multiple;

    public ArgumentScoreholder(boolean flag) {
        this.multiple = flag;
    }

    public static String getName(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return (String) getNames(commandcontext, s).iterator().next();
    }

    public static Collection<String> getNames(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return getNames(commandcontext, s, Collections::emptyList);
    }

    public static Collection<String> getNamesWithDefaultWildcard(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        ScoreboardServer scoreboardserver = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getScoreboard();

        Objects.requireNonNull(scoreboardserver);
        return getNames(commandcontext, s, scoreboardserver::getTrackedPlayers);
    }

    public static Collection<String> getNames(CommandContext<CommandListenerWrapper> commandcontext, String s, Supplier<Collection<String>> supplier) throws CommandSyntaxException {
        Collection<String> collection = ((ArgumentScoreholder.a) commandcontext.getArgument(s, ArgumentScoreholder.a.class)).getNames((CommandListenerWrapper) commandcontext.getSource(), supplier);

        if (collection.isEmpty()) {
            throw ArgumentEntity.NO_ENTITIES_FOUND.create();
        } else {
            return collection;
        }
    }

    public static ArgumentScoreholder scoreHolder() {
        return new ArgumentScoreholder(false);
    }

    public static ArgumentScoreholder scoreHolders() {
        return new ArgumentScoreholder(true);
    }

    public ArgumentScoreholder.a parse(StringReader stringreader) throws CommandSyntaxException {
        if (stringreader.canRead() && stringreader.peek() == '@') {
            ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(stringreader);
            EntitySelector entityselector = argumentparserselector.parse();

            if (!this.multiple && entityselector.getMaxResults() > 1) {
                throw ArgumentEntity.ERROR_NOT_SINGLE_ENTITY.create();
            } else {
                return new ArgumentScoreholder.b(entityselector);
            }
        } else {
            int i = stringreader.getCursor();

            while (stringreader.canRead() && stringreader.peek() != ' ') {
                stringreader.skip();
            }

            String s = stringreader.getString().substring(i, stringreader.getCursor());

            if (s.equals("*")) {
                return (commandlistenerwrapper, supplier) -> {
                    Collection<String> collection = (Collection) supplier.get();

                    if (collection.isEmpty()) {
                        throw ArgumentScoreholder.ERROR_NO_RESULTS.create();
                    } else {
                        return collection;
                    }
                };
            } else {
                Collection<String> collection = Collections.singleton(s);

                return (commandlistenerwrapper, supplier) -> {
                    return collection;
                };
            }
        }
    }

    public Collection<String> getExamples() {
        return ArgumentScoreholder.EXAMPLES;
    }

    @FunctionalInterface
    public interface a {

        Collection<String> getNames(CommandListenerWrapper commandlistenerwrapper, Supplier<Collection<String>> supplier) throws CommandSyntaxException;
    }

    public static class b implements ArgumentScoreholder.a {

        private final EntitySelector selector;

        public b(EntitySelector entityselector) {
            this.selector = entityselector;
        }

        @Override
        public Collection<String> getNames(CommandListenerWrapper commandlistenerwrapper, Supplier<Collection<String>> supplier) throws CommandSyntaxException {
            List<? extends Entity> list = this.selector.findEntities(commandlistenerwrapper);

            if (list.isEmpty()) {
                throw ArgumentEntity.NO_ENTITIES_FOUND.create();
            } else {
                List<String> list1 = Lists.newArrayList();
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    list1.add(entity.getScoreboardName());
                }

                return list1;
            }
        }
    }

    public static class c implements ArgumentSerializer<ArgumentScoreholder> {

        public c() {}

        public void serializeToNetwork(ArgumentScoreholder argumentscoreholder, PacketDataSerializer packetdataserializer) {
            byte b0 = 0;

            if (argumentscoreholder.multiple) {
                b0 = (byte) (b0 | 1);
            }

            packetdataserializer.writeByte(b0);
        }

        @Override
        public ArgumentScoreholder deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
            byte b0 = packetdataserializer.readByte();
            boolean flag = (b0 & 1) != 0;

            return new ArgumentScoreholder(flag);
        }

        public void serializeToJson(ArgumentScoreholder argumentscoreholder, JsonObject jsonobject) {
            jsonobject.addProperty("amount", argumentscoreholder.multiple ? "multiple" : "single");
        }
    }
}
