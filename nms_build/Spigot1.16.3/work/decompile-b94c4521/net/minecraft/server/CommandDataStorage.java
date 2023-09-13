package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;

public class CommandDataStorage implements CommandDataAccessor {

    private static final SuggestionProvider<CommandListenerWrapper> b = (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.a(b(commandcontext).a(), suggestionsbuilder);
    };
    public static final Function<String, CommandData.c> a = (s) -> {
        return new CommandData.c() {
            @Override
            public CommandDataAccessor a(CommandContext<CommandListenerWrapper> commandcontext) {
                return new CommandDataStorage(CommandDataStorage.b(commandcontext), ArgumentMinecraftKeyRegistered.e(commandcontext, s));
            }

            @Override
            public ArgumentBuilder<CommandListenerWrapper, ?> a(ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, Function<ArgumentBuilder<CommandListenerWrapper, ?>, ArgumentBuilder<CommandListenerWrapper, ?>> function) {
                return argumentbuilder.then(CommandDispatcher.a("storage").then((ArgumentBuilder) function.apply(CommandDispatcher.a(s, (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandDataStorage.b))));
            }
        };
    };
    private final PersistentCommandStorage c;
    private final MinecraftKey d;

    private static PersistentCommandStorage b(CommandContext<CommandListenerWrapper> commandcontext) {
        return ((CommandListenerWrapper) commandcontext.getSource()).getServer().aH();
    }

    private CommandDataStorage(PersistentCommandStorage persistentcommandstorage, MinecraftKey minecraftkey) {
        this.c = persistentcommandstorage;
        this.d = minecraftkey;
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.c.a(this.d, nbttagcompound);
    }

    @Override
    public NBTTagCompound a() {
        return this.c.a(this.d);
    }

    @Override
    public IChatBaseComponent b() {
        return new ChatMessage("commands.data.storage.modified", new Object[]{this.d});
    }

    @Override
    public IChatBaseComponent a(NBTBase nbtbase) {
        return new ChatMessage("commands.data.storage.query", new Object[]{this.d, nbtbase.l()});
    }

    @Override
    public IChatBaseComponent a(ArgumentNBTKey.h argumentnbtkey_h, double d0, int i) {
        return new ChatMessage("commands.data.storage.get", new Object[]{argumentnbtkey_h, this.d, String.format(Locale.ROOT, "%.2f", d0), i});
    }
}
