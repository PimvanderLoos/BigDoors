package net.minecraft.network.chat.contents;

import java.util.stream.Stream;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;

public record StorageDataSource(MinecraftKey id) implements DataSource {

    @Override
    public Stream<NBTTagCompound> getData(CommandListenerWrapper commandlistenerwrapper) {
        NBTTagCompound nbttagcompound = commandlistenerwrapper.getServer().getCommandStorage().get(this.id);

        return Stream.of(nbttagcompound);
    }

    public String toString() {
        return "storage=" + this.id;
    }
}
