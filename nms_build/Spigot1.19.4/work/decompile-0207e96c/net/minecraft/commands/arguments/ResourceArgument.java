package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ResourceArgument<T> implements ArgumentType<Holder.c<T>> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType ERROR_NOT_SUMMONABLE_ENTITY = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("entity.not_summonable", object);
    });
    public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_RESOURCE = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.resource.not_found", object, object1);
    });
    public static final Dynamic3CommandExceptionType ERROR_INVALID_RESOURCE_TYPE = new Dynamic3CommandExceptionType((object, object1, object2) -> {
        return IChatBaseComponent.translatable("argument.resource.invalid_type", object, object1, object2);
    });
    final ResourceKey<? extends IRegistry<T>> registryKey;
    private final HolderLookup<T> registryLookup;

    public ResourceArgument(CommandBuildContext commandbuildcontext, ResourceKey<? extends IRegistry<T>> resourcekey) {
        this.registryKey = resourcekey;
        this.registryLookup = commandbuildcontext.holderLookup(resourcekey);
    }

    public static <T> ResourceArgument<T> resource(CommandBuildContext commandbuildcontext, ResourceKey<? extends IRegistry<T>> resourcekey) {
        return new ResourceArgument<>(commandbuildcontext, resourcekey);
    }

    public static <T> Holder.c<T> getResource(CommandContext<CommandListenerWrapper> commandcontext, String s, ResourceKey<IRegistry<T>> resourcekey) throws CommandSyntaxException {
        Holder.c<T> holder_c = (Holder.c) commandcontext.getArgument(s, Holder.c.class);
        ResourceKey<?> resourcekey1 = holder_c.key();

        if (resourcekey1.isFor(resourcekey)) {
            return holder_c;
        } else {
            throw ResourceArgument.ERROR_INVALID_RESOURCE_TYPE.create(resourcekey1.location(), resourcekey1.registry(), resourcekey.location());
        }
    }

    public static Holder.c<AttributeBase> getAttribute(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return getResource(commandcontext, s, Registries.ATTRIBUTE);
    }

    public static Holder.c<WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return getResource(commandcontext, s, Registries.CONFIGURED_FEATURE);
    }

    public static Holder.c<Structure> getStructure(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return getResource(commandcontext, s, Registries.STRUCTURE);
    }

    public static Holder.c<EntityTypes<?>> getEntityType(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return getResource(commandcontext, s, Registries.ENTITY_TYPE);
    }

    public static Holder.c<EntityTypes<?>> getSummonableEntityType(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        Holder.c<EntityTypes<?>> holder_c = getResource(commandcontext, s, Registries.ENTITY_TYPE);

        if (!((EntityTypes) holder_c.value()).canSummon()) {
            throw ResourceArgument.ERROR_NOT_SUMMONABLE_ENTITY.create(holder_c.key().location().toString());
        } else {
            return holder_c;
        }
    }

    public static Holder.c<MobEffectList> getMobEffect(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return getResource(commandcontext, s, Registries.MOB_EFFECT);
    }

    public static Holder.c<Enchantment> getEnchantment(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return getResource(commandcontext, s, Registries.ENCHANTMENT);
    }

    public Holder.c<T> parse(StringReader stringreader) throws CommandSyntaxException {
        MinecraftKey minecraftkey = MinecraftKey.read(stringreader);
        ResourceKey<T> resourcekey = ResourceKey.create(this.registryKey, minecraftkey);

        return (Holder.c) this.registryLookup.get(resourcekey).orElseThrow(() -> {
            return ResourceArgument.ERROR_UNKNOWN_RESOURCE.create(minecraftkey, this.registryKey.location());
        });
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggestResource(this.registryLookup.listElementIds().map(ResourceKey::location), suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return ResourceArgument.EXAMPLES;
    }

    public static class a<T> implements ArgumentTypeInfo<ResourceArgument<T>, ResourceArgument.a<T>.a> {

        public a() {}

        public void serializeToNetwork(ResourceArgument.a<T>.a resourceargument_a_a, PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeResourceLocation(resourceargument_a_a.registryKey.location());
        }

        @Override
        public ResourceArgument.a<T>.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
            MinecraftKey minecraftkey = packetdataserializer.readResourceLocation();

            return new ResourceArgument.a.a(ResourceKey.createRegistryKey(minecraftkey));
        }

        public void serializeToJson(ResourceArgument.a<T>.a resourceargument_a_a, JsonObject jsonobject) {
            jsonobject.addProperty("registry", resourceargument_a_a.registryKey.location().toString());
        }

        public ResourceArgument.a<T>.a unpack(ResourceArgument<T> resourceargument) {
            return new ResourceArgument.a.a(resourceargument.registryKey);
        }

        public final class a implements ArgumentTypeInfo.a<ResourceArgument<T>> {

            final ResourceKey<? extends IRegistry<T>> registryKey;

            a(ResourceKey resourcekey) {
                this.registryKey = resourcekey;
            }

            @Override
            public ResourceArgument<T> instantiate(CommandBuildContext commandbuildcontext) {
                return new ResourceArgument<>(commandbuildcontext, this.registryKey);
            }

            @Override
            public ArgumentTypeInfo<ResourceArgument<T>, ?> type() {
                return a.this;
            }
        }
    }
}
