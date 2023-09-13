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

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("particle.notFound", new Object[]{object});
    });

    public ArgumentParticle() {}

    public static ArgumentParticle particle() {
        return new ArgumentParticle();
    }

    public static ParticleParam getParticle(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (ParticleParam) commandcontext.getArgument(s, ParticleParam.class);
    }

    public ParticleParam parse(StringReader stringreader) throws CommandSyntaxException {
        return readParticle(stringreader);
    }

    public Collection<String> getExamples() {
        return ArgumentParticle.EXAMPLES;
    }

    public static ParticleParam readParticle(StringReader stringreader) throws CommandSyntaxException {
        MinecraftKey minecraftkey = MinecraftKey.read(stringreader);
        Particle<?> particle = (Particle) IRegistry.PARTICLE_TYPE.getOptional(minecraftkey).orElseThrow(() -> {
            return ArgumentParticle.ERROR_UNKNOWN_PARTICLE.create(minecraftkey);
        });

        return readParticle(stringreader, particle);
    }

    private static <T extends ParticleParam> T readParticle(StringReader stringreader, Particle<T> particle) throws CommandSyntaxException {
        return particle.getDeserializer().fromCommand(particle, stringreader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggestResource((Iterable) IRegistry.PARTICLE_TYPE.keySet(), suggestionsbuilder);
    }
}
