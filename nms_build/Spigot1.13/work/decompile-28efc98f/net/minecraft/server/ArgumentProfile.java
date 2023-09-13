package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ArgumentProfile implements ArgumentType<ArgumentProfile.a> {

    private static final Collection<String> b = Arrays.asList(new String[] { "Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e"});
    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("argument.player.unknown", new Object[0]));

    public ArgumentProfile() {}

    public static Collection<GameProfile> a(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((ArgumentProfile.a) commandcontext.getArgument(s, ArgumentProfile.a.class)).getNames((CommandListenerWrapper) commandcontext.getSource());
    }

    public static ArgumentProfile a() {
        return new ArgumentProfile();
    }

    public ArgumentProfile.a a(StringReader stringreader) throws CommandSyntaxException {
        if (stringreader.canRead() && stringreader.peek() == 64) {
            ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(stringreader);
            EntitySelector entityselector = argumentparserselector.s();

            if (entityselector.b()) {
                throw ArgumentEntity.c.create();
            } else {
                return new ArgumentProfile.b(entityselector);
            }
        } else {
            int i = stringreader.getCursor();

            while (stringreader.canRead() && stringreader.peek() != 32) {
                stringreader.skip();
            }

            String s = stringreader.getString().substring(i, stringreader.getCursor());

            return (commandlistenerwrapper) -> {
                GameProfile gameprofile = commandlistenerwrapper.getServer().getUserCache().getProfile(s);

                if (gameprofile == null) {
                    throw ArgumentProfile.a.create();
                } else {
                    return Collections.singleton(gameprofile);
                }
            };
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        if (commandcontext.getSource() instanceof ICompletionProvider) {
            StringReader stringreader = new StringReader(suggestionsbuilder.getInput());

            stringreader.setCursor(suggestionsbuilder.getStart());
            ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(stringreader);

            try {
                argumentparserselector.s();
            } catch (CommandSyntaxException commandsyntaxexception) {
                ;
            }

            return argumentparserselector.a(suggestionsbuilder, (suggestionsbuilder) -> {
                ICompletionProvider.b(((ICompletionProvider) commandcontext.getSource()).l(), suggestionsbuilder);
            });
        } else {
            return Suggestions.empty();
        }
    }

    public Collection<String> getExamples() {
        return ArgumentProfile.b;
    }

    public Object parse(StringReader stringreader) throws CommandSyntaxException {
        return this.a(stringreader);
    }

    public static class b implements ArgumentProfile.a {

        private final EntitySelector a;

        public b(EntitySelector entityselector) {
            this.a = entityselector;
        }

        public Collection<GameProfile> getNames(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
            List list = this.a.d(commandlistenerwrapper);

            if (list.isEmpty()) {
                throw ArgumentEntity.e.create();
            } else {
                ArrayList arraylist = Lists.newArrayList();
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    arraylist.add(entityplayer.getProfile());
                }

                return arraylist;
            }
        }
    }

    @FunctionalInterface
    public interface a {

        Collection<GameProfile> getNames(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException;
    }
}
