package net.minecraft.server.commands.data;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.advancements.critereon.CriterionConditionNBT;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentNBTKey;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;

public class CommandDataAccessorEntity implements CommandDataAccessor {

    private static final SimpleCommandExceptionType ERROR_NO_PLAYERS = new SimpleCommandExceptionType(new ChatMessage("commands.data.entity.invalid"));
    public static final Function<String, CommandData.c> PROVIDER = (s) -> {
        return new CommandData.c() {
            @Override
            public CommandDataAccessor a(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                return new CommandDataAccessorEntity(ArgumentEntity.a(commandcontext, s));
            }

            @Override
            public ArgumentBuilder<CommandListenerWrapper, ?> a(ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, Function<ArgumentBuilder<CommandListenerWrapper, ?>, ArgumentBuilder<CommandListenerWrapper, ?>> function) {
                return argumentbuilder.then(CommandDispatcher.a("entity").then((ArgumentBuilder) function.apply(CommandDispatcher.a(s, (ArgumentType) ArgumentEntity.a()))));
            }
        };
    };
    private final Entity entity;

    public CommandDataAccessorEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) throws CommandSyntaxException {
        if (this.entity instanceof EntityHuman) {
            throw CommandDataAccessorEntity.ERROR_NO_PLAYERS.create();
        } else {
            UUID uuid = this.entity.getUniqueID();

            this.entity.load(nbttagcompound);
            this.entity.a_(uuid);
        }
    }

    @Override
    public NBTTagCompound a() {
        return CriterionConditionNBT.b(this.entity);
    }

    @Override
    public IChatBaseComponent b() {
        return new ChatMessage("commands.data.entity.modified", new Object[]{this.entity.getScoreboardDisplayName()});
    }

    @Override
    public IChatBaseComponent a(NBTBase nbtbase) {
        return new ChatMessage("commands.data.entity.query", new Object[]{this.entity.getScoreboardDisplayName(), GameProfileSerializer.c(nbtbase)});
    }

    @Override
    public IChatBaseComponent a(ArgumentNBTKey.g argumentnbtkey_g, double d0, int i) {
        return new ChatMessage("commands.data.entity.get", new Object[]{argumentnbtkey_g, this.entity.getScoreboardDisplayName(), String.format(Locale.ROOT, "%.2f", d0), i});
    }
}
