package net.minecraft.server.commands.data;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentNBTKey;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;

public class CommandDataAccessorTile implements CommandDataAccessor {

    static final SimpleCommandExceptionType ERROR_NOT_A_BLOCK_ENTITY = new SimpleCommandExceptionType(new ChatMessage("commands.data.block.invalid"));
    public static final Function<String, CommandData.c> PROVIDER = (s) -> {
        return new CommandData.c() {
            @Override
            public CommandDataAccessor a(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                BlockPosition blockposition = ArgumentPosition.a(commandcontext, s + "Pos");
                TileEntity tileentity = ((CommandListenerWrapper) commandcontext.getSource()).getWorld().getTileEntity(blockposition);

                if (tileentity == null) {
                    throw CommandDataAccessorTile.ERROR_NOT_A_BLOCK_ENTITY.create();
                } else {
                    return new CommandDataAccessorTile(tileentity, blockposition);
                }
            }

            @Override
            public ArgumentBuilder<CommandListenerWrapper, ?> a(ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, Function<ArgumentBuilder<CommandListenerWrapper, ?>, ArgumentBuilder<CommandListenerWrapper, ?>> function) {
                return argumentbuilder.then(CommandDispatcher.a("block").then((ArgumentBuilder) function.apply(CommandDispatcher.a(s + "Pos", (ArgumentType) ArgumentPosition.a()))));
            }
        };
    };
    private final TileEntity entity;
    private final BlockPosition pos;

    public CommandDataAccessorTile(TileEntity tileentity, BlockPosition blockposition) {
        this.entity = tileentity;
        this.pos = blockposition;
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("x", this.pos.getX());
        nbttagcompound.setInt("y", this.pos.getY());
        nbttagcompound.setInt("z", this.pos.getZ());
        IBlockData iblockdata = this.entity.getWorld().getType(this.pos);

        this.entity.load(nbttagcompound);
        this.entity.update();
        this.entity.getWorld().notify(this.pos, iblockdata, iblockdata, 3);
    }

    @Override
    public NBTTagCompound a() {
        return this.entity.save(new NBTTagCompound());
    }

    @Override
    public IChatBaseComponent b() {
        return new ChatMessage("commands.data.block.modified", new Object[]{this.pos.getX(), this.pos.getY(), this.pos.getZ()});
    }

    @Override
    public IChatBaseComponent a(NBTBase nbtbase) {
        return new ChatMessage("commands.data.block.query", new Object[]{this.pos.getX(), this.pos.getY(), this.pos.getZ(), GameProfileSerializer.c(nbtbase)});
    }

    @Override
    public IChatBaseComponent a(ArgumentNBTKey.g argumentnbtkey_g, double d0, int i) {
        return new ChatMessage("commands.data.block.get", new Object[]{argumentnbtkey_g, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", d0), i});
    }
}
