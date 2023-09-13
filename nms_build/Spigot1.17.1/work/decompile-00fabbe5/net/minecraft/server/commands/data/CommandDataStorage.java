package net.minecraft.server.commands.data;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.ArgumentNBTKey;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.PersistentCommandStorage;

public class CommandDataStorage implements CommandDataAccessor {

    static final SuggestionProvider<CommandListenerWrapper> SUGGEST_STORAGE = (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a(a(commandcontext).a(), suggestionsbuilder);
    };
    public static final Function<String, CommandData.c> PROVIDER = (s) -> {
        return new CommandData.c() {
            @Override
            public CommandDataAccessor a(CommandContext<CommandListenerWrapper> commandcontext) {
                return new CommandDataStorage(CommandDataStorage.a(commandcontext), ArgumentMinecraftKeyRegistered.f(commandcontext, s));
            }

            @Override
            public ArgumentBuilder<CommandListenerWrapper, ?> a(ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, Function<ArgumentBuilder<CommandListenerWrapper, ?>, ArgumentBuilder<CommandListenerWrapper, ?>> function) {
                return argumentbuilder.then(CommandDispatcher.a("storage").then((ArgumentBuilder) function.apply(CommandDispatcher.a(s, (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandDataStorage.SUGGEST_STORAGE))));
            }
        };
    };
    private final PersistentCommandStorage storage;
    private final MinecraftKey id;

    static PersistentCommandStorage a(CommandContext<CommandListenerWrapper> commandcontext) {
        return ((CommandListenerWrapper) commandcontext.getSource()).getServer().aG();
    }

    CommandDataStorage(PersistentCommandStorage persistentcommandstorage, MinecraftKey minecraftkey) {
        this.storage = persistentcommandstorage;
        this.id = minecraftkey;
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.storage.a(this.id, nbttagcompound);
    }

    @Override
    public NBTTagCompound a() {
        return this.storage.a(this.id);
    }

    @Override
    public IChatBaseComponent b() {
        return new ChatMessage("commands.data.storage.modified", new Object[]{this.id});
    }

    @Override
    public IChatBaseComponent a(NBTBase nbtbase) {
        return new ChatMessage("commands.data.storage.query", new Object[]{this.id, GameProfileSerializer.c(nbtbase)});
    }

    @Override
    public IChatBaseComponent a(ArgumentNBTKey.g argumentnbtkey_g, double d0, int i) {
        return new ChatMessage("commands.data.storage.get", new Object[]{argumentnbtkey_g, this.id, String.format(Locale.ROOT, "%.2f", d0), i});
    }
}
