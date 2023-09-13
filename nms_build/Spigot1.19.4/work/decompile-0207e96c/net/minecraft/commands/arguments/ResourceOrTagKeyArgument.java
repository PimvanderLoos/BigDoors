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

public class ResourceOrTagKeyArgument<T> implements ArgumentType<ResourceOrTagKeyArgument.c<T>> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
    final ResourceKey<? extends IRegistry<T>> registryKey;

    public ResourceOrTagKeyArgument(ResourceKey<? extends IRegistry<T>> resourcekey) {
        this.registryKey = resourcekey;
    }

    public static <T> ResourceOrTagKeyArgument<T> resourceOrTagKey(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return new ResourceOrTagKeyArgument<>(resourcekey);
    }

    public static <T> ResourceOrTagKeyArgument.c<T> getResourceOrTagKey(CommandContext<CommandListenerWrapper> commandcontext, String s, ResourceKey<IRegistry<T>> resourcekey, DynamicCommandExceptionType dynamiccommandexceptiontype) throws CommandSyntaxException {
        ResourceOrTagKeyArgument.c<?> resourceortagkeyargument_c = (ResourceOrTagKeyArgument.c) commandcontext.getArgument(s, ResourceOrTagKeyArgument.c.class);
        Optional<ResourceOrTagKeyArgument.c<T>> optional = resourceortagkeyargument_c.cast(resourcekey);

        return (ResourceOrTagKeyArgument.c) optional.orElseThrow(() -> {
            return dynamiccommandexceptiontype.create(resourceortagkeyargument_c);
        });
    }

    public ResourceOrTagKeyArgument.c<T> parse(StringReader stringreader) throws CommandSyntaxException {
        if (stringreader.canRead() && stringreader.peek() == '#') {
            int i = stringreader.getCursor();

            try {
                stringreader.skip();
                MinecraftKey minecraftkey = MinecraftKey.read(stringreader);

                return new ResourceOrTagKeyArgument.d<>(TagKey.create(this.registryKey, minecraftkey));
            } catch (CommandSyntaxException commandsyntaxexception) {
                stringreader.setCursor(i);
                throw commandsyntaxexception;
            }
        } else {
            MinecraftKey minecraftkey1 = MinecraftKey.read(stringreader);

            return new ResourceOrTagKeyArgument.b<>(ResourceKey.create(this.registryKey, minecraftkey1));
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
        return ResourceOrTagKeyArgument.EXAMPLES;
    }

    public interface c<T> extends Predicate<Holder<T>> {

        Either<ResourceKey<T>, TagKey<T>> unwrap();

        <E> Optional<ResourceOrTagKeyArgument.c<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey);

        String asPrintable();
    }

    private static record d<T> (TagKey<T> key) implements ResourceOrTagKeyArgument.c<T> {

        @Override
        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.right(this.key);
        }

        @Override
        public <E> Optional<ResourceOrTagKeyArgument.c<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return this.key.cast(resourcekey).map(ResourceOrTagKeyArgument.d::new);
        }

        public boolean test(Holder<T> holder) {
            return holder.is(this.key);
        }

        @Override
        public String asPrintable() {
            return "#" + this.key.location();
        }
    }

    private static record b<T> (ResourceKey<T> key) implements ResourceOrTagKeyArgument.c<T> {

        @Override
        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.left(this.key);
        }

        @Override
        public <E> Optional<ResourceOrTagKeyArgument.c<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return this.key.cast(resourcekey).map(ResourceOrTagKeyArgument.b::new);
        }

        public boolean test(Holder<T> holder) {
            return holder.is(this.key);
        }

        @Override
        public String asPrintable() {
            return this.key.location().toString();
        }
    }

    public static class a<T> implements ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, ResourceOrTagKeyArgument.a<T>.a> {

        public a() {}

        public void serializeToNetwork(ResourceOrTagKeyArgument.a<T>.a resourceortagkeyargument_a_a, PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeResourceLocation(resourceortagkeyargument_a_a.registryKey.location());
        }

        @Override
        public ResourceOrTagKeyArgument.a<T>.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
            MinecraftKey minecraftkey = packetdataserializer.readResourceLocation();

            return new ResourceOrTagKeyArgument.a.a(ResourceKey.createRegistryKey(minecraftkey));
        }

        public void serializeToJson(ResourceOrTagKeyArgument.a<T>.a resourceortagkeyargument_a_a, JsonObject jsonobject) {
            jsonobject.addProperty("registry", resourceortagkeyargument_a_a.registryKey.location().toString());
        }

        public ResourceOrTagKeyArgument.a<T>.a unpack(ResourceOrTagKeyArgument<T> resourceortagkeyargument) {
            return new ResourceOrTagKeyArgument.a.a(resourceortagkeyargument.registryKey);
        }

        public final class a implements ArgumentTypeInfo.a<ResourceOrTagKeyArgument<T>> {

            final ResourceKey<? extends IRegistry<T>> registryKey;

            a(ResourceKey resourcekey) {
                this.registryKey = resourcekey;
            }

            @Override
            public ResourceOrTagKeyArgument<T> instantiate(CommandBuildContext commandbuildcontext) {
                return new ResourceOrTagKeyArgument<>(this.registryKey);
            }

            @Override
            public ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, ?> type() {
                return a.this;
            }
        }
    }
}
