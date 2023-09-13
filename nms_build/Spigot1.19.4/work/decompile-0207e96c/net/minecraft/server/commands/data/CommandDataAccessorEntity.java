package net.minecraft.server.commands.data;

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
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;

public class CommandDataAccessorEntity implements CommandDataAccessor {

    private static final SimpleCommandExceptionType ERROR_NO_PLAYERS = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.data.entity.invalid"));
    public static final Function<String, CommandData.c> PROVIDER = (s) -> {
        return new CommandData.c() {
            @Override
            public CommandDataAccessor access(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                return new CommandDataAccessorEntity(ArgumentEntity.getEntity(commandcontext, s));
            }

            @Override
            public ArgumentBuilder<CommandListenerWrapper, ?> wrap(ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, Function<ArgumentBuilder<CommandListenerWrapper, ?>, ArgumentBuilder<CommandListenerWrapper, ?>> function) {
                return argumentbuilder.then(CommandDispatcher.literal("entity").then((ArgumentBuilder) function.apply(CommandDispatcher.argument(s, ArgumentEntity.entity()))));
            }
        };
    };
    private final Entity entity;

    public CommandDataAccessorEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void setData(NBTTagCompound nbttagcompound) throws CommandSyntaxException {
        if (this.entity instanceof EntityHuman) {
            throw CommandDataAccessorEntity.ERROR_NO_PLAYERS.create();
        } else {
            UUID uuid = this.entity.getUUID();

            this.entity.load(nbttagcompound);
            this.entity.setUUID(uuid);
        }
    }

    @Override
    public NBTTagCompound getData() {
        return CriterionConditionNBT.getEntityTagToCompare(this.entity);
    }

    @Override
    public IChatBaseComponent getModifiedSuccess() {
        return IChatBaseComponent.translatable("commands.data.entity.modified", this.entity.getDisplayName());
    }

    @Override
    public IChatBaseComponent getPrintSuccess(NBTBase nbtbase) {
        return IChatBaseComponent.translatable("commands.data.entity.query", this.entity.getDisplayName(), GameProfileSerializer.toPrettyComponent(nbtbase));
    }

    @Override
    public IChatBaseComponent getPrintSuccess(ArgumentNBTKey.g argumentnbtkey_g, double d0, int i) {
        return IChatBaseComponent.translatable("commands.data.entity.get", argumentnbtkey_g, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", d0), i);
    }
}
