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
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.World;

public class ArgumentDimension implements ArgumentType<MinecraftKey> {

    private static final Collection<String> a = (Collection) Stream.of(World.OVERWORLD, World.THE_NETHER).map((resourcekey) -> {
        return resourcekey.a().toString();
    }).collect(Collectors.toList());
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.dimension.invalid", new Object[]{object});
    });

    public ArgumentDimension() {}

    public MinecraftKey parse(StringReader stringreader) throws CommandSyntaxException {
        return MinecraftKey.a(stringreader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return commandcontext.getSource() instanceof ICompletionProvider ? ICompletionProvider.a(((ICompletionProvider) commandcontext.getSource()).p().stream().map(ResourceKey::a), suggestionsbuilder) : Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return ArgumentDimension.a;
    }

    public static ArgumentDimension a() {
        return new ArgumentDimension();
    }

    public static WorldServer a(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        MinecraftKey minecraftkey = (MinecraftKey) commandcontext.getArgument(s, MinecraftKey.class);
        ResourceKey<World> resourcekey = ResourceKey.a(IRegistry.L, minecraftkey);
        WorldServer worldserver = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getWorldServer(resourcekey);

        if (worldserver == null) {
            throw ArgumentDimension.b.create(minecraftkey);
        } else {
            return worldserver;
        }
    }
}
