package net.minecraft.server.commands.data;

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
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.PersistentCommandStorage;

public class CommandDataStorage implements CommandDataAccessor {

    static final SuggestionProvider<CommandListenerWrapper> SUGGEST_STORAGE = (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.suggestResource(getGlobalTags(commandcontext).keys(), suggestionsbuilder);
    };
    public static final Function<String, CommandData.c> PROVIDER = (s) -> {
        return new CommandData.c() {
            @Override
            public CommandDataAccessor access(CommandContext<CommandListenerWrapper> commandcontext) {
                return new CommandDataStorage(CommandDataStorage.getGlobalTags(commandcontext), ArgumentMinecraftKeyRegistered.getId(commandcontext, s));
            }

            @Override
            public ArgumentBuilder<CommandListenerWrapper, ?> wrap(ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, Function<ArgumentBuilder<CommandListenerWrapper, ?>, ArgumentBuilder<CommandListenerWrapper, ?>> function) {
                return argumentbuilder.then(CommandDispatcher.literal("storage").then((ArgumentBuilder) function.apply(CommandDispatcher.argument(s, ArgumentMinecraftKeyRegistered.id()).suggests(CommandDataStorage.SUGGEST_STORAGE))));
            }
        };
    };
    private final PersistentCommandStorage storage;
    private final MinecraftKey id;

    static PersistentCommandStorage getGlobalTags(CommandContext<CommandListenerWrapper> commandcontext) {
        return ((CommandListenerWrapper) commandcontext.getSource()).getServer().getCommandStorage();
    }

    CommandDataStorage(PersistentCommandStorage persistentcommandstorage, MinecraftKey minecraftkey) {
        this.storage = persistentcommandstorage;
        this.id = minecraftkey;
    }

    @Override
    public void setData(NBTTagCompound nbttagcompound) {
        this.storage.set(this.id, nbttagcompound);
    }

    @Override
    public NBTTagCompound getData() {
        return this.storage.get(this.id);
    }

    @Override
    public IChatBaseComponent getModifiedSuccess() {
        return IChatBaseComponent.translatable("commands.data.storage.modified", this.id);
    }

    @Override
    public IChatBaseComponent getPrintSuccess(NBTBase nbtbase) {
        return IChatBaseComponent.translatable("commands.data.storage.query", this.id, GameProfileSerializer.toPrettyComponent(nbtbase));
    }

    @Override
    public IChatBaseComponent getPrintSuccess(ArgumentNBTKey.g argumentnbtkey_g, double d0, int i) {
        return IChatBaseComponent.translatable("commands.data.storage.get", argumentnbtkey_g, this.id, String.format(Locale.ROOT, "%.2f", d0), i);
    }
}
