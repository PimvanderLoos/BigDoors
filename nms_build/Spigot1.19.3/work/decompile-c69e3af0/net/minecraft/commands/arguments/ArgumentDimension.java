package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.World;

public class ArgumentDimension implements ArgumentType<MinecraftKey> {

    private static final Collection<String> EXAMPLES = (Collection) Stream.of(World.OVERWORLD, World.NETHER).map((resourcekey) -> {
        return resourcekey.location().toString();
    }).collect(Collectors.toList());
    private static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("argument.dimension.invalid", object);
    });

    public ArgumentDimension() {}

    public MinecraftKey parse(StringReader stringreader) throws CommandSyntaxException {
        return MinecraftKey.read(stringreader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return commandcontext.getSource() instanceof ICompletionProvider ? ICompletionProvider.suggestResource(((ICompletionProvider) commandcontext.getSource()).levels().stream().map(ResourceKey::location), suggestionsbuilder) : Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return ArgumentDimension.EXAMPLES;
    }

    public static ArgumentDimension dimension() {
        return new ArgumentDimension();
    }

    public static WorldServer getDimension(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);
        ResourceKey<World> resourcekey = ResourceKey.create(Registries.DIMENSION, minecraftkey);
        WorldServer worldserver = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getLevel(resourcekey);

        if (worldserver == null) {
            throw ArgumentDimension.ERROR_INVALID_VALUE.create(minecraftkey);
        } else {
            return worldserver;
        }
    }
}
