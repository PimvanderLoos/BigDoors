package net.minecraft.server.commands.data;

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
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;

public class CommandDataAccessorTile implements CommandDataAccessor {

    static final SimpleCommandExceptionType ERROR_NOT_A_BLOCK_ENTITY = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.data.block.invalid"));
    public static final Function<String, CommandData.c> PROVIDER = (s) -> {
        return new CommandData.c() {
            @Override
            public CommandDataAccessor access(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                BlockPosition blockposition = ArgumentPosition.getLoadedBlockPos(commandcontext, s + "Pos");
                TileEntity tileentity = ((CommandListenerWrapper) commandcontext.getSource()).getLevel().getBlockEntity(blockposition);

                if (tileentity == null) {
                    throw CommandDataAccessorTile.ERROR_NOT_A_BLOCK_ENTITY.create();
                } else {
                    return new CommandDataAccessorTile(tileentity, blockposition);
                }
            }

            @Override
            public ArgumentBuilder<CommandListenerWrapper, ?> wrap(ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder, Function<ArgumentBuilder<CommandListenerWrapper, ?>, ArgumentBuilder<CommandListenerWrapper, ?>> function) {
                return argumentbuilder.then(CommandDispatcher.literal("block").then((ArgumentBuilder) function.apply(CommandDispatcher.argument(s + "Pos", ArgumentPosition.blockPos()))));
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
    public void setData(NBTTagCompound nbttagcompound) {
        IBlockData iblockdata = this.entity.getLevel().getBlockState(this.pos);

        this.entity.load(nbttagcompound);
        this.entity.setChanged();
        this.entity.getLevel().sendBlockUpdated(this.pos, iblockdata, iblockdata, 3);
    }

    @Override
    public NBTTagCompound getData() {
        return this.entity.saveWithFullMetadata();
    }

    @Override
    public IChatBaseComponent getModifiedSuccess() {
        return IChatBaseComponent.translatable("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
    }

    @Override
    public IChatBaseComponent getPrintSuccess(NBTBase nbtbase) {
        return IChatBaseComponent.translatable("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), GameProfileSerializer.toPrettyComponent(nbtbase));
    }

    @Override
    public IChatBaseComponent getPrintSuccess(ArgumentNBTKey.g argumentnbtkey_g, double d0, int i) {
        return IChatBaseComponent.translatable("commands.data.block.get", argumentnbtkey_g, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", d0), i);
    }
}
