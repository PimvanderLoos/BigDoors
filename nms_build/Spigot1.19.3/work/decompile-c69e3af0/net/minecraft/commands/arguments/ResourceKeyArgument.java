package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;

public class ResourceKeyArgument<T> implements ArgumentType<ResourceKey<T>> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType ERROR_INVALID_FEATURE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.place.feature.invalid", object);
    });
    private static final DynamicCommandExceptionType ERROR_INVALID_STRUCTURE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.place.structure.invalid", object);
    });
    private static final DynamicCommandExceptionType ERROR_INVALID_TEMPLATE_POOL = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.place.jigsaw.invalid", object);
    });
    final ResourceKey<? extends IRegistry<T>> registryKey;

    public ResourceKeyArgument(ResourceKey<? extends IRegistry<T>> resourcekey) {
        this.registryKey = resourcekey;
    }

    public static <T> ResourceKeyArgument<T> key(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return new ResourceKeyArgument<>(resourcekey);
    }

    private static <T> ResourceKey<T> getRegistryKey(CommandContext<CommandListenerWrapper> commandcontext, String s, ResourceKey<IRegistry<T>> resourcekey, DynamicCommandExceptionType dynamiccommandexceptiontype) throws CommandSyntaxException {
        ResourceKey<?> resourcekey1 = (ResourceKey) commandcontext.getArgument(s, ResourceKey.class);
        Optional<ResourceKey<T>> optional = resourcekey1.cast(resourcekey);

        return (ResourceKey) optional.orElseThrow(() -> {
            return dynamiccommandexceptiontype.create(resourcekey1);
        });
    }

    private static <T> IRegistry<T> getRegistry(CommandContext<CommandListenerWrapper> commandcontext, ResourceKey<? extends IRegistry<T>> resourcekey) {
        return ((CommandListenerWrapper) commandcontext.getSource()).getServer().registryAccess().registryOrThrow(resourcekey);
    }

    private static <T> Holder.c<T> resolveKey(CommandContext<CommandListenerWrapper> commandcontext, String s, ResourceKey<IRegistry<T>> resourcekey, DynamicCommandExceptionType dynamiccommandexceptiontype) throws CommandSyntaxException {
        ResourceKey<T> resourcekey1 = getRegistryKey(commandcontext, s, resourcekey, dynamiccommandexceptiontype);

        return (Holder.c) getRegistry(commandcontext, resourcekey).getHolder(resourcekey1).orElseThrow(() -> {
            return dynamiccommandexceptiontype.create(resourcekey1.location());
        });
    }

    public static Holder.c<WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return resolveKey(commandcontext, s, Registries.CONFIGURED_FEATURE, ResourceKeyArgument.ERROR_INVALID_FEATURE);
    }

    public static Holder.c<Structure> getStructure(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return resolveKey(commandcontext, s, Registries.STRUCTURE, ResourceKeyArgument.ERROR_INVALID_STRUCTURE);
    }

    public static Holder.c<WorldGenFeatureDefinedStructurePoolTemplate> getStructureTemplatePool(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return resolveKey(commandcontext, s, Registries.TEMPLATE_POOL, ResourceKeyArgument.ERROR_INVALID_TEMPLATE_POOL);
    }

    public ResourceKey<T> parse(StringReader stringreader) throws CommandSyntaxException {
        MinecraftKey minecraftkey = MinecraftKey.read(stringreader);

        return ResourceKey.create(this.registryKey, minecraftkey);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        Object object = commandcontext.getSource();

        if (object instanceof ICompletionProvider) {
            ICompletionProvider icompletionprovider = (ICompletionProvider) object;

            return icompletionprovider.suggestRegistryElements(this.registryKey, ICompletionProvider.a.ELEMENTS, suggestionsbuilder, commandcontext);
        } else {
            return suggestionsbuilder.buildFuture();
        }
    }

    public Collection<String> getExamples() {
        return ResourceKeyArgument.EXAMPLES;
    }

    public static class a<T> implements ArgumentTypeInfo<ResourceKeyArgument<T>, ResourceKeyArgument.a<T>.a> {

        public a() {}

        public void serializeToNetwork(ResourceKeyArgument.a<T>.a resourcekeyargument_a_a, PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeResourceLocation(resourcekeyargument_a_a.registryKey.location());
        }

        @Override
        public ResourceKeyArgument.a<T>.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
            MinecraftKey minecraftkey = packetdataserializer.readResourceLocation();

            return new ResourceKeyArgument.a.a(ResourceKey.createRegistryKey(minecraftkey));
        }

        public void serializeToJson(ResourceKeyArgument.a<T>.a resourcekeyargument_a_a, JsonObject jsonobject) {
            jsonobject.addProperty("registry", resourcekeyargument_a_a.registryKey.location().toString());
        }

        public ResourceKeyArgument.a<T>.a unpack(ResourceKeyArgument<T> resourcekeyargument) {
            return new ResourceKeyArgument.a.a(resourcekeyargument.registryKey);
        }

        public final class a implements ArgumentTypeInfo.a<ResourceKeyArgument<T>> {

            final ResourceKey<? extends IRegistry<T>> registryKey;

            a(ResourceKey resourcekey) {
                this.registryKey = resourcekey;
            }

            @Override
            public ResourceKeyArgument<T> instantiate(CommandBuildContext commandbuildcontext) {
                return new ResourceKeyArgument<>(this.registryKey);
            }

            @Override
            public ArgumentTypeInfo<ResourceKeyArgument<T>, ?> type() {
                return a.this;
            }
        }
    }
}
