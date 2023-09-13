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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.Particle;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;

public class ArgumentParticle implements ArgumentType<ParticleParam> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("particle.notFound", object);
    });
    private final HolderLookup<Particle<?>> particles;

    public ArgumentParticle(CommandBuildContext commandbuildcontext) {
        this.particles = commandbuildcontext.holderLookup(Registries.PARTICLE_TYPE);
    }

    public static ArgumentParticle particle(CommandBuildContext commandbuildcontext) {
        return new ArgumentParticle(commandbuildcontext);
    }

    public static ParticleParam getParticle(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (ParticleParam) commandcontext.getArgument(s, ParticleParam.class);
    }

    public ParticleParam parse(StringReader stringreader) throws CommandSyntaxException {
        return readParticle(stringreader, this.particles);
    }

    public Collection<String> getExamples() {
        return ArgumentParticle.EXAMPLES;
    }

    public static ParticleParam readParticle(StringReader stringreader, HolderLookup<Particle<?>> holderlookup) throws CommandSyntaxException {
        Particle<?> particle = readParticleType(stringreader, holderlookup);

        return readParticle(stringreader, particle);
    }

    private static Particle<?> readParticleType(StringReader stringreader, HolderLookup<Particle<?>> holderlookup) throws CommandSyntaxException {
        MinecraftKey minecraftkey = MinecraftKey.read(stringreader);
        ResourceKey<Particle<?>> resourcekey = ResourceKey.create(Registries.PARTICLE_TYPE, minecraftkey);

        return (Particle) ((Holder.c) holderlookup.get(resourcekey).orElseThrow(() -> {
            return ArgumentParticle.ERROR_UNKNOWN_PARTICLE.create(minecraftkey);
        })).value();
    }

    private static <T extends ParticleParam> T readParticle(StringReader stringreader, Particle<T> particle) throws CommandSyntaxException {
        return particle.getDeserializer().fromCommand(particle, stringreader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggestResource(this.particles.listElementIds().map(ResourceKey::location), suggestionsbuilder);
    }
}
