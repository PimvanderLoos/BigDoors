package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

public class CommandDataAccessorEntity implements CommandDataAccessor {

    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.data.entity.invalid", new Object[0]));
    public static final CommandData.a a = new CommandData.a() {
        public CommandDataAccessor a(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
            return new CommandDataAccessorEntity(ArgumentEntity.a(commandcontext, "target"));
        }

        public ArgumentBuilder<CommandListenerWrapper, ?> a(ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, Function<ArgumentBuilder<CommandListenerWrapper, ?>, ArgumentBuilder<CommandListenerWrapper, ?>> function) {
            return argumentbuilder.then(CommandDispatcher.a("entity").then((ArgumentBuilder) function.apply(CommandDispatcher.a("target", (ArgumentType) ArgumentEntity.a()))));
        }
    };
    private final Entity c;

    public CommandDataAccessorEntity(Entity entity) {
        this.c = entity;
    }

    public void a(NBTTagCompound nbttagcompound) throws CommandSyntaxException {
        if (this.c instanceof EntityHuman) {
            throw CommandDataAccessorEntity.b.create();
        } else {
            UUID uuid = this.c.getUniqueID();

            this.c.f(nbttagcompound);
            this.c.a(uuid);
        }
    }

    public NBTTagCompound a() {
        return CriterionConditionNBT.b(this.c);
    }

    public IChatBaseComponent b() {
        return new ChatMessage("commands.data.entity.modified", new Object[] { this.c.getScoreboardDisplayName()});
    }

    public IChatBaseComponent a(NBTBase nbtbase) {
        return new ChatMessage("commands.data.entity.query", new Object[] { this.c.getScoreboardDisplayName(), nbtbase.k()});
    }

    public IChatBaseComponent a(ArgumentNBTKey.c argumentnbtkey_c, double d0, int i) {
        return new ChatMessage("commands.data.entity.get", new Object[] { argumentnbtkey_c, this.c.getScoreboardDisplayName(), String.format(Locale.ROOT, "%.2f", d0), i});
    }
}
