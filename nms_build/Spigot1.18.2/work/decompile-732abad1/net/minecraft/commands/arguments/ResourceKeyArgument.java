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
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class ResourceKeyArgument<T> implements ArgumentType<ResourceKey<T>> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ATTRIBUTE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("attribute.unknown", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_INVALID_FEATURE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.placefeature.invalid", new Object[]{object});
    });
    final ResourceKey<? extends IRegistry<T>> registryKey;

    public ResourceKeyArgument(ResourceKey<? extends IRegistry<T>> resourcekey) {
        this.registryKey = resourcekey;
    }

    public static <T> ResourceKeyArgument<T> key(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return new ResourceKeyArgument<>(resourcekey);
    }

    private static <T> ResourceKey<T> getRegistryType(CommandContext<CommandListenerWrapper> commandcontext, String s, ResourceKey<IRegistry<T>> resourcekey, DynamicCommandExceptionType dynamiccommandexceptiontype) throws CommandSyntaxException {
        ResourceKey<?> resourcekey1 = (ResourceKey) commandcontext.getArgument(s, ResourceKey.class);
        Optional<ResourceKey<T>> optional = resourcekey1.cast(resourcekey);

        return (ResourceKey) optional.orElseThrow(() -> {
            return dynamiccommandexceptiontype.create(resourcekey1);
        });
    }

    private static <T> IRegistry<T> getRegistry(CommandContext<CommandListenerWrapper> commandcontext, ResourceKey<? extends IRegistry<T>> resourcekey) {
        return ((CommandListenerWrapper) commandcontext.getSource()).getServer().registryAccess().registryOrThrow(resourcekey);
    }

    public static AttributeBase getAttribute(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        ResourceKey<AttributeBase> resourcekey = getRegistryType(commandcontext, s, IRegistry.ATTRIBUTE_REGISTRY, ResourceKeyArgument.ERROR_UNKNOWN_ATTRIBUTE);

        return (AttributeBase) getRegistry(commandcontext, IRegistry.ATTRIBUTE_REGISTRY).getOptional(resourcekey).orElseThrow(() -> {
            return ResourceKeyArgument.ERROR_UNKNOWN_ATTRIBUTE.create(resourcekey.location());
        });
    }

    public static Holder<WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        ResourceKey<WorldGenFeatureConfigured<?, ?>> resourcekey = getRegistryType(commandcontext, s, IRegistry.CONFIGURED_FEATURE_REGISTRY, ResourceKeyArgument.ERROR_INVALID_FEATURE);

        return (Holder) getRegistry(commandcontext, IRegistry.CONFIGURED_FEATURE_REGISTRY).getHolder(resourcekey).orElseThrow(() -> {
            return ResourceKeyArgument.ERROR_INVALID_FEATURE.create(resourcekey.location());
        });
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

    public static class a implements ArgumentSerializer<ResourceKeyArgument<?>> {

        public a() {}

        public void serializeToNetwork(ResourceKeyArgument<?> resourcekeyargument, PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeResourceLocation(resourcekeyargument.registryKey.location());
        }

        @Override
        public ResourceKeyArgument<?> deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
            MinecraftKey minecraftkey = packetdataserializer.readResourceLocation();

            return new ResourceKeyArgument<>(ResourceKey.createRegistryKey(minecraftkey));
        }

        public void serializeToJson(ResourceKeyArgument<?> resourcekeyargument, JsonObject jsonobject) {
            jsonobject.addProperty("registry", resourcekeyargument.registryKey.location().toString());
        }
    }
}
