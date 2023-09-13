package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class ResourceOrTagArgument<T> implements ArgumentType<ResourceOrTagArgument.c<T>> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
    private static final Dynamic2CommandExceptionType ERROR_UNKNOWN_TAG = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.resource_tag.not_found", object, object1);
    });
    private static final Dynamic3CommandExceptionType ERROR_INVALID_TAG_TYPE = new Dynamic3CommandExceptionType((object, object1, object2) -> {
        return IChatBaseComponent.translatable("argument.resource_tag.invalid_type", object, object1, object2);
    });
    private final HolderLookup<T> registryLookup;
    final ResourceKey<? extends IRegistry<T>> registryKey;

    public ResourceOrTagArgument(CommandBuildContext commandbuildcontext, ResourceKey<? extends IRegistry<T>> resourcekey) {
        this.registryKey = resourcekey;
        this.registryLookup = commandbuildcontext.holderLookup(resourcekey);
    }

    public static <T> ResourceOrTagArgument<T> resourceOrTag(CommandBuildContext commandbuildcontext, ResourceKey<? extends IRegistry<T>> resourcekey) {
        return new ResourceOrTagArgument<>(commandbuildcontext, resourcekey);
    }

    public static <T> ResourceOrTagArgument.c<T> getResourceOrTag(CommandContext<CommandListenerWrapper> commandcontext, String s, ResourceKey<IRegistry<T>> resourcekey) throws CommandSyntaxException {
        ResourceOrTagArgument.c<?> resourceortagargument_c = (ResourceOrTagArgument.c) commandcontext.getArgument(s, ResourceOrTagArgument.c.class);
        Optional<ResourceOrTagArgument.c<T>> optional = resourceortagargument_c.cast(resourcekey);

        return (ResourceOrTagArgument.c) optional.orElseThrow(() -> {
            return (CommandSyntaxException) resourceortagargument_c.unwrap().map((holder_c) -> {
                ResourceKey<?> resourcekey1 = holder_c.key();

                return ResourceArgument.ERROR_INVALID_RESOURCE_TYPE.create(resourcekey1.location(), resourcekey1.registry(), resourcekey.location());
            }, (holderset_named) -> {
                TagKey<?> tagkey = holderset_named.key();

                return ResourceOrTagArgument.ERROR_INVALID_TAG_TYPE.create(tagkey.location(), tagkey.registry(), resourcekey.location());
            });
        });
    }

    public ResourceOrTagArgument.c<T> parse(StringReader stringreader) throws CommandSyntaxException {
        if (stringreader.canRead() && stringreader.peek() == '#') {
            int i = stringreader.getCursor();

            try {
                stringreader.skip();
                MinecraftKey minecraftkey = MinecraftKey.read(stringreader);
                TagKey<T> tagkey = TagKey.create(this.registryKey, minecraftkey);
                HolderSet.Named<T> holderset_named = (HolderSet.Named) this.registryLookup.get(tagkey).orElseThrow(() -> {
                    return ResourceOrTagArgument.ERROR_UNKNOWN_TAG.create(minecraftkey, this.registryKey.location());
                });

                return new ResourceOrTagArgument.d<>(holderset_named);
            } catch (CommandSyntaxException commandsyntaxexception) {
                stringreader.setCursor(i);
                throw commandsyntaxexception;
            }
        } else {
            MinecraftKey minecraftkey1 = MinecraftKey.read(stringreader);
            ResourceKey<T> resourcekey = ResourceKey.create(this.registryKey, minecraftkey1);
            Holder.c<T> holder_c = (Holder.c) this.registryLookup.get(resourcekey).orElseThrow(() -> {
                return ResourceArgument.ERROR_UNKNOWN_RESOURCE.create(minecraftkey1, this.registryKey.location());
            });

            return new ResourceOrTagArgument.b<>(holder_c);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        ICompletionProvider.suggestResource(this.registryLookup.listTagIds().map(TagKey::location), suggestionsbuilder, "#");
        return ICompletionProvider.suggestResource(this.registryLookup.listElementIds().map(ResourceKey::location), suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return ResourceOrTagArgument.EXAMPLES;
    }

    public interface c<T> extends Predicate<Holder<T>> {

        Either<Holder.c<T>, HolderSet.Named<T>> unwrap();

        <E> Optional<ResourceOrTagArgument.c<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey);

        String asPrintable();
    }

    private static record d<T> (HolderSet.Named<T> tag) implements ResourceOrTagArgument.c<T> {

        @Override
        public Either<Holder.c<T>, HolderSet.Named<T>> unwrap() {
            return Either.right(this.tag);
        }

        @Override
        public <E> Optional<ResourceOrTagArgument.c<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return this.tag.key().isFor(resourcekey) ? Optional.of(this) : Optional.empty();
        }

        public boolean test(Holder<T> holder) {
            return this.tag.contains(holder);
        }

        @Override
        public String asPrintable() {
            return "#" + this.tag.key().location();
        }
    }

    private static record b<T> (Holder.c<T> value) implements ResourceOrTagArgument.c<T> {

        @Override
        public Either<Holder.c<T>, HolderSet.Named<T>> unwrap() {
            return Either.left(this.value);
        }

        @Override
        public <E> Optional<ResourceOrTagArgument.c<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return this.value.key().isFor(resourcekey) ? Optional.of(this) : Optional.empty();
        }

        public boolean test(Holder<T> holder) {
            return holder.equals(this.value);
        }

        @Override
        public String asPrintable() {
            return this.value.key().location().toString();
        }
    }

    public static class a<T> implements ArgumentTypeInfo<ResourceOrTagArgument<T>, ResourceOrTagArgument.a<T>.a> {

        public a() {}

        public void serializeToNetwork(ResourceOrTagArgument.a<T>.a resourceortagargument_a_a, PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeResourceLocation(resourceortagargument_a_a.registryKey.location());
        }

        @Override
        public ResourceOrTagArgument.a<T>.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
            MinecraftKey minecraftkey = packetdataserializer.readResourceLocation();

            return new ResourceOrTagArgument.a.a(ResourceKey.createRegistryKey(minecraftkey));
        }

        public void serializeToJson(ResourceOrTagArgument.a<T>.a resourceortagargument_a_a, JsonObject jsonobject) {
            jsonobject.addProperty("registry", resourceortagargument_a_a.registryKey.location().toString());
        }

        public ResourceOrTagArgument.a<T>.a unpack(ResourceOrTagArgument<T> resourceortagargument) {
            return new ResourceOrTagArgument.a.a(resourceortagargument.registryKey);
        }

        public final class a implements ArgumentTypeInfo.a<ResourceOrTagArgument<T>> {

            final ResourceKey<? extends IRegistry<T>> registryKey;

            a(ResourceKey resourcekey) {
                this.registryKey = resourcekey;
            }

            @Override
            public ResourceOrTagArgument<T> instantiate(CommandBuildContext commandbuildcontext) {
                return new ResourceOrTagArgument<>(commandbuildcontext, this.registryKey);
            }

            @Override
            public ArgumentTypeInfo<ResourceOrTagArgument<T>, ?> type() {
                return a.this;
            }
        }
    }
}
