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
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class ResourceOrTagLocationArgument<T> implements ArgumentType<ResourceOrTagLocationArgument.b<T>> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
    private static final DynamicCommandExceptionType ERROR_INVALID_BIOME = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.locatebiome.invalid", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_INVALID_STRUCTURE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.locate.invalid", new Object[]{object});
    });
    final ResourceKey<? extends IRegistry<T>> registryKey;

    public ResourceOrTagLocationArgument(ResourceKey<? extends IRegistry<T>> resourcekey) {
        this.registryKey = resourcekey;
    }

    public static <T> ResourceOrTagLocationArgument<T> resourceOrTag(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return new ResourceOrTagLocationArgument<>(resourcekey);
    }

    private static <T> ResourceOrTagLocationArgument.b<T> getRegistryType(CommandContext<CommandListenerWrapper> commandcontext, String s, ResourceKey<IRegistry<T>> resourcekey, DynamicCommandExceptionType dynamiccommandexceptiontype) throws CommandSyntaxException {
        ResourceOrTagLocationArgument.b<?> resourceortaglocationargument_b = (ResourceOrTagLocationArgument.b) commandcontext.getArgument(s, ResourceOrTagLocationArgument.b.class);
        Optional<ResourceOrTagLocationArgument.b<T>> optional = resourceortaglocationargument_b.cast(resourcekey);

        return (ResourceOrTagLocationArgument.b) optional.orElseThrow(() -> {
            return dynamiccommandexceptiontype.create(resourceortaglocationargument_b);
        });
    }

    public static ResourceOrTagLocationArgument.b<BiomeBase> getBiome(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return getRegistryType(commandcontext, s, IRegistry.BIOME_REGISTRY, ResourceOrTagLocationArgument.ERROR_INVALID_BIOME);
    }

    public static ResourceOrTagLocationArgument.b<StructureFeature<?, ?>> getStructureFeature(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return getRegistryType(commandcontext, s, IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, ResourceOrTagLocationArgument.ERROR_INVALID_STRUCTURE);
    }

    public ResourceOrTagLocationArgument.b<T> parse(StringReader stringreader) throws CommandSyntaxException {
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

            return new ResourceOrTagLocationArgument.a<>(ResourceKey.create(this.registryKey, minecraftkey1));
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

    public interface b<T> extends Predicate<Holder<T>> {

        Either<ResourceKey<T>, TagKey<T>> unwrap();

        <E> Optional<ResourceOrTagLocationArgument.b<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey);

        String asPrintable();
    }

    private static record d<T> (TagKey<T> a) implements ResourceOrTagLocationArgument.b<T> {

        private final TagKey<T> key;

        d(TagKey<T> tagkey) {
            this.key = tagkey;
        }

        @Override
        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.right(this.key);
        }

        @Override
        public <E> Optional<ResourceOrTagLocationArgument.b<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return this.key.cast(resourcekey).map(ResourceOrTagLocationArgument.d::new);
        }

        public boolean test(Holder<T> holder) {
            return holder.is(this.key);
        }

        @Override
        public String asPrintable() {
            return "#" + this.key.location();
        }

        public TagKey<T> key() {
            return this.key;
        }
    }

    private static record a<T> (ResourceKey<T> a) implements ResourceOrTagLocationArgument.b<T> {

        private final ResourceKey<T> key;

        a(ResourceKey<T> resourcekey) {
            this.key = resourcekey;
        }

        @Override
        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.left(this.key);
        }

        @Override
        public <E> Optional<ResourceOrTagLocationArgument.b<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return this.key.cast(resourcekey).map(ResourceOrTagLocationArgument.a::new);
        }

        public boolean test(Holder<T> holder) {
            return holder.is(this.key);
        }

        @Override
        public String asPrintable() {
            return this.key.location().toString();
        }

        public ResourceKey<T> key() {
            return this.key;
        }
    }

    public static class c implements ArgumentSerializer<ResourceOrTagLocationArgument<?>> {

        public c() {}

        public void serializeToNetwork(ResourceOrTagLocationArgument<?> resourceortaglocationargument, PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeResourceLocation(resourceortaglocationargument.registryKey.location());
        }

        @Override
        public ResourceOrTagLocationArgument<?> deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
            MinecraftKey minecraftkey = packetdataserializer.readResourceLocation();

            return new ResourceOrTagLocationArgument<>(ResourceKey.createRegistryKey(minecraftkey));
        }

        public void serializeToJson(ResourceOrTagLocationArgument<?> resourceortaglocationargument, JsonObject jsonobject) {
            jsonobject.addProperty("registry", resourceortaglocationargument.registryKey.location().toString());
        }
    }
}
