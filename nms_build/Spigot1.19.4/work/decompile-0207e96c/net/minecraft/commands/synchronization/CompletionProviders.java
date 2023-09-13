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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.EntityTypes;

public class CompletionProviders {

    private static final Map<MinecraftKey, SuggestionProvider<ICompletionProvider>> PROVIDERS_BY_NAME = Maps.newHashMap();
    private static final MinecraftKey DEFAULT_NAME = new MinecraftKey("ask_server");
    public static final SuggestionProvider<ICompletionProvider> ASK_SERVER = register(CompletionProviders.DEFAULT_NAME, (commandcontext, suggestionsbuilder) -> {
        return ((ICompletionProvider) commandcontext.getSource()).customSuggestion(commandcontext);
    });
    public static final SuggestionProvider<CommandListenerWrapper> ALL_RECIPES = register(new MinecraftKey("all_recipes"), (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.suggestResource(((ICompletionProvider) commandcontext.getSource()).getRecipeNames(), suggestionsbuilder);
    });
    public static final SuggestionProvider<CommandListenerWrapper> AVAILABLE_SOUNDS = register(new MinecraftKey("available_sounds"), (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.suggestResource(((ICompletionProvider) commandcontext.getSource()).getAvailableSounds(), suggestionsbuilder);
    });
    public static final SuggestionProvider<CommandListenerWrapper> SUMMONABLE_ENTITIES = register(new MinecraftKey("summonable_entities"), (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.stream().filter((entitytypes) -> {
            return entitytypes.isEnabled(((ICompletionProvider) commandcontext.getSource()).enabledFeatures()) && entitytypes.canSummon();
        }), suggestionsbuilder, EntityTypes::getKey, (entitytypes) -> {
            return IChatBaseComponent.translatable(SystemUtils.makeDescriptionId("entity", EntityTypes.getKey(entitytypes)));
        });
    });

    public CompletionProviders() {}

    public static <S extends ICompletionProvider> SuggestionProvider<S> register(MinecraftKey minecraftkey, SuggestionProvider<ICompletionProvider> suggestionprovider) {
        if (CompletionProviders.PROVIDERS_BY_NAME.containsKey(minecraftkey)) {
            throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + minecraftkey);
        } else {
            CompletionProviders.PROVIDERS_BY_NAME.put(minecraftkey, suggestionprovider);
            return new CompletionProviders.a(minecraftkey, suggestionprovider);
        }
    }

    public static SuggestionProvider<ICompletionProvider> getProvider(MinecraftKey minecraftkey) {
        return (SuggestionProvider) CompletionProviders.PROVIDERS_BY_NAME.getOrDefault(minecraftkey, CompletionProviders.ASK_SERVER);
    }

    public static MinecraftKey getName(SuggestionProvider<ICompletionProvider> suggestionprovider) {
        return suggestionprovider instanceof CompletionProviders.a ? ((CompletionProviders.a) suggestionprovider).name : CompletionProviders.DEFAULT_NAME;
    }

    public static SuggestionProvider<ICompletionProvider> safelySwap(SuggestionProvider<ICompletionProvider> suggestionprovider) {
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
