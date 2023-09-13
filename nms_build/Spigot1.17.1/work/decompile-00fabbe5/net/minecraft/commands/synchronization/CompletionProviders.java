package net.minecraft.commands.synchronization;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.EntityTypes;

public class CompletionProviders {

    private static final Map<MinecraftKey, SuggestionProvider<ICompletionProvider>> PROVIDERS_BY_NAME = Maps.newHashMap();
    private static final MinecraftKey DEFAULT_NAME = new MinecraftKey("ask_server");
    public static final SuggestionProvider<ICompletionProvider> ASK_SERVER = a(CompletionProviders.DEFAULT_NAME, (commandcontext, suggestionsbuilder) -> {
        return ((ICompletionProvider) commandcontext.getSource()).a(commandcontext, suggestionsbuilder);
    });
    public static final SuggestionProvider<CommandListenerWrapper> ALL_RECIPES = a(new MinecraftKey("all_recipes"), (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a(((ICompletionProvider) commandcontext.getSource()).o(), suggestionsbuilder);
    });
    public static final SuggestionProvider<CommandListenerWrapper> AVAILABLE_SOUNDS = a(new MinecraftKey("available_sounds"), (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a((Iterable) ((ICompletionProvider) commandcontext.getSource()).n(), suggestionsbuilder);
    });
    public static final SuggestionProvider<CommandListenerWrapper> AVAILABLE_BIOMES = a(new MinecraftKey("available_biomes"), (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a((Iterable) ((ICompletionProvider) commandcontext.getSource()).q().d(IRegistry.BIOME_REGISTRY).keySet(), suggestionsbuilder);
    });
    public static final SuggestionProvider<CommandListenerWrapper> SUMMONABLE_ENTITIES = a(new MinecraftKey("summonable_entities"), (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a(IRegistry.ENTITY_TYPE.g().filter(EntityTypes::c), suggestionsbuilder, EntityTypes::getName, (entitytypes) -> {
            return new ChatMessage(SystemUtils.a("entity", EntityTypes.getName(entitytypes)));
        });
    });

    public CompletionProviders() {}

    public static <S extends ICompletionProvider> SuggestionProvider<S> a(MinecraftKey minecraftkey, SuggestionProvider<ICompletionProvider> suggestionprovider) {
        if (CompletionProviders.PROVIDERS_BY_NAME.containsKey(minecraftkey)) {
            throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + minecraftkey);
        } else {
            CompletionProviders.PROVIDERS_BY_NAME.put(minecraftkey, suggestionprovider);
            return new CompletionProviders.a(minecraftkey, suggestionprovider);
        }
    }

    public static SuggestionProvider<ICompletionProvider> a(MinecraftKey minecraftkey) {
        return (SuggestionProvider) CompletionProviders.PROVIDERS_BY_NAME.getOrDefault(minecraftkey, CompletionProviders.ASK_SERVER);
    }

    public static MinecraftKey a(SuggestionProvider<ICompletionProvider> suggestionprovider) {
        return suggestionprovider instanceof CompletionProviders.a ? ((CompletionProviders.a) suggestionprovider).name : CompletionProviders.DEFAULT_NAME;
    }

    public static SuggestionProvider<ICompletionProvider> b(SuggestionProvider<ICompletionProvider> suggestionprovider) {
        return suggestionprovider instanceof CompletionProviders.a ? suggestionprovider : CompletionProviders.ASK_SERVER;
    }

    protected static class a implements SuggestionProvider<ICompletionProvider> {

        private final SuggestionProvider<ICompletionProvider> delegate;
        final MinecraftKey name;

        public a(MinecraftKey minecraftkey, SuggestionProvider<ICompletionProvider> suggestionprovider) {
            this.delegate = suggestionprovider;
            this.name = minecraftkey;
        }

        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ICompletionProvider> commandcontext, SuggestionsBuilder suggestionsbuilder) throws CommandSyntaxException {
            return this.delegate.getSuggestions(commandcontext, suggestionsbuilder);
        }
    }
}
