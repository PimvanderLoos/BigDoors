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
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.Particle;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;

public class ArgumentParticle implements ArgumentType<ParticleParam> {

    private static final Collection<String> b = Arrays.asList("foo", "foo:bar", "particle with options");
    public static final DynamicCommandExceptionType a = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("particle.notFound", new Object[]{object});
    });

    public ArgumentParticle() {}

    public static ArgumentParticle a() {
        return new ArgumentParticle();
    }

    public static ParticleParam a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (ParticleParam) commandcontext.getArgument(s, ParticleParam.class);
    }

    public ParticleParam parse(StringReader stringreader) throws CommandSyntaxException {
        return b(stringreader);
    }

    public Collection<String> getExamples() {
        return ArgumentParticle.b;
    }

    public static ParticleParam b(StringReader stringreader) throws CommandSyntaxException {
        MinecraftKey minecraftkey = MinecraftKey.a(stringreader);
        Particle<?> particle = (Particle) IRegistry.PARTICLE_TYPE.getOptional(minecraftkey).orElseThrow(() -> {
            return ArgumentParticle.a.create(minecraftkey);
        });

        return a(stringreader, particle);
    }

    private static <T extends ParticleParam> T a(StringReader stringreader, Particle<T> particle) throws CommandSyntaxException {
        return particle.d().b(particle, stringreader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.a((Iterable) IRegistry.PARTICLE_TYPE.keySet(), suggestionsbuilder);
    }
}
