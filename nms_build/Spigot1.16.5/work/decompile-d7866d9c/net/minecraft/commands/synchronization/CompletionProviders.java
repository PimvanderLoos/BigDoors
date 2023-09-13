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

    private static final Map<MinecraftKey, SuggestionProvider<ICompletionProvider>> f = Maps.newHashMap();
    private static final MinecraftKey g = new MinecraftKey("ask_server");
    public static final SuggestionProvider<ICompletionProvider> a = a(CompletionProviders.g, (commandcontext, suggestionsbuilder) -> {
        return ((ICompletionProvider) commandcontext.getSource()).a(commandcontext, suggestionsbuilder);
    });
    public static final SuggestionProvider<CommandListenerWrapper> b = a(new MinecraftKey("all_recipes"), (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a(((ICompletionProvider) commandcontext.getSource()).o(), suggestionsbuilder);
    });
    public static final SuggestionProvider<CommandListenerWrapper> c = a(new MinecraftKey("available_sounds"), (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a((Iterable) ((ICompletionProvider) commandcontext.getSource()).n(), suggestionsbuilder);
    });
    public static final SuggestionProvider<CommandListenerWrapper> d = a(new MinecraftKey("available_biomes"), (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a((Iterable) ((ICompletionProvider) commandcontext.getSource()).q().b(IRegistry.ay).keySet(), suggestionsbuilder);
    });
    public static final SuggestionProvider<CommandListenerWrapper> e = a(new MinecraftKey("summonable_entities"), (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a(IRegistry.ENTITY_TYPE.g().filter(EntityTypes::b), suggestionsbuilder, EntityTypes::getName, (entitytypes) -> {
            return new ChatMessage(SystemUtils.a("entity", EntityTypes.getName(entitytypes)));
        });
    });

    public static <S extends ICompletionProvider> SuggestionProvider<S> a(MinecraftKey minecraftkey, SuggestionProvider<ICompletionProvider> suggestionprovider) {
        if (CompletionProviders.f.containsKey(minecraftkey)) {
            throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + minecraftkey);
        } else {
            CompletionProviders.f.put(minecraftkey, suggestionprovider);
            return new CompletionProviders.a(minecraftkey, suggestionprovider);
        }
    }

    public static SuggestionProvider<ICompletionProvider> a(MinecraftKey minecraftkey) {
        return (SuggestionProvider) CompletionProviders.f.getOrDefault(minecraftkey, CompletionProviders.a);
    }

    public static MinecraftKey a(SuggestionProvider<ICompletionProvider> suggestionprovider) {
        return suggestionprovider instanceof CompletionProviders.a ? ((CompletionProviders.a) suggestionprovider).b : CompletionProviders.g;
    }

    public static SuggestionProvider<ICompletionProvider> b(SuggestionProvider<ICompletionProvider> suggestionprovider) {
        return suggestionprovider instanceof CompletionProviders.a ? suggestionprovider : CompletionProviders.a;
    }

    public static class a implements SuggestionProvider<ICompletionProvider> {

        private final SuggestionProvider<ICompletionProvider> a;
        private final MinecraftKey b;

        public a(MinecraftKey minecraftkey, SuggestionProvider<ICompletionProvider> suggestionprovider) {
            this.a = suggestionprovider;
            this.b = minecraftkey;
        }

        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ICompletionProvider> commandcontext, SuggestionsBuilder suggestionsbuilder) throws CommandSyntaxException {
            return this.a.getSuggestions(commandcontext, suggestionsbuilder);
        }
    }
}
