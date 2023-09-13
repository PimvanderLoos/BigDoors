package net.minecraft.commands.arguments;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.selector.ArgumentParserSelector;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;

public class ArgumentEntity implements ArgumentType<EntitySelector> {

    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.entity.toomany"));
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.player.toomany"));
    public static final SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.player.entities"));
    public static final SimpleCommandExceptionType NO_ENTITIES_FOUND = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.entity.notfound.entity"));
    public static final SimpleCommandExceptionType NO_PLAYERS_FOUND = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.entity.notfound.player"));
    public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.entity.selector.not_allowed"));
    final boolean single;
    final boolean playersOnly;

    protected ArgumentEntity(boolean flag, boolean flag1) {
        this.single = flag;
        this.playersOnly = flag1;
    }

    public static ArgumentEntity entity() {
        return new ArgumentEntity(true, false);
    }

    public static Entity getEntity(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((EntitySelector) commandcontext.getArgument(s, EntitySelector.class)).findSingleEntity((CommandListenerWrapper) commandcontext.getSource());
    }

    public static ArgumentEntity entities() {
        return new ArgumentEntity(false, false);
    }

    public static Collection<? extends Entity> getEntities(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        Collection<? extends Entity> collection = getOptionalEntities(commandcontext, s);

        if (collection.isEmpty()) {
            throw ArgumentEntity.NO_ENTITIES_FOUND.create();
        } else {
            return collection;
        }
    }

    public static Collection<? extends Entity> getOptionalEntities(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((EntitySelector) commandcontext.getArgument(s, EntitySelector.class)).findEntities((CommandListenerWrapper) commandcontext.getSource());
    }

    public static Collection<EntityPlayer> getOptionalPlayers(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((EntitySelector) commandcontext.getArgument(s, EntitySelector.class)).findPlayers((CommandListenerWrapper) commandcontext.getSource());
    }

    public static ArgumentEntity player() {
        return new ArgumentEntity(true, true);
    }

    public static EntityPlayer getPlayer(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((EntitySelector) commandcontext.getArgument(s, EntitySelector.class)).findSinglePlayer((CommandListenerWrapper) commandcontext.getSource());
    }

    public static ArgumentEntity players() {
        return new ArgumentEntity(false, true);
    }

    public static Collection<EntityPlayer> getPlayers(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        List<EntityPlayer> list = ((EntitySelector) commandcontext.getArgument(s, EntitySelector.class)).findPlayers((CommandListenerWrapper) commandcontext.getSource());

        if (list.isEmpty()) {
            throw ArgumentEntity.NO_PLAYERS_FOUND.create();
        } else {
            return list;
        }
    }

    public EntitySelector parse(StringReader stringreader) throws CommandSyntaxException {
        boolean flag = false;
        ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(stringreader);
        EntitySelector entityselector = argumentparserselector.parse();

        if (entityselector.getMaxResults() > 1 && this.single) {
            if (this.playersOnly) {
                stringreader.setCursor(0);
                throw ArgumentEntity.ERROR_NOT_SINGLE_PLAYER.createWithContext(stringreader);
            } else {
                stringreader.setCursor(0);
                throw ArgumentEntity.ERROR_NOT_SINGLE_ENTITY.createWithContext(stringreader);
            }
        } else if (entityselector.includesEntities() && this.playersOnly && !entityselector.isSelfSelector()) {
            stringreader.setCursor(0);
            throw ArgumentEntity.ERROR_ONLY_PLAYERS_ALLOWED.createWithContext(stringreader);
        } else {
            return entityselector;
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        Object object = commandcontext.getSource();

        if (object instanceof ICompletionProvider) {
            ICompletionProvider icompletionprovider = (ICompletionProvider) object;
            StringReader stringreader = new StringReader(suggestionsbuilder.getInput());

            stringreader.setCursor(suggestionsbuilder.getStart());
            ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(stringreader, icompletionprovider.hasPermission(2));

            try {
                argumentparserselector.parse();
            } catch (CommandSyntaxException commandsyntaxexception) {
                ;
            }

            return argumentparserselector.fillSuggestions(suggestionsbuilder, (suggestionsbuilder1) -> {
                Collection<String> collection = icompletionprovider.getOnlinePlayerNames();
                Iterable<String> iterable = this.playersOnly ? collection : Iterables.concat(collection, icompletionprovider.getSelectedEntities());

                ICompletionProvider.suggest((Iterable) iterable, suggestionsbuilder1);
            });
        } else {
            return Suggestions.empty();
        }
    }

    public Collection<String> getExamples() {
        return ArgumentEntity.EXAMPLES;
    }

    public static class Info implements ArgumentTypeInfo<ArgumentEntity, ArgumentEntity.Info.Template> {

        private static final byte FLAG_SINGLE = 1;
        private static final byte FLAG_PLAYERS_ONLY = 2;

        public Info() {}

        public void serializeToNetwork(ArgumentEntity.Info.Template argumententity_info_template, PacketDataSerializer packetdataserializer) {
            int i = 0;

            if (argumententity_info_template.single) {
                i |= 1;
            }

            if (argumententity_info_template.playersOnly) {
                i |= 2;
            }

            packetdataserializer.writeByte(i);
        }

        @Override
        public ArgumentEntity.Info.Template deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
            byte b0 = packetdataserializer.readByte();

            return new ArgumentEntity.Info.Template((b0 & 1) != 0, (b0 & 2) != 0);
        }

        public void serializeToJson(ArgumentEntity.Info.Template argumententity_info_template, JsonObject jsonobject) {
            jsonobject.addProperty("amount", argumententity_info_template.single ? "single" : "multiple");
            jsonobject.addProperty("type", argumententity_info_template.playersOnly ? "players" : "entities");
        }

        public ArgumentEntity.Info.Template unpack(ArgumentEntity argumententity) {
            return new ArgumentEntity.Info.Template(argumententity.single, argumententity.playersOnly);
        }

        public final class Template implements ArgumentTypeInfo.a<ArgumentEntity> {

            final boolean single;
            final boolean playersOnly;

            Template(boolean flag, boolean flag1) {
                this.single = flag;
                this.playersOnly = flag1;
            }

            @Override
            public ArgumentEntity instantiate(CommandBuildContext commandbuildcontext) {
                return new ArgumentEntity(this.single, this.playersOnly);
            }

            @Override
            public ArgumentTypeInfo<ArgumentEntity, ?> type() {
                return Info.this;
            }
        }
    }
}
