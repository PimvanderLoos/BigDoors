package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.commands.arguments.coordinates.IVectorPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.entity.TileEntity;

public record BlockDataSource(String posPattern, @Nullable IVectorPosition compiledPos) implements DataSource {

    public BlockDataSource(String s) {
        this(s, compilePos(s));
    }

    @Nullable
    private static IVectorPosition compilePos(String s) {
        try {
            return ArgumentPosition.blockPos().parse(new StringReader(s));
        } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
        }
    }

    @Override
    public Stream<NBTTagCompound> getData(CommandListenerWrapper commandlistenerwrapper) {
        if (this.compiledPos != null) {
            WorldServer worldserver = commandlistenerwrapper.getLevel();
            BlockPosition blockposition = this.compiledPos.getBlockPos(commandlistenerwrapper);

            if (worldserver.isLoaded(blockposition)) {
                TileEntity tileentity = worldserver.getBlockEntity(blockposition);

                if (tileentity != null) {
                    return Stream.of(tileentity.saveWithFullMetadata());
                }
            }
        }

        return Stream.empty();
    }

    public String toString() {
        return "block=" + this.posPattern;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            boolean flag;

            if (object instanceof BlockDataSource) {
                BlockDataSource blockdatasource = (BlockDataSource) object;

                if (this.posPattern.equals(blockdatasource.posPattern)) {
                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }
    }

    public int hashCode() {
        return this.posPattern.hashCode();
    }
}
