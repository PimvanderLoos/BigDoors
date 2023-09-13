package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.level.EnumGamemode;

public class GameModeArgument implements ArgumentType<EnumGamemode> {

    private static final Collection<String> EXAMPLES = (Collection) Stream.of(EnumGamemode.SURVIVAL, EnumGamemode.CREATIVE).map(EnumGamemode::getName).collect(Collectors.toList());
    private static final EnumGamemode[] VALUES = EnumGamemode.values();
    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("argument.gamemode.invalid", object);
    });

    public GameModeArgument() {}

    public EnumGamemode parse(StringReader stringreader) throws CommandSyntaxException {
        String s = stringreader.readUnquotedString();
        EnumGamemode enumgamemode = EnumGamemode.byName(s, (EnumGamemode) null);

        if (enumgamemode == null) {
            throw GameModeArgument.ERROR_INVALID.createWithContext(stringreader, s);
        } else {
            return enumgamemode;
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return commandcontext.getSource() instanceof ICompletionProvider ? ICompletionProvider.suggest(Arrays.stream(GameModeArgument.VALUES).map(EnumGamemode::getName), suggestionsbuilder) : Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return GameModeArgument.EXAMPLES;
    }

    public static GameModeArgument gameMode() {
        return new GameModeArgument();
    }

    public static EnumGamemode getGameMode(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return (EnumGamemode) commandcontext.getArgument(s, EnumGamemode.class);
    }
}
