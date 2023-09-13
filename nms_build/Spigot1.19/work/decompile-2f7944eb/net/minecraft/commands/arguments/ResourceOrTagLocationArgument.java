package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class ResourceOrTagLocationArgument<T> implements ArgumentType<ResourceOrTagLocationArgument.c<T>> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
    final ResourceKey<? extends IRegistry<T>> registryKey;

    public ResourceOrTagLocationArgument(ResourceKey<? extends IRegistry<T>> resourcekey) {
        this.registryKey = resourcekey;
    }

    public static <T> ResourceOrTagLocationArgument<T> resourceOrTag(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return new ResourceOrTagLocationArgument<>(resourcekey);
    }

    public static <T> ResourceOrTagLocationArgument.c<T> getRegistryType(CommandContext<CommandListenerWrapper> commandcontext, String s, ResourceKey<IRegistry<T>> resourcekey, DynamicCommandExceptionType dynamiccommandexceptiontype) throws CommandSyntaxException {
        ResourceOrTagLocationArgument.c<?> resourceortaglocationargument_c = (ResourceOrTagLocationArgument.c) commandcontext.getArgument(s, ResourceOrTagLocationArgument.c.class);
        Optional<ResourceOrTagLocationArgument.c<T>> optional = resourceortaglocationargument_c.cast(resourcekey);

        return (ResourceOrTagLocationArgument.c) optional.orElseThrow(() -> {
            return dynamiccommandexceptiontype.create(resourceortaglocationargument_c);
        });
    }

    public ResourceOrTagLocationArgument.c<T> parse(StringReader stringreader) throws CommandSyntaxException {
        if (stringreader.canRead() && stringreader.peek() == '#') {
            int i = stringreader.getCursor();

            try {
                stringreader.skip();
                MinecraftKey minecraftkey = MinecraftKey.read(stringreader);

                return new ResourceOrTagLocationArgument.d<>(TagKey.create(this.registryKey, minecraftkey));
            } catch (CommandSyntaxException commandsyntaxexception) {
                stringreader.setCursor(i);
                throw commandsyntaxexception;
            }
        } else {
            MinecraftKey minecraftkey1 = MinecraftKey.read(stringreader);

            return new ResourceOrTagLocationArgument.b<>(ResourceKey.create(this.registryKey, minecraftkey1));
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        Object object = commandcontext.getSource();

        if (object instanceof ICompletionProvider) {
            ICompletionProvider icompletionprovider = (ICompletionProvider) object;

            return icompletionprovider.suggestRegistryElements(this.registryKey, ICompletionProvider.a.ALL, suggestionsbuilder, commandcontext);
        } else {
            return suggestionsbuilder.buildFuture();
        }
    }

    public Collection<String> getExamples() {
        return ResourceOrTagLocationArgument.EXAMPLES;
    }

    public interface c<T> extends Predicate<Holder<T>> {

        Either<ResourceKey<T>, TagKey<T>> unwrap();

        <E> Optional<ResourceOrTagLocationArgument.c<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey);

        String asPrintable();
    }

    private static record d<T> (TagKey<T> key) implements ResourceOrTagLocationArgument.c<T> {

        @Override
        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.right(this.key);
        }

        @Override
        public <E> Optional<ResourceOrTagLocationArgument.c<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return this.key.cast(resourcekey).map(ResourceOrTagLocationArgument.d::new);
        }

        public boolean test(Holder<T> holder) {
            return holder.is(this.key);
        }

        @Override
        public String asPrintable() {
            return "#" + this.key.location();
        }
    }

    private static record b<T> (ResourceKey<T> key) implements ResourceOrTagLocationArgument.c<T> {

        @Override
        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.left(this.key);
        }

        @Override
        public <E> Optional<ResourceOrTagLocationArgument.c<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return this.key.cast(resourcekey).map(ResourceOrTagLocationArgument.b::new);
        }

        public boolean test(Holder<T> holder) {
            return holder.is(this.key);
        }

        @Override
        public String asPrintable() {
            return this.key.location().toString();
        }
    }

    public static class a<T> implements ArgumentTypeInfo<ResourceOrTagLocationArgument<T>, ResourceOrTagLocationArgument.a<T>.a> {

        public a() {}

        public void serializeToNetwork(ResourceOrTagLocationArgument.a<T>.a resourceortaglocationargument_a_a, PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeResourceLocation(resourceortaglocationargument_a_a.registryKey.location());
        }

        @Override
        public ResourceOrTagLocationArgument.a<T>.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
            MinecraftKey minecraftkey = packetdataserializer.readResourceLocation();

            return new ResourceOrTagLocationArgument.a.a(ResourceKey.createRegistryKey(minecraftkey));
        }

        public void serializeToJson(ResourceOrTagLocationArgument.a<T>.a resourceortaglocationargument_a_a, JsonObject jsonobject) {
            jsonobject.addProperty("registry", resourceortaglocationargument_a_a.registryKey.location().toString());
        }

        public ResourceOrTagLocationArgument.a<T>.a unpack(ResourceOrTagLocationArgument<T> resourceortaglocationargument) {
            return new ResourceOrTagLocationArgument.a.a(resourceortaglocationargument.registryKey);
        }

        public final class a implements ArgumentTypeInfo.a<ResourceOrTagLocationArgument<T>> {

            final ResourceKey<? extends IRegistry<T>> registryKey;

            a(ResourceKey resourcekey) {
                this.registryKey = resourcekey;
            }

            @Override
            public ResourceOrTagLocationArgument<T> instantiate(CommandBuildContext commandbuildcontext) {
                return new ResourceOrTagLocationArgument<>(this.registryKey);
            }

            @Override
            public ArgumentTypeInfo<ResourceOrTagLocationArgument<T>, ?> type() {
                return a.this;
            }
        }
    }
}
